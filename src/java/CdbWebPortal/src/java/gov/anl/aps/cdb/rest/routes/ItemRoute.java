/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.AuthorizationError;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.DbError;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.InvalidRequest;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.utilities.CdbEntityControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.IItemStatusControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCatalogControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainInventoryControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainLocationControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.LocatableItemControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemCategoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemProjectFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeHandlerFacade;
import gov.anl.aps.cdb.portal.model.db.beans.UserGroupFacade;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableItem;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableStatusItem;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.model.jsf.beans.PropertyValueDocumentUploadBean;
import gov.anl.aps.cdb.portal.model.jsf.beans.PropertyValueImageUploadBean;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.portal.view.objects.LocationHistoryObject;
import gov.anl.aps.cdb.rest.authentication.Secured;
import gov.anl.aps.cdb.rest.entities.ConciseItem;
import gov.anl.aps.cdb.rest.entities.ConciseItemOptions;
import gov.anl.aps.cdb.rest.entities.FileUploadObject;
import gov.anl.aps.cdb.rest.entities.ItemDomainCatalogSearchResult;
import gov.anl.aps.cdb.rest.entities.ItemHierarchy;
import gov.anl.aps.cdb.rest.entities.ItemLocationInformation;
import gov.anl.aps.cdb.rest.entities.ItemMembership;
import gov.anl.aps.cdb.rest.entities.ItemPermissions;
import gov.anl.aps.cdb.rest.entities.ItemStatusBasicObject;
import gov.anl.aps.cdb.rest.entities.SimpleLocationInformation;
import gov.anl.aps.cdb.rest.entities.LogEntryEditInformation;
import gov.anl.aps.cdb.rest.entities.NewLocationInformation;
import gov.anl.aps.cdb.rest.entities.SearchEntitiesResults;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
@Path("/Items")
@Tag(name = "Item")
public class ItemRoute extends ItemBaseRoute {

    private static final Logger LOGGER = LogManager.getLogger(ItemRoute.class.getName());

    @EJB
    ItemElementFacade itemElementFacade;

    @EJB
    PropertyTypeFacade propertyTypeFacade;

    @EJB
    DomainFacade domainFacade;

    @EJB
    PropertyTypeHandlerFacade propertyTypeHandlerFacade;

    @EJB
    UserInfoFacade userInfoFacade;

    @EJB
    UserGroupFacade userGroupFacade;

    @EJB
    ItemProjectFacade itemProjectFacade;

    @EJB
    ItemTypeFacade itemTypeFacade;

    @EJB
    ItemCategoryFacade itemCategoryFacade;

    @GET
    @Path("/ById/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Fetch an item by its id.")
    public Item getItemById(@PathParam("itemId") int id) throws ObjectNotFound {
        LOGGER.debug("Fetching item by id: " + id);
        return getItemByIdBase(id);
    }

    @GET
    @Path("/ById/{itemId}/Hierarchy")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Fetch an item by its id.")
    public ItemHierarchy getItemHierarchyById(@PathParam("itemId") int id) throws ObjectNotFound {
        Item itemByIdBase = getItemByIdBase(id);

        ItemHierarchy hierarchy = new ItemHierarchy(itemByIdBase, true);

        return hierarchy;
    }

    @GET
    @Path("/HierarchyById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Fetch an item by its id.")
    @Deprecated
    public ItemHierarchy getItemHierarchyByIdDeprecated(@PathParam("id") int id) throws ObjectNotFound {
        return getItemHierarchyById(id);
    }

    @POST
    @Path("/UpdateParentItem/{elementId}/{parentItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update the contained item in a item hierarchy.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemHierarchy updateContainedItem(@PathParam("elementId") int elementId, @PathParam("parentItemId") Integer containedItemId) throws ObjectNotFound, CdbException {
        LOGGER.debug("Updating contained item for item element id: " + elementId);
        ItemElement find = itemElementFacade.find(elementId);

        Item parentItem = find.getParentItem();

        if (parentItem instanceof ItemDomainInventory == false) {
            throw new CdbException("Currently only inventory items are supported.");
        }

        Item newContainedItem = null;

        if (containedItemId != null) {
            newContainedItem = getItemByIdBase(containedItemId);
        }

        UserInfo updatedByUser = getCurrentRequestUserInfo();
        if (!verifyUserPermissionForItem(updatedByUser, parentItem)) {
            AuthorizationError ex = new AuthorizationError("User does not have permission to update property value for the item");
            LOGGER.error(ex);
            throw ex;
        }

        boolean found = false;
        List<ItemElement> itemElementDisplayList = parentItem.getItemElementDisplayList();
        for (ItemElement ie : itemElementDisplayList) {
            if (ie.getId() == elementId) {
                ie.setContainedItem(newContainedItem);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new CdbException("Unexpected error occurred. Could not reference element from its parent.");
        }

        ItemControllerUtility itemDomainController = parentItem.getItemControllerUtility();
        itemDomainController.update(parentItem, updatedByUser);

        return getItemHierarchyById(parentItem.getId());
    }

    @POST
    @Path("/ClearParentItem/{elementId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Clear the contained item in a item hierarchy.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemHierarchy clearContainedItem(@PathParam("elementId") int elementId) throws CdbException {
        return updateContainedItem(elementId, null);
    }

    public PropertyType getPropertyTypeByName(String name) throws ObjectNotFound {
        PropertyType pt = propertyTypeFacade.findByName(name);
        if (pt == null) {
            ObjectNotFound ex = new ObjectNotFound("Could not find property type with name: " + name);
            LOGGER.error(ex);
            throw ex;
        }
        return pt;
    }

    @GET
    @Path("/ById/{itemId}/ItemsDerivedFromItem")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Item> getItemsDerivedFromItemByItemId(@PathParam("itemId") int id) throws ObjectNotFound {
        LOGGER.debug("Fetching derived from item list for item id: " + id);
        Item itemById = getItemByIdBase(id);
        return itemById.getDerivedFromItemList();
    }

    @GET
    @Path("/ItemsDerivedFromItemByItemId/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public List<Item> getItemsDerivedFromItemByItemIdDeprecated(@PathParam("id") int id) throws ObjectNotFound {
        return getItemsDerivedFromItemByItemId(id);
    }

    @GET
    @Path("/ById/{itemId}/Status")
    @Produces(MediaType.APPLICATION_JSON)
    public PropertyValue getItemStatus(@PathParam("itemId") int id) throws ObjectNotFound {
        LOGGER.debug("Fetching status for item id: " + id);
        Item item = getItemByIdBase(id);

        if (item instanceof LocatableStatusItem) {
            return ((LocatableStatusItem) item).getInventoryStatusPropertyValue();
        }

        return null;
    }

    @GET
    @Path("/ById/{itemId}/Permissions")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemPermissions getItemPermissions(@PathParam("itemId") int id) throws ObjectNotFound {
        LOGGER.debug("Fetching permissions for item id: " + id);

        Item item = getItemByIdBase(id);

        return new ItemPermissions(item);
    }

    @GET
    @Path("/ById/{itemId}/EntityInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public EntityInfo getItemEntityInfo(@PathParam("itemId") int id) throws ObjectNotFound {
        Item itemById = getItemByIdBase(id);

        return itemById.getEntityInfo();
    }

    @GET
    @Path("/ById/{itemId}/Location")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemLocationInformation getItemLocation(@PathParam("itemId") int id) throws ObjectNotFound, InvalidArgument {
        LOGGER.debug("Fetching location for item with id: " + id);
        Item itemById = getItemByIdBase(id);

        if (itemById instanceof LocatableItem) {
            LocatableItem locatableItem = (LocatableItem) itemById;
            LocatableItemControllerUtility controller = new LocatableItemControllerUtility();

            controller.setItemLocationInfo(locatableItem);

            return new ItemLocationInformation(locatableItem);

        } else {
            InvalidArgument ex = new InvalidArgument("Item requested cannot have a location specified. Item Id: " + id);
            LOGGER.error(ex);
            throw ex;
        }
    }

    @GET
    @Path("/ById/{itemId}/LocationHistory")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LocationHistoryObject> getItemLocationHistory(@PathParam("itemId") int id) throws ObjectNotFound, InvalidArgument {
        LOGGER.debug("Fetching location history for item with id: " + id);
        Item itemById = getItemByIdBase(id);

        if (itemById instanceof LocatableItem) {
            LocatableItem locatableItem = (LocatableItem) itemById;
            LocatableItemControllerUtility controllerUtility = new LocatableItemControllerUtility();

            List<LocationHistoryObject> histories = controllerUtility.getLocationHistoryObjectList(locatableItem);

            // When trees are loaded than the ItemHierarchy nodes can be loaded. 
            for (LocationHistoryObject hist : histories) {
                controllerUtility.getLocationTreeForLocationHistoryObject(hist);
                controllerUtility.getHousingTreeForLocationHistoryObject(hist);
            }

            return histories;
        } else {
            InvalidArgument ex = new InvalidArgument("Item requested cannot have a location specified. Item Id: " + id);
            LOGGER.error(ex);
            throw ex;
        }
    }

    @GET
    @Path("/ById/{itemId}/Memberships")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemMembership> getItemMemberships(@PathParam("itemId") int id) throws ObjectNotFound {
        LOGGER.debug("Fetching memberships for item with id: " + id);
        Item itemById = getItemByIdBase(id);
        
        ItemControllerUtility itemControllerUtility = itemById.getItemControllerUtility();
        
        List<Item> parentItemList = itemControllerUtility.getParentItemList(itemById); 
        
        List<ItemMembership> itemMemberships = new ArrayList<>();
        for (Item parentItem : parentItemList) {
            ItemMembership itemMembership = new ItemMembership(parentItem);
            itemMemberships.add(itemMembership);
        }

        return itemMemberships;
    }

    @GET
    @Path("/ById/{itemId}/VerifyPermission")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public boolean verifyUserPermissionForItem(@PathParam("itemId") int id) throws ObjectNotFound {
        LOGGER.debug("Performing permission verification for item: " + id);
        Item itemById = getItemByIdBase(id);
        if (itemById != null) {
            UserInfo user = getCurrentRequestUserInfo();
            LOGGER.debug("Continuing permission verification for user: " + user.getUsername());
            return verifyUserPermissionForItem(user, itemById);
        }
        return false;
    }

    @GET
    @Path("/ById/{itemId}/Permission")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    @Deprecated
    public boolean verifyUserPermissionForItemDeprecated(@PathParam("itemId") int id) throws ObjectNotFound {
        return verifyUserPermissionForItem(id);
    }

    @POST
    @Path("/UpdateLocation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemLocationInformation updateItemLocation(@RequestBody(required = true) SimpleLocationInformation locationInformation) throws ObjectNotFound, InvalidArgument, CdbException {
        LOGGER.debug("Updating location for item with id: " + locationInformation.getLocatableItemId());
        // Validate ids provided 
        Item item = getItemByIdBase(locationInformation.getLocatableItemId());
        if (item instanceof LocatableItem == false) {
            InvalidArgument invalidArgument = new InvalidArgument("Locatable item id is not a locatable item");
            LOGGER.error(invalidArgument);
            throw invalidArgument;
        }
        LocatableItem locatableItem = (LocatableItem) item;

        Integer locationItemId = locationInformation.getLocationItemId();
        ItemDomainLocation locationItem = null;

        if (locationItemId != null) {
            Item locItem = getItemByIdBase(locationItemId);
            if (locItem instanceof ItemDomainLocation == false) {
                InvalidArgument invalidArgument = new InvalidArgument("Location item id is not a location.");
                LOGGER.error(invalidArgument);
                throw invalidArgument;
            }
            locationItem = (ItemDomainLocation) locItem;
        }

        String locationDetails = locationInformation.getLocationDetails();
        if (locationDetails != null && locationDetails.length() > 64) {
            InvalidArgument invalidArgument = new InvalidArgument("Location details length exceeded 64 characters.");
            LOGGER.error(invalidArgument);
            throw invalidArgument;
        }

        // Load current location info
        LocatableItemControllerUtility locationControllerUtility = new LocatableItemControllerUtility();
        locationControllerUtility.setItemLocationInfo(locatableItem);

        // Use the location information provided 
        locatableItem.setLocation(locationItem);
        locatableItem.setLocationDetails(locationDetails);

        // Perfrom update        
        UserInfo currentUser = verifyCurrentUserPermissionForItem(locatableItem);
        ItemControllerUtility controller = locatableItem.getItemControllerUtility();
        controller.update(locatableItem, currentUser);

        // Get the latest item after update to respond
        locatableItem = (LocatableItem) getItemByIdBase(locatableItem.getId());
        locationControllerUtility.setItemLocationInfo(locatableItem);

        return new ItemLocationInformation(locatableItem);
    }

    @POST
    @Path("/UpdatePermissions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update permission, please provide a full permission object or just set the attribute that needs to be updated in the permission object.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemPermissions updateItemPermission(@RequestBody(required = true) ItemPermissions permissions) throws ObjectNotFound, CdbException {
        Integer itemId = permissions.getItemId();
        LOGGER.debug("Updating permissions for item with id: " + itemId);

        Item dbItem = getItemByIdBase(itemId);
        UserInfo currentUser = verifyCurrentUserPermissionForItem(dbItem);

        UserGroup ownerGroup = permissions.getOwnerGroup();
        UserInfo ownerUser = permissions.getOwnerUser();
        Boolean groupWriteable = permissions.getGroupWriteable();

        if (ownerGroup != null) {
            Integer id = ownerGroup.getId();
            ownerGroup = userGroupFacade.findById(id);
        }
        if (ownerUser != null) {
            Integer id = ownerUser.getId();
            ownerUser = userInfoFacade.findById(id);
        }

        if (ownerGroup == null && ownerUser == null && groupWriteable == null) {
            throw new InvalidArgument("Please provide a valid id for either owner user,owner group, group writeable to update permissions.");
        }

        if (ownerGroup != null) {
            dbItem.setOwnerUserGroup(ownerGroup);
        }
        if (ownerUser != null) {
            dbItem.setOwnerUser(ownerUser);
        }
        if (groupWriteable != null) {
            dbItem.getEntityInfo().setIsGroupWriteable(groupWriteable);
        }

        ItemControllerUtility itemDomainControllerForApi = dbItem.getItemControllerUtility();

        itemDomainControllerForApi.update(dbItem, currentUser);

        return getItemPermissions(dbItem.getId());
    }

    @POST
    @Path("/UpdateDetails")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public Item updateItemDetails(@RequestBody(required = true) Item item) throws ObjectNotFound, CdbException {
        LOGGER.debug("Updating details for item with id: " + item.getId());

        int itemId = item.getId();
        Item dbItem = getItemByIdBase(itemId);
        UserInfo currentUser = verifyCurrentUserPermissionForItem(dbItem);

        dbItem.setName(item.getName());
        dbItem.setQrId(item.getQrId());
        dbItem.setItemIdentifier1(item.getItemIdentifier1());
        dbItem.setItemIdentifier2(item.getItemIdentifier2());
        dbItem.setDescription(item.getDescriptionFromAPI());

        Domain domain = dbItem.getDomain();

        List<ItemType> itemTypeList = dbItem.getItemTypeList();

        if (item.getItemTypeList() != null) {
            itemTypeList.clear();

            for (ItemType type : item.getItemTypeList()) {
                ItemType dbType = itemTypeFacade.find(type.getId());

                if (dbType != null) {
                    if (dbType.getDomain().equals(domain)) {
                        itemTypeList.add(dbType);
                    } else {
                        throw new InvalidArgument("Invalid item type provided: " + type.toString());
                    }
                } else {
                    throw new InvalidArgument("Invalid item type provided: " + type.toString());
                }
            }
        }
        dbItem.setItemTypeList(itemTypeList);

        List<ItemCategory> itemCategoryList = dbItem.getItemCategoryList();
        if (item.getItemCategoryList() != null) {
            itemCategoryList.clear();

            for (ItemCategory category : item.getItemCategoryList()) {
                ItemCategory dbCategory = itemCategoryFacade.find(category.getId());

                if (dbCategory != null) {
                    if (dbCategory.getDomain().equals(domain)) {
                        itemCategoryList.add(dbCategory);
                    } else {
                        throw new InvalidArgument("Invalid item category provided: " + category.toString());
                    }
                } else {
                    throw new InvalidArgument("Invalid item category provided: " + category.toString());
                }
            }
        }

        dbItem.setItemCategoryList(itemCategoryList);

        ItemControllerUtility itemDomainControllerForApi = dbItem.getItemControllerUtility();

        itemDomainControllerForApi.update(dbItem, currentUser);

        return dbItem;
    }

    private void propertyValueInternalCheck(PropertyValue dbPropertyValue) throws InvalidRequest {
        PropertyType propertyType = dbPropertyValue.getPropertyType();
        if (propertyType.getIsInternal()) {
            throw new InvalidRequest("Property type is classified as internal. Could only be updated using specialized functionality.");
        }
    }

    @POST
    @Path("/UpdateStatus/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public PropertyValue updateItemStatus(@PathParam("itemId") int itemId, @RequestBody(required = true) ItemStatusBasicObject status) throws InvalidArgument, ObjectNotFound, CdbException {
        Item itemById = getItemByIdBase(itemId);

        UserInfo currentUser = verifyCurrentUserPermissionForItem(itemById);

        ItemControllerUtility ic = itemById.getItemControllerUtility();
        IItemStatusControllerUtility controller = null;
        LocatableStatusItem item = null;

        if (ic instanceof IItemStatusControllerUtility) {
            controller = (IItemStatusControllerUtility) ic;
            item = (LocatableStatusItem) itemById;
        } else {
            throw new InvalidArgument("The item id provided is not of type with a status");
        }

        controller.prepareEditInventoryStatus(item, currentUser);

        item.setInventoryStatusValue(status.getStatus());

        // Verify if user provided a valid allowed value.
        PropertyValue inventoryStatusPropertyValue = item.getInventoryStatusPropertyValue();
        if (PropertyValueUtility.verifyValidValueForPropertyValue(inventoryStatusPropertyValue) == false) {
            throw new InvalidArgument("The value: '" + status.getStatus() + "' is not valid for inventory status.");
        }

        inventoryStatusPropertyValue.setEffectiveFromDateTime(status.getEffectiveFromDate());

        ic.update(item, currentUser);
        return item.getInventoryStatusPropertyValue();
    }

    private void updateDbPropertyValueWithPassedInDate(PropertyValue dbPropertyValue, PropertyValue userPassedValue) {
        dbPropertyValue.setValue(userPassedValue.getValue());
        dbPropertyValue.setTargetValue(userPassedValue.getTargetValue());
        dbPropertyValue.setDisplayValue(userPassedValue.getDisplayValue());
        dbPropertyValue.setTag(userPassedValue.getTag());
        dbPropertyValue.setDescription(userPassedValue.getDescription());
        dbPropertyValue.setUnits(userPassedValue.getUnits());
        dbPropertyValue.setIsDynamic(userPassedValue.getIsDynamic());
        dbPropertyValue.setIsUserWriteable(userPassedValue.getIsUserWriteable());
        dbPropertyValue.setEffectiveFromDateTime(userPassedValue.getEffectiveFromDateTime());
    }

    private void propertyValueAllowedValueCheck(PropertyValue dbPropertyValue) throws InvalidArgument {
        if (PropertyValueUtility.verifyValidValueForPropertyValue(dbPropertyValue) == false) {
            throw new InvalidArgument("The value: '" + dbPropertyValue.getValue() + "' is not allowed for the property value.");
        }
    }

    @DELETE
    @Path("/DeleteById/{itemId}")
    @Operation(summary = "Delete an item by its id.")
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public void deleteItemById(@PathParam("itemId") int itemId) throws ObjectNotFound, ObjectNotFound, AuthorizationError, CdbException {
        LOGGER.debug("Deleting item with id: " + itemId);

        Item dbItem = getItemByIdBase(itemId);
        UserInfo updatedByUser = getCurrentRequestUserInfo();

        if (!verifyUserPermissionForItem(updatedByUser, dbItem)) {
            AuthorizationError ex = new AuthorizationError("User does not have permission to delete item");
            LOGGER.error(ex);
            throw ex;
        }

        ItemControllerUtility itemController = dbItem.getItemControllerUtility();
        itemController.destroy(dbItem, updatedByUser);
    }

    @POST
    @Path("/UpdateProperty/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public PropertyValue updateItemPropertyValue(@PathParam("itemId") int itemId, @RequestBody(required = true) PropertyValue propertyValue) throws InvalidArgument, ObjectNotFound, CdbException {
        LOGGER.debug("Updating details for item with id: " + itemId);
        Item dbItem = getItemByIdBase(itemId);
        UserInfo updatedByUser = getCurrentRequestUserInfo();

        if (!verifyUserPermissionForItem(updatedByUser, dbItem)) {
            AuthorizationError ex = new AuthorizationError("User does not have permission to update property value for the item");
            LOGGER.error(ex);
            throw ex;
        }

        ItemControllerUtility ItemControllerUtility = dbItem.getItemControllerUtility();
        PropertyValue dbPropertyValue = null;

        int propIdx = -1;
        if (propertyValue.getId() == null) {
            InvalidArgument invalidArgument = new InvalidArgument("Property value id must be present.");
            LOGGER.error(invalidArgument);
            throw invalidArgument;
        } else {
            //find the property of this item with same property value id as in propertyValue.
            for (int i = 0; i < dbItem.getPropertyValueList().size(); i++) {
                PropertyValue propertyValueIttr = dbItem.getPropertyValueList().get(i);
                if (propertyValueIttr.getId().equals(propertyValue.getId())) {
                    dbPropertyValue = propertyValueIttr;
                    propIdx = i;
                    LOGGER.debug("Property found, type name = " + dbPropertyValue.getPropertyType().getName());
                    break;
                }
            }
        }

        if (dbPropertyValue == null) {
            ObjectNotFound objectNotFound = new ObjectNotFound("Property value could not be found.");
            LOGGER.error(objectNotFound);
            throw objectNotFound;
        }

        propertyValueInternalCheck(dbPropertyValue);

        // Set passed in property value to match db property value 
        updateDbPropertyValueWithPassedInDate(dbPropertyValue, propertyValue);

        propertyValueAllowedValueCheck(dbPropertyValue);

        ItemControllerUtility.update(dbItem, updatedByUser);

        dbItem = (Item) ItemControllerUtility.findById(itemId);

        List<PropertyValue> pvList = dbItem.getPropertyValueList();
        if (propIdx >= 0) {
            dbPropertyValue = pvList.get(propIdx);
        }

        return dbPropertyValue;
    }

    @POST
    @Path("/UpdatePropertyMetadata/{itemId}/{propertyValueId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public PropertyValue updateItemPropertyMetadata(@PathParam("itemId") int itemId, @PathParam("propertyValueId") int propertyValueId, @RequestBody(required = true) PropertyMetadata propertyMetadata) throws InvalidArgument, ObjectNotFound, CdbException {
        LOGGER.debug("Updating property metadata for item with id: " + itemId);
        Item dbItem = getItemByIdBase(itemId);
        UserInfo updatedByUser = getCurrentRequestUserInfo();

        if (!verifyUserPermissionForItem(updatedByUser, dbItem)) {
            AuthorizationError ex = new AuthorizationError("User does not have permission to upload property metadata for the item property");
            LOGGER.error(ex);
            throw ex;
        }

        ItemControllerUtility itemControllerUtility = dbItem.getItemControllerUtility();
        PropertyValue dbPropertyValue = null;

        int propIdx = -1;

        //find the property value of this item with same property value id name as in propertyValueId.
        for (int i = 0; i < dbItem.getPropertyValueList().size(); i++) {
            PropertyValue propertyValueIttr = dbItem.getPropertyValueList().get(i);
            if (propertyValueIttr.getId().equals(propertyValueId)) {
                dbPropertyValue = propertyValueIttr;
                propIdx = i;
                LOGGER.debug("property_id = " + dbPropertyValue.getId());
                break;
            }
        }

        if (dbPropertyValue == null) {
            ObjectNotFound objectNotFound = new ObjectNotFound("Property value id must be correct.");
            LOGGER.error(objectNotFound);
            throw objectNotFound;
        }

        propertyValueInternalCheck(dbPropertyValue);

        // Set passed in property value to match db property value 
        dbPropertyValue.setPropertyMetadataValue(propertyMetadata.getMetadataKey(), propertyMetadata.getMetadataValue());

        propertyValueAllowedValueCheck(dbPropertyValue);

        itemControllerUtility.update(dbItem, updatedByUser);

        dbItem = (Item) itemControllerUtility.findById(itemId);

        List<PropertyValue> pvList = dbItem.getPropertyValueList();
        if (propIdx >= 0) {
            dbPropertyValue = pvList.get(propIdx);
        }

        return dbPropertyValue;
    }

    @POST
    @Path("/AddProperty/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public PropertyValue addItemPropertyValue(@PathParam("itemId") int itemId,
            @RequestBody(required = true) PropertyValue propertyValue) throws InvalidArgument, ObjectNotFound, CdbException {
        LOGGER.debug("Adding property to item with id: " + itemId);
        Item dbItem = getItemByIdBase(itemId);
        UserInfo updatedByUser = getCurrentRequestUserInfo();

        if (!verifyUserPermissionForItem(updatedByUser, dbItem)) {
            AuthorizationError ex = new AuthorizationError("User does not have permission to add property value for the item");
            LOGGER.error(ex);
            throw ex;
        }

        ItemControllerUtility itemControllerUtility = dbItem.getItemControllerUtility();
        PropertyValue dbPropertyValue = null;

        PropertyType propertyType = propertyValue.getPropertyType();
        //Find property type object by name
        propertyType = getPropertyTypeByName(propertyType.getName());
        if (propertyType == null) {
            InvalidArgument invalidArgument = new InvalidArgument("Property type name must be correct or give property type id");
            LOGGER.error(invalidArgument);
            throw invalidArgument;
        }

        propertyValue.setPropertyType(propertyType);

        propertyValueInternalCheck(propertyValue);

        LOGGER.debug("Creating new property.");
        dbPropertyValue = itemControllerUtility.preparePropertyTypeValueAdd(dbItem, propertyType, null, null, updatedByUser);

        // Set passed in property value to match db property value 
        updateDbPropertyValueWithPassedInDate(dbPropertyValue, propertyValue);

        itemControllerUtility.update(dbItem, updatedByUser);

        dbItem = (Item) itemControllerUtility.findById(itemId);

        List<PropertyValue> pvList = dbItem.getPropertyValueList();
        int propIdx = pvList.size() - 1;
        dbPropertyValue = pvList.get(propIdx);

        return dbPropertyValue;
    }

    @POST
    @Path("/addLogEntry")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public Log addLogEntryToItem(@RequestBody(required = true) LogEntryEditInformation logEntryEditInformation) throws ObjectNotFound, CdbException {
        LOGGER.debug("Adding log for item with id: " + logEntryEditInformation.getItemId());
        int itemId = logEntryEditInformation.getItemId();
        Item itemById = getItemByIdBase(itemId);

        ItemControllerUtility controllerUtility = itemById.getItemControllerUtility();

        UserInfo updateUser = verifyCurrentUserPermissionForItem(itemById);
        Log newLog = controllerUtility.prepareAddLog(itemById, updateUser);

        newLog.setText(logEntryEditInformation.getLogEntry());
        if (logEntryEditInformation.getEffectiveDate() != null) {
            newLog.setEffectiveFromDateTime(logEntryEditInformation.getEffectiveDate());
        }

        controllerUtility.update(itemById, updateUser);

        return newLog;
    }

    @POST
    @Path("/uploadDocument/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public PropertyValue uploadDocumentForItem(@PathParam("itemId") int itemId, @RequestBody(required = true) FileUploadObject documentUpload) throws AuthorizationError, DbError, IOException, ObjectNotFound, CdbException {
        return uploadForItem(itemId, documentUpload, 1);
    }

    @POST
    @Path("/uploadImage/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public PropertyValue uploadImageForItem(@PathParam("itemId") int itemId, @RequestBody(required = true) FileUploadObject imageUpload) throws AuthorizationError, DbError, IOException, ObjectNotFound, CdbException {
        return uploadForItem(itemId, imageUpload, 0);
    }

    /**
     * Generic item upload function
     *
     * @param itemId
     * @param fileUpload
     * @param mode- 0: image, 1: document
     * @return
     * @throws AuthorizationError
     * @throws DbError
     * @throws IOException
     * @throws ObjectNotFound
     * @throws CdbException
     */
    private PropertyValue uploadForItem(@PathParam("itemId") int itemId, FileUploadObject fileUpload, int mode) throws AuthorizationError, DbError, IOException, ObjectNotFound, CdbException {
        String modeString = null;
        if (mode == 0) {
            modeString = "image";
        } else {
            modeString = "document";
        }

        LOGGER.debug("Uploading " + modeString + " for item: " + itemId);
        Item dbItem = getItemByIdBase(itemId);
        UserInfo updatedByUser = getCurrentRequestUserInfo();

        if (!verifyUserPermissionForItem(updatedByUser, dbItem)) {
            AuthorizationError ex = new AuthorizationError("User does not have permission to upload " + modeString + " for the item");
            LOGGER.error(ex);
            throw ex;
        }

        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(fileUpload.getBase64Binary());
        ByteArrayInputStream stream = new ByteArrayInputStream(decode);

        ItemControllerUtility itemControllerUtility = dbItem.getItemControllerUtility();
        PropertyType uploadPropertyType = null;

        if (mode == 0) {
            uploadPropertyType = PropertyValueImageUploadBean.getImagePropertyType(propertyTypeHandlerFacade);
        } else {
            uploadPropertyType = PropertyValueDocumentUploadBean.getDocumentPropertyType(propertyTypeHandlerFacade);
        }

        if (uploadPropertyType == null) {
            DbError dbError = new DbError("Could not find image property type.");
            LOGGER.error(dbError);
            throw dbError;
        }

        PropertyValue pv = itemControllerUtility.preparePropertyTypeValueAdd(dbItem, uploadPropertyType, null, null, updatedByUser);
        String fileName = fileUpload.getFileName();

        try {
            if (mode == 0) {
                PropertyValueImageUploadBean.uploadImage(pv, fileName, stream);
            } else {
                PropertyValueDocumentUploadBean.uploadDocument(pv, fileName, stream);
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw ex;
        }

        itemControllerUtility.update(dbItem, updatedByUser);

        List<PropertyValue> pvList = dbItem.getPropertyValueList();
        int lastIdx = pvList.size() - 1;
        pv = pvList.get(lastIdx);

        return pv;
    }

    @GET
    @Path("/ByQrId/{qrId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Item getItemByQrId(@PathParam("qrId") int qrId) throws ObjectNotFound {
        LOGGER.debug("Fetching item by qrId: " + qrId);
        Item findByQrId = itemFacade.findByQrId(qrId);
        if (findByQrId == null) {
            ObjectNotFound objectNotFound = new ObjectNotFound("Could not find item with qrid: " + qrId);
            LOGGER.error(objectNotFound);
            throw objectNotFound;
        }
        return findByQrId;
    }

    @GET
    @Path("/ById/{itemId}/Properties")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PropertyValue> getPropertiesForItem(@PathParam("itemId") int itemId) throws ObjectNotFound {
        LOGGER.debug("Fetching properties for item by id: " + itemId);
        Item itemById = getItemByIdBase(itemId);
        return itemById.getPropertyValueList();
    }

    @GET
    @Path("/ById/{itemId}/Logs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Log> getLogsForItem(@PathParam("itemId") int itemId) throws ObjectNotFound {
        LOGGER.debug("Fetching logs for item by id: " + itemId);
        Item itemById = getItemByIdBase(itemId);
        return itemById.getLogList();
    }

    @GET
    @Path("/ById/{itemId}/ImageProperties")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PropertyValue> getImagePropertiesForItem(@PathParam("itemId") int itemId) throws ObjectNotFound {
        LOGGER.debug("Fetching image properties for item by id: " + itemId);
        Item itemById = getItemByIdBase(itemId);
        List<PropertyValue> propertyValueList = itemById.getPropertyValueList();
        return PropertyValueUtility.prepareImagePropertyValueList(propertyValueList);
    }

    @GET
    @Path("/PropertiesForItem/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public List<PropertyValue> getPropertiesForItemDeprecated(@PathParam("itemId") int itemId) throws ObjectNotFound {
        return getPropertiesForItem(itemId);
    }

    @GET
    @Path("/LogsForItem/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public List<Log> getLogsForItemDeprecated(@PathParam("itemId") int itemId) throws ObjectNotFound {
        return getLogsForItem(itemId);
    }

    @GET
    @Path("/ImagePropertiesForItem/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public List<PropertyValue> getImagePropertiesForItemDeprecated(@PathParam("itemId") int itemId) throws ObjectNotFound {
        return getImagePropertiesForItem(itemId);
    }

    @GET
    @Path("/ByDomain/{domainName}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Item> getItemsByDomain(@PathParam("domainName") String domainName) {
        LOGGER.debug("Fetch items for domain: " + domainName);
        return itemFacade.findByDomain(domainName);
    }

    @POST
    @Path("/ByDomain/{domainName}/Concise")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<ConciseItem> getConciseItemsByDomain(@PathParam("domainName") String domainName, ConciseItemOptions options) {
        LOGGER.debug("Fetch concise items for domain: " + domainName);
        List<Item> itemList = itemFacade.findByDomain(domainName);
        return ConciseItem.createList(itemList, options);
    }

    @GET
    @Path("/Catalog")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainCatalog> getCatalogItems() {
        return (List<ItemDomainCatalog>) (List<?>) getItemsByDomain(ItemDomainName.catalog.getValue());
    }

    @POST
    @Path("/Catalog/Concise")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<ConciseItem> getConciseCatalogItems(ConciseItemOptions options) {
        List<Item> catalogItems = getItemsByDomain(ItemDomainName.catalog.getValue());
        return ConciseItem.createList(catalogItems, options);
    }

    @GET
    @Path("/Catalog/Favorites")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public List<ItemDomainCatalog> getFavoriteCatalogItems() {
        ItemDomainCatalogControllerUtility controller = new ItemDomainCatalogControllerUtility();
        UserInfo currentUser = getCurrentRequestUserInfo();
        LOGGER.debug("Fetching favorite catalog items for user: " + currentUser.getUsername());
        currentUser = userFacade.find(currentUser.getId());
        return controller.getFavoriteItems(currentUser);
    }

    @GET
    @Path("/Inventory")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainInventory> getInventoryItems() {
        return (List<ItemDomainInventory>) (List<?>) getItemsByDomain(ItemDomainName.inventory.getValue());
    }

    @POST
    @Path("/Inventory/Concise")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<ConciseItem> getConciseInventoryItems(ConciseItemOptions options) {
        List<Item> inventoryItems = getItemsByDomain(ItemDomainName.inventory.getValue());

        return ConciseItem.createList(inventoryItems, options);
    }

    @GET
    @Path("/Domains")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Domain> getDomainList() {
        LOGGER.debug("Fetching domain list");
        return domainFacade.findAll();
    }

    @GET
    @Path("/LocationHierarchy")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemHierarchy> getLocationHierarchy() {
        LOGGER.debug("Fetching location hierarchy");
        List<ItemDomainLocation> locationsTopLevel = getLocationsTopLevel();

        List<ItemHierarchy> result = new ArrayList<>();

        for (ItemDomainLocation location : locationsTopLevel) {
            ItemHierarchy locationHierarchy = new ItemHierarchy(location, true);
            result.add(locationHierarchy);
        }

        return result;
    }

    @GET
    @Path("/LocationsTopLevel")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainLocation> getLocationsTopLevel() {
        LOGGER.debug("Fetching locations top level");
        List<ItemDomainLocation> topLocations
                = (List<ItemDomainLocation>) (List<?>) itemFacade.findByDomainWithoutParents(ItemDomainName.location.getValue());

        return topLocations;
    }

    @GET
    @Path("/LocationsChildLocations/{parentLocationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainLocation> getChildLocations(@PathParam("parentLocationId") int itemId) throws ObjectNotFound, InvalidArgument {
        LOGGER.debug("Fetching child locations for location id: " + itemId);
        Item itemById = getItemByIdBase(itemId);

        if (itemById instanceof ItemDomainLocation == false) {
            InvalidArgument invalidArgument = new InvalidArgument("Item passed in is not for a location item.");
            LOGGER.error(invalidArgument);
            throw invalidArgument;
        }

        List<ItemDomainLocation> childLocations = new ArrayList<>();

        for (ItemElement element : itemById.getItemElementDisplayList()) {
            Item containedItem = element.getContainedItem();
            if (containedItem != null) {
                childLocations.add((ItemDomainLocation) containedItem);
            }
        }

        return childLocations;
    }

    @POST
    @Path("/AddLocation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemDomainLocation createLocation(@RequestBody(required = true) NewLocationInformation newLocationInformation) throws AuthorizationError, InvalidArgument, CdbException {
        String newLocationName = newLocationInformation.getLocationName();
        String newlocationType = newLocationInformation.getLocationType();
        String locationDescription = newLocationInformation.getLocationDescription();

        if (newLocationName == null || newLocationName.isEmpty()) {
            throw new InvalidArgument("Location name for the new location must be provided with the request.");
        }
        if (newlocationType == null || newlocationType.isEmpty()) {
            throw new InvalidArgument("Location type for the new location must be provided with the request.");
        }

        LOGGER.debug("Creating a new location: " + newLocationName);

        UserInfo currentUser = getCurrentRequestUserInfo();
        boolean userAdmin = currentUser.isUserAdmin();

        if (userAdmin == false) {
            throw new AuthorizationError("Only adminstrators can create locations.");
        }

        ItemDomainLocationControllerUtility locControllerUtility = new ItemDomainLocationControllerUtility();

        ItemDomainLocation newLocationItem = locControllerUtility.createEntityInstance(currentUser);

        newLocationItem.setName(newLocationName);
        newLocationItem.setDescription(locationDescription);

        // Verify type is valid
        Domain domain = newLocationItem.getDomain();
        ItemType selectedItemType = null;
        for (ItemType type : domain.getItemTypeList()) {
            if (type.getName().equalsIgnoreCase(newlocationType)) {
                selectedItemType = type;
                break;
            }
        }

        if (selectedItemType == null) {
            throw new InvalidArgument("Invalid type for the new location has been provided with the request.");
        }

        // Select location type. 
        newLocationItem.setItemTypeList(new ArrayList<>());
        newLocationItem.getItemTypeList().add(selectedItemType);

        Integer locationQrId = newLocationInformation.getLocationQrId();
        if (locationQrId != null) {
            newLocationItem.setQrId(locationQrId);
        }

        Integer parentLocationId = newLocationInformation.getParentLocationId();
        if (parentLocationId != null) {
            Float sortOrder = newLocationInformation.getSortOrder();
            Item parentItem = locControllerUtility.findById(parentLocationId);
            if (parentItem == null) {
                throw new InvalidArgument("Could not find item with parent location item id: " + parentLocationId);
            }
            if (parentItem instanceof ItemDomainLocation == false) {
                throw new InvalidArgument("Parent location id must be of type location.");
            }

            ItemDomainLocation parentLocation = (ItemDomainLocation) parentItem;

            ItemElement ie = locControllerUtility.createItemElement(parentLocation, currentUser);
            ie.setContainedItem(newLocationItem);
            ie.setSortOrder(sortOrder);

            newLocationItem.setItemElementMemberList(new ArrayList<>());
            newLocationItem.getItemElementMemberList().add(ie);
        }

        locControllerUtility.create(newLocationItem, currentUser);

        return newLocationItem;
    }

    @GET
    @Path("/Projects")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemProject> getItemProjectList() {
        return itemProjectFacade.findAll();
    }

    @GET
    @Path("/Search/{searchText}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Deprecated... for more complete search use search route.")
    @Deprecated
    public SearchEntitiesResults getSearchResults(@PathParam("searchText") String searchText) throws ObjectNotFound, InvalidArgument {
        LOGGER.debug("Performing an item search for search query: " + searchText);

        ItemDomainCatalogControllerUtility catalogControllerUtility = new ItemDomainCatalogControllerUtility();
        ItemDomainInventoryControllerUtility inventoryInstance = new ItemDomainInventoryControllerUtility();
        ItemDomainMachineDesignControllerUtility mdInstance = new ItemDomainMachineDesignControllerUtility();

        LinkedList<SearchResult> catalogResults = catalogControllerUtility.performEntitySearch(searchText, true);
        LinkedList<SearchResult> inventoryResults = inventoryInstance.performEntitySearch(searchText, true);
        LinkedList<SearchResult> mdResults = mdInstance.performEntitySearch(searchText, true);
        
        SearchEntitiesResults results = new SearchEntitiesResults(); 
        results.setItemDomainCatalogResults(catalogResults);
        results.setItemDomainInventoryResults(inventoryResults);
        results.setItemDomainMachineDesignResults(mdResults);
        
        return results; 
    }

    @GET
    @Path("/DetailedCatalogSearch/{searchText}")
    @Produces(MediaType.APPLICATION_JSON)    
    public List<ItemDomainCatalogSearchResult> getDetailedCatalogSearchResults(@PathParam("searchText") String searchText) throws ObjectNotFound, InvalidArgument {
        LOGGER.debug("Performing a detailed catalog item search for search query: " + searchText);

        ItemDomainCatalogControllerUtility catalogUtility = new ItemDomainCatalogControllerUtility();

        LinkedList<SearchResult> catalogResults = catalogUtility.performEntitySearch(searchText, true);

        List<ItemDomainCatalogSearchResult> detailedSearchResults = new ArrayList<>();
        for (SearchResult result : catalogResults) {
            ItemDomainCatalog item = (ItemDomainCatalog) catalogUtility.findById(result.getObjectId());

            detailedSearchResults.add(new ItemDomainCatalogSearchResult(result, item));
        }

        return detailedSearchResults;
    }

}
