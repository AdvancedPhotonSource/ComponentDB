
package MySQLDBFunctions;

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

# Return an id that is to be used for the subsequent insert into the given table.
# For MySQL, we return nothing, since we use the auto-increment feature instead.
sub pre_insert_get_id {
    my $dbh = $_[0];
    my $table_name = $_[1];

    # ignore args, and return null (which for mysql means auto-increment)
    return;
}


sub post_insert_get_id {
    my $dbh = $_[0];
    my $table_name = $_[1];

    # Not doing it this way anymore. Very slow with big tables, so it must
    # be doing a full table scan. 
    # my $insert_id = get_last_insert_id($dbh, $table_name);
    #

    # This is fast, so it must be stored in driver as returned from insert.
    # But, alas, this is not part of DBI interface.
    my $insert_id = $dbh->{'mysql_insertid'};

    return $insert_id
}

sub get_dbi_dsn {
    my $db_database = $_[0];
    my $db_host = $_[1];

    return "DBI:mysql:$db_database:$db_host";
}

sub initialize_session {
    my $dbh = $_[0];

    return;
}

sub get_null_timestamp {
    return '0000-00-00';
}

sub get_current_date_time {
    return 'now()';
}

sub get_format_date_sql {
    my $column_name = $_[0];

    my $format_sql = 'date_format(' .$column_name. ',\'%Y%m%d%H%i%s\')';

    return $format_sql;
}

# Takes db handle and table name, and gets last insert id for current xact
# Private
sub get_last_insert_id {
    my $dbh = $_[0];
    my $table_name = $_[1];

    my $last_insert_id;
    eval {
        my $select_last_id_sql = 'select last_insert_id() from ' . $table_name;
        my $select_last_id_st = $dbh->prepare($select_last_id_sql);
        $select_last_id_st->execute();
        
        if (my $row = $select_last_id_st->fetchrow_hashref(NAME_lc)) {
            my $key = 'last_insert_id()';
            $last_insert_id = $row->{$key};
        }
    };
    if ($@) {
        LogUtil::log('error',"get_last_insert_id():$@");
        exit;
    }
    return $last_insert_id;
}

1;
