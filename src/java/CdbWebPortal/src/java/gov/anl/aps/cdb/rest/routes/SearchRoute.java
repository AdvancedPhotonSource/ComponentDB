/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.routes;

import gov.anl.aps.cdb.common.exceptions.InvalidRequest;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemCategoryControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableCatalogControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableDesignControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableInventoryControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCatalogControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainInventoryControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainLocationControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMAARCControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemElementControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemTypeControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.PropertyTypeCategoryControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.PropertyTypeControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.SourceControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.UserGroupControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.UserInfoControllerUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.rest.entities.SearchEntitiesOptions;
import gov.anl.aps.cdb.rest.entities.SearchEntitiesResults;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.LinkedList;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author darek
 */
@Path("/Search")
@Tag(name = "Search")
public class SearchRoute {

    @POST
    @Path("/Entities")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public SearchEntitiesResults searchEntities(@RequestBody(required = true) SearchEntitiesOptions searchEntitiesOptions) throws InvalidRequest {
        SearchEntitiesResults results = new SearchEntitiesResults();
        String searchText = searchEntitiesOptions.getSearchText();

        if (searchText == null) {
            throw new InvalidRequest("Search text must be specified.");
        }

        if (searchEntitiesOptions.isIncludeCatalog()) {
            ItemDomainCatalogControllerUtility catalogControllerUtility = new ItemDomainCatalogControllerUtility();
            LinkedList<SearchResult> catalogResults = catalogControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainCatalogResults(catalogResults);
        }
        if (searchEntitiesOptions.isIncludeCableCatalog()) {
            ItemDomainCableCatalogControllerUtility cableCatalogControllerUtility = new ItemDomainCableCatalogControllerUtility();
            LinkedList<SearchResult> cableCatalogResults = cableCatalogControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainCableCatalogResults(cableCatalogResults);
        }
        if (searchEntitiesOptions.isIncludeInventory()) {
            ItemDomainInventoryControllerUtility inventoryControllerUtility = new ItemDomainInventoryControllerUtility();
            LinkedList<SearchResult> inventoryResults = inventoryControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainInventoryResults(inventoryResults);
        }
        if (searchEntitiesOptions.isIncludeCableInventory()) {
            ItemDomainCableInventoryControllerUtility cableInventoryControllerUtility = new ItemDomainCableInventoryControllerUtility();
            LinkedList<SearchResult> cableInventoryResults = cableInventoryControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainCableInventoryResults(cableInventoryResults);
        }
        if (searchEntitiesOptions.isIncludeCableDesign()) {
            ItemDomainCableDesignControllerUtility cableDesignControllerUtility = new ItemDomainCableDesignControllerUtility();
            LinkedList<SearchResult> cableDesignResults = cableDesignControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainCableDesignResults(cableDesignResults);
        }
        if (searchEntitiesOptions.isIncludeMachineDesign()) {
            ItemDomainMachineDesignControllerUtility machineDesignControllerUtility = new ItemDomainMachineDesignControllerUtility();
            LinkedList<SearchResult> machineDesignResults = machineDesignControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainMachineDesignResults(machineDesignResults);
        }
        if (searchEntitiesOptions.isIncludeItemLocation()) {
            ItemDomainLocationControllerUtility locationControllerUtility = new ItemDomainLocationControllerUtility();
            LinkedList<SearchResult> locationResults = locationControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainLocationResults(locationResults);
        }
        if (searchEntitiesOptions.isIncludeMAARC()) {
            ItemDomainMAARCControllerUtility maarcControllerUtility = new ItemDomainMAARCControllerUtility();
            LinkedList<SearchResult> maarcResults = maarcControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainMAARCResults(maarcResults);
        }
        if (searchEntitiesOptions.isIncludeItemElement()) {
            ItemElementControllerUtility itemElementControllerUtility = new ItemElementControllerUtility();
            LinkedList<SearchResult> itemElementResults = itemElementControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainCatalogResults(itemElementResults);
        }
        if (searchEntitiesOptions.isIncludeItemType()) {
            ItemTypeControllerUtility itemTypeControllerUtility = new ItemTypeControllerUtility();
            LinkedList<SearchResult> itemTypeResults = itemTypeControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainCatalogResults(itemTypeResults);
        }
        if (searchEntitiesOptions.isIncludeItemCategoy()) {
            ItemCategoryControllerUtility itemCategoryControllerUtility = new ItemCategoryControllerUtility();
            LinkedList<SearchResult> itemCategoryResults = itemCategoryControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainCatalogResults(itemCategoryResults);
        }
        if (searchEntitiesOptions.isIncludePropertyType()) {
            PropertyTypeControllerUtility propertyTypeControllerUtility = new PropertyTypeControllerUtility();
            LinkedList<SearchResult> propertyTypeResults = propertyTypeControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainCatalogResults(propertyTypeResults);
        }
        if (searchEntitiesOptions.isIncludePropertyTypeCategory()) {
            PropertyTypeCategoryControllerUtility propertyTypeCategoryControllerUtility = new PropertyTypeCategoryControllerUtility();
            LinkedList<SearchResult> propertyTypeCategoryResults = propertyTypeCategoryControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainCatalogResults(propertyTypeCategoryResults);
        }
        if (searchEntitiesOptions.isIncludeSource()) {
            SourceControllerUtility sourceControllerUtility = new SourceControllerUtility();
            LinkedList<SearchResult> sourceResults = sourceControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainCatalogResults(sourceResults);
        }
        if (searchEntitiesOptions.isIncludeUser()) {
            UserInfoControllerUtility userControllerUtility = new UserInfoControllerUtility();
            LinkedList<SearchResult> userResults = userControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainCatalogResults(userResults);
        }
        if (searchEntitiesOptions.isIncludeUserGroup()) {
            UserGroupControllerUtility userGroupControllerUtility = new UserGroupControllerUtility();
            LinkedList<SearchResult> userGroupResults = userGroupControllerUtility.performEntitySearch(searchText, true);
            results.setItemDomainCatalogResults(userGroupResults);
        }

        return results;
    }
}
