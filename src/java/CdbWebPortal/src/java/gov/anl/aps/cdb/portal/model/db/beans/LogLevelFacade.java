/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.LogLevel;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class LogLevelFacade extends CdbEntityFacade<LogLevel> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LogLevelFacade() {
        super(LogLevel.class);
    }
    
    public LogLevel findLogLevelByName(String name) {
        try{
            return (LogLevel) em.createNamedQuery("LogLevel.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null; 
    }
    
}
