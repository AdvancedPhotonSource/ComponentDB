#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import os

DIST_ROOT_DIRECTORY_ENV_KEY = "CDB_ROOT_DIR";
PYTHON_SRC_DIST_PATH = 'src/python'
DIST_VERSION_FILE_PATH = 'etc/version'
PYTHON_SETUP_FILE = 'setup.py'

rootDir = os.getenv(DIST_ROOT_DIRECTORY_ENV_KEY)
if rootDir is None:
    raise EnvironmentError('Please run setup.sh from the root directory of the cdb distribution.')


def getDistVersion():
    versionFilePath = '%s/%s' % (rootDir, DIST_VERSION_FILE_PATH)
    return open(versionFilePath, 'r').read().split('\n')[0]

setupFilePath = "%s/%s/%s" % (rootDir, PYTHON_SRC_DIST_PATH, PYTHON_SETUP_FILE)
setupFile = open(setupFilePath, 'r')

newSetupFileContents = ""

projectName = ""

for line in setupFile.readlines():
    if 'name' in line:
        projectName = line.split("'")[1]
    if 'version' in line:
        versionLineSplit = line.split("'")
        versionNumber = versionLineSplit[1]

        releaseDev = ('.DEV' in getDistVersion())

        if releaseDev:
            if 'dev' in versionNumber:
                # A previous dev release exists
                devVersionSplit = versionNumber.split('dev')
                devVersion = int(devVersionSplit[1]) + 1
                versionNumber = devVersionSplit[0]
                versionNumber += 'dev' + str(devVersion)
            else:
                # First dev release for the planned release
                versionNumber = getDistVersion().lower() + "0"
        else:
            # Production release of cdb_api
            versionNumber = getDistVersion()

        response = raw_input('Continue with releasing version [%s] of cdb_api to pip: [Y/n]: ' % versionNumber)

        if response == '' or response.lower() == 'y':
            line = versionLineSplit[0] + "'"
            line += versionNumber + "'"
            line += versionLineSplit[2]
        elif response.lower() == 'n':
            print 'Exiting script'
            quit()
        else:
            raise IOError("Invalid input given %s" % response)

    newSetupFileContents += line

setupFile = open(setupFilePath, 'r')
originalSetupFileContents = setupFile.read()

setupFile = open(setupFilePath, 'w+')
setupFile.write(newSetupFileContents)
setupFile.close()


os.chdir('%s/%s' % (rootDir, PYTHON_SRC_DIST_PATH))
buildExit = os.system('python %s sdist' % setupFilePath)

if buildExit == 0:
    tarFileName = '%s-%s.tar.gz' % (projectName, versionNumber)

    # Attempt to upload to pip
    distFilePath = 'dist/%s' % tarFileName

    pipExit = os.system('twine upload %s' % distFilePath)

    if pipExit != 0:
        setupFile = open(setupFilePath, 'w')
        setupFile.write(originalSetupFileContents)
        setupFile.close()
