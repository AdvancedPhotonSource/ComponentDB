#!/usr/bin/perl -w


#
#  Copyright (c) 2004-2005 The University of Chicago, as Operator
#  of Argonne National Laboratory.
#

# network_crawler.pl
#   Reads  accel_sw.log and FESwitch.log files,and writes resulting network data to db.
#
# Authors: Janet Anderson (jba@aps.anl.gov)
#
#
#

use Mail::Mailer;
use File::stat;
use Cwd;
use Blowfish_PP;

use LogUtil;
use NetworkCrawlerParser;
use NetworkCrawlerDBLayer;

my $status = 0;
my $allowed_updates = 4;

##########################################################
#  GET SCRIPT ARGUMENTS
##########################################################
my $test_mode = 0;
my $go = 0; # don't run script unless explicitly enabled
my $debug_level = "info"; # default
my $decryption_key = "";
my $replace_all_network_data = 0;
my $force_database_update = 0; # don't update database unless <= allowed_updates

if (!@ARGV) {
    print_usage();
    exit;
}
while (@ARGV) {
    $command = shift @ARGV;
    if ($command =~ /-help/) {
        print_usage();
        exit;
    } elsif  ($command =~ /--force/) {
        $force_database_update = 1;
    } elsif  ($command =~ /--all/) {
        $replace_all_network_data = 1;
    } elsif ($command =~ /--debug-level=(.*)/) {
        $debug_level = $1;
    } elsif ($command =~ /--go/) {
        $go = 1;
    } elsif ($command =~ /--key=(.*)/) {
        # Optional 16 byte blowfish decryption key. If given (non null),
        $decryption_key = $1;
    } elsif ($command =~ /--test/) {
        set_test_mode(1);
        $test_mode =1;
    } else {
        print_usage();
        exit;
    }
}
if (!$go ) {
    print "You must at a minimum use --go command line option to run the script!\n";
    exit;
}

##########
#  SETUP
##########

# Can be 'verbose', 'debug', 'info', 'warn', or 'error'
LogUtil::set_log_level($debug_level);

email_log('info',"Starting ioc network data update script.");   

my @infile;
# Set the input and temporary files
$infile[0] = "/usr/local/iocapps/iocinfo/network/accel_sw.log";
# =======  JBA debug settings
#$infile[0]   = "./accel_sw.log";
# =======


$sb_in=stat($infile[0]);
$infile_date=scalar localtime $sb_in->mtime;

################
#  Setup DBLayer
################

# Tell db layer where the home dir is before we do any chdir.
set_home_dir(getcwd());

# it is assumed the db.properties file is encrypted.
set_decryption_key($decryption_key);


#######################################################
#  Start database update or replace of ioc network data
#######################################################

my @network_hits=();

if ($replace_all_network_data) {

  ########################################################################################
  # Replace all ioc network data with data from latest accel_sw.log and FESwitch.log files
  ########################################################################################
  email_log('info',"New accel_sw.log file date: $infile_date");   
  #$status = zero_out_network_data(); # Some iocs have hand entered data which should not be zeroed out.
  if ( $status ==  0 ) {
    $status = get_all_network_data(\@infile,\@network_hits);
    if ( $status ==  0 ) {
      $status = load_new_network_data(\@network_hits);
    }
  }

} else {

  #############################################################################
  # Check accel_sw.log and FESwitch.log files for differing network data
  #############################################################################

  email_log('info',"Updating modified network data.");   

  if ( $status == 0) {
    $networkData_ref = get_db_network_data();
    $status = get_modified_network_data(\@infile,\@network_hits,$networkData_ref);
    if ( $status >=  0 ) {
      if ( $status > $allowed_updates && !$force_database_update ) {
        email_log('error',"Update count $status is more than $allowed_updates. Database NOT updated.");   
      } else { 
        $status = load_new_network_data(\@network_hits);
      }
    }
  }
}

##################################
# Finish log, send email and exit.
##################################

email_log('info',"Finished ioc network data update script.");   
send_email();

exit;


#########################################################################
# Print out application usage text
#########################################################################
sub print_usage {
    print "\n";
    print "usage: network_crawler.pl --go [--key=<16 char key>] [--test/all/force] [--help] [--debug-level=[verbose|debug|info]]\n\n";


print "The network crawler reads accel_sw.log and FESwitch.log files and writes resulting network\n";
print "data to the MYSQL database.\n";
print "\n";
print "The network crawler first creates a hash of current network data from the\n";
print "SQL database for iocs with ioc_status set to 'production', 'ancillary', or\n";
print "'development'.  If the switch name starts with 'No ' in the database the data\n";
print "is not included in the hash.\n";
print "\n";
print "The network_crawler then creates a hostname hash, group of soft iocnames\n";
print " keyed by their hostname using boot data from \$bootparams/sioc* files.\n";
print "\$bootparams is currently set to /usr/local/iocapps/iocinfo/bootparams.\n";
print "\n";
print "The network_crawler next reads files (accel_sw.log,FESwitch.log) of network\n";
print "interface data, produced nightly from an IT script which queries switch SNMP values, \n";
print "parsing the data and comparing it to the existing MYSQL database data hash to \n";
print "determine if there are changes to ioc primary and secondary ethernet switch, \n";
print "blade, and port settings.  If the file data differs from the database data \n";
print "the a new data array [iocname,ethernettype,switch,blade,port] is added to a \n";
print "changed data hash (%network_hits). \n";
print "\n";
print "Finally, if the number of changes is less than or equal to \$allowed_updates\n";
print "(currently 4), the MYSQL database is updated with data from the network_hits\n";
print "hash.\n";
print "\n";
print "If there is no data in the file for an ioc 'No file data' is logged.\n";
print "If an iocname from the file is not in the database hash 'Unknown ioc..'\n";
print "is logged.\n";
print "\n";
print "Log output goes to standard out and is sent via email to addresses in the \n";
print "\$to_address list (currently jba).\n";
print "\n";
print "The --go command line option is required to execute the crawler script.\n";
print "\n";
print "The --test command line option runs the crawler, but does no db changes.\n";
print "\n";
print "The --all option zeros out all ioc network data in the MYSQL database and\n";
print "then updates the database ioc network data with values from the accel_sw.log\n";
print "file.\n";
print "\n";
print "The --key=(.*) option is the 16 byte blowfish decryption key for database \n";
print "access.\n";
print "\n";
print "The --force option forces a database update when there are more than the \n";
print "allowed number of database updates (\$allowed_updates).\n";
print "\n";

}

