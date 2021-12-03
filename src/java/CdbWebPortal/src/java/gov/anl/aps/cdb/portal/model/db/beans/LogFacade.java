/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.beans.builder.LogQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.LogLevel;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

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
    
    public List<Log> findByLogLevel(String logLevelName) {
        return findByLogLevel(logLevelName, null); 
    }
        
    public List<Log> findByLogLevel(String logLevelName, Date sinceEnteredDate) {
        String queryString = QUERY_STRING_START; 
        
        queryString += " JOIN l.logLevelList sll WHERE";
        queryString += " sll.name = '" + logLevelName + "'";
        if (sinceEnteredDate != null) {
            queryString += " AND l.enteredOnDateTime > :sinceDate ";
        }
        queryString += " ORDER BY l.id DESC";
        
        Query query = em.createQuery(queryString);
        if (sinceEnteredDate != null) {
            query.setParameter("sinceDate", sinceEnteredDate, TemporalType.TIMESTAMP); 
        }
        
        return (List<Log>) query.getResultList();        
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
    
    public List<Log> findByDataTableFilterQueryBuilder(LogQueryBuilder queryBuilder, Integer start, Integer limit) {
        String fullQuery = queryBuilder.getQueryForLogs();

        try {
            return (List<Log>) em.createQuery(fullQuery)
                    .setMaxResults(limit)
                    .setFirstResult(start).getResultList();
        } catch (NoResultException ex) {
        }

        return null;
    }
    
    public Long getCountForQuery(LogQueryBuilder queryBuilder) {
        String fullQuery = queryBuilder.getCountQueryForLogs();
        
        try {
            Query query = em.createQuery(fullQuery);
            long count = (long) query.getSingleResult();
            return count;
        } catch (NoResultException ex) {            
        }
        
        return Long.MIN_VALUE; 
    }
    
    public static LogFacade getInstance() {
        return (LogFacade) SessionUtility.findFacade(LogFacade.class.getSimpleName()); 
    }
    
}
