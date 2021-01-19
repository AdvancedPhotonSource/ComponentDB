/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.ItemProjectFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;

/**
 *
 * @author darek
 */
public class ItemProjectControllerUtility extends CdbEntityControllerUtility<ItemProject, ItemProjectFacade> {

    @Override
    protected ItemProjectFacade getEntityDbFacade() {
        return ItemProjectFacade.getInstance();
    }

    @Override
    public String getEntityInstanceName(ItemProject entity) {
        if (entity != null) {
            return entity.getName();
        }
        return "";
    }
        
    @Override
    public String getEntityTypeName() {
        return "itemProject";
    }
}
