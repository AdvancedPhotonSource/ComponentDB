/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * DB utility class for item elements.
 */
public class ItemElementUtility {

    public static TreeNode createItemElementRoot(Item parentItem) throws CdbException {
        TreeNode itemElementRoot = new DefaultTreeNode(new ItemElement(), null);
        if (parentItem == null) {
            throw new CdbException("Cannot create item element tree view: parent item is not set.");
        }
        
        // Use "tree branch" list to prevent circular trees
        // Whenever new item is encountered, it will be added to the tree branch list before populating
        // element node, and removed from the branch list after population is done
        // If an object is encountered twice in the tree branch, this itemates an error.
        List<Item> itemTreeBranch = new ArrayList<>();
        populateItemNode(itemElementRoot, parentItem, itemTreeBranch);
        return itemElementRoot;
    }

    private static void populateItemNode(TreeNode itemElementNode, Item item, List<Item> itemTreeBranch) throws InvalidObjectState {
        List<ItemElement> itemElementList = item.getItemElementDisplayList();
        if (itemElementList == null) {
            return;
        }
        itemTreeBranch.add(item);
        for (ItemElement itemElement : itemElementList) {
            Item childItem = itemElement.getContainedItem();
           

            TreeNode childItemElementNode = new DefaultTreeNode(itemElement, itemElementNode);
            if (childItem != null) {
                if (itemTreeBranch.contains(childItem)) {
                    throw new InvalidObjectState("Cannot create item element tree view: circular child/parent relationship found with items "
                            + childItem.getName() + " and " + childItem.getName() + ".");
                }
                populateItemNode(childItemElementNode, childItem, itemTreeBranch);
            }

        }
        itemTreeBranch.remove(item);
    }

}
