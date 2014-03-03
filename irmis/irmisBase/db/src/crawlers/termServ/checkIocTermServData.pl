#!/usr/bin/perl -w

use strict;
use Getopt::Std;
use Cwd;
use DBI;
use Mail::Mailer;

my %termServ;
my %fiberConvCh;
my $dbh;
my $email_body = '';
my @iocs = ();
my $uopt;

$termServ{"10.4.1.0"}  ="CTSINJB";
$termServ{"10.4.1.100"}="CTSINJA";
$termServ{"10.4.1.150"}="CTSMCR";
$termServ{"10.4.1.200"}="CTSSR02A";
$termServ{"10.4.2.0"}  ="CTSSR02B";
$termServ{"10.4.2.50"} ="CTSSR12A";
$termServ{"10.4.2.100"}="CTSRFA";
$termServ{"10.4.2.150"}="CTSSR12B";
$termServ{"10.4.2.200"}="CTSSR22A";
$termServ{"10.4.3.0"}  ="CTSSR22B";
$termServ{"10.4.3.50"} ="CTSSR32A";
$termServ{"10.4.3.100"}="CTSSR32B";

$fiberConvCh{0}="A";
$fiberConvCh{1}="B";
$fiberConvCh{2}="C";
$fiberConvCh{3}="D";

##########################################################
#  GET SCRIPT ARGUMENTS
##########################################################
my $go = 0; # don't run script unless explicitly enabled
my $command;
my $decryption_key;

if (!@ARGV) {
    print_usage();
    exit;
}
while (@ARGV) {
    $command = shift @ARGV;
    if ($command =~ /-help/) {
        print_usage();
        exit;
    } elsif ($command =~ /--help/) {
        print_usage();
        exit;
    } elsif ($command =~ /--go/) {
        $go = 1;
    } elsif ($command =~ /--key=(.*)/) {
        $decryption_key = $1;
    } else {
        push (@iocs,$command);
    }
}

if (!$go) {
    print "You must at a minimum use --go command line option to run the script!\n";
    exit;
}

##########################################################
#   Check network data and send email
##########################################################
{
  checkNetworkData(@iocs);
  send_email($email_body);
}


#=============================================================================
sub send_email {
  my $body = $_[0]."\n";
  my $from_address = "checkIocTermServData";
  #my $to_address = "nda,jba,dohan";
  #my $to_address = "jba";
  my $to_address = "nda,jba";
  my $subject = "terminal server data check";

  my $mailer = Mail::Mailer->new();
  $mailer->open({ From  => $from_address,
                To      => $to_address,
                Subject => $subject,
              }) or die "Can't open: $! \n";

  print $mailer $body;
  $mailer->close();
}


#=============================================================================
#
# Get list of IOC names with optional search specification
#
sub checkNetworkData {
  my @iocList = @_;
  my $select_line;
  my $select;

  #use lib "/usr/local/epics/extensions/bin/solaris-sparc";
  use DBI qw(:sql_types);
  use extDBLayer;

  my ($fields,$sth,$arg,$sth2,$ioc_id,$ioc_nm);
  if (!$dbh) {
    $dbh = db_connect($decryption_key);
    if (!$dbh) {
       $email_body = $email_body."ERROR: Cannot connect to database.\n";
       $email_body = $email_body."ERROR: Exiting...\n";
       exit(-1);
    }
  }

  $dbh->{RaiseError} = 1;            #raise exception on error
  $dbh->{PrintError} = 0;
  $dbh->{AutoCommit} = 0;

  if (scalar(@iocList) == 0) {

    # Get records in ioc table
    $select_line = "select ioc_nm, ";
    $select_line .= "ioc_id ";
    $select_line .= "from ioc,ioc_status ";
    $select_line .= "where ioc.ioc_status_id=ioc_status.ioc_status_id ";
    $select_line .= "and (ioc_status='production' or ioc_status='ancillary') ";
    $select = qq($select_line);
    $sth = $dbh->prepare($select);
    $sth->execute();
 
    while (my @iocrow = $sth->fetchrow_array()) {
      $ioc_nm = $iocrow[0];
      next if ( $ioc_nm =~ m/^s/ );
      $ioc_id = $iocrow[1];

      $select_line = "select ";
      $select_line .= "TermServRackNo,";
      $select_line .= "TermServName,";
      $select_line .= "TermServPort,";
      $select_line .= "TermServFiberConvCh,";
      $select_line .= "TermServFiberConvPort ";
      $select_line .= "from aps_ioc ";
      $select_line .= "where ioc_id='$ioc_id' ";
  
      $select = qq($select_line);
      $sth2 = $dbh->prepare($select);
      $sth2->execute();
 
      my @row = $sth2->fetchrow_array();
      $sth2->finish();
      if ( scalar(@row) == 0) {
        $email_body = $email_body. "Error: No $ioc_nm record in aps_ioc table\n\n";
        next;
      }
      compareIocNetworkData(0,$ioc_nm,\@row);
    }
  } else {

    foreach my $ioc_nm (@iocList) {

      if ( $ioc_nm =~ m/^s/ ){
        $email_body = $email_body. "Skipping soft ioc $ioc_nm\n";
        next;
      }

      # Get records in ioc table
      $select_line = "select ";
      $select_line .= "ioc_id ";
      $select_line .= "from ioc ";
      $select_line .= "where ioc_nm='$ioc_nm' ";
      $select = qq($select_line);
      $sth = $dbh->prepare($select);

      $sth->execute();
      my @iocrow = $sth->fetchrow_array();
      if ( scalar(@iocrow) == 0) {
        $email_body = $email_body. "Error: No data for $ioc_nm\n";
        next;
      }
      $ioc_id = $iocrow[0];

      $select_line = "select ";
      $select_line .= "TermServRackNo,";
      $select_line .= "TermServName,";
      $select_line .= "TermServPort,";
      $select_line .= "TermServFiberConvCh,";
      $select_line .= "TermServFiberConvPort ";
      $select_line .= "from aps_ioc ";
      $select_line .= "where ioc_id='$ioc_id' ";

      $select = qq($select_line);
      $sth2 = $dbh->prepare($select);
      $sth2->execute();
  
      my @row = $sth2->fetchrow_array();
      $sth2->finish();
      if ( scalar(@row) == 0) {
        $email_body = $email_body. "Error: No aps_ioc data for $ioc_nm\n\n";
        next;
      }
      compareIocNetworkData(1,$ioc_nm,\@row);
    }
  }

  $sth->finish();
#  $dbh->commit() if $uopt;
  $dbh->disconnect();

  $email_body = $email_body. "DONE\n";
}

sub compareIocNetworkData {

  my $prnt = shift;
  my $ioc = shift;
  my ($aref) = @_;
  my @netData = @$aref;
  my $termServRackNoDB='';
  my $termServNameDB='';
  my $termServPortDB='';
  my $termServFiberConvChDB='';
  my $termServFiberConvPortDB='';
  my $termServPort = '';
  my $termServName = '';
  my $termServRackNo = '';
  my $termServFiberConvPort = '';
  my $termServFiberConvCh = '';
  $termServRackNoDB=$netData[0] if $netData[0];
  $termServNameDB=$netData[1] if $netData[1];
  $termServPortDB=$netData[2] if $netData[2];
  $termServFiberConvChDB=$netData[3] if $netData[3];
  $termServFiberConvPortDB=$netData[4] if $netData[4];

  # Get terminal server data from netall command
  my @line = `netall "c$ioc "`;
  if ($#line < 2) {
    $email_body = $email_body. "Error: No netall command output for c$ioc\n";
  } else {
  my ($consoleIP) = split (/\s+/,$line[2],1);
  my ($ip1,$ip2,$ip3,$ip4,$rest) = split (/\.| /,$consoleIP);
  my $base = 50;
  $termServPort = int($ip4) % $base;
  my $floor = int($ip4/$base)*$base;
  $ip1 =~ s/\f//;
  my $termServIP = "$ip1.$ip2.$ip3.$floor";
  $termServName = $termServ{$termServIP};
  #print "$ip1.$ip2.$ip3.$ip4 base=$base termServPort=$termServPort floor=$floor termServIP=$termServIP \n";

  $base = 12;
  $floor = int(($termServPort-1)/$base);
  $termServFiberConvPort = $termServPort - $floor*$base;
  $termServFiberConvCh = $fiberConvCh{$floor};
  }

#  # Get terminal server data from ts_chassis_rack table
#  my $select_line = "select ";
#  $select_line .= "ts_chassis_room, ts_chassis_rack ";
#  $select_line .= "from net_ts_table ";
#  $select_line .= "where ts_chassis_name = '$termServName' ";
#  my $select = qq($select_line);
#  my $sth = $dbh->prepare($select);
#  $sth->execute();
#  my @row = $sth->fetchrow_array();
#  my $termServRackNo;
#  if ( scalar(@row) == 0) {
#    $email_body = $email_body. "Warning: No net_ts_table data for $termServName from $ioc\n";
#    $termServRackNo = "";
#  } else {
#    $termServRackNo = "$row[0]/$row[1]";
#  }
#    if ( $termServRackNoDB eq $termServRackNo &&

    $termServRackNoDB='' if $termServRackNoDB eq '-' ;
    $termServNameDB='' if $termServNameDB eq '-';
    $termServPortDB='' if $termServPortDB eq '-';
    $termServFiberConvChDB='' if $termServFiberConvChDB eq '-';
    $termServFiberConvPortDB='' if $termServFiberConvPortDB eq '-';

    if (
         $termServNameDB eq $termServName
      && $termServPortDB eq $termServPort
      && $termServFiberConvChDB eq $termServFiberConvCh
      && $termServFiberConvPortDB eq $termServFiberConvPort) {

      $email_body = $email_body. "No differences for $ioc\n" if $prnt;
      return;
    }

    $email_body = $email_body. "Differing Terminal Server data for $ioc\n";
    $email_body = $email_body.sprintf("%-25s Rack Number                  Server:Port    Fbr Ch:Prt\n",$ioc);
    $email_body = $email_body.sprintf("  DB    Terminal Server:      %-25s  %10s:%-2s  %3s:%-2s\n",
      $termServRackNoDB,$termServNameDB,$termServPortDB,
      $termServFiberConvChDB,$termServFiberConvPortDB);
    $email_body = $email_body.sprintf(" netall Terminal Server:      %-25s  %10s:%-2s  %3s:%-2s\n",
      $termServRackNo,$termServName,$termServPort,
      $termServFiberConvCh,$termServFiberConvPort);
    $email_body = $email_body.sprintf("\n");


#  if ( $uopt ) {

#    my $update_line = "UPDATE ioc_table ";
#    $update_line .= "set ";
#    $update_line .= "TermServRackNo='$termServRackNo', ";
#    $update_line .= "TermServName='$termServName', ";
#    $update_line .= "TermServPort='$termServPort', ";
#    $update_line .= "TermServFiberConvCh='$termServFiberConvCh', ";
#    $update_line .= "TermServFiberConvPort='$termServFiberConvPort' ";
#    $update_line .= "where ioc_name='$ioc' ";
#    my $update = qq($update_line);
#  
#    eval
#    {
#      $sth = $dbh->prepare($update);
#      $sth->execute();
#    };
#    if ($@) {
#      $dbh->rollback;
#      $email_body = $email_body. "Error: update net_ts_table for $ioc failed: $@";
#      return;
#    }
#  }

}

#########################################################################
# Print out application usage text
#########################################################################
sub print_usage {
    print "usage: checkIocTermServData --go [--key=<16 char key>] [--help] [<ioc1> <ioc2> ...]\n";
    print "\n";
    print "Compare network data for specified IOC name(s) or\n";
    print "for all iocs if no iocnames are specified.\n";
    print "\n";
    print "Output goes to email messages which are sent to nda and jba.\n";
    print "\n";
    print "The --key option specifies the blowfish decryption key .\n";
    print "\n";
    print "You must have --go command line option to run the script!\n";
}

