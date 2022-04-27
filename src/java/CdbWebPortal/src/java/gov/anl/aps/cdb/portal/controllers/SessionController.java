/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;

@Named(SessionController.controllerNamed)
@ApplicationScoped
public class SessionController {

    public static final String controllerNamed = "sessionController";
    public static final String DATE_FORMAT = "MM/dd/yy HH:mm:ss";
    public static final String INVALIDATED_SESSION = "Invalidated Session"; 

    public static final long BROWSER_LAST_CONNECTED_MIN_TIME = 25000;
    
    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    private Set<HttpSession> allSessions;

    // Stats 
    private String oldestActiveSession = null;
    private String newestActiveSession = null;
    private Integer countActiveSessions = null;

    @PostConstruct
    public void initialize() {
        allSessions = new HashSet<>();
    }

    public static SessionController getInstance() {
        return (SessionController) SessionUtility.findBean(controllerNamed);
    }

    public void registerSession(HttpSession session) {
        allSessions.add(session);
    }

    public Set<HttpSession> getAllSessions() {
        return allSessions;
    }

    public String getSessionTime(HttpSession session) {
        long time = 0;
        try {
            time = session.getCreationTime();
        } catch (IllegalStateException e) {
            return INVALIDATED_SESSION; 
        }

        return formatSessionDate(time);
    }

    private static String formatSessionDate(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat(DATE_FORMAT);
        return format.format(date);
    }

    public String getUsername(HttpSession session) {
        UserInfo user = null;
        try {
            user = (UserInfo) session.getAttribute(SessionUtility.USER_KEY);
        } catch (IllegalStateException e) {
            return INVALIDATED_SESSION; 
        }

        if (user != null) {
            return user.getUsername();
        } else {
            return "Not Logged In";
        }
    }

    public String browserLastConnected(HttpSession session) {
        long lastAccessed = getLastAccessedTime(session);
        
        if (lastAccessed == -1) {
            return INVALIDATED_SESSION; 
        }

        String result = "";

        if (BROWSER_LAST_CONNECTED_MIN_TIME < lastAccessed) {
            result += "Disconnected - ";
        } else {
            result += "Connected - ";
        }
        
        result += "Last Ping:"; 

        long seconds = lastAccessed / 1000;

        if (seconds > 59) {
            long min = seconds / 60;
            result += min + " m ";
        }

        result += (seconds % 60) + " s";

        return result;
    }

    public boolean renderInvalidateSession(HttpSession session) {
        long lastAccessed = getLastAccessedTime(session);
        
        if (lastAccessed == -1) {
            return false; 
        }

        return BROWSER_LAST_CONNECTED_MIN_TIME < lastAccessed;
    }

    private long getLastAccessedTime(HttpSession session) {
        long lastAccessedTime = 0; 
        try {
            lastAccessedTime = session.getLastAccessedTime();
        } catch (IllegalStateException e) {
            return -1; 
        }
        
        long currentTimeMillis = System.currentTimeMillis();

        return currentTimeMillis - lastAccessedTime;
    }
    
    public void invalidateSession(HttpSession session) {
        if (renderInvalidateSession(session)) {
            session.invalidate(); 
        } else {
            SessionUtility.addErrorMessage("Error", "Cannot invalidate a connected session");
        }
    }
 
    public void clearInvalidated() {
        List<HttpSession> sessionsToRemove = new ArrayList<>();
        for (HttpSession session : allSessions) {
            try {
                session.getCreationTime();
            } catch (IllegalStateException e) {
                sessionsToRemove.add(session);
            }
        }
  
        allSessions.removeAll(sessionsToRemove);
    }    
    
    public void clearCaches() {
        EntityManagerFactory entityManagerFactory = em.getEntityManagerFactory();
        Cache cache = entityManagerFactory.getCache();
        cache.evictAll(); 
    }
    
    public void updateStats() {
        List<Long> activeSessionTimes = new ArrayList<>();
        for (HttpSession session : allSessions) {
            long time = -1;
            try {
                time = session.getCreationTime();
            } catch (IllegalStateException e) {
                // Invalidad Session
            }

            if (time != -1) {
                activeSessionTimes.add(time);
            }
        }

        countActiveSessions = activeSessionTimes.size();

        if (countActiveSessions > 0) {
            long minTime = activeSessionTimes.get(0);
            long maxTime = activeSessionTimes.get(0);

            for (long activeSessionTime : activeSessionTimes) {

                if (activeSessionTime < minTime) {
                    minTime = activeSessionTime;
                }
                if (activeSessionTime > maxTime) {
                    maxTime = activeSessionTime;
                }
            }

            oldestActiveSession = formatSessionDate(minTime);
            newestActiveSession = formatSessionDate(maxTime);
        }
    }

    public String getOldestActiveSession() {
        return oldestActiveSession;
    }

    public String getNewestActiveSession() {
        return newestActiveSession;
    }

    public Integer getCountActiveSessions() {
        return countActiveSessions;
    }

    public int getSessionCount() {
        return allSessions.size();
    }

}
