/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import static gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignInventoryController.controllerNamed;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignDeletedItemSettings;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author craig
 */
@Named(ItemDomainMachineDesignDeletedItemsController.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainMachineDesignDeletedItemsController extends ItemDomainMachineDesignController {

    public final static String CONTROLLER_NAMED = "itemDomainMachineDesignDeletedItemsController";
    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesignDeletedItemsController.class.getName());
    
    private final static String pluginItemMachineDesignSectionsName = "itemMachineDesignDetailsViewSections";

    @Override
    public String getItemListPageTitle() {
        return "Deleted Machine Elements";
    }

    @Override
    public boolean isDisplayRowExpansionForItem(Item item) {
        return super.isDisplayRowExpansionForItem(item); //To change body of generated methods, choose Tools | Templates.
    }

    public static ItemDomainMachineDesignInventoryController getInstance() {
        if (SessionUtility.runningFaces()) {
            return (ItemDomainMachineDesignInventoryController) SessionUtility.findBean(controllerNamed);
        } else {
            return null;
        }
    }

    @Override
    protected ItemDomainMachineDesignSettings createNewSettingObject() {
        return new ItemDomainMachineDesignDeletedItemSettings(this);
    }

    public String getPluginItemMachineDesignSectionsName() {
        return pluginItemMachineDesignSectionsName; 
    }

    @Override
    public void createListDataModel() {
        List<ItemDomainMachineDesign> itemList = getItemList();
        ListDataModel newListDataModel = new ListDataModel(itemList);
        setListDataModel(newListDataModel);
    }
    
    @Override
    public List<ItemDomainMachineDesign> getItemList() {
        return itemDomainMachineDesignFacade.getDeletedItems();
    }

    @Override
    public TreeNode loadMachineDesignRootTreeNode(Boolean isTemplate) {
        TreeNode rootTreeNode = new DefaultTreeNode();
        List<ItemDomainMachineDesign> itemsWithoutParents
                = getItemsWithoutParents();

        for (Item item : itemsWithoutParents) {
            if (item.getIsItemDeleted()) {
                expandTreeChildren(item, rootTreeNode);
            }
        }

        return rootTreeNode;
    }
    
    @Override
    public String list() {
        return "deletedItemsList.xhtml?faces-redirect=true";
    }
    
    @Override
    public String viewForCurrentEntity() {
        return "viewDeletedItem?id=" + current.getId() + "&faces-redirect=true";
    }

    @Override
    protected void prepareEntityView(ItemDomainMachineDesign entity) {
        if ((entity != null) && (entity.getIsItemDeleted())) {
            loadViewModeUrlParameter();
            return;
        }
        super.prepareEntityView(entity);
    }

    @Override
    protected ItemDomainMachineDesign performItemRedirection(ItemDomainMachineDesign item, String paramString, boolean forceRedirection) {
        if ((item != null) && (item.getIsItemDeleted())) {
            setCurrent(item);
            prepareView(item);
            return item;
        }

        // Do default action. 
        return super.performItemRedirection(item, paramString, forceRedirection); //To change body of generated methods, choose Tools | Templates.
    }

}
