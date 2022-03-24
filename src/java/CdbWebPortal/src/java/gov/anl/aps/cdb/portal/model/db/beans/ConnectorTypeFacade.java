/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.ConnectorType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
    
    @Override
    public List<ConnectorType> findAll() {
        return (List<ConnectorType>) em.createNamedQuery("ConnectorType.findAll")
                .getResultList();
    }

    public ConnectorType findByName(String name) {
        try {
            return (ConnectorType) em.createNamedQuery("ConnectorType.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }
    
    /**
     * Find unique entity by name.  Returns null if none is found, or raises
     * CdbException if multiple instances are found.
     */
    @Override
    public ConnectorType findUniqueByName(String name, String filterDomainName) throws CdbException {
        
        if ((name == null) || (name.isEmpty())) {
            return null;
        }
        
        // uses findByName() since that method already finds a unique instance by name
        return findByName(name);       
    }

}
