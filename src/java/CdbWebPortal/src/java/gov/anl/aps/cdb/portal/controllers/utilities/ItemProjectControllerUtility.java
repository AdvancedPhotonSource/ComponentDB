/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.ItemProjectFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

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

    @Override
    public ItemProject createEntityInstance(UserInfo sessionUser) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
