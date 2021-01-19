/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.LogTopic;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class LogTopicFacade extends CdbEntityFacade<LogTopic> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LogTopicFacade() {
        super(LogTopic.class);
    }

    public LogTopic findLogTopicByName(String name) {
        try{
            return (LogTopic) em.createNamedQuery("LogTopic.findByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {            
        }
        return null; 
    }
    
    public static LogTopicFacade getInstance() {
        return (LogTopicFacade) SessionUtility.findFacade(LogTopicFacade.class.getSimpleName()); 
    }

}
