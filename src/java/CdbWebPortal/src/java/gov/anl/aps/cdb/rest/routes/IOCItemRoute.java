/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.AuthorizationError;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignBaseControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignIOCControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemElementControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyMetadataValue;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemMetadataIOC;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.authentication.Secured;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
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

    @EJB
    PropertyTypeFacade propertyTypeFacade;

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

        if (!isCurrentRequestUserAdmin()) {
            throw new AuthorizationError("IOCs should be created using IOC API. APIs can run this call.");
        }

        ItemDomainMachineDesign machine = machineFacade.find(machineId);
        UserInfo currentUser = verifyCurrentUserPermissionForItem(machine);
        verifyItemReady(machine);

        ItemElement machineElement = machine.getParentMachineElement();

        if (machineElement != null) {
            List<ItemElement> machineElementList = new ArrayList<>();
            machineElementList.add(machineElement);
            // Remove template
            ItemDomainMachineDesignIOCControllerUtility utility = new ItemDomainMachineDesignIOCControllerUtility();
            utility.unassignMachineTemplate(machineElementList, currentUser);

            // Reload cache after template removal.
            machine = machineFacade.find(machineId);
        }

        // Unassign assigned item. 
        machine.setAssignedItem(null);

        // No uniqueness automation for IOCs.
        machine.setItemIdentifier2(null);

        machine.addEntityType(EntityTypeName.ioc.getValue());

        return updateIocInfo(machine, currentUser, machineTag, functionTag, deploymentStatus, null, null, null, null, true);
    }

    @POST
    @Path("/IOCAddUpdate")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add or update IOC item.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainMachineDesign addOrUpdateIOC(
            @Parameter(description = "DB ID of the IOC. When none specified, a new item will be created.") @QueryParam("id") Integer id,
            @Parameter(description = "IOC name (required for new items, optional for updates)") @QueryParam("name") String name,
            @Parameter(description = "Description of the IOC (optional, updated when specified)") @QueryParam("description") String description,
            @Parameter(description = "Project IDs to assign (required for new items, replaces existing assignments when specified)") @QueryParam("itemProjectIds") List<Integer> itemProjectIds,
            @Parameter(description = "Parent Machine ID (-1 to clear parent, optional)") @QueryParam("parentMachineId") Integer parentMachineId,
            @Parameter(description = "Machine Tag (required for new items, updated when specified)") @QueryParam("machineTag") String machineTag,
            @Parameter(description = "Function Tag (required for new items, updated when specified)") @QueryParam("functionTag") String functionTag,
            @Parameter(description = "Deployment Status (required for new items, updated when specified)") @QueryParam("deploymentStatus") String deploymentStatus,
            @Parameter(description = "Preboot Instructions (optional, updated when specified)") @QueryParam("prebootInstructions") String prebootInstructions,
            @Parameter(description = "Postboot Instructions (optional, updated when specified)") @QueryParam("postbootInstructions") String postbootInstructions,
            @Parameter(description = "Power Cycle Instructions (optional, updated when specified)") @QueryParam("powerCycleInstructions") String powerCycleInstructions,
            @Parameter(description = "Additional Instructions (optional, updated when specified)") @QueryParam("additionalInstructions") String additionalInstructions) throws CdbException, AuthorizationError {

        ItemDomainMachineDesignIOCControllerUtility utility = new ItemDomainMachineDesignIOCControllerUtility();
        ItemDomainMachineDesign iocItem = null;
        UserInfo user = getCurrentRequestUserInfo();

        if (id != null) {
            iocItem = machineFacade.find(id);
            verifyCurrentUserPermissionForItem(iocItem);
            verifyItemReady(iocItem);
        } else {
            iocItem = (ItemDomainMachineDesign) utility.createEntityInstance(user);

            // Initialize lists that will be populated below.
            iocItem.setItemProjectList(new ArrayList<>());

            // Add IOC entity type
            iocItem.addEntityType(EntityTypeName.ioc.getValue());
        }

        // TODO add parent item 
        if (name != null) {
            iocItem.setName(name);
        }

        if (description != null) {
            iocItem.setDescription(description);
        }

        if (itemProjectIds != null && !itemProjectIds.isEmpty()) {
            List<ItemProject> itemProjectList = iocItem.getItemProjectList();
            itemProjectList.clear();
            for (int itemProjectId : itemProjectIds) {
                ItemProject project = getItemProjectById(itemProjectId);
                itemProjectList.add(project);
            }
        }

        if (parentMachineId != null) {
            if (parentMachineId == -1 && iocItem.getId() != null) {
                // Clear parent
                ItemElement parentMachineElement = iocItem.getParentMachineElement();
                if (parentMachineElement != null) {
                    ItemElementControllerUtility ieUtility = parentMachineElement.getControllerUtility();
                    ieUtility.destroy(parentMachineElement, user);
                    iocItem = machineFacade.find(id);
                }
            } else {
                ItemDomainMachineDesign parentMachine = machineFacade.find(parentMachineId);

                if (parentMachine.getEntityTypeList().isEmpty() == false) {
                    throw new InvalidArgument("Parent machine cannot be of any subtype.");
                }
                utility.updateMachineParent(iocItem, user, parentMachine);
            }
        }

        // Verify that all required fields have been defined.
        if (iocItem.getName() == null || iocItem.getName().isEmpty()) {
            throw new InvalidObjectState("Required field IOC name has not been defined.");
        }
        if (iocItem.getItemProjectList().isEmpty()) {
            throw new InvalidObjectState("Required field project has no assignment.");
        }

        // Update IOC specific information
        iocItem = updateIocInfo(iocItem, user, machineTag, functionTag, deploymentStatus,
                prebootInstructions, postbootInstructions, powerCycleInstructions, additionalInstructions, false);

        if (id == null) {
            iocItem = (ItemDomainMachineDesign) utility.create(iocItem, user);
        } else {
            iocItem = (ItemDomainMachineDesign) utility.update(iocItem, user);
        }

        return iocItem;
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
            @Parameter(description = "Preboot Instructions") @QueryParam("prebootInstructions") String prebootInstructions,
            @Parameter(description = "Postboot Instructions") @QueryParam("postbootInstructions") String postbootInstructions,
            @Parameter(description = "Power Cycle Instructions") @QueryParam("powerCycleInstructions") String powerCycleInstructions,
            @Parameter(description = "Additional Instructions") @QueryParam("additionalInstructions") String additionalInstructions) throws AuthorizationError, CdbException {

        ItemDomainMachineDesign machine = machineFacade.find(iocId);

        UserInfo currentUser = verifyCurrentUserPermissionForItem(machine);
        verifyItemReady(machine);

        return updateIocInfo(machine, currentUser, machineTag, functionTag, deploymentStatus, prebootInstructions, postbootInstructions, powerCycleInstructions, additionalInstructions, true);
    }

    private void verifyItemReady(ItemDomainMachineDesign item) throws CdbException {
        if (ItemDomainMachineDesign.isItemDeleted(item)) {
            throw new CdbException("The machine id " + item.getId() + " is a deleted item.");
        }
    }

    private ItemDomainMachineDesign updateIocInfo(ItemDomainMachineDesign iocItem, UserInfo currentUser, String machineTag, String functionTag, String deploymentStatus, String prebootInstructions, String postbootInstructions, String powerCycleInstructions, String additionalInstructions, boolean performUpdate) throws CdbException {
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

        boolean instructionsUpdated = false;

        if (prebootInstructions != null) {
            iocInfo.setPreBoot(prebootInstructions);
            instructionsUpdated = true;
        }

        if (postbootInstructions != null) {
            iocInfo.setPostBoot(postbootInstructions);
            instructionsUpdated = true;
        }

        if (powerCycleInstructions != null) {
            iocInfo.setPowerCycle(powerCycleInstructions);
            instructionsUpdated = true;
        }

        if (additionalInstructions != null) {
            iocInfo.setAdditionalMd(additionalInstructions);
            instructionsUpdated = true;
        }

        if (instructionsUpdated) {
            iocInfo.generateUpdatedInstructionsMarkdown();
        }

        if (performUpdate) {
            iocItem = utility.update(iocItem, currentUser);
        }

        return iocItem;
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainMachineDesign> getIOCItemList() {
        List<ItemDomainMachineDesign> iocItems = machineFacade.getIOCItems();

        return iocItems;
    }

    @GET
    @Path("/allowedValues/machineTag")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AllowedPropertyMetadataValue> getAllowedMachineTags() {
        return getIOCPropertyAllowedValues(ItemMetadataIOC.IOC_ITEM_MACHINE_TAG_KEY);
    }

    @GET
    @Path("/allowedValues/functionTag")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AllowedPropertyMetadataValue> getAllowedFunctionTags() {
        return getIOCPropertyAllowedValues(ItemMetadataIOC.IOC_ITEM_FUNCTION_TAG_KEY);
    }

    @GET
    @Path("/allowedValues/deploymentStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AllowedPropertyMetadataValue> getAllowedDeploymentStatuses() {
        return getIOCPropertyAllowedValues(ItemMetadataIOC.IOC_DEPLOYMENT_STATUS_KEY);
    }

    @GET
    @Path("/coreMetadataPropertyType")
    @Produces(MediaType.APPLICATION_JSON)
    public PropertyType getIOCItemCorePropertyType() {
        return propertyTypeFacade.findByName(ItemMetadataIOC.IOC_ITEM_INTERNAL_PROPERTY_TYPE);
    }

    private List<AllowedPropertyMetadataValue> getIOCPropertyAllowedValues(String keyName) {
        PropertyType property = getIOCItemCorePropertyType();

        PropertyTypeMetadata propertyTypeMetadataForKey = property.getPropertyTypeMetadataForKey(keyName);
        return propertyTypeMetadataForKey.getAllowedPropertyMetadataValueList();
    }

}
