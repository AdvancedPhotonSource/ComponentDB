
package PVCrawlerDBHistoryLayer;

# Move noncurrent ioc boot data to history tables

use strict;
use DBI qw(:sql_types);
require Exporter;
use LogUtil qw(log);
use CommonUtil qw(db_connect ioc_load_by_name get_test_mode get_home_dir get_db_vendor);

our @ISA = qw(Exporter);
our @EXPORT_OK = qw(update_db_tables_hist);
our $VERSION = 1.00;

# Public
sub update_db_tables_hist {
    my $ioc_nm = $_[0];
    my $count=0;
    my $dbh;

    log('debug',"PVCrawlerDBHistoryLayer: update_db_tables: Started $ioc_nm.");

    # Check connect to database -------------------------------------------------------
    if (!$dbh) {
        $dbh = db_connect();
        if (!$dbh) {
            log('error','PVCrawlerDBHistoryLayer: update_db_tables: NOT connected to database.');
            print('ERROR: NOT connected to database.\n');
            print('ERROR: Exiting...\n');
            exit(-1);
        }
    }

    # ======  Get ioc_id ------------------------------------------------------------
    my $ioc_id=ioc_load_by_name($ioc_nm);
    if (!$ioc_id) {
        log('error',"PVCrawlerDBHistoryLayer: update_db_tables: Iocname $ioc_nm not valid or not in database.");
        return(-1);
    }

    # ======  Get old ioc_boot_ids for an ioc_id -----------------------------------
    my $select_ioc_boot_st;
    my $select_ioc_boot_sql = ' SELECT ioc_boot_id FROM ioc_boot WHERE ioc_id = ? '.
          'AND current_load = 0 AND current_boot = 0';
    $select_ioc_boot_st = $dbh->prepare($select_ioc_boot_sql);
    eval {
        $select_ioc_boot_st->bind_param(1, $ioc_id, SQL_INTEGER);
        $count=$select_ioc_boot_st->execute();
    };
    if ($@) {
        log('error',"PVCrawlerDBHistoryLayer: update_db_tables: ioc_boot copy rows: $@");
        exit(-1);
    }
    if ($count == 0) {
        log('info',"PVCrawlerDBHistoryLayer: update_db_tables: ioc=$ioc_nm No old ioc_boot data.");
        return;
    }
    log('debug',"PVCrawlerDBHistoryLayer: update_db_tables: ioc=$ioc_nm ioc_id=$ioc_id $count boots");

    my $ioc_resource_hash_ref;
    my $rec_type_hash_ref;
    my $fld_type_hash_ref;
    my $rec_hash_ref;

    my $ioc_boot_row;
    my $ioc_boot_id;

    my $delete_fld_sql = 'delete fld from rec,fld where fld.rec_id=rec.rec_id '.
                         'and rec.ioc_boot_id=? ';

    my $delete_fld_type_sql = 'delete fld_type from fld_type,rec_type where '.
                             'fld_type.rec_type_id=rec_type.rec_type_id '.
                             'and rec_type.ioc_boot_id=? ';

    my $delete_rec_type_dev_sup_sql = 'delete rec_type_dev_sup from rec_type_dev_sup,rec_type '.
                             'where rec_type_dev_sup.rec_type_id=rec_type.rec_type_id '.
                             'and rec_type.ioc_boot_id=? ';

    my $delete_rec_alias_sql = 'delete rec_alias from rec,rec_alias where '.
                     'rec_alias.rec_id=rec.rec_id and rec.ioc_boot_id=? ';

    my $delete_rec_sql = 'delete from rec where ioc_boot_id = ?';

    my $delete_rec_type_sql = 'delete from rec_type where ioc_boot_id = ?';

    my $delete_ioc_resource_and_uri_sql = 'delete ioc_resource,uri from ioc_resource '.
          'inner join uri where ioc_resource.uri_id=uri.uri_id '.
          'and ioc_resource.ioc_boot_id = ?';

    # Loop over ioc_boot_ids -------------------------------------------------------
    while (my $ioc_boot_row = $select_ioc_boot_st->fetchrow_hashref)
    {

        $ioc_boot_id = $ioc_boot_row->{ioc_boot_id};
        log('debug',"PVCrawlerDBHistoryLayer: update_db_tables: ### ioc_boot_id=$ioc_boot_id");

        # ====== Copy old boot data to history tables ==========
        $ioc_resource_hash_ref = ioc_resource_and_uri_copy_to_history($dbh,$ioc_boot_id);
        next if ! %$ioc_resource_hash_ref;
        $rec_type_hash_ref = rec_type_copy_to_history($dbh,$ioc_boot_id,$ioc_resource_hash_ref);
        $rec_hash_ref = rec_and_rec_alias_copy_to_history($dbh,$ioc_boot_id,$rec_type_hash_ref,$ioc_resource_hash_ref);
        rec_type_dev_sup_copy_to_history($dbh,$rec_type_hash_ref);
        $fld_type_hash_ref = fld_type_copy_to_history($dbh,$rec_type_hash_ref,$ioc_boot_id);
        fld_copy_to_history($dbh,$rec_hash_ref,$fld_type_hash_ref,$ioc_resource_hash_ref,$ioc_boot_id);

        # ====== Delete old boot data from current data tables ==========
        $count=0;
        $count+=delete_rows($dbh,$ioc_boot_id,'fld',$delete_fld_sql);
        $count+=delete_rows($dbh,$ioc_boot_id,'fld_type',$delete_fld_type_sql);
        $count+=delete_rows($dbh,$ioc_boot_id,'rec_type_dev_sup',$delete_rec_type_dev_sup_sql);
        $count+=delete_rows($dbh,$ioc_boot_id,'rec_alias',$delete_rec_alias_sql);
        $count+=delete_rows($dbh,$ioc_boot_id,'rec',$delete_rec_sql);
        $count+=delete_rows($dbh,$ioc_boot_id,'rec_type',$delete_rec_type_sql);
        $count+=delete_rows($dbh,$ioc_boot_id,'ioc_resource_and_uri',$delete_ioc_resource_and_uri_sql);

        if (get_test_mode()) {
            $dbh->rollback;
            log('info',"PVCrawlerDBHistoryLayer: update_db_tables TEST MODE: No database updates.");
        } else {
            log('debug',"PVCrawlerDBHistoryLayer: update_db_tables: Committing changes for ioc=$ioc_nm ioc_boot_id=$ioc_boot_id");
            $dbh->commit;
        }
        log('info',"PVCrawlerDBHistoryLayer: ioc_boot_id=$ioc_boot_id $count records moved.");
    }
    log('debug',"PVCrawlerDBHistoryLayer: update_db_tables: Finished.");
    return;
}

# Private
sub ioc_resource_and_uri_copy_to_history {
    my $dbh = $_[0];
    my $ioc_boot_id = $_[1];
    my %ioc_resource_hash=();
    my $count=0;
    my $ioc_resource_id;
    my $uri_id;
    my $total=0;
    my $uri_row;
    my $uri_history_id;

    log('verbose',"ioc_resource_and_uri_copy_to_history: ioc_boot_id=$ioc_boot_id");

    # ======  Get ioc_resource lines for an ioc_boot_id ==========
    my $fields  = 'ioc_resource_id,ioc_boot_id,text_line,load_order,uri_id,unreachable,subst_str,ioc_resource_type_id';
    my $select_ioc_resource_sql = "select $fields from ioc_resource where ioc_boot_id = ?";
    my $select_ioc_resource_st = $dbh->prepare($select_ioc_resource_sql);

    eval {
        $select_ioc_resource_st->bind_param(1, $ioc_boot_id, SQL_INTEGER);
        $count=$select_ioc_resource_st->execute();
    };
    if ($@) {
        $dbh->rollback;
        log('error',"ioc_resource read: $@");
        exit(-1);
    }
    log('debug',"ioc_resource_and_uri_copy_to_history: ioc_boot_id=$ioc_boot_id $count ioc_resource selected");

    # SQL for selecting rows of the uri table
    $fields  = 'uri,uri_modified_date,modified_date,modified_by';
    my $select_uri_sql = "select $fields from uri where uri_id = ?";
    my $select_uri_st = $dbh->prepare($select_uri_sql);

    # SQL for writing out a row of the uri_history table
    my $insert_uri_history_sql = 'insert into uri_history ' .
        '(uri_history_id, uri, uri_modified_date, modified_date,modified_by) ' .
        'values (?, ?, ?, ?,?)';
    my $insert_uri_history_st = $dbh->prepare($insert_uri_history_sql);

    # SQL for writing out a row of the ioc_resource_history table
    my $ioc_resource_history_id;
    my $insert_ioc_resource_history_sql = 'insert into ioc_resource_history ' .
      '(ioc_resource_history_id, ioc_boot_id, text_line, load_order, ' .
      'uri_history_id, unreachable, subst_str, ioc_resource_type_id) ' .
      'values (?, ?, ?, ?, ?, ?, ?, ?)';
    my $insert_ioc_resource_history_st = $dbh->prepare($insert_ioc_resource_history_sql);

    my $uri_history_count=0;
    my $ioc_resource_history_count=0;

    # Loop over ioc_resource_ids ==========
    while (my $ioc_resource_row = $select_ioc_resource_st->fetchrow_hashref) 
    {
        $ioc_resource_id = $ioc_resource_row->{ioc_resource_id};
        $uri_id = $ioc_resource_row->{uri_id};

        # =====  Get uri row ==========
        eval {
            $select_uri_st->bind_param(1, $uri_id, SQL_INTEGER);
            $count=$select_uri_st->execute();
            $total=$total+$count;
        };
        if ($@) {
            $dbh->rollback;
            log('error',"uri read: $@");
            exit(-1);
        }
        log('verbose'," ioc_resource_id=$ioc_resource_id uri_id=$uri_id $count uri selected.");

        $uri_row = $select_uri_st->fetchrow_hashref;
        if ( ! $uri_row ) {
            $dbh->rollback;
            log('error',"NO uri_id for ioc_resource_id $ioc_resource_id ");
        }
            
        log('verbose',"writing uri_history uri_id=$uri_id uri=$uri_row->{uri}");
        eval {
            # bind and execute to write out uri_history row
            $uri_history_id = pre_insert_get_id($dbh, 'uri_history');
            $insert_uri_history_st->bind_param(1, $uri_history_id, SQL_INTEGER) if $uri_history_id ;
            $insert_uri_history_st->bind_param(2, $uri_row->{uri}, SQL_VARCHAR);
            $insert_uri_history_st->bind_param(3, $uri_row->{uri_modified_date}, SQL_TIMESTAMP);
            $insert_uri_history_st->bind_param(4, $uri_row->{modified_date}, SQL_TIMESTAMP);
            $insert_uri_history_st->bind_param(5, $uri_row->{modified_by}, SQL_VARCHAR);
            $uri_history_count+=$insert_uri_history_st->execute();
            $uri_history_id = post_insert_get_id($dbh, 'uri_history');

            # bind and execute to write out ioc_resource_history row
            $ioc_resource_history_id = pre_insert_get_id($dbh, 'ioc_resource_history');
            $insert_ioc_resource_history_st->bind_param(1, $ioc_resource_history_id, SQL_INTEGER) if $ioc_resource_history_id;
            $insert_ioc_resource_history_st->bind_param(2, $ioc_resource_row->{ioc_boot_id} , SQL_INTEGER);
            $insert_ioc_resource_history_st->bind_param(3, $ioc_resource_row->{text_line}, SQL_VARCHAR);
            $insert_ioc_resource_history_st->bind_param(4, $ioc_resource_row->{load_order}, SQL_INTEGER);
            $insert_ioc_resource_history_st->bind_param(5, $uri_history_id, SQL_INTEGER);
            $insert_ioc_resource_history_st->bind_param(6, $ioc_resource_row->{unreachable}, SQL_INTEGER);
            $insert_ioc_resource_history_st->bind_param(7, $ioc_resource_row->{subst_str}, SQL_VARCHAR);
            $insert_ioc_resource_history_st->bind_param(8, $ioc_resource_row->{ioc_resource_type_id}, SQL_INTEGER);
            $ioc_resource_history_count+=$insert_ioc_resource_history_st->execute();
        };
        if ($@) {
            $dbh->rollback;
            log('error',"ioc_resource_and_uri_copy_to_history: $@");
            exit(-1);
        }
        log('verbose',"writing uri_history uri_id=$uri_id uri_history_id=$uri_history_id uri=$uri_row->{uri} .");
        $ioc_resource_history_id = post_insert_get_id($dbh, 'ioc_resource_history');
        $ioc_resource_hash{$ioc_resource_id}=$ioc_resource_history_id;
    }
    log('debug',"ioc_resource_and_uri_copy_to_history: $total uri selected");
  
    log('info',"PVCrawlerDBHistoryLayer: Inserted ".
                    "$uri_history_count uri_history ".
                    "$ioc_resource_history_count ioc_resource_history") 
                    if $uri_history_count > 0 || $ioc_resource_history_count > 0;

    return \%ioc_resource_hash;
}


# Private
sub rec_type_copy_to_history {
    my $dbh = $_[0];
    my $ioc_boot_id = $_[1];
    my $ioc_resource_hash_ref = $_[2];
    my %ioc_resource_hash = %$ioc_resource_hash_ref;
    my %rec_type_hash=();
    my $count=0;
    my $rec_type_id;
    my $ioc_resource_id;
    my $rec_type_history_id;
    my $insert_rec_type_history_sql;
    my $insert_rec_type_history_st;
    my $rec_type_row;

    log('verbose',"rec_type_copy_to_history: ioc_boot_id=$ioc_boot_id.");

    # ======  SQL for selecting rec_type lines for an ioc_boot_id ==========
    my $fields  = 'rec_type_id,rec_type,ioc_resource_id';
    my $select_rec_type_sql = "select $fields from rec_type where ioc_boot_id = ?";
    my $select_rec_type_st = $dbh->prepare($select_rec_type_sql);

    eval {
        $select_rec_type_st->bind_param(1, $ioc_boot_id, SQL_INTEGER);
        $count=$select_rec_type_st->execute();
    };
    if ($@) {
        $dbh->rollback;
        log('error',"rec_type copy rows: $@");
        exit(-1);
    }
    log('debug',"rec_type_copy_to_history: $count rec_type selected.");

    # SQL for writing out a row of the rec_type_history table
    $insert_rec_type_history_sql = 'insert into rec_type_history ' .
      '(rec_type_history_id, ioc_boot_id, rec_type, ioc_resource_history_id) ' .
      'values (?, ?, ?, ?)';
    $insert_rec_type_history_st = $dbh->prepare($insert_rec_type_history_sql);

    my $rec_type_history_count=0;

    # Loop over rec_types ==========
    while ( $rec_type_row = $select_rec_type_st->fetchrow_hashref) {
        $rec_type_id = $rec_type_row->{rec_type_id};
        $ioc_resource_id= $rec_type_row->{ioc_resource_id};

        eval {  
            # bind and execute to write out rec_type_history row
            $rec_type_history_id = pre_insert_get_id($dbh, 'rec_type_history');
            $insert_rec_type_history_st->bind_param(1, $rec_type_history_id, SQL_INTEGER) if $rec_type_history_id;
            $insert_rec_type_history_st->bind_param(2, $ioc_boot_id , SQL_INTEGER);
            $insert_rec_type_history_st->bind_param(3, $rec_type_row->{rec_type}, SQL_VARCHAR);
            $insert_rec_type_history_st->bind_param(4, $ioc_resource_hash{$ioc_resource_id}, SQL_INTEGER);
            $rec_type_history_count += $insert_rec_type_history_st->execute();
        };
        if ($@) {
            $dbh->rollback;
            log('error',"ioc_resource_history uri-history write: $@");
            exit(-1);
        }
        $rec_type_history_id = post_insert_get_id($dbh, 'rec_type_history');
        $rec_type_hash{$rec_type_id}= $rec_type_history_id;
        log('verbose',"rec_type_copy_to_history: rec_type_id=$rec_type_id rec_type_history_id=$rec_type_history_id rec_type=$rec_type_row->{rec_type}");
    }

    log('info',"PVCrawlerDBHistoryLayer: Inserted $rec_type_history_count rec_type_history");

    return \%rec_type_hash;
}

# Private
sub fld_type_copy_to_history {
    my $dbh = $_[0];
    my $rec_type_hash_ref = $_[1];
    my %rec_type_hash = %$rec_type_hash_ref;
    my $ioc_boot_id = $_[2];
    my %fld_type_hash;
    my $count=0;
    my $fld_type_id;
    my $fld_type_row;
    my $rec_type_id;
    my $fld_type_history_id;

    log('verbose','fld_type_copy_to_history: Entered.');

    # SQL for selecting rows of the fld_type table
    my $fields  = 'fld_type_id,fld_type.rec_type_id,fld_type,dbd_type,def_fld_val';
    my $select_fld_type_sql = "select $fields from fld_type,rec_type where ".
       'fld_type.rec_type_id=rec_type.rec_type_id and rec_type.ioc_boot_id = ?';
    my $select_fld_type_st = $dbh->prepare($select_fld_type_sql);

    # ======  Get fld_type lines for a rec_type_id ==========
    eval {
        $select_fld_type_st->bind_param(1, $ioc_boot_id, SQL_INTEGER);
        $count=$select_fld_type_st->execute();

        log('verbose',"fld_type_copy_to_history: ioc_boot_id=$ioc_boot_id $count fld_type selected.");
    };
    if ($@) {
        log('Error',"fld_type_copy_to_history: fld_type select: $@");
        exit(-1);
    }
    
    # SQL for writing out a row of the fld_type_history table
    my $insert_fld_type_history_sql = 'insert into fld_type_history ' .
        '(fld_type_history_id, rec_type_history_id, fld_type, dbd_type, def_fld_val) ' .
        'values (?, ?, ?, ?, ?)';
    my $insert_fld_type_history_st = $dbh->prepare($insert_fld_type_history_sql);
    
    my $fld_type_history_count=0;

    # Loop over fld_type lines ==========
    while ($fld_type_row = $select_fld_type_st->fetchrow_hashref) {

        $fld_type_id = $fld_type_row->{fld_type_id};
        $fld_type_history_id = pre_insert_get_id($dbh, 'fld_type_history');

        eval {
            $insert_fld_type_history_st->bind_param(1, $fld_type_history_id, SQL_INTEGER) if $fld_type_history_id;
            $insert_fld_type_history_st->bind_param(2, $rec_type_hash{$fld_type_row->{rec_type_id}}, SQL_INTEGER);
            $insert_fld_type_history_st->bind_param(3, $fld_type_row->{fld_type}, SQL_VARCHAR);
            $insert_fld_type_history_st->bind_param(4, $fld_type_row->{dbd_type}, SQL_VARCHAR);
            $insert_fld_type_history_st->bind_param(5, $fld_type_row->{def_fld_val}, SQL_VARCHAR);
            $fld_type_history_count += $insert_fld_type_history_st->execute();
        };
        if ($@) {
            log('Error',"fld_type_copy_to_history: $@");
            exit(-1);
        }

        $fld_type_history_id= post_insert_get_id($dbh, 'fld_type_history');
        $fld_type_hash{$fld_type_id}= $fld_type_history_id;
        log('verbose',"fld_type_copy_to_history: fld_type_history_id=$fld_type_history_id".
             " rec_type_history_id=$rec_type_hash{$fld_type_row->{rec_type_id}}".
             " $fld_type_row->{fld_type} $fld_type_row->{dbd_type}".
             " $fld_type_row->{def_fld_val}.");

    }
    log('info',"PVCrawlerDBHistoryLayer: Inserted $fld_type_history_count fld_type_history");

    return \%fld_type_hash;
}



# Private
sub rec_and_rec_alias_copy_to_history {
    my $dbh = $_[0];
    my $ioc_boot_id = $_[1];
    my $rec_type_hash_ref = $_[2];
    my %rec_type_hash = %$rec_type_hash_ref;
    my $ioc_resource_hash_ref = $_[3];
    my %ioc_resource_hash = %$ioc_resource_hash_ref;
    my %rec_hash;
    my $count=0;
    my $rec_row;
    my $rec_id;
    my $rec_type_id;
    my $rec_type_history_id;
    my $rec_history_id;
    my $total=0;
    my $rec_alias_row;
    my $rec_alias_history_id;

    # ======  Get ioc_id ------------------------------------------------------------
    log('verbose',"rec_and_rec_alias_copy_to_history: ioc_boot_id=$ioc_boot_id.");

    # ======  SQL for selecting rec lines for an ioc_boot_id ==========
    my $fields  = 'rec_id,rec_nm,rec_type_id,rec_criticality';
    my $select_rec_sql = "select $fields from rec where ioc_boot_id = ?";
    my $select_rec_st = $dbh->prepare($select_rec_sql);

    eval {
        $select_rec_st->bind_param(1, $ioc_boot_id, SQL_INTEGER);
        $count=$select_rec_st->execute();
    };
    if ($@) {
        $dbh->rollback;
        log('error',"rec_and_rec_alias_copy_to_history:rec select: $@");
        exit(-1);
    }

    log('debug',"rec_and_rec_alias_copy_to_history: ioc_boot_id=$ioc_boot_id $count rec.");

    # SQL for writing out a row of the rec_history table
    my $insert_rec_history_sql = 'insert into rec_history ' .
        '(rec_history_id, ioc_boot_id, rec_nm, rec_type_history_id, rec_criticality) ' .
        'values (?, ?, ?, ?, ?)';
    my $insert_rec_history_st = $dbh->prepare($insert_rec_history_sql);

    # ======   SQL for selecting rec_alias lines for a rec ==========
    $fields  = 'rec_alias_id,alias_nm,ioc_resource_id';
    my $select_rec_alias_sql = "select $fields from rec_alias where rec_id = ?";
    my $select_rec_alias_st = $dbh->prepare($select_rec_alias_sql);

    # SQL for writing out a row of the rec_alias_history table
    my $insert_rec_alias_history_sql = 'insert into rec_alias_history ' .
        '(rec_alias_history_id, rec_history_id, alias_nm, ioc_resource_history_id) ' .
        'values (?, ?, ?, ?)';
    my $insert_rec_alias_history_st = $dbh->prepare($insert_rec_alias_history_sql);

    my $rec_history_count=0;
    my $rec_alias_history_count=0;
    my $rec_criticality;

    # Loop over recs ==========
    while ($rec_row = $select_rec_st->fetchrow_hashref) {
        $rec_id = $rec_row->{rec_id};
        $rec_type_id = $rec_row->{rec_type_id};
        $rec_type_history_id = $rec_type_hash{$rec_type_id} if $rec_type_id;
        $rec_criticality = $rec_row->{rec_criticality};
    
       # Create rec_history record for a given rec_id ==========
        eval {
            # Copy rec row to rec_history
            $rec_history_id = pre_insert_get_id($dbh, 'rec_history');
            $insert_rec_history_st->bind_param(1, $rec_history_id, SQL_INTEGER) if $rec_history_id;
            $insert_rec_history_st->bind_param(2, $ioc_boot_id, SQL_INTEGER);
            $insert_rec_history_st->bind_param(3, $rec_row->{rec_nm}, SQL_VARCHAR);
            $insert_rec_history_st->bind_param(4, $rec_type_history_id, SQL_INTEGER) if $rec_type_id;
            $insert_rec_history_st->bind_param(5, $rec_criticality, SQL_INTEGER);
            $rec_history_count += $insert_rec_history_st->execute();
        };
        if ($@) {
            $dbh->rollback;
            log('error',"rec_and_rec_alias_copy_to_history: rec copy: $@");
            exit(-1);
        }
        $rec_history_id = post_insert_get_id($dbh, 'rec_history');
        $rec_hash{$rec_id} = $rec_history_id;
        #log('verbose',"rec_and_rec_alias_copy_to_history: rec_type_id=$rec_type_id $rec_row->{rec_nm} rec_type_history_id=$rec_type_history_id.");

       # Select rec_alias for a given rec_id  ==========
        eval {
            $select_rec_alias_st->bind_param(1, $rec_id, SQL_INTEGER);
            $select_rec_alias_st->execute();
        };
        if ($@) {
            $dbh->rollback;
            log('error',"rec_and_rec_alias_copy_to_history:rec_alias select: $@");
            exit(-1);
        }

        $total=$total+$count;
        log('verbose',"rec_and_rec_alias_copy_to_history: rec_id=$rec_id $count rec_alias.");

        # Loop over rec_aliases ==========
        while ($rec_alias_row = $select_rec_alias_st->fetchrow_hashref) {

            # Create rec_alias_history record ==========
            eval {
                # Copy rec_alias row to rec_alias_history
                $rec_alias_history_id = pre_insert_get_id($dbh, 'rec_alias_history');
                $insert_rec_alias_history_st->bind_param(1, $rec_alias_history_id, SQL_INTEGER) if $rec_alias_history_id;
                $insert_rec_alias_history_st->bind_param(2, $rec_history_id, SQL_INTEGER);
                $insert_rec_alias_history_st->bind_param(3, $rec_alias_row->{alias_nm}, SQL_VARCHAR);
                $insert_rec_alias_history_st->bind_param(4, $ioc_resource_hash{$rec_alias_row->{ioc_resource_id}}, SQL_INTEGER);
                $rec_alias_history_count += $insert_rec_alias_history_st->execute();
            };
            if ($@) {
                $dbh->rollback;
                log('error',"rec_and_rec_alias_copy_to_history: rec_alias copy: $@");
                exit(-1);
            }
        }
    }
    log('info',"PVCrawlerDBHistoryLayer: Inserted ".
               "$rec_history_count rec_history ".
               "$rec_alias_history_count rec_alias_history ");

    log('debug',"rec_and_rec_alias_copy_to_history: $total rec_alias.");
    return \%rec_hash;
}


# Private
sub fld_copy_to_history {
    my $dbh = $_[0];
    my $rec_hash_ref = $_[1];
    my %rec_hash = %$rec_hash_ref;
    my $fld_type_hash_ref = $_[2];
    my %fld_type_hash = %$fld_type_hash_ref;
    my $ioc_resource_hash_ref = $_[3];
    my %ioc_resource_hash = %$ioc_resource_hash_ref;
    my $ioc_boot_id = $_[4];
    my $count=0;
    my $ioc_resource_id;
    my $fld_row;
    my $fld_history_id;

    log('verbose','fld_copy_to_history: Entered.');

    # ======  SQL for selecting fld lines for an ioc_boot_id ==========
    my $fields  = 'fld_id,fld.rec_id,fld_type_id,fld_val,fld.ioc_resource_id';
    my $select_fld_sql = "select $fields from fld inner join rec where ".
                         'rec.ioc_boot_id=? '.
                         'and fld.rec_id=rec.rec_id ';
    #my $select_fld_sql = "select $fields from fld,rec where ".
    #                     'rec.ioc_boot_id=?  and fld.rec_id=rec.rec_id ';
    my $select_fld_st = $dbh->prepare($select_fld_sql);
    eval {
        $select_fld_st->bind_param(1, $ioc_boot_id, SQL_INTEGER);
        $count=$select_fld_st->execute();
    };
    if ($@) {
        $dbh->rollback;
        log('error',"fld select: $@");
        exit(-1);
    }
    log('verbose',"fld_copy_to_history: ioc_boot_id=$ioc_boot_id $count fld .");
    

    # SQL for writing out a row of the fld_history table
    my $insert_fld_history_sql = 'insert into fld_history ' .
         '(fld_history_id, rec_history_id,fld_type_history_id,fld_val,ioc_resource_history_id) ' .
         'values (?, ?, ?, ?, ?)';
    my $insert_fld_history_st = $dbh->prepare($insert_fld_history_sql);

    my $fld_history_count=0;
     
    # Loop over fld lines ==========
    while ($fld_row = $select_fld_st->fetchrow_hashref) {
 
        eval {
            $fld_history_id = pre_insert_get_id($dbh, 'fld_history');
            $insert_fld_history_st->bind_param(1, $fld_history_id,  SQL_INTEGER) if $fld_history_id;
            $insert_fld_history_st->bind_param(2, $rec_hash{$fld_row->{rec_id}},  SQL_INTEGER);
            $insert_fld_history_st->bind_param(3, $fld_type_hash{$fld_row->{fld_type_id}},  SQL_INTEGER);
            $insert_fld_history_st->bind_param(4, $fld_row->{fld_val}, SQL_VARCHAR);
            $insert_fld_history_st->bind_param(5, $ioc_resource_hash{$fld_row->{ioc_resource_id}}, SQL_INTEGER);
            $fld_history_count += $insert_fld_history_st->execute();
        };
        if ($@) {
            $dbh->rollback;
            log('error',"fld_history copy: $@");
            exit(-1);
        }
 
    }
    log('info',"PVCrawlerDBHistoryLayer: Inserted $fld_history_count fld_history ");

    log('debug',"fld_copy_to_history: $count fld.");
    return;
}

#####################################################
#   my $outputfile = "fld_values";
#   open OUT, "> $outputfile"  or die "Cannot create $outputfile";

#   # Loop over fld lines ==========
#   while ($fld_row = $select_fld_st->fetchrow_hashref) {
#       my $rec_history_id = $rec_hash{$fld_row->{rec_id}};
#       my $fld_type_history_id = $fld_type_hash{$fld_row->{fld_type_id}};
#       my $ioc_resource_history_id = $ioc_resource_hash{$fld_row->{ioc_resource_id}};

#       $rec_history_id = '\\N' if ! $rec_history_id ;
#       $fld_type_history_id = '\\N' if ! $fld_type_history_id ;
#       $ioc_resource_history_id = '\\N' if ! $ioc_resource_history_id ;

#       print OUT "\\N,$rec_history_id,$fld_type_history_id,".
#            "'$fld_row->{fld_val}',$ioc_resource_history_id\n";
#   }
#   eval {
#       my $load_data_local_infile_sql = "LOAD DATA LOCAL INFILE \"$outputfile\" ".
#                "INTO TABLE fld_history ".
#                "FIELDS TERMINATED BY \",\" OPTIONALLY ENCLOSED BY  \"'\"";
#       my $load_data_local_infile_st = $dbh->prepare($load_data_local_infile_sql);
#       $load_data_local_infile_st->execute();
#   };
#   if ($@) {
#       $dbh->rollback;
#       log('error',"fld_history copy: $@");
#       exit(-1);
#   }
#   unlink $outputfile;
#####################################################
##$dbh->do('ALTER TABLE fld_history DISABLE KEYS');
##$dbh->do(' ALTER TABLE fld_history ENABLE KEYS');
#####################################################


# Private
sub rec_type_dev_sup_copy_to_history {
    my $dbh = $_[0];
    my $rec_type_hash_ref = $_[1];
    my %rec_type_hash = %$rec_type_hash_ref;
    my %rec_type_dev_sup_hash;
    my $count=0;
    my $total=0;
    my $rec_type_dev_sup_row;
    my $rec_type_dev_sup_id;
    my $rec_type_dev_sup_history_id;
    my $rec_type_id;

    log('verbose','rec_type_dev_sup_copy_to_history: Entered.');

    my $fields  = 'rec_type_dev_sup_id,dtyp_str,dev_sup_dset,dev_sup_io_type';
    my $select_rec_type_dev_sup_sql = "select $fields from rec_type_dev_sup where rec_type_id = ?";
    my $select_rec_type_dev_sup_st = $dbh->prepare($select_rec_type_dev_sup_sql);

    # SQL for writing out a row of the rec_type_dev_sup_history table
    my $insert_rec_type_dev_sup_history_sql = 'insert into rec_type_dev_sup_history ' .
        '(rec_type_dev_sup_history_id, rec_type_history_id, dtyp_str, dev_sup_dset, dev_sup_io_type) ' .
        'values (?, ?, ?, ?, ?)';
    my $insert_rec_type_dev_sup_history_st = $dbh->prepare($insert_rec_type_dev_sup_history_sql);

    my $rec_type_dev_sup_history_count=0;
    
    for $rec_type_id (keys %rec_type_hash) {

        # ======  Get rec_type_dev_sup lines for a rec_type_id ==========
        eval {
            $select_rec_type_dev_sup_st->bind_param(1, $rec_type_id, SQL_INTEGER);
            $count=$select_rec_type_dev_sup_st->execute();
        };
        if ($@) {
            log('Error',"rec_type_dev_sup_copy_to_history: $@");
            exit(-1);
        }
        log('verbose',"rec_type_dev_sup_copy_to_history: rec_type_id=$rec_type_id $count rec_type_sev_sup rec_type_dev_sup.");
        $total=$total+$count;
    
        # Loop over rec_type_dev_sup lines ==========
        while ($rec_type_dev_sup_row = $select_rec_type_dev_sup_st->fetchrow_hashref) {
            $rec_type_dev_sup_id = $rec_type_dev_sup_row->{rec_type_dev_sup_id};
    
            eval {
                $rec_type_dev_sup_history_id = pre_insert_get_id($dbh, 'rec_type_dev_sup_history');
                $insert_rec_type_dev_sup_history_st->bind_param(1, $rec_type_dev_sup_history_id, SQL_INTEGER) if $rec_type_dev_sup_history_id;
                $insert_rec_type_dev_sup_history_st->bind_param(2, $rec_type_hash{$rec_type_id}, SQL_INTEGER);
                $insert_rec_type_dev_sup_history_st->bind_param(3, $rec_type_dev_sup_row->{dtyp_str}, SQL_VARCHAR);
                $insert_rec_type_dev_sup_history_st->bind_param(4, $rec_type_dev_sup_row->{dev_sup_dset}, SQL_VARCHAR);
                $insert_rec_type_dev_sup_history_st->bind_param(5, $rec_type_dev_sup_row->{dev_sup_io_type}, SQL_VARCHAR);
                $rec_type_dev_sup_history_count += $insert_rec_type_dev_sup_history_st->execute();

            };
            if ($@) {
                log('Error',"rec_type_dev_sup_copy_to_history: $@");
                exit(-1);
            }
            $rec_type_dev_sup_history_id= post_insert_get_id($dbh, 'rec_type_dev_sup_history');
            $rec_type_dev_sup_hash{$rec_type_dev_sup_id}= $rec_type_dev_sup_history_id;
            #log('verbose',"rec_type_dev_sup_copy_to_history: rec_type_dev_sup_history_id=$rec_type_dev_sup_history_id rec_type_history_id=$rec_type_hash{$rec_type_id} $rec_type_dev_sup_row->{dtyp_str} $rec_type_dev_sup_row->{dev_sup_dset} $rec_type_dev_sup_row->{dev_sup_io_type}.");
        }
    }

    log('info',"PVCrawlerDBHistoryLayer: Inserted ".
               "$rec_type_dev_sup_history_count rec_type_dev_sup_history ");

    log('debug',"rec_type_dev_sup_copy_to_history: $total rec_type_dev_sup.");
    return \%rec_type_dev_sup_hash;
}


# Private
sub delete_rows {
    my $dbh = $_[0];
    my $ioc_boot_id = $_[1];
    my $tables = $_[2];
    my $delete_sql = $_[3];
    my $count=0;

    my $delete_st = $dbh->prepare($delete_sql);

    eval {
        $delete_st->bind_param(1, $ioc_boot_id, SQL_INTEGER);
        $count=$delete_st->execute();
    };
    if ($@) {
        $dbh->rollback;
        log('error',"$tables delete_rows: $@");
        exit(-1);
    }

    log('info',"PVCrawlerDBHistoryLayer: delete_rows: $count $tables deleted.");

    return $count;
}

# Very odd perl autoloading ...
sub AUTOLOAD {
    our $AUTOLOAD;

    # Get the core function being called
    my $call_signature;
    if ($AUTOLOAD =~ /.*::(.*)/) {
        $call_signature = $1;
    } else {
        $call_signature = $AUTOLOAD;
    }

    if ($call_signature ne 'pre_insert_get_id' &&
        $call_signature ne 'post_insert_get_id' &&
        $call_signature ne 'get_dbi_dsn' &&
        $call_signature ne 'get_null_timestamp' &&
        $call_signature ne 'get_current_date_time' &&
        $call_signature ne 'initialize_session' &&
        $call_signature ne 'get_format_date_sql') {
        warn "Attempt to call $call_signature, which is not defined!\n";
        return;
    }

    # Determine which vendor DBFunctions.pm module we need to load
    my $home_dir=get_home_dir();
    my $db_vendor=get_db_vendor();
    my $db_functions_module = $db_vendor . "DBFunctions";
    require "$home_dir/$db_functions_module.pm";

    my $subroutine = "$db_functions_module"."::".$call_signature;
    goto &$subroutine;
}

1;
