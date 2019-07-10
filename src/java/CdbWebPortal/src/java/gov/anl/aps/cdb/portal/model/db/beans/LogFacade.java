/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.LogLevel;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author djarosz
 */
@Stateless
public class LogFacade extends CdbEntityFacade<Log> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;
    
    private final String QUERY_STRING_START = "SELECT l FROM Log l ";

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LogFacade() {
        super(Log.class);
    }
    
    public List<Log> findByFilterViewSystemLogAttributes(
            List<LogLevel> systemLogLevels) {
        String queryString = QUERY_STRING_START; 
        
        queryString += " JOIN l.logLevelList sll WHERE";
        
        for (LogLevel logLevel: systemLogLevels) {
            if (systemLogLevels.indexOf(logLevel) != 0) {
                queryString += " OR"; 
            }
            Integer logLevelId = logLevel.getId(); 
            queryString += " sll.id = " + logLevelId;             
        }
        
        queryString += " ORDER BY l.id DESC";
        
        return (List<Log>) em.createQuery(queryString).getResultList();
        
    }
    
    public static LogFacade getInstance() {
        return (LogFacade) SessionUtility.findFacade(LogFacade.class.getSimpleName()); 
    }
    
}
