/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author djarosz
 */
@Path("/items")
public class ItemRoute {
    
    @EJB 
    ItemFacade itemFacade; 
    
    @GET
    @Path("/ById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Item getItemById(@PathParam("id") String id) {
        int identifier = Integer.parseInt(id);
        Item findById = itemFacade.findById(identifier); 
        return findById; 
    }
    
    @GET
    @Path("/ByQrId/{qrId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Item getItemByQrId(@PathParam("qrId") String qrId) {
        int qrIdInt = Integer.parseInt(qrId);
        Item findByQrId = itemFacade.findByQrId(qrIdInt); 
        return findByQrId; 
    }
    
}
