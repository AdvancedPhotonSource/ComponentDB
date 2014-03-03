package PVCrawlerParser;

# This package provides subroutines for parsing ioc startup
# command files. 
#
# Authors:
#
# Modifications:
#   10/23/07  Mariana Varotto (mvarotto@aps.anl.gov)
#             
#             Created SEQCrawlerParser module to deal with specific sequencer 
#	      crawler functions. Moved parse_seq_command() to SEQCrawlerParser
#             module
#
#             Rewrote parse_ld_command to make it generic, and added call to 
#             parse_ld_seq (in SEQCrawlerParser module) to specifically 
#             extract seq information from the loaded object file. This will 
#             allow to reuse parse_ld_command for future crawlers
#             
#             Added parse_registerRecordDeviceDriver_command function to 
#             deal with 3.14 iocs
#
#             Moved seq_hits and load_hits arrays to SEQCrawlerParser module
#
#             Added get_arch function to obtain ioc architecture
#
#   1/10/05.  Ron MacKenzie (ronm@slac.stanford.edu).
#             The following changes were made to support the crawling of sequence files:
#             Added call to parse_ld_command ("ld" parser) in parse_startup_command
#             Added call to parse_seq_command (sequence parser) in parse_startup_command 
#             Added new arguments to parse_inline_include() for seq processing.
#             Added 3 new functions for SEQ processing:
#               parse_ld_command(). This might be useful to other callers besides SEQ.
#               parse_seq_command()
#

use strict;
use FileHandle;
use File::Basename; 
use File::Compare;
use File::Path;
use File::stat;
require Exporter;
use POSIX;

use SEQCrawlerParser qw(parse_ld_seq @seq_hits @load_hits_seq parse_seq_command);
use AOICrawlerParser qw(finished_parse_startup_command_aoi add_db_record_aoi start_parse_inline_include_aoi parse_startup_command_line_aoi);
use RECCrawlerParser qw(finished_parse_command_rec add_db_record_rec parse_db_comment_rec parse_startup_command_line_rec);
use CommonUtil qw(mail);
use LogUtil qw(log);

our @ISA = qw(Exporter);
our @EXPORT_OK = qw(initialize_parser parse_startup_command get_rnam_fldn get_rnam_aliasn get_file_list get_dbd get_device_support);
our $VERSION = 1.00;

my $parser_initialized = 0;
my $temp_file_dir;
my $cwd;
my %gbl_var;
my %nfs_mounts;
my $ioc_name;
my $ioc_arch;
my $IRMIS_SITE;
my $alias_index;
my $env_vars;
my $orig_cwd;
my $aoi_scan=1;
my $rec_scan=1;

# stores record names joined with fields (ie. .db file info)
my @rnam_fldn;
my @rnam_aliasn;
my $pv_index;


# stores list of ioc resources (ie. .dbd, .template, and .db file paths)
my @file_list;
my $file_index;

# stored record type definitions (ie. .dbd file info)
my %dbd;
my $dbd_def;

# device support info gleaned from dbd file
my @dev_sup;
my $dev_sup_index;

# file path substitutions to try for those difficult to reach paths
my $path_substs;

# Initialize parser. Must be called each time before parse_startup_command.
sub initialize_parser {
    # initialize module globals
    $temp_file_dir = $_[0];
    $cwd = $_[1];
    $orig_cwd = $_[1];
    $dbd_def = $_[2];
    $ioc_name = $_[3];
    $path_substs = $_[4];
    $env_vars = $_[5];
    $aoi_scan = $_[6];           # flag to skip aoi scanning.
    $rec_scan = $_[7];           # flag to skip rec scanning.

    # CLEANUP possibly large lists
    undef(@file_list);
    undef(@rnam_fldn);
    undef(@rnam_aliasn);
    undef(%dbd);
    undef(@dev_sup);
    # used during parsing to keep track of script variable assignments
    undef(%gbl_var);  
    # used during parsing to keep track of nfs mount points
    undef(%nfs_mounts);
    # RONM added  for SEQ processing
    @seq_hits=();
    @load_hits_seq=();

    # add an initial IOC_NAME entry in gbl_var to compensate for soft iocs
    #   which assume the existence of an environment variable IOC_NAME.
    $gbl_var{"IOC_NAME"} = $ioc_name;

    # add an initial IOC_INFO entry in gbl_var to compensate for soft iocs
    #   which assume the existence of an environment variable IOC_INFO.
    $gbl_var{"IOC_INFO"} = "/usr/local/iocapps/iocinfo";

    #JROCK new
    # find the IRMIS_SITE env var if available
    $IRMIS_SITE = find_env_var("IRMIS_SITE");    
    
    $file_index = 0;
    $pv_index = 0;
    $alias_index = 0;
    $dev_sup_index = 0;
    $ioc_arch = '';

    $parser_initialized = 1; # good to go

}

# Five subroutines to get data after parsing complete
sub get_rnam_fldn {
    return \@rnam_fldn;
}

sub get_rnam_aliasn {
    return \@rnam_aliasn;
}

sub get_file_list {
    return \@file_list;
}

sub get_dbd {
    return \%dbd;
}

sub get_device_support {
    return \@dev_sup;
}

# Parse up an entire VxWorks startup command file.
sub parse_startup_command {

    #
    # arguments to subroutine
    #
    # full file path of the script to be parsed
    my $script_path = $_[0];
    my $st_cmd_dir = $_[1];      # the directory. RONM
    my $ioc_nm     = $_[2];      # the ioc name. RONM
    my $sequence_scan = $_[3];   # flag to skip sequence file scanning.

    my %parse_err_hash = ();
    my $parse_err = 0;
    my $parse_err_aoi = 0;
    my $parse_err_rec = 0;
    my $parse_err_seq = 0;
    my $SCRIPT;

    log('debug',"Entering parse_startup_command cmd_dir is $st_cmd_dir script_path is $script_path ");
    log('debug',"Sequence scan flag is: $sequence_scan, AOI scan flag is: $aoi_scan, REC scan flag is: $rec_scan");

    if (!$parser_initialized) {
        log('error',"Using parser before initializing. Exiting...");
        exit;
    }

    # make sure we have a valid path 
    $script_path = try_nfs_mount_and_file_system($script_path);
    
    # Open up startup command file
    if ( -e $script_path ) {
        log('debug',"script mod time = ".
            get_file_mod_date($script_path));
        if (!($SCRIPT = new FileHandle "$script_path")) {
            log('error',"Could not open startup command file: $!");
            mail("Could not open startup command file: $!");
            $parse_err = 1; # general parse error
        }
    } else {
        log('error',"Could not locate $script_path");
        mail("Could not locate $script_path");
        $parse_err = 1;
    }
    
    get_arch ($st_cmd_dir);

  # Iterate over lines in startup command file
  if (defined $SCRIPT) {
    while ( my $script_line = <$SCRIPT> ) {
        chomp($script_line);

        # substitute for any $(VAR) or ${VAR}
        $script_line = substitute_env_vars($script_line);

        # parse iocshcmd line
        log('debug',"sl = $script_line");
        if ($script_line =~ /^iocshCmd\s/  || $script_line =~ /^iocshCmd\(/ ) {
              $script_line = parse_iocshCmd_command($script_line);
        }
      
        # strip any leading whitespace
        if ($script_line =~ /^\s*(.*)/) {
            $script_line = $1;
        }
        # Remove trailing whitespace
        $script_line =~ s/\s+$//;

        log('debug',"sl = $script_line");

        # comments and blank lines
        if ( ((substr($script_line,0,1) eq "#") && ($script_line !~ /^\#\!/)) ||
            ($script_line =~ /^\s*\/\*/) ||
            ($script_line =~ /^\s*$/)) {
            $parse_err_aoi += parse_startup_command_line_aoi($script_path, $script_line, $ioc_nm) if $aoi_scan;
            $parse_err_rec += parse_startup_command_line_rec($script_path, $script_line) if $rec_scan;
            next;
        }
        # separate out inline include, var assignment, ld, and function call
        if ($script_line =~ /^<.*/) {
            $parse_err_aoi += start_parse_inline_include_aoi($script_line,$ioc_nm) if $aoi_scan;
            my %include_err_hash = parse_inline_include($script_line, $st_cmd_dir, $ioc_nm, $sequence_scan);
            $parse_err_aoi += $include_err_hash{AOI};
            $parse_err_rec += $include_err_hash{REC};
            $parse_err_seq += $include_err_hash{SEQ};
            $parse_err += $include_err_hash{PV};

        } elsif ($script_line =~ /^\#\!/) { 
            $parse_err_seq += parse_shebang_command($script_line, $st_cmd_dir, $sequence_scan);

        } elsif ( $script_line =~ /^[\w\*]+\s*=.*/ ||
                  $script_line =~ /^\(\w+\)\w+\s*=.*/) {
            $parse_err += parse_var_assignment($script_line);

        } elsif ($script_line =~ /^ld\s/ || $script_line =~ /^ld\(/ ) {
            
            $parse_err_seq += parse_ld_command($script_line, $st_cmd_dir, $sequence_scan);
        }         
	elsif ($script_line =~ m/registerRecordDeviceDriver/) {
            
            $parse_err_seq += parse_registerRecordDeviceDriver_command($script_line, $st_cmd_dir, $sequence_scan);
        }
	elsif ($script_line =~ m/^\s*seq/i) {   # seq command may be proceeded by spaces.
            
            if ($sequence_scan) {
                $parse_err_seq += parse_seq_command($script_line, $ioc_nm);
            }
            else {
                log('debug',"Not parsing sequence command in PVCrawlerParser.");
            }
            
        } else {
            # Assume we have a function call at this point
            my $return_val; # ignore return value since its not part of var assignment
            $parse_err += parse_function_call($script_line, \$return_val);
        }
        
       $parse_err_aoi += parse_startup_command_line_aoi($script_path, $script_line, $ioc_nm) if $aoi_scan;

    }
    close($SCRIPT);
  }
    $parse_err_aoi += finished_parse_startup_command_aoi() if $aoi_scan;
    $parse_err_rec += finished_parse_command_rec() if $rec_scan;

    $parse_err_hash{"AOI"}=$parse_err_aoi; 
    $parse_err_hash{"REC"}=$parse_err_rec; 
    $parse_err_hash{"SEQ"}=$parse_err_seq; 
    $parse_err_hash{"PV"}=$parse_err; 
    return %parse_err_hash;
}

sub parse_iocshCmd_command{
    my $script_line = $_[0];
    $script_line =~ s/iocshCmd//g;  # Remove iocshCmd 
    $script_line =~ s/^\s+//; # Remove leading whitespace
    $script_line =~ s/\s+$//; # Remove trailing whitespace
    if ($script_line =~ m/^\(/ ) { # Remove outer parentheses
       $script_line =~ s/^\(\s*//;
       $script_line =~ s/\s*\)\s*$//;
    }
    if ($script_line =~ m/^\"/ ) { # Remove outer quotes
       $script_line =~ s/^\"\s*//;
       $script_line =~ s/\s*\"\s*$//;
    }
    $script_line =~ s/\\\"/\"/g;  # Change \" to "
    $script_line=substitute_env_vars($script_line);
    return $script_line;
}

sub parse_shebang_command {
    my $script_line = $_[0];
    my $st_cmd_dir  = $_[1];
    my $sequence_scan = $_[2];   # flag to skip sequence file scanning.
    my $parse_err=0;

 if ($sequence_scan) {
    # make sure we have a valid path
    $st_cmd_dir = try_nfs_mount_and_file_system($st_cmd_dir);

    log('debug',"Entering parse_shebang_command, script line is: $script_line");

    # The shebang command has the executable name as part of the string 
    # Extract executable name:

    if ( $script_line =~ /^\#\!/ ) { 
        $script_line =~ s/^\#\!//;
        my ($efile) = split(/ /, $script_line,1);   # Split into parts 
        my $executable_file = basename($efile);              # Remove the directory
        log('debug', "The loaded file is: $executable_file ");
        $ioc_arch=$gbl_var{ARCH} if $ioc_arch eq '';
        $parse_err=parse_ld_seq($executable_file, $ioc_arch, $st_cmd_dir);
    } else {
        log('debug', "Wrong shebang command syntax"); 
        $parse_err=1;
    }
  }
  return $parse_err; 
}

sub parse_registerRecordDeviceDriver_command {
    my $script_line = $_[0];
    my $st_cmd_dir  = $_[1];
    my $sequence_scan = $_[2];   # flag to skip sequence file scanning.
    my $parse_err=0;

    # make sure we have a valid path
    $st_cmd_dir = try_nfs_mount_and_file_system($st_cmd_dir);

    log('debug',"Entering parse_registerRecordDeviceDriver_command, script line is: $script_line");

    # The call to registerRecordDeviceDriver has the library name as part of the string
    # Extract library name:

    if ( $script_line =~ /_/ ) { 
        my @register_line = split("_", $script_line);   # Split into array
        log('debug', "Array is @register_line "); 
        my $register_file = $register_line[0];          # Assume library name at the begining of string
        $register_file =~ s/^\s+//;                     # strip leading and trailing blanks.
        $register_file =~ s/\s+$//;
        log('debug', "The loaded file is: $register_file "); 
        if ($sequence_scan) {
            $ioc_arch=$gbl_var{ARCH} if $ioc_arch eq '';
            $parse_err=parse_ld_seq($register_file, $ioc_arch, $st_cmd_dir);
        }
    } else {
        log('debug', "Invalid registerRecordDeviceDriver command syntax"); 
        $parse_err=1;
    }
    return $parse_err;
}

sub parse_ld_command {
    my $script_line = $_[0];
    my $st_cmd_dir  = $_[1];
    my $sequence_scan = $_[2];   # flag to skip sequence file scanning.
    my $ld_cmd_file;
    my $ld_cmd_file_tmp;
    my $size;
    my @line_b;
    my $parse_err=0;

    # make sure we have a valid path
    $st_cmd_dir = try_nfs_mount_and_file_system($st_cmd_dir);

    log('debug',"Entering parse_ld_command, script line is: $script_line");

    # Find ld filename
    # Get rid of  stuff before obj file name

    if ( $script_line =~ /</ ) {
        @line_b = split("<", $script_line);    # Remove part of the string related to ld syntax
    } else {
        $ld_cmd_file_tmp = $script_line;
        $ld_cmd_file_tmp =~ s/\(/ /g;  # Replace open parentheses with blank
        $ld_cmd_file_tmp =~ s/\)/ /g;  # Replace close parentheses with blank
        $ld_cmd_file_tmp =~ s/,/ /g;   # Replace commas with blank
        @line_b = split(" ", $ld_cmd_file_tmp);
    }
    $size = @line_b;
    $ld_cmd_file = $line_b[$size-1];
    
#    my @line_a = split("/", $ld_cmd_file);    # Split into array.  Then, assume file at end of line.
#    $size = @line_a;
#    $ld_cmd_file = $line_a[$size-1];
    $ld_cmd_file =~ s/^\s+//;  # strip leading and trailing blanks.
    $ld_cmd_file =~ s/\s+$//;
    $ld_cmd_file =~ s/\"//g;   # Remove quotes around file name
    $ld_cmd_file =~ s/\'//g;   # Remove quotes around file name
    log('debug', "The loaded file is: $ld_cmd_file "); 
    if ($sequence_scan) {
        $ioc_arch=$gbl_var{ARCH} if $ioc_arch eq '';
        $parse_err=parse_ld_seq($ld_cmd_file, $ioc_arch, $st_cmd_dir);
    }
    return $parse_err;
}

# Parse up VxWorks inline include
# Line assumed to be of form "< ../nfsCommands"
sub parse_inline_include {
    my $script_line = $_[0];
    my $st_cmd_dir = $_[1];      # just the directory. RONM added these.
    my $ioc_nm = $_[2];          # ioc name
    my $sequence_scan = $_[3];   # flag to skip sequence file scanning.
    my $include_path;

    # handle weird case where someone does "< asdstdPreInit()" - still just file path
    if ($script_line =~ /^<\s*([\S]*)\(\)\s*#*/ ||
        $script_line =~ /^<\s*([\S]*)\s*#*/) {
        $include_path = $1;
    } else {
        log('error',"unable to parse inline include $script_line");
        mail("Unable to parse inline include $script_line");
        return 2;
    }

    $include_path =~ s/\"//g;   # Remove quotes
    $include_path =~ s/\'//g;   # Remove quotes

    # substitute for any $(VAR) or ${VAR}
    $include_path = substitute_env_vars($include_path);

    # make sure path is fully qualified using module global $cwd
    if (substr($include_path,0,1) ne "/") {
        $include_path = $cwd . "/" . $include_path;
    }

    # make sure we have a valid path 
    $include_path = try_nfs_mount_and_file_system($include_path);

    log('debug',"Inline include path = $include_path");
    log('debug',"Inline include st_cmd_dir is $st_cmd_dir");    

    return parse_startup_command($include_path, $st_cmd_dir, $ioc_nm, $sequence_scan);
}



# Test for existence of a path. If it fails, first try substituting
# any path substitutions given by the path.properties file. If it continues
# to fail, try substituting the
# original file system path for the nfs mount point. This is sometimes
# necessary to help the pv crawler do its work. Return path, possibly
# modified.
# Private
sub try_nfs_mount_and_file_system {
    my $path = $_[0];
    my $new_path;

    if (! -e $path) {
        foreach my $subpath (keys %$path_substs) {
            my $substitution = $path_substs->{$subpath};
            if ($path =~ /$subpath/ ) {
                $new_path = $path;
                $new_path =~ s/$subpath/$substitution/;
                if (-e $new_path) {
                    log('debug',"$path has been replaced with $new_path using path.properties");
                    return $new_path;
                }
            }
        }
        # try original file system path instead of nfs mount point
        foreach my $mp (keys %nfs_mounts) {
            my $file_system = $nfs_mounts{$mp};
            if ( $path =~ /^$mp/ ) {
                $new_path = $path;
                $new_path =~ s/$mp/$file_system/;
                if (-e $new_path) {
                    log('debug',"$path has been replaced with $new_path using local file system");
                    return $new_path;
                }
            }
        }
    }
    return $path;
}

# Parse up VxWorks variable assignment.
# Line assumed to be of form a = b or a = func(arg1...)
# or a = (cast)b or (cast)a = b
sub parse_var_assignment {
    my $script_line = $_[0];
    my $parse_err = 0;

    log('debug',"parse_var_assignment");

    # Get "this = that" or with cast "(short)this = that"
    if ($script_line =~ /(^[\w\*]+)\s*=\s*(.*)/ ||
        $script_line =~ /^\(\w+\)(\w+)\s*=\s*(.*)/) {
        my $var_name = $1;
        my $var_value;
        my $rvalue = $2;

        if ($rvalue =~ /^"(.*)"/) {  # quoted value
            $var_value = $1;

        } elsif ($rvalue =~ /^([0-9]+)/ ||
                 $rvalue =~ /^\(\w+\)\s*([0-9]+)/) {  # a decimal value
            $var_value = $1;

        } elsif ($rvalue =~ /(^\w+)\s*(\(*.*)/) {  # a function call
            my $f = $1;
            my $args = $2;
            my $return_val = "";
            $parse_err = parse_function_call($f . $args, \$return_val);
            $var_value = $return_val;

        } else {
            log('warn',"unable to parse var assignment with complex rval $script_line");
            mail("Unable to parse var assignment with complex rval $script_line");
            return 2;
        }
        $gbl_var{$var_name} = $var_value;
        log('debug',"gbl_var{$var_name} = $var_value");
        
    } else {
        log('warn',"unable to parse var assignment $script_line");
        mail("Unable to parse var assignment $script_line");
        return 2;
    }
    return $parse_err;
}

# Parse up VxWorks function call.
# Line assumed to be of form "doThisFunc(arg1, arg2)"
sub parse_function_call {
    my $script_line = $_[0];
    my $return_val = $_[1];
    my $args;
    my $f;

    log('debug',"parse_function_call");


    if ($script_line =~ /(^\w+)\s*(\(*.*)#*/) {
        $f = $1;
        $args = $2;
        log('debug',"function $f with args $args");
    } else {
        log('warn',"unable to parse function call $script_line");
        mail("Unable to parse function call: $script_line");
        return 2; 
    }

    # process these functions, ignore all others
    if ($f eq "cd") {
        return process_cd($args);

    } elsif ($f eq "nfsMount") {
        return process_nfs_mount($args);

    } elsif ($f eq "dbLoadDatabase") {
        return process_db_load_database($args);

    } elsif ($f eq "dbLoadRecords") {
        return process_db_load_records($args);

    } elsif ($f eq "dbLoadTemplate") {
        return process_db_load_template($args);

    } elsif ($f eq "asdCreateSubstitutionString") {
        return process_asd_create_substitution_string($args, $return_val);

    } elsif ($f eq "asdGetTargetName") {
        return process_asd_get_target_name($args, $return_val);

    } elsif ($f eq "epicsEnvSet") {
        return process_epics_env_set($args, $return_val);

    } elsif ($f =~ /sprin.*/) {
        return process_sprint($args, $return_val);

    } elsif ($f eq "putenv") {
        return process_putenv($args, $return_val);

    }
    return 0;
}

# Process VxWorks "cd args"
sub process_cd {
    my $args = $_[0];

    # parse up args into array
    my @parsed_args = parse_args($args);

    # we should only have one arg here
    my $path = $parsed_args[0];

    log('debug',"request chdir to $path");

    # fully qualify path if needed
    if (substr($path,0,1) ne "/") {
        $path = $cwd . "/" . $path;
    }

    # make sure we have a valid path 
    $path = try_nfs_mount_and_file_system($path);

    log('debug',"actual chdir to $path");

    if (!chdir $path) {
        log('error',"Cannot cd to $path");
        mail("Cannot cd to $path");
        return 1;
    }
    # update the module global $cwd
    $cwd = getcwd();
    return 0;
}

# Process VxWorks nfsMount(args)
sub process_nfs_mount {

    #JROCK new
    if ($IRMIS_SITE eq "SLAC") {
        # for SLAC's setup, need to assign some things here 
        $gbl_var{"Xhost_c"} = $gbl_var{"IOC_NAME"};
        my $xpath_val = find_env_var("Xpath_c");
        if ($xpath_val ne "") {
            $gbl_var{"Xpath_c"} = $xpath_val;
        }
    }

    my $args = $_[0];

    # parse up args into array
    my @parsed_args = parse_args($args);

    # we should have 3 args (host, file_system, mount_point)
    if (scalar @parsed_args != 3) {
        log('warn',"expecting 3 arguments to nfsMount, skipping");
        mail("Expecting 3 arguments to nfsMount, skipping nfsMount $args");
        return 1;
    }

    my ($host,$file_system,$mount_point) = @parsed_args;

    # Retain a record of a mount point's underlying file_system, but only
    # if the actual mount point is not accesible to the parser. Basically,
    # if we can't get at the mount point, there is some hope that the
    # underlying file_system is accessible.
    if (! -e $mount_point) {
        log('debug',"mount point $mount_point inaccessible, so using $file_system instead");
        $nfs_mounts{$mount_point} = $file_system;
    }
    return 0;
}

# Process EPICS dbLoadDatabase(args)
sub process_db_load_database {
    my $args = $_[0];

    # parse up args into array
    my @parsed_args = parse_db_args($args);

    # path of dbd file should be zero'th arg
    my $dbd_path = $parsed_args[0];
    log('debug',"Entered process_db_load_database with $dbd_path");

    # check to see if a .db or .template file has been given
    my $file_name = basename($dbd_path);
    if ($file_name =~ /[^\.].db$/ ||
        $file_name =~ /[^\.].template$/) {
        log('warn',"dbLoadDatabase has been called on $dbd_path");
        # redirect to dbLoadRecords handling
        return process_db_load_records($args);
    }

    # JROCK new 
    if ($IRMIS_SITE eq "SLAC") {
        # for soft IOCs at SLAC
        chdir $orig_cwd;
        $cwd = $orig_cwd;
    }

    # fully qualify path if needed
    if (substr($dbd_path,0,1) ne "/") {
        $dbd_path = $cwd . "/" . $dbd_path;
    }

    # make sure we have a valid path 
    $dbd_path = try_nfs_mount_and_file_system($dbd_path);

    # check for existence
    my $st = stat($dbd_path);
    if (!$st) {
        log('warn',"dbd file $dbd_path unreachable");
        mail("dbd file $dbd_path unreachable");
        return 1; # dbd file unreachable
    }
    my $file_mtime = $st->mtime; # get file mtime

    # add to master list of ioc resources

    $file_list[$file_index] = 
    {id=>$file_index+1, type=>"dbd", path=>"$dbd_path", 
     subst=>"", mtime=>"$file_mtime"};
    $file_index++;
    
    log('debug',"process_db_load_database: calling process_file with name $dbd_path");
    return process_dbd_file($dbd_path);
}

my $dbd_rtyp;

sub process_dbd_line {
    my $script_line = $_[0];

        log('debug',"process_dbd_line: Parsing dbd line: $script_line");

        if ($script_line =~ /^recordtype\s*\((.*)\)/) {
            $dbd_rtyp = $1;
            #log('verbose',"  recordtype $1");
        }
        elsif ($script_line =~ /^field\s*\((.*)\)/) {
            my ( $dbd_fldn, $dbd_fdtyp ) = split( ",", $1 );
            $dbd_fldn  =~ s/\s//g;
            $dbd_fdtyp =~ s/\s//g;
            if ( $dbd_fdtyp eq "DBF_NOACCESS" ) {
                return 0;
            }
            my $dbd_def_val = $dbd_def->{$dbd_fdtyp};
            # create new field entry (id assigned later)
            my $newFld = {id=>-1, file_index=>$file_index, fldn=>"$dbd_fldn",
                          def_val=>"$dbd_def_val", type=>"$dbd_fdtyp"};
            
            # add to hash of arrays, keyed by record type
            push @{$dbd{$dbd_rtyp}}, $newFld;
        }
        elsif ($script_line =~ /^device\s*\((.*),(.*),(.*),"(.*)"\)/) {
            my $newDevSup = {rec_typ=>$1, io_type=>$2, 
                             dev_sup_dset_nm=>$3, dtyp_str=>$4};
            $dev_sup[$dev_sup_index++] = $newDevSup;
        }
        elsif ($script_line =~ /^\w+\s*\((.*)\)/ || $script_line =~ /\}/ || $script_line =~ /^\S*\s+\S*\s*$/ ) {
        }
        else {
            log('error',"Error in dbd line: $script_line");
            return 1;
        }
    return 0;
}

sub parse_db_args {
    my $args = $_[0];
    my $arg_val;
    my @parsed_args;
    $args =~ s/^\s*\(//;  # Remove leading (
    $args =~ s/\)\s*$//;  # Remove trailing )
    @parsed_args = split (/,/,$args,2);
    foreach my $arg (@parsed_args) {
        if ($arg =~ m/iocSubString/){
          $arg = 'IOC='.$gbl_var{"IOC_NAME"} if $gbl_var{"IOC_NAME"};
        }
        $arg =~ s/^\s*\"//;  # Remove leading quote
        $arg =~ s/\"\s*$//;  # Remove trailing quote
        $arg =~ s/^\s+//;  # Remove leading whitespace
        $arg =~ s/\s+$//;  # Remove trailing whitespace
    }
    return @parsed_args;
}

# Process EPICS dbLoadRecords(args)
sub process_db_load_records {
    my $args = $_[0];

    # parse up args into array
    my @parsed_args = parse_db_args($args);

    my $num_args = @parsed_args;
    my $db_path;
    my $file_mtime = "";
    my $subst_str;

    if ($num_args == 1) {
        # just a straight db file to process
        $db_path = $parsed_args[0];

        # check to see if a .dbd file has been given
        my $file_name = basename($db_path);
        if ($file_name =~ /[^\.].dbd$/) {
            log('warn',"dbLoadRecords has been called on $db_path");
            # redirect to dbLoadDatabase handling
            return process_db_load_database($args);
        }

        # JROCK new
        if ($IRMIS_SITE eq "SLAC") {
            # for soft IOCs at SLAC
            chdir $orig_cwd;
            $cwd = $orig_cwd;
        }
        
        # fully qualify path if needed
        if (substr($db_path,0,1) ne "/") {
            $db_path = $cwd . "/" . $db_path;
        }

        # make sure we have a valid path 
        $db_path = try_nfs_mount_and_file_system($db_path);
        
        if ( !( -e $db_path ) ) {
            log('warn',"db file $db_path unreachable");
            mail("db file $db_path unreachable");
        } else {
            $file_mtime = stat($db_path)->mtime;
        }

        # add to master list of ioc resources
        $file_list[$file_index] =
        {id=>$file_index+1, type=>"db", path=>"$db_path", 
         subst=>"", mtime=>"$file_mtime"};
        $file_index++;

        
    } elsif ($num_args == 2 || $num_args == 3 || $num_args == 4 ) {
        # we have a substitution string to deal with

        if ($num_args == 2) {
            $db_path = $parsed_args[0];
            $subst_str = $parsed_args[1];
        } elsif ($num_args == 3) {
            # we've been redirected here from process_db_load_database, so
            #   the arguments must be interpreted as from dbLoadDatabase
            $db_path = $parsed_args[0];
            ####There were probably quote marks embedded in quote marks
            ####$subst_str = $parsed_args[1].'""'.$parsed_args[2];
            # dbLoadDatabase has 3 args, filename,directory,substitutions
            # both directory and substitutions may be null
            if ( ($parsed_args[1] != 0) && ($parsed_args[1] != -1) ) {
              $db_path = $parsed_args[1].$parsed_args[0];
            }
            $subst_str = $parsed_args[2];
        } elsif ($num_args == 4) {
            # we've been redirected here from process_db_load_database, so
            #   the arguments must be interpreted as from dbLoadDatabase
            $db_path = $parsed_args[0];
            #There were probably a set of quote marks embedded in quote marks
            $subst_str = $parsed_args[1].$parsed_args[2].$parsed_args[3];
        }            

        # JROCK new 
        if ($IRMIS_SITE eq "SLAC") {
            # for soft IOCs at SLAC
            chdir $orig_cwd;
            $cwd = $orig_cwd;
        }
        
        # fully qualify path if needed
        if (substr($db_path,0,1) ne "/") {
            $db_path = $cwd . "/" . $db_path;
        }

        # make sure we have a valid path 
        $db_path = try_nfs_mount_and_file_system($db_path);
        
        if ( !( -e $db_path ) ) {
            log('warn',"db file $db_path unreachable");
            mail("db file $db_path unreachable");
        } else {
            $file_mtime = stat($db_path)->mtime;
        }

        my $msicommand =
            "msi -M " . " \'"
            . $subst_str . "\' "
            . $db_path
            . " > $temp_file_dir/msioutput";
        log('debug',"$msicommand");
        system("$msicommand");
        #if (system("$msicommand") != 0) {
         #   $file_mtime = "";  # something is wrong in template processing
        #}

        # add to master list of ioc resources
        $file_list[$file_index] = 
        {id=>$file_index+1, type=>"db", path=>"$db_path", 
         subst=>"$subst_str", mtime=>"$file_mtime"};
        $file_index++;

        # real path is now the output of msi
        $db_path = $temp_file_dir . "/msioutput";

    } else {
        log('warn',"don't know how to handle these db_load_records arguments: $args");
        mail("Don't know how to handle these db_load_records arguments: $args");
        return 1;
    }

    if ($file_mtime) {
        log('debug',"calling parse_db with $db_path");
        return parse_db($db_path);
    }
    return 0;
}


sub process_db_load_template {
    my $args = $_[0];

    # parse up args into array
    my @parsed_args = parse_args($args);

    # path of template file should be zero'th arg
    my $template_path = $parsed_args[0];

    my $file_mtime = "";

    # JROCK new 
    if ($IRMIS_SITE eq "SLAC") {
        # for soft IOCs at SLAC
        chdir $orig_cwd;
        $cwd = $orig_cwd;
    }
    
    # fully qualify path if needed
    if (substr($template_path,0,1) ne "/") {
        $template_path = $cwd . "/" . $template_path;
    }

    # make sure we have a valid path 
    $template_path = try_nfs_mount_and_file_system($template_path);

    if ( !( -e $template_path ) ) {
        log('warn',"template file $template_path unreachable");
        mail("Template file $template_path unreachable");
    } else {
        $file_mtime = stat($template_path)->mtime;    
    }

    my $msiwork;
    if ($file_mtime) {
        # invoke msi utility to process template file
        $msiwork = $temp_file_dir . "/msioutput";
        log('debug',"system msi -S $template_path > $msiwork");
        system("msi -S $template_path >$msiwork");
        #if (system("msi -S $template_path >$msiwork") != 0) {
        #$file_mtime = "";  # something is wrong with template processing
        #}
    }

    # add to master list of ioc resources
    $file_list[$file_index] = 
    {id=>$file_index+1, type=>"template", path=>"$template_path", 
     subst=>"", mtime=>"$file_mtime"};
    $file_index++;

    if ($file_mtime) {
        # parse output of msi
        log('debug',"calling parse_db with $msiwork\n");
        return parse_db($msiwork);

    } else {
        return 1;
    }
}

sub parse_db {

    # full path of db file to be processed
    my $db_path = $_[0];

    my $fld_token;
    my $fldn;
    my $fldv;
    my $new_rec_alias;
    my $new_rec_fld;
    my $rec_token;
    my $rnam;
    my $rtyp;
    my $x;

    if ( !( -e $db_path ) ) {
        log('warn',"db file $db_path unreachable");
        mail("db file $db_path unreachable");
        return 1;
    }
    log('debug',"opening $db_path");
    if ( ! open(DBFILE,"<$db_path") ) {
      log('error',"Cannot open file $db_path; $!\n");
      mail("Cannot open file $db_path; $!\n");
      return 1;
    }
    my $llp = "";   # last line processed (either contains 'record' or 'field')

    # NOTE: redo this parsing, as it will not work in some boundary cases
    while (<DBFILE>) {
        # leading comment - # is legal in desc, inp etc arguments
        if (/^\s*#/) {
            #parse_db_comment_aoi($_) if $aoi_scan;  # AOIs not allowed in db (vdct problems)
            parse_db_comment_rec($db_path,$_) if $rec_scan;
            next;
        }
        # assume that 'record' is only token on this line
        if (/\bg*record\s*\((.*)\)/) {
            # must have at least proc'd 1 rec line, so have rnam, rtyp and pv_index data
            if ( $llp eq "record") {
                
                # this code gets executed if a .db file has a record with no fields 
                #  (eg stringin) - it gives empty fldn, fldv fields
                $fldn         = "";
                $fldv         = "";
                
                $new_rec_fld = {rec_nm=>"$rnam", fld_nm=>"$fldn", rec_typ=>"$rtyp",
                                fld_val=>"$fldv", file_index=>$file_index-1 };

                $rnam_fldn[$pv_index++] = $new_rec_fld;
            }
            $rec_token = $1;    #get the part within parenthesis
            ( $rtyp, $rnam ) = split( /,/,$rec_token);       #get the individual strings
            $rtyp =~ s/\s//g;   #remove any white space
            $rnam =~ s/\s//g;   #remove any white space
            $rnam =~ s/"//g;    #the record name can be surrounded by 0,1 or 2 "
            $llp = "record";
            add_db_record_aoi($rnam) if $aoi_scan;
            add_db_record_rec($rnam) if $rec_scan;
        }
        
        #assume that 'field' is only token on this line
        if (/\bfield\s*\((.*)\)/) {
            $fld_token = $1;    #get the part within parenthesis
            /"(.*)"/;
            $fldv = $1;         #the stuff between the " ".
            ( $fldn, $x ) = split( /,/, $fld_token );
            $fldn =~ s/ //g;    #get rid of surrounding possible spaces
            
            $new_rec_fld = {rec_nm=>"$rnam", fld_nm=>"$fldn", rec_typ=>"$rtyp",
                            fld_val=>"$fldv", file_index=>$file_index-1 };
            
            $rnam_fldn[$pv_index++] = $new_rec_fld;
            
            $llp = "field";    #last line processed is a 'field' line
        }    #end if 'field'
        
        #assume that 'alias' is only token on this line
        if (/\balias\s*\((.*)\)/) {
            my $alias_token = $1;      #get the part within parenthesis
            $alias_token =~ s/"//g;    #get rid of surrounding quote marks
            $alias_token =~ s/'//g;    #get rid of surrounding quote marks
            $alias_token =~ s/ //g;    #get rid of surrounding possible spaces
            my ( $aliasn, $aliasv ) = split( /,/, $alias_token );
            
            if ($aliasv) {

                # alias("canonicalName","alias") line
                $new_rec_alias = {rec_nm=>"$aliasn", alias_nm=>"$aliasv", 
                              file_index=>$file_index-1 };
                $rnam_aliasn[$alias_index++] = $new_rec_alias;
            } else { 

                # alias("alias") line within a record { } block
                $new_rec_fld = {rec_nm=>"$rnam", fld_nm=>"alias", rec_typ=>"$rtyp",
                            fld_val=>"$aliasn", file_index=>$file_index-1 };
                $rnam_fldn[$pv_index++] = $new_rec_fld;
                $llp = "field";    #last line processed is a 'field' line
            }
            
        }    #end if 'alias'
        
    }   #end while DBFILE

    # Catch boundary condition
    if ( $llp eq "record") {
        
        # this code gets executed if a .db file has a record with no fields 
        #  (eg stringin) - it gives empty fldn, fldv fields
        $fldn         = "";
        $fldv         = "";
        
        $new_rec_fld = {rec_nm=>"$rnam", fld_nm=>"$fldn", rec_typ=>"$rtyp",
                        fld_val=>"$fldv", file_index=>$file_index-1 };
        
        $rnam_fldn[$pv_index++] = $new_rec_fld;
    }
    close(DBFILE);    
    return 0;
}

# Simulate the actions of the asdCreateSubstitutionString(var,val) function
sub process_asd_create_substitution_string { 
    my $args = $_[0];
    my $return_val = $_[1]; # a reference to a string

    log('debug',"args to process_asd_create_subst_string $args");
    
    # parse up args into array
    my @parsed_args = parse_args($args);

    my $subst_var = $parsed_args[0];
    my $subst_val = $parsed_args[1];

    $$return_val = $subst_var . "=" . $subst_val;
    return 0;
}

# Simulate the actions of the asdGetTargetName function
sub process_asd_get_target_name { 
    my $args = $_[0];
    my $return_val = $_[1]; # a reference to a string
    
    $$return_val = $ioc_name; # ioc name given to parser at initialization
    return 0;
}

# Simulate the actions of the epicsEnvSet function
sub process_epics_env_set {
    my $args = $_[0];
    my $return_val = $_[1];

    # parse up args into array
    my @parsed_args = parse_args($args);

    # create var as if user had instead used arg1 = arg2
    $gbl_var{$parsed_args[0]} = $parsed_args[1];
    
    return 0;
}

# Simulate the actions of the family of sprint* functions
# Right now we assume simple cases of sprintf(newvar,"blah%sblah",var)
# or sprintf(newvar,"quoted literal").
sub process_sprint { 
    my $args = $_[0];
    my $return_val = $_[1]; # a reference to a string
    my $format_str;
    my $literal_str;
    my $new_var;
    my $var_value;

    log('debug',"args to process_sprint $args");
    
    # parse up args into array
    # JROCK added parameter to call to parse_args from process_sprint only
    my @parsed_args = parse_args($args, "sprint");
    my $num_args = @parsed_args;

    if ($num_args > 2) {
        $new_var = $parsed_args[0];
        $format_str = $parsed_args[1];
        for (my $i = 2; $i < $num_args; ++$i) {
            $var_value = $parsed_args[$i];
            $format_str =~ s/\%s/$var_value/;
        }
        $gbl_var{$new_var} = $format_str;
        $$return_val = $format_str; 

    } elsif ($num_args == 2) {
        $new_var = $parsed_args[0];
        $literal_str = $parsed_args[1];
        $gbl_var{$new_var} = $literal_str;
        $$return_val = $literal_str;

    } else {
        log('warn',"sprintf has less than 2 arguments: $args");
        mail("sprintf has less than 2 arguments: $args");
        return 0;
    }

    return 0;
}

# Simulate the actions of the putenv function
sub process_putenv {
    my $args = $_[0];
    my $return_val = $_[1];

    my @parsed_args = parse_args($args);
    my $arg0 = $parsed_args[0];

    if ($arg0 =~ /(^\w+)\s*=\s*(.*)/) {
        my $var_name = $1;
        my $var_value = $2;
        if ($var_name && defined $var_value) {
            log('debug',"putenv creating new gbl_var $var_name with value $var_value");
            $gbl_var{$var_name} = $var_value;
        }
    } else {
        log('warn',"Unable to parse var assignment: $arg0");
        mail("putenv Unable to parse var assignment: $arg0");
        return 2;
    }
    return 0;
}

# Parse up VxWorks function arguments. 
# Return array of values, resolving any variables into their actual value
sub parse_args {
    my $args = $_[0];
    my $orig_args = $args;
    my $arg_value;
    my $first_arg;


    # JROCK new
    # check for second parameter, indicating how to handle the results
    # e.g. if "sprint", restore the original first param, the var name
    # for now this is the only special case...
    my $arg2 = "";
    if (@_ > 1) {
        $arg2 = $_[1];
    }
    
    my @parsed_args = ();

    # strip off any surrounding ()
    if ($args =~ /^\((.*)\)/) {
        $args = $1;
    }

    # strip off any leading space
    if ($args =~ /^\s*(.*)/) {
        $args = $1;
    }

    # JROCK new
    # if parsing sprint, save the first arg to restore later
    if ($arg2 eq "sprint") {
        my $comma_pos = index($args, ",");
        $first_arg = substr($args, 0, $comma_pos);
    }
    
    # substitute for any $(VAR) or ${VAR}
    $args = substitute_env_vars($args);
    
    # loop until no more arguments
    while ($args) {
        
        # see if next argument is quoted literal
        if (substr($args,0,1) eq '"') {

            # do a char-by-char parse due to possible use of escaped double quotes
            my $remaining_string = substr($args,1);
            my $quoted_arg = '';
            my $escape_mode = 0;
            while ($remaining_string =~ /^(.)(.*)$/) {
                my $next_char = $1;
                $remaining_string = $2;
                if (!$escape_mode) {
                    if ($next_char eq '\\') {  
                        $escape_mode = 1;
                    } elsif ($next_char eq '"') {
                        last;
                    } else {
                        $quoted_arg = $quoted_arg . $next_char;
                    }
                    
                } else {
                    $quoted_arg = $quoted_arg . $next_char;
                    $escape_mode = 0;
                }
            }
            if ($remaining_string =~ /^[, ]*(.*)/) {
                $remaining_string = $1;
            }
            $arg_value = $quoted_arg;
            $args = $remaining_string;  

        # see if next argument is an integer literal or hex literal
        } elsif ($args =~ /^(0x[0-9ABCDEFabcdef]+)[, ]*(.*)/ ||
                 $args =~ /^(\d+)[, ]*(.*)/) {
                $arg_value = $1; # a literal argument
                $args = $2;
    
        # see if we have a variable that needs to be dereferenced
        } elsif ($args =~ /^([A-Za-z][\w:;\-]*)[, ]*(.*)/) {
            my $var_name = $1; # refers to a variable which we must dereference
            $arg_value = $gbl_var{$var_name};
            if (!$arg_value) {
                #log('warn',"new variable $var_name encountered");
                $arg_value = $var_name;  # whatever...
            } else {
                log('debug',"deferencing $var_name to $arg_value");
            }
            $args = $2;

        # assume its a literal argument at this point
        } elsif ($args =~ /^([^,]*)[, ]*(.*)/) {
            $arg_value = $1;
            $args = $2;

        # i give up
        } else {
            log('warn',"Unable to parse arguments in $orig_args");
            mail("Unable to parse arguments in $orig_args");
            return $orig_args;
        }

        push @parsed_args, "$arg_value";

        # strip leading whitespace for next iteration
        if ($args =~ /\s*(.*)/) {
            $args = $1;
        }
    }

    # JROCK new
    # if parsing for a "sprint", restore the original first arg, the var name
    if ($arg2 eq "sprint") {
       $parsed_args[0] = $first_arg;
    }

    return @parsed_args;
}

# Locate all $(VAR) or ${VAR} in a string and attempt 
#   to replace with value from gbl_var
sub substitute_env_vars {
    my $str = $_[0];
    my $original_str = $str;
    my $leading_str;
    my $subst_val;
    my $subst_var;
    my $trailing_str;

    # loop until no more vars found ie. $(var)
    while ($str =~ /^([^\$]*)\$[\({](\w+)[\)}](.*)/) {
        
        $leading_str = $1;
        $subst_var = $2;
        $trailing_str = $3;

        $subst_val = $gbl_var{$subst_var};
        if (! defined $subst_val) {
            log('warn',"Unable to dereference variable $subst_var in $original_str");
            mail("Unable to dereference variable $subst_var in $original_str") if substr($original_str,0,1) ne "#"; 

            # Hack here until we get our developers to replace usage of EPICS_HOST_ARCH
            #  environment variable with ARCH
            if ($subst_var eq "EPICS_HOST_ARCH") {
                $subst_val = $gbl_var{ARCH};
                if (!$subst_val) {
                    log('warn',"Substituting ARCH for EPICS_HOST_ARCH failed");
                    mail("Substituting ARCH for EPICS_HOST_ARCH failed");
                    return $original_str;
                }
            } else {
                return $original_str;
            }
        }
        $str = $leading_str . $subst_val . $trailing_str;
    }
    return $str;
}

# Reads in all lines of a text file into a buffer. If an include statement is encountered,
#  the lines from the included file are incorporated recursively.
# Argument 0 is a file path.
sub process_dbd_file {
    my $file_path = $_[0];
    my $FILE_HANDLE;
    my $line_error_count=0;

    log('debug', "process_dbd_file: Processing file $file_path");

    # fully qualify path if needed
    if (substr($file_path,0,1) ne "/") {
        $file_path = $cwd . "/" . $file_path;
    }

    # make sure we have a valid path 
    $file_path = try_nfs_mount_and_file_system($file_path);

    log('debug', "process_dbd_file opening $file_path");
    if (!($FILE_HANDLE = new FileHandle "$file_path")) {
        log('warn',"Could not open file: $!");
        mail("Could not open file: $!");
        return undef;
    }    

    while (my $script_line = <$FILE_HANDLE>) {
        chomp($script_line);


        $script_line =~ s/^\s+//; # Remove leading white space

        # skip comments and blank lines
        if ((substr($script_line,0,1) eq "#") ||
            ($script_line =~ /^\/\*/) ||
            ($script_line =~ /^$/) ||
            ($script_line =~ /^#/)) {
            next;
        }
        if ($script_line =~ /^include\s*"([^"]*)"/) { #"
            $line_error_count += process_dbd_file($1);
        } else {
            $line_error_count += process_dbd_line($script_line);
        }
            
    }
    log('error',"Error parsing dbd file $file_path") if $line_error_count;
    mail("Error parsing dbd file $file_path") if $line_error_count;
    return $line_error_count;
}
    
# Return mtime of given file in YYYYMMDDHHMMSS format
sub get_file_mod_date {
    my $file_name = $_[0];    # full file path
    my $mod_time;
    
    if ( !( -e $file_name ) ) {
        return "00000000000000";
	} else {
        $mod_time = stat($file_name)->mtime;
        if ($mod_time) {
            return format_date($mod_time);
        } else {
            return "00000000000000";
        }
    }
}

# Takes seconds after epoch and converts to string with "YYYYMMDDHHMMSS"
sub format_date {
    my $secsAfter1970 = $_[0];

    my @tmArr = localtime($secsAfter1970);

    my $formatStr = '%Y%m%d%H%M%S';

    my $formatDate = POSIX::strftime($formatStr, $tmArr[0],$tmArr[1],$tmArr[2],
                                     $tmArr[3], $tmArr[4], $tmArr[5]);

    return $formatDate;
}

# *********************************
# Subroutine: find the value of an env var defined in env.properties,
# *********************************
# JROCK new
# given its name
sub find_env_var {
   my $env_name = $_[0];

   foreach my $name (keys %$env_vars) {
     if ($name=~ /$env_name/ ) {
          #return %$env_vars{$name};
          return $$env_vars{$name};
          }
      }

   return "";
}

sub get_arch {
    my $stcmd_path = $_[0];
    use Shell qw(strings grep);        # these shell commands used below.

    # make sure we have a valid path 
    $stcmd_path = try_nfs_mount_and_file_system($stcmd_path);
    
    # make sure we have a valid file 
    # not an error if Makefile does not exist
    return if ( ! -f $stcmd_path.'/Makefile' );

    # Open up startup command file
    if ( -e $stcmd_path ) {

        my @string_list = strings ($stcmd_path.'/Makefile');    # parse Makefile in same dir as st.cmd
        
        if (@string_list) {
            my $grepArch = '^ARCH.*=' ;           
            my @archArr = grep (/$grepArch/, @string_list); # grep ARCH line in Makefile
            if (@archArr) {
                my @archStr = split("=", $archArr[$#archArr]);# split ARCH string, assume ARCH at the end
	        my $arch = $archStr[1];
                $arch =~ s/^\s+//;                       # strip leading and trailing blanks.
                $arch =~ s/\s+$//;
                log('debug',"ioc arch is: $arch");
	        $ioc_arch = $arch;
            }  
	    else {
                log('warn',"Could not locate ARCH in $stcmd_path/Makefile");
                mail("Could not locate ARCH in $stcmd_path/Makefile");
   	    }
        }
	else {
            log('warn',"Could not locate Makefile in $stcmd_path");
            mail("Could not locate Makefile in $stcmd_path");
	}
        return;
	
    } else {
        log('warn',"Could not locate $stcmd_path");
        mail("Could not locate $stcmd_path");
        return 2;
    }
}
1;
