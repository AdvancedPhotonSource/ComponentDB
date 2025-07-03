/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.AuthorizationError;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
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
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.GET;
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
        verifyItemReady(machine);

        machine.addEntityType(EntityTypeName.ioc.getValue());

        return updateIocInfo(machine, currentUser, machineTag, functionTag, deploymentStatus, null);
    }

    @POST
    @Path("/updateIOCInfo/{iocId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update IOC Info.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainMachineDesign updateIOCInfo(@PathParam("iocId") int iocId,
            @Parameter(description = "Machine Tag") @QueryParam("machineTag") String machineTag,
            @Parameter(description = "Function Tag") @QueryParam("functionTag") String functionTag,
            @Parameter(description = "Deployment Status") @QueryParam("deploymentStatus") String deploymentStatus,
            @Parameter(description = "Boot Instructions") @QueryParam("bootInstructions") String bootInstructions) throws AuthorizationError, CdbException {

        ItemDomainMachineDesign machine = machineFacade.find(iocId);

        UserInfo currentUser = verifyCurrentUserPermissionForItem(machine);
        verifyItemReady(machine);

        return updateIocInfo(machine, currentUser, machineTag, functionTag, deploymentStatus, bootInstructions);
    }

    private void verifyItemReady(ItemDomainMachineDesign item) throws CdbException {
        if (ItemDomainMachineDesign.isItemDeleted(item)) {
            throw new CdbException("The machine id " + item.getId() + " is a deleted item.");
        }
    }

    private ItemDomainMachineDesign updateIocInfo(ItemDomainMachineDesign iocItem, UserInfo currentUser, String machineTag, String functionTag, String deploymentStatus, String bootInstructions) throws CdbException {
        ItemDomainMachineDesignBaseControllerUtility utility = iocItem.getItemControllerUtility();

        if (!ItemDomainMachineDesign.isItemIOC(iocItem)) {
            throw new CdbException("Not and IOC Item");
        }

        ItemMetadataIOC iocInfo = iocItem.getIocInfo();
        if (machineTag != null) {
            iocInfo.setMachineTag(machineTag);
        }
        if (functionTag != null) {
            iocInfo.setFunctionTag(functionTag);
        }
        if (deploymentStatus != null) {
            iocInfo.setDeploymentStatus(deploymentStatus);
        }

        iocItem = utility.update(iocItem, currentUser);

        return iocItem;
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainMachineDesign> getIOCItemList() {
        List<ItemDomainMachineDesign> iocItems = machineFacade.getIOCItems();

        return iocItems;
    }

}
