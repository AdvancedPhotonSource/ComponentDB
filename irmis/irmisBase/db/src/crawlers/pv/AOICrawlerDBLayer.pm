
package AOICrawlerDBLayer;

# This package provides subroutines for writing out aoi crawler data to
# a relational database. Vendor specific code is factored out into
# perl modules XXXDBFunctions.pm, so this module should remain
# vendor agnostic.

use strict;
use DBI qw(:sql_types);
require Exporter;
use POSIX;
use File::Basename; 

use LogUtil qw(log);
use CommonUtil qw(db_connect get_test_mode ioc_load_by_name mail);
use PVCrawlerDBLayer;

our @ISA = qw(Exporter);
our @EXPORT_OK = qw(update_db_tables_aoi);
our $VERSION = 1.00;

my $dbh;
my %aoi_hash;
my %plc_hash;
my %plc_version_pv_hash;


#***********
# Public sub
#***********
sub update_db_tables_aoi {
    #
    # arguments to subroutine
    #

    # ioc boot info
    my $ioc_boot_info = $_[0];

    # results of st.cmd file parse
    my $parse_err = $_[1];

    if (get_test_mode()) {
        log('info',"AOICrawlerDBLayer:update_db_tables_aoi: Test Mode. No database updates.");
        return;
    }

    # flag indicating whether an ioc resource has changed
    my $ioc_resource_changed = $_[2];

    # array ref to stcmd_line_list
    my $stcmd_line_list = $_[3];
    
    log('debug',"AOICrawlerDBLayer: update aoi tables: Started.");

    %aoi_hash=();
    %plc_hash=();
    %plc_version_pv_hash=();

    if (!$dbh) {
        $dbh = db_connect();
    }

    my $ioc_nm = $ioc_boot_info->{ioc_nm};
    my $ioc_boot_id = $ioc_boot_info->{ioc_boot_id};

    # given ioc_nm, get ioc_id
    my $ioc_id = ioc_load_by_name($ioc_nm);

    my $remove_err = remove_old_aoi_records($dbh,$ioc_id);
    if ($remove_err) {
        log('error',"Remove records error for ioc $ioc_nm.");
        return;
    }

    if (get_test_mode()) {
        $dbh->rollback;
        log('info',"AOICrawlerDBLayer: update_db_tables: TEST MODE; Rollback AOI removes.");
    } else {
        log('debug',"AOICrawlerDBLayer: update_db_tables: Commiting AOI removes");
        $dbh->commit;
    }

    # if parse errors detected, do not add new pv data and st.cmd lines to aoi database
    if ($parse_err) {
        log('error',"AOI parse error detected. NO new AOI records written to database.");
        return;
    } else {
        add_new_aoi_records($dbh,$stcmd_line_list,$ioc_id,$ioc_boot_id);

        if (get_test_mode()) {
            $dbh->rollback;
            log('info',"AOICrawlerDBLayer: update_db_tables: TEST MODE; Rollback AOI adds.");
        } else {
            log('debug',"AOICrawlerDBLayer: update_db_tables: Commiting AOI adds");
            $dbh->commit;
        }
    }

    log('debug',"AOICrawlerDBLayer: update_db_tables: Finished.");
    return;
}

#***********
# Private
#***********
# Load a single aoi record given the aoi name. Returns aoi_id.
sub aoi_load_by_name {
    my $aoi_name = $_[0];

    my $aoi_id = 0;

    return $aoi_hash{$aoi_name}if $aoi_hash{$aoi_name};

    log('debug',"AOICrawlerDBLayer: begin aoi_load_by_name -> $aoi_name");

    if (!$dbh) {
        $dbh = db_connect();
    }

    my $select_aoi_sql = 'select aoi_id from aoi where aoi_name = ?';
    my $select_aoi_st;    
    eval {
        $select_aoi_st = $dbh->prepare($select_aoi_sql);
        $select_aoi_st->bind_param(1,$aoi_name,SQL_VARCHAR);
        $select_aoi_st->execute();
    };
    if ($@) {
        log('error',"aoi_load_by_name():$@");
        exit;
    }

    if (my $aoi_row = $select_aoi_st->fetchrow_hashref) {
        $aoi_id = $aoi_row->{aoi_id};
    } else { 
        log('error',"AOICrawlerDBLayer: invalid aoi name: $aoi_name");
        mail("AOI error: aoi name: $aoi_name not in database.");
        $aoi_id = -1;
    }
    $aoi_hash{$aoi_name} = $aoi_id; 

    return $aoi_id;
}

#***********
# Private
#***********
# Update the plc_version_pv_name in a single plc record given the plc_id.
sub update_plc_version_pv_name {
    my $plc_id = $_[0];
    my $plc_version_pv_name = $_[1];
    my $count=0;

    return if $plc_version_pv_hash{$plc_id} && ($plc_version_pv_hash{$plc_id} eq $plc_version_pv_name);

    log('debug',"AOICrawlerDBLayer: begin update plc_version_pv_name -> $plc_id");

    if (!$dbh) {
        $dbh = db_connect();
    }

    my $update_plc_sql = 'update plc set plc_version_pv_name=? where plc_id=?';
    my $update_plc_st;    
    eval {
        $update_plc_st = $dbh->prepare($update_plc_sql);
        $update_plc_st->bind_param(1,$plc_version_pv_name,SQL_VARCHAR);
        $update_plc_st->bind_param(2,$plc_id,SQL_INTEGER);
        $count=$update_plc_st->execute();
    };
    if ($@) {
        log('error',"update_plc_version_pv_name():$@");
        exit;
    }
    if ($count < 1) {
        log('error',"update_plc_version_pv_name():No records updated.");
        mail("AOI error: plc_version_pv_name $plc_version_pv_name not updated. plc may not be in database.");
    } else {
        $plc_version_pv_hash{$plc_id} = $plc_version_pv_name; 
    }
}


#***********
# Private
#***********
# Load a single plc record given the plc name. Returns plc_id.
sub plc_load_by_name {
    my $plc_name = $_[0];

    my $plc_id = 0;

    return $plc_hash{$plc_name}if $plc_hash{$plc_name};

    log('debug',"AOICrawlerDBLayer: begin plc_load_by_name -> $plc_name");

    if (!$dbh) {
        $dbh = db_connect();
    }

    my $select_plc_sql = 'select plc_id from plc, component where plc.component_id = component.component_id and component.component_instance_name = ?';
    my $select_plc_st;    
    eval {
        $select_plc_st = $dbh->prepare($select_plc_sql);
        $select_plc_st->bind_param(1,$plc_name,SQL_VARCHAR);
        $select_plc_st->execute();
    };
    if ($@) {
        log('error',"plc_load_by_name():$@");
        exit;
    }

    if (my $plc_row = $select_plc_st->fetchrow_hashref) {
        $plc_id = $plc_row->{plc_id};
    } else {
        log('error',"AOICrawlerDBLayer: invalid plc name: $plc_name");
        mail("PLC error: invalid plc name: $plc_name");
        $plc_id = -1;
    }
    $plc_hash{$plc_name} = $plc_id; 

    return $plc_id;
}

#***********
# Private
#***********
sub remove_old_aoi_records {
    my $dbh = $_[0];
    my $ioc_id = $_[1];

    log('debug',"AOICrawlerDBLayer: remove_old_aoi_records: Started ioc_id=$ioc_id.");

    # need to remove records from  aoi tables: aoi_epics_record, 
    # aoi_ioc_stcmd_line, aoi_plc_stcmd_line, and ioc_stcmd_line
    
    # will need the aoi_id for each aoi name encountered by the
    # aoi crawler
    
    # each aoi epics record has as a foreign key the aoi_ioc_stcmd_line_id
    
    my $select_sql = 'select ioc_stcmd_line_id from ioc_stcmd_line where ioc_id=?';
    my $select_st;
    eval {
        $select_st = $dbh->prepare($select_sql);
        $select_st->execute("$ioc_id");
    };
    if ($@) {
        log('error',"AOICrawlerDBLayer: remove_old_aoi_records: select from ioc_stcmd_line: $@");
        return 1;
    }

    my $aoi_epics_record_deleted = 0;
    my $ioc_stcmd_line_deleted = 0;
    my $aoi_plc_stcmd_line_deleted = 0;
    my $aoi_ioc_stcmd_line_deleted = 0;
    my $aoi_crawler_deleted = 0;
    my $ioc_row;
    my $delete_sql;
    my $delete_st;
    my $ioc_stcmd_line_id;
    my $rows_deleted;
    while ($ioc_row = $select_st->fetchrow_hashref) {
        $ioc_stcmd_line_id = $ioc_row->{ioc_stcmd_line_id};
        log('verbose',"AOICrawlerDBLayer: remove_old_aoi_records: ioc_stcmd_line_id=$ioc_stcmd_line_id");
        

	# take care of table aoi_epics_record
	
	my $select_aoi_sql = 'select aoi_ioc_stcmd_line_id from aoi_ioc_stcmd_line where ioc_stcmd_line_id=?';
	my $select_aoi_st;
	eval{
	    $select_aoi_st = $dbh->prepare($select_aoi_sql);
	    $select_aoi_st->execute("$ioc_stcmd_line_id");
	};
	
	if ($@){
	    log('error',"AOICrawlerDBLayer: remove_old_aoi_records: select from aoi_ioc_stcmd_line: $@");
	    return 1;
	}
	
	my $aoi_ioc_row;
	my $aoi_ioc_stcmd_line_id;
	while ($aoi_ioc_row = $select_aoi_st->fetchrow_hashref){
	
	    $aoi_ioc_stcmd_line_id = $aoi_ioc_row->{aoi_ioc_stcmd_line_id};
	    
	    log('verbose',"AOICrawlerDBLayer: remove_old_aoi_records: aoi_ioc_stcmd_line_id = $aoi_ioc_stcmd_line_id");
	    
            $delete_sql = 'delete from aoi_epics_record where aoi_ioc_stcmd_line_id='."\'$aoi_ioc_stcmd_line_id\'";
            $rows_deleted = 0;
            eval {
	        $delete_st = $dbh->prepare($delete_sql);
	        $rows_deleted += $delete_st->execute();
            };
        
            if ($@) {
                log('error',"AOICrawlerDBLayer: remove_old_aoi_records: aoi_epics_record: $@");
                return 1;
            }
            log('debug',"AOICrawlerDBLayer: remove_old_aoi_records: aoi_epics_record rows_deleted=$rows_deleted");
            $aoi_epics_record_deleted += $rows_deleted;
        
        }
        

        # take care of table aoi_ioc_stcmd_line
        
        $rows_deleted = 0;
        $delete_sql = 'delete from aoi_ioc_stcmd_line where ioc_stcmd_line_id='."$ioc_stcmd_line_id";
        eval {
	    $delete_st = $dbh->prepare($delete_sql);
	    $rows_deleted = $delete_st->execute();
        };
        if ($@) {
            log('error',"AOICrawlerDBLayer: remove_old_aoi_records: aoi_ioc_stcmd_line: $@");
            return 1;
        }
        log('debug',"AOICrawlerDBLayer: remove_old_aoi_records: aoi_ioc_stcmd_line rows_deleted=$rows_deleted");
        $aoi_ioc_stcmd_line_deleted += $rows_deleted;
        
        
        # take care of table aoi_plc_stcmd_line
        
        $rows_deleted = 0;
        $delete_sql = 'delete from aoi_plc_stcmd_line where ioc_stcmd_line_id='."$ioc_stcmd_line_id";
	eval {
	    $delete_st = $dbh->prepare($delete_sql);
	    $rows_deleted = $delete_st->execute();
	};
	if ($@) {
	    log('error',"AOICrawlerDBLayer: remove_old_aoi_records:  aoi_plc_stcmd_line: $@");
	    return 1;
	}
        log('debug',"AOICrawlerDBLayer: remove_old_aoi_records: aoi_plc_stcmd_line rows_deleted=$rows_deleted");
        $aoi_plc_stcmd_line_deleted += $rows_deleted;
        
        
	# take care of table ioc_stcmd_line

	$rows_deleted = 0;
        $delete_sql = 'delete from ioc_stcmd_line where ioc_stcmd_line_id='."$ioc_stcmd_line_id";
        eval {
	    $delete_st = $dbh->prepare($delete_sql);
	    $rows_deleted = $delete_st->execute();
        };
        if ($@) {
            log('error',"AOICrawlerDBLayer: remove_old_aoi_records: ioc_stcmd_line: $@");
            return 1;
        }
        log('debug',"AOICrawlerDBLayer: remove_old_aoi_records: ioc_stcmd_line rows_deleted=$rows_deleted");
        $ioc_stcmd_line_deleted += $rows_deleted;

    }

    $rows_deleted = 0;
    $delete_sql = 'delete from aoi_crawler where ioc_id='."$ioc_id";
    eval {
	$delete_st = $dbh->prepare($delete_sql);
	$rows_deleted = $delete_st->execute();
    };
    if ($@) {
        log('error',"AOICrawlerDBLayer: remove_old_aoi_records: aoi_crawler: $@");
        return 1;
    }
    log('debug',"AOICrawlerDBLayer: remove_old_aoi_records: aoi_crawler rows_deleted = $rows_deleted");
    $aoi_crawler_deleted += $rows_deleted;

    log('info',"AOICrawlerDBLayer: Removed ".
       "$aoi_epics_record_deleted aoi_epics_record, ".
       "$ioc_stcmd_line_deleted ioc_stcmd_line, ".
       "$aoi_plc_stcmd_line_deleted aoi_plc_stcmd_line, ".
       "$aoi_ioc_stcmd_line_deleted aoi_ioc_stcmd_line, ".
       "$aoi_crawler_deleted aoi_crawler ");

    log('debug',"AOICrawlerDBLayer: remove_old_aoi_records: Finished ioc_id=$ioc_id.");
    return 0;
}

#***********
# Private
#***********
sub add_new_aoi_records {
    my $dbh = $_[0];
    my $stcmd_line_list = $_[1];
    my $ioc_id = $_[2];
    my $ioc_boot_id = $_[3];

    # need to populate aoi tables: aoi_epics_record, 
    # aoi_ioc_stcmd_line, and ioc_stcmd_line
    
    # will need the aoi_id for each aoi name encountered by the
    # aoi crawler
    
    # each epics record has as a foreign key aoi_ioc_stcmd_line_id
    
    log('debug',"AOICrawlerDBLayer: add_new_aoi_records: Started.");

    eval {
        # SQL for writing out a row of the aoi_epics_record table
        my $insert_epics_record_sql = 'insert into aoi_epics_record ' .
            '(aoi_epics_record_id, aoi_ioc_stcmd_line_id, rec_nm) ' .
            'values (?, ?, ?)';
        my $insert_epics_record_st = $dbh->prepare($insert_epics_record_sql);    
        

        # SQL for writing out a row of the ioc_stcmd_line table
        my $insert_ioc_stcmd_line_sql = 'insert into ioc_stcmd_line ' .
           '(ioc_stcmd_line_id, ioc_stcmd_line,  table_modified_date, table_modified_by, ioc_id,'.
           'ioc_stcmd_line_number, include_line_number )'.
           'values (?, ?, '.get_current_date_time().', \'aoicrawler\', ?, ?, ?)';
        my $insert_ioc_stcmd_line_st = $dbh->prepare($insert_ioc_stcmd_line_sql);    
        

        # SQL for writing out a row of the aoi_ioc_stcmd_line table
        my $insert_aoi_ioc_stcmd_line_sql = 'insert into aoi_ioc_stcmd_line ' .
                '(aoi_ioc_stcmd_line_id, aoi_id, ioc_stcmd_line_id, pv_filter) ' .
                'values (?, ?, ?, ?)';
        my $insert_aoi_ioc_stcmd_line_st = $dbh->prepare($insert_aoi_ioc_stcmd_line_sql);


        # SQL for writing out a row of the aoi_crawler table
        my $insert_aoi_crawler_sql = 'insert into aoi_crawler ' .
                '(aoi_crawler_id, ioc_id, ioc_boot_id) ' .
                'values (?, ?, ?)';            
        my $insert_aoi_crawler_st = $dbh->prepare($insert_aoi_crawler_sql); 
        
        # SQL for writing out a row of the aoi_plc_stcmd_line table
        my $insert_aoi_plc_stcmd_line_sql = 'insert into aoi_plc_stcmd_line ' .
           '(aoi_plc_stcmd_line_id, aoi_id, plc_id, ioc_stcmd_line_id) ' .
           'values (?, ?, ?,?)';
        my $insert_aoi_plc_stcmd_line_st = $dbh->prepare($insert_aoi_plc_stcmd_line_sql);    
            

        log('debug',"add_new_aoi_records: sql for table aoi_ioc_stcmd_line :  $insert_aoi_ioc_stcmd_line_sql");
        log('debug',"add_new_aoi_records: sql for table epics_record : $insert_epics_record_sql");
        log('debug',"add_new_aoi_records: sql for table ioc_stcmd_line : $insert_ioc_stcmd_line_sql");
        
        my $last_ioc_stcmd_line_id = 0;
        my $last_aoi_plc_id = 0;
        my $last_aoi_stcmd_line_id = 0;
        my $aoi_id = 0;
        my $last_epics_record_id = 0;
        
        my $aoi_ioc_stcmd_line = 0;
        my $aoi_plc_stcmd_line = 0; 
        my $aoi_epics_record = 0;
        my $ioc_stcmd_line = 0;
        my $aoi_crawler = 0;

        # loop through $stcmd_line_list to fill the ioc_stcmd_line table
        for my $stcmd_line (@$stcmd_line_list) {

            my $stcmd_line_index = $stcmd_line->{stcmd_line_index};
            my $stcmd_line_text = $stcmd_line->{stcmd_line};

            $last_ioc_stcmd_line_id = pre_insert_get_id($dbh, "ioc_stcmd_line");
            if ($last_ioc_stcmd_line_id) { $insert_ioc_stcmd_line_st->bind_param(1,$last_ioc_stcmd_line_id, SQL_INTEGER); }
            $insert_ioc_stcmd_line_st->bind_param(2,$stcmd_line_text, SQL_VARCHAR);
            $insert_ioc_stcmd_line_st->bind_param(3,$ioc_id, SQL_INTEGER);
            $insert_ioc_stcmd_line_st->bind_param(4,$stcmd_line_index, SQL_INTEGER);

            if ( $stcmd_line->{stcmd_include_line_index} ne '') {
                my $stcmd_include_line_index = $stcmd_line->{stcmd_include_line_index};
                $insert_ioc_stcmd_line_st->bind_param(5,$stcmd_include_line_index, SQL_INTEGER);
            } else {
                $insert_ioc_stcmd_line_st->bind_param(5,undef, SQL_INTEGER);
            }
            log('verbose',"add_new_aoi_records: Inserting aoi stcmd line: $stcmd_line_text");
            $insert_ioc_stcmd_line_st->execute();
            $ioc_stcmd_line++;
            $last_ioc_stcmd_line_id = post_insert_get_id($dbh, 'ioc_stcmd_line');
            
            my $aois_ref = $stcmd_line->{aoi_list};
            my $aoi_pv_filter = "";
            my @filters = ();
            my $write_record = 0;
            
            # loop through the $aoi_list to populate the table aoi_ioc_stcmd_line
            for my $aoi_name (@$aois_ref) {
                $aoi_id = aoi_load_by_name($aoi_name);
                next if $aoi_id == -1 ;
                log('verbose',"add_new_aoi_records: aoi list contains: $aoi_name");
                
                # get PV filter(s) for the specific aoi in this loop
                # clean up re-used loop variables
                undef(@filters);
                my $pv_filters_ref = $stcmd_line->{pv_filter_hash};
                $aoi_pv_filter = "";
                $write_record = 0;
                
                #$aoi_pv_filter is a string of zero or more filters to be used on record name

                # check if have an undefined PV filter
                ## if (exists $pv_filters_ref{$aoi_name}) {
                ## }
                
                while ( my ($key, $value) = each %$pv_filters_ref ) {
                
                    if ($aoi_name eq $key) {
                    	log('verbose',"add_new_aoi_records: $aoi_name has pv filter: $value");
                    
                    	$aoi_pv_filter = $value;
                    	# create array list of filter strings for this aoi
                    	# assume comma separated and possible spaces in the filter string provided by user
                    
                    	@filters = split /,\s*/, $aoi_pv_filter;
                    
                    	for my $rec_filter (@filters){
                    		log('verbose',"add_new_aoi_records: aoi $aoi_name filter field: $rec_filter");
                    	}
                    }
                }
                
                $last_aoi_stcmd_line_id = 0;
                $last_aoi_stcmd_line_id = pre_insert_get_id($dbh, "aoi_ioc_stcmd_line");
        
                if ($last_aoi_stcmd_line_id) { $insert_aoi_ioc_stcmd_line_st->bind_param(1, $last_aoi_stcmd_line_id, SQL_INTEGER); }
                $insert_aoi_ioc_stcmd_line_st->bind_param(2,$aoi_id, SQL_INTEGER);
                $insert_aoi_ioc_stcmd_line_st->bind_param(3,$last_ioc_stcmd_line_id, SQL_INTEGER);
		$insert_aoi_ioc_stcmd_line_st->bind_param(4,$aoi_pv_filter, SQL_VARCHAR);

                # log('verbose',"add_new_aoi_records: insert into table aoi_ioc_stcmd_line, last_ioc_stcmd_line_id  $last_ioc_stcmd_line_id and last_aoi_stcmd_line_id $last_aoi_stcmd_line_id");
                
                $insert_aoi_ioc_stcmd_line_st->execute();
                
                $last_aoi_stcmd_line_id = post_insert_get_id($dbh, 'aoi_ioc_stcmd_line');
                
                log('verbose',"add_new_aoi_records: from post insert, last_aoi_stcmd_line_id = $last_aoi_stcmd_line_id");
            
                $aoi_ioc_stcmd_line++;
                
                # write data to table aoi_epics_record
                # loop through the $record_list
                my $records_ref = $stcmd_line->{record_list};
                my $count = 0;
                for my $rec_nm (@$records_ref) {
                    # check if user provided a filter for PV record names
                    if($aoi_pv_filter ne "") {
                        # match filters to epics record name
                        for my $rec_filter(@filters){
                            if($rec_nm =~ /($rec_filter)/) {
                                # flag write this record name to table
                                log('verbose',"add_new_aoi_records: matched PV name $rec_nm with filter $rec_filter");
                                $write_record = 1;
                            }
                        } 
                    } else {
                        # user did not provide a filter for epics record names
                        # flag write record name to table
                        $write_record = 1;
                    }
                    
                    if($write_record) {
                        $last_epics_record_id = pre_insert_get_id($dbh, "aoi_epics_record");
                        if ($last_epics_record_id) { $insert_epics_record_st->bind_param(1, $last_epics_record_id, SQL_INTEGER); }
                        $insert_epics_record_st->bind_param(2, $last_aoi_stcmd_line_id, SQL_INTEGER);
                        $insert_epics_record_st->bind_param(3, $rec_nm, SQL_VARCHAR);
                        log('verbose',"add_new_aoi_records: insert aoi_epics_record: $rec_nm");
                        $insert_epics_record_st->execute();
                        $aoi_epics_record++;
                        $count++;
                    } 
                    $write_record = 0;
                    log('verbose',"add_new_aoi_records: insert $count aoi_epics_record") if $count > 0;
                }  # end for records_ref
                
                # loop through the aoi stcmd_line plc_list to populate the aoi_plc_stcmd_line table
                log('verbose',"add_new_aoi_records: loop through the plc_list...");
                my $plcs_ref = $stcmd_line->{plc_list};
                for my $plc_info (@$plcs_ref) {
                    my @plc = @$plc_info;
		    my $plc_id = plc_load_by_name($plc[0]);
                    next if $plc_id == -1;
                    update_plc_version_pv_name($plc_id,$plc[1]) if defined $plc[1];
                    
                    my $last_aoi_plc_stcmd_line_id = pre_insert_get_id($dbh, "aoi_plc_stcmd_line");
                    if ($last_aoi_plc_stcmd_line_id) {
                        $insert_aoi_plc_stcmd_line_st->bind_param(1,$last_aoi_plc_stcmd_line_id, SQL_INTEGER);
                    }
                    $insert_aoi_plc_stcmd_line_st->bind_param(2,$aoi_id, SQL_INTEGER);
                    $insert_aoi_plc_stcmd_line_st->bind_param(3,$plc_id, SQL_INTEGER);
                    $insert_aoi_plc_stcmd_line_st->bind_param(4,$last_ioc_stcmd_line_id, SQL_INTEGER);
                    $insert_aoi_plc_stcmd_line_st->execute();
                    log('verbose',"add_new_aoi_records: insert aoi_plc_stcmd_line, aoi id: $aoi_id and plc id:  $plc_id");
                    $aoi_plc_stcmd_line++;
                } # end for plc_name
                
                $count = scalar(@$plcs_ref);
                log('verbose',"add_new_aoi_records: total plcs inserted  $count in table aoi_plc_stcmd_line") if $count > 0;
                undef(@$plcs_ref);
            }
            #$count = scalar(@$aois_ref);

        } # end for $stcmd_line loop
        
        #$ioc_stcmd_line = scalar(@$stcmd_line_list);

        # insert a record into aoi_crawler 
        my $last_aoi_crawler_id = pre_insert_get_id($dbh, "aoi_crawler");
        if ($last_aoi_crawler_id) { $insert_aoi_crawler_st->bind_param(1,$last_aoi_crawler_id, SQL_INTEGER); }
        $insert_aoi_crawler_st->bind_param(2,$ioc_id, SQL_INTEGER);
        $insert_aoi_crawler_st->bind_param(3,$ioc_boot_id, SQL_INTEGER);
        $insert_aoi_crawler_st->execute();
        $aoi_crawler++;

        log('info',"AOICrawlerDBLayer: Inserted ".
           "$aoi_epics_record aoi_epics_record, ".
           "$ioc_stcmd_line ioc_stcmd_line, ".
           "$aoi_plc_stcmd_line aoi_plc_stcmd_line, ".
           "$aoi_ioc_stcmd_line aoi_ioc_stcmd_line, ".
           "$aoi_crawler aoi_crawler ");

    };  # end eval
    
    if ($@) {
        $dbh->rollback;
        log('error',"AOICrawlerDBLayer: add_new_aoi_records():$@");
        exit;
    }

    log('debug',"AOICrawlerDBLayer: add_new_aoi_records: Finished.");
    return;
}

# *****************************
# Very odd perl autoloading ...
# *****************************
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
    my $db_vendor=PVCrawlerDBLayer::get_db_vendor();
    my $home_dir=PVCrawlerDBLayer::get_home_dir();
    my $db_functions_module = $db_vendor . "DBFunctions";
    require "$home_dir/$db_functions_module.pm";

    my $subroutine = "$db_functions_module"."::".$call_signature;
    goto &$subroutine;
}

1;
