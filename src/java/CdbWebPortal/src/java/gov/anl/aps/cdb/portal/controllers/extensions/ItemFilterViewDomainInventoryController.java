/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainLocationController;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainLocationControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainLocationFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemUtility;
import gov.anl.aps.cdb.portal.view.objects.FilterViewResultItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
@Named("itemFilterViewDomainInventoryController")
@SessionScoped
public class ItemFilterViewDomainInventoryController extends ItemFilterViewController implements Serializable {

    protected ListDataModel filterViewLocationDataModel = null;
    protected ItemDomainLocation filterViewLocationItemLoaded = null;
    protected boolean filterViewLocationDataModelLoaded = false;

    @EJB
    private ItemDomainLocationFacade itemDomainLocationFacade;

    ItemDomainInventoryController itemDomainController = null;

    @Override
    public ItemController getItemController() {
        if (itemDomainController == null) {
            itemDomainController = ItemDomainInventoryController.getInstance();
        }
        return itemDomainController;
    }

    @Override
    public void processPreRenderFilterViewPage() {
        super.processPreRenderFilterViewPage();

        filterViewLocationDataModelLoaded = false;
        filterViewLocationDataModel = null;
    }

    @Override
    protected void filterViewItemProjectChanged() {
        super.filterViewItemProjectChanged();
        filterViewLocationDataModelLoaded = false;
    }

    public boolean isFilterViewLocationDataModelNeedReloading(ItemDomainLocation newLocationItem) {
        if (filterViewLocationDataModel == null) {
            return true;
        }
        if (!filterViewLocationDataModelLoaded) {
            return true;
        }
        if (filterViewLocationItemLoaded != null) {
            if (!filterViewLocationItemLoaded.equals(newLocationItem)) {
                return true;
            }
        } else // last is null but new is not. 
        if (newLocationItem != null) {
            if (!newLocationItem.equals(filterViewLocationItemLoaded)) {
                return true;
            }
        }
        return false;
    }

    public void updateFilterViewLocationDataModelLoadedStatus(ItemDomainLocation lastLocationLoaded) {
        filterViewLocationDataModelLoaded = true;
        filterViewLocationItemLoaded = lastLocationLoaded;
    }

    public ListDataModel getFilterViewLocationDataModel() {
        ItemDomainLocationController locationController = ItemDomainLocationController.getInstance();
        ItemDomainLocation selection = locationController.getFilterViewLocationLastSelection();
        if (isFilterViewLocationDataModelNeedReloading(selection)) {
            updateFilterViewLocationDataModelLoadedStatus(selection);
            List<ItemDomainInventoryBase> itemList = new ArrayList<>();
            
            if (selection != null) {
                ItemDomainLocationControllerUtility util = new ItemDomainLocationControllerUtility(); 
                itemList = util.getInventoryLocatedInLocationHierarchically(selection, false);
            }
            filterViewLocationDataModel = createFilterViewListDataModel((List<Item>) (List<?>) itemList);            
        }

        return filterViewLocationDataModel;
    }

    @Override
    protected void prepareFilterViewResultItem(FilterViewResultItem fvio) {
        super.prepareFilterViewResultItem(fvio);
        Item inventoryItem = fvio.getItemObject();

        TreeNode rootTreeNode = ItemUtility.createNewTreeNode(inventoryItem, null);
        ItemDomainLocationController.addLocationRelationshipsToParentTreeNode(inventoryItem, rootTreeNode);
        if (rootTreeNode.getChildren().size() > 0) {
            fvio.addFilterViewItemExpansion(rootTreeNode, "Location For");
        }
    }
}
