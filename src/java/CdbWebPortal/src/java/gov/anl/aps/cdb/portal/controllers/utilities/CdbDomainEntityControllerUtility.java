/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbDomainEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.Date;
import java.util.List;

/**
 *
 * @author darek
 */
public abstract class CdbDomainEntityControllerUtility<EntityType extends CdbDomainEntity, FacadeType extends CdbEntityFacade<EntityType>>
        extends CdbEntityControllerUtility<EntityType, FacadeType> {
    
    public Log prepareAddLog(EntityType cdbDomainEntity, UserInfo user) {
        Log logEntry = null;        
        logEntry = LogUtility.createLogEntry(user);        
        
        List<Log> cdbDomainEntityLogList = cdbDomainEntity.getLogList();
        cdbDomainEntityLogList.add(0, logEntry);
        return logEntry; 
    }

    public PropertyValue preparePropertyTypeValueAdd(EntityType cdbDomainEntity, PropertyType propertyType) {
        return preparePropertyTypeValueAdd(cdbDomainEntity, propertyType, propertyType.getDefaultValue(), null);
    }

    public PropertyValue preparePropertyTypeValueAdd(EntityType cdbDomainEntity,
            PropertyType propertyType, String propertyValueString, String tag) {
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        return preparePropertyTypeValueAdd(cdbDomainEntity, propertyType, propertyValueString, tag, lastModifiedByUser);
    }

    public PropertyValue preparePropertyTypeValueAdd(EntityType cdbDomainEntity,
            PropertyType propertyType, String propertyValueString, String tag,
            UserInfo updatedByUser) {
        Date lastModifiedOnDateTime = new Date();

        PropertyValue propertyValue = new PropertyValue();
        propertyValue.setPropertyType(propertyType);
        propertyValue.setValue(propertyValueString);
        propertyValue.setUnits(propertyType.getDefaultUnits());
        cdbDomainEntity.addPropertyValueToPropertyValueList(propertyValue);
        propertyValue.setEnteredByUser(updatedByUser);
        propertyValue.setEnteredOnDateTime(lastModifiedOnDateTime);
        if (tag != null) {
            propertyValue.setTag(tag);
        }

        cdbDomainEntity.resetPropertyValueLists();

        // Get method called by GUI populates metadata
        // Needed for multi-edit or API to also populate metadata
        propertyValue.getPropertyValueMetadataList();

        return propertyValue;
    }

}
