/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author craig
 */
public abstract class ImportHelperTreeViewBase<EntityType extends CdbEntity, EntityControllerType extends CdbEntityController>
        extends ImportHelperBase<EntityType, EntityControllerType> {
    
    private Map<EntityType, TreeNode> treeNodeMap = new HashMap<>();    
    private int treeNodeChildCount = 0;
    
    protected abstract EntityType getItemParent(EntityType item);
    protected abstract String getItemName(EntityType item);
    protected abstract List<EntityType> getItemChildren(EntityType item);

    protected int getTreeNodeChildCount() {
        return treeNodeChildCount;
    }
    
    @Override
    protected void reset_() {
        treeNodeMap = new HashMap<>();
        treeNodeChildCount = 0;
    }
    
    /**
     * Specifies whether the subclass will provide a tree view.  Default is false,
     * subclass should override to customize.
     */
    @Override
    public boolean hasTreeView() {
        return true;
    }
    
    protected void updateTreeView(
            EntityType item, 
            EntityType parent,
            boolean addChildren) {
        
        TreeNode itemNode = treeNodeMap.get(item);
        if (itemNode  == null) {
            itemNode = new DefaultTreeNode(item);
            itemNode.setExpanded(true);
            treeNodeMap.put(item, itemNode);
            if (addChildren) {
                addChildrenForItemToTreeNode(item, itemNode);
            }
        }
        
        if (parent != null) {
            // create parent nodes recursively if they don't exist
            TreeNode parentNode = treeNodeMap.get(parent);
            if (parentNode == null) {
                EntityType grandParent = getItemParent(parent);
                updateTreeView(parent, grandParent, false);
                parentNode = treeNodeMap.get(parent);
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