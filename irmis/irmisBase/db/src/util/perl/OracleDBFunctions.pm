
package OracleDBFunctions;

# The purpose of this package is to implement subroutines which
# fullfill a contract, allowing the calling code to be database
# vendor independent (for inserts, at least).
#

use LogUtil;

require Exporter;

our @ISA = qw(Exporter);
our @EXPORT = qw(pre_insert_get_id post_insert_get_id get_dbi_dsn get_null_timestamp get_current_date_time initialize_session get_format_date_sql);
our @EXPORT_OK = qw();
our $VERSION = 1.00;

my $save_sequence_id;

# Return an id that is to be used for the subsequent insert into the given table.
# For Oracle, we assume the existence of a sequence with a name suffix which
# is the same as the table of interest. Ie. for table ioc_boot, there must
# exist a sequence named seq_ioc_boot.
sub pre_insert_get_id {
    my $dbh = $_[0];
    my $table_name = $_[1];

    my $sequence_name = "seq_" . $table_name;

    # ignore args, and return null (which for mysql means auto-increment)
    $save_sequence_id = get_next_sequence_id($dbh, $sequence_name);
    return $save_sequence_id;
}


sub post_insert_get_id {
    my $dbh = $_[0];
    my $table_name = $_[1];

    # note, we assume someone has invoked pre_insert_get_id just once prior to here
    # a bit of a hack, so be careful
    return $save_sequence_id;
}

sub get_dbi_dsn {
    my $db_database = $_[0];
    my $db_host = $_[1];

    return "DBI:Oracle:$db_database";
}

sub initialize_session {
    my $dbh = $_[0];

    $dbh->do("alter session set nls_timestamp_format='YYYY-MM-DD HH24:MI:SS:FF6'");
    return;
}

sub get_null_timestamp {
    return '';
}

sub get_current_date_time {
    return 'localtimestamp';
}

sub get_format_date_sql {
    my $column_name = $_[0];

    my $format_sql = 'to_char(' .$column_name. ',\'yyyymmddhh24miss\')';

    return $format_sql;
}

# Takes db handle and sequence name, and gets next id in sequence.
# Private
sub get_next_sequence_id {
    my $dbh = $_[0];
    my $sequence_name = $_[1];

    my $next_sequence_id;
    eval {
        my $select_sequence_sql = 'select ' . $sequence_name . '.nextval from dual';
        my $select_sequence_st = $dbh->prepare($select_sequence_sql);
        $select_sequence_st->execute();
        
        if (my $row = $select_sequence_st->fetchrow_hashref(NAME_lc)) {
            my $key = 'nextval';
            $next_sequence_id = $row->{$key};
        }
    };
    if ($@) {
        LogUtil::log('error',"get_next_sequence_id():$@");
        exit;
    }
    return $next_sequence_id;
}

1;
