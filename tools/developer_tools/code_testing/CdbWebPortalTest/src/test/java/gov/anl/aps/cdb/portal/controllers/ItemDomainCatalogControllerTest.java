/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.test.CdbDBTest;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class ItemDomainCatalogControllerTest extends CdbDBTest {

    ItemDomainCatalogControllerTestable itemDomainCatalogController;
    
    @PersistenceContext
    EntityManager em;

    @Before
    public void initalize() {    
        itemDomainCatalogController = new ItemDomainCatalogControllerTestable(this);
    }

    @Test
    public void checkItemFacadeWasSet() {
        assertNotNull(itemDomainCatalogController.getItem(1));
    }
    
    @Test
    public void checkCatalogItemList() {
        System.out.println("getItemList");
        String default_domain = itemDomainCatalogController.getDefaultDomainName(); 
        List<Item> itemList = itemDomainCatalogController.getItemList(); 
        for (Item item : itemList) {
            assertEquals(default_domain, item.getDomain().getName());
        }
    }
    
    @Test
    public void checkItemTypeBehavior() {
        // Set current to a new item 
        itemDomainCatalogController.prepareCreate();
        //Item should have been created 
        Item item = itemDomainCatalogController.getCurrent(); 
        assertNotNull(item);
        // Should be disabled until category is selected
        assertTrue(itemDomainCatalogController.isDisabledItemItemType());               
        // Generated item type list should be empty
        List<ItemType> itemTypes = itemDomainCatalogController.getAvailableItemTypesForCurrentItem();
        assertTrue(itemTypes == null || itemTypes.isEmpty());
        // Select category
        List<ItemCategory> itemCategoryList = itemDomainCatalogController.getDomainItemCategoryList();  
        assertNotNull(itemCategoryList);
        
        List<ItemCategory> newItemCategoryList = new ArrayList<>();
        newItemCategoryList.add(itemCategoryList.get(0));        
        item.setItemCategoryList(newItemCategoryList);        
        itemTypes = itemDomainCatalogController.getAvailableItemTypesForCurrentItem();
        assertTrue(itemTypes != null && !itemTypes.isEmpty());
    }
    
    @Test
    public void isListDifferent() {
        List<Object> listA = (List<Object>)(Object) itemDomainCatalogController.getItemList(); 
        List<Object> listB = (List<Object>)(Object) itemDomainCatalogController.getItemList();                 
        
        assertFalse(itemDomainCatalogController.isListDifferentTestable(listA, listB));
        
        listB.remove(0);
        assertTrue(itemDomainCatalogController.isListDifferentTestable(listA, listB));
    }
    
    @Test
    public void checkGetItemsWithoutParentsMethod() {
        List<Item> itemList = itemDomainCatalogController.getItemsWithoutParents(); 
        
        assertNotNull(itemList);
        for (Item item : itemList) {
            List<ItemElement> itemElements = item.getItemElementMemberList(); 
            assertTrue(itemElements == null || itemElements.isEmpty());
        }
    }
    
    @Test
    public void testCheckItemUniqueness() {
        // Get unique item
        Item uniqueItem = itemDomainCatalogController.getItemList().get(0); 
        assertNotNull(uniqueItem);
        
        Item newItem = itemDomainCatalogController.createEntityInstance(); 
        assertNotNull(newItem);
        
        newItem.setName(uniqueItem.getName());
        newItem.setItemIdentifier1(uniqueItem.getItemIdentifier1());
        newItem.setItemIdentifier2(uniqueItem.getItemIdentifier2());
        newItem.setDerivedFromItem(uniqueItem.getDerivedFromItem()); 
        
        assertFalse(checkItemUniqueness(newItem));                
        
        newItem.setName("Some New Name that wes used for the test");
        
        assertTrue(checkItemUniqueness(newItem));                                
    }
    
    private boolean checkItemUniqueness(Item item) {
        try {
            itemDomainCatalogController.checkItemUniqueness(item);
        } catch (CdbException ex) {
            return false;
        }
        return true; 
    }

    /**
     * Testable controller class that sets all of the necessary database
     * connections in Preparation for testing.
     */
    public class ItemDomainCatalogControllerTestable extends ItemDomainCatalogController {

        public ItemDomainCatalogControllerTestable(CdbDBTest cdbTest) {
            this.itemFacade = cdbTest.getItemFacade();
            this.itemElementFacade = cdbTest.getItemElementFacade();
            this.itemTypeFacade = cdbTest.getItemTypeFacade();
            this.domainFacade = cdbTest.getDomainFacade();
            this.entityTypeFacade = cdbTest.getEntityTypeFacade();
            this.itemCategoryFacade = cdbTest.getItemCategoryFacade();
            this.listFacade = cdbTest.getListFacade();
            this.userInfoFacade = cdbTest.getUserInfoFacade();
        }                
       
        public boolean isListDifferentTestable(List<Object> originalList, List<Object> listToCompare) {
            return super.isListDifferent(originalList, listToCompare);
        }                
    }

}
