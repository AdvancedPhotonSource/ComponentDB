/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperCableCatalog;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainCableCatalogSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableCatalogControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperCableCatalogConnectors;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainCableCatalogController.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainCableCatalogController extends ItemDomainCatalogBaseController<ItemDomainCableCatalogControllerUtility, ItemDomainCableCatalog, ItemDomainCableCatalogFacade, ItemDomainCableCatalogSettings> {
    
    public static final String CONTROLLER_NAMED = "itemDomainCableCatalogController";
    
    @EJB
    ItemDomainCableCatalogFacade itemDomainCableCatalogFacade;
    
    public static ItemDomainCableCatalogController getInstance() {
        if (SessionUtility.runningFaces()) {
            return (ItemDomainCableCatalogController) SessionUtility.findBean(ItemDomainCableCatalogController.CONTROLLER_NAMED);
        } else {
            // TODO add apiInstance
            return null;
        }
    }
    
    @Override
    public ItemMultiEditController getItemMultiEditController() {
        return ItemMultiEditDomainCableCatalogController.getInstance();
    }    

    @Override
    protected ItemDomainCableCatalogSettings createNewSettingObject() {
        return new ItemDomainCableCatalogSettings(this);
    }

    @Override
    protected ItemDomainCableCatalogFacade getEntityDbFacade() {
        return itemDomainCableCatalogFacade; 
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.cableCatalog.getValue(); 
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return false; 
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        return "Cable Inventory";
    }

    @Override
    public String getStyleName() {
        return "catalog"; 
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        return ItemDomainName.cableInventory.getValue(); 
    } 
    
    @Override
    public boolean getEntityDisplayItemConnectors() {
        return true; 
    }    

    @Override
    public boolean getEntityDisplayConnectorCableEndDesignation() {
        return true; 
    }    

    @Override
    protected ItemDomainCableCatalogControllerUtility createControllerUtilityInstance() {
        return new ItemDomainCableCatalogControllerUtility(); 
    }
    
    protected void initializeItemConnector(ItemConnector itemConnector) {
        itemConnector.getConnector().setCableEndDesignation(Connector.DEFAULT_CABLE_END_DESIGNATION);
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
                "Basic Cable Catalog Create/Update Format", ImportHelperCableCatalog.class));
        
        formatInfo.add(new ImportExportFormatInfo(
                "Cable Catalog Connectors Create Format", ImportHelperCableCatalogConnectors.class));
        
        String completionUrl = "/views/itemDomainCableCatalog/list?faces-redirect=true";
        
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
                new ImportExportFormatInfo("Basic Cable Catalog Create/Update Format", 
                        ImportHelperCableCatalog.class));
        
        String completionUrl = "/views/itemDomainCableCatalog/list?faces-redirect=true";
        
        return new DomainImportExportInfo(formatInfo, completionUrl);
    }
    
    // </editor-fold>   
}
