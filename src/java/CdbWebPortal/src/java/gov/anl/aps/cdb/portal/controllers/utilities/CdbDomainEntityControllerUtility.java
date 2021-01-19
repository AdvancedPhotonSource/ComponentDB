/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbDomainEntity;

/**
 *
 * @author darek
 */
public abstract class CdbDomainEntityControllerUtility<EntityType extends CdbDomainEntity, FacadeType extends CdbEntityFacade<EntityType>> 
        extends CdbEntityControllerUtility<EntityType, FacadeType> {
    
}
