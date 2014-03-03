#!/usr/bin/perl -w

#
#  Copyright (c) 2004-2005 The University of Chicago, as Operator
#  of Argonne National Laboratory.
#

# sdds_crawler.pl
#   Reads sdds files, and writes any ControlName column to
#   the rec_client table. This is a generic pv client crawler
#   for sdds files that contain a ControlName column.
#
# Authors: Claude Saunders (saunders@aps.anl.gov)
#
#
#

use FileHandle;
use Cwd;

use LogUtil;
use SDDSCrawlerDBLayer;

##########################################################
#  GET SCRIPT ARGUMENTS
##########################################################
$go = 0; # don't run script unless explicitly enabled
$debug_level = "info"; # default
$sdds_files_file = "";
$sdds_app_name = "";
$decryption_key = "";
if (!@ARGV) {
    print_usage();
    exit;
}
while (@ARGV) {
    $command = shift @ARGV;
    if ($command =~ /-help/) {
        print_usage();
        exit;
    } elsif ($command =~ /--sdds-files=(.*)/) {
        $sdds_files_file = $1;
    } elsif ($command =~ /--debug-level=(.*)/) {
        $debug_level = $1;
    } elsif ($command =~ /--go/) {
        $go = 1;
    } elsif ($command =~ /--key=(.*)/) {
        $decryption_key = $1;
    } elsif ($command =~ /--sdds-app-name=(.*)/) {
        $sdds_app_name = $1;
    } elsif ($command =~ /--test/) {
        SDDSCrawlerDBLayer::set_test_mode(1);
    } else {
        print_usage();
        exit;
    }
}
if (!$go || !$sdds_files_file || !$sdds_app_name) {
    print "You must at a minimum use --go, --sdds-files, and --sdds-app-name command line options to run the script!\n";
    exit;
}

##########
#  SETUP
##########
# Can be 'verbose', 'debug', 'info', 'warn', or 'error'
LogUtil::set_log_level($debug_level);

# skipping LogUtil prefix for brevity from now on
log('info',"sdds crawler beginning, app name: $sdds_app_name files: $sdds_files_file");

# Tell db layer where the home dir is before we do any chdir.
SDDSCrawlerDBLayer::set_home_dir(getcwd());

# Optional 16 byte blowfish decryption key. If given (non null),
# it is assumed the db.properties file is encrypted.
SDDSCrawlerDBLayer::set_decryption_key($decryption_key);

# get list of directories to search
if ( !open(SDDSFILES,$sdds_files_file)) {
    log('error',"sdds_crawler: could not open $sdds_files_file: $!");
    die;
}
@sddsFiles = <SDDSFILES>;
close (SDDSFILES);

# hash that associates sdds file with its array of pvs
# (hash of arrays, keyed by sdds file path
#  array elems are pv names)
my %sdds_pvs; 

###################################
#  Iterate over files
###################################
foreach $filename (@sddsFiles) {
    next if $filename =~ m/^#/;

    chomp $filename;

    log('debug',"sdds_crawler: Processing $filename");


    ###########################
    # get pv names out of file
    ###########################
    $stdout = `sdds2plaindata $filename /tmp/sddstext.txt -noRowCount -outputMode=ascii -column=ControlName 2>&1`;
    if ($stdout =~ /^[eE]rror/) {
        log('warn',"Unable to open and/or convert $filename: $stdout");
        next;
    }
    open(TEXTFILE, '/tmp/sddstext.txt' );
    while (my $pv_line = <TEXTFILE>) {
        chomp($pv_line);
        push @{$sdds_pvs{$filename}}, $pv_line;
    }
    close(TEXTFILE);
    unlink('/tmp/sddstext.txt');

}

log('info',"total # files processed: ".  keys %sdds_pvs);

# Write out data to database
log('info',"Write data to DB");
SDDSCrawlerDBLayer::all_save($sdds_app_name, \%sdds_pvs);

log('info',"sdds crawler done");

exit;

#########################################################################
# Print out application usage text
#########################################################################
sub print_usage {
    print "usage: sdds_crawler --go --sdds-files=<file-name> --sdds-app-name=<app-name> [--key=<16 char key>] [--test] [--help] [--debug-level=[verbose|debug|info]]\n\n";
    print "The sdds_crawler processes a list of sdds files specified in the input\n";
    print "file given in the --sdds-files option. In each sdds file the crawler looks for\n";
    print "a column entitled ControlName. These PVs are then entered in the\n";
    print "rec_client table with corresponding vuri and uri entries.\n";
    print "\n";

    print "Log output goes to standard out, and can be adjusted from the default\n";
    print "\"info\" level. Watch out, since verbose is VERY verbose.\n\n";

    print "The list of files to process is given by the --sdds-files\n";
    print "option. You specify a file which contains full path names.\n\n";

    print "You must also give the name of the sdds application which is normally\n";
    print "used to process the sdds file (ie. sddslogger) with the --sdds-app-name\n";
    print "option.\n\n";

    print "The --test option runs the crawler, but does no db access.\n";
    print "\n";

    print "You must have --go, --sdds-files, and sdds-app-name command line \n";
    print "options to run the script!\n";
}

