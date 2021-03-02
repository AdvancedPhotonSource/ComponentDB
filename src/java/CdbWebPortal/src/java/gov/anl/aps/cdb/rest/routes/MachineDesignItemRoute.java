/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.authentication.Secured;
import gov.anl.aps.cdb.rest.entities.ItemDomainMdSearchResult;
import gov.anl.aps.cdb.rest.entities.ItemDomanMdHierarchySearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

    private List<ItemDomainMachineDesign> itemsWithContainerHierarchy(String rootItemName, String containerItemName, String itemName) {
        List<ItemDomainMachineDesign> itemList = facade.findByName(itemName);
        List<ItemDomainMachineDesign> resultList = new ArrayList<>();
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
                if ((parentItem.getName().equals(containerItemName)) ||
                        (alternateName.equals(containerItemName))) { 
                    foundContainer = true;
                }
                
                // check root match
                if ((parentItem.getParentMachineDesign() == null) && 
                        (parentItem.getName().equals(rootItemName))) {
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
     * @throws ObjectNotFound
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
    @Path("/ByHierarchy")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Integer> getMdInHierarchyIdList(ItemDomanMdHierarchySearchRequest request) throws ObjectNotFound, InvalidArgument {
        
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
                if (mdItems.size() == 1) {
                    // one matching item
                    idList.add(mdItems.get(0).getId());
                } else if (mdItems.size() == 0) {
                    // no matching items
                    idList.add(0);
                } else {
                    // multiple matching items
                    idList.add(-1);
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
    @Path("/UpdateAssignedItem/{mdItemId}/{assignedItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update assigned item in a machine design item.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainMachineDesign updateAssignedItem(@PathParam("mdItemId") int mdItemId, @PathParam("assignedItemId") Integer assignedItemId) throws ObjectNotFound, CdbException {
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
        return updateAssignedItem(mdItemId, null); 
    }
}
