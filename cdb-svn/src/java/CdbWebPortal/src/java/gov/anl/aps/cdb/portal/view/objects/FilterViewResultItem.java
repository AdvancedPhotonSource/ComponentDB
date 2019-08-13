/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
public class FilterViewResultItem {

    private Item itemObject = null;

    private List<FilterViewResultItemExpansion> filterViewExpansionList = null;

    public boolean isDisplayExpansion() {
        if (filterViewExpansionList != null) {
            return !filterViewExpansionList.isEmpty();
        }
        return false;
    }

    public FilterViewResultItem(Item item) {
        this.itemObject = item;
    }

    public Item getItemObject() {
        return itemObject;
    }

    public void setItemObject(Item itemObject) {
        this.itemObject = itemObject;
    }

    public List<FilterViewResultItemExpansion> getFilterViewExpansionList() {
        return filterViewExpansionList;
    }
    
    public void addFilterViewItemExpansion(TreeNode root, String headerTitle) {
        if (filterViewExpansionList == null) {
            filterViewExpansionList = new ArrayList<>(); 
        }
        FilterViewResultItemExpansion expanstion = new FilterViewResultItemExpansion(headerTitle, root); 
        filterViewExpansionList.add(expanstion); 
    }

    public class FilterViewResultItemExpansion {

        private String headerTitle = null;
        private TreeNode rootTreeNode = null;

        public FilterViewResultItemExpansion(String headerTitle, TreeNode rootTreeNode) {
            this.headerTitle = headerTitle;
            this.rootTreeNode = rootTreeNode; 

        }

        public String getHeaderTitle() {
            return headerTitle;
        }

        public TreeNode getRootTreeNode() {
            return rootTreeNode;
        }

    }

}
