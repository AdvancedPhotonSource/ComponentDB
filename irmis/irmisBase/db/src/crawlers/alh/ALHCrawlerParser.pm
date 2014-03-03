package ALHCrawlerParser;

# This package provides routines for parsing alh config files for alh_crawler
#

use FileHandle;
use File::Basename;
use Cwd;

use LogUtil;

require Exporter;

our @ISA = qw(Exporter);
our @EXPORT = qw(&parse_alhfile);
our @EXPORT_OK = qw();
our $VERSION = 1.00;

######################################
# parse alh config files files keeping
# pv references for DB update
# Public
######################################
sub parse_alhfile {
    my ($seen_ref,$alh_hits_ref,$file) = @_;
    my $name;
    my ($pvname,$id,$parent);
    my $alhModTime = 0;
    my $currentDir;

    log('info',"Parsing alh file=$file ");

    if ( $seen_ref->{$file} ) { return; }

    if (!open(IN, $file) ) {
        log('warn', "Can't open $file: $!");
        return;
    }

    $seen_ref->{$file}=1;

    my @pv_list = ();

    while (my $alhLine = <IN>) {
      
        $alhLine =~ s/^\s+//;  # Remove leading whitespace
        # Look for lines  CHANNEL parentName ChannelName <mask> 
        if( $alhLine =~ m/^CHANNEL/) {
            ($id,$parent,$pvname) = split (/\s+/,$alhLine);

            if ( $pvname =~ /\$\(/ || $pvname =~ /\$\{/ ) {   #### )}
                log('warn',"Macro present in CHANNEL $pvname.");
            } else {
                push (@pv_list,[$pvname]);
                log('verbose', "parse_alhfile: Adding: $id  $pvname");
            }
        }
        if( $alhLine =~ m/^\$HEARTBEATPV/ || $alhLine =~ m/^\$ACKPV/ ||
             $alhLine =~ m/^\$FORCEPV/ || $alhLine =~ m/^\$SEVRPV/ ) {
            ($id,$pvname) = split (/\s+/,$alhLine);
            if ( ! $pvname ) {
                log('warning', "No pvname: $alhLine");
                next;
            }
            if ( $pvname =~ /\$\(/ || $pvname =~ /\$\{/ ) {   #### )}
                log('warn',"Macro present in CHANNEL $pvname.");
            } else {
                push (@pv_list,[$pvname]);
                log('verbose', "Adding: $id  $pvname");
            }
        }
    }
    push (@$alh_hits_ref,[$file,\@pv_list]);
    $count=scalar(@pv_list);
    log('debug', "Finished parsing $file  pv_count=$count");
    close (IN);
}

1;


