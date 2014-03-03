
package SNSBootScan;

# The purpose of this package is to implement subroutines which
# identify ioc's to be crawled, and their startup command file 
# names and paths. Since this differs from EPICS facility to 
# facility, we need to have one of these packages per facility.
#
# Implements the required "find_boot_iocs" subroutine of the BootScan
# package for the SNS facility. This subroutine must return an
# array of hashes, each hash as follows:
#  $boot_info = {ioc_nm=>"$ioc_nm",            # ioc name
#                booted=>"$ioc_booted",        # is it considered booted?
#                boot_date=>"$boot_date",      # date of boot
#                st_cmd_dir=>"$stCmdDir",      # startup command path info
#                st_cmd_file=>"$stCmdFile"};
#
use File::Basename; 
use File::stat;

use LogUtil;

require Exporter;

our @ISA = qw(Exporter);
our @EXPORT = qw(find_boot_iocs);
our @EXPORT_OK = qw();
our $VERSION = 1.00;

# Location where site IOC's boot from
my $IOCBootBaseDir = "/local/home/SAUNDERS/snsBoot/ade/epics/iocCommon/";

#
# Returns array of hashes, one element per ioc.
#
sub find_boot_iocs {

    chdir $IOCBootBaseDir; 

    my @candidate_ioc_names = "ccl-vac-ioc1"; # gets all in cwd
    my @ioc_names = ();
    my @boot_infos = ();

    # put any rules here to filter out unwanted iocs
    #  - for now, accept all
    foreach $ioc_nm (@candidate_ioc_names) {
        push @ioc_names, $ioc_nm;
    }
    log('debug',"iocs to be boot scanned: @ioc_names");

    # get boot time and startup cmd file path
    foreach $ioc_nm (@ioc_names) {

        $st_cmd_file = "startup.cmd";  # always this, apparently
        $st_cmd_dir = $IOCBootBaseDir . $ioc_nm;

        # Let's try and use the pvlist file to figure out boot date
        $pvlist_path = $st_cmd_dir . "/var/$ioc_nm.pvlist";
        if (-e $pvlist_path) {
            $boot_date = stat($pvlist_path)->mtime;
            $booted = 1;
        } else {
            $boot_date = "00000000000000";
            $booted = 0;
        }

        # Create new boot_info hash entry
        $new_boot_info = {ioc_nm=>"$ioc_nm", booted=>"$booted",
                          boot_date=>"$boot_date", st_cmd_dir=>"$st_cmd_dir", 
                          st_cmd_file=>"$st_cmd_file"};

        log('debug',"adding boot info $ioc_nm $booted $boot_date $st_cmd_dir");

        push @boot_infos, $new_boot_info;
    }

    return @boot_infos;
}

1;
