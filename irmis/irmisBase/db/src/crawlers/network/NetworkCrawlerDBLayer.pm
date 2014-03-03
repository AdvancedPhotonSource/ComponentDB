package NetworkCrawlerDBLayer;

# This package provides routines for writing ioc interface network 
# data from accel_sw file to a MySQL database. 

use DBI qw(:sql_types);
require Exporter;
use LogUtil;
use Blowfish_PP;
use File::stat;

use NetworkCrawlerParser;

our @ISA = qw(Exporter);
our @EXPORT = qw( &set_test_mode &set_home_dir &set_decryption_key
                  &zero_out_network_data &load_new_network_data
                  &get_db_network_data );
our @EXPORT_OK = qw();
our $VERSION = 1.00;

my $test_mode = 0;  # if 1, do no db access
my $home_dir = "."; # dir from which parent crawler is run
my $db_properties_path = "db.properties";
my $dbh;
my $db_vendor; # established on first db_connect
my $decryption_key; # optional 16 byte blowfish key
my $update_table="aps_ioc";

#=============================================================================
# Public
sub set_test_mode {
    $test_mode = $_[0];
}

#=============================================================================
# Public
# Sets the directory where db.properties file can be found. 
sub set_home_dir {
    $home_dir = $_[0];

    $db_properties_path = $home_dir . "/db.properties";
}

#=============================================================================
# Public
# Sets the 16 char blowfish decryption key. If this is defined, this module
# will assume the db.properties file is encrypted with this key.
sub set_decryption_key {
    $decryption_key = $_[0];
}

#=============================================================================
sub db_connect {

    #connect to database
    my $dsn;
    my $dbh;

    # Look up connection parameters in external db.properties file.
    # If a decryption key has been supplied, use blowfish algorithm
    # to decrypt the contents of the file.
    
    my $db_host;
    my $db_database;
    my $db_user;
    my $db_password;

    if (!-e $db_properties_path) {
        email_log('error',"Unable to find db.properties file");
        return -1;
    }

    # Read in contents of db.properties (decrypting if key defined)
    my $blowfish_object;
    if ($decryption_key) {
        my $packed_key = pack("a16",$decryption_key);
        $blowfish_object = Crypt::Blowfish_PP->new($packed_key);
        if (!$blowfish_object) {
            email_log('error',"The given blowfish key appears to be invalid");
            return -1;
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
        email_log('error',"Unable to read db connection parameters from db.properties");
        return -1;
    }


    eval {
        $dsn =  "DBI:mysql:$db_database:$db_host";

        $dbh = DBI->connect($dsn,$db_user,$db_password,
                            {AutoCommit=>0, RaiseError=>1, PrintError=>0, 
                             FetchHashKeyName=>'NAME_lc'});
    };
    if ($@) {
        email_log('error',"Unable to connect to db $@");
        return -1;
    }
    return $dbh;
}

#=============================================================================
############################################
############################################
#  WARNING: ALL switch data is zeroed out.
#  Even when PrimEnetSwitch or SecEnetSwitch
#  is set to 'No Permanent Connection'
############################################
############################################
# Public
sub zero_out_network_data {
  my $ioc_id = $_[0];

  my $update_line;
  my $update;
  my $sth;

  email_log('info',"Zeroing out ALL ioc network data.");

  if ($test_mode) {
    email_log('info',"Test mode. No database changes made.");
    return 0;
  }

  if (!$dbh) {
    $dbh = db_connect();
    if ($dbh <= 0) {
      email_log('error',"Cannot connect to database.");
      return -1;
    }
  }

  # Update network data for ALL iocs 
  $update_line = "UPDATE $update_table ";
  $update_line .= "set ";
  $update_line .= "PrimEnetSwitch='', ";
  $update_line .= "PrimEnetBlade='', ";
  $update_line .= "PrimEnetPort='0', ";
  $update_line .= "SecEnetSwitch='', ";
  $update_line .= "SecEnetBlade='', ";
  $update_line .= "SecEnetPort='0' ";

  $update = qq($update_line);

  eval {
    $sth = $dbh->prepare($update);
    $sth->execute();
    $sth->finish();

    $dbh->commit();
  };
  if($@) {
    email_log('error',"Cannot zero out network data: $@");   
    return -1;
  }

  email_log('info',"Zeroed out network data in table $update_table.");   
  return 0;
}

#=============================================================================
# Public
sub load_new_network_data {
  my $network_hits_ref = $_[0];
  my $update_count = 0;
  my $row;

  if ($test_mode) {
    email_log('info',"Test mode. No database changes made.");
    return 0;
  }

  email_log('info',"Updating database ioc network data.");   

  if (!$dbh) {
    $dbh = db_connect();
    if ($dbh <= 0) {
      email_log('error',"Cannot connect to database.");
      return -1;
    }
  }

  # Loop over array items, updating network data in database
  foreach $row (@$network_hits_ref) {
    $rtnval=update_database_record($row);
    return -1 if $rtnval == -1;   
    $update_count = $update_count + $rtnval;
  }

  email_log('info',"Number of database updates = $update_count.");

  $dbh->commit();

  return 0;
}

#=============================================================================
sub update_database_record {
  my $data= $_[0];   
  my ($ioc,$Etype,$switch,$blade,$port)= @$data;   

  my $ioc_id = 0;
  my $ioc_status;
  my $ioc_row;
  my $select_line;
  my $update_line;
  my $update_request;
  my $sth;

  if ( $Etype =~ m/p/ ) {
    $msg = sprintf("%-16s Primary Ethernet:   %10s:%3s:%-2s",
       $ioc,$switch,$blade,$port);
  }
  if ( $Etype =~ m/s/ )  {
    $msg=sprintf("%-16s Secondary Ethernet: %10s:%3s:%-2s",
       $ioc,$switch,$blade,$port);
  }
  email_log('info',"Updating record: $msg");   

  # Get ioc_id from database
  $select_line = 'select ioc.ioc_id,ioc_status from  aps_ioc,ioc,ioc_status'.
                 ' where ioc.ioc_id=aps_ioc.ioc_id'.
                 " and ioc.ioc_nm='$ioc' ".
                 ' and ioc.ioc_status_id=ioc_status.ioc_status_id';
  eval {
    $sth = $dbh->prepare($select_line);
    $sth->execute();
  };
  if($@) {
    email_log('error',"Exiting. Database select error for $ioc: $@");   
    return -1;
  }
  if ($ioc_row = $sth->fetchrow_hashref) {
    $ioc_id = $ioc_row->{ioc_id};
    $ioc_status = $ioc_row->{ioc_status};
  }
  if ($ioc_id == 0) {
    # This is not fatal. May be a bad record in new switch data.
    email_log('warning',"Update failed: cannot find ioc $ioc in database");   
    return 0;
  }
  if ( $ioc_status ne 'production' && $ioc_status ne 'ancillary' && $ioc_status ne 'development') {
    email_log('warning',"Update ignored: Ioc $ioc is not production, ancillary or development.");   
    return 0;
  }

  # Update data for the specified ioc_id 
  $update_line = "UPDATE $update_table ";
  $update_line .= "set ";

  if ( $Etype =~ m/p/ ) {
    $update_line .= "PrimEnetSwitch='$switch', ";
    $update_line .= "PrimEnetBlade='$blade', ";
    $update_line .= "PrimEnetPort='$port' ";
  }
  if ( $Etype =~ m/s/ )  {
    $update_line .= "SecEnetSwitch='$switch', ";
    $update_line .= "SecEnetBlade='$blade', ";
    $update_line .= "SecEnetPort='$port' ";
  }
  $update_line .= "where ioc_id='$ioc_id'";

  $update_request = qq($update_line);
  eval {
    $sth = $dbh->prepare($update_request);
    $sth->execute();
    $sth->finish();
  };
  if($@) {
    email_log('error',"Exiting. $update_table update failed for ioc $ioc: $@");
    return -1;
  }
  return 1;
}

#=============================================================================
# Public
sub get_db_network_data {

  my $update_line;
  my $update;
  my $sth;
  my %networkDataHash;

  email_log('info',"Started read of ioc network data from database.");

  if (!$dbh) {
    $dbh = db_connect();
    if ($dbh <= 0) {
      email_log('error',"Cannot connect to database.");
      return -1;
    }
  }

  # Get network data from database
  $select_line = "select ioc_nm,PrimEnetSwitch,PrimEnetBlade,PrimEnetPort,";
  $select_line .= "SecEnetSwitch,SecEnetBlade,SecEnetPort from aps_ioc,ioc,ioc_status ";
  $select_line .= "where ioc.ioc_id=aps_ioc.ioc_id ";
  $select_line .= "and ioc.ioc_status_id=ioc_status.ioc_status_id ";
  $select_line .= "and (ioc_status='production' or ioc_status='ancillary' or ioc_status='development' ) ";
  $select_line .= "and (system NOT like 'BL%') ";
  eval {
    $sth = $dbh->prepare($select_line);
    $sth->execute();
  };
  if($@) {
    email_log('error',"database select error for $ioc: $@");   
    return -1;
  }
  while (my $hash_ref = $sth->fetchrow_arrayref) {
    $ioc_nm = $hash_ref->[0];
    $switch = "";
    $blade = "";
    $port = "";
    $switch = $hash_ref->[1] if defined $hash_ref->[1];
    $blade = $hash_ref->[2] if defined $hash_ref->[2];
    $port = $hash_ref->[3] if defined $hash_ref->[3];
    $iocEtype = $ioc_nm."-p";
    $bladePort = $blade.'/'.$port;
    #@net_info = ($switch, $bladePort);
    #$networkDataHash{$iocEtype}= \@net_info;
    $networkDataHash{$iocEtype}= [$switch, $bladePort] ;
    if ($ioc_nm  =~ m/^ioc/ ) { # not a soft ioc
      $switch = "";
      $blade = "";
      $port = "";
      $switch = $hash_ref->[4] if defined $hash_ref->[4];
      $blade = $hash_ref->[5] if defined $hash_ref->[5];
      $port = $hash_ref->[6] if defined $hash_ref->[6];
      $iocEtype = $ioc_nm."-s";
      $bladePort = $blade.'/'.$port;
      $networkDataHash{$iocEtype}= [$switch, $bladePort] ;
    }
  }
  email_log('info',"Finished reading ioc network data from database.");   
  return \%networkDataHash;
}

#=============================================================================
1;

