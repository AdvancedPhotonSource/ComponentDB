/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainInventoryControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.authentication.Secured;
import gov.anl.aps.cdb.rest.entities.NewInventoryInformation;
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
@Path("/ComponentInventoryItems")
@Tag(name = "componentInventoryItems")
public class ComponentInventorytemRoute extends ItemBaseRoute {
    
    private static final Logger LOGGER = LogManager.getLogger(ComponentInventorytemRoute.class.getName());
    
    @EJB
    ItemDomainInventoryFacade facade; 
    
    @PUT
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create inventory item.")    
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainInventory createInventory(@RequestBody(required = true) NewInventoryInformation inventoryInformation) throws InvalidArgument, CdbException {
        ItemDomainInventoryControllerUtility utility = new ItemDomainInventoryControllerUtility(); 
        UserInfo requestUser = getCurrentRequestUserInfo();
        
        ItemDomainInventory createEntityInstance = utility.createEntityInstance(requestUser); 
        
        inventoryInformation.updateItemDomainInventoryWithInformation(createEntityInstance);
               
        utility.create(createEntityInstance, requestUser); 
        
        return createEntityInstance; 
    }
            
    
    

}
