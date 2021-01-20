/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemCategoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.GET;
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
@Path("/Domains")
@Tag(name = "domain")
public class DomainRoute extends BaseRoute {
    
    private static final Logger LOGGER = LogManager.getLogger(Domain.class.getName());
    
    @EJB
    DomainFacade domainFacade; 
    
    @EJB
    ItemCategoryFacade itemCategoryFacade;
    
    @EJB 
    ItemTypeFacade itemTypeFacade;
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Domain> getDomainList() {
        LOGGER.debug("Fetching domain list");
        return domainFacade.findAll();
    }
    
    @GET
    @Path("/ById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Domain getDomainById(@PathParam("id") int id) {
        LOGGER.debug("Fetching domain with id: " + id);
        return domainFacade.find(id);
    }
    
    @GET
    @Path("/ByName/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Domain getDomainByName(@PathParam("name") String name) {
        LOGGER.debug("Fetching domain with name: " + name);
        return domainFacade.findByName(name);
    }
    
    @GET
    @Path("/ById/{id}/Categories")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemCategory> getDomainCategoryList(@PathParam("id") int id) {
        Domain domainById = getDomainById(id);
        return domainById.getItemCategoryList();
    }
    
    @GET
    @Path("/ById/{id}/Types")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemType> getDomainTypeList(@PathParam("id") int id) {
        Domain domainById = getDomainById(id);
        return domainById.getItemTypeList();
    }
    
    @GET
    @Path("/Category/ById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemCategory getItemCategoryById(@PathParam("id") int id) {
        return itemCategoryFacade.find(id);
    }
    
    @GET
    @Path("/Category/ById/{id}/Types")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemType> getItemCategoryAllowedTypes(@PathParam("id") int id) {
        ItemCategory itemCategoryById = getItemCategoryById(id);        
        return itemCategoryById.getItemTypeList();
    }
       
    @GET
    @Path("/Type/ById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemType getItemTypeById(@PathParam("id") int id) {
        return itemTypeFacade.find(id);
    }
    
    @GET
    @Path("/Type/ById/{id}/Categories")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemCategory> getItemTypeAllowedTypes(@PathParam("id") int id) {
        ItemType itemTypeById = getItemTypeById(id);        
        return itemTypeById.getItemCategoryList();
    }
    
}
