/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import java.util.List;
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

    @EJB
    DomainFacade domainFacade;

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

    @GET
    @Path("/PropertiesForItem/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PropertyValue> getPropertiesForItem(@PathParam("itemId") String itemId) {
        Item itemById = getItemById(itemId);
        return itemById.getPropertyValueList();
    }
    
    @GET
    @Path("/LogsForItem/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Log> getLogsForItem(@PathParam("itemId") String itemId) {
        Item itemById = getItemById(itemId);
        return itemById.getLogList();
    }
    
    @GET
    @Path("/ImagePropertiesForItem/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PropertyValue> getImagePropertiesForItem(@PathParam("itemId") String itemId) {
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
    @Path("/Domains")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Domain> getDomainList() {
        return domainFacade.findAll();
    }

}
