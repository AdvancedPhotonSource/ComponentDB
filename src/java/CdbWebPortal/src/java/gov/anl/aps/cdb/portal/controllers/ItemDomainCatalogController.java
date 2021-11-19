/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperCatalog;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperCatalogAssembly;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainCatalogSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCatalogControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperCatalogPorts;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.jsf.beans.SparePartsBean;
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

/**
 *
 * @author djarosz
 */
@Named(ItemDomainCatalogController.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainCatalogController extends ItemDomainCatalogBaseController<ItemDomainCatalogControllerUtility, ItemDomainCatalog, ItemDomainCatalogFacade, ItemDomainCatalogSettings> {

    private static final Logger logger = LogManager.getLogger(ItemDomainCatalogController.class.getName());

    public final static String CONTROLLER_NAMED = "itemDomainCatalogController";

    @EJB
    ItemDomainCatalogFacade itemDomainCatalogFacade;

    @Override
    public ItemMultiEditController getItemMultiEditController() {
        return ItemMultiEditDomainCatalogController.getInstance();
    }

    public ItemDomainCatalogController() {
        super();
    }

    public static ItemDomainCatalogController getInstance() {
        return (ItemDomainCatalogController) SessionUtility.findBean(ItemDomainCatalogController.CONTROLLER_NAMED);
    }

    @Override
    protected ItemDomainCatalogFacade getEntityDbFacade() {
        return itemDomainCatalogFacade;
    }

    @Override
    protected ItemDomainCatalogSettings createNewSettingObject() {
        return new ItemDomainCatalogSettings(this);
    }

    @Override
    public String getDisplayItemConnectorName() {
        return "port";
    }
    
    public List<ItemDomainInventory> getInventorySparesList() {
        ItemDomainCatalog current = getCurrent();
        List<ItemDomainInventory> inventorySparesList = current.getInventorySparesList();
        if (inventorySparesList == null) {
            inventorySparesList = new ArrayList<>();
            for (ItemDomainInventory inventoryItem : current.getInventoryItemList()) {
                if (inventoryItem.getSparePartIndicator()) {
                    inventorySparesList.add(inventoryItem);
                }
            }
            current.setInventorySparesList(inventorySparesList);
        }
        return inventorySparesList;
    }

    public List<ItemDomainInventory> getInventoryNonSparesList() {
        ItemDomainCatalog current = getCurrent();
        List<ItemDomainInventory> inventoryNonSparesList = current.getInventoryNonSparesList();
        if (inventoryNonSparesList == null) {
            ItemDomainCatalog currentItem = getCurrent();
            if (currentItem != null) {
                List<ItemDomainInventory> spareItems = getInventorySparesList();
                List<ItemDomainInventory> allInventoryItems = getCurrent().getInventoryItemList();
                inventoryNonSparesList = new ArrayList<>(allInventoryItems);
                inventoryNonSparesList.removeAll(spareItems);
            }
            current.setInventoryNonSparesList(inventoryNonSparesList);
        }
        return inventoryNonSparesList;
    }

    public int getInventorySparesCount() {
        List<ItemDomainInventory> sparesList = getInventorySparesList();
        if (sparesList != null) {
            return sparesList.size();
        }
        return 0;
    }

    public void notifyUserIfMinimumSparesReachedForCurrent() {
        int sparesMin = SparePartsBean.getSparePartsMinimumForItem(getCurrent());
        if (sparesMin == -1) {
            // Either an error occured or no spare parts configuration was found.
            return;
        } else {
            int sparesCount = getInventorySparesCount();
            if (sparesCount < sparesMin) {
                String sparesMessage;
                sparesMessage = "You now have " + sparesCount;
                if (sparesCount == 1) {
                    sparesMessage += " spare";
                } else {
                    sparesMessage += " spares";
                }

                sparesMessage += " but require a minumum of " + sparesMin;

                SessionUtility.addWarningMessage("Spares Warning", sparesMessage);
            }
        }

    }

    public int getInventoryNonSparesCount() {
        List<ItemDomainInventory> nonSparesList = getInventoryNonSparesList();
        if (nonSparesList != null) {
            return nonSparesList.size();
        }
        return 0;
    }

    public Boolean getDisplayInventorySpares() {
        ItemDomainCatalog current = getCurrent();
        Boolean displayInventorySpares = current.getDisplayInventorySpares();
        if (displayInventorySpares == null) {
            displayInventorySpares = SparePartsBean.isItemContainSparePartConfiguration(getCurrent());
            current.setDisplayInventorySpares(displayInventorySpares);
        }
        return displayInventorySpares;
    }

    @Override
    protected ItemDomainCatalogControllerUtility createControllerUtilityInstance() {
        return new ItemDomainCatalogControllerUtility();
    }

    // <editor-fold defaultstate="collapsed" desc="import/export support">   

    @Override
    public boolean getEntityDisplayImportButton() {
        return true;
    }

    @Override
    protected DomainImportExportInfo initializeDomainImportInfo() {

        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();
        formatInfo.add(new ImportExportFormatInfo(
                "Basic Catalog Create/Update Format", ImportHelperCatalog.class));
        formatInfo.add(new ImportExportFormatInfo(
                "Catalog Assembly Create Format", ImportHelperCatalogAssembly.class));
        formatInfo.add(new ImportExportFormatInfo(
                "Catalog Ports Create/Update/Delete Format", ImportHelperCatalogPorts.class));

        String completionUrl = "/views/itemDomainCatalog/list?faces-redirect=true";

        return new DomainImportExportInfo(formatInfo, completionUrl);
    }

    @Override
    public boolean getEntityDisplayExportButton() {
        return true;
    }
    
    @Override
    protected DomainImportExportInfo initializeDomainExportInfo() {
        
        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();
        
        formatInfo.add(
                new ImportExportFormatInfo(
                        "Basic Catalog Create/Update Format",  ImportHelperCatalog.class));
        formatInfo.add(new ImportExportFormatInfo(
                "Catalog Ports Create/Update/Delete Format", ImportHelperCatalogPorts.class));
        
        String completionUrl = "/views/itemDomainCatalog/list?faces-redirect=true";
        
        return new DomainImportExportInfo(formatInfo, completionUrl);
    }
    
    // </editor-fold>   
}
