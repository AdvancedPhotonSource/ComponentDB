/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemUtility;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author djarosz
 */
public class FilterViewItemHierarchySelection {

    private List<Item> itemSelectionList = null;
    private Item itemSelection = null;
    private FilterViewItemHierarchySelection childFilterViewItemHierarchySelection = null;
    private List<FilterViewItemHierarchySelection> filterViewListRepresentation = null;
    private FilterViewItemHierarchySelection parentFilterViewItemHierarchySelection = null; 

    public FilterViewItemHierarchySelection(List<Item> itemSelectionList) {
        this.itemSelectionList = itemSelectionList;
    }
    
    private FilterViewItemHierarchySelection(List<Item> itemSelectionList, FilterViewItemHierarchySelection parentFilterViewItemHierarchySelection) {
        this.itemSelectionList = itemSelectionList;
        this.parentFilterViewItemHierarchySelection = parentFilterViewItemHierarchySelection;
    }
    
    public boolean hasParent() {
        return parentFilterViewItemHierarchySelection != null; 
    }

    public List<Item> getItemSelectionList() {
        return itemSelectionList;
    }

    public void setItemSelectionList(List<Item> itemSelectionList) {
        this.itemSelectionList = itemSelectionList;
    }

    public Item getItemSelection() {
        return itemSelection;
    }
    
    private void resetParentFilterViewListRepresentation() {
        this.filterViewListRepresentation = null;        
        if (parentFilterViewItemHierarchySelection != null) {
            parentFilterViewItemHierarchySelection.resetParentFilterViewListRepresentation(); 
        }
    }

    public void setItemSelection(Item itemSelection) {
        if (this.itemSelection == null || !this.itemSelection.equals(itemSelection)) {
            this.resetParentFilterViewListRepresentation();
            if (itemSelection != null) {
                List<Item> childItemList = ItemUtility.getItemListFromElementsList(itemSelection);
                childFilterViewItemHierarchySelection = new FilterViewItemHierarchySelection(childItemList, this);
            } else {
                childFilterViewItemHierarchySelection = null; 
            }
        }
        this.itemSelection = itemSelection;
    }
   
    public Item getLowesetSelectionMade() {
        if (childFilterViewItemHierarchySelection == null) {
            if (this.itemSelection != null) {
                return this.itemSelection;  
            } else if (this.parentFilterViewItemHierarchySelection != null) {
                return parentFilterViewItemHierarchySelection.itemSelection; 
            } else {
                return null; 
            }
        } else {
            return childFilterViewItemHierarchySelection.getLowesetSelectionMade(); 
        }
    }

    public List<FilterViewItemHierarchySelection> getFilterViewSelectionHierarchyList() {
        if (filterViewListRepresentation == null) {
            filterViewListRepresentation = new ArrayList<>();
            filterViewListRepresentation.add(this);
            FilterViewItemHierarchySelection childFilterView = childFilterViewItemHierarchySelection;
            while (childFilterView != null) {
                if (childFilterView.itemSelectionList.isEmpty()) {
                    break;
                } else {
                    filterViewListRepresentation.add(childFilterView);
                    childFilterView = childFilterView.childFilterViewItemHierarchySelection; 
                }
            }
        }
        return filterViewListRepresentation;
    }

}
