#!/usr/bin/perl -w

#
#  Copyright (c) 2004-2005 The University of Chicago, as Operator
#  of Argonne National Laboratory.
#

# alh_crawler.pl
#   Reads medm alh files,and writes resulting data to db.
#
# Authors: Janet Anderson (jba@aps.anl.gov)
#
#
#

use FileHandle;
use Cwd;

use LogUtil;
use ALHCrawlerDBLayer;
use ALHCrawlerParser;

##########################################################
#  GET SCRIPT ARGUMENTS
##########################################################
$go = 0; # don't run script unless explicitly enabled
$debug_level = "info"; # default
$alh_dirs_file = "";
if (!@ARGV) {
    print_usage();
    exit;
}
while (@ARGV) {
    $command = shift @ARGV;
    if ($command =~ /-help/) {
        print_usage();
        exit;
    } elsif ($command =~ /--alh-dirs=(.*)/) {
        $alh_dirs_file = $1;
    } elsif ($command =~ /--debug-level=(.*)/) {
        $debug_level = $1;
    } elsif ($command =~ /--go/) {
        $go = 1;
    } elsif ($command =~ /--key=(.*)/) {
        $decryption_key = $1;
    } elsif ($command =~ /--test/) {
        ALHCrawlerDBLayer::set_test_mode(1);
    } else {
        print_usage();
        exit;
    }
}
if (!$go || !$alh_dirs_file) {
    print "You must at a minimum use --go and --alh-dirs command line options to run the script!\n";
    exit;
}

##########
#  SETUP
##########
# Can be 'verbose', 'debug', 'info', 'warn', or 'error'
LogUtil::set_log_level($debug_level);

# skipping LogUtil prefix for brevity from now on
log('info',"alh crawler beginning");

# Tell db layer where the home dir is before we do any chdir.
ALHCrawlerDBLayer::set_home_dir(getcwd());

# Optional 16 byte blowfish decryption key. If given (non null),
# it is assumed the db.properties file is encrypted.
ALHCrawlerDBLayer::set_decryption_key($decryption_key);

# get list of directories to search
if ( !open(ALHGROUPS,$alh_dirs_file)) {
    log('error',"Could not open directories file $alh_dirs_file: $!");
    die;
}
@alhDirs = <ALHGROUPS>;
#log('debug',"alh_crawler: alhDirs=@alhDirs");
close (ALHGROUPS);

my @alh_hits;
my %seen;
my $numFiles;
my @alhfiles;
my $alhfile;
my @keys;
my @uniq;

###################################
#  Iterate over directories
###################################
foreach $dirname (@alhDirs) {
    next if $dirname =~ m/^#/;
    %seen = ();
    @alh_hits = ();

    chomp $dirname;
    if (! -d $dirname) {
        log('error',"Not a directory $dirname");
        next;
    }
    log('info',"Processing directory $dirname");


    if (!opendir(IN, "$dirname") ) {
        log('warn',"Can't open directory $dirname: $!");
        next;
    }

    @alhfiles = grep /\.alhConfig$/, readdir IN; #get all the alh config files in directory
    closedir IN;
    if (!@alhfiles) {
        log('warn',"No alh config files in directory $dirname");
        next;
    }

    # Number of files to process.
    $numFiles = $#alhfiles+1;
    log('debug',"Number of files to process in $dirname is $numFiles");
   

    ########################
    # iterate over alh files
    ########################
    foreach $alhfile (@alhfiles) {

        log('info',"Processing alh file $alhfile");
        parse_alhfile(\%seen,\@alh_hits,"$dirname/$alhfile"); 

    }
    #######################
    # log # files processed
    #######################

    # Log number of files processed.
    @keys = (keys %seen);
    $numFiles = $#keys+1;
    log('debug',"Number of alh files processed in $dirname is $numFiles");
    @keys = ();
   
    #####################################################
    # Update database tables rec_client, uri_rel, and uri 
    #####################################################

    $numRecords = scalar (@alh_hits);
    log('debug',"Adding $numRecords uri records from $dirname");

    ALHCrawlerDBLayer::update_db_tables(\@alh_hits);

    @uniq = ();
    log('info',"Finished directory $dirname");
}

log('info',"alh crawler done");

exit;

#########################################################################
# Print out application usage text
#########################################################################
sub print_usage {
    print "usage: alh_crawler --go --alh-dirs=<file-name> [--key=<16 char key>] [--test] [--help] [--debug-level=[verbose|debug|info]]\n\n";
    print "The alh_crawler scans a list of directories, parsing data inf data\n";
    print "structures representing alh config files and their pvname references.\n";
    print "Structures are then inserted into a mysql database using perl DBI.\n";
    print "\n";

    print "Log output goes to standard out, and can be adjusted from the default\n";
    print "\"info\" level. Watch out, since verbose is VERY verbose.\n\n";

    print "You will need to establish the expected schema by running the\n";
    print "create_pv_tables.sql and alter_pv_tables.sql scripts in the rdbCore\n";
    print "ddl subdirectory. Then edit db.properties to set your local database\n";
    print "connection parameters (ie. user/password etc.).\n\n";

    print "The list of directories to scan is determined by the --alh-dirs\n";
    print "option. You specify a file which contains full path directory names\n";

    print "The --test option runs the crawler, but does no db access.\n";
    print "\\n";

    print "You must have --go and --alh-dirs command line options to run the script!\n";
}

