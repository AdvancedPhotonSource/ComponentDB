/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemTravelerDomainCatalogController.controllerNamed)
@SessionScoped
public class ItemTravelerDomainCatalogController extends ItemTravelerDomainTemplateControllerBase implements Serializable {

    public final static String controllerNamed = "itemTravelerDomainCatalogController";

    private ItemDomainCatalogController itemDomainCatalogController = null;

    @Override
    protected ItemController getItemController() {
        if (itemDomainCatalogController == null) {
            itemDomainCatalogController = ItemDomainCatalogController.getInstance();
        }
        return itemDomainCatalogController;
    }
}
