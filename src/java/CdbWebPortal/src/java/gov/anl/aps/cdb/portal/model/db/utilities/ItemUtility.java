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

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
     * Generate a list of items related to the item provided. 
     * 
     * 
     * @param item item provided
     * @param relationshipTypeName relationship name to look for
     * @param itemFirstInRelationship item provided is first item element or second item element. 
     * @return list of items found. 
     */
    public static List<Item> getItemsRelatedToItem(Item item, String relationshipTypeName, boolean itemFirstInRelationship) {
        ItemElement selfElement = item.getSelfElement(); 
        List<Item> itemList = new ArrayList<>(); 
        
        List<ItemElementRelationship> itemElementRelationshipList; 
        if (itemFirstInRelationship) {
            itemElementRelationshipList = selfElement.getItemElementRelationshipList(); 
        } else {
            itemElementRelationshipList = selfElement.getItemElementRelationshipList1(); 
        }
        
        if (itemElementRelationshipList != null) {
            for (ItemElementRelationship ier : itemElementRelationshipList) {
                if (ier.getRelationshipType().getName().equals(relationshipTypeName)) {
                    ItemElement itemElement; 
                    if (itemFirstInRelationship) {
                        itemElement = ier.getSecondItemElement();
                    } else {
                        itemElement = ier.getFirstItemElement(); 
                    }
                    itemList.add(itemElement.getParentItem()); 
                }
            }
        }
        
        return itemList; 
    }   
    
}
