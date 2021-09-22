/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.AuthorizationError;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignBaseControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControlControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemProjectFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainMachineDesignQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.authentication.Secured;
import gov.anl.aps.cdb.rest.entities.ItemDomainMdSearchResult;
import gov.anl.aps.cdb.rest.entities.ItemDomainMachineDesignIdListRequest;
import gov.anl.aps.cdb.rest.entities.NewControlRelationshipInformation;
import gov.anl.aps.cdb.rest.entities.NewMachinePlaceholderOptions;
import gov.anl.aps.cdb.rest.entities.UpdateMachineAssignedItemInformation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainMachineDesign> getMachineDesignItemList() {
        LOGGER.debug("Fetching machine design list");
        return facade.findAll();
    }

    @GET
    @Path("/ById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDomainMachineDesign getMachineDesignItemById(@PathParam("id") int id) throws ObjectNotFound {
        LOGGER.debug("Fetching item with id: " + id);
        ItemDomainMachineDesign item = facade.find(id);
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
    public List<ItemDomainMachineDesign> getMachineDesignItemsByName(@PathParam("name") String name) throws ObjectNotFound {
        LOGGER.debug("Fetching items with name: " + name);
        List<ItemDomainMachineDesign> itemList = facade.findByName(name);
        if (itemList == null || itemList.isEmpty()) {
            ObjectNotFound ex = new ObjectNotFound("Could not find item with name: " + name);
            LOGGER.error(ex);
            throw ex;
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

    private List<ItemDomainMachineDesign> itemsWithContainerHierarchy(String rootItemName, String containerItemName, String itemName) {
        List<ItemDomainMachineDesign> resultList = new ArrayList<>();
        List<ItemDomainMachineDesign> itemList = facade.findByName(itemName);
        if (itemList.isEmpty()) {
            itemList = facade.findByAlternateName(itemName);
        }
        for (ItemDomainMachineDesign item : itemList) {

            // walk up hierarchy to top-level "root" parent
            ItemDomainMachineDesign parentItem = item.getParentMachineDesign();
            boolean foundContainer = false;
            boolean foundRoot = false;
            while (parentItem != null) {

                // check container match
                String alternateName = parentItem.getAlternateName();
                if (alternateName == null) {
                    alternateName = "";
                }
                if ((parentItem.getName().equals(containerItemName))
                        || (alternateName.equals(containerItemName))) {
                    foundContainer = true;
                }

                // check root match
                if ((parentItem.getParentMachineDesign() == null)
                        && (parentItem.getName().equals(rootItemName))) {
                    foundRoot = true;
                }

                parentItem = parentItem.getParentMachineDesign();

                if (foundContainer && foundRoot) {
                    resultList.add(item);
                    break;
                }
            }
        }
        return resultList;
    }

    /**
     * Searches the top-level machine design hierarchy "root" node for children
     * with specified name.
     *
     * @param rootItemName
     * @param containerItemName
     * @param itemName
     * @return
     * @throws gov.anl.aps.cdb.common.exceptions.InvalidArgument
     */
    @GET
    @Path("/ByHierarchy/{root}/{container}/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainMachineDesign> getMdInHierarchyByName(
            @PathParam("root") String rootItemName,
            @PathParam("container") String containerItemName,
            @PathParam("name") String itemName) throws InvalidArgument {

        LOGGER.debug("Fetching item in hiearchy: " + rootItemName + " in container: " + containerItemName + " named: " + itemName);

        if ((rootItemName == null) || (rootItemName.isBlank())) {
            throw new InvalidArgument(("must specify root item name"));
        }

        if ((containerItemName == null) || (containerItemName.isBlank())) {
            throw new InvalidArgument(("must specify container item name"));
        }

        if ((itemName == null) || (itemName.isBlank())) {
            throw new InvalidArgument(("must specify item name"));
        }

        return itemsWithContainerHierarchy(rootItemName, containerItemName, itemName);
    }

    @POST
    @Path("/IdList")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Integer> getHierarchyIdList(@RequestBody(required = true) ItemDomainMachineDesignIdListRequest request) throws InvalidArgument {

        Instant start = Instant.now();

        List<String> itemNames = request.getItemNames();
        List<String> rackNames = request.getRackNames();
        String rootItemName = request.getRootName();

        if ((rootItemName == null) || (rootItemName.isBlank())) {
            throw new InvalidArgument("must specify root item name");
        }

        if (itemNames.size() != rackNames.size()) {
            throw new InvalidArgument("list sizes must match for item and rack names");
        }

        LOGGER.debug("Fetching list of machine item id's by name list size: "
                + itemNames.size());

        List<Integer> idList = new ArrayList<>(itemNames.size());
        for (int listIndex = 0; listIndex < itemNames.size(); listIndex++) {
            String itemName = itemNames.get(listIndex);
            String containerItemName = rackNames.get(listIndex);
            if (((itemName != null) && (!itemName.isBlank()))
                    && ((containerItemName != null) && (!containerItemName.isBlank()))) {

                List<ItemDomainMachineDesign> mdItems = itemsWithContainerHierarchy(
                        rootItemName, containerItemName, itemName);
                switch (mdItems.size()) {
                    case 1:
                        // one matching item
                        idList.add(mdItems.get(0).getId());
                        break;
                    case 0:
                        // no matching items
                        idList.add(0);
                        break;
                    default:
                        // multiple matching items
                        idList.add(-1);
                        break;
                }
            } else {
                idList.add(0);
            }
        }

        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);
        LOGGER.debug("Duration: " + elapsed.toSeconds());

        return idList;
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

        mdItem.setAssignedItem(assignedItem);
        if (isInstalled != null && assignedItem instanceof ItemDomainInventory) {
            mdItem.setIsHoused(isInstalled);
        }
      
        ItemControllerUtility itemControllerUtility = mdItem.getItemControllerUtility();

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
        ItemElement machineElement = itemControllerUtility.performMachineMove(newParentMdId, childMd, currentUser);

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
        
        String controlInterfaceToParent = controlRelationshipInformation.getControlInterfaceToParent();
        
        ItemElementRelationship relationship = utility.applyRelationship(controlledMachineElement, controllingMachineElement, controlInterfaceToParent, currentRequestUserInfo); 
        
        utility.update(controllingMachineElement, currentRequestUserInfo);
        
        return relationship;        
    }        
}
