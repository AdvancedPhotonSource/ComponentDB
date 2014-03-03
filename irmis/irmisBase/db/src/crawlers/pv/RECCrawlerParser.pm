
package RECCrawlerParser;

# This package provides subroutines for parsing ioc startup command
# file and db file lines for rec specification comments and creating
# a hash of epics record name and criticality_level pairs for epics
# records which have a rec specified criticality value.

use strict;
require Exporter;

use CommonUtil qw(mail);
use LogUtil qw(log);

our @ISA = qw(Exporter);
our @EXPORT_OK = qw(initialize_rec_parser get_rec_value_hash parse_startup_command_line_rec finished_parse_command_rec parse_db_comment_rec add_db_record_rec );
our $VERSION = 1.00;

my $criticality='1';            # stores latest value from #<rec comment line
my $criticality_db;             # stores latest value from #<rec comment line in a db file
my @criticality_list;           # stores list of criticality values
my %rec_value_hash;             # stores rec_nm,criticality pairs
my $rec_parser_initialized = 0;

#***********
# Public sub
#***********
# Initialize rec parser MUST be called for each ioc before parse_command.
sub initialize_rec_parser {

    # CLEANUP possibly large lists
    undef(%rec_value_hash);
    undef(@criticality_list);

    # initialize module globals
    @criticality_list = ();
    $criticality = 1;

    $rec_parser_initialized = 1; # good to go

    log('debug',"REC parser initialized.");
}

#***********
# Public sub
#***********
sub get_rec_value_hash {
    return \%rec_value_hash;
}


#***********
# Public sub
#***********
# Parse a startup command line comment for '# <rec ... >'
sub parse_startup_command_line_rec {
    my $script_path = $_[0];
    my $script_line = $_[1];
    my $parse_err = 0;

    if (!$rec_parser_initialized) {
        log('error',"Using REC parser before initializing. Exiting...");
        exit;
    }

    log('debug',"REC parser: Parsing startup command comment: $script_line");

    return $parse_err if ($script_line =~ /^\s*$/);   # Skip blank lines

    $script_line =~ s/^\s+//; # Remove leading white space
    $script_line =~ s/\s+$//; # Remove trailing white space

    # look for rec comment lines
    if ( $script_line =~ m/^#\s*<\s*rec/  || $script_line =~ m/^#\s*<\s*\/\s*rec/ )
    {
        log('debug',"REC: parse comment: $script_line");
        $parse_err = parse_rec_comment($script_path,$script_line);
    }

    return $parse_err;
}


#***********
# Public sub
#***********
# Parse a db file comment line for a #rec comment
sub parse_db_comment_rec {
    my $script_path = $_[0];
    my $script_line = $_[1];
    my $parse_err = 0;
    my $value;

    log('debug',"REC: Parsing db comment: $script_line");

    return $parse_err if ($script_line =~ /^\s*$/);   # Skip blank lines

    $script_line =~ s/^\s+//; # Remove leading white space
    $script_line =~ s/\s+$//; # Remove trailing white space

    # look for rec comment lines
    if ( $script_line =~ m/^#\s*<\s*rec/ ) {

        log('debug',"REC: parse db comment: $script_line");

        # parse out criticality value for next record
        # this will overwrite any previous criticality value from db file
        $value = $script_line;
        $value =~ s/^.*criticality\s*=\s*//;
        $value =~ s/\s.*$//;

        if ($value!='0' && $value!='1' ){
            log('error',"Ignoring unknown criticality value, $value, in line $script_line from $script_path");
            mail("Ignoring unknown criticality value, $value, in line $script_line from $script_path");
            $parse_err = 1;
        } else {
            $criticality_db = $value;
        }
    }

    return $parse_err;
}


#***********
# Private 
#***********
# Parse rec comment line
# Line assumed to be of form "#<rec ...>" or "#</rec>"
sub parse_rec_comment {
    my $script_path = $_[0];
    my $script_line = $_[1];
    my $parse_err = 0;
    my $rec_name;
    my $pv_filter_string;
    my $value;

    log('debug',"REC parser: parsing rec comment: $script_line in $script_path."); 

    if (!$rec_parser_initialized) {
        log('error',"Using REC parser before initializing. Exiting...");
        exit;
    }

    if ( $script_line =~ m/^#\s*<\s*rec/ ) {

        # parse out criticality value
        $value = $script_line;
        $value =~ s/^.*criticality\s*=\s*//;
        $value =~ s/\s*>.*$//;

        # update current criticality value
        if ($value!='0' && $value!='1' ){
            log('error',"Ignoring unknown criticality value, $value, in line $script_line from $script_path");
            mail("Ignoring unknown criticality value, $value, in line $script_line from $script_path");
            $parse_err = 1; 
        } else {
            push(@criticality_list, $criticality);
            log('debug',"push $criticality New criticality=$value");
            $criticality = $value;
        }
    } elsif ( $script_line =~ m/^#\s*<\s*\/\s*rec\s*>/ ) {
        if (scalar(@criticality_list)) {
             $criticality = pop(@criticality_list);
             log('debug',"pop to criticality=$criticality");
        } else {
            log('error',"Encountered error for \/rec comment line in $script_path.");
            mail("Encountered error for \/rec comment line in $script_path.");
            $parse_err = 1; 
        }
    } else {
        log('error',"Unable to parse rec comment line: $script_line in $script_path");
        mail("Unable to parse rec comment line: $script_line in $script_path");
        $parse_err = 1; 
    }
    return $parse_err;
}


#***********
# Public sub
#***********
sub finished_parse_command_rec {

    if (scalar(@criticality_list)) {
        log('error',"Error rec's still capturing after read of st.cmd: $@");
        mail("Error rec's still capturing after read of st.cmd: $@");
        return 1;
    }

    log('debug',"REC parser: Finished parsing commands.");
    return 0;
}


#***********
# Public sub
#***********
sub add_db_record_rec {
    my $rnam = $_[0];

    if (defined $criticality_db ) {
        # criticality=1 is db table default
        $rec_value_hash{$rnam}=$criticality_db if $criticality_db=='0';
        log('debug',"criticality=$criticality_db record_name=$rnam")  if $criticality_db=='0';
        # REC criticality in db file applys only to one record
        undef $criticality_db;
    } else {
        # criticality=1 is db table default
        $rec_value_hash{$rnam}=$criticality if $criticality=='0';
        log('debug',"criticality=$criticality record_name=$rnam")  if $criticality=='0';
    }
    return;
}

1;
