/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.AuthorizationError;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.controllers.utilities.CdbEntityControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.rest.authentication.Secured;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

/**
 *
 * @author djarosz
 */
@Path("/PropertyValues")
@Tag(name = "PropertyValue")
public class PropertyValueRoute extends BaseRoute {
    
    private static final Logger LOGGER = LogManager.getLogger(PropertyType.class.getName());
    
    @EJB
    private PropertyValueFacade propertyValueFacade; 
    
    @GET
    @Path("/ById/{id}")
    @Produces(MediaType.APPLICATION_JSON)    
    public PropertyValue getPropertyValue(@PathParam("id") int id) {        
        LOGGER.debug("Fetching property value with id: " + id);
        return propertyValueFacade.find(id);
    }
    
    @GET
    @Path("/ById/{id}/History")
    @Produces(MediaType.APPLICATION_JSON)    
    public List<PropertyValueHistory> getPropertyValueHistory(@PathParam("id") int id) {
        PropertyValue propertyValue = getPropertyValue(id);
        return propertyValue.getPropertyValueHistoryList();        
    }

    @GET
    @Path("/ById/{id}/Metadata")
    @Produces(MediaType.APPLICATION_JSON)    
    public List<PropertyMetadata> getPropertyValueMetadata(@PathParam("id") int id) {
        PropertyValue propertyValue = getPropertyValue(id);
        return propertyValue.getPropertyMetadataList();
    }
        
    @POST
    @Path("/ById/{id}/AddUpdateMetadata")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "cdbAuth")
    @Secured
    public List<PropertyMetadata> addOrUpdatePropertyValueMetadataByMap(@PathParam("id") int propertyValueId, @RequestBody(required = true) Map<String, String> metadataMap) throws InvalidArgument, AuthorizationError, CdbException {
        PropertyValue propertyValue = getPropertyValue(propertyValueId);
        
        List<ItemElement> itemElementList = propertyValue.getItemElementList(); 
        
        if (itemElementList.size() != 1) {
            throw new InvalidArgument("Only properties assigned to a single item can be updated via this API call.");            
        }
        ItemElement ie = itemElementList.get(0);
        Item parentItem = ie.getParentItem();
        if (parentItem == null) {
            throw new InvalidArgument("Failed to fetch item assigned to this property.");
        }
        
        if (propertyValue.getPropertyType().getIsInternal()) {
            throw new InvalidArgument("Internal properties can only be updated using specialized functionality.");
        }
                
        UserInfo updatedByUser = verifyCurrentUserPermissionForItem(parentItem);                
        
        List<PropertyValue> propertyValueList = parentItem.getPropertyValueList();
        propertyValue = null; 
        for (PropertyValue pv : propertyValueList) {
            if (pv.getId() == propertyValueId) {
                propertyValue = pv; 
                break; 
            }
        }
        
        if (propertyValue == null) {
            throw new InvalidArgument("Could not find property assigned to parent item.");
        }
        
        Set<Map.Entry<String, String>> entrySet = metadataMap.entrySet();
        for (Map.Entry entry : entrySet) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();                        
            propertyValue.setPropertyMetadataValue(key, value);
        }
             
        ItemControllerUtility controllerUtility = parentItem.getItemControllerUtility();
        controllerUtility.update(parentItem, updatedByUser); 
        
        propertyValue = getPropertyValue(propertyValueId);
        return propertyValue.getPropertyMetadataList(); 
    }
    
    
}
