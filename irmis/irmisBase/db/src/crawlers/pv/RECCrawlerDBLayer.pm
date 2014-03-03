
package RECCrawlerDBLayer;

# This package provides subroutines for reading REC data from and writing
# REC data to a relational database. Vendor specific code is factored out into
# perl modules XXXDBFunctions.pm, so this module should remain
# vendor agnostic.

use strict;
use DBI qw(:sql_types);
use File::Basename; 
##use POSIX;
require Exporter;

use LogUtil qw(log);
use CommonUtil qw(db_connect get_test_mode);

our @ISA = qw(Exporter);
our @EXPORT_OK = qw(update_db_tables_rec);
our $VERSION = 1.00;


#***********
# Public sub
#***********
sub update_db_tables_rec {

    my $ioc_boot_info = $_[0];
    my $rec_values_hash_ref = $_[1];
    my $dbh;
    
    log('debug',"RECCrawlerDBLayer: update_db_tables_rec: Started.");

    if (!$dbh) {
        $dbh = db_connect();
    }

    my $ioc_nm = $ioc_boot_info->{ioc_nm};
    my $ioc_boot_id = $ioc_boot_info->{ioc_boot_id};

    update_rec_values($dbh,$rec_values_hash_ref,$ioc_nm,$ioc_boot_id);

    if (get_test_mode()) {
        $dbh->rollback;
        log('info',"RECCrawlerDBLayer: update_db_tables_rec: TEST MODE; Rollback REC value updates.");
    } else {
        log('debug',"RECCrawlerDBLayer: update_db_tables_rec: Commiting REC value updates.");
        $dbh->commit;
    }

    log('debug',"RECCrawlerDBLayer: update_db_tables_rec: Finished.");
    return;
}

#***********
# Private
#***********
# Load the rec values into the db rec table
sub update_rec_values {
    my $dbh = $_[0];
    my $rec_values_hash_ref = $_[1];
    my $ioc_nm = $_[2];
    my $ioc_boot_id = $_[3];

    my %rec_values_hash = %$rec_values_hash_ref;

    log('debug',"RECCrawlerDBLayer: $ioc_nm begin update_rec_values");

    if (!$dbh) {
        $dbh = db_connect();
    }

    my $update_rec_sql = 'update rec set rec_criticality=? where rec_nm=? and ioc_boot_id=?';
    my $update_rec_st;    
    my $rec_criticality;    
    my $count=0;

    for my $recnm (keys %rec_values_hash) {
        $rec_criticality = $rec_values_hash{$recnm};
        $rec_criticality = 1 if !defined $rec_criticality;
        eval {
            $update_rec_st = $dbh->prepare($update_rec_sql);
            $update_rec_st->bind_param(1,$rec_criticality,SQL_INTEGER);
            $update_rec_st->bind_param(2,$recnm,SQL_VARCHAR);
            $update_rec_st->bind_param(3,$ioc_boot_id,SQL_INTEGER);
            $count += $update_rec_st->execute();
        };
        if ($@) {
            log('error',"$ioc_nm update_rec_values():$@");
            exit;
        }
    }
    log('info',"RECCrawlerDBLayer:Updated $count criticality values.");
}

1;
