/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.SystemLogLevel;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.List;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ItemDomainCableDesignControllerUtility extends ItemControllerUtility<ItemDomainCableDesign, ItemDomainCableDesignFacade> {

    private static final Logger LOGGER = LogManager.getLogger(CdbEntityControllerUtility.class.getName());

    @Override
    public boolean isEntityHasQrId() {
        return false;
    }

    @Override
    public boolean isEntityHasName() {
        return true;
    }

    @Override
    public boolean isEntityHasProject() {
        return true;
    }
    
    @Override
    public boolean isEntityHasItemIdentifier2() {
        return false;
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.cableDesign.getValue(); 
    }

    @Override
    protected ItemDomainCableDesignFacade getItemFacadeInstance() {
        return ItemDomainCableDesignFacade.getInstance(); 
    }
        
    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
        
    @Override
    public String getEntityTypeName() {
        return "cableDesign";
    }

    public void updateListAndDestroyRelationships(
            List<ItemDomainCableDesign> items, 
            List<ItemElementRelationship> relationships, 
            UserInfo user) throws CdbException, RuntimeException {
        
        try {
            for (ItemDomainCableDesign entity : items) {
                LOGGER.debug("Updating " + getDisplayEntityTypeName() + " " + getEntityInstanceName(entity));
                prepareEntityUpdate(entity, user);
            }
            getEntityDbFacade().editEntitiesAndDestroyRelationships(items, relationships);
            for (ItemDomainCableDesign entity : items) {
                entity.setPersitanceErrorMessage(null);
                addCdbEntitySystemLog(SystemLogLevel.entityInfo.toString(), "Updated: " + entity.getSystemLogString(), user);
            }
        } catch (CdbException ex) {
            LOGGER.error("Could not update " + getDisplayEntityTypeName() + " entities: " + ex.getMessage());
            setPersistenceErrorMessageForList(items, ex.getMessage());
            addCdbEntityWarningSystemLog("Failed to update list of " + getDisplayEntityTypeName(), ex, null, user);
            throw ex;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            LOGGER.error("Could not update list of " + getDisplayEntityTypeName() + ": " + t.getMessage());
            addCdbEntityWarningSystemLog("Failed to update list of " + getDisplayEntityTypeName(), ex, null, user); 
            setPersistenceErrorMessageForList(items, t.getMessage());
            throw ex;
        }
    }
}
