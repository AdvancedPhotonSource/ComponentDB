/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.test.CdbDBTest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author darek
 */
public class ItemDomainCatalogControllerTest extends CdbDBTest {
    
    @Test
    public void checkItemFacadeWasSet() {
        ItemDomainCatalogControllerTestable itcc = new ItemDomainCatalogControllerTestable(this); 
        assertNotNull(itcc.getItem(23));
    }
    
    /**
     * Testable class that sets all of the necessary database connections in Preparation for testing. 
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
    }
    
}
