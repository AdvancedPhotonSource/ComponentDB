/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperBase;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author craigmcc
 */
@Named(ImportExportInfoController.controllerNamed)
@ApplicationScoped
public class ImportExportInfoController {
    
    public static final String controllerNamed = "importExportInfoController";
    Map<String, DomainImportExportInfo> importInfoMap = new LinkedHashMap<>();
    
    @PostConstruct
    public void initialize() {
        List<CdbEntityController> controllerList = new ArrayList<>();
        controllerList.add(ItemDomainCatalogController.getInstance());
        controllerList.add(ItemDomainInventoryController.getInstance());
        controllerList.add(ItemDomainMachineDesignController.getInstance());
        controllerList.add(ItemDomainMachineDesignInventoryController.getInstance());
        controllerList.add(ItemDomainCableCatalogController.getInstance());
        controllerList.add(ItemDomainCableInventoryController.getInstance());
        controllerList.add(ItemDomainCableDesignController.getInstance());
        controllerList.add(ItemCategoryController.getInstance());
        controllerList.add(ItemTypeController.getInstance());
        controllerList.add(ItemDomainLocationController.getInstance());
        controllerList.add(SourceController.getInstance());
        controllerList.add(ConnectorTypeController.getInstance());
        controllerList.add(UserInfoController.getInstance());
        controllerList.add(UserGroupController.getInstance());
        for (CdbEntityController controller : controllerList) {
            String domainName = controller.getControllerUtility().getDisplayEntityTypeName();
            DomainImportExportInfo importInfo = controller.getDomainImportInfo();
            this.registerImportInfo(domainName, importInfo);
        }
    }

    public static ImportExportInfoController getInstance() {
        return (ImportExportInfoController) SessionUtility.findBean(controllerNamed);
    }

    public void registerImportInfo(String domainDisplayString, DomainImportExportInfo importInfo) {
        importInfoMap.put(domainDisplayString, importInfo);         
    }
    
    public Set<String> getDomains() {
        return importInfoMap.keySet();
    }
    
    public List<ImportExportFormatInfo> getImportFormatsForDomain(String domain) {
        if (!importInfoMap.containsKey(domain)) {
            return null;
        }
        return importInfoMap.get(domain).getFormatInfoList();
    }
    
    public StreamedContent downloadTemplate(ImportExportFormatInfo importFormatInfo) {
        System.out.println("downloading: " + importFormatInfo.getFormatName());
        try {
                Class helperClass = importFormatInfo.getHelperClass();
                ImportHelperBase importHelper = (ImportHelperBase) helperClass.newInstance();
                return importHelper.getTemplateExcelFile();
        } catch (InstantiationException | IllegalAccessException ex) {

        }
        return null;
    }

}
