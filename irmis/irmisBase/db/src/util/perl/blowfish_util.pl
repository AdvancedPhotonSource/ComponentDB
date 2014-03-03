#!/usr/bin/perl -w

#
#  Copyright (c) 2004-2005 The University of Chicago, as Operator
#  of Argonne National Laboratory.
#

# blowfish_util.pl
#   Encrypts/Decrypts input file using blowfish symmetric key
#   block cipher algorithm. Uses 16 byte shared key, although
#   underlying library supports other key lengths.
#
# Authors: Claude Saunders (saunders@aps.anl.gov)
#
#

# license-free 100% perl implementation of blowfish
use Blowfish_PP;  

##########################################################
#  GET SCRIPT ARGUMENTS
##########################################################
if (!@ARGV) {
    print_usage();
    exit;
}
while (@ARGV) {
    $command = shift @ARGV;
    if ($command =~ /-help/) {
        print_usage();
        exit;
    } elsif ($command =~ /--key=(.*)/) {
        $key = $1;
    } elsif ($command =~ /--encrypt/) {
        $direction = 'encrypt';
    } elsif ($command =~ /--decrypt/) {
        $direction = 'decrypt';
    } elsif ($command =~ /--input-file=(.*)/) {
        $input_file_name = $1;
    } elsif ($command =~ /--output-file=(.*)/) {
        $output_file_name = $1;
    } else {
        print_usage();
        exit;
    }
}
if (!$key || !$direction || !$input_file_name || !$output_file_name) {
    print_usage();
    exit;
}
if (length($key) != 16) {
    print "The --key must be 16 alpha-numeric characters long. Exiting...\n";
    exit;
}

#############################################################
# ENCRYPT INPUT FILE 
#############################################################
$packed_key = pack("a16",$key);
$blowfish_object = Crypt::Blowfish_PP->new($packed_key);

if (!$blowfish_object) {
    print "The --key must be 16 alpha-numeric characters long. Exiting...\n";
    exit;
}

# check for existence of input-file
$st = stat($input_file_name);
if (!$st) {
    print "Unable to open $input_file_name : $@ \n";
    print "Exiting...\n";
    exit;
}

# and make sure we can create output file
if (!open(OUTPUTFILE, ">$output_file_name")) {
    print "Unable to open $output_file_name : $@ \n";
    print "Exiting...\n";
    exit;
}

open(INPUTFILE, $input_file_name);

# Copy input file to output file, encrypting or decrypting

while ($read_len = read(INPUTFILE, $inbuf, 8)) {

    # blank pad if we don't have a perfect 8 byte block
    if ($read_len < 8) { print "padding\n"; }
    $inbuf .= "\040"x(8-$read_len) if $read_len < 8;

    if ($direction eq 'encrypt') {
        $outbuf = $blowfish_object->encrypt($inbuf);
    } else {
        $outbuf = $blowfish_object->decrypt($inbuf);
    }

    print OUTPUTFILE $outbuf;

}
close(INPUTFILE);
close(OUTPUTFILE);


print "Done!\n";
exit;

#############################################################
# SUBROUTINES
#############################################################


# Print out application usage text
sub print_usage {
    print "usage: blowfish_util.pl --key=<abcdefghijklmnop> --encrypt|--decrypt \n";
    print "\t--input-file=<file name> --output-file=<file name>\n";
    print "\t\tnote: --key key must be alphanumeric and 16 bytes in length\n";
}

