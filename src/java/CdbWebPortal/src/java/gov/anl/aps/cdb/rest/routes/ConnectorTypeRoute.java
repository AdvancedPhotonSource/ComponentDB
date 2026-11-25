/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.portal.model.db.beans.ConnectorTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ConnectorType;
import gov.anl.aps.cdb.rest.entities.ConnectorTypeIdListRequest;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;  
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author craig
 */
@Path("/ConnectorTypes")
@Tag(name = "connectorTypes")
public class ConnectorTypeRoute extends BaseRoute {
    
    private static final Logger LOGGER = LogManager.getLogger(ConnectorTypeRoute.class.getName());
    
    @EJB
    ConnectorTypeFacade connectorTypeFacade; 
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ConnectorType> getConnectorTypeList() {
        LOGGER.debug("Fetching connector type list");
        return connectorTypeFacade.findAll();
    }
    
    @GET
    @Path("/ById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ConnectorType getConnectorTypeById(@PathParam("id") int id) throws ObjectNotFound {
        LOGGER.debug("Fetching connector type with id: " + id);
        ConnectorType connectorType = connectorTypeFacade.find(id);
        if (connectorType == null) {
            ObjectNotFound ex = new ObjectNotFound("Could not find connectorType with id: " + id);
            LOGGER.error(ex);
            throw ex; 
        }
        return connectorType;
    }
    
    @GET
    @Path("/ByName/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public ConnectorType getConnectorTypeByName(@PathParam("name") String name) throws ObjectNotFound {
        LOGGER.debug("Fetching connectorType with name: " + name);
        ConnectorType connectorType = connectorTypeFacade.findByName(name);
        if (connectorType == null) {
            ObjectNotFound ex = new ObjectNotFound("Could not find connectorType with name: " + name);
            LOGGER.error(ex);
            throw ex; 
        }
        return connectorType;
    }

    @POST
    @Path("/IdList")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Integer> getConnectorTypeIdList(@RequestBody(required = true) ConnectorTypeIdListRequest request) {
        List<String> nameList = request.getNameList();
        LOGGER.debug("Fetching list of connectorType id's by name list size: " + nameList.size());
        List<Integer> idList = new ArrayList<>(nameList.size());
        for (String name : nameList) {
            if ((name != null) && (!name.isBlank())) {
                ConnectorType connectorTypeItem = connectorTypeFacade.findByName(name);
                if (connectorTypeItem == null) {
                    // use 0 to indicate that there is no item with specified name
                    idList.add(0);
                } else {
                    idList.add(connectorTypeItem.getId());
                }
            } else {
                idList.add(0);
            }
        }
        return idList;
    }
}
