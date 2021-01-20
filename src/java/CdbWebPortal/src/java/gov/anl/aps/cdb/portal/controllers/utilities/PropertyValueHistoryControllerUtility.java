/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueHistoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/**
 *
 * @author darek
 */
public class PropertyValueHistoryControllerUtility extends CdbEntityControllerUtility<PropertyValueHistory, PropertyValueHistoryFacade> {

    @Override
    protected PropertyValueHistoryFacade getEntityDbFacade() {
        return PropertyValueHistoryFacade.getInstance();
    }
        
    @Override
    public String getEntityTypeName() {
        return "propertyValueHistory";
    }

    @Override
    public PropertyValueHistory createEntityInstance(UserInfo sessionUser) {
        return new PropertyValueHistory();
    }
    
}
