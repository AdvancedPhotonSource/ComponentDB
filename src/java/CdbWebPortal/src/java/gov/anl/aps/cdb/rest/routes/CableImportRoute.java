/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.rest.entities.CableCatalogItemInfo;
import gov.anl.aps.cdb.rest.entities.ItemDomainCableCatalogIdListRequest;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author craig
 */
@Path("/CableImport")
@Tag(name = "cableImport")
public class CableImportRoute {
    
    private static final Logger LOGGER = LogManager.getLogger(CableImportRoute.class.getName());

    @EJB
    ItemDomainCableCatalogFacade cableCatalogFacade; 
    
    @POST
    @Path("/IdList")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Integer> getCableTypeIdList(@RequestBody(required = true) ItemDomainCableCatalogIdListRequest request) {
        List<String> nameList = request.getNameList();
        LOGGER.debug("Fetching list of cable catalog id's by name list size: " + nameList.size());
        List<Integer> idList = new ArrayList<>();
        for (String name : nameList) {
            if ((name != null) && (!name.isBlank())) {
                List<ItemDomainCableCatalog> itemList = cableCatalogFacade.findByName(name);
                if (itemList == null || itemList.isEmpty()) {
                    // use 0 to indicate that there is no item with specified name
                    idList.add(0);
                } else if (itemList.size() > 1) {
                    // use -1 to indicate that there are multiple items with same name
                    idList.add(-1);
                } else {
                    idList.add(itemList.get(0).getId());
                }
            } else {
                idList.add(0);
            }
        }
        return idList;
    }

    @POST
    @Path("/ItemInfoList")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<CableCatalogItemInfo> getCableCatalogInfoList(@RequestBody(required = true) ItemDomainCableCatalogIdListRequest request) {
        List<String> nameList = request.getNameList();
        LOGGER.debug("Fetching list of cable catalog items by name list size: " + nameList.size());
        List<CableCatalogItemInfo> result = new ArrayList<>();
        for (String name : nameList) {
            if ((name != null) && (!name.isBlank())) {
                List<ItemDomainCableCatalog> itemList = cableCatalogFacade.findByName(name);
                if (itemList == null || itemList.isEmpty()) {
                    // use null to indicate that there is no item with specified name
                    result.add(null);
                } else if (itemList.size() > 1) {
                    // use -1 to indicate that there are multiple items with same name
                    result.add(null);
                } else {
                    CableCatalogItemInfo info = new CableCatalogItemInfo();
                    ItemDomainCableCatalog catalog = itemList.get(0);
                    info.setName(catalog.getName());
                    info.setId(catalog.getId());
                    if (catalog.getItemConnectorList() != null) {
                        for (ItemConnector connector : catalog.getItemConnectorList()) {
                            info.addConnectorName(connector.getConnectorName());
                        }
                    result.add(info);
                    }
                }
            } else {
                result.add(null);
            }
        }
        return result;
    }

}
