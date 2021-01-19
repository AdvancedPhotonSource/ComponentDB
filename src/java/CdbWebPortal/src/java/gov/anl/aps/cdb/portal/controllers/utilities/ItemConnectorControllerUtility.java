/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.ItemConnectorFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;

/**
 *
 * @author darek
 */
public class ItemConnectorControllerUtility extends CdbEntityControllerUtility<ItemConnector, ItemConnectorFacade> {

    @Override
    protected ItemConnectorFacade getEntityDbFacade() {
        return ItemConnectorFacade.getInstance();
    }
    
    @Override
    public String getEntityTypeName() {
        return "itemConnector";
    }
       
}
