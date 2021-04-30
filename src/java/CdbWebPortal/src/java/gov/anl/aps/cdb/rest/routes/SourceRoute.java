/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.portal.model.db.beans.SourceFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import gov.anl.aps.cdb.rest.entities.SourceIdListRequest;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
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
 * @author craig
 */
@Path("/Sources")
@Tag(name = "sources")
public class SourceRoute extends BaseRoute {
    
    private static final Logger LOGGER = LogManager.getLogger(SourceRoute.class.getName());
    
    @EJB
    SourceFacade sourceFacade; 
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Source> getSourceList() {
        LOGGER.debug("Fetching source list");
        return sourceFacade.findAll();
    }
    
    @GET
    @Path("/ById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Source getSourceById(@PathParam("id") int id) throws ObjectNotFound {
        LOGGER.debug("Fetching source with id: " + id);
        Source source = sourceFacade.find(id);
        if (source == null) {
            ObjectNotFound ex = new ObjectNotFound("Could not find source with id: " + id);
            LOGGER.error(ex);
            throw ex; 
        }
        return source;
    }
    
    @GET
    @Path("/ByName/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Source getSourceByName(@PathParam("name") String name) throws ObjectNotFound {
        LOGGER.debug("Fetching source with name: " + name);
        Source source = sourceFacade.findByName(name);
        if (source == null) {
            ObjectNotFound ex = new ObjectNotFound("Could not find source with name: " + name);
            LOGGER.error(ex);
            throw ex; 
        }
        return source;
    }

    @POST
    @Path("/IdList")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Integer> getSourceIdList(@RequestBody(required = true) SourceIdListRequest request) {
        List<String> nameList = request.getNameList();
        LOGGER.debug("Fetching list of source id's by name list size: " + nameList.size());
        List<Integer> idList = new ArrayList<>(nameList.size());
        for (String name : nameList) {
            if ((name != null) && (!name.isBlank())) {
                Source sourceItem = sourceFacade.findByName(name);
                if (sourceItem == null) {
                    // use 0 to indicate that there is no item with specified name
                    idList.add(0);
                } else {
                    idList.add(sourceItem.getId());
                }
            } else {
                idList.add(0);
            }
        }
        return idList;
    }
}
