/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.ItemTypeCategoryEntity;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/**
 *
 * @author darek
 */
public abstract class ItemTypeCategoryControllerUtility<TypeCategoryEntity extends ItemTypeCategoryEntity, TypeCategoryFacade extends CdbEntityFacade<TypeCategoryEntity>> extends CdbEntityControllerUtility<TypeCategoryEntity, TypeCategoryFacade> {

    protected abstract TypeCategoryEntity createItemTypeCategoryEntity();      
    
    @Override
    public TypeCategoryEntity createEntityInstance(UserInfo sessionUser) {
        return createEntityInstance(sessionUser, null); 
    }
    
    public TypeCategoryEntity createEntityInstance(UserInfo sessionUser, Domain domain) {
        TypeCategoryEntity itemTypeCategory;
        itemTypeCategory = createItemTypeCategoryEntity();
        itemTypeCategory.setDomain(domain);
        return itemTypeCategory;
        
    }
    
}
