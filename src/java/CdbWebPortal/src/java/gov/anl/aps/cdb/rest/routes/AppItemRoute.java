/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainAppControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainAppFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainApp;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.authentication.Secured;
import gov.anl.aps.cdb.rest.entities.NewAppInformation;
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
@Path("/AppItems")
@Tag(name = "appItems")
public class AppItemRoute extends ItemBaseRoute {

    private static final Logger LOGGER = LogManager.getLogger(AppItemRoute.class.getName());

    @EJB
    ItemDomainAppFacade facade;

    @PUT
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create app item.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainApp createApp(@RequestBody(required = true) NewAppInformation appInfo) throws InvalidArgument, CdbException {
        ItemDomainAppControllerUtility utility = new ItemDomainAppControllerUtility();
        UserInfo requestUser = getCurrentRequestUserInfo();

        ItemDomainApp createEntityInstance = utility.createEntityInstance(requestUser);

        appInfo.updateItemDomainAppwithInformation(createEntityInstance);

        utility.create(createEntityInstance, requestUser);

        return createEntityInstance;
    }   

}
