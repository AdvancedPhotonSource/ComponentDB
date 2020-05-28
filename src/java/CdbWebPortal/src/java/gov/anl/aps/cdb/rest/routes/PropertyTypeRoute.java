/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Path("/PropertyTypes")
@Tag(name = "Property")
public class PropertyTypeRoute extends BaseRoute {
    
    private static final Logger LOGGER = LogManager.getLogger(PropertyType.class.getName());
    
    @EJB
    private PropertyTypeFacade propertyTypeFacade;
    
    @GET
    @Path("/all")    
    @Produces(MediaType.APPLICATION_JSON)    
    public List<PropertyType> getAll() {        
        LOGGER.debug("Fetching all property types.");
        return propertyTypeFacade.findAll();
    }
    
    @GET
    @Path("/inventoryStatusPropertyType")    
    @Produces(MediaType.APPLICATION_JSON)    
    public PropertyType getInventoryStatusPropertyType() { 
        LOGGER.debug("Fetching inventory item status property type.");
        return propertyTypeFacade.findByName(ItemDomainInventory.ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME); 
    }
    
}
