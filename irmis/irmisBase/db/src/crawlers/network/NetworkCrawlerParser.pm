package NetworkCrawlerParser;

# This package provides routines for writing ioc network interface data
# to a MySQL database by parsing a nightly updated network data file. 

use DBI qw(:sql_types);
require Exporter;

use Mail::Mailer;
use File::stat;
use Cwd;

use LogUtil;

our @ISA = qw(Exporter);
our @EXPORT = qw( &email_log &send_email &get_modified_network_data 
    &get_all_network_data);
our @EXPORT_OK = qw();
our $VERSION = 1.00;
our %hostHash = ();
our $bootparams="/usr/local/iocapps/iocinfo/bootparams";

$email_body="";


#=============================================================================
# Public
sub get_modified_network_data {
  my ($aref) = $_[0];
  my @infiles = @$aref;
  my $network_hits_ref = $_[1];
  my $networkData_ref = $_[2];

  my $ioc_id;
  my $ioc_row;
  my $select_line;
  my $line;
  my $iocEtypeHold = '';
  my %seen = ();

  my %networkHash = %$networkData_ref;

  createHostHash();

  my $updateCount = 0;
  foreach $infile (@infiles) {
  
    if ( ! open(INFILE,$infile) ) {
      email_log('error',"Cannot open network data file $infile: $@");   
      return -1;
    }
    email_log('info',"Processing network data file $infile.");   
  
    while($line = <INFILE>)
    {
      email_log('verbose',"Input line: $line");   
      $line =~ s/\015?\012//; # remove CR LF  characters
      next if $line =~ m/^\s*$/x;    # Skip blank lines
  
      ($switch,$bladePort,$iocEtype,$connectStatus) = split(/\s+/,$line);
  
      # Use only lines with network data for iocs
      if ( !defined $switch || $switch =~ m/^Switch/ ||
           !defined $connectStatus || !defined $iocEtype ||
           $iocEtype =~ m/^hub/ || $iocEtype =~ m/^sw/ ||
           $iocEtype eq "up" || $iocEtype eq "down" ||
           $iocEtype eq "connected" || $iocEtype eq "notconnect") {
        email_log('verbose',"Skipping: $line");
        next;
      }
  
      if ( $bladePort =~ /\// ) {
        ($blade,@ports) = split(/\//,$bladePort);
      } else {
         $blade=substr($bladePort,0,1);
         if ($blade =~ m/[0-9]/ ) {
           $blade='';
           $ports[0]=$bladePort;
         } else{
           $ports[0]=substr($bladePort,1,length($bladePort)-1);
         }
      }
      $port=$ports[$#ports];
      $bladePort=$blade.'/'.$ports[$#ports];
  
      # Process soft ioc host data 
      if ($iocEtype !~ m/^ioc/ ) {
        $host = $iocEtype;
        my $softiocList=$hostHash{$host};
        if ( $softiocList ) {
          foreach $ioc (@$softiocList) {
  
            $iocEtype = $ioc."-p";
            # next if ioc from file not in database
            if ( !exists($networkHash{$iocEtype}) ) {
                email_log('info',"Unknown ioc:   $ioc-$Etype OLD:           NEW:$switch:$bladePort");   
                next;
            }
            @ioc_networkDB = @{$networkHash{$iocEtype}};
            $switchDB = "";
            $bladePortDB = "";
            $switchDB = $ioc_networkDB[0] if defined $ioc_networkDB[0];
            $bladePortDB = $ioc_networkDB[1] if defined $ioc_networkDB[1];
            if ( $switchDB ne $switch || $bladePortDB ne $bladePort ) {
              #($blade,$port) = split(/\//,$bladePort);
              $Etype = "p";
              push(@$network_hits_ref,[$ioc,$Etype,$switch,$blade,$port]);   
              $updateCount++;
              email_log('info',"Modified data: $ioc-$Etype OLD: $switchDB:$bladePortDB NEW:$switch:$bladePort");   
            }
            $seen{$iocEtype} = 1;
          }
          next;
        } else {
          email_log('verbose',"Skipping: $line");
          next;
        }
      }
  
      $oneInterface = 0;
      if (substr($iocEtype,length($iocEtype)-2,1) ne "-") {
            $oneInterface = 1;
            $ioc=$iocEtype;
            $Etype = "p";
            $iocEtype = $ioc."-p";
  
      } else {
            # Get ioc name
            # Cant use split because ioc may have embedded dashes
            $ioc = substr($iocEtype,0,length($iocEtype)-2);
            $Etype = substr($iocEtype,length($iocEtype)-1,1);
            # Use only network data for iocs primary/secondary interfaces
            if ( $Etype !~ m/s/ &&  $Etype !~ m/p/ )  {
                  email_log('verbose',"Skipping: $line");
                  next;
            }
      }
  
  
      # next if ioc from file not in database
      if ( !exists($networkHash{$iocEtype}) ) {
          email_log('info',"Unknown ioc:   $ioc-$Etype OLD:           NEW:$switch:$bladePort");   
          next;
      }
  
      @ioc_networkDB = @{$networkHash{$iocEtype}};
      $switchDB = $ioc_networkDB[0];
      $bladePortDB = $ioc_networkDB[1];
  
      # next if duplicate iocEtype in file
      if ( exists($seen{$iocEtype}) ) {
        email_log('info',"Duplicate ???: $ioc-$Etype OLD: $switchDB:$bladePortDB NEW:$switch:$bladePort");   
        next;
      } 
    
      # Update network_hits and count if file data differs from database
      # and if not a hand entered DB data ioc
      if ( ($switchDB ne $switch || $bladePortDB ne $bladePort) && $switchDB !~ m/^No / ) {
        push(@$network_hits_ref,[$ioc,$Etype,$switch,$blade,$port]);   
        $updateCount++;
        email_log('info',"Modified data: $ioc-$Etype OLD: $switchDB:$bladePortDB NEW:$switch:$bladePort");   
      }
      $seen{$iocEtype} = 1;


      # If ioc has only one interface, zero out the secondary interface values
      if ($oneInterface) {
            $Etype = "s";
            $iocEtype = $ioc."-s";
            $switch = "";
            $blade = "";
            $port = "0";
            $bladePort = $blade.'/'.$port;
  
            # next if ioc from file not in database
            if ( !exists($networkHash{$iocEtype}) ) {
                email_log('info',"Unknown ioc:   $ioc-$Etype OLD:           NEW:$switch:$bladePort");   
                next;
            }
  
            @ioc_networkDB = @{$networkHash{$iocEtype}};
            $switchDB = $ioc_networkDB[0];
            $bladePortDB = $ioc_networkDB[1];
  
            # next if duplicate iocEtype in file
            if ( exists($seen{$iocEtype}) ) {
              email_log('info',"Duplicate ???: $ioc-$Etype OLD: $switchDB:$bladePortDB NEW:$switch:$bladePort");   
              next;
            } 
    
            # Update network_hits and count if file data differs from database
            # and if not a hand entered DB data ioc
            if ( ($switchDB ne $switch || $bladePortDB ne $bladePort) && $switchDB !~ m/^No / ) {
              push(@$network_hits_ref,[$ioc,$Etype,$switch,$blade,$port]);   
              $updateCount++;
              email_log('info',"Modified data: $ioc-$Etype OLD: $switchDB:$bladePortDB NEW:$switch:$bladePort");   
            }
            $seen{$iocEtype} = 1;
      }
    }
  
    close(INFILE);
  }
  # Output message if network data missing from file
  # Zero out iocEtype network data in database if iocEtype network data missing from file
  foreach $iocEtype (keys %networkHash) {
    if ( !exists($seen{$iocEtype}) ) {
      @ioc_networkDB = @{$networkHash{$iocEtype}};
      $switchDB = defined($ioc_networkDB[0]) ? $ioc_networkDB[0] :"";
      $bladePortDB = defined($ioc_networkDB[1]) ? $ioc_networkDB[1] :"";
      $ioc = substr($iocEtype,0,length($iocEtype)-2);
      $Etype = substr($iocEtype,length($iocEtype)-1,1);
      # Update network_hits and count if database data differs from null entry
      # and if not a hand entered DB data ioc
      if ( ($switchDB ne "" || $bladePortDB ne "/0") && $switchDB !~ m/^No / ) {
          push(@$network_hits_ref,[$ioc,$Etype,'','',0]);   
          $updateCount++;
      }
      email_log('warn',"No file data:  $iocEtype OLD: $switchDB:$bladePortDB NEW: no data");   
    }
  }

  return $updateCount;
}

#=============================================================================
# Public
sub get_all_network_data {
  my ($aref) = $_[0];
  my @infiles = @$aref;
  my $network_hits_ref = $_[1];
  my $ioc;
  my $host;

  email_log('info',"Replacing ALL ioc network data.");   

  foreach $infile (@infiles) {
  
    if ( ! open(INFILE,$infile) ) {
      email_log('error',"Cannot open $infile: $@");   
      return -1;
    }
  
    createHostHash();
  
    my $line;
    while($line = <INFILE>)
    {
      email_log('verbose',"Input: $line");   
      $line =~ s/\015?\012//; # remove CR LF  characters
      next if $line =~ m/^\s*$/x;    # Skip blank lines
  
      ($switch,$bladePort,$iocEtype,$connectStatus) = split(/\s+/,$line);
  
      if ( !defined $switch || $switch =~ m/^Switch/ ||
           !defined $connectStatus || $bladePort =~ m/^---/  ||
           $iocEtype eq "connected" || $iocEtype eq "notconnect") {
        email_log('verbose',"Skipping: $line");   
        next;
      }
  
      ($blade,@ports) = split(/\//,$bladePort);
      $port=$ports[$#ports];
      $bladePort=$blade.'/'.$ports[$#ports];
  
      # Get ioc name from input file
      # Cant use split because ioc may have embedded dashes
      if (substr($iocEtype,length($iocEtype)-2,1) ne "-") {
  
        $host = $iocEtype;
        my $softiocList=$hostHash{$host};
        if ( $softiocList ) {
          foreach $softioc (@$softiocList) {
            #print "host=$host ioc=$softioc,'p',switch=$switch,blade=$blade,port=$port \n";   
            push(@$network_hits_ref,[$softioc,'p',$switch,$blade,$port]);
            email_log('debug',"Network data: $softioc-p NEW:$switch:$bladePort");   
              next;
          }
          next;
        } else {
          email_log('verbose',"Skipping: $line");   
          next;
        }
  
      }
      $host = substr($iocEtype,0,length($iocEtype)-2);
      $Etype = substr($iocEtype,length($iocEtype)-1,1);
  
      if ( $host =~ m/^hub/  || !defined $Etype  )  {
        email_log('verbose',"Skipping: $line");   
        next;
      }
  
      $ioc = $host;
  
      if ( $Etype !~ m/s/ &&  $Etype !~ m/p/ )  {
          email_log('verbose',"Skipping: $line");   
          next;
      }
  
      # May have to add ioc prefix
      next if  $ioc !~ m/^ioc/;
  #    if ( $ioc !~ m/^ioc/ ) {
  #      email_log('warn',"Prefix `ioc` was added to $ioc.");   
  #      $ioc =~ s/^/ioc/ 
  #    }
  
      push(@$network_hits_ref,[$ioc,$Etype,$switch,$blade,$port]);
      email_log('debug',"Network data: $ioc-$Etype NEW:$switch:$bladePort");   
    }
    close(INFILE);
  }
  return 0;
}


#=============================================================================
# Create a hash of hostnames containing a list of softiocnames on the host.
  
# Private
sub createHostHash {
  my $filename;
  my $hostname;
  my $softioc;

  push (@filenameList, glob("$bootparams/sioc*"));
  foreach $filename (@filenameList) {
    $hostname = getHostname($filename);
    next if $hostname eq '';
    my $softioc=$filename;
    $softioc =~ s/^.*\///;
    push(@$hostname,$softioc);
    $hostHash{$hostname}= \@$hostname if !exists $hostHash{$hostname};
  }
}


#=============================================================================
# Get host name for a soft ioc name from bootparams file.
# Private
sub getHostname {
  my ($iocname) = @_;
  local *IN;
  my $line;
  my $item;
  my ($name,$value);
  $iocname =~ s/^.*\///;
  return if $iocname !~ m/sioc/;
  if (!open(IN, "$bootparams/$iocname") ) {
    print "   Can't open bootparams file $iocname: $!\n";
    return "";
  }
  while ($line = <IN>) {
    chomp($line);
    (@chunks) = split (/ |,|\(|\)/,$line);
    foreach $item (@chunks) {
      ($name,$value) = split (/=/,$item);
       $value =~ s/\..*//;
       if ( $name eq "hn" ) {
        return $value;
       }
    }
  }
  return "";
}

#=============================================================================
# Public
sub send_email {
  my $body = $email_body."\n";
  my $from_address = "updateIocNetworkData";
  my $to_address = "jba";
#  my $to_address = "nda,jba";
  my $subject = "ioc network data updates";

  $mailer = Mail::Mailer->new();
  $mailer->open({ From	=> $from_address,
                To	=> $to_address,
                Subject	=> $subject,
              }) or die "Can't open: $! \n";

  print $mailer $body;
  $mailer->close();
}

#=============================================================================
# Public
sub email_log {
  if ( $_[0] ne 'debug' && $_[0] ne 'verbose' ) {
    $email_body = $email_body.uc($_[0]).": ".$_[1]."\n";
  }
  LogUtil::log($_[0],$_[1]);
}

#=============================================================================
1;

