
package AOICrawlerParser;

# This package provides subroutines for parsing ioc startup
# command files and storing st.cmd lines and pv's that are
# specifically designated for aoi's.
#

use strict;
use FileHandle;
use File::Basename;
use File::Compare;
use File::Path;
use File::stat;

require Exporter;
use POSIX;

use LogUtil qw(log);
use CommonUtil qw(mail);

our @ISA = qw(Exporter);
our @EXPORT_OK = qw(initialize_parser_aoi get_stcmd_line_list_aoi parse_startup_command_line_aoi start_parse_inline_include_aoi finished_parse_startup_command_aoi add_db_record_aoi);
our $VERSION = 1.00;

my $parser_initialized = 0;

my @aoi_list;                   # stores list of aoi's
my @plc_list;                   # stores list of plc's
my @stcmd_line_list;            # stores list of st.cmd lines
my $stcmd_line_index = 0;       # current index for stcmd_line_list
my @stcmd_include_line_list;    # stores list of include line indexes 
my @record_list =();            # stores list of pvnames

# stores hash of EPICS PV filters used on by an AOI for a specific ioc st.cmd line
my %pv_filter_hash;

# Public
# Initialize parser. Must be called each time before parse_startup_command_aoi.
sub initialize_parser_aoi {
    # initialize module globals

    # CLEANUP possibly large lists
    #undef(@aoi_list);
    #undef(@stcmd_include_line_list);
    #undef(@plc_list);
    #undef(@stcmd_line_list);
    undef(%pv_filter_hash);

    @aoi_list = ();
    @stcmd_include_line_list = ();
    @plc_list = ();
    @stcmd_line_list = ();
    @record_list = ();

    $stcmd_line_index = 0;

    $parser_initialized = 1; # good to go
    log('debug',"AOI parser initialized.");
}

# Public
sub get_stcmd_line_list_aoi {
    return \@stcmd_line_list;
}


# Public
# Parse a line in startup command file for aoi data
sub parse_startup_command_line_aoi {
    my $script_path = $_[0];
    my $script_line = $_[1];
    my $ioc_nm = $_[2];
    my $parse_err = 0;

    log('debug',"AOI parser: Parsing command line: $script_line.");

    if (!$parser_initialized) {
        log('error',"Using AOI parser before initializing. Exiting...");
        exit;
    }

    $script_line =~ s/\cM//g;

    return 0 if ($script_line =~ /^\s*$/);   # Skip blank lines

    # look for plc comment lines
    if ( $script_line =~ "^#<upc")
    {
        log('debug',"Parsing plc comment line: $script_line");
        $parse_err = parse_plc_comment($script_path,$script_line);
    }
    elsif ( $script_line =~ "^#</upc")
    {
        update_stcmd_line($script_line, $ioc_nm);
        log('debug',"Parsing plc comment line: $script_line");
        $parse_err = parse_plc_comment($script_path,$script_line);
        return $parse_err;
    }

    # look for aoi comment lines
    elsif ( $script_line =~ "^#<aoi")
    {
        log('debug',"Parsing aoi comment line: $script_line");
        $parse_err = parse_aoi_comment($script_path,$script_line);
    }
    elsif ( $script_line =~ "^#</aoi")
    {
        update_stcmd_line($script_line, $ioc_nm);
        log('debug',"Parsing aoi comment line: $script_line");
        $parse_err = parse_aoi_comment($script_path,$script_line);
        return $parse_err;
    }

    # look for  other comment lines
    elsif ( (substr($script_line,0,1) eq "#") ||
              ($script_line =~ /^\s*\/\*/) ||
              ($script_line =~ /^\s*$/) )
    {
    }

    # separate out inline include
    elsif ($script_line =~ /^<.*/)
    {
        # remove last line number from stcmd_include_line_list
        pop(@stcmd_include_line_list);
        log('debug',"Finished parsing include line: $script_line");
        return $parse_err;
    }

    update_stcmd_line($script_line, $ioc_nm);
    return $parse_err;
}


#Public
sub finished_parse_startup_command_aoi {
    my $parse_err = 0;

    if (scalar(@aoi_list) && !scalar(@stcmd_include_line_list) ) {
        log('error',"Error aoi's still capturing after read of st.cmd file: @aoi_list");
        mail("Error aoi's still capturing after end of st.cmd file. aoi: @aoi_list");
        $parse_err = 1;
    }

    if (scalar(@plc_list)  && !scalar(@stcmd_include_line_list) ) {
        log('error',"Error plc's still capturing after read of st.cmd file: @plc_list");
        mail("Error plc's still capturing after end of st.cmd file: @plc_list");
        $parse_err = 1;
    }
    log('debug',"AOI parser: finished parsing startup commands.");
    return $parse_err;
}


# Parse plc comment line
# Line assumed to be of form "#<upc_..." or "#</upc_..."
sub parse_plc_comment {
    my $script_path = $_[0];
    my $script_line = $_[1];
    my $plc_name;
    my $plc_version_pv_name;
    my $parse_err = 0;

    log('debug',"AOI parser: parsing plc comment: $script_line.") if !scalar(@record_list); 

    # make sure that plc name follows plc naming convention for
    # a parent plc (plc_machine_techsystem_function) or for a
    # child plc (plc_machine_techsystem_function_id).
    # only dashes and alphanumeric characters are allowed between underscores.

    if ( $script_line =~ /^#\<upc name\=\"plc(_[A-Za-z0-9-]+){2}(_[A-Za-z0-9-]+)?\"/ ) {

        # parse out actual plc name
        $plc_name = $script_line;
        $plc_name =~ s/^#\<upc name\=\"//;
        $plc_name =~ s/\".*\>//;

        # parse out actual plc_version_pv_name
        if ( $script_line =~ m/version/ ) {
            $plc_version_pv_name = $script_line;
            $plc_version_pv_name =~ s/^#\<upc name\=\".*\"\s+version_pv\s*=\s*\"//;
            $plc_version_pv_name =~ s/\".*\>//;
            log('debug',"-----plc_name=$plc_name--------pv=$plc_version_pv_name");
        }

        # check to se if this plc is on the currently capturing list
        foreach my $plc_info (@plc_list) {
            my @plc = @$plc_info;
    	    if ( $plc[0] eq $plc_name ) {
    	        log('error',"Duplicate upc_name: $plc_name:$script_line in $script_path.");
    	        mail("Duplicate upc_name: $plc_name:$script_line in $script_path.");
                $parse_err = 1;
    	    }
        }

    	# add new plc to the plc_list array
        if ( defined $plc_version_pv_name) {
    	    push(@plc_list, ["$plc_name","$plc_version_pv_name"]);
	} else {
    	    push(@plc_list, ["$plc_name"]);
	}
        log('debug',"UPC CAPTURING FOR upc_name: $plc_name");

        if (!scalar(@aoi_list)) {
    	    log('error',"Plc $plc_name not defined within an aoi definition.");
    	    mail("Plc $plc_name not defined within an aoi definition.");
            $parse_err = 1;
        }

    } elsif ( $script_line =~ /^#\<\/upc\>/ ) {
        if (scalar(@plc_list)) {
            my $last_plc=pop(@plc_list);
            log('debug',"DONE UPC CAPTURING  for @$last_plc[0]");
        } else {
            log('error',"Encountered error for upc ending line:$script_line in $script_path.");
    	    mail("Encountered error for upc ending line:$script_line in $script_path.");
            $parse_err = 1;
        }
    } else {
        log('error',"Unable to parse upc comment line:$script_line in $script_path.");
        mail("Unable to parse upc comment line:$script_line in $script_path.");
        $parse_err = 1;
    }
    return $parse_err;
}


# Parse up aoi comment line
# Line assumed to be of form "#<aoi_..." or "#</aoi_..."
sub parse_aoi_comment {
    my $script_path = $_[0];
    my $script_line = $_[1];

    my $aoi_name;
    my $pv_filter_string;
    my $parse_err = 0;

    log('debug',"AOI parser: parsing aoi comment: $script_line in $script_path.") if !scalar(@record_list); 
#log('info',"AOI parser: parsing aoi comment: $script_line in $script_path.");

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
                
        # log('debug',"In AOICrawlerParser.pm, working on aoi:  $aoi_name");
        
        # add this aoi to aoi currently capturing list, $aoi_list, if not already included
        foreach my $name (@aoi_list) {
    	    if ( $name eq $aoi_name ) {
    	        log('error',"Duplicate aoi_name: $aoi_name");
    	        mail("Duplicate aoi_name: $aoi_name in $script_path");
                $parse_err = 1;
    	    }
        }

#log('info',"AOI parser: aoi_name=$aoi_name.");

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
#log('info',"AOI parser: END of #<aoi comment for aoi_name=$aoi_name.");
    } elsif ( $script_line =~ /^#\<\/aoi\>/ ) {
        if (scalar(@aoi_list)) {
    	     $aoi_name = pop(@aoi_list);
    	     # also need to remove the pv_filter_list for last aoi name
    	     delete $pv_filter_hash{$aoi_name};
        } else {
    	    log('error',"Encountered error for aoi ending line in $script_path.");
    	    mail("Encountered error for aoi ending line in $script_path.");
            $parse_err = 1;
        }
#log('info',"AOI parser: END of #</aoi comment.");
    } else {
        log('error',"Unable to parse aoi comment line: $script_line in $script_path");
        mail("Unable to parse aoi comment line: $script_line in $script_path");
        $parse_err = 1;
    }
    return $parse_err;
}

# associate st.cmd line with corresponding aoi's and plc's
# store the st.cmd line in an array for later writing to database
sub update_stcmd_line {
    my $script_line = $_[0];
    my $ioc_nm = $_[1];
    my $include_line_index = '';

    if (scalar(@stcmd_include_line_list)) {
        $include_line_index = "$stcmd_include_line_list[$#stcmd_include_line_list]",
    }
    # add the st.cmd line to the array stcmd_line_list
    my @records=@record_list;
    my @aois=@aoi_list;
    my @plcs=@plc_list;
    my %pv_filters = %pv_filter_hash;
        
    my $aoiCount=scalar(@aois);
    my $plcCount=scalar(@plcs);
    my $recordCount=scalar(@record_list);

    log('debug',"AOI: [$stcmd_line_index] inc_index=$include_line_index,aois=$aoiCount,plcs=$plcCount,recs=$recordCount,stcmd=$script_line");


    if (scalar(@aois)) {
        push @stcmd_line_list,{stcmd_line_index=>"$stcmd_line_index",
                                           stcmd_include_line_index=>"$include_line_index",
                                           stcmd_line=>"$script_line",
                                           aoi_list=>\@aois,
                                           plc_list=>\@plcs,
                                           pv_filter_hash=>\%pv_filters,
                                           record_list=>\@records};
    }
    $stcmd_line_index++;
    @record_list = ();
    return;
}

# Public
sub start_parse_inline_include_aoi {
    my $script_line = $_[0];
    my $ioc_nm = $_[1];

    update_stcmd_line($script_line, $ioc_nm);
    push(@stcmd_include_line_list, $stcmd_line_index - 1);
    log('debug',"AOI parser: Parsing include line: $script_line");
    return 0;
}


# Public
sub add_db_record_aoi {
    my $rnam = $_[0];
    return if !scalar(@aoi_list); 
    push(@record_list,$rnam);
    log('debug',"$rnam");
}
1;
