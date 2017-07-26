/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemMultiEditDomainCatalogController.controllerNamed)
@SessionScoped
public class ItemMultiEditDomainCatalogController extends ItemMultiEditController implements Serializable {
    
    public final static String controllerNamed = "itemMultiEditDomainCatalogController";
    
    private ItemDomainCatalogController itemDomainCatalogController = null; 

    @Override
    protected ItemController getItemController() {
        if (itemDomainCatalogController == null) {
            itemDomainCatalogController = ItemDomainCatalogController.getInstance();
        }
        return itemDomainCatalogController; 
    }

    @Override
    protected String getControllerNamedConstant() {
        return controllerNamed; 
    }
    
    public static ItemMultiEditDomainCatalogController getInstance() {
        return (ItemMultiEditDomainCatalogController) SessionUtility.findBean(controllerNamed);
    }   
    
}
