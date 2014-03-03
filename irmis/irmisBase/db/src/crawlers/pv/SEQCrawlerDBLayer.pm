
package SEQCrawlerDBLayer;

# SEQCrawlerDBLayer.pm
# 
#   This package provides routines for writing Sequence file name, IOC, and PV occurance
#   data to a database. 
#
# Author: Jan. 1. 2006 Ron MacKenzie (ronm@slac.stanford.edu).
#
# Modifications:
#
#   10/23/07  Mariana Varotto (mvarotto@aps.anl.gov)
#
#             Fixed segment of code that clears current_load flag
#             Added code to check whether a (potential) record discovered in a seq program 
#              does exist

use DBI qw(:sql_types);
require Exporter;
use POSIX;
use LogUtil qw(log);
use File::stat;
use CommonUtil qw(db_connect get_test_mode);

our @ISA = qw(Exporter);
our @EXPORT_OK = qw(update_db_tables_seq);
our $VERSION = 1.00;

our $SEQ = 4;       # Mike Zelazny reserved this (SEQ=4) with Claude.

my $dbh;

#***********
# Public sub
#***********
# Store data in the database.
sub update_db_tables_seq {
    my $ioc_nm = $_[0];
    my $parse_err = $_[1];

    if (get_test_mode()) {
        log('info',"SEQCrawlerDBLayer:update_db_tables_seq: Test Mode. No database updates.");
        return;
    }

    log('debug',"SEQCrawlerDBLayer: update_db_tables_seq: Started.");

    if (!$dbh) {
        $dbh = db_connect();
        if (!$dbh) {
            log('error`',"SEQCrawlerDBLayer: Cannot connect to DB");
            exit;
        }
    }

    # This subr is called once per IOC.
    #
    # The data to be stored (as a result of the current crawl of the IOC) is in 
    # the @seq_hits array.  Before we store it in the database, let's delete 
    # records any previous crawls of this IOC by calling remove_old_seq_records().

    remove_old_seq_records($ioc_nm);    

    # if parse errors detected, do not add new SEQ data to database
    if ($parse_err) {
        log('error',"SEQ parse error detected. NO new SEQ records written to database.");
        return;
    } else {
        add_new_seq_records($ioc_nm);
    }

    if (get_test_mode()) {
       $dbh->rollback;
       log('info',"SEQCrawlerDBLayer: update_db_tables_seq: TEST MODE: Rollback SEQ changes.");
    } else {
       $dbh->commit; 
       log('debug',"SEQCrawlerDBLayer: update_db_tables_seq: Committed SEQ changes.");
    }

    log('debug',"SEQCrawlerDBLayer: update_db_tables_seq: Finished.");
    return;
}

#************
# Private sub 
# ***********
# Remove all SEQ records in database for a specified IOC
sub remove_old_seq_records {
    my ($ioc_name) = @_;

    if (!$dbh) {
        $dbh = db_connect();
    }

    log('debug',"SEQCrawlerDBLayer::remove_old_seq_records Starting $ioc_name");

    my $select_sql;
    my $select_st;
    $select_sql = 'select vuri.vuri_id,uri_id from rec_client,vuri_rel,vuri '
             ."where rec_client_type_id = $SEQ and rel_info like '$ioc_name %' "
             .'and rec_client.vuri_id = child_vuri_id and rec_client.vuri_id = vuri.vuri_id';
    eval {
        $select_st = $dbh->prepare($select_sql);
        $select_st->execute();
    };
    if ($@) {
        log('error',"SEQCrawlerDBLayer::remove_old_seq_records: select from rec_client,vuri_rel: $@ ");
        return 1;
    }

    my $vuri_id;
    my $uri_id;
    my %vuriHash;;
    my %uriHash;;
    while (my @select_row = $select_st->fetchrow_array) {
        $vuri_id = $select_row[0];
        $uri_id = $select_row[1];
        $vuriHash{$vuri_id}=1 if !exists($vuriHash{$vuri_id});
        $uriHash{$uri_id}=1 if !exists($uriHash{$uri_id});
    };

    my $rec_client_count=0;
    my $vuri_rel_count=0;
    my $vuri_count=0;
    my $uri_count=0;

    my $delete_sql;
    my $delete_st;

    $delete_sql = 'delete rec_client from rec_client,vuri_rel '
             ."where rec_client_type_id = $SEQ and rel_info like '$ioc_name %' and vuri_id = child_vuri_id ";
    eval {
        $delete_st = $dbh->prepare($delete_sql);
        $rec_client_count = $delete_st->execute();
    };
    if ($@) {
        log('error',"SEQCrawlerDBLayer::remove_old_seq_records: delete from rec_client: $@");
        return 1;
    }
    log('debug',"SEQCrawlerDBLayer::remove_old_seq_records: Remove $rec_client_count rec_client records.");

    # Code may change if other types of records can have rel_info='$iocname %'
    $delete_sql = "delete vuri_rel from vuri_rel where rel_info like '$ioc_name %' ";
    eval {
        $delete_st = $dbh->prepare($delete_sql);
        $vuri_rel_count = $delete_st->execute();
    };
    if ($@) {
        log('error',"SEQCrawlerDBLayer::remove_old_seq_records: delete from vuri_rel: $@");
        return 1;
    }
    log('debug',"SEQCrawlerDBLayer::remove_old_seq_records: Remove $vuri_rel_count vuri_rel records.");

    $delete_sql = "delete from vuri where vuri_id = ? ";
    foreach $vuri_id (keys %vuriHash) {
        eval {
            $delete_st = $dbh->prepare($delete_sql);
            $vuri_count+=$delete_st->execute("$vuri_id");
        };
        if ($@) {
            log('error',"SEQCrawlerDBLayer::remove_old_seq_records: delete from vuri: $@");
            return 1;
        }
    }
    log('debug',"SEQCrawlerDBLayer::remove_old_seq_records: Remove $vuri_count vuri records.");

    $delete_sql = "delete from uri where uri_id = ? ";
    foreach $uri_id (keys %uriHash) {
        eval {
            $delete_st = $dbh->prepare($delete_sql);
            $uri_count+=$delete_st->execute("$uri_id");
        };
        if ($@) {
            log('error',"SEQCrawlerDBLayer::remove_old_seq_records: delete from uri: $@");
            return 1;
        }
    }
    log('debug',"SEQCrawlerDBLayer::remove_old_seq_records: Remove $uri_count uri records.");

    log('info',"SEQCrawlerDBLayer: Removed ".
          "$uri_count uri ".
          "$vuri_count vuri ".
          "$vuri_rel_count vuri_rel ".
          "$rec_client_count rec_client records."); 

    log('debug',"SEQCrawlerDBLayer::remove_old_seq_records: Finished $ioc_name ");

    return 0;
};


#***********************************
# Private subr:  add_new_seq_records
#***********************************
sub add_new_seq_records {
    my ($ioc_name) = @_;

    my $seq_hits_ref = SEQCrawlerParser::get_seq_hits;

    my $ioc_nm = '';
    if (defined $$seq_hits_ref[0][2]) {
      ($ioc_nm) = split(/ /,$$seq_hits_ref[0][2],1);
    }

    my $rec_client_count=0;
    my $vuri_rel_count=0;
    my $vuri_count=0;
    my $uri_count=0;

    if ($ioc_nm eq '') {
        log('debug', "No sequences found for this IOC"); 
        log('info',"SEQCrawlerDBLayer: Inserted ".
          "$uri_count uri ".
          "$vuri_count vuri ".
          "$vuri_rel_count vuri_rel ".
          "$rec_client_count rec_client records."); 
        return;
    }
    else {
      log('debug',"SEQCrawlerDBLayer::add_new_seq_records Entered for ioc $ioc_nm");
    }

    if (!$dbh) {
        $dbh = db_connect();
    }

    log('debug',"SEQCr Adding uri, and  rec_client records\n");

    my $uri_id  = -1;
    my $vuri_id = -1;

    my $count = scalar(@$seq_hits_ref);
    log('debug',"SEQCrawlerDBLayer::add_new_seq_records: $ioc_name  seq_hits=$count");

    # ********************************************************************************
    # Loop over array items, writing to DB tables: uri, vuri, vuri_rel, and rec_client
    # ********************************************************************************
    foreach $row (@$seq_hits_ref) {
        
        # rows in 2D array seq_hits were added using following line
        #     push (@seq_hits, [$dot_oh_file, \@seq_pv_list, $rel_info]);

        my $filename    = $row->[0];
        my $pv_list_ref = $row->[1];
        my $rel_info    = $row->[2];

        #my ($ioc_nm) = split(/ /,$rel_info);
        #
        # print "SEQa YYY ARRAY FILENAME IS **************** = $filename \n";
        # print "SEQa YYY IOC NAME IS ********************** = $ioc_nm \n";
                       # two different ways to use pointer
                       # $_->[1] is the array pointer.  
                       #   which is dereferenced by ->[0] for array element.
        # print "SEQa YYY PV 0 is ************************** =  $pv_list_ref->[0] \n";
        # print "SEQa YYY PV 1 is ************************** =  $row->[1]->[1] \n";

        # my $last_ix = @$pv_list_ref;    # @ works differently with a reference.
        # print ("SEQb last_ix is $last_ix \n");

        # for ($ii=0; $ii<$last_ix; $ii++) {
	#   print "SEQb YYY PV number $ii is $pv_list_ref->[$ii] \n";
	# }

        eval {                                      # try/catch.

            # *************************
            # Write a row to  URI table
            # *************************

            $uri_id = pre_insert_get_id($dbh, "uri");

            # SQL for writing out a row of the URI table
            my $insert_uri_sql = 'insert into uri ' .
                    '(uri_id, uri, uri_modified_date, modified_date, modified_by) ' .
                    'values (?, ?, ?, ' . get_current_date_time() . ', \'seqcrawler\')';
            my $insert_uri_st = $dbh->prepare($insert_uri_sql);
    
            # bind and execute to write out uri row
            if ($uri_id) { $insert_uri_st->bind_param(1, $uri_id, SQL_INTEGER); }
            $insert_uri_st->bind_param(2, $filename, SQL_VARCHAR);
	    eval { $mtime = stat($filename)->mtime; };
            if ($@) {
                 $insert_uri_st->bind_param(3, get_null_timestamp(), SQL_TIMESTAMP);
            } else {
                 $insert_uri_st->bind_param(3, format_date($mtime), SQL_TIMESTAMP);
            }
            $uri_count+=$insert_uri_st->execute();
    
            $uri_id = post_insert_get_id($dbh, "uri");

            $prevFilename = $filename;

            log('debug',"SEQCr URI table insert: uri_id = $uri_id, filename = $filename "); 

            # ***********************************************
            # WRITE A ROW INTO VURI TABLE
            # VURI is just a pointer record pointing to URI 
            #      with one-to-one correspondance            
            # ***********************************************

            $vuri_id = pre_insert_get_id($dbh, "vuri");

            # SQL for writing out a row of the vuri table
            my $insert_vuri_sql = 'insert into vuri ' .
                             '(vuri_id, uri_id) ' .
                             'values (?, ?)';
            my $insert_vuri_st = $dbh->prepare($insert_vuri_sql);
    
            # bind and execute to write out vuri row
            if ($vuri_id) { $insert_vuri_st->bind_param(1, $vuri_id, SQL_INTEGER); }
            $insert_vuri_st->bind_param(2, $uri_id, SQL_INTEGER);
            $vuri_count+=$insert_vuri_st->execute();
   
            $vuri_id = post_insert_get_id($dbh, "vuri");

            log('debug',"SEQCr VURI table insert: vuri_id = $vuri_id uri_id = $uri_id"); 

            # *************************
            # WRITE A ROW INTO VURI_REL
            # *************************
            # 
            # This subr is entered once for each startup file.  Therefore once for each IOC.
            # But, we want to write a vuri_rel row for each uri row.  That way, we know
            #   the ioc associated with each sequence file.
            # So, we will have lots of duplicate IOC names in vuri_rel but that will
            #     make cleanup easier in some cases.

            my $vuri_rel_id = pre_insert_get_id($dbh, "vuri_rel");

            # SQL for writing out a row of the vuri_rel table
            my $insert_vuri_rel_sql = 'insert into vuri_rel ' .
                    '(vuri_rel_id, parent_vuri_id, child_vuri_id, rel_info) ' .
                    'values (?, null, ?, ?)';
            my $insert_vuri_rel_st = $dbh->prepare($insert_vuri_rel_sql);

            log('debug',"SEQCr Writing to vuri_rel: rel_info = $rel_info");

            # bind and execute to write out vuri_rel row
            if ($vuri_rel_id) { $insert_vuri_rel_st->bind_param(1, $vuri_rel_id, SQL_INTEGER); }
            $insert_vuri_rel_st->bind_param(2, $vuri_id, SQL_INTEGER);
            $insert_vuri_rel_st->bind_param(3, $rel_info, SQL_VARCHAR);  # "IOC Name" space "SEQ Command"
            $vuri_rel_count+=$insert_vuri_rel_st->execute();

            $vuri_rel_id = post_insert_get_id($dbh, "vuri_rel");
            log('debug',"SEQCr VURI_REL table insert: vuri_rel_id = $vuri_rel_id, vuri_id = $vuri_id, rel_info = $rel_info "); 

            if (scalar(@$pv_list_ref)) { 
                
                # ***********************************************
                # WRITE A ROW INTO REC_CLIENT
                # REC_CLIENT holds PV name and points to VURI.
                #       There  is one REC_CLIENT per PV name.  
                # ***********************************************

                my $last_ix = @$pv_list_ref;    # @ works differently with a reference.
                # print ("SEQb last_ix is $last_ix \n");

                for ($ii=0; $ii<$last_ix; $ii++) {
	            # print "SEQb PV number $ii is $pv_list_ref->[$ii] \n";
	            $pv_name =  $pv_list_ref->[$ii];
	            next if $pv_name eq '';
                    # print "SEQc YYY PV number $ii is  $pv_name \n";
                    my ($rec_name,$fld_name) = split (/\./,$pv_name,2); # dot seperates PV and FIELD
                    unless (defined $fld_name) {$fld_name = "VAL";}
                    #
		    # CHECK NOW IF REC EXIST
		    
		    my $select_rec_nm_sql = "select rec_nm from rec where rec_nm = '$rec_name'";
                    my $select_rec_nm_st;
                    $select_rec_nm_st = $dbh->prepare($select_rec_nm_sql);
                    $select_rec_nm_st->execute();
                    if ($select_rec_nm_st){
                        my $rec_nm_row = $select_rec_nm_st->fetchrow_hashref; # rec exists -- ASSUMING THERE'S ONLY ONE
                        my $rec_nm = $rec_nm_row->{rec_nm};
                        # log('debug',"SEQCr rec_nm found in db = $rec_nm"); 
                        # log('debug',"SEQCr rec_nm from seq = $rec_name"); 
			if ($rec_nm) {    		                     # Insert only if rec exists
                            if ($rec_nm eq $rec_name)
			    {
                                my $rec_client_id = pre_insert_get_id($dbh, "rec_client");
                                #
                                # SQL for writing out a row of the rec_client table
                                my $insert_rec_client_sql = 'insert into rec_client ' .
                                      '(rec_client_id, rec_client_type_id, rec_nm, fld_type, '.
                                      'vuri_id, current_load) ';
                                $insert_rec_client_sql = $insert_rec_client_sql .
                                                 "values (?, $SEQ, ?, ?, ?, 1)";
                                my $insert_rec_client_st = $dbh->prepare($insert_rec_client_sql);
                                #
                                # bind and execute to write out rec_client row
                                if ($rec_client_id) { $insert_rec_client_st->bind_param(1, $rec_client_id, SQL_INTEGER); }
                                $insert_rec_client_st->bind_param(2, $rec_name , SQL_VARCHAR);
                                $insert_rec_client_st->bind_param(3, $fld_name, SQL_VARCHAR);
                                $insert_rec_client_st->bind_param(4, $vuri_id, SQL_INTEGER);
                                $rec_client_count+=$insert_rec_client_st->execute();        
                            }
			    else {  
			        log('warn',"SEQ: Record $rec_name not found. $filename $rel_info");
			        mail("SEQ: Record $rec_name not found. File=$filename $rel_info");
			    }
			}
                    }
	        }   # end for
            }        # end if scalar  
        };           # end eval (try/catch...)
        if ($@) {
            $dbh->rollback;
            log('error',"SEQCr ERROR update_DB():$@");
            exit;
        }
        else {
          log('debug',"SEQCr Database written successfully. Commit not done yet."); 
	}

    }              # end for each row.

    log('info',"SEQCrawlerDBLayer: Inserted ".
          "$uri_count uri ".
          "$vuri_count vuri ".
          "$vuri_rel_count vuri_rel ".
          "$rec_client_count rec_client records."); 

    return;
}


#************
# Private sub
#************
# Takes seconds after epoch and converts to string with "YYYY-MM-DD HH:MM:SS"
sub format_date {
    my $secsAfter1970 = $_[0];

    if (!$secsAfter1970 || $secsAfter1970 == 0) {
        return get_null_timestamp();
    }

    my @tmArr = localtime($secsAfter1970);
    my $formatStr = '%Y-%m-%d %H:%M:%S';

    my $formatDate = POSIX::strftime($formatStr, $tmArr[0],$tmArr[1],$tmArr[2],
                                     $tmArr[3], $tmArr[4], $tmArr[5]);

    return $formatDate;
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
    my $home_dir = PVCrawlerDBLayer::get_home_dir();
    my $db_functions_module = $db_vendor . "DBFunctions";    
    require "$home_dir/$db_functions_module.pm";

    my $subroutine = "$db_functions_module"."::".$call_signature;
    goto &$subroutine;
}
1;
