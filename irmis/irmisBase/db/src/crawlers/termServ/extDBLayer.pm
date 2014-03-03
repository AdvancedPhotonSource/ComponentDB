package extDBLayer;

# This package provides routines for extensions to connect
# and disconnect from a MySQL database.

use strict;
use DBI qw(:sql_types);
require Exporter;
use Blowfish_PP;
#use File::stat;

our @ISA = qw(Exporter);
our @EXPORT = qw( db_connect db_disconnect);
our @EXPORT_OK = qw();
our $VERSION = 1.00;

#------------------------------------------------------------
# Public
# Disconnect from the database
#------------------------------------------------------------
sub db_disconnect {
  my $dbh = $_[0];

  DBI->disconnect($dbh) if $dbh;
}

#------------------------------------------------------------
# Public
# Connect to the database
#------------------------------------------------------------
sub db_connect {
    my $decryption_key = $_[0];

    #connect to database

    my $dbh;
    my $db_host;
    my $db_database;
    my $db_user;
    my $db_password;
    my $db_properties_path = "db.properties";


    #my $hostArch = $ENV{EPICS_HOST_ARCH};
    #$hostArch = `/usr/local/epics/startup/EpicsHostArch.pl` if ! $hostArch;
    #my $db_properties_path = "/usr/local/epics/extensions/bin/$hostArch/db.properties";
    #my $decryption_key="DTdSJmnRfNmCTpAO";
    my $db_vendor; # established on first db_connect

    if (!-e $db_properties_path) {
       print("ERROR: Unable to find db.properties file\n");
       print("ERROR: Exiting...\n");
        exit(-1);
    }

    # Read in contents of db.properties
    my $blowfish_object;
    if ($decryption_key) {
        my $packed_key = pack("a16",$decryption_key);
        $blowfish_object = Crypt::Blowfish_PP->new($packed_key);
        if (!$blowfish_object) {
           print("ERROR: The given blowfish key appears to be invalid\n");
           print("ERROR: Exiting...\n");
            exit(-1);
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
    my $prop_name;
    my $prop_val;
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
       print("ERROR: Unable to read db connection parameters from db.properties\n");
       print("ERROR: Exiting...\n");
        exit(-1);
    }


    # Get db vendor-specific DBI connect string
    my $dsn = "DBI:mysql:$db_database:$db_host";

    eval {
        $dbh = DBI->connect($dsn,$db_user,$db_password,
                            {AutoCommit=>0, RaiseError=>1, PrintError=>0, 
                             FetchHashKeyName=>'NAME_lc'});
    };
    if ($@) {
       print("ERROR: Unable to connect to db $@\n");
       print("ERROR: Exiting...\n");
        exit(-1);
    }

    return $dbh;
}

1;
