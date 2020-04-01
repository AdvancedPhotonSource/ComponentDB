/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author craig
 */
public class ImportHelperCableDesign extends ImportHelperBase<ItemDomainCableDesign, ItemDomainCableDesignController> {


    protected static String completionUrlValue = "/views/itemDomainCableDesign/list?faces-redirect=true";
    
    @Override
    protected void createColumnModels_() {
        columns.add(new ImportHelperBase.StringColumnModel("Name", "name", "setName", true, "Cable name, uniquely identifies cable in CDB. Embedded '#cdbid# tag will be replaced with the internal CDB identifier (integer).", 128));
        columns.add(new ImportHelperBase.StringColumnModel("Alt Name", "alternateName", "setAlternateName", false, "Alternate cable name. Embedded '#cdbid# tag will be replaced with the internal CDB identifier (integer).", 32));
        columns.add(new ImportHelperBase.StringColumnModel("Ext Cable Name", "externalCableName", "setExternalCableName", false, "Cable name in external system (e.g., CAD, routing tool) e.g., SR_R_401_D1109_RR8G[low] | SR_M_A02_C61_64_02-00[high]", 256));
        columns.add(new ImportHelperBase.StringColumnModel("Import Cable ID", "importCableId", "setImportCableId", false, "Import cable identifier.", 256));
        columns.add(new ImportHelperBase.StringColumnModel("Alternate Cable ID", "alternateCableId", "setAlternateCableId", false, "Alternate (e.g., group-specific) cable identifier.", 256));
        columns.add(new ImportHelperBase.StringColumnModel("Description", "description", "setDescription", true, "Description of cable.", 256));
        columns.add(new ImportHelperBase.StringColumnModel("Laying", "laying", "setLaying", false, "Laying style e.g., S=single-layer, M=multi-layer, T=triangular, B=bundle", 256));
        columns.add(new ImportHelperBase.StringColumnModel("Voltage", "voltage", "setVoltage", false, "Voltage aplication e.g., COM=communication, CTRL=control, IW=instrumentation, LV=low voltage, MV=medium voltage", 256));
        columns.add(new ImportHelperBase.IdRefColumnModel("Owner", "team", "setTeamId", true, "Numeric ID of CDB technical system.", 0, ItemCategoryController.getInstance()));
        columns.add(new ImportHelperBase.IdRefColumnModel("Project", "itemProjectString", "setProjectId", true, "Numeric ID of CDB project.", 0, ItemProjectController.getInstance()));
        columns.add(new ImportHelperBase.IdRefColumnModel("Type", "catalogItemString", "setCatalogItemId", true, "Numeric ID of CDB cable type catalog item.", 0, ItemDomainCableCatalogController.getInstance()));
        columns.add(new ImportHelperBase.IdRefColumnModel("Endpoint1", "endpoint1String", "setEndpoint1Id", true, "Numeric ID of CDB machine design item for first endpoint.", 0, ItemDomainMachineDesignController.getInstance()));
        columns.add(new ImportHelperBase.StringColumnModel("Endpoint1 Desc", "endpoint1Description", "setEndpoint1Description", false, "Endpoint details useful for external editing.", 256));
        columns.add(new ImportHelperBase.IdRefColumnModel("Endpoint2", "endpoint2String", "setEndpoint2Id", true, "Numeric ID of CDB machine design item for second endpoint.", 0, ItemDomainMachineDesignController.getInstance()));
        columns.add(new ImportHelperBase.StringColumnModel("Endpoint2 Desc", "endpoint2Description", "setEndpoint2Description", false, "Endpoint details useful for external editing.", 256));
    }
    
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    public ItemDomainCableDesignController getEntityController() {
        return ItemDomainCableDesignController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Cable Design Template";
    }
    
    @Override
    protected ItemDomainCableDesign createEntityInstance() {
        return getEntityController().createEntityInstance();
    }
    
    /*
    * Replaces occurrences of "#cdbid# in the name field with a unique id during
    * import, that we will replace in postImport with the object's cdb id.
    */
    @Override
    protected String postParse(ItemDomainCableDesign e, String id) {
        
        String idPattern = "#cdbid#";
        String replacePattern = "#cdbid-" + id + "#";
        
        if (e.getName().contains(idPattern)) {
            String nameValue = e.getName().replaceAll(idPattern, replacePattern);
            e.setName(nameValue);
        }
        
        return "";
    }

    /*
    * Finds strings matching the pattern "#cdbid*#" in the name and alt name fields
    * and replace them with the internal cdb identifier.
    */
    @Override
    protected String postImport() {
        
        String idRegexPattern = "#cdbid[^#]*#";
        idRegexPattern = Matcher.quoteReplacement(idRegexPattern);
        Pattern pattern = Pattern.compile(idRegexPattern);
        
        List<ItemDomainCableDesign> updatedRows = new ArrayList<>();
        for (ItemDomainCableDesign cable : rows) {
            
            boolean updated = false;
            
            Matcher nameMatcher = pattern.matcher(cable.getName());
            if (nameMatcher.find()) {
                updated = true;
                String nameValue = cable.getName().replaceAll(idRegexPattern, String.valueOf(cable.getId()));
                cable.setName(nameValue);
            }
            
            Matcher altNameMatcher = pattern.matcher(cable.getAlternateName());
            if (altNameMatcher.find()) {
                updated = true;
                String altNameValue = cable.getAlternateName().replaceAll(idRegexPattern, String.valueOf(cable.getId()));
                cable.setAlternateName(altNameValue);
            }
            
            if (updated) {
                updatedRows.add(cable);
            }
        }
        
        String message = "";
        if (updatedRows.size() > 0) {
            try {
                getEntityController().updateList(updatedRows);
                message = "Updated attributes for " + updatedRows.size() + " instance(s).";
            } catch (CdbException | RuntimeException ex) {
                Throwable t = ExceptionUtils.getRootCause(ex);
                message = "Post commit attribute update failed. Additional action required to update name and alternate name fields. " + ex.getMessage() + ": " + t.getMessage() + ".";
            }
        }
        
        return message;
    }
}
