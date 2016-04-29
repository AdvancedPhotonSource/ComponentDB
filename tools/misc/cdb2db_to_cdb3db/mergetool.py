import sys, os
from cdb.common.utility.configurationManager import ConfigurationManager;
cm = ConfigurationManager.getInstance()

# Read the correct database.
databaseName = "cdb"
cdbInstallDir = "/home/djarosz/development"
cdbWebServiceConfigFile = '%s/etc/%s.cdb-web-service.conf' % (cdbInstallDir, databaseName)

print cdbWebServiceConfigFile

if os.path.isfile(cdbWebServiceConfigFile):
    cm.setConfigFile(cdbWebServiceConfigFile)
else:
    print >> sys.stderr, "Configuration file does not exist: %s." %(cdbWebServiceConfigFile)
    exit(1)

from dbLegacy.api.componentDbApi import ComponentDbApi as LegacyComponentDbApi
from dbLegacy.api.logDbApi import LogDbApi as LegacyLogDbApi
from dbLegacy.api.designDbApi import DesignDbApi as LegacyDesignDbApi
from dbLegacy.api.locationDbApi import LocationDbApi as LegacyLocationDbApi
from dbLegacy.api.propertyDbApi import PropertyDbApi as LegacyPropertyDbApi
from dbLegacy.api.userDbApi import UserDbApi as LegacyUserDbApi

legacyDbComponent = LegacyComponentDbApi()
legacyDbDesignDB = LegacyDesignDbApi()
legacyDbLog = LegacyLogDbApi()
legacyDbProperty = LegacyPropertyDbApi()
legacyDbLocation = LegacyLocationDbApi()
legacyDbUsers = LegacyUserDbApi()

allGroups = legacyDbUsers.getUserGroups()
allUsers = legacyDbUsers.getUsers()
allComponents = legacyDbComponent.getComponents()
allDesigns = legacyDbDesignDB.getDesigns()

# TODO Create New Temp Database
# Currently done manually.
print "\n Creating NEW DB"
cmd = 'echo \'cdb\' | /home/djarosz/development/prep3/sbin/cdb_create_db.sh cdb_temporary_db /home/djarosz/development/prep3/db/sql/cdb 1'
os.system(cmd)
print "\n DONE CREATING DB \n\n"


# Load configuration file for temporary database.

# TODO CREATE TEMPORARY CONFIGGURATION
cdbWebServiceConfigFile = '/home/djarosz/development/tmp/tmp.cdb-web-service.conf'
cm.setConfigFile(cdbWebServiceConfigFile)


# Move Old Data onto the new database schema



from cdb.common.db.api.userDbApi import UserDbApi
from cdb.common.db.api.itemDbApi import ItemDbApi
usersDbApi = UserDbApi()
itemDbApi = ItemDbApi()

# Populate Items
componentEntityType = itemDbApi.addEntityType('Component', 'classification for component type objects')
catalogDomainHandler = itemDbApi.addDomainHandler('Catalog', 'Handler responsible for items in a catalog type domain.')
catalogDomainHandlerName = catalogDomainHandler.data['name']
catalogDomain = itemDbApi.addDomain('Catalog', 'Catalog Domain', catalogDomainHandlerName)
exit()

# Populate Group Table

for group in allGroups:
    groupData = group.data
    name = groupData['name']
    description = groupData['description']
    usersDbApi.addGroup(name, description)

# Populate User Table

for user in allUsers:
    userData = user.data
    firstName = userData['firstName']
    lastName = userData['lastName']
    middleName = userData['middleName']
    description = userData['description']
    email = userData['email']
    username = userData['username']
    usersDbApi.addUser(username, firstName, lastName, middleName, email, description, '')
    for group in user['userGroupList']:
        usersDbApi.addUserToGroup(username, group.data['name'])







# Backup new database with merged data ( Create Populate scripts)

# Drop Temp Database

# Prompt user to restore from the new populate scripts (Replacing the database)