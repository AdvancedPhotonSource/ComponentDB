/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.LinkedList;

/**
 *
 * @author darek
 */
public class SearchEntitiesResults {
    
    LinkedList<SearchResult> itemDomainCatalogResults;
    LinkedList<SearchResult> itemDomainInventoryResults;
    LinkedList<SearchResult> itemDomainMachineDesignResults;
    LinkedList<SearchResult> itemDomainCableCatalogResults;
    LinkedList<SearchResult> itemDomainCableInventoryResults;
    LinkedList<SearchResult> itemDomainCableDesignResults;
    LinkedList<SearchResult> itemDomainLocationResults; 
    LinkedList<SearchResult> itemDomainMAARCResults; 
    LinkedList<SearchResult> itemElementResults; 
    LinkedList<SearchResult> itemTypeResults;
    LinkedList<SearchResult> itemCategoryResults;
    LinkedList<SearchResult> propertyTypeResults; 
    LinkedList<SearchResult> propertyTypeCategoryResults; 
    LinkedList<SearchResult> sourceResults;
    LinkedList<SearchResult> userResults; 
    LinkedList<SearchResult> userGroupResults;

    public SearchEntitiesResults() {
    }

    public LinkedList<SearchResult> getItemDomainCatalogResults() {
        return itemDomainCatalogResults;
    }

    public void setItemDomainCatalogResults(LinkedList<SearchResult> itemDomainCatalogResults) {
        this.itemDomainCatalogResults = itemDomainCatalogResults;
    }

    public LinkedList<SearchResult> getItemDomainInventoryResults() {
        return itemDomainInventoryResults;
    }

    public void setItemDomainInventoryResults(LinkedList<SearchResult> itemDomainInventoryResults) {
        this.itemDomainInventoryResults = itemDomainInventoryResults;
    }

    public LinkedList<SearchResult> getItemDomainMachineDesignResults() {
        return itemDomainMachineDesignResults;
    }

    public void setItemDomainMachineDesignResults(LinkedList<SearchResult> itemDomainMachineDesignResults) {
        this.itemDomainMachineDesignResults = itemDomainMachineDesignResults;
    }

    public LinkedList<SearchResult> getItemDomainCableCatalogResults() {
        return itemDomainCableCatalogResults;
    }

    public void setItemDomainCableCatalogResults(LinkedList<SearchResult> itemDomainCableCatalogResults) {
        this.itemDomainCableCatalogResults = itemDomainCableCatalogResults;
    }

    public LinkedList<SearchResult> getItemDomainCableInventoryResults() {
        return itemDomainCableInventoryResults;
    }

    public void setItemDomainCableInventoryResults(LinkedList<SearchResult> itemDomainCableInventoryResults) {
        this.itemDomainCableInventoryResults = itemDomainCableInventoryResults;
    }

    public LinkedList<SearchResult> getItemDomainCableDesignResults() {
        return itemDomainCableDesignResults;
    }

    public void setItemDomainCableDesignResults(LinkedList<SearchResult> itemDomainCableDesignResults) {
        this.itemDomainCableDesignResults = itemDomainCableDesignResults;
    }

    public LinkedList<SearchResult> getItemDomainLocationResults() {
        return itemDomainLocationResults;
    }

    public void setItemDomainLocationResults(LinkedList<SearchResult> itemDomainLocationResults) {
        this.itemDomainLocationResults = itemDomainLocationResults;
    }

    public LinkedList<SearchResult> getItemDomainMAARCResults() {
        return itemDomainMAARCResults;
    }

    public void setItemDomainMAARCResults(LinkedList<SearchResult> itemDomainMAARCResults) {
        this.itemDomainMAARCResults = itemDomainMAARCResults;
    }

    public LinkedList<SearchResult> getItemElementResults() {
        return itemElementResults;
    }

    public void setItemElementResults(LinkedList<SearchResult> itemElementResults) {
        this.itemElementResults = itemElementResults;
    }

    public LinkedList<SearchResult> getItemTypeResults() {
        return itemTypeResults;
    }

    public void setItemTypeResults(LinkedList<SearchResult> itemTypeResults) {
        this.itemTypeResults = itemTypeResults;
    }

    public LinkedList<SearchResult> getItemCategoryResults() {
        return itemCategoryResults;
    }

    public void setItemCategoryResults(LinkedList<SearchResult> itemCategoryResults) {
        this.itemCategoryResults = itemCategoryResults;
    }

    public LinkedList<SearchResult> getPropertyTypeResults() {
        return propertyTypeResults;
    }

    public void setPropertyTypeResults(LinkedList<SearchResult> propertyTypeResults) {
        this.propertyTypeResults = propertyTypeResults;
    }

    public LinkedList<SearchResult> getPropertyTypeCategoryResults() {
        return propertyTypeCategoryResults;
    }

    public void setPropertyTypeCategoryResults(LinkedList<SearchResult> propertyTypeCategoryResults) {
        this.propertyTypeCategoryResults = propertyTypeCategoryResults;
    }

    public LinkedList<SearchResult> getSourceResults() {
        return sourceResults;
    }

    public void setSourceResults(LinkedList<SearchResult> sourceResults) {
        this.sourceResults = sourceResults;
    }

    public LinkedList<SearchResult> getUserResults() {
        return userResults;
    }

    public void setUserResults(LinkedList<SearchResult> userResults) {
        this.userResults = userResults;
    }

    public LinkedList<SearchResult> getUserGroupResults() {
        return userGroupResults;
    }

    public void setUserGroupResults(LinkedList<SearchResult> userGroupResults) {
        this.userGroupResults = userGroupResults;
    }

    
    
}
