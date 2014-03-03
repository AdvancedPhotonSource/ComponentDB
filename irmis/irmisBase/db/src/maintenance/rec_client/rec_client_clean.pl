#!/usr/bin/perl -w

#
#  Copyright (c) 2004-2005 The University of Chicago, as Operator
#  of Argonne National Laboratory.
#

# rec_client_clean.pl
#   Run periodically to clean out (delete) old unused rows in 
#   the rec_client, vuri, uri, and vuri_rel tables.
#
# Authors: Claude Saunders (saunders@aps.anl.gov)
#
#

use DBI qw(:sql_types);
use FileHandle;
use Cwd;

use Blowfish_PP;
use LogUtil;

##########################################################
#  GET SCRIPT ARGUMENTS
##########################################################
$go = 0; # don't run script unless explicitly enabled
$debug_level = "info"; # default
if (!@ARGV) {
    print_usage();
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
    } else {
        print_usage();
        exit;
    }
}
if (!$go) {
    print "You must at a minimum use --go command line option to run the script!\n";
    exit;
}

##########
#  SETUP
##########
# Can be 'verbose', 'debug', 'info', 'warn', or 'error'
LogUtil::set_log_level($debug_level);
$dbh = 0;
$db_properties_path = "./db.properties";

##########
#  MAIN
##########
# skipping LogUtil prefix for brevity from now on
log('info',"rec_client_clean beginning");
$dbh = db_connect();

# SQL to get a batch of rec_client rows to clean up
my $select_rec_client_sql = 'select rc.rec_client_id, rc.vuri_id, v.uri_id from rec_client rc, vuri v where rc.vuri_id=v.vuri_id and rc.current_load = 0 limit 1000';
my $select_rec_client_st;    

$workRows = 0;
$iteration = 0;
do {
    eval {
        # Get a batch of rec_client rows to work on
        $select_rec_client_st = $dbh->prepare($select_rec_client_sql);
        $select_rec_client_st->execute();
        $workRows = 0;
        while ($row = $select_rec_client_st->fetchrow_hashref) {
            $rec_client_id = $row->{rec_client_id};
            $vuri_id = $row->{vuri_id};
            $uri_id = $row->{uri_id};
            
            # do deletes on rec_client, vuri_rel, vuri, uri
            my $delete_rec_client_sql = "delete from rec_client where rec_client_id=$rec_client_id";
            my $delete_rec_client_st = $dbh->prepare($delete_rec_client_sql);
            $delete_rec_client_st->execute();
            eval {
                # this may fail in many cases, but that is ok
                my $delete_vuri_rel_sql = "delete from vuri_rel where child_vuri_id=$vuri_id";
                my $delete_vuri_rel_st = $dbh->prepare($delete_vuri_rel_sql);
                $delete_vuri_rel_st->execute();
            };
            eval {
                # this may fail in many cases, but that is ok
                my $delete_vuri_sql = "delete from vuri where vuri_id=$vuri_id";
                my $delete_vuri_st = $dbh->prepare($delete_vuri_sql);
                $delete_vuri_st->execute();
            };
            eval {
                # this may fail in many cases, but that is ok
                my $delete_uri_sql = "delete from uri where uri_id=$uri_id";
                my $delete_uri_st = $dbh->prepare($delete_uri_sql);
                $delete_uri_st->execute();
            };
            $dbh->commit;            
            $workRows++;
        }
    };
    if ($@) {
        LogUtil::log('error',"rec_client_clean:$@");
        $dbh->rollback;
        exit;
    }
    $iteration++;
    print "iteration $iteration\n";
} until ($workRows == 0);

log('info',"rec_client_clean done");

exit;

# Private
#########################################################################
# Establish $dbh connection to database using db.properties file.
#########################################################################
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

    if (!-e $db_properties_path) {
        LogUtil::log('error',"Unable to find db.properties file");
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


    # Make DBI connect string
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
    return $dbh;
}

#########################################################################
# Print out application usage text
#########################################################################
sub print_usage {
    print "usage: rec_client_clean.pl --go [--key=<16 char key>] [--help] [--debug-level=[verbose|debug|info]]\n\n";
    print "This script cleans out old records from the rec_client tables.\n";
    print "\\n";

    print "You must have --go command line option to run the script!\n";
}

