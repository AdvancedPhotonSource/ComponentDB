#!/usr/bin/perl -w

#
#  Copyright (c) 2004-2005 The University of Chicago, as Operator
#  of Argonne National Laboratory.
#

# adl_crawler.pl
#   Reads medm adl files,and writes resulting data to db.
#
# Authors: Don Dohan (dohan@aps.anl.gov)
#          Janet Anderson (jba@aps.anl.gov)
#
#
#

##########################################################################
##   NOTES
##########################################################################
## The main routine scans (call to parse_adl_file) the files in each 
## directory (e.g. adlsys/linac). 
## For each adl that is a (top) parent (meaning it doesn't require a
## substitution string passed to it in order for MEDM to correctly display it)
## parse_adl_file locates instances of either chan= or rdbk= and appends these
## process variable references into @adl_hits.
## If an instance of 'display[' is located, then parse_adlfile is recursively called.
## Instances of chan or rdbk, along with the level of the parse_adlfile recussion.
## Pv references that are more than 1 level deep are then combined into a rel_info string.
##    @adl_hits -- parent, rel_info, pv_list,pv_macro_list
## pv_macro_list is a list of pvnames which required macro replacement to resolve.
## After all files in the directory are processed the database files
##  uri, vuri, vuri_rel, and rec_client are updated. Existing medm records are marked
## as old and new medm records are added.
##
## Note:  if a child file (requires macro) in the adlsys directory is not called by a 
## valid top-parent, then this file will never be scanned for process variable references
## and an error message is logged.

##########################################################################

use strict;

use FileHandle;
use Cwd;

use LogUtil;
use ADLCrawlerDBLayer;
use ADLCrawlerParser;

##########################################################
#  GET SCRIPT ARGUMENTS
##########################################################
my $command;
my $go = 0; # don't run script unless explicitly enabled
my $debug_level = "info"; # default
my $adl_dirs_file = "";
my $edp_dirs_file = "";
my $decryption_key;
if (!@ARGV) {
    print_usage();
    exit;
}
while (@ARGV) {
    $command = shift @ARGV;
    if ($command =~ /-help/) {
        print_usage();
        exit;
    } elsif ($command =~ /--adl-dirs=(.*)/) {
        $adl_dirs_file = $1;
    } elsif ($command =~ /--edp-dirs=(.*)/) {
        $edp_dirs_file = $1;
    } elsif ($command =~ /--debug-level=(.*)/) {
        $debug_level = $1;
    } elsif ($command =~ /--go/) {
        $go = 1;
    } elsif ($command =~ /--key=(.*)/) {
        $decryption_key = $1;
    } elsif ($command =~ /--test/) {
        ADLCrawlerDBLayer::set_test_mode(1);
    } else {
        print_usage();
        exit;
    }
}
if (!$go || !$adl_dirs_file ) {
    print "You must at a minimum use --go and --adl-dirs command line options to run the script!\n";
    exit;
}

######## APS TEST ONLY #########
# create file of directory names
################################
if (!$adl_dirs_file ) {
    # Create APS specific default adl_dirs_file file
    # the following file contains the directories to be searched for adl files.
    # this file is created by the following:
    $adl_dirs_file  = "./createdAdlDirectoryFile";
    system("rm $adl_dirs_file");
    my $adl_source_top = "/net/helios/iocapps/adlsys/";		#<top> location of original adl files
    chdir $adl_source_top;
    system("find $adl_source_top -type d | grep -v CVS > $adl_dirs_file");
}

##########
#  SETUP
##########
# Can be 'verbose', 'debug', 'info', 'warn', or 'error'
LogUtil::set_log_level($debug_level);

# skipping LogUtil prefix for brevity from now on
log('info',"adl crawler beginning");

# Tell db layer where the home dir is before we do any chdir.
ADLCrawlerDBLayer::set_home_dir(getcwd());

# Optional 16 byte blowfish decryption key. If given (non null),
# it is assumed the db.properties file is encrypted.
ADLCrawlerDBLayer::set_decryption_key($decryption_key);

# get list of directories to search
if ( !open(ADLDIRS,$adl_dirs_file)) {
    log('error',"could not open directories file $adl_dirs_file: $!");
    die;
}
@adlDirs = <ADLDIRS>;
#log('debug',"adlDirs=@adlDirs");
close (ADLDIRS);

# get list of EPICS_DISPLAY_PATH directories to search
if ( !open(EDPDIRS,$edp_dirs_file)) {
    log('warn',"could not open EPICS_DISPLAY_PATH dirs file $edp_dirs_file: $!");
} else {
    while (<EDPDIRS>) {
      chomp;
      next if m/^\s*#/;
      log('debug',"EPICS_DISPLAY_PATH directory: $_");
      push(@epics_display_path_list,$_);
    }
    close (EDPDIRS);
}
if (! @epics_display_path_list) {
    log('warn',"No EPICS_DISPLAY_PATH directories. Adl files may not be found.");
}

# Globals from ADLCrawlerDBLayer
#update_adl_tables
#set_test_mode
#set_home_dir
#set_decryption_key

# Globals from ADLCrawlerParser
#@adlDirs
#@epics_display_path_list;
#$sub_level;
#%children;
#@adl_hits;
#%seen = ();
#&is_top_parent
#&parse_adlfile

my $numFiles;
my @adlfiles;
my @total_adlfiles;
my $adlfile;
my @keys;
my @uniq;
my $firstTime = 1;
my $dirname;
my @parentList;
my @argsList;
my $numRecords;

###################################
#  Iterate over directories
###################################
foreach $dirname (@adlDirs) {
    next if $dirname =~ m/^#/;
    @adl_hits = ();

    chomp $dirname;
    if (! -d $dirname) {
        log('warn',"Not a directory $dirname");
        next;
    }
    log('debug',"Processing directory $dirname");

    chdir $dirname;
    @adlfiles = <$dirname/*.adl>;	#get all adl files in directory.
    if (!@adlfiles) {	#if no adl files go to the next group.
        log('warn',"No adl files in directory $dirname");
        next;
    }
    push(@total_adlfiles,@adlfiles);

    # Number of files to process.
    $numFiles = $#adlfiles+1;
    log('debug',"Number of files to process in $dirname is $numFiles");
   

    ########################
    # iterate over adl files
    ########################
    my %children;
    foreach $adlfile (@adlfiles) {

        @parentList = ();
        @argsList = ();
        %children = ();
        $sub_level = 0;

        next if is_top_parent($adlfile)!=1 ; # file NOT top parent

        # At this point file is a top parent
        #log('info',"Processing top parent $adlfile");
        parse_adlfile(\@parentList,\@argsList,$adlfile,"''"); 

    }

    #####################################################
    # Update database tables rec_client, uri_rel, and uri 
    #####################################################

    $numRecords = scalar (@adl_hits);
    log('debug',"Adding $numRecords uri records from $dirname");

    ADLCrawlerDBLayer::update_adl_tables(\@adl_hits);

    @uniq = ();
    log('debug',"Finished processing $dirname");
}

##################################
# log # files processed and errors
##################################

# Log number of files processed.
@keys = (keys %seen);
$numFiles = $#keys+1;
log('info',"Total number of files processed is $numFiles");
@keys = ();
   

# Log error if a file in @total_adlfiles was not processed 
$numFiles = 0;
foreach $adlfile (@total_adlfiles) {
    if (!exists $seen{$adlfile}) {
        log('warn',"File not opened: $adlfile");
        $numFiles++;
    }
}

# Log number of files not processed.
log('info',"Total number of files NOT processed is $numFiles");
   
log('info',"adl crawler done");

exit;




#########################################################################
# Print out application usage text
#########################################################################
sub print_usage {
    print "usage: adl_crawler --go --adl-dirs=<file-name> --edp-dirs=<file-name> [--key=<16 char key>] [--test] [--help] [--debug-level=[verbose|debug|info]]\n\n";
    print "The adl_crawler scans a list of directories, parsing\n";
    print "what data it can into data structures representing adl files\n";
    print "and their content (pvname and related display references). These data\n";
    print "structures are then inserted into a mysql database using perl DBI.\n";
    print "\n";

    print "Log output goes to standard out, and can be adjusted from the default\n";
    print "\"info\" level. Watch out, since verbose is VERY verbose.\n\n";

    print "You will need to establish the expected schema by running the\n";
    print "create_pv_tables.sql and alter_pv_tables.sql scripts in the rdbCore\n";
    print "ddl subdirectory. Then edit db.properties to set your local database\n";
    print "connection parameters (ie. user/password etc.).\n\n";

    print "The list of directories to scan is determined by the --adl-dirs\n";
    print "option. You specify a file which contains full path directory names\n\n";

    print "A list of site EPICS_DISPLAY_PATH directories is determined by the --edp-dirs\n";
    print "option. You specify a file which contains full path directory names\n\n";

    print "The --test option runs the crawler, but does no db access.\n\n";


    print "You must have --go and --adl-dirs command line options to run the script!\n";
    print "\n\n";

}

