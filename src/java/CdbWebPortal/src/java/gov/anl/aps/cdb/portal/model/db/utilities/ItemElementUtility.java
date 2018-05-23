/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * DB utility class for item elements.
 */
public class ItemElementUtility {
    
    private final static String ITEM_ELEMENT_NODE_TYPE = "itemElement"; 
    
    public static TreeNode createItemElementRoot (Item parentItem) throws CdbException {
        return createTreeRoot(parentItem, false);
    }
    
    public static TreeNode createItemRoot (Item parentItem) throws CdbException {
        return createTreeRoot(parentItem, true);
    }

    private static TreeNode createTreeRoot(Item parentItem, boolean useChildItem) throws CdbException {
        Object newTreeNodeDataObject; 
        String nodeType; 
        if (useChildItem) {
            newTreeNodeDataObject = parentItem; 
            nodeType = parentItem.getDomain().getName().replace(" ", "");
        } else {
            newTreeNodeDataObject = parentItem.getSelfElement(); 
            nodeType = ITEM_ELEMENT_NODE_TYPE;
        }
        
        TreeNode treeRoot = new DefaultTreeNode(nodeType, newTreeNodeDataObject, null);
        if (parentItem == null) {
            throw new CdbException("Cannot create item element tree view: parent item is not set.");
        }
        
        // Use "tree branch" list to prevent circular trees
        // Whenever new item is encountered, it will be added to the tree branch list before populating
        // element node, and removed from the branch list after population is done
        // If an object is encountered twice in the tree branch, this itemates an error.
        List<Item> itemTreeBranch = new ArrayList<>();
        populateItemNode(treeRoot, parentItem, itemTreeBranch, useChildItem);
        return treeRoot;
    }

    private static void populateItemNode(TreeNode itemElementNode, Item item, List<Item> itemTreeBranch, boolean useChildItem) throws InvalidObjectState {
        List<ItemElement> itemElementList = item.getItemElementDisplayList();
        if (itemElementList == null) {
            return;
        }
        itemTreeBranch.add(item);
        for (ItemElement itemElement : itemElementList) {
            Item childItem = itemElement.getContainedItem();
           
            TreeNode childItemElementNode = null;
            
            if (useChildItem) {
                if (childItem != null) {
                    String nodeType = childItem.getDomain().getName().replace(" ", "");; 
                    childItemElementNode = new DefaultTreeNode(nodeType, childItem, itemElementNode);
                } else {
                    // no child item is linked to item element. 
                    continue;
                }
            } 
            
            if (childItemElementNode == null) {
                childItemElementNode = new DefaultTreeNode(ITEM_ELEMENT_NODE_TYPE, itemElement, itemElementNode);
            }
            if (childItem != null) {
                if (itemTreeBranch.contains(childItem)) {
                    throw new InvalidObjectState("Cannot create item element tree view: circular child/parent relationship found with items "
                            + childItem.getName() + " and " + childItem.getName() + ".");
                }
                populateItemNode(childItemElementNode, childItem, itemTreeBranch, useChildItem);
            }

        }
        itemTreeBranch.remove(item);
    }

}
