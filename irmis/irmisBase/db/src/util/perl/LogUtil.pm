package LogUtil;

# This package provides subroutines for logging crawler progress.
#

require Exporter;
use POSIX;

our @ISA = qw(Exporter);
our @EXPORT = qw(log set_log_level);
our @EXPORT_OK = qw();
our $VERSION = 1.00;

# Levels map
my %log_level_map = (verbose=>0, debug=>1, info=>2, warning=>3, warn=>3, error=>4);
my %terse_level_map = (verbose=>"V",debug=>"D",info=>"I",warning=>"W",warn=>"W",error=>"E");

# Default logging level 
my $log_level = 2;

# Log a message with given log level
# Public
sub log {
    my $type = $_[0];
    my $message = $_[1];

    my $request_level = $log_level_map{$type};

    if ($request_level >= $log_level) {
        $time_stamp = format_date(time());
        chomp($message);
        print $terse_level_map{$type} . "|" . $time_stamp . "|" . $message . "\n";
    }

}

# Request logging of this type and all higher log levels
# Public
sub set_log_level {
    my $type = $_[0];

    $log_level = $log_level_map{$type};
}

# Takes seconds after epoch and converts to string with "YYYYMMDDHHMMSS"
# Private
sub format_date {
    my $secsAfter1970 = $_[0];

    my @tmArr = localtime($secsAfter1970);

    my $formatStr = '%Y%m%d%H%M%S';

    my $formatDate = POSIX::strftime($formatStr, $tmArr[0],$tmArr[1],$tmArr[2],
                                     $tmArr[3], $tmArr[4], $tmArr[5]);

    return $formatDate;
}

1;
