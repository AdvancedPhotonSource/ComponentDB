package ADLCrawlerParser;

# This package provides routines for parsing MEDM adl files for adl_crawler.
#

use FileHandle;
use File::Basename;
use Cwd;

use LogUtil;

require Exporter;

our @ISA = qw(Exporter);
our @EXPORT = qw(@adlDirs @epics_display_path_list $sub_level $children @adl_hits %seen &is_top_parent &parse_adlfile);
our @EXPORT_OK = qw();
our $VERSION = 1.00;

###################################################
# test to see if adl file is a <top> parent or
# if it is a child (i.e. needs macro parameter)
# Public
###################################################
sub is_top_parent {
   my ($parent) = $_[0];
   if (! open (TPFN, "$parent")) {
       log('warn',"Cannot open $parent: $@");
       return -2;
   }
   while (<TPFN>) {
      if(/(chan)="(.+)"/) {
         if($2 =~ /\$\(/) {
            close(TPFN);
            return -2;
         }
      }
      if(/(rdbk)="(.+)"/) {
         if($2 =~ /\$\(/) {
            close(TPFN);
            return -2;
         }
      }
      if(/(ctrl)="(.+)"/) {
         if($2 =~ /\$\(/) {
            close(TPFN);
            return -2;
         }
      }
      ### test if related display needs macro subst - then this is also a child
      if(/display\[\d+\]/) {
         $argstr = <TPFN>;
         while (<TPFN>) {
            if (/\}/) {last};
            $argstr = $argstr.$_;
            chomp($argstr);
         }
         if($argstr =~ /args="(.+?)"/) {
            $args = "'".$1."'";
            if ($args =~ /\$\(/) {
               close(TPFN);
               return -1;    #this is a child file
            } #end test args has $
         } #end ?is this an argument string
      } #end ? is this a related display
   } # end reading this adl file
   close(TPFN);
   return 1; # This is a top parent
}


###################################################
# parse adl files and related display files keeping
# pv references for DB update
# Public
###################################################
sub parse_adlfile {
    my ($parentL,$argsL,$file,$args) = @_;
    my @parentList = @$parentL;
    my @argsList = @$argsL;
    my $newargs;
    my $argCount;
    my $name;
    my $pvname;
    my $adlModTime = 0;
    my $startDir = getcwd();
    my $rel_info;
    my $currentDir;
    my $filename;
    my $i;
    my $namestring; 

    #log('debug',"parse_adlfile: startDir=$startDir file=$file args=$args argsList=@argsList");

    my $full_path_file = get_full_path_name($file);	#see rules for MEDM search paths
    if ($full_path_file eq "NOTFOUND") {
        log('warn',"File not found: $file from parent $parentList[$#argsList]");
        find_and_log_adlfile($file);
        return -2;		#could not find the related display....
    }

    # getcwd changes /usr/local/iocapps (from EPICS_DISPLAY_PATH)
    # to /net/helios/iocapps
    chdir dirname($full_path_file);
    $currentDir = getcwd();
    my $base = basename($full_path_file);
    $full_path_file = $currentDir."/".$base;

    if ($children{"$full_path_file.$args"} ) {return;}	# already scanned

    $seen{$full_path_file} = 1 if !exists $seen{$full_path_file};
    $children{"$full_path_file.$args"} = 1;

    push(@parentList, $full_path_file);
    push(@argsList, $args);

    $sub_level++;
    $filename = $full_path_file;

    $rel_info = " ";
    for ($i = 0; $i <= $#argsList; $i++) {
        $rel_info = $rel_info." ".$argsList[$#argsList-$i]." ".$parentList[$#argsList-$i];
    }

    my $TEMP = new FileHandle "$filename";
    if ( !$TEMP ) {
        log('warn',"Could not open $filename: parentList=@parentList");
        return;
    }

    my @pv_list = ();
    my @pv_macro_list = ();

    while (my $adlLine = <$TEMP>) {
      
        if( $adlLine =~ m/(chan)="(.+)"/) {
            $nameString = $2;
            ($pvname,$argCount) = replaceMacros($nameString,\@argsList);
            if ( $pvname =~ /\$\(/ || $pvname =~ /\$\{/ ) {   #### )}
                log('warn',"Macro present in chan $pvname\t$argCount\t@argsList\t@parentList");
            } else {
                if ($argCount){
                  push (@pv_macro_list,[$pvname,$argCount]);
                } else {
                  push (@pv_list,[$pvname]);
                }
            }
        }
        if( $adlLine =~ m/(rdbk)="(.+)"/) {
            $nameString = $2;
            ($pvname,$argCount) = replaceMacros($nameString,\@argsList);
            if ( $pvname =~ /\$\(/ || $pvname =~ /\$\{/ ) {   #### )}
                log('warn',"Macro present in rdbk $pvname\t$argCount\t@argsList\t@parentList");
            } else {
                if ($argCount){
                  push (@pv_macro_list,[$pvname,$argCount]);
                } else {
                  push (@pv_list,[$pvname]);
                }
            }
        }
        if( $adlLine =~ m/(ctrl)="(.+)"/) {
            $nameString = $2;
            ($pvname,$argCount) = replaceMacros($nameString,\@argsList);
            if ( $pvname =~ /\$\(/ || $pvname =~ /\$\{/ ) {   #### )}
                log('warn',"Macro present in ctrl $pvname\t$argCount\t@argsList\t@parentList");
            } else {
                if ($argCount){
                  push (@pv_macro_list,[$pvname,$argCount]);
                } else {
                  push (@pv_list,[$pvname]);
                }
            }
        }
        if( $adlLine =~ m/display\[\d+\]/) {
            $argstr = $adlLine;
            while (<$TEMP>) {    #### {
                if (/\}/) {last};
                $argstr = $argstr.$_;
                chomp($argstr);
            }
            $adlname="";
            if($argstr =~ /args="(.+?)"/) {
                $newargs = "'".$1."'";
                $newargs =~ s/\s+$//;  # Remove trailing whitespace
                if ($argstr !~ m/name="(.+?)"/) {
                    log('warn',"No related display name present in $filename");
                } else {
                    ($relatedAdl,$argCount) = replaceMacros($1,\@argsList);
                    #log('debug',"RELATED DISPLAY: $relatedAdl args=$newargs parentList=@parentList");
                    #log('debug',"RELATED DISPLAY: args=$newargs");
                    if ($file ne $relatedAdl) {
	                    parse_adlfile(\@parentList,\@argsList,$relatedAdl,$newargs);
                        chdir $currentDir;
                    }
                }
            }
        }
    }
    push (@adl_hits,[$filename,$rel_info,\@pv_macro_list,\@pv_list]);
    close ($TEMP);
    $sub_level--;
    chdir $startDir;
}


###################################################
# parse a macro Definition string e.g. "A=1,B=3,C=9" 
# Private
###################################################
sub parseMacroDefinitionString {
    my ($defString) = @_;
    my @defList = ();
    my %defHash = ();
    my $name = "";
    my $val;
    my $string;

    $string = $defString;
    $string =~ s/^'//; # Remove leading single quote mark
    $string =~ s/'$//; # Remove trailing single quote mark
    $string =~ s/\s+=/=/; # Remove whitespace before equal sign
    $string =~ s/=\s+/=/; # Remove whitespace after equal sign
    if ($string =~ m/,/ ) {
        @defList = split(/,/,$string);
    } else {
        @defList = split(/ /,$string);
    }
    foreach $def (@defList) { 
        ($name,$val) = split(/=/,$def);
        if (!defined $val) {
            log('warn',"Invalid macro substitution string: $def in $defString") if !$val;
            next;
        }
        $name =~ s/^\s+//;  # Remove leading whitespace
        $name =~ s/\s+$//;  # Remove trailing whitespace
        $val =~ s/^\s+//;  # Remove leading whitespace
        $val =~ s/\s+$//;  # Remove trailing whitespace
        $defHash{$name} = $val;
    }
    return %defHash;
}


###################################################
# Do macro substitutions on a adl string using a list
# of substitution strings e.g.
# adl string : $(UNIT):Vert:Kb3AO 
# Sub strings: 'UNIT=$(UNIT)$(ID)','UNIT=S1FB,ID=ID1'
# Private
###################################################
sub replaceMacros {
    my ($adlString,$arg_list_ref) = @_;
    my $argString;
    my $newArgString;
    my $newAdlString;
    my $i = 0;

    my $argIndex = scalar(@$arg_list_ref)-1; 
    return ($adlString,0) if $argIndex == 0;
    return ($adlString,$i) if ( $adlString !~ m/\$[\{\(]/ );  #no macros present

    $argString = $arg_list_ref->[$argIndex];
    return ($adlString,$i) if ( $argString eq "''" );  #no substitutions present
    return (simpleMSI($adlString,$argString),1) if $argIndex == 1;

    $newArgString = $argString;
    for ($i = 1; $i <= $argIndex; $i++) {
        last if ( "$argString" !~ m/\$[\{\(]/ );  # )} no macros present
        $newArgString = simpleMSI($argString,$arg_list_ref->[$argIndex-$i]);
        $argString = $newArgString;
    }
    $newAdlString = simpleMSI($adlString,$newArgString);
    return ($newAdlString,$i);
}

###################################################
# Do macro substitution on a string from an adl file
# using a single substitution string
# Private
###################################################
sub simpleMSI {
   my ($nameString,$argString) = @_;
   my $name;
   my %argHash = ();
   my $str = "";
   my $string;

   #log('debug',"START simpleMSI: nameString=$nameString argString=$argString");
   return $nameString if $argString eq "''";

   %argHash = parseMacroDefinitionString($argString);

   $string = $nameString;
   $str = $string;
   foreach $name (keys %argHash) {
       #log ('debug',"simpleMSI: name= $name hash value=$argHash{$name}");
       $string =~ s/\$\($name\)/$argHash{$name}/g; 
       $string =~ s/\$\{$name\}/$argHash{$name}/g; 
   }
   return $string;
}


###################################################
# Get the full path name to a file
# Private
###################################################
sub get_full_path_name {
    my ($readfile) = @_;	# name of file to search for, parent directory

    if (-e $readfile) {
        if ( $readfile !~ m/^\// ) {
            my $currentDir = getcwd();
            return "$currentDir/$readfile";
        } else {
            return $readfile;
        }
    }

    my $fn;
    my $dir;
    foreach $dir (@epics_display_path_list) {
        $fn = "$dir"."/"."$readfile";
        if (-e $fn) { return $fn; }
        $fn =~ s/ *$//g;
        if (-e $fn) { return $fn; }
        $fn = $fn . ".adl";
        if (-e $fn) { return $fn; }
    }
    return "NOTFOUND";
}
   

###################################################
# Print a list of existing full path file names
# Private
###################################################
sub find_and_log_adlfile {
    my ($fname) = @_;	# name of file to search for
    my @file_list = ();
    my $fn;
    my $dir;

    return if ($fname =~ /^\// );

    foreach $dir (@adlDirs) {
        next if $dir =~ m/^#/;
        chomp $dir;
        next if !-d $dir;
        $fn = "$dir/$fname";
        if (-f $fn) {
            push (@file_list,$fn);
        }

    }
    foreach $dir (@epics_display_path_list) {
        $fn = "$dir/$fname";
        if (-f $fn) {
            push (@file_list,$fn);
        }
    }
    log ('debug',"Existing $fname files: @file_list") if $#file_list+1;
    return;
}
   
1;

