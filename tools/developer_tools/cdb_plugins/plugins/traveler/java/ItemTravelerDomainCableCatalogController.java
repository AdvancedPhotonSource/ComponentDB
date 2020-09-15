/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemTravelerDomainCableCatalogController.controllerNamed)
@SessionScoped
public class ItemTravelerDomainCableCatalogController extends ItemTravelerDomainTemplateControllerBase implements Serializable {

    public final static String controllerNamed = "itemTravelerDomainCableCatalogController";

    private ItemDomainCableCatalogController itemDomainCatalogController = null;

    @Override
    protected ItemController getItemController() {
        if (itemDomainCatalogController == null) {
            itemDomainCatalogController = ItemDomainCableCatalogController.getInstance();
        }
        return itemDomainCatalogController;
    }
}
