#!/usr/bin/perl -w

#
#  Copyright (c) 2004-2005 The University of Chicago, as Operator
#  of Argonne National Laboratory.
#

# aoi_crawler.pl
#   Reads ioc st.cmd files, dbd files, db files and 
#   writes resulting data to the aoi db. 
#
# Authors: Don Dohan (dohan@aps.anl.gov) 
#          Claude Saunders (saunders@aps.anl.gov)
#	   Debby Quock	(quock@aps.anl.gov)
#
#
#

use FileHandle;
use File::Basename; 
use Cwd;
use File::Compare;
use File::Path;
use File::stat;

use LogUtil;
use AOICrawlerDBLayer;
use AOICrawlerParser;

##########################################################
#  GET SCRIPT ARGUMENTS
##########################################################
$go = 0; # don't run script unless explicitly enabled
$force = 0;
$debug_level = "info"; # default
my @iocs = ();
if (!@ARGV) {
    print_usage();
    print "You must at a minimum use --go command line options to run the script!\n";
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
    } elsif ($command =~ /--test/) {
        AOICrawlerDBLayer::set_test_mode(1);
    } elsif ($command =~ /--force/) {
        $force = 1; 
    } else {
        push (@iocs,$command);
    }
}
if (!$go) {
    print "You must at a minimum use --go command line option to run the script!\n";
    exit;
}

##########################################################
#  SETUP
##########################################################
# Can be 'verbose', 'debug', 'info', 'warn', or 'error'
LogUtil::set_log_level($debug_level);

# skipping LogUtil prefix for brevity from now on
log('info',"aoi crawler beginning");

# Tell db layer where the home dir is before we do any chdir.
AOICrawlerDBLayer::set_home_dir(getcwd());

# Optional 16 byte blowfish decryption key. If given (non null),
# it is assumed the db.properties file is encrypted.
AOICrawlerDBLayer::set_decryption_key($decryption_key);

# Read site-specific file path substitutions (if any)
$path_substs = get_file_path_substs();
log ('info',"Calling get_file_path_substs()");

# Directory where temporary msioutput file can be written/read
$temp_file_dir       = "/tmp";

# verify that msi tool is accessible in user's path
$ENV{PATH} = $ENV{PATH}.':/usr/bin:/usr/local/epics/extensions/bin/solaris-sparc';
log('info',"Verify that msi tool is accessible");
$msi_output=`which msi`;
if ($msi_output =~ /^no msi/ ) {
    log('error','Cannot find the required EPICS utility "msi" in your path.');
    log('error','Exiting...');
    exit;
}
log('info',"$msi_output");

##########################################################
#  IDENTIFY IOC's to be examined
##########################################################

log('info',"Command line ioc list= @iocs") if scalar(@iocs);
log('info',"Calling find_boot_iocs()");
@ioc_boot_infos = find_boot_iocs(@iocs);

##########################################################
#  ITERATE OVER IOC's
##########################################################
foreach my $ioc_boot_info (@ioc_boot_infos) {

    my $ioc_nm = $ioc_boot_info->{ioc_nm};

#Code for testing
#next if $ioc_nm ne "siocbakeout" && 
#        $ioc_nm ne "ioclic2" && 
#        $ioc_nm ne "iocid04" && 
#        $ioc_nm ne "siocsrtune";
#next if $ioc_nm ne "siocbakeout" ;
#next if $ioc_nm ne "ioclic2" ;

    my $ioc_id = $ioc_boot_info->{ioc_id};
    my $current_load_ioc_boot_id = $ioc_boot_info->{ioc_boot_id};
    my $boot_date = $ioc_boot_info->{ioc_boot_date};
    my $sys_boot_line = $ioc_boot_info->{sys_boot_line};

    log('info',"=== Processing ioc $ioc_nm, boot date: $boot_date");

    # Get our last record of aoi update from db
    my $aoi_ioc_boot_id = aoi_crawler_boot_id($ioc_id);

    #If there is an aoi_crawler record, and the aoi_ioc_boot_id does NOT
    #differ and force was not specified , there is nothing to do
    if ($aoi_ioc_boot_id != 0 && 
         $aoi_ioc_boot_id eq $current_load_ioc_boot_id && !$force) {
            log('info',"Skipping up-to-date ioc $ioc_nm.");
        next;
    }

    log('info',"Parsing $sys_boot_line");

    #############################################################
    # PARSE UP ST.CMD FILE AND RETRIEVE RESULTANT DATA STRUCTURES
    #############################################################

    my $st_cmd_dir = dirname($sys_boot_line);
    
    initialize_parser($temp_file_dir, $st_cmd_dir, $ioc_nm, $path_substs);
    $parse_err = parse_startup_command($sys_boot_line,$ioc_id);

    # db and dbd file list 
    # (array of hashes, each hash with id, path, subst, mtime keys)
    $file_list = get_file_list();
    
    # list of ioc st.cmd lines that are associated with specific aoi's
    # (array of hashes, each hash with stcmd_line_id, stcmd_line, 
    #  ioc_nm, and stcmd_file_modified_date)
    $stcmd_line_list = get_stcmd_line_list();

    ##########################################################
    #  COMPARE CURRENT file_list WITH LAST DB LOAD INFO
    ##########################################################
    $ioc_resource_changed = 
        check_for_file_mods($file_list, $ioc_boot_info);
    log('debug',"IOC Resources changed: $ioc_resource_changed, Parse error: $parse_err");

    if ($ioc_resource_changed) {
        log('error',"ERROR: Resource files have changed since pvcrawler DB load.");
    }
    
    ##########################################################
    #  WRITE DATA TO DB (aoi stcmd lines and aoi pv data (if changed))
    ##########################################################    
    log('info',"Calling all_aoi_save for ioc $ioc_nm to write data to DB");
    AOICrawlerDBLayer::all_aoi_save($ioc_boot_info, $parse_err, 
                               $ioc_resource_changed,
                               $stcmd_line_list);

    # recover memory from possibly rather large arrays
    undef(@$file_list);
    undef(@$stcmd_line_list);
    
    log('info',"=== Finished processing ioc $ioc_nm");

}    # end for each active ioc


log('info',"aoi crawler done");
exit;


#############################################################
# SUBROUTINES
#############################################################


# Return mtime of given file in YYYYMMDDHHMMSS format
sub get_file_mod_date {
    my $file_name = $_[0];    # full file path
    
    if ( !( -e $file_name ) ) {
        return;
	} else {
        $mod_time = stat($file_name)->mtime;
        if ($mod_time) {
            return format_date($mod_time);
        } else {
            return;
        }
    }
}


# Compare file_list just scanned from ioc startup against last ioc boot records.
# If any file mod dates have changed, or any new files have been
# introduced or removed, return 1. Otherwise return 0.
#
sub check_for_file_mods {
    my $file_list = $_[0];
    my $ioc_boot_info = $_[1];

    my $ioc_resources = AOICrawlerDBLayer::find_ioc_resource_and_uri($ioc_boot_info);
    my $ioc_resource_size = @$ioc_resources;
    if (!$ioc_resource_size) {
        log('info',"No resources in DB.");
        return 1; # no prior record, so everything is "new"
    }
    
    # if they are different size, then something must have changed
    my $file_list_size = @$file_list;
    log('debug',"file_list_size $file_list_size irs $ioc_resource_size");
    if ($file_list_size != $ioc_resource_size) {
        log('info',"check_for_file_mods(): Resource array sizes differ, so return 1");
        log('info',"file_list_size $file_list_size irs $ioc_resource_size");
        return 1;
    }

    # now check to see if all file names and mod dates match
    my $all_match = 1;
    for $file (@$file_list) {
        my $found = 0;
        my $mod_dates_match = 0;
        # compare file paths and mod dates
#log('info',"check_for_file_mods(): checking $file->{path}");

        for $resource (@$ioc_resources) {
#log('info',"check_for_file_mods():         resource file $resource->{uri}");
$resourceFile = $resource->{uri};
$resourceFile =~ s/\.aps4\.anl\.gov//;
            #if ($file->{path} eq $resource->{uri}) {
            #}
            if ($file->{path} eq $resourceFile) {
                $found = 1;
                if (format_date($file->{mtime}) eq $resource->{uri_modified_date}) {
                    $mod_dates_match = 1;
                }
                last;
            }
        }
        if (!$found || !$mod_dates_match) {
            log('debug',"check_for_file_mods(): mismatch found: $found mod_dates_match $mod_dates_match, so return 1");
            log('info',"check_for_file_mods(): $file->{path} not found.") if !$found;
            log('info',"check_for_file_mods(): $file->{path} date mismatch.") if !$mod_dates_match;
            return 1;
        }
    }

    return 0;

}

# Look for a file called path.properties which contains a set of possible
# path substitutions to try when a file path cannot be found. This is to
# help in odd cases where some systems use non-standard mount points.
# File path.properties is a series of lines, each with:
#   /existing/sub-path=/new/sub-path/to/try
#
sub get_file_path_substs {

    my %substs = ();

    my $substs_path = getcwd() . "/path.properties";

    if (-e $substs_path) {
        open(PATHPROPS,$substs_path);
        while (my $props_line = <PATHPROPS>) {
            chomp($props_line);
            if ($props_line =~ /(.*)=(.*)/) {
                $existing_path = $1;
                $new_path = $2;
                $substs{$existing_path} = $new_path;
            }
        }
    }
    return \%substs;
}

# Takes seconds after epoch and converts to string of format "YYYYMMDDHHMMSS"
sub format_date {
    my $secsAfter1970 = $_[0];

    if (!$secsAfter1970) {
       return "0000-00-00";
    }

    my @tmArr = localtime($secsAfter1970);

    my $formatStr;
    $formatStr = '%Y%m%d%H%M%S';

    my $formatDate = POSIX::strftime($formatStr, $tmArr[0],$tmArr[1],$tmArr[2],
                                     $tmArr[3], $tmArr[4], $tmArr[5]);

    return $formatDate;
}

# Print out application usage text
sub print_usage {

    print "usage: aoi_crawler.pl --go [--key=<16 chars>] [--help] [--force] [<ioc1> <ioc2> ...]\n";
    print "\n";
    print "The aoi_crawler scans ioc startup command files for specified IOC\n";
    print "names(s) or for all iocs if no iocnames are specified, parsing\n";
    print "what data it can into data structures representing st.cmd lines\n";
    print "for an aoi(s) and the aoi's EPICS records and plc(s). The old structures\n";
    print "are removed from the database and the new structures are then inserted\n";
    print "into the database using perl DBI.\n";
    print "\n";
    print "Log output goes to standard out, and can be adjusted from the default\n";
    print "\"info\" level. Watch out, since verbose is VERY verbose.\n";
    print "\n";
    print "You will need to establish the expected schema by running the\n";
    print "create_aoi_tables.sql and alter_aoi_tables.sql scripts in the\n";
    print "ddl subdirectory. Then edit db.properties to set your local database\n";
    print "connection parameters (ie. user/password, db vendor, etc.).\n";
    print "If a 16 char decryption key is given, crawler assumes the\n";
    print "db.properties file is blowfish encrypted with the given key.\n";
    print "\n";
    print "The iocs to scan is determined by comparing the ioc_boot_id saved\n";
    print "in the aoi_crawler table by the last aoi_crawler scan to the \n";
    print "ioc_boot_id in the ioc_boot table where the current_load is 1\n";
    print "\n";
    print "The --force option forces a scan even when ioc_boot_id's match.\n";
    print "The --test option runs the crawler, but does no db writes.\n";
}

