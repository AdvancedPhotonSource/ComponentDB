/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author djarosz
 */
@Path("/propertyTypes")
public class PropertyTypeRoute {
    // TODO add logger 
    
    @EJB
    private PropertyTypeFacade propertyTypeFacade;
    
    @GET
    @Path("/all")    
    @Produces(MediaType.APPLICATION_JSON)    
    public List<PropertyType> getAll() {        
        System.out.println("REST returinging find all");
        return propertyTypeFacade.findAll();
    }
    
}
