#!/usr/bin/env python

#
# CDB attachment status and cleanup script
#
# Usage:
#
# $0 [CDB_DB_NAME]
#

####################################################################
from mergeUtils import typesOfMerge
from mergeUtils.mergeUtility import MergeUtility
import sys

if len(sys.argv) < 4 or not typesOfMerge.isTypeOfMergeValid(sys.argv[2]):
    print 'This script has not been provided a database name.'
    print 'please rerun script with the deployment name (CDB_DB_NAME)'
    print 'Usage:'
    print  "$0 [CDB_DB_NAME] [TYPE_OF_MERGE] [DEFAULT_PROJECT] [[PROJECTS_TO_ADD]]"
    print ""
    print "Type of merge = Full, Start, Finish"
    print "Full: Use the current db from start to finish"
    print "Start: Use the db to create & populate a temp db but do not remove it"
    print "Finish: Some data already exists, use the db to finish populating the temp db."
    exit(1)

databaseName = sys.argv[1]
populateTypesAndCategoriesAsNeeded=True

typeOfMerge=sys.argv[2]

defaultProject= sys.argv[3]
projectsToAdd = sys.argv[4:]



if defaultProject not in projectsToAdd:
    projectsToAdd.append(defaultProject)

mergeUtility = MergeUtility(databaseName, typeOfMerge)
mergeUtility.backupCurrentDb()

mergeUtility.populateGroupTable()
mergeUtility.populateUserTable()
mergeUtility.populateSources()

if populateTypesAndCategoriesAsNeeded is False:
    # Not compatible with finish type of merge.
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
if typeOfMerge== typesOfMerge.finish or typeOfMerge == typesOfMerge.full:
    mergeUtility.destroyTempDb()
mergeUtility.printSummaryInfo()