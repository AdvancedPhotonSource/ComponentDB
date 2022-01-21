/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author craig
 */
@Named(ItemMultiEditDomainInventoryController.controllerNamed)
@SessionScoped
public class ItemMultiEditDomainInventoryController 
        extends ItemMultiEditDomainInventoryBaseController<ItemDomainInventory, ItemDomainInventoryController> 
        implements Serializable {

    public final static String controllerNamed = "itemMultiEditDomainInventoryController";

    public ItemDomainInventoryController getInventoryControllerInstance() {
        return ItemDomainInventoryController.getInstance();
    }

    @Override
    protected String getControllerNamedConstant() {
        return controllerNamed;
    }

    public static ItemMultiEditDomainInventoryController getInstance() {
        return (ItemMultiEditDomainInventoryController) SessionUtility.findBean(controllerNamed);
    }
    
    protected String generateUnitName(int unitCount) {
        return ItemDomainInventory.generatePaddedUnitName(unitCount);
    }

}
