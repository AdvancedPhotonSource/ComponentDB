/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.rest.entities.CableCatalogItemInfo;
import gov.anl.aps.cdb.rest.entities.ItemDomainCableCatalogIdListRequest;
import gov.anl.aps.cdb.rest.entities.ItemDomainMachineDesignIdListRequest;
import gov.anl.aps.cdb.rest.entities.MachineDesignItemInfo;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Duration;
import java.time.Instant;
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
    @EJB
    ItemDomainMachineDesignFacade machineDesignFacade;
    
    @POST
    @Path("/CableCatalogIdList")
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
    @Path("/CableCatalogInfoList")
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

    private List<ItemDomainMachineDesign> itemsWithContainerHierarchy(String rootItemName, String containerItemName, String itemName) {
        List<ItemDomainMachineDesign> resultList = new ArrayList<>();
        List<ItemDomainMachineDesign> itemList = machineDesignFacade.findByName(itemName);
        if (itemList.isEmpty()) {
            itemList = machineDesignFacade.findByAlternateName(itemName);
        }
        for (ItemDomainMachineDesign item : itemList) {

            // walk up hierarchy to top-level "root" parent
            ItemDomainMachineDesign parentItem = item.getParentMachineDesign();
            boolean foundContainer = false;
            boolean foundRoot = false;
            while (parentItem != null) {

                // check container match
                String alternateName = parentItem.getAlternateName();
                if (alternateName == null) {
                    alternateName = "";
                }
                if ((parentItem.getName().equals(containerItemName))
                        || (alternateName.equals(containerItemName))) {
                    foundContainer = true;
                }

                // check root match
                if ((parentItem.getParentMachineDesign() == null)
                        && (parentItem.getName().equals(rootItemName))) {
                    foundRoot = true;
                }

                parentItem = parentItem.getParentMachineDesign();

                if (foundContainer && foundRoot) {
                    resultList.add(item);
                    break;
                }
            }
        }
        return resultList;
    }

    @POST
    @Path("/MachineInfoList")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<MachineDesignItemInfo> getMachineInfoList(@RequestBody(required = true) ItemDomainMachineDesignIdListRequest request) throws InvalidArgument {

        Instant start = Instant.now();

        List<String> itemNames = request.getItemNames();
        List<String> rackNames = request.getRackNames();
        String rootItemName = request.getRootName();

        if ((rootItemName == null) || (rootItemName.isBlank())) {
            throw new InvalidArgument("must specify root item name");
        }

        if (itemNames.size() != rackNames.size()) {
            throw new InvalidArgument("list sizes must match for item and rack names");
        }

        LOGGER.debug("Fetching list of machine item info by name list size: "
                + itemNames.size());

        List<MachineDesignItemInfo> infoList = new ArrayList<>(itemNames.size());
        for (int listIndex = 0; listIndex < itemNames.size(); listIndex++) {
            String itemName = itemNames.get(listIndex);
            String containerItemName = rackNames.get(listIndex);
            if (((itemName != null) && (!itemName.isBlank()))
                    && ((containerItemName != null) && (!containerItemName.isBlank()))) {

                List<ItemDomainMachineDesign> mdItems = itemsWithContainerHierarchy(
                        rootItemName, containerItemName, itemName);
                switch (mdItems.size()) {
                    case 1:
                        // one matching item
                        ItemDomainMachineDesign mdItem = mdItems.get(0);
                        infoList.add(new MachineDesignItemInfo(mdItem.getId(), mdItem.getCatalogItemName()));
                        break;
                    case 0:
                        // no matching items
                        infoList.add(new MachineDesignItemInfo(0, null));
                        break;
                    default:
                        // multiple matching items
                        infoList.add(new MachineDesignItemInfo(-1, null));
                        break;
                }
            } else {
                infoList.add(new MachineDesignItemInfo(0, null));
            }
        }

        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);
        LOGGER.debug("Duration: " + elapsed.toSeconds());

        return infoList;
    }

}
