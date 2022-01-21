/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableInventoryController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author craig
 */
@Named(ItemMultiEditDomainCableInventoryController.controllerNamed)
@SessionScoped
public class ItemMultiEditDomainCableInventoryController 
        extends ItemMultiEditDomainInventoryBaseController<ItemDomainCableInventory, ItemDomainCableInventoryController> 
        implements Serializable {

    public final static String controllerNamed = "itemMultiEditDomainCableInventoryController";

    public ItemDomainCableInventoryController getInventoryControllerInstance() {
        return ItemDomainCableInventoryController.getInstance();
    }

    @Override
    protected String getControllerNamedConstant() {
        return controllerNamed;
    }

    public static ItemMultiEditDomainCableInventoryController getInstance() {
        return (ItemMultiEditDomainCableInventoryController) SessionUtility.findBean(controllerNamed);
    }
    
    protected String generateUnitName(int unitCount) {
        return ItemDomainCableInventory.generatePaddedUnitName(unitCount);
    }

}
