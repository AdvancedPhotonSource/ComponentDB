#!/usr/bin/perl -w

#
#  Copyright (c) 2004-2005 The University of Chicago, as Operator
#  of Argonne National Laboratory.
#

# iocsr_crawler.pl
#   Captures pv names from each ioc's .req save/restore
#   file. These pvs are listed in the rec_client table
#   with the corresponding .req file from which they came.
#   The list of iocs to examine comes from the ioc_boot table.
#
# Authors: Claude Saunders (saunders@aps.anl.gov)
#
#
#

use FileHandle;
use Cwd;

use LogUtil;
use IOCSRCrawlerDBLayer;

##########################################################
#  GET SCRIPT ARGUMENTS
##########################################################
$go = 0; # don't run script unless explicitly enabled
$debug_level = "info"; # default
$app_name = "";
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
     } elsif ($command =~ /--debug-level=(.*)/) {
        $debug_level = $1;
    } elsif ($command =~ /--go/) {
        $go = 1;
    } elsif ($command =~ /--key=(.*)/) {
        $decryption_key = $1;
    } elsif ($command =~ /--app-name=(.*)/) {
        $app_name = $1;
    } elsif ($command =~ /--test/) {
        IOCSRCrawlerDBLayer::set_test_mode(1);
    } else {
        print_usage();
        exit;
    }
}
if (!$go || !$app_name) {
    print "You must at a minimum use --go and --app-name command line options to run the script!\n";
    exit;
}

##########
#  SETUP
##########
# Can be 'verbose', 'debug', 'info', 'warn', or 'error'
LogUtil::set_log_level($debug_level);

# skipping LogUtil prefix for brevity from now on
log('info',"iocsr crawler beginning, app name: $app_name");

# Tell db layer where the home dir is before we do any chdir.
IOCSRCrawlerDBLayer::set_home_dir(getcwd());

# Optional 16 byte blowfish decryption key. If given (non null),
# it is assumed the db.properties file is encrypted.
IOCSRCrawlerDBLayer::set_decryption_key($decryption_key);

# hash that associates .req file with its array of pvs
# (hash of arrays, keyed by .req file path
#  array elems are pv names)
my %iocsr_pvs; 

# array of directory paths which may have .req files in them
my @ioc_boot_dirs = ();

# load hash with .req file names from ioc_boot table
IOCSRCrawlerDBLayer::get_boot_dirs(\@ioc_boot_dirs);

###################################
#  Iterate over files
###################################
foreach my $boot_dir (@ioc_boot_dirs) {

    chomp $boot_dir;

    log('debug',"iocsr_crawler: Processing $boot_dir");

    ###########################
    # get all .req files in dir
    ###########################
    my @req_files = glob $boot_dir . "/*.req";

    ###########################
    # get pv names out of file
    ###########################
    foreach my $filename (@req_files) {
        chomp $filename;

        if (open(TEXTFILE, $filename)) {
            while (my $pv_line = <TEXTFILE>) {
                chomp($pv_line);
                if ($pv_line =~ /^\w/) {
                    push @{$iocsr_pvs{$filename}}, $pv_line;
                }
            }
            close(TEXTFILE);
            
        } else {
            log('warn',"Unable to open file.");
        } 
        
    }
}

log('info',"total # files processed: ".  keys %iocsr_pvs);

# Write out data to database
log('info',"Write data to DB");
IOCSRCrawlerDBLayer::all_save($app_name, \%iocsr_pvs);

log('info',"iocsr crawler done");

exit;

#########################################################################
# Print out application usage text
#########################################################################
sub print_usage {
    print "usage: iocsr_crawler --go --app-name=<app-name> [--key=<16 char key>] [--test] [--help] [--debug-level=[verbose|debug|info]]\n\n";
    print "The iocsr_crawler processes .req files found in the ioc boot directories.\n";
    print "The directories are found by consulting the ioc_boot table and grabbing the\n";
    print "sys_boot_line column for current rows. The pvs are grabbed out of each\n";
    print "ioc save/restore req file and entered in the rec_client table.\n";
    print "\n";

    print "Log output goes to standard out, and can be adjusted from the default\n";
    print "\"info\" level. Watch out, since verbose is VERY verbose.\n\n";

    print "You must also give the name of the application which is normally\n";
    print "used to process the req file(s) (ie. sddslogger) with the --app-name\n";
    print "option.\n\n";

    print "The --test option runs the crawler, but does no db access.\n";
    print "\n";

    print "You must have --go and --app-name command line \n";
    print "options to run the script!\n";
}

