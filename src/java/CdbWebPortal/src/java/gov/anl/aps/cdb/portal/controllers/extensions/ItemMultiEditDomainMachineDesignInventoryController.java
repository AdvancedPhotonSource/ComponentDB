/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignInventoryController;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.AuthorizationUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
@Named(ItemMultiEditDomainMachineDesignInventoryController.controllerNamed)
@SessionScoped
public class ItemMultiEditDomainMachineDesignInventoryController extends ItemMultiEditLocatableItemController implements Serializable {

    public final static String controllerNamed = "itemMultiEditDomainMachineDesignInventoryController";

    Integer unitCount = null;

    protected boolean updateLocation = false;
    protected boolean updateLocationDetails = false;
    protected String toggledLocationEditViewUUID = null;

    protected boolean updateInventoryStatus = false;

    private ItemDomainMachineDesignInventoryController itemDomainMachineDesignInventoryController = null;

    public ItemDomainMachineDesignInventoryController getItemDomainMachineDesignInventoryController() {
        if (itemDomainMachineDesignInventoryController == null) {
            itemDomainMachineDesignInventoryController = ItemDomainMachineDesignInventoryController.getInstance();
        }
        return itemDomainMachineDesignInventoryController;
    }

    @Override
    protected ItemController getItemController() {
        return getItemDomainMachineDesignInventoryController();
    }

    @Override
    protected String getControllerNamedConstant() {
        return controllerNamed;
    }

    public boolean isCurrentViewIsTemplate() {
        return getItemDomainMachineDesignInventoryController().isCurrentViewIsTemplate();
    }

    public String getMdNodeRepIcon(ItemElement ie) {
        return getItemDomainMachineDesignInventoryController().getMdNodeRepIcon(ie);
    }

    public static ItemMultiEditDomainMachineDesignInventoryController getInstance() {
        return (ItemMultiEditDomainMachineDesignInventoryController) SessionUtility.findBean(controllerNamed);
    }

    @Override
    public void resetMultiEditVariables() {
        super.resetMultiEditVariables();
        unitCount = null;
    }

    public TreeNode getMachineDesignTemplateRootTreeNode() {
        return getItemDomainMachineDesignInventoryController().getMachineDesignTemplateRootTreeNode();
    }

    public void createInventoryFromTemplateSelected(NodeSelectEvent nodeSelection) {
        ItemDomainMachineDesignInventoryController itemDomainMachineDesignInventoryController = getItemDomainMachineDesignInventoryController();
        itemDomainMachineDesignInventoryController.templateToCreateNewItemSelected(nodeSelection);
        derivedFromItemForNewItems = itemDomainMachineDesignInventoryController.getTemplateToCreateNewItem();
 
    }

    @Override
    public Item createItemEntity() {
        ItemDomainMachineDesignInventoryController itemDomainMachineDesignInventoryController = getItemDomainMachineDesignInventoryController();
        EntityInfo newItemEntityInfo = getNewItemEntityInfo();
        UserInfo ownerUser = newItemEntityInfo.getOwnerUser();
        UserGroup ownerUserGroup = newItemEntityInfo.getOwnerUserGroup();
        if (derivedFromItemForNewItems instanceof ItemDomainMachineDesign) {
            ItemDomainMachineDesign template = (ItemDomainMachineDesign) derivedFromItemForNewItems;
            ItemDomainMachineDesign item = itemDomainMachineDesignInventoryController.performPrepareCreateInventoryFromTemplate(template, ownerUser, ownerUserGroup);
            return item; 
        } else {
            return null; 
        }
    } 

    @Override
    protected List<Item> getEditableItemsForCurrentNonAdminUser() {
        List<Item> itemList = getItemList();
        UserInfo user = (UserInfo) SessionUtility.getUser();
        
        List<Item> editableItems = new ArrayList<>(); 
        
        for (Item item : itemList) {
            EntityInfo entityInfo = item.getEntityInfo();
            if (AuthorizationUtility.isEntityWriteableByUser(entityInfo, user)) {
                editableItems.add(item); 
            }
        }
        
        return editableItems; 
    }

    @Override
    protected boolean checkCreateConfig() {
        if (derivedFromItemForNewItems == null) {
            SessionUtility.addErrorMessage("No Template Selected", "Please select a machine template item.");
            return false; 
        }
        return true; 
    }      
}
