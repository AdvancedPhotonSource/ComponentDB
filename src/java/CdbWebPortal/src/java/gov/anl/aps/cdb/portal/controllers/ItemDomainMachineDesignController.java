/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import gov.anl.aps.cdb.portal.controllers.extensions.CableWizardClient;
import gov.anl.aps.cdb.portal.model.ItemGenericLazyDataModel;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainMachineDesignController.controllerNamed)
@SessionScoped
public class ItemDomainMachineDesignController extends ItemDomainMachineDesignBaseController<ItemDomainMachineDesignTreeNode, ItemDomainMachineDesignControllerUtility, ItemGenericLazyDataModel, ItemDomainMachineDesignSettings> implements CableWizardClient {

    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesignController.class.getName());

    public final static String controllerNamed = "itemDomainMachineDesignController";
    
    @Override
    public ItemGenericLazyDataModel createItemLazyDataModel() {
        return new ItemGenericLazyDataModel(getEntityDbFacade(), getDefaultDomain(), settingObject);
    }

    @Override
    protected ItemDomainMachineDesignSettings createNewSettingObject() {
        return new ItemDomainMachineDesignSettings(this);
    }

    public static ItemDomainMachineDesignController getInstance() {
        return (ItemDomainMachineDesignController) SessionUtility.findBean(controllerNamed);
    }

    public boolean isShowAssemblyElements() {
        ItemDomainMachineDesignTreeNode.MachineTreeConfiguration config = getCurrentMachineDesignListRootTreeNode().getConfig();
        return config.isShowAssemblyElements();
    }

    public void setShowAssemblyElements(boolean showAssemblyElements) {
        ItemDomainMachineDesignTreeNode.MachineTreeConfiguration config = getCurrentMachineDesignListRootTreeNode().getConfig();
        config.setShowAssemblyElements(showAssemblyElements);
    }

    public boolean isCablesShown() {
        ItemDomainMachineDesignTreeNode.MachineTreeConfiguration config = getCurrentMachineDesignListRootTreeNode().getConfig();
        return config.isShowCables();
    }

    public void setCablesShown(boolean cablesShown) {
        ItemDomainMachineDesignTreeNode.MachineTreeConfiguration config = getCurrentMachineDesignListRootTreeNode().getConfig();
        config.setShowCables(cablesShown);
    }

    @Override
    public void processPreRenderList() {
        super.processPreRenderList();

        String paramValue = SessionUtility.getRequestParameterValue("id");

        if (paramValue != null) {
            Integer idParam = Integer.parseInt(paramValue);
            ItemDomainMachineDesign result = itemDomainMachineDesignFacade.find(idParam);

            if (result != null) {
                ItemDomainMachineDesignTreeNode machineDesignTreeRootTreeNode = getMachineDesignTreeRootTreeNode();
                expandToSpecificMachineDesignItem(machineDesignTreeRootTreeNode, result);
            }
        }
    }

    public void createRepresentingMachineForAssemblyElement() {
        ItemDomainMachineDesignTreeNode selectedItemInListTreeTable = getSelectedItemInListTreeTable();

        // After update the parent should be selected 
        ItemDomainMachineDesignTreeNode parent = (ItemDomainMachineDesignTreeNode) selectedItemInListTreeTable.getParent();
        ItemElement machineElement = parent.getElement();
        this.selectedItemInListTreeTable = parent;

        ItemDomainMachineDesign parentMachine = (ItemDomainMachineDesign) machineElement.getContainedItem();
        ItemElement element = selectedItemInListTreeTable.getElement();
        UserInfo user = SessionUtility.getUser();

        try {
            ItemDomainMachineDesign newMachine = getControllerUtility().createRepresentingMachineForAssemblyElement(parentMachine, element, user);
            setCurrent(newMachine);
        } catch (CdbException ex) {
            LOGGER.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            return;
        }

        create();

        resetListConfigurationVariables();
        resetListDataModel();
        expandToSelectedTreeNodeAndSelect();
    }

    @Override
    public void unlinkAssignedItemFromMachineElement(ItemElement element) {
        ItemDomainMachineDesign mdItem = (ItemDomainMachineDesign) element.getContainedItem();

        Item assignedItem = mdItem.getAssignedItem();

        Item catalogItem = null;

        if (assignedItem instanceof ItemDomainInventory) {
            catalogItem = ((ItemDomainInventory) assignedItem).getCatalogItem();
        } else if (assignedItem instanceof ItemDomainCatalog) {
            catalogItem = assignedItem;
        }

        if (catalogItem != null) {
            List<ItemElement> itemElementDisplayList = catalogItem.getItemElementDisplayList();
            if (itemElementDisplayList.size() > 0) {
                List<ItemElement> machineElementList = mdItem.getItemElementDisplayList();
                for (ItemElement machineElement : machineElementList) {
                    Item containedItem = machineElement.getContainedItem();
                    ItemElement selfElement = containedItem.getSelfElement();
                    ItemElement representsItemElement = selfElement.getRepresentsItemElement();
                    if (representsItemElement != null) {
                        // Rep element set, stop unlink assigned item.
                        SessionUtility.addWarningMessage("Cannot unlink", "Catalog assembly is represented in the children machine nodes.", true);
                        return;
                    }
                }
            }
        }

        // Validation passed continue with update. 
        super.unlinkAssignedItemFromMachineElement(element);
    }

    @Override
    protected void performMoveToTrashOperationsForItem(ItemDomainMachineDesign item) {
        super.performMoveToTrashOperationsForItem(item);

        // When moved to trash, it cannot represent a catalog element.
        item.setRepresentsCatalogElement(null);
    }

    public String navigateToParentAssemblyOfSelectedTreeNode() {
        updateCurrentUsingSelectedItemInTreeTable();
        ItemDomainMachineDesign current = getCurrent();
        ItemDomainMachineDesign parentMachineDesign = current.getParentMachineDesign();

        Item assignedItem = parentMachineDesign.getAssignedItem();

        if (assignedItem == null) {
            SessionUtility.addErrorMessage("Error", "Could not locate a parent assembly.", true);
            return null;
        }

        if (assignedItem instanceof ItemDomainCatalog) {
            SessionUtility.addWarningMessage("Assign assmbly inventory first", "Parent needs to be an inventory assembly to proceed.", true);
            return null;
        }

        if (assignedItem instanceof ItemDomainInventory) {
            return super.prepareView(assignedItem);
        }

        SessionUtility.addErrorMessage("Error", "Unknown error occurred.", true);
        return null;
    }

    public String showInControlHierarchyForSelectedTreeNode() {
        updateCurrentUsingSelectedItemInTreeTable();

        ItemDomainMachineDesign item = getCurrent();

        // Verify if has control relationship..
        int relationshipId = ItemElementRelationshipTypeNames.control.getDbId();
        List<ItemDomainMachineDesign> machines = itemDomainMachineDesignFacade.fetchRelationshipParentItems(item.getId(), relationshipId);

        if (machines.size() == 0) {
            SessionUtility.addErrorMessage("No control relationship",
                    "This item does not show up in control hierarchy.");
            return null;
        }

        if (item != null) {
            String redirect = "/views/itemDomainMachineDesignControl/list";
            redirect += "?id=" + item.getId() + "&faces-redirect=true";
            return redirect;
        }

        SessionUtility.addErrorMessage("Error", "Cannot load details for a non machine design.");
        return null;
    }

    @Override
    protected ItemDomainMachineDesignControllerUtility createControllerUtilityInstance() {
        return new ItemDomainMachineDesignControllerUtility();
    }

    public void saveListFilterSettings() {
        ItemDomainMachineDesignTreeNode machineDesignTreeRootTreeNode = getMachineDesignTreeRootTreeNode();

        List<ItemDomainMachineDesign> topLevelItems = machineDesignTreeRootTreeNode.getTopLevelItems();
        String filteredMachineIds = settingObject.getFilterFilteredMachineIds();
        filteredMachineIds = "";
        int count = 0;
        for (ItemDomainMachineDesign machine : topLevelItems) {
            if (machine.isFilterMachineNode()) {
                filteredMachineIds += machine.getId() + ",";
                count++;
            }
        }
        if (count == topLevelItems.size()) {
            filteredMachineIds = "";
        }
        settingObject.setFilterFilteredMachineIds(filteredMachineIds);

        String nameFilter = machineDesignTreeRootTreeNode.getNameFilter();
        settingObject.setFilterByName(nameFilter);

        settingObject.saveListSettingsForSessionSettingEntityActionListener(null);
        SettingController instance = SettingController.getInstance();
        instance.saveSettingListForSettingEntity();
    }

    @Override
    public ItemDomainMachineDesignTreeNode loadMachineDesignRootTreeNode(List<ItemDomainMachineDesign> itemsWithoutParents) {
        // Load is filterable setting into the top level nodes.     
        String filteredMachineIds = settingObject.getFilterFilteredMachineIds();
        if (filteredMachineIds != null && filteredMachineIds.isBlank() == false) {
            String[] filterableNodeIds = filteredMachineIds.split(",");

            for (ItemDomainMachineDesign machineItem : itemsWithoutParents) {
                boolean filterable = false;
                for (String nodeIdStr : filterableNodeIds) {
                    int id = Integer.parseInt(nodeIdStr);

                    if (id == machineItem.getId()) {
                        filterable = true;
                        break;
                    }
                }

                machineItem.updateFilterMachineNode(filterable);
            }
        }

        ItemDomainMachineDesignTreeNode rootTreeNode = new ItemDomainMachineDesignTreeNode(itemsWithoutParents, getDefaultDomain(), getEntityDbFacade(), settingObject);

        String filterByName = settingObject.getFilterByName();
        if (filterByName != null) {
            rootTreeNode.setNameFilter(filterByName);
        }

        return rootTreeNode;
    }

    @Override
    public ItemDomainMachineDesignTreeNode createMachineTreeNodeInstance() {
        return new ItemDomainMachineDesignTreeNode();
    }

}
