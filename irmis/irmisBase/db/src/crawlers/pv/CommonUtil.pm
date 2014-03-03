
package CommonUtil;

# This package provides subroutines use by multiple pv_crawler modules.

use DBI qw(:sql_types);
use Mail::Mailer;
require Exporter;
use LogUtil qw(log);
use Blowfish_PP;

our @ISA = qw(Exporter);
our @EXPORT_OK = qw(set_test_mode get_test_mode get_db_vendor get_home_dir set_home_dir set_decryption_key db_connect ioc_load_by_name mail_initialize mail mail_send);
our $VERSION = 1.00;

my $test_mode = 0;  # if 1, do no db access
my $db_vendor; # established on first db_connect
my $home_dir = ".";  # home dir from which parent crawler is run
my $decryption_key; # optional 16 byte blowfish key
my $db_properties_path = "db.properties";
my $dbh;

my $mail_message = '';
my $mail_send_status = 0;

# Public
sub set_test_mode {
    $test_mode = $_[0];
}

# Public
sub get_test_mode {
    return $test_mode;
}

# Public
# Gets the db_vendor
sub get_db_vendor {
    return $db_vendor;
}

# Public
# Gets the directory where db.properties file can be found. 
sub get_home_dir {
    return $home_dir;
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
sub db_connect {

    #connect to database
    #####my $dbh;
    return $dbh if $dbh;

    # Look up connection parameters in external db.properties file.
    # If a decryption key has been supplied, use blowfish algorithm
    # to decrypt the contents of the file.
    
    my $db_host;
    my $db_database;
    my $db_user;
    my $db_password;

    if (!-e $db_properties_path) {
        log('error',"Unable to find db.properties file: $db_properties_path");
        log('error',"Exiting...");
        exit;
    }

    # Read in contents of db.properties (decrypting if key defined)
    my $blowfish_object;
    if ($decryption_key) {
        my $packed_key = pack("a16",$decryption_key);
        $blowfish_object = Crypt::Blowfish_PP->new($packed_key);
        if (!$blowfish_object) {
            log('error',"The given blowfish key appears to be invalid");
            log('error',"Exiting...");
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
        log('error',"Unable to read db connection parameters from db.properties");
        log('error',"Exiting...");
        exit;
    }


    # Get db vendor-specific DBI connect string
    my $dsn = get_dbi_dsn($db_database, $db_host);

    eval {
        $dbh = DBI->connect($dsn,$db_user,$db_password,
                            {AutoCommit=>0, RaiseError=>1, PrintError=>0, 
                             FetchHashKeyName=>'NAME_lc'});
    };
    if ($@) {
        log('error',"Unable to connect to db $@");
        log('error',"Exiting...");
        exit;
    }
    # Do any db vendor-specific db session initialization
    initialize_session($dbh);

    return $dbh;
}

# Load a single ioc record given the ioc name. Returns ioc_id.
# Public
sub ioc_load_by_name {
    my $ioc_nm = $_[0];

    my $ioc_id = 0;

    log('debug',"PVCrawlerDBLayer: begin ioc_load_by_name -> $ioc_nm");

    if (!$dbh) {
        $dbh = db_connect();
    }

    my $select_ioc_sql = 'select ioc_id from ioc where ioc_nm = ?';
    my $select_ioc_st;    
    eval {
        $select_ioc_st = $dbh->prepare($select_ioc_sql);
        $select_ioc_st->bind_param(1,$ioc_nm,SQL_VARCHAR);
        $select_ioc_st->execute();
    };
    if ($@) {
        log('error',"ioc_load_by_name():$@");
        exit;
    }

    if (my $ioc_row = $select_ioc_st->fetchrow_hashref) {
        $ioc_id = $ioc_row->{ioc_id};
    } 

    return $ioc_id;
}

#=============================================================================
#Public
sub mail_initialize {
  $mail_send_status = $_[0];
  $mail_message = '';
}

#Public
sub mail {
  return if !$mail_send_status;
  # Append line feed at end of message
  $mail_message = $mail_message.$_[0].'
';
}

#Public
sub mail_send {
  my $ioc_nm= $_[0];
  my $mail_info= $_[1];
  my $body;
  my $to_address='';

  return if !$mail_send_status || $mail_message eq '';

  if ( $mail_info->{to_address} eq '' ) {
    log('error',"NO cognisant developer mail address for $ioc_nm.");
  }

#$to_address = $mail_info->{to_address}.',jba,quock';
#$to_address = 'jba,quock';
#$to_address = 'jba';
$to_address = $mail_info->{to_address};

  return if $to_address eq '';

  my $from_address = "pv_crawler";
  my $subject = $ioc_nm." pv_crawler errors";
  my $mailer = Mail::Mailer->new();
  if ( ! $mailer->open({ From  => $from_address,
                To      => $to_address,
                Subject => $subject,
              })  ) {
    log('error',"Can't open mailer: $!");
    return;
  }
  $body="You are receiving this email because you are the cognizant developer for IOC
'$ioc_nm'. Errors reported from the IRMIS pv_crawler for '$ioc_nm' follow:

".$mail_message.$mail_info->{footer};
  
  print $mailer $body;
  $mailer->close() or log('error',"Can't send mail: $!");
  log('info',"Error messages emailed to $to_address");
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
    my $db_functions_module = $db_vendor . "DBFunctions";
    require "$home_dir/$db_functions_module.pm";

    my $subroutine = "$db_functions_module"."::".$call_signature;
    goto &$subroutine;
}

1;

