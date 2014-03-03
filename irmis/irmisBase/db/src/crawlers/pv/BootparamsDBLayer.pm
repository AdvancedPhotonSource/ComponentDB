
package BootparamsDBLayer;

# This package provides subroutines for parsing data from the bootparams file
# and writing the data to a relational database. 

use strict;
use DBI qw(:sql_types);
use File::Basename; 
require Exporter;

use LogUtil qw(log);
use CommonUtil qw(db_connect get_test_mode ioc_load_by_name);

our @ISA = qw(Exporter);
our @EXPORT = qw(update_bootparams);
our @EXPORT_OK = qw();
our $VERSION = 1.00;


#***********
# Public
#***********
# Load the bootparam values into the db rec table
sub update_bootparams {
    my $ioc_boot_info = $_[0];

    my $ioc_nm = $ioc_boot_info->{ioc_nm};
    my $line =  $ioc_boot_info->{boot_line};
    my $ioc_id = ioc_load_by_name($ioc_nm);
    my $dbh;

    log('debug',"BootparamsDBLayer: update_bootparams: Started.");

    log('debug',"BootparamsDBLayer: $ioc_nm begin update_ioc_boot_values");

    if (!$dbh) {
        $dbh = db_connect();
    }

    my $update_boot_sql = 'update ioc_boot set ';

      $update_boot_sql .= " ioc_boot_date=ioc_boot_date,";

    chomp($line);

    my @chunks;
    if (!($line =~ m/^hn=/) ) {
      my ($boot_device_type,$boot_device_number,$processor_number,$host_file);
      ($boot_device_type,$boot_device_number,$processor_number,
          $host_file,@chunks) = split (/ |,|\(|\)/,$line);
      my ($host_name,$file_name) = split (/:/,$host_file);
      $update_boot_sql .= " boot_device=\"".$boot_device_type.$boot_device_number."\",";
      $update_boot_sql .= " processor_number=$processor_number,";
      $update_boot_sql .= " host_name=\"$host_name\",";
      $update_boot_sql .= " os_file_name=\"$file_name\",";
    } else {
      (@chunks) = split (/ |,|\(|\)/,$line);
    }

    foreach my $item (@chunks) {
      my ($name,$value) = split (/=/,$item);

      $update_boot_sql .= " boot_device=\"$value\","          if $name eq "bd";
      $update_boot_sql .= " boot_params_version=$value,"      if $name eq "v";
      $update_boot_sql .= " console_connection=\"$value\","   if $name eq "cc";
      $update_boot_sql .= " host_inet_address=\"$value\","    if $name eq "h";
      $update_boot_sql .= " host_name=\"$value\","            if $name eq "hn";
      $update_boot_sql .= " ioc_inet_address=\"$value\","     if $name eq "e";
      $update_boot_sql .= " ioc_pid=$value,"                  if $name eq "pid";
      $update_boot_sql .= " launch_script=\"$value\","        if $name eq "ls";
      $update_boot_sql .= " launch_script_pid=$value,"        if $name eq "lpid";
      $update_boot_sql .= " os_file_name=\"$value\","         if $name eq "fn";
      $update_boot_sql .= " processor_number=$value,"         if $name eq "pn";
      $update_boot_sql .= " target_architecture=\"$value\","  if $name eq "ta";
    }

    # remove trailing comma
    $update_boot_sql =~ s/,$/ /;
 

    my $ioc_boot_id = $ioc_boot_info->{ioc_boot_id};
    if (!defined $ioc_boot_id){
      if (get_test_mode()) {
        log('info',"BootparamsDBLayer: update_bootparams: Test mode. No database updates.");
      } else {
        log('error',"BootparamsDBLayer: update_bootparams: ioc_boot_id not defined. Probably NO write access to database.");
      }
      return;
    }

    $update_boot_sql .= " where ioc_id=$ioc_id and ioc_boot_id=$ioc_boot_id";

    my $update_ioc_boot_st = $dbh->prepare($update_boot_sql);
    eval {
        $update_ioc_boot_st->execute();
    };
    if ($@) {
      log('error',"$ioc_nm update_ioc_boot_values():$@");
      exit;
    }

    if (get_test_mode()) {
        $dbh->rollback;
        log('info',"BootparamsDBLayer: update_bootparams: TEST MODE; Rollback bootparam updates.");
    } else {
        log('debug',"BootparamsDBLayer: update_bootparams: Commiting bootparam updates");
        $dbh->commit;
    }

    log('info',"BootparamsDBLayer: update_bootparams: Finished.");
}

1;

