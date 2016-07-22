#!/usr/bin/env python

#
# CDB attachment status and cleanup script
#
# Usage:
#
# $0 [CDB_DB_NAME]
#

####################################################################

from mergeUtils.mergeUtility import MergeUtility
import sys

if len(sys.argv) < 4:
    print 'This script has not been provided a database name.'
    print 'please rerun script with the deployment name (CDB_DB_NAME)'
    print 'Usage:'
    print  "$0 [CDB_DB_NAME] [DEFAULT_PROJECT] [PROJECTS_TO_ADD]"
    exit(1)

databaseName = sys.argv[1]
populateTypesAndCategoriesAsNeeded=True

defaultProject= sys.argv[2]
projectsToAdd = sys.argv[3:]

if defaultProject not in projectsToAdd:
    projectsToAdd.append(defaultProject)

mergeUtility = MergeUtility(databaseName)
mergeUtility.backupCurrentDb()

mergeUtility.populateGroupTable()
mergeUtility.populateUserTable()
mergeUtility.populateSources()

if populateTypesAndCategoriesAsNeeded is False:
    mergeUtility.populateCategories()
    mergeUtility.populateTypes()
mergeUtility.populateProjects(projectsToAdd)

mergeUtility.populateLogTopics()
mergeUtility.populatePropertyTypeHandler()
mergeUtility.populatePropertyTypeCategories()
mergeUtility.populatePropertyType()
mergeUtility.populateItemsUsingLocations()
mergeUtility.populateCatalogInventoryItems(populateTypesAndCategoriesAsNeeded, defaultProject)

mergeUtility.backupTempDb()
mergeUtility.destroyTempDb()
mergeUtility.printSummaryInfo()