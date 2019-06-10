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
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeHandlerFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.model.jsf.beans.PropertyValueImageUploadBean;
import gov.anl.aps.cdb.portal.utilities.AuthorizationUtility;
import gov.anl.aps.cdb.rest.authentication.Secured;
import gov.anl.aps.cdb.rest.authentication.User;
import gov.anl.aps.cdb.rest.entities.FileUploadObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author djarosz
 */
@Path("/items")
@Tag(name = "Item")
public class ItemRoute extends BaseRoute {

    @EJB
    ItemFacade itemFacade;

    @EJB
    DomainFacade domainFacade;

    @EJB
    PropertyTypeHandlerFacade propertyTypeHandlerFacade;

    @GET
    @Path("/ById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Fetch an item by its id.")
    public Item getItemById(@PathParam("id") int id) throws ObjectNotFound {
        Item findById = itemFacade.findById(id);
        if (findById == null) {
            throw new ObjectNotFound("Could not find item with id: " + id); 
        }
        return findById;
    }
    
    @GET
    @Path("/ItemsDerivedFromItemByItemId/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Item> getItemsDerivedFromItemByItemId(@PathParam("id") int id) throws ObjectNotFound {
        Item itemById = getItemById(id);        
        return itemById.getDerivedFromItemList(); 
    }

    @GET
    @Path("/ById/{id}/Status")
    @Produces(MediaType.APPLICATION_JSON)
    public PropertyValue getItemStatus(@PathParam("id") int id) {
        Item item = itemFacade.findById(id);

        if (item instanceof ItemDomainInventory) {
            return ((ItemDomainInventory) item).getInventoryStatusPropertyValue();
        }

        return null;
    }

    @GET
    @Path("/ById/{id}/Permission")
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public boolean verifyUserPermissionForItem(@PathParam("id") int id) throws ObjectNotFound {
        Item itemById = getItemById(id);
        if (itemById != null) {
            UserInfo user = getCurrentRequestUserInfo();
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
    @Path("/UpdateDetails")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public Item updateItemDetails(Item item) throws ObjectNotFound, CdbException {
        int itemId = item.getId();
        Item dbItem = getItemById(itemId);

        dbItem.setName(item.getName());
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
        Item dbItem = getItemById(itemId);
        UserInfo updatedByUser = getCurrentRequestUserInfo();

        ItemController itemController = dbItem.getItemDomainController();        
        PropertyValue dbPropertyValue = null;

        int propIdx = -1;

        if (propertyValue.getId() == null) {
            PropertyType propertyType = propertyValue.getPropertyType();
            if (propertyType == null) {
                throw new InvalidArgument("Property type must be assigned to new property value.");
            }
            dbPropertyValue = itemController.preparePropertyTypeValueAdd(dbItem, propertyType, null, null, updatedByUser);
        } else {
            // Property already exists for the particular item.             
            for (int i = 0; i < dbItem.getPropertyValueList().size(); i++) {
                PropertyValue propertyValueIttr = dbItem.getPropertyValueList().get(i);
                if (propertyValueIttr.getId().equals(propertyValue.getId())) {
                    dbPropertyValue = propertyValueIttr;
                    propIdx = i;
                    break;
                }
            }
        }

        if (dbPropertyValue == null) {
            throw new ObjectNotFound("There was an error trying to load the property value.");
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
        if (propIdx > 0) {
            dbPropertyValue = pvList.get(propIdx);
        } else {
            propIdx = pvList.size() - 1;
            dbPropertyValue = pvList.get(propIdx);
        }

        return dbPropertyValue;
    }

    @POST
    @Path("/uploadImage/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public PropertyValue uploadImageForItem(@PathParam("itemId") int itemId, FileUploadObject imageUpload) throws AuthorizationError, DbError, IOException, ObjectNotFound, CdbException {
        Item dbItem = getItemById(itemId);
        UserInfo updatedByUser = getCurrentRequestUserInfo();
        
        if (!verifyUserPermissionForItem(updatedByUser, dbItem)) {
            //TODO add logger
            throw new AuthorizationError("User does not have permission to upload image for the item");
        }

        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(imageUpload.getBase64Binary());
        ByteArrayInputStream stream = new ByteArrayInputStream(decode);       

        ItemController itemController = dbItem.getItemDomainController();

        PropertyType imagePropertyType = PropertyValueImageUploadBean.getImagePropertyType(propertyTypeHandlerFacade);
        if (imagePropertyType == null) {
            throw new DbError("Could not find image property type.");
        }

        PropertyValue pv = itemController.preparePropertyTypeValueAdd(dbItem, imagePropertyType, null, null, updatedByUser);

        try {
            PropertyValueImageUploadBean.uploadImage(pv, imageUpload.getFileName(), stream);
        } catch (IOException ex) {
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
        Item findByQrId = itemFacade.findByQrId(qrId);
        if (findByQrId == null) {
            throw new ObjectNotFound("Could not find item with qrid: " + qrId);
        }
        return findByQrId;
    }

    @GET
    @Path("/PropertiesForItem/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PropertyValue> getPropertiesForItem(@PathParam("itemId") int itemId) throws ObjectNotFound {
        Item itemById = getItemById(itemId);
        return itemById.getPropertyValueList();
    }

    @GET
    @Path("/LogsForItem/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Log> getLogsForItem(@PathParam("itemId") int itemId) throws ObjectNotFound {
        Item itemById = getItemById(itemId);
        return itemById.getLogList();
    }

    @GET
    @Path("/ImagePropertiesForItem/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PropertyValue> getImagePropertiesForItem(@PathParam("itemId") int itemId) throws ObjectNotFound {
        Item itemById = getItemById(itemId);
        List<PropertyValue> propertyValueList = itemById.getPropertyValueList();
        return PropertyValueUtility.prepareImagePropertyValueList(propertyValueList);
    }

    @GET
    @Path("/ByDomain/{domainName}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Item> getItemsByDomain(@PathParam("domainName") String domainName) {
        return itemFacade.findByDomain(domainName);
    }

    @GET
    @Path("/Catalog")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainCatalog> getCatalogItems() {
        return (List<ItemDomainCatalog>) (List<?>) getItemsByDomain(ItemDomainName.catalog.getValue());
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
        return domainFacade.findAll();
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
