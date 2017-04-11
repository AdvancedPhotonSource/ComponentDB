/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * DB utility class for items.
 */
public class ItemUtility {

    public static List<Item> filterItem(String query, List<Item> candidateItemList) {
        Pattern searchPattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);

        List<Item> filteredItemList = new ArrayList<>();
        for (Item item : candidateItemList) {
            boolean nameContainsQuery = searchPattern.matcher(item.toString()).find();
            if (nameContainsQuery) {
                filteredItemList.add(item);
            }
        }
        return filteredItemList;
    }

    public static TreeNode createNewTreeNode(Item item, TreeNode parentTreeNode) {
        String treeNodeType = item.getDomain().getName();
        return new DefaultTreeNode(treeNodeType, item, parentTreeNode);
    }

    /**
     * Use item elements list to generate a list of items.
     *
     * @param parentItem
     * @return
     */
    public static List<Item> getItemListFromElementsList(Item parentItem) {
        List<Item> itemList = new ArrayList<>();
        List<ItemElement> itemElementList = parentItem.getItemElementDisplayList();
        if (itemElementList != null) {
            for (ItemElement itemElement : itemElementList) {
                Item item = itemElement.getContainedItem();
                if (item != null) {
                    itemList.add(item);
                }
            }
        }

        return itemList;
    }

    /**
     *
     *
     * @param item
     * @param relationshipTypeName
     * @param itemFirstInRelationship
     * @return
     */
    public static List<ItemElementRelationship> getItemRelationshipList(Item item, String relationshipTypeName, boolean itemFirstInRelationship) {
        ItemElement selfElement = item.getSelfElement();
        
        List<ItemElementRelationship> ierList = new ArrayList<>(); 
        List<ItemElementRelationship> ierListToLookAt = null; 

        if (itemFirstInRelationship) {
            ierListToLookAt = selfElement.getItemElementRelationshipList();
        } else {
            ierListToLookAt = selfElement.getItemElementRelationshipList1();
        }
        
        if (ierListToLookAt != null) {
            for (ItemElementRelationship ier : ierListToLookAt) {
                RelationshipType relationshipType = ier.getRelationshipType();
                if (relationshipType != null) {
                    if (relationshipType.getName().equals(relationshipTypeName)) {
                        ierList.add(ier); 
                    }
                }
            }
                
        }
        
        return ierList; 
    }

    /**
     * Generate a list of items related to the item provided.
     *
     *
     * @param item item provided
     * @param relationshipTypeName relationship name to look for
     * @param itemFirstInRelationship item provided is first item element or
     * second item element.
     * @return list of items found.
     */
    public static List<Item> getItemsRelatedToItem(Item item, String relationshipTypeName, boolean itemFirstInRelationship) {
        List<Item> itemList = new ArrayList<>();
        List<ItemElementRelationship> itemElementRelationshipList;
        itemElementRelationshipList = getItemRelationshipList(item, relationshipTypeName, itemFirstInRelationship);

        if (itemElementRelationshipList != null) {
            for (ItemElementRelationship ier : itemElementRelationshipList) {
                ItemElement itemElement;
                if (itemFirstInRelationship) {
                    itemElement = ier.getSecondItemElement();
                } else {
                    itemElement = ier.getFirstItemElement();
                }
                itemList.add(itemElement.getParentItem());
            }
        }

        return itemList;
    }

}
