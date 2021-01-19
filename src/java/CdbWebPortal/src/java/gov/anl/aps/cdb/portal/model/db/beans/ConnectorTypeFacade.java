/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.ConnectorType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class ConnectorTypeFacade extends CdbEntityFacade<ConnectorType> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConnectorTypeFacade() {
        super(ConnectorType.class);
    }
    
    public static ConnectorTypeFacade getInstance() {
        return (ConnectorTypeFacade) SessionUtility.findFacade(ConnectorTypeFacade.class.getSimpleName()); 
    }
    
}
