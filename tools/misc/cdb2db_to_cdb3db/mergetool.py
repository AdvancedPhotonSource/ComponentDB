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

if len(sys.argv) < 2:
    print 'This script has not been provided a database name.'
    print 'please rerun script with the deployment name (CDB_DB_NAME)'
    print 'Usage:'
    print  "$0 [CDB_DB_NAME]"
    exit(1)

databaseName = sys.argv[1]


mergeUtility = MergeUtility(databaseName)
mergeUtility.backupCurrentDb()

mergeUtility.populateGroupTable()
mergeUtility.populateUserTable()
mergeUtility.populateSources()
#mergeUtility.populateCategories()
#mergeUtility.populateTypes()
mergeUtility.populateLogTopics()
mergeUtility.populatePropertyTypeHandler()
mergeUtility.populatePropertyTypeCategories()
mergeUtility.populatePropertyType()
mergeUtility.populateItemsUsingLocations()
mergeUtility.populateItemsUsingComponents(populateTypesAndCategoriesAsNeeded=True)
mergeUtility.populateItemsUsingDesigns()

mergeUtility.backupTempDb()
mergeUtility.destroyTempDb()
mergeUtility.printSummaryInfo()