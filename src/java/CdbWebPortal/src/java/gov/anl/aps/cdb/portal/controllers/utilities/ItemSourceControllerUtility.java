/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.ItemSourceFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemSource;

/**
 *
 * @author darek
 */
public class ItemSourceControllerUtility extends CdbEntityControllerUtility<ItemSource, ItemSourceFacade> {

    @Override
    protected ItemSourceFacade getEntityDbFacade() {
        return ItemSourceFacade.getInstance();
    }
        
    @Override
    public String getEntityTypeName() {
        return "itemSource";
    }
    
}
