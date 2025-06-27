/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.AuthorizationError;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignBaseControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemMetadataIOC;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.authentication.Secured;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.ejb.EJB;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author djarosz
 */
@Path("/IOCItems")
@Tag(name = "IOCItems")
public class IOCItemRoute extends ItemBaseRoute {

    @EJB
    ItemDomainMachineDesignFacade machineFacade;

    @POST
    @Path("/convertToIOC/{mdId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Move machine to a new parent.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainMachineDesign convertMachineToIOC(@PathParam("mdId") int machineId,
            @Parameter(description = "Machine Tag") @QueryParam("machineTag") String machineTag,
            @Parameter(description = "Function Tag") @QueryParam("functionTag") String functionTag,
            @Parameter(description = "Deployment Status") @QueryParam("deploymentStatus") String deploymentStatus) throws AuthorizationError, CdbException {
        ItemDomainMachineDesign machine = machineFacade.find(machineId);

        UserInfo currentUser = verifyCurrentUserPermissionForItem(machine);

        machine.addEntityType(EntityTypeName.ioc.getValue());

        if (ItemDomainMachineDesign.isItemDeleted(machine)) {
            throw new CdbException("The machine id " + machine.getId() + " is a deleted item.");
        }

        ItemDomainMachineDesignBaseControllerUtility utility = machine.getItemControllerUtility();

        ItemMetadataIOC iocInfo = machine.getIocInfo();
        if (machineTag != null) {
            iocInfo.setMachineTag(machineTag);
        }
        if (functionTag != null) {
            iocInfo.setFunctionTag(functionTag);
        }
        if (deploymentStatus != null) {
            iocInfo.setDeploymentStatus(deploymentStatus);
        }

        machine = utility.update(machine, currentUser);

        return machine;
    }

}
