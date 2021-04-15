/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class PropertyValueHistoryFacade extends CdbEntityFacade<PropertyValueHistory> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PropertyValueHistoryFacade() {
        super(PropertyValueHistory.class);
    }
    
    public static PropertyValueHistoryFacade getInstance() {
        return (PropertyValueHistoryFacade) SessionUtility.findFacade(PropertyValueHistoryFacade.class.getSimpleName()); 
    }
    
}
