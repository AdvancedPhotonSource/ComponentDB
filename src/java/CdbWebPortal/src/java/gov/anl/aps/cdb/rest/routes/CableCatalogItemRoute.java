/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
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
 * @author craig
 */
@Path("/CableCatalogItems")
@Tag(name = "cableCatalogItems")
public class CableCatalogItemRoute extends BaseRoute {
    
    private static final Logger LOGGER = LogManager.getLogger(CableCatalogItemRoute.class.getName());
    
    @EJB
    ItemDomainCableCatalogFacade facade; 
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ItemDomainCableCatalog> getCableCatalogItemList() {
        LOGGER.debug("Fetching cable catalog list");
        return facade.findAll();
    }
    
    @GET
    @Path("/ById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ItemDomainCableCatalog getCableCatalogItemById(@PathParam("id") int id) throws ObjectNotFound {
        LOGGER.debug("Fetching item with id: " + id);
        ItemDomainCableCatalog item = facade.find(id);
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
    public ItemDomainCableCatalog getCableCatalogItemByName(@PathParam("name") String name) throws ObjectNotFound {
        LOGGER.debug("Fetching item with name: " + name);
        List<ItemDomainCableCatalog> itemList = facade.findByName(name);
        if (itemList == null || itemList.isEmpty()) {
            ObjectNotFound ex = new ObjectNotFound("Could not find item with name: " + name);
            LOGGER.error(ex);
            throw ex; 
        } else if (itemList.size() > 1) {
            ObjectNotFound ex = new ObjectNotFound("Found multiple items with name: " + name);
            LOGGER.error(ex);
            throw ex; 
        }
        return itemList.get(0);
    }

}
