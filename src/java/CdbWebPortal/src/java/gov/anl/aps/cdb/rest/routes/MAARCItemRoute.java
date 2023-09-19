/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.AuthorizationError;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMAARCControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMAARCFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMAARC;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.authentication.Secured;
import gov.anl.aps.cdb.rest.entities.NewMAARCInformation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
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
@Path("/MAARCItems")
@Tag(name = "MAARCItems")
public class MAARCItemRoute extends ItemBaseRoute {

    private static final Logger LOGGER = LogManager.getLogger(MachineDesignItemRoute.class.getName());

    @EJB
    ItemDomainMAARCFacade facade;

    @GET
    @Path("/ByName/{name}")
    @Operation(summary = "Fetches a single maarc item by unique name.")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDomainMAARC getMAARCItemByName(@PathParam("name") String name) throws ObjectNotFound {
        LOGGER.debug("Fetching items with name: " + name);
        List<ItemDomainMAARC> itemList = facade.findByName(name);
        if (itemList == null || itemList.isEmpty()) {
            ObjectNotFound ex = new ObjectNotFound("Could not find item with name: " + name);
            LOGGER.error(ex);
            throw ex;
        } else if (itemList.size() > 1) {
            ObjectNotFound ex = new ObjectNotFound("Multiple results exit for: " + name);
            LOGGER.error(ex);
            throw ex;
        }
        return itemList.get(0);
    }

    @PUT
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create maarc placeholder item.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainMAARC create(@RequestBody(required = true) NewMAARCInformation newMAARCInformation) throws InvalidArgument, AuthorizationError, CdbException {
        ItemDomainMAARCControllerUtility utility = new ItemDomainMAARCControllerUtility();
        UserInfo requestUser = getCurrentRequestUserInfo();
        Integer parentItemId = newMAARCInformation.getParentItemId();
        String parentElementName = newMAARCInformation.getParentElementName();
        
        ItemDomainMAARC newItem = utility.createEntityInstance(requestUser);
        newMAARCInformation.updateItemDomainMAARCWithInformation(newItem);
        
        ItemDomainMAARC parentItem = null;
        if (parentItemId != null) {
            parentItem = (ItemDomainMAARC) this.getItemByIdBase(parentItemId);            
            
            ItemElement createItemElement = utility.createItemElement(parentItem, requestUser);
            
            if (parentElementName != null) {
                createItemElement.setName(parentElementName);
            } else {
                parentElementName = createItemElement.getName(); 
            }
            createItemElement.setContainedItem(newItem);

            utility.saveNewItemElement(createItemElement, requestUser);                                        
        } else {
            utility.create(newItem, requestUser);
        }

        return newItem;

    }

}
