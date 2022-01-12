/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperInventory;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemCreateWizardController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemCreateWizardDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemEnforcedPropertiesController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemEnforcedPropertiesDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainInventorySettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainInventoryControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainInventoryLazyDataModel;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import gov.anl.aps.cdb.portal.view.objects.InventoryItemElementConstraintInformation;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
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
public class ItemDomainInventoryController extends ItemDomainInventoryBaseController<ItemDomainInventoryControllerUtility, ItemDomainInventory, ItemDomainInventoryFacade, ItemDomainInventorySettings> {

    public static final String ITEM_DOMAIN_INVENTORY_STATUS_PROPERTY_TYPE_NAME = "Component Instance Status";

    private static final String DEFAULT_DOMAIN_NAME = ItemDomainName.inventory.getValue();
    private final String DEFAULT_DOMAIN_DERIVED_FROM_ITEM_DOMAIN_NAME = "Catalog";

    private static final Logger logger = LogManager.getLogger(ItemDomainInventoryController.class.getName());   

    private ItemDomainInventoryLazyDataModel itemDomainInventoryLazyDataModel = null;

    @EJB
    private ItemDomainInventoryFacade itemDomainInventoryFacade;

    public boolean isInventory(Item item) {
        return item instanceof ItemDomainInventory;
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
    public void resetListDataModel() {
        super.resetListDataModel();
        itemDomainInventoryLazyDataModel = null;
    }

    @Override
    public DataModel getListDataModel() {
        return getItemDomainInventoryLazyDataModel();
    }

    @Override
    protected Boolean fetchFilterablePropertyValue(Integer propertyTypeId) {
        return true;
    }

    public ItemDomainInventoryLazyDataModel getItemDomainInventoryLazyDataModel() {
        if (itemDomainInventoryLazyDataModel == null) {
            itemDomainInventoryLazyDataModel = new ItemDomainInventoryLazyDataModel(itemDomainInventoryFacade, getDefaultDomain());
        }
        return itemDomainInventoryLazyDataModel;
    }

    public boolean isInventoryDomainItem(Item item) {
        return item.getDomain().getName().equals(getDefaultDomainName());
    }

    public void createSaveFromDialog(String onSuccessCommand) {
        String result = create();
        if (result != null) {
            SessionUtility.executeRemoteCommand(onSuccessCommand);
        }
    }

    public void createCancelFromDialog() {
        if (getCurrent() != null) {
            ItemDomainCatalog catalogItem = getCurrent().getCatalogItem();
            if (catalogItem != null) {
                catalogItem.getDerivedFromItemList().remove(getCurrent());
            }
            setCurrent(null);
        }
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
    public ItemElementConstraintInformation loadItemElementConstraintInformation(ItemElement itemElement) {
        return new InventoryItemElementConstraintInformation(itemElement);
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
        formatInfo.add(new ImportExportFormatInfo("Basic Inventory Format", ImportHelperInventory.class));

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
}
