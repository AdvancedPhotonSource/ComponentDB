/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.AuthorizationError;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.DbError;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.LocatableItemController;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeHandlerFacade;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableItem;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.model.jsf.beans.PropertyValueImageUploadBean;
import gov.anl.aps.cdb.portal.utilities.AuthorizationUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.rest.authentication.Secured;
import gov.anl.aps.cdb.rest.authentication.User;
import gov.anl.aps.cdb.rest.entities.FileUploadObject;
import gov.anl.aps.cdb.rest.entities.ItemDomainCatalogSearchResult;
import gov.anl.aps.cdb.rest.entities.ItemDomainMdSearchResult;
import gov.anl.aps.cdb.rest.entities.ItemHierarchy;
import gov.anl.aps.cdb.rest.entities.ItemLocationInformation;
import gov.anl.aps.cdb.rest.entities.ItemSearchResults;
import gov.anl.aps.cdb.rest.entities.SimpleLocationInformation;
import gov.anl.aps.cdb.rest.entities.LogEntryEditInformation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
@Path("/Items")
@Tag(name = "Item")
public class ItemRoute extends BaseRoute {
    
    private static final Logger LOGGER = Logger.getLogger(ItemRoute.class.getName());
    
    @EJB
    ItemFacade itemFacade;
    
    @EJB
    PropertyTypeFacade propertyTypeFacade;
    
    @EJB
    DomainFacade domainFacade;
    
    @EJB
    PropertyTypeHandlerFacade propertyTypeHandlerFacade;
    
    @EJB
    UserInfoFacade userInfoFacade;    
    
    @GET
    @Path("/ById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Fetch an item by its id.")
    public Item getItemByIdFromAPI(@PathParam("id") int id) throws ObjectNotFound {
        LOGGER.debug("Fetching item by id: " + id); 
        return getItemById(id); 
    }
        
    public Item getItemById(@PathParam("id") int id) throws ObjectNotFound {        
        Item findById = itemFacade.findById(id);
        if (findById == null) {
            ObjectNotFound ex = new ObjectNotFound("Could not find item with id: " + id);
            LOGGER.error(ex);
            throw ex; 
        }
        return findById;
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
    @Path("/ItemsDerivedFromItemByItemId/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Item> getItemsDerivedFromItemByItemId(@PathParam("id") int id) throws ObjectNotFound {        
        LOGGER.debug("Fetching derived from item list for item id: " + id); 
        Item itemById = getItemById(id);
        return itemById.getDerivedFromItemList();
    }
    
    @GET
    @Path("/ById/{id}/Status")
    @Produces(MediaType.APPLICATION_JSON)
    public PropertyValue getItemStatus(@PathParam("id") int id) {
        LOGGER.debug("Fetching status for item id: " + id);
        Item item = itemFacade.findById(id);
        
        if (item instanceof ItemDomainInventory) {
            return ((ItemDomainInventory) item).getInventoryStatusPropertyValue();
        }
        
        return null;
    }
    
    @GET
    @Path("/ById/{id}/Location")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemLocationInformation getItemLocation(@PathParam("id") int id) throws ObjectNotFound, InvalidArgument {
        LOGGER.debug("Fetching location for item with id: " + id);
        Item itemById = getItemById(id);
                
        if (itemById instanceof LocatableItem) {
            LocatableItem locatableItem = (LocatableItem) itemById;
            LocatableItemController controller = LocatableItemController.getApiInstance();
            
            controller.setItemLocationInfo(locatableItem);            
            
            return new ItemLocationInformation(locatableItem);
            
        } else {
            InvalidArgument ex = new InvalidArgument("Item requested cannot have a location specified. Item Id: " + id);
            LOGGER.error(ex);
            throw ex; 
        }
    }
    
    @GET
    @Path("/ById/{id}/Permission")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public boolean verifyUserPermissionForItem(@PathParam("id") int id) throws ObjectNotFound {
        LOGGER.debug("Performing permission verification for item: " + id);
        Item itemById = getItemById(id);
        if (itemById != null) {            
            UserInfo user = getCurrentRequestUserInfo();
            LOGGER.debug("Continuing permission verification for user: " + user.getUsername());
            return verifyUserPermissionForItem(user, itemById);
        }
        return false;
    }
    
    private boolean verifyUserPermissionForItem(UserInfo user, Item item) {        
        if (user != null) {
            if (isUserAdmin(user)) {
                return true;
            }
            return AuthorizationUtility.isEntityWriteableByUser(item, user);
        }
        return false;
    }
    
    @POST
    @Path("/UpdateLocation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public ItemLocationInformation updateItemLocation(SimpleLocationInformation locationInformation) throws ObjectNotFound, InvalidArgument, CdbException {
        LOGGER.debug("Updating location for item with id: " + locationInformation.getLocatableItemId());
        // Validate ids provided 
        Item item = getItemById(locationInformation.getLocatableItemId());        
        if (item instanceof LocatableItem == false) {
            InvalidArgument invalidArgument = new InvalidArgument("Locatable item id is not a locatable item");
            LOGGER.error(invalidArgument);
            throw invalidArgument; 
        }        
        LocatableItem locatableItem = (LocatableItem) item;
        
        Integer locationItemId = locationInformation.getLocationItemId();
        ItemDomainLocation locationItem = null;        
        
        if (locationItemId != null) {
            Item locItem = getItemById(locationItemId);            
            if (locItem instanceof ItemDomainLocation == false) {
                InvalidArgument invalidArgument = new InvalidArgument("Location item id is not a location.");
                LOGGER.error(invalidArgument);
                throw invalidArgument; 
            }            
            locationItem = (ItemDomainLocation) locItem;            
        }

        // Load current location info
        LocatableItemController locationController = LocatableItemController.getApiInstance();
        locationController.setItemLocationInfo(locatableItem);

        // Use the location information provided 
        locatableItem.setLocation(locationItem);
        locatableItem.setLocationDetails(locationInformation.getLocationDetails());

        // Perfrom update
        UserInfo updateUser = getCurrentRequestUserInfo();
        ItemController controller = locatableItem.getItemDomainController();
        controller.updateFromApi(locatableItem, updateUser);

        // Get the latest item after update to respond
        locatableItem = (LocatableItem) getItemById(locatableItem.getId());
        locationController.setItemLocationInfo(locatableItem);
        
        return new ItemLocationInformation(locatableItem);
    }
    
    @POST
    @Path("/UpdateDetails")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public Item updateItemDetails(Item item) throws ObjectNotFound, CdbException {
        LOGGER.debug("Updating details for item with id: " + item.getId());
        int itemId = item.getId();
        Item dbItem = getItemById(itemId);
        
        dbItem.setName(item.getName());
        dbItem.setQrId(item.getQrId());
        dbItem.setItemIdentifier1(item.getItemIdentifier1());
        dbItem.setItemIdentifier2(item.getItemIdentifier2());
        dbItem.setDescription(item.getDescriptionFromAPI());
        dbItem.setItemTypeList(item.getItemTypeList());
        dbItem.setItemCategoryList(item.getItemCategoryList());
        
        ItemController itemDomainControllerForApi = dbItem.getItemDomainController();
        
        UserInfo updateUser = getCurrentRequestUserInfo();
        itemDomainControllerForApi.updateFromApi(dbItem, updateUser);
        
        return dbItem;
    }
    
    @POST
    @Path("/UpdateProperty/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public PropertyValue updateItemPropertyValue(@PathParam("itemId") int itemId, PropertyValue propertyValue) throws InvalidArgument, ObjectNotFound, CdbException {
        LOGGER.debug("Updating details for item with id: " + itemId);
        Item dbItem = getItemById(itemId);
        UserInfo updatedByUser = getCurrentRequestUserInfo();
        
        if (!verifyUserPermissionForItem(updatedByUser, dbItem)) {            
            AuthorizationError ex = new AuthorizationError("User does not have permission to update property value for the item");
            LOGGER.error(ex);
            throw ex; 
        }
        
        ItemController itemController = dbItem.getItemDomainController();
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

        // Set passed in property value to match db property value 
        dbPropertyValue.setValue(propertyValue.getValue());
        dbPropertyValue.setDisplayValue(propertyValue.getDisplayValue());
        dbPropertyValue.setTag(propertyValue.getTag());
        dbPropertyValue.setDescription(propertyValue.getDescription());
        dbPropertyValue.setUnits(propertyValue.getUnits());
        dbPropertyValue.setIsDynamic(propertyValue.getIsDynamic());
        dbPropertyValue.setIsUserWriteable(propertyValue.getIsUserWriteable());
        
        itemController.updateFromApi(dbItem, updatedByUser);
        
        dbItem = (Item) itemController.getCurrent();
        
        List<PropertyValue> pvList = dbItem.getPropertyValueList();
        if (propIdx >= 0) {
            dbPropertyValue = pvList.get(propIdx);
        } 
        
        return dbPropertyValue;
    }
    
    @POST
    @Path("/UpdatePropertyMetadata/{itemId}/{propertyValueId}") //TODO propertyValueID instead of property type name
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public PropertyValue updateItemPropertyMetadata(@PathParam("itemId") int itemId, @PathParam("propertyValueId") int propertyValueId, PropertyMetadata propertyMetadata) throws InvalidArgument, ObjectNotFound, CdbException {
        LOGGER.debug("Updating property metadata for item with id: " + itemId);
        Item dbItem = getItemById(itemId);
        UserInfo updatedByUser = getCurrentRequestUserInfo();
        
        if (!verifyUserPermissionForItem(updatedByUser, dbItem)) {            
            AuthorizationError ex = new AuthorizationError("User does not have permission to upload property metadata for the item property");
            LOGGER.error(ex);
            throw ex; 
        }
        
        ItemController itemController = dbItem.getItemDomainController();
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
        
        // Set passed in property value to match db property value 
        dbPropertyValue.setPropertyMetadataValue(propertyMetadata.getMetadataKey(), propertyMetadata.getMetadataValue());
        
        itemController.updateFromApi(dbItem, updatedByUser);
        
        dbItem = (Item) itemController.getCurrent();
        
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
    public PropertyValue addItemPropertyValue(@PathParam("itemId") int itemId, PropertyValue propertyValue) throws InvalidArgument, ObjectNotFound, CdbException {
        LOGGER.debug("Adding property to item with id: " + itemId);
        Item dbItem = getItemById(itemId);
        UserInfo updatedByUser = getCurrentRequestUserInfo();
        
        if (!verifyUserPermissionForItem(updatedByUser, dbItem)) {            
            AuthorizationError ex = new AuthorizationError("User does not have permission to add property value for the item");
            LOGGER.error(ex);
            throw ex; 
        }
        
        ItemController itemController = dbItem.getItemDomainController();
        PropertyValue dbPropertyValue = null;
        
        PropertyType propertyType = propertyValue.getPropertyType();
        if(propertyType.getId() == null) {
            //Find property type object by name
            propertyType = getPropertyTypeByName(propertyType.getName());
            if (propertyType == null) {
                InvalidArgument invalidArgument = new InvalidArgument("Property type name must be correct or give property type id");
                LOGGER.error(invalidArgument);
                throw invalidArgument; 
            }
        }
        propertyValue.setPropertyType(propertyType);
        
        LOGGER.debug("Creating new property.");
        dbPropertyValue = itemController.preparePropertyTypeValueAdd(dbItem, propertyType, null, null, updatedByUser);

        // Set passed in property value to match db property value 
        dbPropertyValue.setValue(propertyValue.getValue());
        dbPropertyValue.setDisplayValue(propertyValue.getDisplayValue());
        dbPropertyValue.setTag(propertyValue.getTag());
        dbPropertyValue.setDescription(propertyValue.getDescription());
        dbPropertyValue.setUnits(propertyValue.getUnits());
        dbPropertyValue.setIsDynamic(propertyValue.getIsDynamic());
        dbPropertyValue.setIsUserWriteable(propertyValue.getIsUserWriteable());
        
        itemController.updateFromApi(dbItem, updatedByUser);
        
        dbItem = (Item) itemController.getCurrent();
        
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
    public Log addLogEntryToItem(LogEntryEditInformation logEntryEditInformation) throws ObjectNotFound, CdbException {
        LOGGER.debug("Adding log for item with id: " + logEntryEditInformation.getItemId());
        int itemId = logEntryEditInformation.getItemId();
        Item itemById = getItemById(itemId);
        
        ItemController controller = itemById.getItemDomainController();
        UserInfo updateUser = getCurrentRequestUserInfo();
        Log newLog = controller.prepareAddLog(itemById, updateUser);
        
        newLog.setText(logEntryEditInformation.getLogEntry());        
        if (logEntryEditInformation.getEffectiveDate() != null) {
            newLog.setEffectiveFromDateTime(logEntryEditInformation.getEffectiveDate()); 
        }
        
        
        controller.updateFromApi(itemById, updateUser);
        
        return newLog;
    }
    
    @POST
    @Path("/uploadImage/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public PropertyValue uploadImageForItem(@PathParam("itemId") int itemId, FileUploadObject imageUpload) throws AuthorizationError, DbError, IOException, ObjectNotFound, CdbException {
        LOGGER.debug("Uploading image for item: " + itemId);
        Item dbItem = getItemById(itemId);
        UserInfo updatedByUser = getCurrentRequestUserInfo();
        
        if (!verifyUserPermissionForItem(updatedByUser, dbItem)) {            
            AuthorizationError ex = new AuthorizationError("User does not have permission to upload image for the item");
            LOGGER.error(ex);
            throw ex; 
        }
        
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(imageUpload.getBase64Binary());
        ByteArrayInputStream stream = new ByteArrayInputStream(decode);
        
        ItemController itemController = dbItem.getItemDomainController();
        
        PropertyType imagePropertyType = PropertyValueImageUploadBean.getImagePropertyType(propertyTypeHandlerFacade);
        if (imagePropertyType == null) {
            DbError dbError = new DbError("Could not find image property type.");
            LOGGER.error(dbError);
            throw dbError; 
        }
        
        PropertyValue pv = itemController.preparePropertyTypeValueAdd(dbItem, imagePropertyType, null, null, updatedByUser);
        
        try {
            PropertyValueImageUploadBean.uploadImage(pv, imageUpload.getFileName(), stream);
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw ex;
        }
        
        itemController.updateFromApi(dbItem, updatedByUser);
        
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
    @Path("/PropertiesForItem/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PropertyValue> getPropertiesForItem(@PathParam("itemId") int itemId) throws ObjectNotFound {
        LOGGER.debug("Fetching properties for item by id: " + itemId);
        Item itemById = getItemById(itemId);
        return itemById.getPropertyValueList();
    }
    
    @GET
    @Path("/LogsForItem/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Log> getLogsForItem(@PathParam("itemId") int itemId) throws ObjectNotFound {
        LOGGER.debug("Fetching logs for item by id: " + itemId);
        Item itemById = getItemById(itemId);
        return itemById.getLogList();
    }
    
    @GET
    @Path("/ImagePropertiesForItem/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PropertyValue> getImagePropertiesForItem(@PathParam("itemId") int itemId) throws ObjectNotFound {
        LOGGER.debug("Fetching image properties for item by id: " + itemId);
        Item itemById = getItemById(itemId);
        List<PropertyValue> propertyValueList = itemById.getPropertyValueList();
        return PropertyValueUtility.prepareImagePropertyValueList(propertyValueList);
    }
    
    @GET
    @Path("/ByDomain/{domainName}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Item> getItemsByDomain(@PathParam("domainName") String domainName) {
        LOGGER.debug("Fetch items for domain: " + domainName);
        return itemFacade.findByDomain(domainName);
    }
    
    @GET
    @Path("/Catalog")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainCatalog> getCatalogItems() {
        return (List<ItemDomainCatalog>) (List<?>) getItemsByDomain(ItemDomainName.catalog.getValue());
    }
    
    @GET
    @Path("/Catalog/Favorites")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public List<ItemDomainCatalog> getFavoriteCatalogItems() {        
        ItemDomainCatalogController controller = ItemDomainCatalogController.getApiInstance();
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
        Item itemById = getItemById(itemId);
        
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
    
    @GET
    @Path("/Search/{searchText}")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemSearchResults getSearchResults(@PathParam("searchText") String searchText) throws ObjectNotFound, InvalidArgument {
        LOGGER.debug("Performing an item search for search query: " + searchText);
        
        ItemDomainCatalogController catalogInstance = ItemDomainCatalogController.getApiInstance();
        ItemDomainInventoryController inventoryInstance = ItemDomainInventoryController.getApiInstance();
        ItemDomainMachineDesignController mdInstance = ItemDomainMachineDesignController.getApiInstance(); 
        
        catalogInstance.performEntitySearch(searchText, true);        
        LinkedList<SearchResult> catalogResults = catalogInstance.getSearchResultList();
        inventoryInstance.performEntitySearch(searchText, true);
        LinkedList<SearchResult> inventoryResults = inventoryInstance.getSearchResultList();
        mdInstance.performEntitySearch(searchText, true);
        LinkedList<SearchResult> mdResults = mdInstance.getSearchResultList(); 
        
        return new ItemSearchResults(catalogResults, inventoryResults, mdResults);
    }
    
    @GET
    @Path("/DetailedCatalogSearch/{searchText}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainCatalogSearchResult> getDetailedCatalogSearchResults(@PathParam("searchText") String searchText) throws ObjectNotFound, InvalidArgument {
        LOGGER.debug("Performing a detailed catalog item search for search query: " + searchText);
        
        ItemDomainCatalogController catalogInstance = ItemDomainCatalogController.getApiInstance();        
        
        catalogInstance.performEntitySearch(searchText, true);        
        LinkedList<SearchResult> catalogResults = catalogInstance.getSearchResultList();
        
        List<ItemDomainCatalogSearchResult> detailedSearchResults = new ArrayList<>(); 
        for (SearchResult result : catalogResults) {
            ItemDomainCatalog item = (ItemDomainCatalog) catalogInstance.getItem(result.getObjectId()); 
            
            detailedSearchResults.add(new ItemDomainCatalogSearchResult(result, item));                         
        }
        
        return detailedSearchResults; 
    }
    
    @GET
    @Path("/DetailedMachineDesignSearch/{searchText}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainMdSearchResult> getDetailedMdSearchResults(@PathParam("searchText") String searchText) throws ObjectNotFound, InvalidArgument {
        LOGGER.debug("Performing a detailed machine design item search for search query: " + searchText);
        
        ItemDomainMachineDesignController mdInstance = ItemDomainMachineDesignController.getApiInstance();
        
        TreeNode rootNode = mdInstance.getSearchResults(searchText, true);
        
        List<TreeNode> children = rootNode.getChildren();
        List<ItemDomainMdSearchResult> itemHierarchy = new ArrayList<>(); 
        for (TreeNode child: children) {
            ItemDomainMdSearchResult hierarchy = new ItemDomainMdSearchResult(child);
            itemHierarchy.add(hierarchy); 
        }
        
        return itemHierarchy; 
    }
    
    private UserInfo getCurrentRequestUserInfo() {
        Principal userPrincipal = securityContext.getUserPrincipal();
        if (userPrincipal instanceof User) {
            UserInfo user = ((User) userPrincipal).getUser();
            return user;
        }
        return null;
    }
        
}
