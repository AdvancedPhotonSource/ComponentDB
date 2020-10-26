/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainLocationFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.authentication.Secured;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Path("/LocationItems")
@Tag(name = "locationItems")
public class LocationItemRoute extends ItemBaseRoute {

    private static final Logger LOGGER = LogManager.getLogger(LocationItemRoute.class.getName());

    @EJB
    ItemDomainLocationFacade facade;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainLocation> getLocationItemList() {
        LOGGER.debug("Fetching machine design list");
        return facade.findAll();
    }

    @GET
    @Path("/ById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDomainLocation getLocationItemById(@PathParam("id") int id) throws ObjectNotFound {
        LOGGER.debug("Fetching item with id: " + id);
        ItemDomainLocation item = facade.find(id);
        if (item == null) {
            ObjectNotFound ex = new ObjectNotFound("Could not find item with id: " + id);
            LOGGER.error(ex);
            throw ex;
        }
        return item;
    }

    @GET
    @Path("/ByName/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainLocation> getLocationItemsByName(@PathParam("name") String name) throws ObjectNotFound {
        LOGGER.debug("Fetching items with name: " + name);
        List<ItemDomainLocation> itemList = facade.findByName(name);
        if (itemList == null || itemList.isEmpty()) {
            ObjectNotFound ex = new ObjectNotFound("Could not find item with name: " + name);
            LOGGER.error(ex);
            throw ex;
        }
        return itemList;
    } 
    
    @POST
    @Path("/UpdateLocationParent/{locationItemId}/{newParentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update location parent.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainLocation updateLocationParent(@PathParam("locationItemId") Integer locationItemId, @PathParam("newParentId") Integer newParentId) throws ObjectNotFound, CdbException {
        ItemDomainLocation currentItem = getLocationItemById(locationItemId);
        ItemDomainLocation newParent = getLocationItemById(newParentId); 

        UserInfo currentUser = verifyCurrentUserPermissionForItem(currentItem);                        
        
        ItemDomainLocationController itemDomainController = ItemDomainLocationController.getApiInstance(); 
        itemDomainController.updateParentForItem(currentItem, newParent);
        
        itemDomainController.updateFromApi(currentItem, currentUser);
        
        return getLocationItemById(locationItemId);
    }
    

}
