/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
public class ItemDomainMdSearchResult extends SearchResult {

    private ItemDomainMachineDesign machineDesign;
    private List<ItemDomainMdSearchResult> childItems;
    private boolean searchMatch; 

    public ItemDomainMdSearchResult(TreeNode parentNode) {
        super((SearchResult) parentNode.getData());
        SearchResult result = (SearchResult) parentNode.getData();
        searchMatch = ObjectUtility.equals(result.getRowStyle(),SearchResult.SEARCH_RESULT_ROW_STYLE); 
        
        machineDesign = (ItemDomainMachineDesign) result.getCdbEntity(); 
        
        childItems = new ArrayList<>();
        
        populateChildItems(parentNode);
    }
    
    private void populateChildItems(TreeNode node) {        
        List<TreeNode> children = node.getChildren();
        
        for (TreeNode child : children) {
            ItemDomainMdSearchResult IHChild = new ItemDomainMdSearchResult(child);
            childItems.add(IHChild); 
        }
    }

    public ItemDomainMachineDesign getMachineDesign() {
        return machineDesign;
    }

    public List<ItemDomainMdSearchResult> getChildItems() {
        return childItems;
    }

    public void setChildItems(List<ItemDomainMdSearchResult> childItems) {
        this.childItems = childItems;
    }

    public void addChildItem(ItemDomainMdSearchResult item) {
        childItems.add(item); 
    }

    public boolean isSearchMatch() {
        return searchMatch;
    }

}
