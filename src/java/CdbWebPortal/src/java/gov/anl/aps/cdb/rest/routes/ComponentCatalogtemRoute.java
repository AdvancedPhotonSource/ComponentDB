/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCatalogControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.authentication.Secured;
import gov.anl.aps.cdb.rest.entities.NewCatalogInformation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.ejb.EJB;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Path("/ComponentCatalogItems")
@Tag(name = "componentCatalogItems")
public class ComponentCatalogtemRoute extends ItemBaseRoute {
    
    private static final Logger LOGGER = LogManager.getLogger(ComponentCatalogtemRoute.class.getName());
    
    @EJB
    ItemDomainCatalogFacade facade; 
    
    @PUT
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create machine placeholder item.")    
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainCatalog create(@RequestBody(required = true) NewCatalogInformation catalogInformation) throws InvalidArgument, CdbException {
        ItemDomainCatalogControllerUtility utility = new ItemDomainCatalogControllerUtility(); 
        UserInfo requestUser = getCurrentRequestUserInfo();
        
        ItemDomainCatalog createEntityInstance = utility.createEntityInstance(requestUser); 
        
        catalogInformation.updateItemDomainCatalogWithInformation(createEntityInstance);
               
        utility.create(createEntityInstance, requestUser); 
        
        return createEntityInstance; 
    }
            
    
    

}
