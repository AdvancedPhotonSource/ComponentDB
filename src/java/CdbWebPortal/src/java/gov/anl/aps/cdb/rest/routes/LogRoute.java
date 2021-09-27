/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.InvalidSession;
import gov.anl.aps.cdb.portal.constants.SystemLogLevel;
import gov.anl.aps.cdb.portal.model.db.beans.LogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.rest.authentication.Secured;
import gov.anl.aps.cdb.rest.entities.DateParam;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.text.ParseException;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
@Path("/Logs")
@Tag(name = "Log")
public class LogRoute extends BaseRoute {
    
    private static final Logger LOGGER = LogManager.getLogger(LogRoute.class.getName());
    
    @EJB
    LogFacade logFacade; 
    
    @GET
    @Path("/System/LoginInfo")    
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public List<Log> getSuccessfulLoginLog() throws InvalidSession {
        checkSystemLogPrivilage();
        LOGGER.debug("Fetching successful login log entries.");
        String loginInfoLevel = SystemLogLevel.loginInfo.toString(); 
        return logFacade.findByLogLevel(loginInfoLevel);
    }
    
    @GET
    @Path("/System/LoginWarning")    
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public List<Log> getUnsuccessfulLoginLog() throws InvalidSession {
        checkSystemLogPrivilage();
        LOGGER.debug("Fetching unsuccessful login log entries.");
        String loginInfoLevel = SystemLogLevel.loginWarning.toString(); 
        return logFacade.findByLogLevel(loginInfoLevel);
    }
    
    @GET
    @Path("/System/EntityInfo")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public List<Log> getSuccessfulEntityUpdateLog() throws InvalidSession {
        checkSystemLogPrivilage();
        LOGGER.debug("Fetching entity warnings.");
        String loginInfoLevel = SystemLogLevel.entityInfo.toString(); 
        return logFacade.findByLogLevel(loginInfoLevel);
    }
    
    @GET
    @Path("/System/EntityInfoSinceEnteredDate/{sinceDate}")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public List<Log> getSuccessfulEntityUpdateLogSinceEnteredDate(@PathParam("sinceDate") String sinceDate) throws ParseException, InvalidSession {        
        DateParam sinceDateParam = new DateParam(sinceDate); 
        checkSystemLogPrivilage();
        LOGGER.debug("Fetching entity info since: " + sinceDate);
        String loginInfoLevel = SystemLogLevel.entityInfo.toString(); 
        return logFacade.findByLogLevel(loginInfoLevel, sinceDateParam.getDate());
    }
    
    @GET
    @Path("/System/EntityWarning")    
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public List<Log> getUnsuccessfulEntityUpdateLog() throws InvalidSession {
        checkSystemLogPrivilage();
        LOGGER.debug("Fetching entity warnings.");
        String loginInfoLevel = SystemLogLevel.entityWarning.toString(); 
        return logFacade.findByLogLevel(loginInfoLevel);
    }
    
    @GET
    @Path("/System/EntityWarningSinceEnteredDate/{sinceDate}")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public List<Log> getUnsuccessfulEntityUpdateLogSinceEnteredDate(@PathParam("sinceDate") String sinceDate) throws ParseException, InvalidSession {        
        DateParam sinceDateParam = new DateParam(sinceDate);
        checkSystemLogPrivilage();
        LOGGER.debug("Fetching entity warnings since: " + sinceDate);
        String loginInfoLevel = SystemLogLevel.entityWarning.toString(); 
        return logFacade.findByLogLevel(loginInfoLevel, sinceDateParam.getDate());
    }
    
    private void checkSystemLogPrivilage() throws InvalidSession {        
        if (isCurrentRequestUserAdmin() == false) {
            throw new InvalidSession("Only users with the administrator privilage can view system logs."); 
        }
    }
}
