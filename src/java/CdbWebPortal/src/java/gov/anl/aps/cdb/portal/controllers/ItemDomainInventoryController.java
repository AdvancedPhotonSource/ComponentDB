/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.constants.CdbRole;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperInventory;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemCreateWizardController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemCreateWizardDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemEnforcedPropertiesController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemEnforcedPropertiesDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditDomainInventoryController;
import gov.anl.aps.cdb.portal.view.objects.InventoryMachineAssignmentSelection;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainInventorySettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainInventoryControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignBaseControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainInventoryLazyDataModel;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.AuthorizationUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author djarosz
 */
@Named("itemDomainInventoryController")
@SessionScoped
public class ItemDomainInventoryController extends ItemDomainInventoryBaseController<ItemDomainInventoryControllerUtility, ItemDomainInventory, ItemDomainInventoryFacade, ItemDomainInventorySettings, ItemDomainInventoryLazyDataModel> {

    public static final String ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME = "Component Instance Status";

    private static final String DEFAULT_DOMAIN_NAME = ItemDomainName.inventory.getValue();
    private final String DEFAULT_DOMAIN_DERIVED_FROM_ITEM_DOMAIN_NAME = "Catalog";

    private static final Logger logger = LogManager.getLogger(ItemDomainInventoryController.class.getName());

    @EJB
    private ItemDomainInventoryFacade itemDomainInventoryFacade;

    private final InventoryMachineAssignmentSelection machineAssignmentSelection
            = new InventoryMachineAssignmentSelection();

    public boolean isInventory(Item item) {
        return item instanceof ItemDomainInventory;
    }

    public InventoryMachineAssignmentSelection getMachineAssignmentSelection() {
        return machineAssignmentSelection;
    }

    public ItemDomainMachineDesignTreeNode getMachineAssignmentTree() {
        return machineAssignmentSelection.getTree();
    }

    public ItemDomainMachineDesignTreeNode getSelectedMachineAssignmentNode() {
        return machineAssignmentSelection.getSelectedNode();
    }

    public void setSelectedMachineAssignmentNode(ItemDomainMachineDesignTreeNode node) {
        machineAssignmentSelection.setSelectedNode(node);
        machineAssignmentSelection.setReplacementConfirmation(false);
    }

    public boolean isMachineAssignmentInstalled() {
        return machineAssignmentSelection.isInstalled();
    }

    public void setMachineAssignmentInstalled(boolean installed) {
        machineAssignmentSelection.setInstalled(installed);
    }

    public boolean isMachineAssignmentReplacementConfirmation() {
        return machineAssignmentSelection.isReplacementConfirmation();
    }

    public void setMachineAssignmentReplacementConfirmation(boolean confirmation) {
        machineAssignmentSelection.setReplacementConfirmation(confirmation);
    }

    public void prepareMachineAssignment() {
        resetMachineAssignmentState();
        ItemDomainMachineDesign assignedMachine = getAssignedMachine();
        if (assignedMachine != null) {
            machineAssignmentSelection.selectMachine(assignedMachine);
            machineAssignmentSelection.setInstalled(assignedMachine.isIsHoused());
        }
    }

    public void cancelMachineAssignment() {
        resetMachineAssignmentState();
        reloadCurrent();
    }

    public void saveMachineAssignment() {
        saveMachineAssignment(false);
    }

    public void confirmMachineAssignmentReplacement() {
        machineAssignmentSelection.resetReplacementProgress();
        saveMachineAssignment(true);
    }

    public void cancelMachineAssignmentReplacement() {
        machineAssignmentSelection.setReplacementConfirmation(false);
    }

    public void completeMachineAssignmentReplacement() {
        finishMachineAssignmentUpdate();
    }

    public Item getSelectedMachineCurrentAssignment() {
        ItemDomainMachineDesign machine = machineAssignmentSelection.getSelectedMachine();
        return machine == null ? null : machine.getAssignedItem();
    }

    public Item getSelectedMachineCurrentCatalog() {
        return getCatalogItem(getSelectedMachineCurrentAssignment());
    }

    public Item getCurrentInventoryCatalog() {
        return getCatalogItem(getCurrent());
    }

    public boolean isCurrentAssignedToMachine() {
        return getAssignedMachine() != null;
    }

    public boolean isSourceMachineReleaseRequired() {
        ItemDomainMachineDesign sourceMachine = getAssignedMachine();
        ItemDomainMachineDesign destinationMachine = machineAssignmentSelection.getSelectedMachine();
        return sourceMachine != null && !sourceMachine.equals(destinationMachine);
    }

    public void clearMachineAssignment() {
        ItemDomainMachineDesign machine = getAssignedMachine();
        if (machine == null) {
            SessionUtility.addInfoMessage("No update", "Inventory item is not assigned to a machine element.");
            return;
        }
        if (!verifyMachineAssignmentPermission(machine)) {
            return;
        }
        try {
            logger.info("Clearing inventory {} from machine {}", getCurrent(), machine);
            updateMachineAssignment(machine, getCatalogItem(machine.getAssignedItem()), null);
            finishMachineAssignmentUpdate();
        } catch (CdbException | RuntimeException ex) {
            logger.error("Could not clear inventory machine assignment", ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
        }
    }

    private void saveMachineAssignment(boolean replacementConfirmed) {
        ItemDomainMachineDesign machine = machineAssignmentSelection.getSelectedMachine();
        if (machine == null) {
            SessionUtility.addErrorMessage("Invalid Selection", "Select a machine element to continue.");
            return;
        }
        if (!verifyMachineAssignmentPermission(machine)) {
            return;
        }
        if (!replacementConfirmed
                && requiresCatalogReplacementConfirmation(machine)) {
            machineAssignmentSelection.setReplacementConfirmation(true);
            SessionUtility.executeRemoteCommand("showMachineAssignmentReplacementConfirmDialog()");
            return;
        }

        ItemDomainMachineDesign previousMachine = getAssignedMachine();
        if (previousMachine != null && !previousMachine.equals(machine)
                && !verifyMachineAssignmentPermission(previousMachine)) {
            return;
        }

        boolean previousMachineCleared = false;
        boolean previousMachineInstalled = previousMachine != null && previousMachine.isIsHoused();
        try {
            if (replacementConfirmed) {
                logger.info("Replacing catalog assignment on destination machine {}", machine);
                updateMachineAssignment(machine, getCurrentInventoryCatalog(), null);
                machineAssignmentSelection.setDestinationPrepared(true);
                SessionUtility.addInfoMessage("Machine Assignment", "Updated destination machine catalog assignment.");
            }
            if (previousMachine != null && !previousMachine.equals(machine)) {
                logger.info("Releasing inventory {} from source machine {}", getCurrent(), previousMachine);
                updateMachineAssignment(previousMachine,
                        getCatalogItem(previousMachine.getAssignedItem()), null);
                previousMachineCleared = true;
                machineAssignmentSelection.setSourceReleased(true);
                SessionUtility.addInfoMessage("Machine Assignment", "Released inventory from source machine element.");
                reloadCurrent();
            }
            logger.info("Assigning inventory {} to destination machine {}", getCurrent(), machine);
            updateMachineAssignment(machine, getCurrent(),
                    machineAssignmentSelection.isInstalled());
            machineAssignmentSelection.setInventoryAssigned(true);
            SessionUtility.addInfoMessage("Machine Assignment", "Assigned inventory to destination machine element.");
            if (replacementConfirmed) {
                ItemDomainMachineDesignController.getInstance().resetListDataModel();
                reloadCurrent();
            } else {
                finishMachineAssignmentUpdate();
            }
        } catch (CdbException | RuntimeException ex) {
            logger.error("Could not update inventory machine assignment", ex);
            machineAssignmentSelection.setReplacementError(ex.getMessage());
            if (previousMachineCleared) {
                restoreMachineAssignment(previousMachine, previousMachineInstalled);
            }
            if (!replacementConfirmed) {
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }
        }
    }

    private boolean verifyMachineAssignmentPermission(ItemDomainMachineDesign machine) {
        UserInfo user = SessionUtility.getUser();
        boolean permitted = user != null && (SessionUtility.getRole() == CdbRole.ADMIN
                || (AuthorizationUtility.isEntityWriteableByUser(getCurrent(), user)
                && AuthorizationUtility.isEntityWriteableByUser(machine, user)));
        if (!permitted) {
            SessionUtility.addErrorMessage("Error", "Write permission is required for both the inventory item and machine element.");
        }
        return permitted;
    }

    private void restoreMachineAssignment(ItemDomainMachineDesign machine, boolean installed) {
        try {
            updateMachineAssignment(machine, getCurrent(), installed);
        } catch (CdbException | RuntimeException ex) {
            logger.error("Could not restore previous inventory machine assignment", ex);
            SessionUtility.addErrorMessage("Error", "The previous machine assignment could not be restored.");
        }
    }

    private void updateMachineAssignment(ItemDomainMachineDesign machine, Item assignment,
            Boolean installed) throws CdbException {
        ItemDomainMachineDesignBaseControllerUtility utility = machine.getItemControllerUtility();
        utility.updateAssignedItem(machine, assignment, SessionUtility.getUser(), installed);
        utility.update(machine, SessionUtility.getUser());
    }

    private ItemDomainMachineDesign getAssignedMachine() {
        for (ItemElement element : getCurrent().getItemElementMemberList2()) {
            Item parent = element.getParentItem();
            if (parent instanceof ItemDomainMachineDesign) {
                ItemDomainMachineDesign machine = (ItemDomainMachineDesign) parent;
                if (getCurrent().equals(machine.getAssignedItem())) {
                    return machine;
                }
            }
        }
        return null;
    }

    private Item getCatalogItem(Item item) {
        return item instanceof ItemDomainInventory ? item.getDerivedFromItem() : item;
    }

    private boolean requiresCatalogReplacementConfirmation(ItemDomainMachineDesign machine) {
        Item assignment = machine.getAssignedItem();
        if (assignment == null || assignment.equals(getCurrent())) {
            return false;
        }
        Item assignedCatalog = getCatalogItem(assignment);
        return assignedCatalog == null || !assignedCatalog.equals(getCurrentInventoryCatalog());
    }

    private void finishMachineAssignmentUpdate() {
        ItemDomainMachineDesignController.getInstance().resetListDataModel();
        resetMachineAssignmentState();
        reloadCurrent();
        SessionUtility.executeRemoteCommand("machineAssignmentUpdateSuccess()");
    }

    public void resetMachineAssignmentState() {
        machineAssignmentSelection.reset();
    }

    @Override
    protected ItemDomainInventoryFacade getEntityDbFacade() {
        return itemDomainInventoryFacade;
    }

    @Override
    protected ItemDomainInventorySettings createNewSettingObject() {
        return new ItemDomainInventorySettings(this);
    }

    @Override
    protected String generatePaddedUnitName(int itemNumber) {
        return ItemDomainInventory.generatePaddedUnitName(itemNumber);
    }

    public static ItemDomainInventoryController getInstance() {
        return (ItemDomainInventoryController) findDomainController(DEFAULT_DOMAIN_NAME);
    }

    @Override
    protected ItemCreateWizardController getItemCreateWizardController() {
        return ItemCreateWizardDomainInventoryController.getInstance();
    }

    @Override
    public ItemMultiEditController getItemMultiEditController() {
        return ItemMultiEditDomainInventoryController.getInstance();
    }

    @Override
    public ItemEnforcedPropertiesController getItemEnforcedPropertiesController() {
        return ItemEnforcedPropertiesDomainInventoryController.getInstance();
    } 

    public boolean isCollapsedRelatedMAARCItemsForCurrent() {
        return getRelatedMAARCRelationshipsForCurrent().size() < 1;
    }

    public List<ItemElementRelationship> getRelatedMAARCRelationshipsForCurrent() {
        ItemDomainInventory current = getCurrent();
        List<ItemElementRelationship> relatedMAARCRelationshipsForCurrent = current.getRelatedMAARCRelationshipsForCurrent();
        if (relatedMAARCRelationshipsForCurrent == null) {
            relatedMAARCRelationshipsForCurrent = ItemDomainMAARCController.getRelatedMAARCRelationshipsForItem(getCurrent());
        }

        return relatedMAARCRelationshipsForCurrent;
    }

    @Override
    public List<ItemDomainInventory> getItemListWithProject(ItemProject itemProject) {
        String projectName = itemProject.getName();
        return itemDomainInventoryFacade.findByDomainAndProjectOrderByDerivedFromItem(getDefaultDomainName(), projectName);
    }

    @Override
    public List<EntityType> getFilterableEntityTypes() {
        return getDefaultDomainDerivedFromDomain().getAllowedEntityTypeList();
    } 

    @Override
    public ItemDomainInventoryLazyDataModel createItemLazyDataModel() {
        return new ItemDomainInventoryLazyDataModel(itemDomainInventoryFacade, getDefaultDomain(), settingObject);
    }

    @Override
    protected Boolean fetchFilterablePropertyValue(Integer propertyTypeId) {
        return true;
    }

    public boolean isInventoryDomainItem(Item item) {
        return item.getDomain().getName().equals(getDefaultDomainName());
    }

    @Override
    public String prepareView(ItemDomainInventory item) {
        resetBOMSupportVariables();
        return super.prepareView(item); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String prepareEdit(ItemDomainInventory inventoryItem) {
        resetBOMSupportVariables();
        setCurrent(inventoryItem);
        return super.prepareEdit(inventoryItem);
    } 

    @Override
    protected void completeEntityUpdate(ItemDomainInventory entity) {
        super.completeEntityUpdate(entity); 
        
        List<ItemElement> itemElementMemberList = entity.getItemElementMemberList2();
        
        for (ItemElement element : itemElementMemberList) {
            Item parentItem = element.getParentItem();
            if (parentItem instanceof ItemDomainMachineDesign) {
                // Make sure the machine hierarchy is updated to reflect the changes made to inventory item. 
                ItemDomainMachineDesignController instance = ItemDomainMachineDesignController.getInstance();
                instance.resetListDataModel();
                break;
            }
        }
    }       

    @Override
    public boolean isShowCloneCreateItemElementsPlaceholdersOption() {
        // Item elements should match the assembly. User has no control over that.
        return false;
    }

    @Override
    public String prepareCloneForItemToClone() {
        // Item elements should match the assembly. User has no control over that.
        cloneCreateItemElementPlaceholders = true;

        return super.prepareCloneForItemToClone();
    }

    @Override
    public String getPrimaryImageValueForItem(Item item) {
        String result = super.getPrimaryImageValueForItem(item);
        if (result.equals("")) {
            Item catalogItem = item.getDerivedFromItem();
            if (catalogItem != null) {
                return super.getPrimaryImageValueForItem(catalogItem);
            }
        }
        return result;
    }

    @Override
    protected DomainImportExportInfo initializeDomainImportInfo() {

        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();
        formatInfo.add(new ImportExportFormatInfo("Basic Inventory Create/Update/Delete Format", ImportHelperInventory.class));

        String completionUrl = "/views/itemDomainInventory/list?faces-redirect=true";

        return new DomainImportExportInfo(formatInfo, completionUrl);
    }

    @Override
    public boolean getEntityDisplayExportButton() {
        return true;
    }

    @Override
    protected DomainImportExportInfo initializeDomainExportInfo() {

        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();

        formatInfo.add(new ImportExportFormatInfo("Basic Inventory Create/Update/Delete Format", ImportHelperInventory.class));

        String completionUrl = "/views/itemDomainInventory/list?faces-redirect=true";

        return new DomainImportExportInfo(formatInfo, completionUrl);
    }

    @Override
    public String getDefaultDomainName() {
        return DEFAULT_DOMAIN_NAME;
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return true;
    } 

    @Override
    public Boolean getDisplayMembershipByData() {
        return true; 
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        return DEFAULT_DOMAIN_DERIVED_FROM_ITEM_DOMAIN_NAME;
    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }

    @Override
    protected ItemDomainInventoryControllerUtility createControllerUtilityInstance() {
        return new ItemDomainInventoryControllerUtility();
    }
    
    @Override
    protected ItemDomainInventory performItemRedirection(ItemDomainInventory item, String paramString, boolean forceRedirection) {
        // Special redirect for qrid 
        if (paramString.contains("qrId")) {
            
            ItemDomainInventoryControllerUtility utility = getControllerUtility();
            List<Item> parentItemList = utility.getParentItemList(item);
            
            for (Item parentItem : parentItemList) {
                if (parentItem instanceof ItemDomainMachineDesign) {               
                    String listForEntity = ItemDomainMachineDesignController.listForEntity((ItemDomainMachineDesign) parentItem); 
                    ItemDomainMachineDesignController mdc = ItemDomainMachineDesignController.getInstance();
                    String domainPath = mdc.getDomainPath();

                    SessionUtility.addInfoMessage("Machine Redirect", "Redirected " + paramString + " to installed in machine element. Use link in 'Assigned Item' column for inventory details.");
                    SessionUtility.navigateTo(domainPath + "/" + listForEntity);
                    return null;
                }           
                
            }
        }
        
        return super.performItemRedirection(item, paramString, forceRedirection); 
    }

}
