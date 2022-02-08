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
import gov.anl.aps.cdb.portal.model.ItemDomainCatalogLazyDataModel;
import gov.anl.aps.cdb.portal.model.ItemLazyDataModel;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainCatalogController.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainCatalogController extends ItemDomainCatalogBaseController<ItemDomainCatalogControllerUtility, ItemDomainCatalog, ItemDomainCatalogFacade, ItemDomainCatalogSettings, ItemDomainCatalogLazyDataModel> {

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
    public String getControllerName() {
        return CONTROLLER_NAMED;
    }

    @Override
    protected ItemDomainCatalogFacade getEntityDbFacade() {
        return itemDomainCatalogFacade;
    } 

    @Override
    public ItemLazyDataModel createItemLazyDataModel() {
        return new ItemDomainCatalogLazyDataModel(itemDomainCatalogFacade, getDefaultDomain()); 
    }

    @Override
    protected ItemDomainCatalogSettings createNewSettingObject() {
        return new ItemDomainCatalogSettings(this);
    }

    @Override
    protected ItemDomainCatalogControllerUtility createControllerUtilityInstance() {
        return new ItemDomainCatalogControllerUtility();
    } 

    @Override
    public void prepareCreateSingleItemElementSimpleDialog() {
        super.prepareCreateSingleItemElementSimpleDialog(); 
        
        setItemElementSelectionController(this);
    }
    
    public ItemDomainCatalogBaseController getItemElementSelectionController() {
        ItemDomainCatalog current = getCurrent();
        return current.getItemElementSelectionController();
    }

    public void setItemElementSelectionController(ItemDomainCatalogBaseController itemElementSelectionController) {
        ItemDomainCatalog current = getCurrent();
        current.setItemElementSelectionController(itemElementSelectionController);        
    }
    
    public String getItemElementSelectionControllerString() {
        String controllerName = getItemElementSelectionController().getControllerName();
        return controllerName; 
    }

    public void setItemElementSelectionControllerString(String controllerName) {
        ItemDomainCatalogBaseController controller = (ItemDomainCatalogBaseController) SessionUtility.findBean(controllerName); 
        setItemElementSelectionController(controller);
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
                "Catalog Assembly Create/Update/Delete Format", ImportHelperCatalogAssembly.class));
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
                "Catalog Assembly Create/Update/Delete Format", ImportHelperCatalogAssembly.class));
        formatInfo.add(new ImportExportFormatInfo(
                "Catalog Ports Create/Update/Delete Format", ImportHelperCatalogPorts.class));
        
        String completionUrl = "/views/itemDomainCatalog/list?faces-redirect=true";
        
        return new DomainImportExportInfo(formatInfo, completionUrl);
    }
    
    // </editor-fold>   
}
