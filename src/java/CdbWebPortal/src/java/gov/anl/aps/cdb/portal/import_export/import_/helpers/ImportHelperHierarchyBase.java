/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author craig
 */
public abstract class ImportHelperHierarchyBase<EntityType extends CdbEntity, EntityControllerType extends CdbEntityController>
        extends ImportHelperTreeViewBase<EntityType, EntityControllerType> {
    
    private Map<Integer, EntityType> parentIndentMap = new HashMap<>();
    private Map<Integer, Integer> childCountMap = new HashMap<>();
    
    protected abstract String getKeyName_();
    protected abstract String getKeyIndent_();
    protected abstract String getKeyParent_();
    protected abstract CreateInfo createEntityInstance_(
            EntityType itemParent,
            Map<String, Object> rowMap,
            String itemName,
            String itemPath,
            int itemSiblingNumber);
    
    @Override
    protected void reset_() {
        super.reset_();
        parentIndentMap = new HashMap<>();
        childCountMap = new HashMap<>();
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        
        boolean isValid = true;
        String validString = "";

        EntityType itemParent = null;

        String itemName = (String) rowMap.get(getKeyName_());
        if ((itemName == null) || (itemName.isEmpty())) {
            // didn't find a non-empty name column for this row
            isValid = false;
            validString = "hierarchical name columns are all empty";
            return new CreateInfo(null, isValid, validString);
        }

        // find parent for this item
        
        if (!rowMap.containsKey(getKeyIndent_())) {
            // return because we need this value to continue
            isValid = false;
            validString = "missing indent level map entry";
            return new CreateInfo(null, isValid, validString);
        }        
        int itemIndentLevel = (int) rowMap.get(getKeyIndent_());
        
        EntityType itemContainer
                = (EntityType) rowMap.get(getKeyParent_());
        
        // calculate sibling number
        Integer numSiblings = childCountMap.get(itemIndentLevel - 1);
        if (numSiblings == null) {
            numSiblings = 0;
        }
        int itemSiblingNumber = numSiblings + 1;
        childCountMap.put(itemIndentLevel - 1, itemSiblingNumber);
        
        String itemPath = "";
        if (itemIndentLevel > 1) {

            // not allowed to specify parent for non level 0 item
            if (itemContainer != null) {
                String msg = "Can only specify existing parent for level 0 item";
                validString = appendToString(validString, msg);
                isValid = false;
            }

            // find parent at previous indent level
            itemParent = parentIndentMap.get(itemIndentLevel - 1);
            
            if (itemParent == null) {
                // should have a parent for this item in map
                String msg = "Unable to determine parent for item";
                validString = appendToString(validString, msg);
                isValid = false;
            }

            // create "parent path" to display item hierarchy in validation table
            for (int i = 1; i < itemIndentLevel; i++) {
                EntityType pathItem = parentIndentMap.get(i);
                itemPath = itemPath + getItemName(pathItem) + "/ ";
            }

        } else {
            // this is either a top-level item, or a parent item is explicitly specified for it
            
            if (itemContainer == null) {
                // new item is a top-level item with no parent
                itemParent = null;
            } else {
                itemParent = itemContainer;
            }
        }
        
        CreateInfo createValidInfo
                = createEntityInstance_(itemParent, rowMap, itemName, itemPath, itemSiblingNumber);
        if (!createValidInfo.getValidInfo().isValid()) {
            isValid = false;
            validString = appendToString(validString, createValidInfo.getValidInfo().getValidString());
        }
        EntityType item = (EntityType) createValidInfo.getEntity();
        if (item == null) {
            isValid = false;
            validString = appendToString(validString, "Unexpected error creating entity instance");
        }

        // set current item as last parent at its indent level
        parentIndentMap.put(itemIndentLevel, item);
        
        // reset child count map for current level
        childCountMap.put(itemIndentLevel, 0);

        // update tree view with item and parent
        updateTreeView(item, itemParent, false);

        return new CreateInfo(item, isValid, validString);
    }
}
