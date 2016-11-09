#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


import sys
from optparse import OptionParser
from cStringIO import StringIO

class CdbOptionParser(OptionParser):
    def __init__(self):
        OptionParser.__init__(self, add_help_option=False)

    def replaceKeys(self, output):
        replacementMap = {
            'usage:' : 'Usage:',
            'options:' : 'Options:',
        }
        result = output
        for (key, value) in replacementMap.items():
            result = result.replace(key, value)
        return result

    def printUsage(self, file=None):
        self.print_usage(file)

    # Replaces base class method
    def print_usage(self, file=None):
        sysStdout = sys.stdout
        cliStdout = StringIO()
        sys.stdout = cliStdout
        OptionParser.print_usage(self, file)
        sys.stdout = sysStdout
        print self.replaceKeys(cliStdout.getvalue())

    def printHelp(self, file=None):
        self.printHelp(file)

    # Replaces base class method
    def print_help(self, file=None):
        sysStdout = sys.stdout
        cliStdout = StringIO()
        sys.stdout = cliStdout
        OptionParser.print_help(self, file)
        sys.stdout = sysStdout
        print self.replaceKeys(cliStdout.getvalue())

#######################################################################
# Testing

if __name__ == '__main__':
    p = CdbOptionParser()
    p.add_option('-f', '--file', dest='filename',
        help='write report to FILE', metavar='FILE')
    p.add_option('-q', '--quiet',
        action='store_false', dest='verbose', default=True,
        help='do not print log messages to standard output')
    p.parse_args()
    p.print_usage()
    p.print_help()
