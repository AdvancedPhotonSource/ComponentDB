/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
public class ItemHierarchy {

    private Item parentItem;
    private Item derivedItem;
    private List<ItemHierarchy> childItems;
    private Integer elementId;
    private String elementName; 

    private ItemHierarchy() {
        
    }
    
    public ItemHierarchy(Item parentItem, boolean autocreateHierarchy) {
        this.parentItem = parentItem;
        childItems = new ArrayList<>();

        if (autocreateHierarchy) {
            for (ItemElement element : parentItem.getItemElementDisplayList()) {
                Item containedItem = element.getContainedItem();
               
                ItemElement derivedFromItemElement = element.getDerivedFromItemElement();
                Item derivedItem = derivedFromItemElement.getContainedItem();                
                
                ItemHierarchy child = null; 
                if (containedItem != null) {
                    child = new ItemHierarchy(containedItem, true);
                }
                
                if (derivedItem != null) {
                    if (child == null) {
                        child = new ItemHierarchy();
                    }
                    
                    child.derivedItem = derivedItem; 
                }
                
                
                if (child != null) {
                    child.elementId = element.getId(); 
                    child.elementName = element.getName();
                    addChildItem(child);                    
                }
            }
        }
    }

    public static ItemHierarchy createSingleNodeHierarchyFromTreeNode(TreeNode node) {
        TreeNode parentNode = node.getChildren().get(0);
        ItemHierarchy singleNodeHierarchy = null;
        ItemHierarchy parentHierarchy = null;

        while (parentNode != null) {
            Item parent = (Item) parentNode.getData();

            if (singleNodeHierarchy == null) {
                singleNodeHierarchy = new ItemHierarchy(parent, false);
                parentHierarchy = singleNodeHierarchy;
            } else {
                ItemHierarchy child = new ItemHierarchy(parent, false);
                parentHierarchy.addChildItem(child);
                parentHierarchy = child;
            }
            if (parentNode.getChildren().size() > 0) {
                parentNode = parentNode.getChildren().get(0);
            } else {
                parentNode = null;
            }
        }
        return singleNodeHierarchy;
    }

    public Item getParentItem() {
        return parentItem;
    }

    public void setParentItem(Item parentItem) {
        this.parentItem = parentItem;
    }

    public List<ItemHierarchy> getChildItems() {
        return childItems;
    }

    public void setChildItems(List<ItemHierarchy> childItems) {
        this.childItems = childItems;
    }

    public Integer getElementId() {
        return elementId;
    }

    public void setElementId(Integer elementId) {
        this.elementId = elementId;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public Item getDerivedItem() {
        return derivedItem;
    }

    public void setDerivedItem(Item derivedItem) {
        this.derivedItem = derivedItem;
    }

    public void addChildItem(ItemHierarchy item) {
        childItems.add(item);
    }

}
