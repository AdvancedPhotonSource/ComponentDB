#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""

import os
from pathlib import Path
import shutil

DIST_ROOT_DIRECTORY_ENV_KEY = "CDB_ROOT_DIR"
PYTHON_SRC_DIST_PATH = 'tools/developer_tools/python-client'
DIST_VERSION_FILE_PATH = 'etc/version'
PYTHON_API_SETUP_FILE = 'setup-api.py'
PYTHON_CLI_SETUP_FILE = 'setup-cli.py'

rootDir = os.getenv(DIST_ROOT_DIRECTORY_ENV_KEY)
if rootDir is None:
    raise EnvironmentError('Please run setup.sh from the root directory of the cdb distribution.')


def getDistVersion():
    versionFilePath = '%s/%s' % (rootDir, DIST_VERSION_FILE_PATH)
    return open(versionFilePath, 'r').read().split('\n')[0]

def publish_api(setup_file):
    setupFilePath = "%s/%s/%s" % (rootDir, PYTHON_SRC_DIST_PATH, setup_file)
    setupFile = open(setupFilePath, 'r')

    newSetupFileContents = ""

    projectName = ""

    for line in setupFile.readlines():
        if 'name=' in line:
            projectName = line.split('"')[1]
        if 'version=' in line:
            versionLineSplit = line.split('"')
            versionNumber = versionLineSplit[1]

        '''Code commented out aides in automatically generating the next dev version... Will handle manually for now.'''
        #     releaseDev = ('.DEV' in getDistVersion())

        #     if releaseDev:
        #         if 'dev' in versionNumber:
        #             # A previous dev release exists
        #             devVersionSplit = versionNumber.split('dev')
        #             devVersion = int(devVersionSplit[1]) + 1
        #             versionNumber = devVersionSplit[0]
        #             versionNumber += 'dev' + str(devVersion)
        #         else:
        #             # First dev release for the planned release
        #             versionNumber = getDistVersion().lower() + "0"
        #     else:
        #         # Production release of cdb_api
        #         versionNumber = getDistVersion()

        #     response = input('Continue with releasing version [%s] of cdb_api to pip: [Y/n]: ' % versionNumber)

        #     if response == '' or response.lower() == 'y':
        #         line = versionLineSplit[0] + "'"
        #         line += versionNumber + "'"
        #         line += versionLineSplit[2]
        #     elif response.lower() == 'n':
        #         print('Exiting script')
        #         quit()
        #     else:
        #         raise IOError("Invalid input given %s" % response)

        # newSetupFileContents += line

    # setupFile = open(setupFilePath, 'r')
    # originalSetupFileContents = setupFile.read()

    # setupFile = open(setupFilePath, 'w+')
    # setupFile.write(newSetupFileContents)
    # setupFile.close()


    os.chdir('%s/%s' % (rootDir, PYTHON_SRC_DIST_PATH))    

    p = Path('setup.py')
    p.symlink_to("./%s" % setup_file)

    buildExit = os.system('python setup.py sdist')

    # Clean up
    if os.path.exists('./setup.py'):
        os.remove('./setup.py')

    if buildExit == 0:
        # Clean up
        egg_info_file_path = './%s.egg-info' % projectName.replace("-", "_")
        if os.path.exists(egg_info_file_path):
            shutil.rmtree(egg_info_file_path)

        tarFileName = '%s-%s.tar.gz' % (projectName, versionNumber)

        # Attempt to upload to pip
        distFilePath = 'dist/%s' % tarFileName
        return distFilePath
        
new_api_generator_script = "%s/%s/%s http://localhost:8080/cdb" % (rootDir, PYTHON_SRC_DIST_PATH, 'generatePyClient.sh')
generation_exit = os.system(new_api_generator_script)

if generation_exit == 0:
    api_bin_path = publish_api(PYTHON_API_SETUP_FILE)
    cli_bin_path = publish_api(PYTHON_CLI_SETUP_FILE)

    print("%s %s" % (api_bin_path, cli_bin_path)) 

    pipExit = os.system('twine upload %s %s' % (api_bin_path, cli_bin_path))

    '''Code commented out aides in automatically generating the next dev version... Will handle manually for now.'''
    # if pipExit != 0:
    #     setupFile = open(setupFilePath, 'w')
    #     setupFile.write(originalSetupFileContents)
    #     setupFile.close()