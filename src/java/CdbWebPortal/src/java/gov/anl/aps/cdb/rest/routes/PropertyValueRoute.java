/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Path("/PropertyValues")
@Tag(name = "PropertyValue")
public class PropertyValueRoute extends BaseRoute {
    
    private static final Logger LOGGER = Logger.getLogger(PropertyType.class.getName());
    
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
    
    
}
