/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.AuthorizationError;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableDesignControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.LocatableItemControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableDesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import static gov.anl.aps.cdb.portal.model.db.entities.CdbEntity.VALUE_CABLE_END_1;
import static gov.anl.aps.cdb.portal.model.db.entities.CdbEntity.VALUE_CABLE_END_2;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemUtility;
import gov.anl.aps.cdb.rest.authentication.Secured;
import gov.anl.aps.cdb.portal.view.objects.CableDesignConnectionListObject;
import gov.anl.aps.cdb.rest.entities.CableDesignConnectionListSummaryObject;
import gov.anl.aps.cdb.rest.entities.ItemDomainCableDesignIdListRequest;
import gov.anl.aps.cdb.rest.entities.ItemDomainCableDesignMetadata;
import gov.anl.aps.cdb.rest.entities.UpdateCableDesignAssignedItemInformation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author craig
 */
@Path("/CableDesignItems")
@Tag(name = "cableDesignItems")
public class CableDesignItemRoute extends ItemBaseRoute {

    private static final Logger LOGGER = LogManager.getLogger(CableDesignItemRoute.class.getName());

    @EJB
    ItemDomainCableDesignFacade facade;
    @EJB
    ItemDomainCableCatalogFacade cableTypeFacade;
    @EJB
    ItemDomainCableInventoryFacade cableInventoryFacade;
    @EJB
    ItemDomainMachineDesignFacade machineDesignFacade;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainCableDesign> getCableDesignItemList(@Parameter(description = "Optional bool to specify if connections should be included. "
            + "Can be accessed by `connectionList` of the cable design item.") @QueryParam("includeConnections") boolean includeConnections,
            @Parameter(description = "Optional bool to specify if connection endpoints should include locations. If true, connections will be automatically included.") @QueryParam("includeMdLocations") boolean includeMdLocations) {
        LOGGER.debug("Fetching cable design list.");
        List<ItemDomainCableDesign> cableDesigns = facade.findAll();

        if (includeMdLocations) {
            includeConnections = true;
        }

        if (includeConnections) {
            for (ItemDomainCableDesign cableDesign : cableDesigns) {
                loadConnectionInformation(cableDesign, includeMdLocations);
            }
        }

        return cableDesigns;
    }

    @GET
    @Path("/ById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDomainCableDesign getCableDesignItemById(@PathParam("id") int id) throws ObjectNotFound {
        LOGGER.debug("Fetching item with id: " + id);
        ItemDomainCableDesign item = facade.find(id);
        if (item == null) {
            ObjectNotFound ex = new ObjectNotFound("Could not find item with id: " + id);
            LOGGER.error(ex);
            throw ex;
        }
        loadConnectionInformation(item, true);
        return item;
    }

    @GET
    @Path("/ByName/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDomainCableDesign getCableDesignItemByName(@PathParam("name") String name) throws ObjectNotFound {
        LOGGER.debug("Fetching item with name: " + name);
        List<ItemDomainCableDesign> itemList = facade.findByName(name);
        if (itemList == null || itemList.isEmpty()) {
            ObjectNotFound ex = new ObjectNotFound("Could not find item with name: " + name);
            LOGGER.error(ex);
            throw ex;
        } else if (itemList.size() > 1) {
            ObjectNotFound ex = new ObjectNotFound("Found multiple items with name: " + name);
            LOGGER.error(ex);
            throw ex;
        }
        ItemDomainCableDesign item = itemList.get(0);
        loadConnectionInformation(item, true);
        return item;
    }

    @POST
    @Path("/IdList")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Integer> getCableDesignIdList(@RequestBody(required = true) ItemDomainCableDesignIdListRequest request) {
        List<String> nameList = request.getNameList();
        LOGGER.debug("Fetching list of cable design id's by name list size: " + nameList.size());
        List<Integer> idList = new ArrayList<>(nameList.size());
        for (String name : nameList) {
            if ((name != null) && (!name.isBlank())) {
                List<ItemDomainCableDesign> itemList = facade.findByName(name);
                if (itemList == null || itemList.isEmpty()) {
                    // use 0 to indicate that there is no item with specified name
                    idList.add(0);
                } else if (itemList.size() > 1) {
                    // use -1 to indicate that there are multiple items with same name
                    idList.add(-1);
                } else {
                    idList.add(itemList.get(0).getId());
                }
            } else {
                idList.add(0);
            }
        }
        return idList;
    }

    @POST
    @Path("/UpdateCableTypeName")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update cable type for cable design item given cable design id and cable type name.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainCableDesign updateCableTypeName(
            @RequestBody(required = true) UpdateCableDesignAssignedItemInformation info) throws CdbException {

        Integer cableDesignId = info.getCdItemId();
        String cableTypeName = info.getCableTypeName();

        // validate parameters
        if (cableDesignId == null || cableTypeName == null) {
            throw new InvalidArgument("Both cable design id and cable type name must be specified");
        }

        ItemDomainCableDesign cableDesignItem = (ItemDomainCableDesign) getItemByIdBase(cableDesignId);
        if (cableDesignItem == null) {
            throw new InvalidArgument(cableDesignId + " is not a valid cable design item id");
        }

        ItemDomainCableCatalog cableCatalogItem = cableTypeFacade.findUniqueByName(cableTypeName, null);
        if (cableCatalogItem == null) {
            throw new InvalidArgument(cableTypeName + " is not a valid cable type catalog name");
        }

        // update cable design item
        UserInfo currentUser = verifyCurrentUserPermissionForItem(cableDesignItem);
        ItemDomainCableDesignControllerUtility itemControllerUtility = cableDesignItem.getItemControllerUtility();
        itemControllerUtility.updateAssignedItem(cableDesignItem, cableCatalogItem, currentUser, null);
        itemControllerUtility.update(cableDesignItem, currentUser);

        return getCableDesignItemById(cableDesignId);
    }

    @POST
    @Path("/UpdateCableTypeId")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update cable type for cable design item given cable design id and cable type id.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainCableDesign updateCableTypeId(
            @RequestBody(required = true) UpdateCableDesignAssignedItemInformation info) throws CdbException {

        Integer cableDesignId = info.getCdItemId();
        Integer cableTypeId = info.getCableTypeId();

        // validate parameters
        if (cableDesignId == null || cableTypeId == null) {
            throw new InvalidArgument("Both cable design id and cable type id must be specified");
        }

        ItemDomainCableDesign cableDesignItem = (ItemDomainCableDesign) getItemByIdBase(cableDesignId);
        if (cableDesignItem == null) {
            throw new InvalidArgument(cableDesignId + " is not a valid cable design item id");
        }

        ItemDomainCableCatalog cableCatalogItem = cableTypeFacade.findById(cableTypeId);
        if (cableCatalogItem == null) {
            throw new InvalidArgument(cableTypeId + " is not a valid cable type catalog item id");
        }

        // update cable design item
        UserInfo currentUser = verifyCurrentUserPermissionForItem(cableDesignItem);
        ItemDomainCableDesignControllerUtility itemControllerUtility = cableDesignItem.getItemControllerUtility();
        itemControllerUtility.updateAssignedItem(cableDesignItem, cableCatalogItem, currentUser, null);
        itemControllerUtility.update(cableDesignItem, currentUser);

        return getCableDesignItemById(cableDesignId);
    }

    @POST
    @Path("/ClearCableType")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Clear cable type for cable design item given cable design id.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainCableDesign clearCableType(
            @RequestBody(required = true) UpdateCableDesignAssignedItemInformation info) throws CdbException {

        Integer cableDesignId = info.getCdItemId();

        // validate parameters
        if (cableDesignId == null) {
            throw new InvalidArgument("Cable design id must be specified");
        }

        ItemDomainCableDesign cableDesignItem = (ItemDomainCableDesign) getItemByIdBase(cableDesignId);
        if (cableDesignItem == null) {
            throw new InvalidArgument(cableDesignId + " is not a valid cable design item id");
        }

        // update cable design item
        UserInfo currentUser = verifyCurrentUserPermissionForItem(cableDesignItem);
        ItemDomainCableDesignControllerUtility itemControllerUtility = cableDesignItem.getItemControllerUtility();
        itemControllerUtility.updateAssignedItem(cableDesignItem, null, currentUser, null);
        itemControllerUtility.update(cableDesignItem, currentUser);

        return getCableDesignItemById(cableDesignId);
    }

    @POST
    @Path("/UpdateAssignedInventoryName")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update assigned inventory and installation status for cable design item given cable design id, inventory tag/name, and installation status.  Cable type id or name is optional and if not specified the inventory look up by name uses current cable type.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainCableDesign updateAssignedInventoryName(
            @RequestBody(required = true) UpdateCableDesignAssignedItemInformation info) throws CdbException {

        Integer cableDesignId = info.getCdItemId();
        String inventoryTag = info.getInventoryTag();
        Boolean isInstalled = info.getIsInstalled();
        String cableTypeName = info.getCableTypeName();
        Integer cableTypeId = info.getCableTypeId();

        // validate parameters
        if (cableDesignId == null || inventoryTag == null || isInstalled == null) {
            throw new InvalidArgument("Cable design id, inventory unit tag, and installation status must be specified");
        }

        ItemDomainCableDesign cableDesignItem = (ItemDomainCableDesign) getItemByIdBase(cableDesignId);
        if (cableDesignItem == null) {
            throw new InvalidArgument(cableDesignId + " is not a valid cable design item id");
        }

        // determine what cable type to use
        ItemDomainCableCatalog cableCatalogItem = null;
        if (cableTypeName != null) {
            cableCatalogItem = cableTypeFacade.findUniqueByName(cableTypeName, null);
            if (cableCatalogItem == null) {
                throw new InvalidArgument(cableTypeName + " is not a valid cable type catalog name");
            }
        } else if (cableTypeId != null) {
            cableCatalogItem = cableTypeFacade.findById(cableTypeId);
            if (cableCatalogItem == null) {
                throw new InvalidArgument(cableTypeId + " is not a valid cable type catalog item id");
            }
        } else {
            // use current cable type
            cableCatalogItem = cableDesignItem.getCatalogItem();
            if (cableCatalogItem == null) {
                throw new InvalidArgument("No cable type is currently assigned to cable design item, must specify cable type id or name to assign inventory");
            }
        }

        // look up inventory for specified cable type
        ItemDomainCableInventory inventoryItem = cableCatalogItem.getInventoryItemNamed(inventoryTag);
        if (inventoryItem == null) {
            throw new InvalidArgument(inventoryTag
                    + " is not a valid cable inventory name for cable type: '" + cableCatalogItem.getName() + "'");
        }

        // update cable design item
        UserInfo currentUser = verifyCurrentUserPermissionForItem(cableDesignItem);
        ItemDomainCableDesignControllerUtility itemControllerUtility = cableDesignItem.getItemControllerUtility();
        itemControllerUtility.updateAssignedItem(cableDesignItem, inventoryItem, currentUser, isInstalled);
        itemControllerUtility.update(cableDesignItem, currentUser);

        return getCableDesignItemById(cableDesignId);
    }

    @POST
    @Path("/UpdateAssignedInventoryId")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update assigned inventory and installation status for cable design item given cable design id, inventory item id, and installation status. Note this operation might change the cable design item's cable type if inventory is of different type.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainCableDesign updateAssignedInventoryId(
            @RequestBody(required = true) UpdateCableDesignAssignedItemInformation info) throws CdbException {

        Integer cableDesignId = info.getCdItemId();
        Integer inventoryId = info.getInventoryId();
        Boolean isInstalled = info.getIsInstalled();

        // validate parameters
        if (cableDesignId == null || inventoryId == null || isInstalled == null) {
            throw new InvalidArgument("Cable design id, inventory item id, and installation status must be specified");
        }

        ItemDomainCableDesign cableDesignItem = (ItemDomainCableDesign) getItemByIdBase(cableDesignId);
        if (cableDesignItem == null) {
            throw new InvalidArgument(cableDesignId + " is not a valid cable design item id");
        }

        ItemDomainCableInventory cableInventoryItem = cableInventoryFacade.findById(inventoryId);
        if (cableInventoryItem == null) {
            throw new InvalidArgument(inventoryId + " is not a valid cable inventory item id");
        }

        // update cable design item
        UserInfo currentUser = verifyCurrentUserPermissionForItem(cableDesignItem);
        ItemDomainCableDesignControllerUtility itemControllerUtility = cableDesignItem.getItemControllerUtility();
        itemControllerUtility.updateAssignedItem(cableDesignItem, cableInventoryItem, currentUser, isInstalled);
        itemControllerUtility.update(cableDesignItem, currentUser);

        return getCableDesignItemById(cableDesignId);
    }

    @POST
    @Path("/ClearAssignedInventory")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Clear assigned inventory and installation status for cable design item given cable design id. Cable design will retain existing cable type.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainCableDesign clearAssignedInventory(
            @RequestBody(required = true) UpdateCableDesignAssignedItemInformation info) throws CdbException {

        Integer cableDesignId = info.getCdItemId();

        // validate parameters
        if (cableDesignId == null) {
            throw new InvalidArgument("Cable design id must be specified");
        }

        ItemDomainCableDesign cableDesignItem = (ItemDomainCableDesign) getItemByIdBase(cableDesignId);
        if (cableDesignItem == null) {
            throw new InvalidArgument(cableDesignId + " is not a valid cable design item id");
        }

        // set assigned item to current cable type catalog item, if any (might be null if no inventory or cable type assigned)
        ItemDomainCableCatalog cableCatalogItem = cableDesignItem.getCatalogItem();

        // update cable design item
        UserInfo currentUser = verifyCurrentUserPermissionForItem(cableDesignItem);
        ItemDomainCableDesignControllerUtility itemControllerUtility = cableDesignItem.getItemControllerUtility();
        itemControllerUtility.updateAssignedItem(cableDesignItem, cableCatalogItem, currentUser, null);
        itemControllerUtility.update(cableDesignItem, currentUser);

        return getCableDesignItemById(cableDesignId);
    }

    @POST
    @Path("/UpdateInstallationStatus")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update installation status for cable design item given cable design id and installation status.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainCableDesign updateInstallationStatus(
            @RequestBody(required = true) UpdateCableDesignAssignedItemInformation info) throws CdbException {

        Integer cableDesignId = info.getCdItemId();
        Boolean isInstalled = info.getIsInstalled();

        // validate parameters
        if (cableDesignId == null || isInstalled == null) {
            throw new InvalidArgument("Cable design id and installation status must be specified");
        }

        ItemDomainCableDesign cableDesignItem = (ItemDomainCableDesign) getItemByIdBase(cableDesignId);
        if (cableDesignItem == null) {
            throw new InvalidArgument(cableDesignId + " is not a valid cable design item id");
        }

        // make sure there is an assigned inventory item
        ItemDomainCableInventory inventoryItem = cableDesignItem.getInventoryItem();
        if (inventoryItem == null) {
            throw new InvalidArgument("No inventory item assigned to cable design");
        }

        // update cable design item
        UserInfo currentUser = verifyCurrentUserPermissionForItem(cableDesignItem);
        ItemDomainCableDesignControllerUtility itemControllerUtility = cableDesignItem.getItemControllerUtility();
        itemControllerUtility.updateAssignedItem(cableDesignItem, inventoryItem, currentUser, isInstalled);
        itemControllerUtility.update(cableDesignItem, currentUser);

        return getCableDesignItemById(cableDesignId);
    }

    @DELETE
    @Path("/CableDesignEndpoint")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainCableDesign deleteCableDesignEndpoint(
            @Parameter(description = "DB ID of the cable.") @QueryParam("cableDesignId") Integer cableDesignId,
            @Parameter(description = "DB ID of the cable relationship") @QueryParam("endpointRelationshipId") Integer endpointRelationshipId) throws CdbException {
        ItemDomainCableDesign cableDesignItem = getCableDesignItemById(cableDesignId);
        verifyCurrentUserPermissionForItem(cableDesignItem);

        ItemElementRelationship endpointRelationship = findEndpointConnectionRelationship(cableDesignItem, endpointRelationshipId);

        cableDesignItem.deleteCableRelationship(endpointRelationship);

        ItemDomainCableDesignControllerUtility utility = cableDesignItem.getItemControllerUtility();
        UserInfo user = getCurrentRequestUserInfo();
        cableDesignItem = utility.update(cableDesignItem, user);

        loadConnectionInformation(cableDesignItem, false);
        return cableDesignItem;
    }

    @POST
    @Path("/CableDesignEndpointAddUpdate")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainCableDesign addOrUpdateCableDesignEndpoint(
            @Parameter(description = "DB ID of the cable.") @QueryParam("cableDesignId") Integer cableDesignId,
            @Parameter(description = "DB ID of the cable relationship. "
                    + "Update when provided or create.") @QueryParam("endpointRelationshipId") Integer endpointRelationshipId,
            @Parameter(description = "Cable End - 1 or 2. Required for new endpoints.") @QueryParam("cableEnd") Integer cableEnd,
            @Parameter(description = "Endpoint Machine design id") @QueryParam("machineDesignId") Integer machineDesignId,
            @Parameter(description = "Device port name") @QueryParam("devicePortName") String devicePortName,
            @Parameter(description = "Connector name") @QueryParam("connectorName") String connectorName) throws CdbException {

        if (cableDesignId == null) {
            throw new InvalidArgument("Cable Design id is required");
        }
        if (machineDesignId == null) {
            throw new InvalidArgument("Machine Design id is required");
        }

        String cableEndString = null;

        if (cableEnd != null) {
            cableEndString = String.valueOf(cableEnd);
            boolean validCableEnd = VALUE_CABLE_END_1.equals(cableEndString) || VALUE_CABLE_END_2.equals(cableEndString);
            if (!validCableEnd) {
                throw new InvalidArgument("Invalid cable end (1 or 2) provided.");
            }
        }

        ItemDomainCableDesign cableDesignItem = getCableDesignItemById(cableDesignId);
        verifyCurrentUserPermissionForItem(cableDesignItem);

        ItemDomainMachineDesign md = getMachineDesignById(machineDesignId);
        ItemConnector endpointConnector = findConnectorByName(md, devicePortName);
        ItemConnector cableConnector = findConnectorByName(cableDesignItem, connectorName);

        // Load Cable Connections 
        if (endpointRelationshipId != null) {
            ItemElementRelationship endpointRelationship = findEndpointConnectionRelationship(cableDesignItem, endpointRelationshipId);

            if (cableEndString == null) {
                cableEndString = endpointRelationship.getCableEndDesignation();
            }

            ValidInfo result = cableDesignItem.updateCableRelationship(endpointRelationship, md, endpointConnector, cableConnector, cableEndString);
            if (!result.isValid()) {
                throw new InvalidObjectState(result.getValidString());
            }
        } else {
            if (cableEnd == null) {
                throw new InvalidArgument("Cable end (1 or 2) is is required");
            }
            // New connection
            cableDesignItem.addCableRelationship(md, endpointConnector, cableConnector, cableEndString, false);
        }

        ItemDomainCableDesignControllerUtility utility = cableDesignItem.getItemControllerUtility();
        UserInfo user = getCurrentRequestUserInfo();
        cableDesignItem = utility.update(cableDesignItem, user);

        loadConnectionInformation(cableDesignItem, false);
        return cableDesignItem;
    }

    @POST
    @Path("/CableDesignAddUpdate")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainCableDesign addOrUpdateCableDesign(
            @Parameter(description = "DB ID of the cable. When none specified, item will be created.") @QueryParam("id") Integer id,
            @Parameter(description = "Cable design name") @QueryParam("name") String name,
            @Parameter(description = "Cable design qrId (-1 to clear)") @QueryParam("qrId") Integer qrId,
            @Parameter(description = "Cable design alternate name") @QueryParam("alternateName") String alternateName,
            @Parameter(description = "Description of the cable design") @QueryParam("description") String description,
            @Parameter(description = "Project Ids") @QueryParam("itemProjectIds") List<Integer> itemProjectIds,
            @Parameter(description = "Technical System Ids") @QueryParam("technicalSystemIds") List<Integer> technicalSystemIds,
            @Parameter(description = "Cable Type Id (-1 to clear)") @QueryParam("cableTypeId") Integer cableTypeId,
            @Parameter(description = "Endpoint 1 machine design id. \n"
                    + "(Must be specified to make any changes to endpoint 1)") @QueryParam("end1MachineDesignId") Integer end1MachineDesignId,
            @Parameter(description = "Endpoint 1 device port name") @QueryParam("end1DevicePortName") String end1DevicePortName,
            @Parameter(description = "Endpoint 1 connector name") @QueryParam("end1ConnectorName") String end1ConnectorName,
            @Parameter(description = "Endpoint 2 machine design id. \n"
                    + "(Must be specified to make any changes to endpoint 2)") @QueryParam("end2MachineDesignId") Integer end2MachineDesignId,
            @Parameter(description = "Endpoint 2 device port name") @QueryParam("end2DevicePortName") String end2DevicePortName,
            @Parameter(description = "Endpoint 2 connector name") @QueryParam("end2ConnectorName") String end2ConnectorName) throws CdbException {

        ItemDomainCableDesignControllerUtility utility = new ItemDomainCableDesignControllerUtility();
        ItemDomainCableDesign cableDesignItem = null;
        UserInfo user = getCurrentRequestUserInfo();

        if (id != null) {
            cableDesignItem = getCableDesignItemById(id);
            verifyCurrentUserPermissionForItem(cableDesignItem);
        } else {
            cableDesignItem = utility.createEntityInstance(user);

            // Initialize lists that will be populated below. 
            cableDesignItem.setItemProjectList(new ArrayList<>());
            cableDesignItem.setItemCategoryList(new ArrayList<>());
        }

        if (name != null) {
            cableDesignItem.setName(name);
        }

        if (qrId != null) {
            cableDesignItem.setQrId(qrId);
        }

        if (alternateName != null) {
            cableDesignItem.setAlternateName(alternateName);
        }

        if (description != null) {
            cableDesignItem.setDescription(description);
        }

        if (itemProjectIds != null && !itemProjectIds.isEmpty()) {
            List<ItemProject> itemProjectList = cableDesignItem.getItemProjectList();
            itemProjectList.clear();
            for (int itemProjectId : itemProjectIds) {
                ItemProject project = getItemProjectById(itemProjectId);
                itemProjectList.add(project);
            }

        }

        if (technicalSystemIds != null && !technicalSystemIds.isEmpty()) {
            List<ItemCategory> itemCategoryList = cableDesignItem.getItemCategoryList();
            itemCategoryList.clear();
            for (int technicalSystemId : technicalSystemIds) {
                ItemCategory techsys = getItemCategoryById(technicalSystemId);
                itemCategoryList.add(techsys);
            }
        }

        if (cableTypeId != null) {
            if (cableTypeId == -1) {
                cableDesignItem.setCatalogItem(null);
            } else {
                ItemDomainCableCatalog cableType = getCableCatalogById(cableTypeId);
                cableDesignItem.setCatalogItem(cableType);
            }
        }

        // Gather endpoint 1 information 
        if (end1MachineDesignId != null) {
            ItemDomainMachineDesign end1 = getMachineDesignById(end1MachineDesignId);
            ItemConnector portEnd1 = findConnectorByName(end1, end1DevicePortName);
            ItemConnector connectorEnd1 = findConnectorByName(cableDesignItem, end1ConnectorName);
            cableDesignItem.setPrimaryEndpoint(end1, portEnd1, connectorEnd1, VALUE_CABLE_END_1);
        } else {
            // Verify if other arguments were passed in. 
            if (end1DevicePortName != null || end1ConnectorName != null) {
                throw new InvalidArgument("Must specify end1 machine design id to make changes to end 1 port or conector");
            }
        }

        // Gather endpoint 2 information 
        if (end2MachineDesignId != null) {
            ItemDomainMachineDesign end2 = getMachineDesignById(end2MachineDesignId);
            ItemConnector portEnd2 = findConnectorByName(end2, end2DevicePortName);
            ItemConnector connectorEnd2 = findConnectorByName(cableDesignItem, end2ConnectorName);
            cableDesignItem.setPrimaryEndpoint(end2, portEnd2, connectorEnd2, VALUE_CABLE_END_2);
        } else {
            // Verify if other arguments were passed in. 
            if (end2DevicePortName != null || end2ConnectorName != null) {
                throw new InvalidArgument("Must specify end1 machine design id to make changes to end 2 port or conector");
            }
        }

        // Verify that all required fields have been defined. 
        if (cableDesignItem.getName().isEmpty()) {
            throw new InvalidObjectState("Required field cable name has not been defined.");
        }
        if (cableDesignItem.getPrimaryRelationshipForCableEnd(VALUE_CABLE_END_1) == null) {
            throw new InvalidObjectState("Required field cable endpoint 1 has not been defined.");
        }
        if (cableDesignItem.getPrimaryRelationshipForCableEnd(VALUE_CABLE_END_2) == null) {
            throw new InvalidObjectState("Required field cable endpoint 2 has not been defined.");
        }
        if (cableDesignItem.getItemProjectList().isEmpty()) {
            throw new InvalidObjectState("Required field project has no assignment.");
        }
        if (cableDesignItem.getItemCategoryList().isEmpty()) {
            throw new InvalidObjectState("Required field technical system has no assignment.");
        }

        if (id == null) {
            cableDesignItem = utility.create(cableDesignItem, user);
        } else {
            cableDesignItem = utility.update(cableDesignItem, user);
        }

        loadConnectionInformation(cableDesignItem, false);
        return cableDesignItem;
    }

    @POST
    @Path("/CableDesignMetadataUpdate")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainCableDesignMetadata updateCableDesignMetadata(@Parameter(description = "Only specified metadata fields will be set. "
            + "Object from fetching existing metadata can also be used as input.") @RequestBody(required = true) ItemDomainCableDesignMetadata cableDesignMetadata) throws InvalidArgument, ObjectNotFound, AuthorizationError, CdbException {
        // Check if all parameters are null
        if (cableDesignMetadata.isEmpty()) {
            throw new InvalidArgument("No metadata value was specified. There is nothing to update.");
        }

        Integer id = cableDesignMetadata.getCableDesignId();
        if (id == null) {
            throw new InvalidArgument("Cable design id is required to perform this update.");
        }

        ItemDomainCableDesign cableDesignItem = getCableDesignItemById(id);
        UserInfo currentUser = verifyCurrentUserPermissionForItem(cableDesignItem);
        ItemDomainCableDesignControllerUtility utility = new ItemDomainCableDesignControllerUtility();

        String externalCableName = cableDesignMetadata.getExternalCableName();
        String importCableId = cableDesignMetadata.getImportCableId();
        String alternateCableId = cableDesignMetadata.getAlternateCableId();
        String laying = cableDesignMetadata.getLaying();
        String voltage = cableDesignMetadata.getVoltage();
        String routedLength = cableDesignMetadata.getRoutedLength();
        String route = cableDesignMetadata.getRoute();
        String totalReqLength = cableDesignMetadata.getTotalReqLength();
        String notes = cableDesignMetadata.getNotes();

        String endpoint1Description = cableDesignMetadata.getEndpoint1Description();
        String endpoint1Route = cableDesignMetadata.getEndpoint1Route();
        String endpoint1EndLength = cableDesignMetadata.getEndpoint1EndLength();
        String endpoint1Termination = cableDesignMetadata.getEndpoint1Termination();
        String endpoint1Pinlist = cableDesignMetadata.getEndpoint1Pinlist();
        String endpoint1Notes = cableDesignMetadata.getEndpoint1Notes();
        String endpoint1Drawing = cableDesignMetadata.getEndpoint1Drawing();

        String endpoint2Description = cableDesignMetadata.getEndpoint2Description();
        String endpoint2Route = cableDesignMetadata.getEndpoint2Route();
        String endpoint2EndLength = cableDesignMetadata.getEndpoint2EndLength();
        String endpoint2Termination = cableDesignMetadata.getEndpoint2Termination();
        String endpoint2Pinlist = cableDesignMetadata.getEndpoint2Pinlist();
        String endpoint2Notes = cableDesignMetadata.getEndpoint2Notes();
        String endpoint2Drawing = cableDesignMetadata.getEndpoint2Drawing();

        // Update cable metadata when provided 
        if (externalCableName != null) {
            cableDesignItem.setExternalCableName(externalCableName);
        }
        if (importCableId != null) {
            cableDesignItem.setImportCableId(importCableId);
        }
        if (alternateCableId != null) {
            cableDesignItem.setAlternateCableId(alternateCableId);
        }
        if (laying != null) {
            cableDesignItem.setLaying(laying);
        }
        if (voltage != null) {
            cableDesignItem.setVoltage(voltage);
        }
        if (routedLength != null) {
            cableDesignItem.setRoutedLength(routedLength);
        }
        if (route != null) {
            cableDesignItem.setRoute(route);
        }
        if (totalReqLength != null) {
            cableDesignItem.setTotalReqLength(totalReqLength);
        }
        if (notes != null) {
            cableDesignItem.setNotes(notes);
        }

        // Update endpoint 2 metadata when provided 
        if (endpoint1Description != null) {
            cableDesignItem.setEndpoint1Description(endpoint1Description);
        }
        if (endpoint1Route != null) {
            cableDesignItem.setEndpoint1Route(endpoint1Route);
        }
        if (endpoint1EndLength != null) {
            cableDesignItem.setEndpoint1EndLength(endpoint1EndLength);
        }
        if (endpoint1Termination != null) {
            cableDesignItem.setEndpoint1Termination(endpoint1Termination);
        }
        if (endpoint1Pinlist != null) {
            cableDesignItem.setEndpoint1Pinlist(endpoint1Pinlist);
        }
        if (endpoint1Notes != null) {
            cableDesignItem.setEndpoint1Notes(endpoint1Notes);
        }
        if (endpoint1Drawing != null) {
            cableDesignItem.setEndpoint1Drawing(endpoint1Drawing);
        }

        // Update endpoint 2 metadata when provided 
        if (endpoint2Description != null) {
            cableDesignItem.setEndpoint2Description(endpoint2Description);
        }
        if (endpoint2Route != null) {
            cableDesignItem.setEndpoint2Route(endpoint2Route);
        }
        if (endpoint2EndLength != null) {
            cableDesignItem.setEndpoint2EndLength(endpoint2EndLength);
        }
        if (endpoint2Termination != null) {
            cableDesignItem.setEndpoint2Termination(endpoint2Termination);
        }
        if (endpoint2Pinlist != null) {
            cableDesignItem.setEndpoint2Pinlist(endpoint2Pinlist);
        }
        if (endpoint2Notes != null) {
            cableDesignItem.setEndpoint2Notes(endpoint2Notes);
        }
        if (endpoint2Drawing != null) {
            cableDesignItem.setEndpoint2Drawing(endpoint2Drawing);
        }

        utility.update(cableDesignItem, currentUser);

        return getCableDesignMetadata(id);
    }

    @GET
    @Path("/CableDesignMetadata/{cableDesignId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDomainCableDesignMetadata getCableDesignMetadata(@PathParam("cableDesignId") int cableDesignId) throws CdbException {
        ItemDomainCableDesign cableDesignItem = getCableDesignItemById(cableDesignId);
        return new ItemDomainCableDesignMetadata(cableDesignItem);
    }

    @GET
    @Path("/ConnectionList/{cableDesignId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CableDesignConnectionListObject> getCableDesignConnectionList(@PathParam("cableDesignId") int cableDesignId) throws ObjectNotFound {
        ItemDomainCableDesign cableDesign = getCableDesignItemById(cableDesignId);
        List<CableDesignConnectionListObject> connectionList = CableDesignConnectionListObject.getConnectionList(cableDesign);

        return connectionList;
    }

    private ItemElementRelationship findEndpointConnectionRelationship(ItemDomainCableDesign cableDesignItem, Integer endpointRelationshipId) throws InvalidArgument {
        List<ItemElementRelationship> endpointRelationships = ItemUtility.getItemRelationshipList(
                cableDesignItem,
                ItemElementRelationshipTypeNames.itemCableConnection.getValue(),
                false);

        ItemElementRelationship endpointRelationship = null;

        for (ItemElementRelationship ier : endpointRelationships) {
            if (Objects.equals(ier.getId(), endpointRelationshipId)) {
                endpointRelationship = ier;
                break;
            }
        }

        if (endpointRelationship == null) {
            throw new InvalidArgument("Cannot find endpoint relationship id.");
        }

        return endpointRelationship;
    }

    private ItemConnector findConnectorByName(Item itemWithConnector, String connectorName) throws ObjectNotFound {
        if (connectorName == null) {
            return null;
        }

        ItemConnector connector = itemWithConnector.getConnectorNamed(connectorName);
        if (connector == null) {
            throw new ObjectNotFound("Could not find " + connectorName + " in item id" + itemWithConnector.getId());
        }

        return connector;
    }

    public ItemDomainCableCatalog getCableCatalogById(int id) throws ObjectNotFound {
        ItemDomainCableCatalog cableType = cableTypeFacade.find(id);
        if (cableType == null) {
            ObjectNotFound ex = new ObjectNotFound("Could not find cable catalog (cable type) with id: " + id);
            LOGGER.error(ex);
            throw ex;
        }

        return cableType;
    }

    public ItemDomainMachineDesign getMachineDesignById(int id) throws ObjectNotFound {
        ItemDomainMachineDesign machine = machineDesignFacade.find(id);
        if (machine == null) {
            ObjectNotFound ex = new ObjectNotFound("Could not find machine design with id: " + id);
            LOGGER.error(ex);
            throw ex;
        }

        return machine;
    }

    private void loadConnectionInformation(ItemDomainCableDesign cableDesign, boolean includeLocation) {
        List<CableDesignConnectionListSummaryObject> connectionList
                = CableDesignConnectionListSummaryObject.getConnectionSummaryList(cableDesign);
        cableDesign.setConnectionList(connectionList);

        if (!includeLocation) {
            return;
        }

        LocatableItemControllerUtility controller = new LocatableItemControllerUtility();
        for (CableDesignConnectionListSummaryObject obj : connectionList) {
            ItemDomainMachineDesign mdItem = obj.getMdItem();
            controller.setItemLocationInfo(mdItem);
        }
    }

}
