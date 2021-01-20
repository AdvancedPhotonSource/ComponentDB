/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.PropertyMetadataFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/**
 *
 * @author darek
 */
public class PropertyMetadataControllerUtility extends CdbEntityControllerUtility<PropertyMetadata, PropertyMetadataFacade> {

    @Override
    protected PropertyMetadataFacade getEntityDbFacade() {
        return PropertyMetadataFacade.getInstance();
    }
        
    @Override
    public String getEntityTypeName() {
        return "propertyMetadata";
    }

    @Override
    public PropertyMetadata createEntityInstance(UserInfo sessionUser) {
        return new PropertyMetadata(); 
    }
    
}
