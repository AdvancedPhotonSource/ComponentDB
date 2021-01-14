/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;

/**
 *
 * @author darek
 * @param <ItemInventoryEntityType>
 * @param <ItemDomainEntityFacade>
 */
public abstract class ItemDomainInventoryBaseControllerUtility<ItemInventoryEntityType extends ItemDomainInventoryBase, ItemDomainEntityFacade extends ItemFacadeBase<ItemInventoryEntityType>> 
        extends ItemControllerUtility<ItemInventoryEntityType, ItemDomainEntityFacade> {               

    @Override
    public boolean isEntityHasName() {
        return true; 
    }

    @Override
    public boolean isEntityHasQrId() {
        return true; 
    }

    @Override
    public boolean isEntityHasProject() {
        return true; 
    }
}
