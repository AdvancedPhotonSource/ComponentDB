/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditDomainMachineDesignInventoryController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignInventorySettings;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignInventoryControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperMachineInventory;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableStatusItem;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemStatusUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import gov.anl.aps.cdb.portal.view.objects.KeyValueObject;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.NodeSelectEvent;

@Named(ItemDomainMachineDesignInventoryController.controllerNamed)
@SessionScoped
public class ItemDomainMachineDesignInventoryController extends ItemDomainMachineDesignBaseController<ItemDomainMachineDesignTreeNode, ItemDomainMachineDesignInventoryControllerUtility> implements IItemStatusController {

    public final static String controllerNamed = "itemDomainMachineDesignInventoryController";
    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesignInventoryController.class.getName());

    private final static String pluginItemMachineDesignSectionsName = "itemMachineDesignInventoryDetailsViewSections";

    @Override
    public void createListDataModel() {
        List<ItemDomainMachineDesign> itemList = getAllObjectList();
        ListDataModel listDataModel = new ListDataModel(itemList);
        setListDataModel(listDataModel);
    }

    public boolean isCurrentTopLevel() {
        ItemDomainMachineDesign current = getCurrent();

        if (current != null) {
            List<ItemElement> itemElementMemberList = current.getItemElementMemberList();
            List<ItemElement> itemElementMemberList2 = current.getItemElementMemberList2();

            return itemElementMemberList.isEmpty() && itemElementMemberList2.isEmpty();
        }

        return false;
    }

    public String getSubassemblyPageTitle() {
        String title = "Preassembled Machine: ";
        if (getCurrent() != null) {
            ItemDomainMachineDesign current = getCurrent();

            while (current.getParentMachineDesign() != null) {
                current = current.getParentMachineDesign();
            }

            title += current;
        }

        return title;
    }

    @Override
    protected String getViewPath() {
        return "/views/itemDomainMachineDesignInventory/view.xhtml";
    }

    @Override
    public String getItemListPageTitle() {
        return "Preassembled Machine Elements (defined by Machine Templates)";
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return true;
    }

    @Override
    public boolean isDisplayRowExpansionForItem(Item item) {
        return super.isDisplayRowExpansionForItem(item); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        return ItemDomainName.machineDesign.getValue();
    }

    @Override
    public DataModel getTopLevelMachineDesignSelectionList() {
        ItemDomainMachineDesign current = getCurrent();
        DataModel topLevelMachineDesignSelectionList = current.getTopLevelMachineDesignSelectionList();

        if (topLevelMachineDesignSelectionList == null) {
            List<ItemDomainMachineDesign> topLevelMachineDesignInventory = itemDomainMachineDesignFacade.getTopLevelMachineDesignInventory();

            removeTopLevelParentOfItemFromList(current, topLevelMachineDesignInventory);

            topLevelMachineDesignSelectionList = new ListDataModel(topLevelMachineDesignInventory);
            current.setTopLevelMachineDesignSelectionList(topLevelMachineDesignSelectionList);
        }

        return topLevelMachineDesignSelectionList;
    }

    public static ItemDomainMachineDesignInventoryController getInstance() {
        return (ItemDomainMachineDesignInventoryController) SessionUtility.findBean(controllerNamed);
    }

    public void prepareCreateInventoryFromTemplate(ItemDomainMachineDesign template) {
        ItemDomainMachineDesign currentForCurrentData = getCurrentForCurrentData();
        currentForCurrentData.setNewMdInventoryItem(performPrepareCreateInventoryFromTemplate(template));
    }

    public ItemDomainMachineDesign performPrepareCreateInventoryFromTemplate(ItemDomainMachineDesign template) {
        return performPrepareCreateInventoryFromTemplate(template, null, null);
    }

    public ItemDomainMachineDesign performPrepareCreateInventoryFromTemplate(
            ItemDomainMachineDesign template,
            UserInfo ownerUser,
            UserGroup ownerGroup) {

        ItemDomainMachineDesign mdInventory = null;

        try {
            ItemDomainMachineDesignInventoryControllerUtility controllerUtility = getControllerUtility();
            if (ownerUser == null) {
                ownerUser = SessionUtility.getUser();
            }
            if (ownerGroup == null) {                
                ownerGroup = ownerUser.getUserGroupList().get(0);
            }

            List<KeyValueObject> machineDesignNameList = getMachineDesignNameList();
            mdInventory = controllerUtility.createMachineDesignFromTemplateHierachically(null, template, ownerUser, ownerGroup, machineDesignNameList);
        } catch (CdbException | CloneNotSupportedException ex) {
            LOGGER.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
            return null;
        }

        List<Item> inventoryForCurrentTemplate = template.getDerivedFromItemList();
        int unitNum = inventoryForCurrentTemplate.size() + 1;
        mdInventory.setName(ItemDomainInventory.generatePaddedUnitName(unitNum));

        template.getDerivedFromItemList().add(mdInventory);

        UserInfo user = SessionUtility.getUser();
        getControllerUtility().assignInventoryAttributes(mdInventory, template, user);

        return mdInventory;
    }

    public void createInventoryFromTemplateSelected(NodeSelectEvent nodeSelection) {
        templateToCreateNewItemSelected(nodeSelection);
        prepareCreateInventoryFromTemplate(templateToCreateNewItem);
    }

    public void createInventoryFromDialog() {
        create();
    }

    @Override
    public String create() {
        ItemDomainMachineDesign newMdInventoryItem = getNewMdInventoryItem();
        if (newMdInventoryItem != null) {
            setCurrent(newMdInventoryItem);
            return super.create();
        } else {
            SessionUtility.addWarningMessage("No machine template selected", "Select machine template to continue.");
            return null;
        }
    }

    @Override
    public List<ItemDomainMachineDesign> getDefaultTopLevelMachineList() {
        ItemDomainMachineDesign md = getCurrent();
        ItemDomainMachineDesign parentMachineDesign = md.getParentMachineDesign();
        while (parentMachineDesign != null) {
            md = parentMachineDesign;
            parentMachineDesign = parentMachineDesign.getParentMachineDesign();
        }

        md = findById(md.getId());
        List<ItemDomainMachineDesign> parentList = new ArrayList<>();
        parentList.add(md);
        return parentList;
    }

    @Override
    protected boolean resetFiltersOnPreRenderList() {
        ItemDomainMachineDesign current = getCurrent();
        return current != null;
    }

    @Override
    protected void prepareEntityView(ItemDomainMachineDesign entity) {
        processPreRenderList();
        if (isMdInventory(entity)) {
            loadViewModeUrlParameter();
        }
    }

    @Override
    public String currentDualViewList() {
        setCurrentFlash();
        resetListConfigurationVariables();
        return view();
    }

    @Override
    protected ItemDomainMachineDesign performItemRedirection(ItemDomainMachineDesign item, String paramString, boolean forceRedirection) {
        if (isMdInventory(item)) {
            setCurrent(item);
            prepareView(item);
            resetListDataModel();
            return item;
        }

        // Do default action. 
        return super.performItemRedirection(item, paramString, forceRedirection); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ItemDomainMachineDesignSettings createNewSettingObject() {
        return new ItemDomainMachineDesignInventorySettings(this);
    }

    private boolean isMdInventory(ItemDomainMachineDesign item) {
        if (item instanceof ItemDomainMachineDesign) {
            if (isInventory(item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemMultiEditController getItemMultiEditController() {
        return ItemMultiEditDomainMachineDesignInventoryController.getInstance();
    }

    public ItemDomainMachineDesign getNewMdInventoryItem() {
        ItemDomainMachineDesign currentForCurrentData = getCurrentForCurrentData();
        return currentForCurrentData.getNewMdInventoryItem();
    }

    @Override
    public String getPluginItemMachineDesignSectionsName() {
        return pluginItemMachineDesignSectionsName;
    }

    @Override
    public void prepareEditInventoryStatus() {
        ItemDomainMachineDesign current = getCurrent();
        UserInfo user = SessionUtility.getUser();
        getControllerUtility().prepareEditInventoryStatus(current, user);
    }

    @Override
    public PropertyValue getCurrentStatusPropertyValue() {
        return getControllerUtility().getItemStatusPropertyValue(getCurrent());
    }

    @Override
    public final PropertyType getInventoryStatusPropertyType() {
        return getControllerUtility().getInventoryStatusPropertyType();
    }

    @Override
    public boolean getRenderedHistoryButton() {
        return ItemStatusUtility.getRenderedHistoryButton(this);
    }

    public PropertyValue getItemStatusPropertyValue(LocatableStatusItem item) {
        return ItemStatusUtility.getItemStatusPropertyValue(item);
    }

    @Override
    public boolean getEntityDisplayImportButton() {
        return true;
    }

    @Override
    protected DomainImportExportInfo initializeDomainImportInfo() {

        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();
        formatInfo.add(new ImportExportFormatInfo("Basic Machine Inventory Format", ImportHelperMachineInventory.class));

        String completionUrl = "/views/itemDomainMachineDesignInventory/list?faces-redirect=true";

        return new DomainImportExportInfo(formatInfo, completionUrl);
    }

    public String deletedItemsList() {
        return "/views/itemDomainMachineDesign/deletedItemsList?faces-redirect=true";
    }

    @Override
    protected ItemDomainMachineDesignInventoryControllerUtility getControllerUtility() {
        return new ItemDomainMachineDesignInventoryControllerUtility();
    }

    /**
     * Executes move to trash operation invoked from confirmation dialog.
     * Invokes base implementation, and then redirects to the machine inventory
     * list view if the root item in the item view is moved to trash.
     */
    @Override
    public void moveToTrash() {
        ItemDomainMachineDesign item = findById(getCurrent().getId());
        boolean isTopLevelItem = (item.getParentMachineDesign() == null);
        ItemDomainMachineDesign rootItem = item;
        while (rootItem.getParentMachineDesign() != null) {
            rootItem = rootItem.getParentMachineDesign();
        }
        super.moveToTrash();
        if (isTopLevelItem && (item.getIsItemDeleted())) {
            // if we deleted root item in item view, redirect to machine inventory list view
            SessionUtility.navigateTo("list.xhtml?faces-redirect=true");
        } else {
            setCurrent(rootItem);
        }
    }

    @Override
    protected ItemDomainMachineDesignInventoryControllerUtility createControllerUtilityInstance() {
        return new ItemDomainMachineDesignInventoryControllerUtility();
    }

    @Override
    public ItemDomainMachineDesignTreeNode loadMachineDesignRootTreeNode(List<ItemDomainMachineDesign> itemsWithoutParents) {
        ItemDomainMachineDesignTreeNode rootTreeNode = new ItemDomainMachineDesignTreeNode(itemsWithoutParents, getDefaultDomain(), getEntityDbFacade());

        return rootTreeNode;
    }

    @Override
    public ItemDomainMachineDesignTreeNode createMachineTreeNodeInstance() {
        return new ItemDomainMachineDesignTreeNode();
    }
}
