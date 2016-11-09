#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""


# 
# Subprocess class
#

#######################################################################

import os
import subprocess
import platform

from cdb.common.utility.loggingManager import LoggingManager
from cdb.common.exceptions.commandFailed import CommandFailed

#######################################################################

class CdbSubprocess(subprocess.Popen):

    def __init__(self, args, bufsize=0, executable=None, stdin=None, stdout=subprocess.PIPE, stderr=subprocess.PIPE, preexec_fn=None, close_fds=False, shell=True, cwd=None, env=None, universal_newlines=False, startupinfo=None, creationflags=0, useExceptions=True, quietMode=False):
        """ Overrides Popen constructor with more appropriate defaults. """
        subprocess.Popen.__init__(self, args, bufsize, executable, stdin, stdout, stderr, preexec_fn, close_fds, shell, cwd, env, universal_newlines, startupinfo, creationflags)
        self.logger = LoggingManager.getInstance().getLogger(self.__class__.__name__)
        self._stdout = None
        self._stderr = None
        self._args = args
        self.useExceptions = useExceptions
        self.quietMode = quietMode

    def __commandLog(self):
        # Not very useful to show the name of this file.
        # Walk up the stack to find the caller.
        import traceback
        stack =  traceback.extract_stack()
        for i in range(2, len(stack)):
            if stack[-i][0] != stack[-1][0]:
                fileName, lineNumber, functionName, text = stack[-i]
                break
            else:
                fileName = lineNumber = functionName = text = '?'

        self.logger.debug('From [%s:%s] Invoking: [%s]' % (os.path.basename(fileName), lineNumber, self._args))

    def run(self, input=None):
        """ Run subprocess. """
        if not self.quietMode:
            self.__commandLog()
        (self._stdout, self._stderr) = subprocess.Popen.communicate(self, input)
        if not self.quietMode:
            self.logger.debug('Exit status: %s' % self.returncode)
        if self.returncode != 0 and self.useExceptions:
            if not self.quietMode:
                self.logger.debug('StdOut: %s' % self._stdout)
                self.logger.debug('StdErr: %s' % self._stderr)
            error = self._stderr.strip()
            if error == '':
                error = self._stdout.strip()
            raise CommandFailed('%s' % (error))
        return (self._stdout, self._stderr) 

    def getLogger(self):
        return self.logger

    def getArgs(self):
        return self._args

    def getStdOut(self):
        return self._stdout

    def getStdErr(self):
        return self._stderr

    def getExitStatus(self):
        return self.returncode

# Convenience function for getting subprocess.
def getSubprocess(command):
    if platform.system() != 'Windows':
        close_fds = True
    else:
        close_fds = False
    p = CdbSubprocess(command, close_fds=close_fds)
    return p

# Convenience function for executing command.
def executeCommand(command):
    """ Create subprocess and run it, return subprocess object. """
    p = getSubprocess(command)
    p.run()
    return p

# Convenience function for executing command that may fail, and we do not
# care about the failure.
def executeCommandAndIgnoreFailure(command):
    """ Create subprocess, run it, igore any failures, and return subprocess object. """
    p = getSubprocess(command)
    try:
        p.run()
    except CommandFailed, ex:
        p.getLogger().debug('Command failed, stdout: %s, stderr: %s' % (p.getStdOut(), p.getStdErr()))
    return p

def executeCommandAndLogToStdOut(command):
    """ Execute command, display output to stdout, maintain log file and return subprocess object. """
    p = getSubprocess(command)
    p._commandLog()

    while True:
        outp = p.stdout.readline()
        if not outp:
            break
        print outp,

    retval = p.wait()

    p._logger.debug('Exit status: %s' % retval)

    if retval != 0:
        error = ''
        while True:
            err = p.stderr.readline()
            if not err:
                break
            error += err
        raise CommandFailed(error)
    return p

#######################################################################
# Testing.

if __name__ == '__main__':
    p = CdbSubprocess('ls -l', useExceptions=False)
    p.run()
    print p.getStdOut()
    print p.getStdErr()
    print p.getExitStatus()

