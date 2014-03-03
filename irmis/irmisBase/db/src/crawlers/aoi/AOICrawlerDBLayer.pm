
package AOICrawlerDBLayer;

# This package provides subroutines for writing out aoi crawler data to
# a relational database. Vendor specific code is factored out into
# perl modules XXXDBFunctions.pm, so this module should remain
# vendor agnostic.

use File::Basename; 
use DBI qw(:sql_types);
require Exporter;
use POSIX;
use LogUtil;
use Blowfish_PP;

our @ISA = qw(Exporter);
our @EXPORT = qw(all_aoi_save find_ioc_resource_and_uri set_test_mode set_home_dir set_decryption_key find_boot_iocs aoi_crawler_boot_id );
our @EXPORT_OK = qw();
our $VERSION = 1.00;

my $test_mode = 0;  # if 1, do no db access
my $home_dir = ".";  # home dir from which parent crawler is run
my $db_properties_path = "db.properties";
my $dbh;
my $db_vendor; # established on first db_connect
my $decryption_key; # optional 16 byte blowfish key
my %aoi_hash;
my %plc_hash;

# Public
sub set_test_mode {
    $test_mode = $_[0];
}

# Public
# Sets the directory where db.properties file can be found. 
sub set_home_dir {
    $home_dir = $_[0];

    $db_properties_path = $home_dir . "/db.properties";
}

# Public
# Sets the 16 char blowfish decryption key. If this is defined, this module
# will assume the db.properties file is encrypted with this key.
sub set_decryption_key {
    $decryption_key = $_[0];
}

# Public
sub all_aoi_save {
    #
    # arguments to subroutine
    #

    # ioc boot info
    my $ioc_boot_info = $_[0];

    # results of st.cmd file parse
    my $parse_err = $_[1];

    # flag indicating whether an ioc resource has changed
    my $ioc_resource_changed = $_[2];

    # array ref to stcmd_line_list
    my $stcmd_line_list = $_[3];
    
    if ($test_mode) {
        LogUtil::log('info',"Test Mode; No database updates.");
        return;
    }

    #
    # locals
    #
    LogUtil::log('debug',"AOICrawlerDBLayer: begin all_save");

    # below not needed for aoi crawler
    #$ioc_boot_info->{ioc_boot_id} = 
    #    ioc_boot_save($ioc_boot_info, $parse_err, $ioc_resource_changed);

    # if a dbd or db file has NOT changed, write out info on aoi resources
    #if ( ! $ioc_resource_changed) {

        # don't do below for aoi crawler
        # ioc_resource_ids array indexed by file_index in dbd
        # my @ioc_resource_ids = 
        #    ioc_resource_and_uri_save($dbh, $ioc_boot_info, $file_list);

        # if no parse errors detected, write pv data and st.cmd lines to aoi database
        if (!$parse_err) {
            
            if (!$dbh) {
                $dbh = db_connect();
            }

            my $ioc_nm = $ioc_boot_info->{ioc_nm};
            my $ioc_id = $ioc_boot_info->{ioc_id};
            my $ioc_boot_id = $ioc_boot_info->{ioc_boot_id};

            LogUtil::log('debug',"AOICrawlerDBLayer: removing all aoi records for $ioc_nm");

            my $remove_err = remove_old_aoi_records_for_ioc($dbh,$ioc_id);
            if ($remove_err) {
                LogUtil::log('error',"Remove records error for ioc $ioc_nm.");
                return;
            } else {
                LogUtil::log('debug',"AOICrawlerDBLayer: commit remove changes");
                $dbh->commit;
            }


            LogUtil::log('debug',"AOICrawlerDBLayer: save all pv and stcmd data");
            
            # rec_type_ids hash keyed by rec_typ
            # my %rec_type_ids = 
            #    rec_type_save($dbh, $ioc_boot_info, \@ioc_resource_ids, $dbd);
            
            # fld_type_save($dbh, \%rec_type_ids, $dbd);
            
            rec_and_stcmd_save($dbh,$stcmd_line_list,$ioc_id,$ioc_boot_id);

            # dev_sup_save($dbh, $dev_sup, \%rec_type_ids);
 
            LogUtil::log('debug',"AOICrawlerDBLayer: commit add changes");
            $dbh->commit;

        } else { 
            LogUtil::log('error',"Parse error detected. DB records for ioc NOT changed.");
        }

    #} else {
    #        LogUtil::log('error',"No ioc resources changed.");
    #}

    return;

}

# Load a single aoi record given the aoi name. Returns aoi_id.
# Private
sub aoi_load_by_name {
    my $aoi_name = $_[0];

    my $aoi_id = 0;

    return $aoi_hash{$aoi_name}if $aoi_hash{$aoi_name};

    LogUtil::log('debug',"AOICrawlerDBLayer: begin aoi_load_by_name -> $aoi_name");

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
        LogUtil::log('error',"aoi_load_by_name():$@");
        exit;
    }

    if (my $aoi_row = $select_aoi_st->fetchrow_hashref) {
        $aoi_id = $aoi_row->{aoi_id};
    } 
    $aoi_hash{$aoi_name} = $aoi_id; 

    return $aoi_id;
}

# Load a single plc record given the plc name. Returns plc_id.
# Private
sub plc_load_by_name {
    my $plc_name = $_[0];

    my $plc_id = 0;

    return $plc_hash{$plc_name}if $plc_hash{$plc_name};

    LogUtil::log('debug',"AOICrawlerDBLayer: begin plc_load_by_name -> $plc_name");

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
        LogUtil::log('error',"plc_load_by_name():$@");
        exit;
    }

    if (my $plc_row = $select_plc_st->fetchrow_hashref) {
        $plc_id = $plc_row->{plc_id};
    } 
    $plc_hash{$plc_name} = $plc_id; 

    return $plc_id;
}

# Find latest ioc_boot record in db for given ioc_nm. Also gets any
# possible error code from the ioc_error companion table. The second
# argument specifies whether to look for the latest boot record where
# current_boot = 1, or whether to look for current_load = 1
# Private
sub ioc_boot_record_load {
    my $ioc_boot_info = $_[0];
    my $current_load_flag = $_[1];

    my $ioc_nm = $ioc_boot_info->{ioc_nm};

    my $ioc_boot_id = 0;
    my $ioc_boot_date = 0;
    my $load_performed = 0;

#    if ($test_mode) {
#        my $null_boot_record = {ioc_boot_id=>0, ioc_boot_date=>"0000000",
#                                load_performed=>1, ioc_error_num=>0};
#        
#        return $null_boot_record;
#    }

    LogUtil::log('debug',"AOICrawlerDBLayer: begin ioc_boot_record_load");

    if (!$dbh) {
        $dbh = db_connect();
    }

    # Get latest boot record for this ioc (if any)
    my $select_ioc_boot_sql;
    my $db_vendor_format_date = get_format_date_sql('ioc_boot.ioc_boot_date');
    if ($current_load_flag) {
        $select_ioc_boot_sql = 'select ioc_boot.ioc_boot_id, ' .
            $db_vendor_format_date . ' ioc_boot_date, ' .
            'ioc_boot.current_load ' .
            'from ioc_boot, ioc where ioc.ioc_id = ioc_boot.ioc_id and ' .
            'ioc.ioc_nm = ? and ioc_boot.current_load = 1';
    } else {
        $select_ioc_boot_sql = 'select ioc_boot.ioc_boot_id, ' .
            $db_vendor_format_date . ' ioc_boot_date, ' .
            'ioc_boot.current_load ' .
            'from ioc_boot, ioc where ioc.ioc_id = ioc_boot.ioc_id and ' .
            'ioc.ioc_nm = ? and ioc_boot.current_boot = 1';
    }

    my $select_ioc_boot_st;
    eval {
        $select_ioc_boot_st = $dbh->prepare($select_ioc_boot_sql);
        $select_ioc_boot_st->bind_param(1, $ioc_nm, SQL_VARCHAR);
        $select_ioc_boot_st->execute();
    };
    if ($@) {
        LogUtil::log('error',"ioc_boot_record_load():$@");
        exit;
    }
    
    if (my $ioc_boot_row = $select_ioc_boot_st->fetchrow_hashref) {
        $ioc_boot_id = $ioc_boot_row->{ioc_boot_id};
        $ioc_boot_date = $ioc_boot_row->{ioc_boot_date};
        $load_performed = $ioc_boot_row->{current_load};
    }

    # Look for any ioc_error record
    my $select_ioc_error_sql = 'select ioc_error_num from ioc_error where ' .
        'ioc_boot_id = ?';

    my $select_ioc_error_st;
    eval {
        $select_ioc_error_st = $dbh->prepare($select_ioc_error_sql);
        $select_ioc_error_st->bind_param(1, $ioc_boot_id, SQL_INTEGER);
        $select_ioc_error_st->execute();
    };
    if ($@) {
        LogUtil::log('error',"ioc_boot_record_load():$@");
        exit;
    }
    my $ioc_error_num = 0;
    if (my $ioc_error_row = $select_ioc_error_st->fetchrow_hashref) {
        $ioc_error_num = $ioc_error_row->{ioc_error_num};
    }

    my %ioc_boot_record = (ioc_boot_id=>"$ioc_boot_id", ioc_boot_date=>"$ioc_boot_date",
                           load_performed=>"$load_performed", ioc_error_num=>$ioc_error_num);

    return \%ioc_boot_record;
}


# Public
sub find_ioc_resource_and_uri {
    my $ioc_boot_info = $_[0];
    my @ioc_resources = ();
    
    if ($test_mode) {
        return \@ioc_resources;
    }

    LogUtil::log('debug',"AOICrawlerDBLayer: begin find_ioc_resource_and_uri");

    if (!$dbh) {
        $dbh = db_connect();
    }

    # Get the right ioc_boot record (must have current_load=1)
    my $ioc_boot_record = ioc_boot_record_load($ioc_boot_info,1);
    if ($ioc_boot_record->{ioc_boot_id} == 0) {  # not found
        return \@ioc_resources;
    }
    my $ioc_boot_id = $ioc_boot_record->{ioc_boot_id};
    my $db_vendor_format_date = get_format_date_sql('uri.uri_modified_date');
    my $select_ioc_resource_sql = 'select unreachable,uri, ' . $db_vendor_format_date . 
        ' uri_modified_date from ' .
        'ioc_resource,uri where ioc_resource.uri_id=uri.uri_id ' .
        'and ioc_resource.ioc_boot_id = ?';
    my $select_ioc_resource_st;    
    eval {
        $select_ioc_resource_st = $dbh->prepare($select_ioc_resource_sql);
        $select_ioc_resource_st->bind_param(1,$ioc_boot_id,SQL_INTEGER);
        $select_ioc_resource_st->execute();
    };
    if ($@) {
        LogUtil::log('error',"find_ioc_resource_and_uri():$@");
        exit;
    }

    my $ioc_resource_row;
    # my @ioc_resources = ();
    my %ioc_resource;
    while ($ioc_resource_row = $select_ioc_resource_st->fetchrow_hashref) {
        $unreachable = $ioc_resource_row->{unreachable};
        $uri = $ioc_resource_row->{uri};
        $uri_modified_date = $ioc_resource_row->{uri_modified_date};
        $ioc_resource = {unreachable=>"$unreachable",uri=>"$uri",
                         uri_modified_date=>"$uri_modified_date"};
        push @ioc_resources, $ioc_resource;
    } 
    
    return \@ioc_resources;
}


# Private
sub remove_old_aoi_records_for_ioc {
    my $dbh = $_[0];
    my $ioc_id = $_[1];

    return if ($test_mode);
    
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
        LogUtil::log('error',"select from ioc_stcmd_line: $@");
        return 1;
    }

    my $ioc_row;
    my $delete_sql;
    my $delete_st;
    while ($ioc_row = $select_st->fetchrow_hashref) {
        $ioc_stcmd_line_id = $ioc_row->{ioc_stcmd_line_id};
        LogUtil::log('verbose',"ioc_stcmd_line_id=$ioc_stcmd_line_id");
        

	# take care of table aoi_epics_record
	
	my $select_aoi_sql = 'select aoi_ioc_stcmd_line_id from aoi_ioc_stcmd_line where ioc_stcmd_line_id=?';
	my $select_aoi_st;
	eval{
	    $select_aoi_st = $dbh->prepare($select_aoi_sql);
	    $select_aoi_st->execute("$ioc_stcmd_line_id");
	};
	
	if ($@){
	    LogUtil::log('error',"select from aoi_ioc_stcmd_line: $@");
	    return 1;
	}
	
	my $aoi_ioc_row;
	
	while ($aoi_ioc_row = $select_aoi_st->fetchrow_hashref){
	
	    $aoi_ioc_stcmd_line_id = $aoi_ioc_row->{aoi_ioc_stcmd_line_id};
	    
	    LogUtil::log('verbose',"aoi_ioc_stcmd_line_id = $aoi_ioc_stcmd_line_id");
	    
            $delete_sql = 'delete from aoi_epics_record where aoi_ioc_stcmd_line_id='."\'$aoi_ioc_stcmd_line_id\'";
            $rows_deleted = 0;
            eval {
                $rows_deleted = $dbh->do($delete_sql);
            };
        
            if ($@) {
                LogUtil::log('error',"remove from aoi_epics_record: $@");
                return 1;
            }
            LogUtil::log('verbose',"aoi_epics_record rows_deleted = $rows_deleted");
        
        }
        

        # take care of table aoi_ioc_stcmd_line
        
        $rows_deleted = 0;
        $delete_sql = 'delete from aoi_ioc_stcmd_line where ioc_stcmd_line_id='."$ioc_stcmd_line_id";
        eval {
            $rows_deleted = $dbh->do($delete_sql);
        };
        if ($@) {
            LogUtil::log('error',"remove from aoi_ioc_stcmd_line: $@");
            return 1;
        }
        LogUtil::log('verbose',"aoi_ioc_stcmd_line rows_deleted=$rows_deleted");
        
        
        # take care of table aoi_plc_stcmd_line
        
        $rows_deleted = 0;
        $delete_sql = 'delete from aoi_plc_stcmd_line where ioc_stcmd_line_id='."$ioc_stcmd_line_id";
	eval {
	    $rows_deleted = $dbh->do($delete_sql);
	};
	if ($@) {
	    LogUtil::log('error',"remove from aoi_plc_stcmd_line: $@");
	    return 1;
	}
        LogUtil::log('verbose',"aoi_plc_stcmd_line rows_deleted=$rows_deleted");
        
        
	# take care of table ioc_stcmd_line

	$rows_deleted = 0;
        $delete_sql = 'delete from ioc_stcmd_line where ioc_stcmd_line_id='."$ioc_stcmd_line_id";
        eval {
            $rows_deleted = $dbh->do($delete_sql);
        };
        if ($@) {
            LogUtil::log('error',"remove from ioc_stcmd_line: $@");
            return 1;
        }
        LogUtil::log('verbose',"ioc_stcmd_line rows_deleted=$rows_deleted");

    }

    $rows_deleted = 0;
    $delete_sql = 'delete from aoi_crawler where ioc_id='."$ioc_id";
    eval {
        $rows_deleted = $dbh->do($delete_sql);
    };
    if ($@) {
        LogUtil::log('error',"remove from aoi_crawler: $@");
        return 1;
    }
    LogUtil::log('verbose',"aoi_crawler rows_deleted = $rows_deleted");

    return 0;
}

# Private
sub rec_and_stcmd_save {
    my $dbh = $_[0];
    my $stcmd_line_list = $_[1];
    my $ioc_id = $_[2];
    my $ioc_boot_id = $_[3];

    return if ($test_mode);
    
    # need to populate aoi tables: aoi_epics_record, 
    # aoi_ioc_stcmd_line, and ioc_stcmd_line
    
    # will need the aoi_id for each aoi name encountered by the
    # aoi crawler
    
    # each epics record has as a foreign key aoi_ioc_stcmd_line_id
    
    LogUtil::log('debug',"Writing aoi data to database");

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
            

        LogUtil::log('debug',"sql for table aoi_ioc_stcmd_line :  $insert_aoi_ioc_stcmd_line_sql");
        LogUtil::log('debug',"sql for table epics_record : $insert_epics_record_sql");
        LogUtil::log('debug',"sql for table ioc_stcmd_line : $insert_ioc_stcmd_line_sql");
        
        my $last_stcmd_line_id = 0;
        my $last_aoi_plc_id = 0;
        my $last_aoi_stcmd_line_id = 0;
        my $aoi_id = 0;
        my $last_epics_record_id = 0;
        
        my $aoi_line_count = 0;
        my $plc_line_count = 0;
        my $record_line_count = 0;

        # loop through $stcmd_line_list to fill the ioc_stcmd_line table
        for $stcmd_line (@$stcmd_line_list) {

            $stcmd_line_index = $stcmd_line->{stcmd_line_index};
            $stcmd_line_text = $stcmd_line->{stcmd_line};
            $ioc_id = $stcmd_line->{ioc_id};

            $last_stcmd_line_id = pre_insert_get_id($dbh, "ioc_stcmd_line");
            if ($last_stcmd_line_id) { $insert_ioc_stcmd_line_st->bind_param(1,$last_stcmd_line_id, SQL_INTEGER); }
            $insert_ioc_stcmd_line_st->bind_param(2,$stcmd_line_text, SQL_VARCHAR);
            $insert_ioc_stcmd_line_st->bind_param(3,$ioc_id, SQL_INTEGER);
            $insert_ioc_stcmd_line_st->bind_param(4,$stcmd_line_index, SQL_INTEGER);

            if ( $stcmd_line->{stcmd_include_line_index} ne '') {
                $stcmd_include_line_index = $stcmd_line->{stcmd_include_line_index};
                $insert_ioc_stcmd_line_st->bind_param(5,$stcmd_include_line_index, SQL_INTEGER);
            } else {
                $insert_ioc_stcmd_line_st->bind_param(5,undef, SQL_INTEGER);
            }
            
            LogUtil::log('verbose',"Inserting aoi stcmd line: $stcmd_line_text");
            
            $insert_ioc_stcmd_line_st->execute();

            $last_stcmd_line_id = post_insert_get_id($dbh, 'ioc_stcmd_line');
            
       
            # loop through the $record_list to populate the epics_record table
            # my $records_ref = $stcmd_line->{record_list};
            # for $rec_nm (@$records_ref) {

            #    $last_epics_record_id = pre_insert_get_id($dbh, "epics_record");
            #    if ($last_epics_record_id) { $insert_epics_record_st->bind_param(1,$last_epics_record_id, SQL_INTEGER); }
            #    $insert_epics_record_st->bind_param(2,$last_stcmd_line_id, SQL_INTEGER);
            #    $insert_epics_record_st->bind_param(3,$rec_nm, SQL_VARCHAR);

            #    LogUtil::log('verbose',"insert epics_record: $rec_nm");
                
            #    $insert_epics_record_st->execute();
            #    $record_line_count++;
                
            # } # end for @records loop
            # $count = scalar(@$records_ref);
            # LogUtil::log('verbose',"insert $count epics_records") if $count > 0;
            
           
            # loop through the $aoi_list to populate the table aoi_ioc_stcmd_line
            
            my $aois_ref = $stcmd_line->{aoi_list};
            my $aoi_pv_filter = "";
                      
            my @filters = ();
            my $write_record = 0;
            
            for $aoi_name (@$aois_ref) {
            
                $aoi_id = aoi_load_by_name($aoi_name);

                if (!$aoi_id) { 
                    LogUtil::log('error',"Cannot insert into aoi_ioc_stcmd_line: invalid aoi name: $aoi_name");
                    next;
                }
                
                LogUtil::log('verbose',"aoi list contains: $aoi_name");
                
                # get PV filter(s) for the specific aoi in this loop
                
                # clean up re-used loop variables
                undef(%pv_filter_ref);
                undef(@filters);
                undef(@plcs_ref);
                
                my $pv_filters_ref = $stcmd_line->{pv_filter_hash};
                
                $aoi_pv_filter = "";
                $write_record = 0;
                
                # here, $aoi_pv_filter is a string of zero or more filters to be used on one record name
                # check if have an undefined PV filter
                
                ## if (exists $pv_filters_ref{$aoi_name}) {
                
                while ( ($key, $value) = each %$pv_filters_ref ) {
                
                    if ($aoi_name eq $key) {
                    	LogUtil::log('verbose',"$aoi_name has pv filter: $value");
                    
                    	$aoi_pv_filter = $value;
                    	# create array list of filter strings for this aoi
                    	# assume comma separated and possible spaces in the filter string provided by user
                    
                    	@filters = split /,\s*/, $aoi_pv_filter;
                    
                    	for $rec_filter (@filters){
                    		LogUtil::log('verbose',"aoi $aoi_name filter field: $rec_filter");
                    	}
                    }
                }
                
                $last_aoi_stcmd_line_id = 0;
                $last_aoi_stcmd_line_id = pre_insert_get_id($dbh, "aoi_ioc_stcmd_line");
        
                if ($last_aoi_stcmd_line_id) { $insert_aoi_ioc_stcmd_line_st->bind_param(1, $last_aoi_stcmd_line_id, SQL_INTEGER); }
                
                $insert_aoi_ioc_stcmd_line_st->bind_param(2,$aoi_id, SQL_INTEGER);
                $insert_aoi_ioc_stcmd_line_st->bind_param(3,$last_stcmd_line_id, SQL_INTEGER);
		$insert_aoi_ioc_stcmd_line_st->bind_param(4,$aoi_pv_filter, SQL_VARCHAR);

                # LogUtil::log('verbose',"insert into table aoi_ioc_stcmd_line, last_stcmd_line_id  $last_stcmd_line_id and last_aoi_stcmd_line_id $last_aoi_stcmd_line_id");
                
                $insert_aoi_ioc_stcmd_line_st->execute();
                
                $last_aoi_stcmd_line_id = post_insert_get_id($dbh, 'aoi_ioc_stcmd_line');
                
                LogUtil::log('verbose',"from post insert, last_aoi_stcmd_line_id = $last_aoi_stcmd_line_id");
            
                $aoi_line_count++;
                
                # write data to table aoi_epics_record
                # loop through the $record_list
                
                my $records_ref = $stcmd_line->{record_list};
                
                $count = 0;
                for $rec_nm (@$records_ref) {
                    # check if user provided a filter for PV record names
                    if($aoi_pv_filter ne "") {
                        # match filters to epics record name
                        for $rec_filter(@filters){
                            if($rec_nm =~ /($rec_filter)/) {
                                # flag write this record name to table
                                
                                LogUtil::log('verbose',"matched PV name $rec_nm with filter $rec_filter");
                                
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
                        
                        LogUtil::log('verbose',"insert aoi_epics_record: $rec_nm");
                        
                        $insert_epics_record_st->execute();
                        $record_line_count++;
                        $count++;
                    } 
                
                    $write_record = 0;
                    
                    LogUtil::log('verbose',"insert $count aoi_epics_record") if $count > 0;
           
                }  # end for records_ref
                
                # loop through the $aoi_plc_stcmd_line_list to populate the aoi_plc_stcmd_line table
                
                LogUtil::log('verbose',"loop through the plc_list...");
                
                my $plcs_ref = $stcmd_line->{plc_list};
                my $write_aoi_plc = 1;
                
                for $plc_name (@$plcs_ref) {

		    $plc_id = plc_load_by_name($plc_name);
                    if (!$plc_id) { 
                        LogUtil::log('error',"Cannot insert into aoi_plc_stcmd_line: invalid plc name: $plc_name");
                        next;
                    }
                    
                    # check to see if this combination of aoi and plc is already entered in the table aoi_plc_stcmd_line
                    # if it already exists in the table, skip over insert
                    
      		    my $select_aoi_plc_sql = 'select plc_id from aoi_plc_stcmd_line where aoi_id = ?';
         	    my $select_aoi_plc_st;    
                    eval {
                       $select_aoi_plc_st = $dbh->prepare($select_aoi_plc_sql);
                       $select_aoi_plc_st->bind_param(1,$aoi_id,SQL_INTEGER);
                       $select_aoi_plc_st->execute();
                    };
                    if ($@) {
                         LogUtil::log('error',"select_aoi_plc_st:$@");
                    exit;
         	    }
               
                    my $aoi_plc_row;
                    
                    while ($aoi_plc_row = $select_aoi_plc_st->fetchrow_hashref) {
                    	my $result_plc_id = $aoi_plc_row->{plc_id};
                    	LogUtil::log('verbose',"result_plc_id from aoi_plc query: $result_plc_id where aoi_id: $aoi_id");
                   
                    	if ($result_plc_id == $plc_id) {
               		    # do not write aoi/plc data to table aoi_plc_stcmd_line
               		    $write_aoi_plc = 0;	
                    	}
                    } 
                
                    if ($write_aoi_plc) {
                    
                    	my $last_aoi_plc_stcmd_line_id = pre_insert_get_id($dbh, "aoi_plc_stcmd_line");
			if ($last_aoi_plc_stcmd_line_id) { $insert_aoi_plc_stcmd_line_st->bind_param(1,$last_aoi_plc_stcmd_line_id, SQL_INTEGER); }
			                    	
			$insert_aoi_plc_stcmd_line_st->bind_param(2,$aoi_id, SQL_INTEGER);
			$insert_aoi_plc_stcmd_line_st->bind_param(3,$plc_id, SQL_INTEGER);
			$insert_aoi_plc_stcmd_line_st->bind_param(4,$last_stcmd_line_id, SQL_INTEGER);
			$insert_aoi_plc_stcmd_line_st->execute();
			    	
			LogUtil::log('verbose',"insert aoi_plc_stcmd_line, aoi id: $aoi_id and plc id:  $plc_id");
                    	$plc_line_count++;
                    }
                    
                    # reset reused loop variable
                    $write_aoi_plc = 1;
                
                } # end for plc_name
                
                $count = scalar(@$plcs_ref);
                LogUtil::log('verbose',"total plcs inserted  $plc_line_count in table aoi_plc_stcmd_line") if $plc_line_count > 0;
            }
            $count = scalar(@$aois_ref);

        } # end for $stcmd_line loop
        
        $count = scalar(@$stcmd_line_list);
        LogUtil::log('debug',"inserted $count ioc_stcmd_line lines");
        LogUtil::log('debug',"inserted $record_line_count aoi_epics_record lines");
        LogUtil::log('debug',"inserted $aoi_line_count aoi_ioc_stcmd_line lines");
        LogUtil::log('debug',"inserted $plc_line_count aoi_plc_stcmd_line lines");

        # insert a record into aoi_crawler 
        $last_aoi_crawler_id = pre_insert_get_id($dbh, "aoi_crawler");
        if ($last_aoi_crawler_id) { $insert_aoi_crawler_st->bind_param(1,$last_aoi_crawler_id, SQL_INTEGER); }
        $insert_aoi_crawler_st->bind_param(2,$ioc_id, SQL_INTEGER);
        $insert_aoi_crawler_st->bind_param(3,$ioc_boot_id, SQL_INTEGER);

        $insert_aoi_crawler_st->execute();

        LogUtil::log('debug',"inserted 1 aoi_crawler line: ioc_id=$ioc_id");

    };  # end eval
    
    if ($@) {
        $dbh->rollback;
        LogUtil::log('error',"aoi crawler rec_and_stcmd_save():$@");
        exit;
    }

    return;
}

# Private
sub db_connect {

    #connect to database
    my $dbh;

    # Look up connection parameters in external db.properties file.
    # If a decryption key has been supplied, use blowfish algorithm
    # to decrypt the contents of the file.
    
    my $db_host;
    my $db_database;
    my $db_user;
    my $db_password;
    chdir $home_dir;
    if (!-e $db_properties_path) {
        LogUtil::log('error',"Unable to find $db_properties file");
        LogUtil::log('error',"Exiting...");
        exit;
    }

    # Read in contents of db.properties (decrypting if key defined)
    my $blowfish_object;
    if ($decryption_key) {
        my $packed_key = pack("a16",$decryption_key);
        $blowfish_object = Crypt::Blowfish_PP->new($packed_key);
        if (!$blowfish_object) {
            LogUtil::log('error',"The given blowfish key appears to be invalid");
            LogUtil::log('error',"Exiting...");
            exit;
        }
    }
    open(DBPROPS,$db_properties_path);
    my $inbuf;
    my $input_text;
    while (my $read_len = read(DBPROPS, $inbuf, 8)) {
        if ($blowfish_object) {
            my $decrypted_text = $blowfish_object->decrypt($inbuf);
            $input_text = $input_text . $decrypted_text;
        } else {
            $input_text = $input_text . $inbuf;
        }
    }
    close(DBPROPS);

    # Parse up the db.properties text
    my $props_line;
    while ($input_text =~ /^([^\n]*)\n(.*)/s) {
        $props_line = $1;
        $input_text = $2;
        if ($props_line =~ /(.*)=(.*)/) {
            $prop_name = $1;
            $prop_val = $2;
            if ($prop_name eq "db.host") {
                $db_host = $prop_val;
            } elsif ($prop_name eq "db.database") {
                $db_database = $prop_val;
            } elsif ($prop_name eq "db.user") {
                $db_user = $prop_val;
            } elsif ($prop_name eq "db.password") {
                $db_password = $prop_val;
            } elsif ($prop_name eq "db.vendor") {
                # db_vendor is package global
                $db_vendor = $prop_val;
            }
        }
    }
    if (!$db_host || !$db_database || !$db_user || !$db_password ||!$db_vendor) {
        LogUtil::log('error',"Unable to read db connection parameters from db.properties");
        LogUtil::log('error',"Exiting...");
        exit;
    }


    # Get db vendor-specific DBI connect string
    #my $dsn = get_dbi_dsn($db_database, $db_host);
    my $dsn = "DBI:mysql:$db_database:$db_host";

    eval {
        $dbh = DBI->connect($dsn,$db_user,$db_password,
                            {AutoCommit=>0, RaiseError=>1, PrintError=>0, 
                             FetchHashKeyName=>'NAME_lc'});
    };
    if ($@) {
        LogUtil::log('error',"Unable to connect to db $@");
        LogUtil::log('error',"Exiting...");
        exit;
    }
    # Do any db vendor-specific db session initialization
    #initialize_session($dbh);

    return $dbh;
}

# Very odd perl autoloading ...
sub AUTOLOAD {

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
#   my $db_functions_module = $db_vendor . "DBFunctions";
my $db_functions_module = "MySQLDBFunctions";
    require "$home_dir/$db_functions_module.pm";

    my $subroutine = "$db_functions_module"."::".$call_signature;
    goto &$subroutine;
}

1;

############################################################################

sub aoi_crawler_boot_id {
    my $ioc_id = $_[0];

    LogUtil::log('debug',"AOICrawlerDBLayer: begin aoi_crawler_record_load");

    if (!$dbh) {
        $dbh = db_connect();
    }

    my $select_aoi_crawler_sql = 'select ioc_boot_id,ioc_id from aoi_crawler where ioc_id=?';
    my $select_aoi_crawler_st;
    eval {
        $select_aoi_crawler_st = $dbh->prepare($select_aoi_crawler_sql);
        $select_aoi_crawler_st->execute($ioc_id);
    };
    if ($@) {
        LogUtil::log('error',"aoi_crawler_record_load():$@");
        exit;
    }

    my $ioc_boot_id = 0;
    my $ioc_row = $select_aoi_crawler_st->fetchrow_hashref;
    $ioc_boot_id = $ioc_row->{ioc_boot_id} if $ioc_row;

    return $ioc_boot_id;
}

# Public
# Gets last boot info for active iocs
sub find_boot_iocs {
    my @iocList = @_;

    LogUtil::log('debug',"AOICrawlerDBLayer: begin find_boot_iocs");

    if (!$dbh) {
        $dbh = db_connect();
    }

     my $select_ioc_sql = 'select ioc_nm,ioc_id from ioc,ioc_status where '.
                         'ioc.ioc_status_id=ioc_status.ioc_status_id '.
                   "and (ioc_status='production' or ioc_status='ancillary')";
    if (scalar(@iocList) > 0) {
        $prefix = " and ( ";
        foreach my $ioc_nm (@iocList) {
            $mycondition .= $prefix . " ioc_nm='$ioc_nm'";
            $prefix = ' or ' ;
        }
        $mycondition .= ' )';
        $select_ioc_sql .= $mycondition;
    }
    my $select_ioc_st;
    eval {
        $select_ioc_st = $dbh->prepare($select_ioc_sql);
        $select_ioc_st->execute();
    };
    if ($@) {
        LogUtil::log('error',"find_boot_iocs():$@");
        exit;
    }

    my @ioc_boot_infos = ();
    while ($ioc_row = $select_ioc_st->fetchrow_hashref) {
        my $ioc_nm = $ioc_row->{ioc_nm};
        my $ioc_id = $ioc_row->{ioc_id};

        #LogUtil::log('debug',"AOICrawlerDBLayer: find_boot_iocs  processing $ioc_nm");
        # put any rules here to filter out unwanted iocs
        if ($ioc_nm =~ /^ioc.*/ ||
            $ioc_nm =~ /^sioc.*/) {
            push @ioc_names, "$ioc_nm";
        }

        my $db_vendor_format_date = get_format_date_sql('ioc_boot.ioc_boot_date');
        my $select_ioc_boot_sql = 'select ioc_boot_id, ';
        $select_ioc_boot_sql .= ' sys_boot_line, ';
        $select_ioc_boot_sql .= $db_vendor_format_date . ' ioc_boot_date';
        $select_ioc_boot_sql .= ' from ioc_boot where ioc_id=?';
        $select_ioc_boot_sql .= ' and current_load=\'1\'';
        my $select_ioc_boot_st;
        eval {
            $select_ioc_boot_st = $dbh->prepare($select_ioc_boot_sql);
            $select_ioc_boot_st->execute($ioc_id);
        };
        if ($@) {
            LogUtil::log('error',"find_boot_iocs():$@");
            exit;
        }
        #my %ioc_boot_info;
        my $ioc_boot_id = 0;
        my $ioc_boot_date = 0;
        my $sys_boot_line = '';
        my $ioc_boot_row = $select_ioc_boot_st->fetchrow_hashref;
        if ( $ioc_boot_row ) {
            my $ioc_boot_id = $ioc_boot_row->{ioc_boot_id};
            my $ioc_boot_date = $ioc_boot_row->{ioc_boot_date};
            my $sys_boot_line = $ioc_boot_row->{sys_boot_line};

            $ioc_boot_info = {ioc_nm=>"$ioc_nm", 
                              ioc_id=>"$ioc_id",
                              ioc_boot_id=>"$ioc_boot_id",
                              ioc_boot_date=>"$ioc_boot_date",
                              sys_boot_line=>"$sys_boot_line"};

            push @ioc_boot_infos, $ioc_boot_info;

            #LogUtil::log('debug',"AOICrawlerDBLayer: $ioc_nm bootId=$ioc_boot_id bootDate=$ioc_boot_date");

        } else {
            LogUtil::log('error',"ERROR: no boot data for $ioc_nm");
        }


    }

    LogUtil::log('debug',"AOICrawlerDBLayer: finished find_boot_iocs");
    return @ioc_boot_infos;
}


