
package AOICrawlerParser;

# This package provides subroutines for parsing ioc startup
# command files and storing st.cmd lines and pv's that are
# specifically designated for aoi's.
#

use FileHandle;
use File::Basename;
use File::Compare;
use File::Path;
use File::stat;

require Exporter;
use POSIX;
use LogUtil;

our @ISA = qw(Exporter);
our @EXPORT = qw(initialize_parser parse_startup_command get_file_list get_stcmd_line_list );
our @EXPORT_OK = qw();
our $VERSION = 1.00;

my $parser_initialized = 0;
my $temp_file_dir;
my $cwd;
my %gbl_var;
my %nfs_mounts;
my $ioc_name;

my $pv_index = 0;
my @record_list =();

# stores list of ioc resources (ie. .dbd, .template, and .db file paths)
my @file_list;
my $file_index;

# stores list of aoi's
my @aoi_list;

# stores list of plc's
my @plc_list;

# stores list of include line indexes 
my @stcmd_include_line_list;

# stores hash of EPICS PV filters used on by an AOI for a specific ioc st.cmd line
my %pv_filter_hash;

# file path substitutions to try for those difficult to reach paths
my $path_substs;

# stores list of st.cmd lines
my @stcmd_line_list;
my $stcmd_line_index = 0;

# Initialize parser. Must be called each time before parse_startup_command.
sub initialize_parser {
    # initialize module globals
    $temp_file_dir = $_[0];
    $cwd = $_[1];
    $ioc_name = $_[2];
    $path_substs = $_[3];

    # CLEANUP possibly large lists
    undef(@file_list);
    undef(@aoi_list);
    undef(@stcmd_include_line_list);
    undef(@plc_list);
    undef(@stcmd_line_list);
    undef(%pv_filter_hash);

    # used during parsing to keep track of script variable assignments
    undef(%gbl_var);
    # used during parsing to keep track of nfs mount points
    undef(%nfs_mounts);

    # add an initial IOC_NAME entry in gbl_var to compensate for soft iocs
    #   which assume the existence of an environment variable IOC_NAME.
    $gbl_var{IOC_NAME} = $ioc_name;

    $file_index = 0;
    $pv_index = 0;
    $stcmd_line_index = 0;

    $parser_initialized = 1; # good to go
}

# Subroutines to get data after parsing complete
sub get_file_list {
    return \@file_list;
}

sub get_stcmd_line_list {
    return \@stcmd_line_list;
}

# Parse up an entire VxWorks startup command file.
sub parse_startup_command {

    #
    # arguments to subroutine
    #
    # full file path of the script to be parsed
    my $script_path = $_[0];
    my $ioc_id = $_[1];

    my $parse_err = 0;
    my $SCRIPT;
    my $stcmd_file_mod_date;

    if (!$parser_initialized) {
        LogUtil::log('error',"Using parser before initializing. Exiting...");
        exit;
    }

    # make sure we have a valid path
    $script_path = try_nfs_mount_and_file_system($script_path);

    # Open up startup command file
    if ( -e $script_path ) {
        $stcmd_file_mod_date = get_file_mod_date($script_path);

        log('debug',"script mod time = ".
            get_file_mod_date($script_path));
        if (!($SCRIPT = new FileHandle "$script_path")) {
            log('warn',"could not open startup command file: $!");
            return 2; # general parse error
        }
    } else {
        log('warn',"Could not locate $script_path");
        return 2;
    }

    # Iterate over lines in startup command file
    while ( my $script_line = <$SCRIPT> ) {

        @record_list = ();

        chomp($script_line);
	my $previous_pv_index = $pv_index;

        $script_line =~ s/\cM//g;

        # strip any leading whitespace
        if ($script_line =~ /^\s*(.*)/) {
            $script_line = $1;
        }

        next if ($script_line =~ /^\s*$/);   # Skip blank lines

        log('verbose',"sl = $script_line");

        # look for plc comment lines
        if ( $script_line =~ "^#<upc")
        {
            log('debug',"Parsing plc comment line: $script_line");
            $parse_err = parse_plc_comment($script_line);
        }
        elsif ( $script_line =~ "^#</upc")
        {
            $parse_err = update_stcmd_line($script_line, $stcmd_file_mod_date, $ioc_id);
            log('debug',"Parsing plc comment line: $script_line");
            $parse_err = parse_plc_comment($script_line);
            next;
        }


        # look for aoi comment lines
        elsif ( $script_line =~ "^#<aoi")
        {
            log('debug',"Parsing aoi comment line: $script_line");
            $parse_err = parse_aoi_comment($script_line);
        }
        elsif ( $script_line =~ "^#</aoi")
        {
            $parse_err = update_stcmd_line($script_line, $stcmd_file_mod_date, $ioc_id);
            log('debug',"Parsing aoi comment line: $script_line");
            $parse_err = parse_aoi_comment($script_line);
            next;
        }

        # look for  other comment lines
        elsif ( (substr($script_line,0,1) eq "#") ||
                  ($script_line =~ /^\s*\/\*/) ||
                  ($script_line =~ /^\s*$/) )
        {
        }

        # separate out inline include, var assignment, ld, and function call
        elsif ($script_line =~ /^<.*/)
        {

            $parse_err = update_stcmd_line($script_line, $stcmd_file_mod_date, $ioc_id);
            log('verbose',"Parsing include line: $script_line");
            $parse_err = parse_inline_include($script_line,$ioc_id);
            log('verbose',"Finished parsing include line: $script_line");
            next;

        }
        elsif ( $script_line =~ /^[\w\*]+\s*=.*/ ||
                $script_line =~ /^\(\w+\)\w+\s*=.*/) {

            $parse_err = parse_var_assignment($script_line);

        } elsif ($script_line =~ /^ld.*/) {
            # ignore ld command

        } else {

            # Assume we have a function call at this point
            my $return_val; # ignore return value since its not part of var assignment
            $parse_err = parse_function_call($script_line, \$return_val);
            if ($parse_err) {
                close($SCRIPT);
                return $parse_err;
            }
        }

        $parse_err = update_stcmd_line($script_line, $stcmd_file_mod_date, $ioc_id);

    }

    if (scalar(@aoi_list) && !scalar(@stcmd_include_line_list) ) {
        log('error',"Error aoi's still capturing after read of st.cmd: @aoi_list");
    }

    if (scalar(@plc_list)  && !scalar(@stcmd_include_line_list) ) {
        log('error',"Error plc's still capturing after read of st.cmd: @plc_list");
    }

    close($SCRIPT);
    return $parse_err;
}


# Parse plc comment line
# Line assumed to be of form "#<upc_..." or "#</upc_..."
sub parse_plc_comment {
    my $script_line = $_[0];

    my $plc_parent = 0;
    my $plc_name;

    # make sure that plc name follows plc naming convention for
    # a parent plc (plc_machine_techsystem_function) or for a
    # child plc (plc_machine_techsystem_function_id).
    # only dashes and alphanumeric characters are allowed between underscores.

    if ( $script_line =~ /^#\<upc name\=\"plc(_[A-Za-z0-9-]+){2}(_[A-Za-z0-9-]+)?\"/ ) {

        # parse out actual plc name
        $plc_name = $script_line;
        $plc_name =~ s/^#\<upc name\=\"//;
        $plc_name =~ s/\".*\>//;
        if ( $script_line =~ /^#\<upc name\=\"plc(_[A-Za-z0-9-]+){2}\"/ ) {
    	    $plc_parent = 1;
        }

        # add this plc to plc currently capturing list, $plc_list, if not already included
        foreach my $name (@plc_list) {
    	    if ( $name eq $plc_name ) {
    	        log('error',"Duplicate upc_name: $plc_name");
    	    }
        }

    	# add new plc to the plc_list array
    	push(@plc_list, "$plc_name");
        log('verbose',"UPC CAPTURING FOR upc_name: $plc_name");

    } elsif ( $script_line =~ /^#\<\/upc\>/ ) {

        if (scalar(@plc_list)) {
             log('verbose',"DONE UPC CAPTURING  for $plc_list[$#plc_list]");
    	     pop(@plc_list);
        } else {
    	    log('error',"Encountered error for upc ending line");
        }

    } else {
        log('error',"Unable to parse upc comment line.");
        return 2;
    }

    return 0;
}


# Parse up VxWorks aoi comment line
# Line assumed to be of form "#<aoi_..." or "#</aoi_..."
sub parse_aoi_comment {
    my $script_line = $_[0];

    my $aoi_parent = 0;
    my $aoi_name;
    my $pv_filter_string;

    # make sure that aoi name follows aoi naming convention for
    # a parent aoi (aoi_machine_techsystem_function) or for a
    # child aoi (aoi_machine_techsystem_function_id).
    # only dashes and alphanumeric characters are allowed between underscores.

    if ( $script_line =~ /^#\<aoi name\=\"aoi(_[A-Za-z0-9-]+){3}(_[A-Za-z0-9-]+)?\"(\s+pv_filter\=\"[A-Za-z0-9-:_]+(,\s?[A-Za-z0-9-:_]+)*\")?\>/ ) {

        # parse out actual aoi name
        $aoi_name = $script_line;
        $aoi_name =~ s/^#\<aoi name\=\"//;
        
        # parse out end part of tag that may include a pv_filter attribute
                
        $aoi_name =~ s/\"(\s+pv_filter\=\"[A-Za-z0-9-:_]+(,\s?[A-Za-z0-9-:_]+)*\")?\>//;
                
        # log('warn',"In AOICrawlerParser.pm, working on aoi:  $aoi_name");
        
        if ( $script_line =~ /^#\<aoi name\=\"aoi(_[A-Za-z0-9-]+){3}\"\s*[A-Za-z0-9-:_\"]*\>/ ) {
    	    $aoi_parent = 1;
        }

        # add this aoi to aoi currently capturing list, $aoi_list, if not already included
        foreach my $name (@aoi_list) {
    	    if ( $name eq $aoi_name ) {
    	        log('warning',"Duplicate aoi_name: $aoi_name");
    	    }
        }

    	# add new aoi to the aoi_list array
    	push(@aoi_list, "$aoi_name");
    	
    	# parse out optional pv_filter attribute from the aoi comment line
    	# each aoi name can have an array of EPICS PV filters 
    	
    	if ( $script_line =~ /^#\<aoi name\=\"aoi(_[A-Za-z0-9-]+){3}(_[A-Za-z0-9-]+)?\"(\s+pv_filter\=\"[A-Za-z0-9-:_]+(,\s?[A-Za-z0-9-:_]+)*\")+\>/ ) {
    		# pv_filter attribute exists
    		# capture pv_filter strings into an array
    		
    		$pv_filter_string = $script_line;
    		$pv_filter_string =~ s/^#\<aoi name\=\"aoi(_[A-Za-z0-9-]+){3}(_[A-Za-z0-9-]+)?\"\s+pv_filter\=\"//;
    		
    		# parse out end part of tag
    		
		$pv_filter_string =~ s/\"\>//;    	
		
		# log('warn',"In AOICrawlerParser.pm, aoi $aoi_name has pv filter string: $pv_filter_string");
    		    		
    		# assign aoi name to $pv_filter hash
    		$pv_filter_hash{$aoi_name} = $pv_filter_string;
    		
    		# while ( ($key, $value) = each %pv_filter_hash ) {
    		#	log('warn',"In AOICrawlerParser.pm, pv_filter_hash:  $key => $value");
    		# }
    	}

    } elsif ( $script_line =~ /^#\<\/aoi\>/ ) {

        if (scalar(@aoi_list)) {
    	     $aoi_name = pop(@aoi_list);
    	     
    	     # also need to remove the pv_filter_list for last aoi name
    	     
    	     delete $pv_filter_hash{$aoi_name};
    	     
        } else {
    	    log('error',"Encountered error for aoi ending line");
        }

    } else {
        log('error',"Unable to parse aoi comment line: $script_line");
        return 2;
    }

    return 0;
}

# associate st.cmd line with corresponding aoi's and plc's
# store the st.cmd line in an array for later writing to database
sub update_stcmd_line {
    my $script_line = $_[0];
    my $file_mod_date = $_[1];
    my $ioc_id = $_[2];
    my $include_line_index = '';

    if (scalar(@stcmd_include_line_list)) {
        $include_line_index = "$stcmd_include_line_list[$#stcmd_include_line_list]",
    }
    # add the st.cmd line to the array stcmd_line_list
    my @records=@record_list;
    my @aois=@aoi_list;
    my @plcs=@plc_list;
    my %pv_filters = %pv_filter_hash;
        
    $stcmd_line_list[$stcmd_line_index] = {stcmd_line_index=>"$stcmd_line_index",
                                           stcmd_include_line_index=>"$include_line_index",
                                           stcmd_line=>"$script_line",
                                           ioc_id=>"$ioc_id",
                                           aoi_list=>\@aois,
                                           plc_list=>\@plcs,
                                           pv_filter_hash=>\%pv_filters,
                                           record_list=>\@records};
    
    $stcmd_line_index++;

    return 0;
}

# Parse up VxWorks inline include
# Line assumed to be of form "< ../nfsCommands"
sub parse_inline_include {

    my $script_line = $_[0];
    my $ioc_id = $_[1];
    my $script_line_number = $_[2];
    my $include_path;

    # handle weird case where someone does "< asdstdPreInit()" - still just file path
    if ($script_line =~ /^<\s*([\S]*)\(\)\s*#*/ ||
        $script_line =~ /^<\s*([\S]*)\s*#*/) {
        $include_path = $1;
    } else {
        log('warn',"unable to parse inline include $script_line");
        return 2;
    }

    # add PREVIOUS stcmd line number to stcmd_include_line_list
    push(@stcmd_include_line_list, $stcmd_line_index - 1);

    # substitute for any $(VAR) or ${VAR}
    $include_path = substitute_env_vars($include_path);

    # make sure path is fully qualified using module global $cwd
    if (substr($include_path,0,1) ne "/") {
        $include_path = $cwd . "/" . $include_path;
    }

    # make sure we have a valid path
    $include_path = try_nfs_mount_and_file_system($include_path);

    $parse_err = parse_startup_command($include_path,$ioc_id);

    # remove last line number from stcmd_include_line_list
    pop(@stcmd_include_line_list);

    return $parse_err;
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

    if (! -e $path) {
        foreach my $subpath (keys %$path_substs) {
            my $substitution = $path_substs->{$subpath};
            if ($path =~ /$subpath/ ) {
                my $new_path = $path;
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
                my $new_path = $path;
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
            parse_function_call($f . $args, \$return_val);
            $var_value = $return_val;

        } else {
            log('warn',"unable to parse var assignment with complex rval $script_line");
            return 2;
        }
        $gbl_var{$var_name} = $var_value;
        log('debug',"gbl_var{$var_name} = $var_value");

    } else {
        log('warn',"unable to parse var assignment $script_line");
        return 2;
    }
    return 0;
}

# Parse up VxWorks function call.
# Line assumed to be of form "doThisFunc(arg1, arg2)"
sub parse_function_call {
    my $script_line = $_[0];
    my $return_val = $_[1];

    log('debug',"parse_function_call");

    # get function name
    if ($script_line =~ /(^\w+)\s*(\(*.*)#*/) {
        $f = $1;
        $args = $2;
        log('debug',"function $f with args $args");
    } else {
        log('warn',"unable to parse function call $script_line");
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

    # make sure we have a valid path
    $path = try_nfs_mount_and_file_system($path);

    log('debug',"actual chdir to $path");

    chdir $path;

    # update the module global $cwd
    $cwd = getcwd();
    return 0;
}

# Process VxWorks nfsMount(args)
sub process_nfs_mount {
    my $args = $_[0];

    # parse up args into array
    my @parsed_args = parse_args($args);

    # we should have 3 args (host, file_system, mount_point)
    if (scalar @parsed_args != 3) {
        log('warn',"expecting 3 arguments to nfsMount, skipping");
        return 2;
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
    my @parsed_args = parse_args($args);

    # path of dbd file should be zero'th arg
    my $dbd_path = $parsed_args[0];

    # check to see if a .db or .template file has been given
    my $file_name = basename($dbd_path);
    if ($file_name =~ /[^\.].db$/ ||
        $file_name =~ /[^\.].template$/) {
        log('warn',"dbLoadDatabase has been called on $dbd_path");
        # redirect to dbLoadRecords handling
        return process_db_load_records($args);
    }
    # for AOI CRAWLER DO NOTHING

    return 0;
}

# Process EPICS dbLoadRecords(args)
sub process_db_load_records {
    my $args = $_[0];

    # parse up args into array
    my @parsed_args = parse_args($args);

    my $num_args = @parsed_args;
    my $db_path;
    my $file_mtime = "";

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

        # fully qualify path if needed
        if (substr($db_path,0,1) ne "/") {
            $db_path = $cwd . "/" . $db_path;
        }

        # make sure we have a valid path
        $db_path = try_nfs_mount_and_file_system($db_path);

        if ( !( -e $db_path ) ) {
            log('warn',"db file $db_path unreachable");
        } else {
            $file_mtime = stat($db_path)->mtime;
        }

        # add to master list of ioc resources
        $file_list[$file_index] =
        {id=>$file_index+1, type=>"db", path=>"$db_path",
         subst=>"", mtime=>"$file_mtime"};
        $file_index++;


    } elsif ($num_args == 2 || $num_args == 3) {
        # we have a substitution string to deal with

        if ($num_args == 2) {
            $db_path = $parsed_args[0];
            $subst_str = $parsed_args[1];
        } else {
            # we've been redirected here from process_db_load_database, so
            #   the arguments must be interpreted as from dbLoadDatabase
            $db_path = $parsed_args[0];
            $subst_str = $parsed_args[2];
        }

        # fully qualify path if needed
        if (substr($db_path,0,1) ne "/") {
            $db_path = $cwd . "/" . $db_path;
        }

        # make sure we have a valid path
        $db_path = try_nfs_mount_and_file_system($db_path);

        if ( !( -e $db_path ) ) {
            log('warn',"db file $db_path unreachable");
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
        log('warn',"don't know how to handle these arguments: $args");
        return 2;
    }

    if ($file_mtime) {
        log('debug',"calling parse_db with $db_path");
        return parse_db($db_path);
    } else {
        return 0;
    }
}

sub process_db_load_template {
    my $args = $_[0];

    # parse up args into array
    my @parsed_args = parse_args($args);

    # path of template file should be zero'th arg
    my $template_path = $parsed_args[0];

    my $file_mtime = "";

    # fully qualify path if needed
    if (substr($template_path,0,1) ne "/") {
        $template_path = $cwd . "/" . $template_path;
    }

    # make sure we have a valid path
    $template_path = try_nfs_mount_and_file_system($template_path);

    if ( !( -e $template_path ) ) {
        log('warn',"template file $template_path unreachable");
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
        return 0;
    }
}

sub parse_db {
    @record_list = ();
    return if !scalar(@aoi_list); 

    # full path of db file to be processed
    my $db_path = $_[0];

    # for sorting integers - lead char has to occur before 0 ascii
    my $lead = "########";

    if ( !( -e $db_path ) ) {
        log('warn',"db file $db_path unreachable");
        return 2;
    }
    log('debug',"opening $db_path");
    open( DBFILE, $db_path );
    my $llp = "";   # last line processed (either contains 'record' or 'field')

    # NOTE: redo this parsing, as it will not work in some boundary cases
    while (<DBFILE>) {
        # ignore leading comment - # is legal in desc, inp etc arguments
        if (/^\s*#/) {
            next;
        }
        # assume that 'record' is only token on this line
        if (/\bg*record\s*\((.*)\)/) {
            # must have at least proc'd 1 rec line, so have rnam, rtyp and pv_index data
#            if ( $llp eq "record") {
#
#                # this code gets executed if a .db file has a record with no fields
#                #  (eg stringin) - it gives empty fldn, fldv fields
#                $fldn         = "";
#                $fldv         = "";
#                $pad_pv_index =
#                    substr( $lead, 0, 8 - length($pv_index) ) . $pv_index;
#
#                $new_rec_fld = {rec_nm=>"$rnam", fld_nm=>"$fldn", rec_typ=>"$rtyp",
#                                fld_val=>"$fldv", file_index=>$file_index-1 };
#
#                $rnam_fldn[$pv_index++] = $new_rec_fld;
#            }
            $rec_token = $1;    #get the part within parenthesis
            ( $rtyp, $rnam ) = split /,/,
            $rec_token;       #get the individual strings
            $rnam =~ s/\s//g;   #remove any white space
            $rnam =~ s/"//g;    #the record name can be surrounded by 0,1 or 2 "
            $llp = "record";
            push(@record_list,$rnam);
        }

        #assume that 'field' is only token on this line
#        if (/\bfield\s*\((.*)\)/) {
#            $fld_token = $1;    #get the part within parenthesis
#            /"(.*)"/;
#            $fldv = $1;         #the stuff between the " ".
#            ( $fldn, $x ) = split( /,/, $fld_token );
#            $fldn =~ s/ //g;    #get rid of surrounding possible spaces
#            $pad_pv_index =
#                substr( $lead, 0, 8 - length($pv_index) ) . $pv_index;
#
#            $new_rec_fld = {rec_nm=>"$rnam", fld_nm=>"$fldn", rec_typ=>"$rtyp",
#                            fld_val=>"$fldv", file_index=>$file_index-1 };
#
#            $rnam_fldn[$pv_index++] = $new_rec_fld;
#
#            $llp = "field";    #last line processed is a 'field' line
#        }    #end if 'field'

    }   #end while DBFILE
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

    log('debug',"args to process_sprint $args");

    # parse up args into array
    my @parsed_args = parse_args($args);
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
        log('warn',"hit sprintf case with less than 2 arguments, not sure what to do");
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
        if ($var_name && $var_value) {
            log('debug',"putenv creating new gbl_var $var_name with value $var_value");
            $gbl_var{$var_name} = $var_value;
        }
    } else {
        log('warn',"putenv unable to parse var assignment $arg0");
        return 2;
    }
    return 0;
}

# Parse up VxWorks function arguments.
# Return array of values, resolving any variables into their actual value
sub parse_args {
    my $args = $_[0];
    my $orig_args = $args;

    my @parsed_args = ();

    # strip off any surrounding ()
    if ($args =~ /^\((.*)\)/) {
        $args = $1;
    }

    # strip off any leading space
    if ($args =~ /^\s*(.*)/) {
        $args = $1;
    }

    # substitute
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
        } elsif ( ($args =~ /^(0x[0-9ABCDEFabcdef]+)[, ]*(.*)/ ) ||
                  ($args =~ /^(\d+)[, ]*(.*)/) ) {
                $arg_value = $1; # a literal argument
                $args = $2;

        # see if we have a variable that needs to be dereferenced
        } elsif ($args =~ /^([A-Za-z]+\w*)[, ]*(.*)/) {
            $var_name = $1; # refers to a variable which we must dereference
            $arg_value = $gbl_var{$var_name};
            if (!$arg_value) {
                log('debug',"new variable $var_name encountered");
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
            log('warn',"unable to parse out arguments");
            return $orig_args;
        }

        push @parsed_args, "$arg_value";

        # strip leading whitespace for next iteration
        if ($args =~ /\s*(.*)/) {
            $args = $1;
        }
    }
    return @parsed_args;
}

# Locate all $(VAR) or ${VAR} in a string and attempt
#   to replace with value from gbl_var
sub substitute_env_vars {
    my $str = $_[0];
    my $original_str = $str;

    # loop until no more vars found ie. $(var)
    while ($str =~ /^([^\$]*)\$[\({](\w+)[\)}](.*)/) {

        $leading_str = $1;
        $subst_var = $2;
        $trailing_str = $3;

        $subst_val = $gbl_var{$subst_var};
        if (!$subst_val) {
            log('warn',"unable to dereference variable $subst_var");

            # Hack here until we get our developers to replace usage of EPICS_HOST_ARCH
            #  environment variable with ARCH
            if ($subst_var eq "EPICS_HOST_ARCH") {
                $subst_val = $gbl_var{ARCH};
                if (!$subst_val) {
                    log('warn',"substituting ARCH for EPICS_HOST_ARCH failed");
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

# Return mtime of given file in YYYYMMDDHHMMSS format
sub get_file_mod_date {
    my $file_name = $_[0];    # full file path

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

1;



