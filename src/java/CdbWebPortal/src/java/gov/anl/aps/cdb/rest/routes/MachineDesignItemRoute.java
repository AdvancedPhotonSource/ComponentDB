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
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignBaseControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControlControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.LocatableItemControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemProjectFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainMachineDesignQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.view.objects.MachineDesignConnectorListObject;
import gov.anl.aps.cdb.rest.authentication.Secured;
import gov.anl.aps.cdb.rest.entities.ControlRelationshipHierarchy;
import gov.anl.aps.cdb.rest.entities.ItemDomainMdSearchResult;
import gov.anl.aps.cdb.rest.entities.ItemHierarchy;
import gov.anl.aps.cdb.rest.entities.NewControlRelationshipInformation;
import gov.anl.aps.cdb.rest.entities.NewMachinePlaceholderOptions;
import gov.anl.aps.cdb.rest.entities.PromoteMachineElementInformation;
import gov.anl.aps.cdb.rest.entities.RepresentingAssemblyElementForMachineInformation;
import gov.anl.aps.cdb.rest.entities.UpdateMachineAssignedItemInformation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.TreeNode;

/**
 *
 * @author craig
 */
@Path("/MachineDesignItems")
@Tag(name = "machineDesignItems")
public class MachineDesignItemRoute extends ItemBaseRoute {

    private static final Logger LOGGER = LogManager.getLogger(MachineDesignItemRoute.class.getName());

    @EJB
    ItemDomainMachineDesignFacade facade;
    @EJB
    ItemElementFacade itemElementFacade;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainMachineDesign> getMachineDesignItemList() {
        LOGGER.debug("Fetching machine design list");
        return facade.findAll();
    }

    public ItemDomainMachineDesign getMachineDesignItemById(int id) throws ObjectNotFound {
        return getMachineDesignItemById(id, false);
    }

    @GET
    @Path("/ById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDomainMachineDesign getMachineDesignItemById(@PathParam("id") int id,
            @Parameter(description = "Optional bool to specify if location should be loaded. See item.locationItem.")
            @QueryParam("includeLocation") boolean includeLocation) throws ObjectNotFound {
        LOGGER.debug("Fetching item with id: " + id);
        ItemDomainMachineDesign item = facade.find(id);
        if (item == null) {
            ObjectNotFound ex = new ObjectNotFound("Could not find item with id: " + id + ". Ensure that it is a machine item id.");
            LOGGER.error(ex);
            throw ex;
        }

        if (includeLocation) {
            LocatableItemControllerUtility controller = new LocatableItemControllerUtility();
            controller.setItemLocationInfo(item);
            ItemDomainLocation activeInheritedLocation = item.getActiveInheritedLocation();
            item.setLocation(activeInheritedLocation);
        }

        return item;
    }

    @GET
    @Path("/ByName/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainMachineDesign> getMachineDesignItemsByName(@PathParam("name") String name,
            @Parameter(description = "Optional bool to specify if location should be loaded. See item.locationItem.")
            @QueryParam("includeLocation") boolean includeLocation) throws ObjectNotFound {
        LOGGER.debug("Fetching items with name: " + name);
        List<ItemDomainMachineDesign> itemList = facade.findByName(name);
        if (itemList == null || itemList.isEmpty()) {
            ObjectNotFound ex = new ObjectNotFound("Could not find item with name: " + name);
            LOGGER.error(ex);
            throw ex;
        }

        if (includeLocation) {
            LocatableItemControllerUtility controller = new LocatableItemControllerUtility();
            for (ItemDomainMachineDesign md : itemList) {
                controller.setItemLocationInfo(md);
                ItemDomainLocation activeInheritedLocation = md.getActiveInheritedLocation();
                md.setLocation(activeInheritedLocation);
            }
        }

        return itemList;
    }

    @GET
    @Path("/ByNamePattern/{namePattern}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainMachineDesign> getMachineDesignItemsByNamePattern(@PathParam("namePattern") String namePattern) throws ObjectNotFound {
        LOGGER.debug("Fetching items with name pattern: " + namePattern);
        Map<String, String> filterMap = new HashMap<>();
        filterMap.put(ItemQueryBuilder.QueryTranslator.name.getValue(), namePattern);
        ItemQueryBuilder queryBuilder = new ItemDomainMachineDesignQueryBuilder(ItemDomainName.MACHINE_DESIGN_ID, filterMap);
        List<ItemDomainMachineDesign> itemList = facade.findByDataTableFilterQueryBuilder(queryBuilder);

        return itemList;
    }

    @GET
    @Path("/ById/{itemId}/HousingHierarchy")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Fetch an item by its id.")
    public ItemHierarchy getHousingHierarchyById(@PathParam("itemId") int id) throws ObjectNotFound {
        ItemDomainMachineDesign currentItem = getMachineDesignItemById(id);

        ItemHierarchy ih = new ItemHierarchy();
        ih.setItem(currentItem);

        ItemDomainMachineDesign parentMachine = currentItem.getParentMachineDesign();

        while (parentMachine != null) {
            ItemHierarchy parentHierarchy = new ItemHierarchy();
            parentHierarchy.setItem(parentMachine);

            List<ItemHierarchy> children = new ArrayList<>();
            children.add(ih);
            parentHierarchy.setChildItems(children);

            ih = parentHierarchy;
            parentMachine = parentMachine.getParentMachineDesign();
        }

        return ih;
    }

    @GET
    @Path("/DetailedMachineDesignSearch/{searchText}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainMdSearchResult> getDetailedMdSearchResults(@PathParam("searchText") String searchText) throws ObjectNotFound, InvalidArgument {
        LOGGER.debug("Performing a detailed machine design item search for search query: " + searchText);

        ItemDomainMachineDesignControllerUtility mdInstance = new ItemDomainMachineDesignControllerUtility();

        TreeNode rootNode = mdInstance.getSearchResults(searchText, true);

        List<TreeNode> children = rootNode.getChildren();
        List<ItemDomainMdSearchResult> itemHierarchy = new ArrayList<>();
        for (TreeNode child : children) {
            ItemDomainMdSearchResult hierarchy = new ItemDomainMdSearchResult(child);
            itemHierarchy.add(hierarchy);
        }

        return itemHierarchy;
    }

    @POST
    @Path("/PromoteMachineElement")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a machine design that represents an assembly element that is assigned to machine hierarchy.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainMachineDesign promoteAssemblyElementToMachine(@RequestBody(required = true) PromoteMachineElementInformation info) throws CdbException {
        ItemDomainMachineDesignControllerUtility machineUtility = new ItemDomainMachineDesignControllerUtility();

        int parentMdItemId = info.getParentMdItemId();
        ItemDomainMachineDesign parentMachine = getMachineDesignItemById(parentMdItemId);
        UserInfo creatorUser = verifyCurrentUserPermissionForItem(parentMachine);
        ItemElement assyElement = itemElementFacade.find(info.getAssemblyElementId());

        ItemDomainMachineDesign promotedMachine = machineUtility.createRepresentingMachineForAssemblyElement(parentMachine, assyElement, creatorUser);

        String newName = info.getNewName();
        if (newName != null) {
            promotedMachine.setName(newName);
        }

        promotedMachine = machineUtility.create(promotedMachine, creatorUser);

        return promotedMachine;
    }

    @POST
    @Path("/UpdateRepresentingAssemblyElementForMachine")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a machine design to represent parent's assembly element for its assigned item.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainMachineDesign updateRepresentingAssemblyElementForMachine(@RequestBody(required = true) RepresentingAssemblyElementForMachineInformation info) throws InvalidArgument, ObjectNotFound, InvalidObjectState, CdbException {
        ItemDomainMachineDesignControllerUtility machineUtility = new ItemDomainMachineDesignControllerUtility();

        int machineId = info.getMachineId();
        Integer assemblyElementId = info.getAssemblyElementId();
        boolean templateMatchByNameForCustomBuild = info.isTemplateMatchByNameForCustomBuild();

        ItemDomainMachineDesign machine = getMachineDesignItemById(machineId);
        if (!machine.getIsItemTemplate() && templateMatchByNameForCustomBuild) {
            throw new InvalidArgument("Template match by name for custom build can only be provided when updating a template machine element.");
        }
        UserInfo updateUser = verifyCurrentUserPermissionForItem(machine);
        ItemElement assyElement;
        if (assemblyElementId != null) {
            assyElement = itemElementFacade.find(assemblyElementId);
        } else {
            // Cearing the assigned item. 
            assyElement = null;
        }

        machineUtility.updateRepresentingAssemblyElementForMachine(machine, assyElement, templateMatchByNameForCustomBuild);
        machine = machineUtility.update(machine, updateUser);

        return machine;
    }

    @POST
    @Path("/unassignTemplateFromMachineElement/{machineId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Unassign template from a machine element.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainMachineDesign unassignTemplateFromMachineElement(@PathParam("machineId") int machineId) throws InvalidArgument, ObjectNotFound, CdbException {
        ItemDomainMachineDesign machineItem = getMachineDesignItemById(machineId);

        UserInfo updateUser = verifyCurrentUserPermissionForItem(machineItem);

        ItemDomainMachineDesignControllerUtility machineUtility = new ItemDomainMachineDesignControllerUtility();
        ItemElement machineElement = machineItem.getParentMachineElement();

        List<ItemElement> itemElementList = new ArrayList<>();
        itemElementList.add(machineElement);

        machineUtility.unassignMachineTemplate(itemElementList, updateUser);

        machineItem = getMachineDesignItemById(machineId);
        return machineItem;
    }

    @POST
    @Path("/UpdateAssignedItem")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update assigned item in a machine design item.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainMachineDesign updateAssignedItem(@RequestBody(required = true) UpdateMachineAssignedItemInformation info) throws CdbException {
        int mdItemId = info.getMdItemId();
        Integer assignedItemId = info.getAssignedItemId();
        Boolean isInstalled = info.getIsInstalled();

        return updateAssignedItemForMd(mdItemId, assignedItemId, isInstalled);
    }

    @POST
    @Path("/UpdateAssignedItem/{mdItemId}/{assignedItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update assigned item in a machine design item.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    @Deprecated
    public ItemDomainMachineDesign updateAssignedItemDeprecated(@PathParam("mdItemId") int mdItemId, @PathParam("assignedItemId") Integer assignedItemId) throws ObjectNotFound, CdbException {
        return updateAssignedItemForMd(mdItemId, assignedItemId);
    }

    private ItemDomainMachineDesign updateAssignedItemForMd(int mdItemId, Integer assignedItemId) throws ObjectNotFound, CdbException {
        return updateAssignedItemForMd(mdItemId, assignedItemId, null);
    }

    private ItemDomainMachineDesign updateAssignedItemForMd(int mdItemId, Integer assignedItemId, Boolean isInstalled) throws ObjectNotFound, CdbException {
        Item currentItem = getItemByIdBase(mdItemId);
        Item assignedItem = null;
        if (assignedItemId != null) {
            assignedItem = getItemByIdBase(assignedItemId);
        }

        if (currentItem instanceof ItemDomainMachineDesign == false) {
            throw new InvalidArgument("Item with id " + mdItemId + " is not of type Machine Design");
        }

        UserInfo currentUser = verifyCurrentUserPermissionForItem(currentItem);
        ItemDomainMachineDesign mdItem = (ItemDomainMachineDesign) currentItem;

        ItemDomainMachineDesignBaseControllerUtility itemControllerUtility = mdItem.getItemControllerUtility();

        itemControllerUtility.updateAssignedItem(mdItem, assignedItem, currentUser, isInstalled);

        itemControllerUtility.update(mdItem, currentUser);

        return getMachineDesignItemById(mdItemId);
    }

    @POST
    @Path("/ClearAssignedItem/{mdItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update assigned item in a machine design item.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainMachineDesign clearAssignedItem(@PathParam("mdItemId") int mdItemId) throws CdbException {
        return updateAssignedItemForMd(mdItemId, null);
    }

    @PUT
    @Path("/createPlaceholder/{parentMdId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create machine placeholder item.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainMachineDesign createPlaceholder(
            @PathParam("parentMdId") int parentMdId,
            @RequestBody(required = true) NewMachinePlaceholderOptions newMachinePlaceholderOptions) throws InvalidArgument, AuthorizationError, CdbException {

        ItemDomainMachineDesign parentItem = facade.findById(parentMdId);
        if (parentItem instanceof ItemDomainMachineDesign == false) {
            throw new InvalidArgument("parent item id provided is not a machine.");
        }

        UserInfo currentUser = verifyCurrentUserPermissionForItem(parentItem);

        ItemDomainMachineDesignBaseControllerUtility itemControllerUtility = parentItem.getItemControllerUtility();

        ItemElement machinePlaceholder = itemControllerUtility.prepareMachinePlaceholder(parentItem, currentUser);

        ItemDomainMachineDesign newMachine = (ItemDomainMachineDesign) machinePlaceholder.getContainedItem();

        assignDataToMachineElement(newMachine, newMachinePlaceholderOptions);

        itemControllerUtility.saveNewItemElement(machinePlaceholder, currentUser);

        return newMachine;

    }

    public void assignDataToMachineElement(ItemDomainMachineDesign newMachine, NewMachinePlaceholderOptions options) throws InvalidArgument {
        String name = options.getName();

        if (name == null || name.isEmpty()) {
            throw new InvalidArgument("Name must be provided to create a new placeholder.");
        }

        String alternateName = options.getAlternateName();
        String description = options.getDescription();

        newMachine.setName(name);
        newMachine.setAlternateName(alternateName);
        newMachine.setDescription(description);

        if (options.getProjectId() != null) {
            Integer projectId = options.getProjectId();
            ItemProjectFacade projectFacade = ItemProjectFacade.getInstance();
            ItemProject project = projectFacade.find(projectId);

            List<ItemProject> itemProjectList = newMachine.getItemProjectList();
            if (itemProjectList == null) {
                itemProjectList = new ArrayList<>();
                newMachine.setItemProjectList(itemProjectList);
            }

            if (project != null) {
                itemProjectList.clear();
                itemProjectList.add(project);
            } else {
                throw new InvalidArgument("Project with id: " + projectId + " cannot be found");
            }
        }

    }

    @POST
    @Path("/moveMachine/{mdId}/{newParentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Move machine to a new parent.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemElement moveMachine(@PathParam("mdId") int childId,
            @PathParam("newParentId") int newParentId) throws AuthorizationError, CdbException {

        ItemDomainMachineDesign childMd = facade.find(childId);
        ItemDomainMachineDesign newParentMdId = facade.find(newParentId);

        UserInfo currentUser = verifyCurrentUserPermissionForItem(childMd);
        verifyCurrentUserPermissionForItem(newParentMdId);

        ItemDomainMachineDesignBaseControllerUtility itemControllerUtility = childMd.getItemControllerUtility();
        ItemElement machineElement = itemControllerUtility.performMachineMoveWithUpdate(newParentMdId, childMd, currentUser);

        return machineElement;
    }

    @PUT
    @Path("/createControlElement")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create machine control top level item.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainMachineDesign createControlElement(
            @RequestBody(required = true) NewMachinePlaceholderOptions controlElementOptions) throws InvalidArgument, CdbException {

        ItemDomainMachineDesignControlControllerUtility utility = new ItemDomainMachineDesignControlControllerUtility();
        UserInfo sessionUser = getCurrentRequestUserInfo();

        ItemDomainMachineDesign newMachine = utility.createEntityInstance(sessionUser);
        assignDataToMachineElement(newMachine, controlElementOptions);

        newMachine = utility.create(newMachine, sessionUser);

        return newMachine;
    }

    @PUT
    @Path("/createControlRelationship")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create machine control relationship.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemElementRelationship createControlRelationship(
            @RequestBody(required = true) NewControlRelationshipInformation controlRelationshipInformation) throws InvalidArgument, CdbException {
        ItemDomainMachineDesignControlControllerUtility utility = new ItemDomainMachineDesignControlControllerUtility();

        int controllingMachineId = controlRelationshipInformation.getControllingMachineId();
        int controlledMachineId = controlRelationshipInformation.getControlledMachineId();

        ItemDomainMachineDesign controllingMachineElement = facade.find(controllingMachineId);
        if (controllingMachineElement == null) {
            throw new InvalidArgument("Invalid controlling machine id entered.");
        }
        UserInfo currentRequestUserInfo = verifyCurrentUserPermissionForItem(controllingMachineElement);
        ItemDomainMachineDesign controlledMachineElement = facade.find(controlledMachineId);

        if (controlledMachineElement == null) {
            throw new InvalidArgument("Invalid controlled machine id entered.");
        }

        Integer linkedParentMachineRelationshipId = controlRelationshipInformation.getLinkedParentMachineRelationshipId();

        String controlInterfaceToParent = controlRelationshipInformation.getControlInterfaceToParent();

        ItemElementRelationship relationship = utility.applyRelationship(controlledMachineElement, controllingMachineElement, linkedParentMachineRelationshipId, controlInterfaceToParent, currentRequestUserInfo);

        ItemDomainMachineDesign updatedItem = utility.update(controllingMachineElement, currentRequestUserInfo);

        List<ItemElementRelationship> relationshipList = updatedItem.getItemElementRelationshipList1();
        for (ItemElementRelationship ier : relationshipList) {
            Item firstItem = ier.getFirstItem();

            if (firstItem.equals(controlledMachineElement)) {
                if (linkedParentMachineRelationshipId != null) {
                    ItemElementRelationship rfp = ier.getRelationshipForParent();
                    if (rfp == null) {
                        // Should not be possible because this would mean a global and non-global relationship was created. 
                        continue;
                    }
                    if (!Objects.equals(rfp.getId(), linkedParentMachineRelationshipId)) {
                        continue;
                    }
                }
                relationship = ier;
                break;
            }

        }

        return relationship;
    }

    @GET
    @Path("/controlRelationshipHierarchyForMachine/{machineId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ControlRelationshipHierarchy> getControlHierarchyForMachineElement(@PathParam("machineId") int machineId) throws ObjectNotFound {
        ItemDomainMachineDesign machine = getMachineDesignItemById(machineId);
        ItemDomainMachineDesignControlControllerUtility utility = new ItemDomainMachineDesignControlControllerUtility();

        List<ControlRelationshipHierarchy> controlHierarchyList = new ArrayList<>();
        createControlHierarchyList(controlHierarchyList, utility, machine);

        controlHierarchyList.sort((o1, o2) -> {
            ItemDomainMachineDesign machineItem = o1.getMachineItem();
            ItemDomainMachineDesign machineItem1 = o2.getMachineItem();

            return machineItem.getId() - machineItem1.getId();
        });

        return controlHierarchyList;
    }

    private void createControlHierarchyList(List<ControlRelationshipHierarchy> controlHierarchyList, ItemDomainMachineDesignControlControllerUtility utility, ItemDomainMachineDesign machine) {
        ControlRelationshipHierarchy activeRelationshipHierarchy = null;

        List<ItemDomainMachineDesign> controlChildItems = utility.getControlChildItems(machine);

        if (controlChildItems.size() == 1) {
            ItemDomainMachineDesign child = controlChildItems.get(0);
            PropertyValue interfaceToParent = utility.getControlInterfaceToParentForItem(child, machine);
            activeRelationshipHierarchy = new ControlRelationshipHierarchy(child, interfaceToParent);
        }

        createControlHierarchyList(controlHierarchyList, utility, machine, activeRelationshipHierarchy);
    }

    private void createControlHierarchyList(List<ControlRelationshipHierarchy> controlHierarchyList, ItemDomainMachineDesignControlControllerUtility utility, ItemDomainMachineDesign machine, ControlRelationshipHierarchy activeRelationshipHierarchy) {
        List<ItemDomainMachineDesign> controlParentItems = utility.getControlParentItems(machine);
        ControlRelationshipHierarchy newRelationshipHierarchy = null;

        if (controlParentItems.size() == 0) {
            if (activeRelationshipHierarchy != null) {
                activeRelationshipHierarchy = new ControlRelationshipHierarchy(activeRelationshipHierarchy, machine, null);
                controlHierarchyList.add(activeRelationshipHierarchy);
            }
        } else {
            for (ItemDomainMachineDesign parentItem : controlParentItems) {
                PropertyValue interfaceToParent = utility.getControlInterfaceToParentForItem(machine, parentItem);

                if (activeRelationshipHierarchy == null) {
                    // Leaf most node
                    newRelationshipHierarchy = new ControlRelationshipHierarchy(machine, interfaceToParent);
                } else {
                    if (newRelationshipHierarchy != null) {
                        // Copy over active for a new branch. 
                        activeRelationshipHierarchy = activeRelationshipHierarchy.thisOnlyClone();
                    }
                    newRelationshipHierarchy = new ControlRelationshipHierarchy(activeRelationshipHierarchy, machine, interfaceToParent);
                }

                createControlHierarchyList(controlHierarchyList, utility, parentItem, newRelationshipHierarchy);
            }
        }
    }

    @GET
    @Path("/ConnectorListForMachine/{machineId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MachineDesignConnectorListObject> getMachineDesignConnectorList(@PathParam("machineId") int machineId) throws ObjectNotFound {
        ItemDomainMachineDesign machine = getMachineDesignItemById(machineId);
        List<MachineDesignConnectorListObject> connList = MachineDesignConnectorListObject.createMachineDesignConnectorList(machine);

        return connList;
    }
}
