/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author craig
 */
public abstract class HierarchicalImportHelperBase<EntityType extends CdbEntity, EntityControllerType extends CdbEntityController>
        extends ImportHelperBase<EntityType, EntityControllerType> {
    
    private Map<String, TreeNode> treeNodeMap = new HashMap<>();
    
    private int treeNodeChildCount = 0;
    
    protected int getTreeNodeChildCount() {
        return treeNodeChildCount;
    }
    
    @Override
    protected void reset_() {
        treeNodeMap = new HashMap<>();
        treeNodeChildCount = 0;
    }
    
    protected abstract EntityType getItemParent(EntityType item);
    protected abstract String getItemName(EntityType item);
    protected abstract List<EntityType> getItemChildren(EntityType item);

    protected void updateTreeView(
            EntityType item, 
            EntityType parent,
            boolean addChildren) {
        
        TreeNode itemNode = new DefaultTreeNode(item);
        itemNode.setExpanded(true);
        treeNodeMap.put(getItemName(item), itemNode);
        
        if (addChildren) {
            addChildrenForItemToTreeNode(item, itemNode);
        }
        
        if (parent != null) {
            
            // create parent nodes recursively if they don't exist
            TreeNode parentNode = treeNodeMap.get(getItemName(parent));
            if (parentNode == null) {
                EntityType grandParent = getItemParent(item);
                updateTreeView(parent, grandParent, false);
                parentNode = treeNodeMap.get(getItemName(parent));
            }
            
            parentNode.getChildren().add(itemNode);
            
        } else {
            // top level machine design item, so add to root tree node
            rootTreeNode.getChildren().add(itemNode);
        }
    }
    
    private void addChildrenForItemToTreeNode(
            EntityType item, 
            TreeNode itemNode) {
        
        treeNodeChildCount = treeNodeChildCount + 1;
        
        itemNode.setExpanded(false);
        
        List<EntityType> children = getItemChildren(item);
        for (EntityType childItem : children) {
            TreeNode childNode = new DefaultTreeNode(childItem);
            childNode.setExpanded(false);
            itemNode.getChildren().add(childNode);
            addChildrenForItemToTreeNode(childItem, childNode);
        }
    }
    
}
