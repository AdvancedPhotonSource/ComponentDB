/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.beans.SourceFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class SourceControllerUtility extends CdbEntityControllerUtility<Source, SourceFacade> {

    private static final Logger logger = LogManager.getLogger(SourceControllerUtility.class.getName());   

    @Override
    public void prepareEntityInsert(Source source, UserInfo userInfo) throws ObjectAlreadyExists {
        Source existingSource = getEntityDbFacade().findByName(source.getName());
        if (existingSource != null) {
            throw new ObjectAlreadyExists("Source " + source.getName() + " already exists.");
        }
        logger.debug("Inserting new source " + source.getName());
    }

    @Override
    public void prepareEntityUpdate(Source source, UserInfo userInfo) throws ObjectAlreadyExists {
        Source existingSource = getEntityDbFacade().findByName(source.getName());
        if (existingSource != null && !existingSource.getId().equals(source.getId())) {
            throw new ObjectAlreadyExists("Source " + source.getName() + " already exists.");
        }
        logger.debug("Updating source " + source.getName());
    }
    
    @Override
    protected SourceFacade getEntityDbFacade() {
        return SourceFacade.getInstance(); 
    }

    @Override
    public String getEntityInstanceName(Source entity) {
        if (entity != null) {
            return entity.getName();
        }
        return "";
    }
        
    @Override
    public String getEntityTypeName() {
        return "source";
    }   

    @Override
    public Source createEntityInstance(UserInfo sessionUser) {
        return new Source();
    }
    
}
