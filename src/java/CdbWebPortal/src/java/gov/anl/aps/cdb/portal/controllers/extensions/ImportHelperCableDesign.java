/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
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
    
    protected static String COLUMN_MODEL_NAME = "Name";
    
    @Override
    protected List<InputSpec> initializeInputSpecs_() {
        List<InputSpec> specs = new ArrayList<>();
        
        specs.add(new ImportHelperBase.StringInputSpec(COLUMN_MODEL_NAME, "setName", true, "Cable name, uniquely identifies cable in CDB. Embedded '#cdbid# tag will be replaced with the internal CDB identifier (integer).", 128));
        specs.add(new ImportHelperBase.StringInputSpec("Alt Name", "setAlternateName", false, "Alternate cable name. Embedded '#cdbid# tag will be replaced with the internal CDB identifier (integer).", 32));
        specs.add(new ImportHelperBase.StringInputSpec("Ext Cable Name", "setExternalCableName", false, "Cable name in external system (e.g., CAD, routing tool) e.g., SR_R_401_D1109_RR8G[low] | SR_M_A02_C61_64_02-00[high]", 256));
        specs.add(new ImportHelperBase.StringInputSpec("Import Cable ID", "setImportCableId", false, "Import cable identifier.", 256));
        specs.add(new ImportHelperBase.StringInputSpec("Alternate Cable ID", "setAlternateCableId", false, "Alternate (e.g., group-specific) cable identifier.", 256));
        specs.add(new ImportHelperBase.StringInputSpec("Description", "setDescription", false, "Description of cable.", 256));
        specs.add(new ImportHelperBase.StringInputSpec("Laying", "setLaying", false, "Laying style e.g., S=single-layer, M=multi-layer, T=triangular, B=bundle", 256));
        specs.add(new ImportHelperBase.StringInputSpec("Voltage", "setVoltage", false, "Voltage aplication e.g., COM=communication, CTRL=control, IW=instrumentation, LV=low voltage, MV=medium voltage", 256));
        specs.add(new ImportHelperBase.IdOrNameRefInputSpec("Owner", "setTeam", false, "Numeric ID of CDB technical system.", ItemCategoryController.getInstance(), ItemCategory.class, ItemDomainName.cableDesign.getValue()));
        specs.add(new ImportHelperBase.IdOrNameRefInputSpec("Project", "setProject", true, "Numeric ID of CDB project.", ItemProjectController.getInstance(), ItemProject.class, null));
        specs.add(new ImportHelperBase.IdOrNameRefInputSpec("Type", "setCatalogItem", false, "Numeric ID of CDB cable type catalog item.", ItemDomainCableCatalogController.getInstance(), Item.class, null));
        specs.add(new ImportHelperBase.IdRefInputSpec("Endpoint1", "setEndpoint1", false, "Numeric ID of CDB machine design item for first endpoint.", ItemDomainMachineDesignController.getInstance(), Item.class));
        specs.add(new ImportHelperBase.StringInputSpec("Endpoint1 Desc", "setEndpoint1Description", false, "Endpoint details useful for external editing.", 256));
        specs.add(new ImportHelperBase.IdRefInputSpec("Endpoint2", "setEndpoint2", false, "Numeric ID of CDB machine design item for second endpoint.", ItemDomainMachineDesignController.getInstance(), Item.class));
        specs.add(new ImportHelperBase.StringInputSpec("Endpoint2 Desc", "setEndpoint2Description", false, "Endpoint details useful for external editing.", 256));

        return specs;
    }
    
    @Override
    protected List<OutputColumnModel> initializeTableViewColumns_() {
        List<OutputColumnModel> columns = new ArrayList<>();
        
        columns.add(new OutputColumnModel(COLUMN_MODEL_NAME, "name"));
        columns.add(new OutputColumnModel("Alt Name", "alternateName"));
        columns.add(new OutputColumnModel("Ext Cable Name", "externalCableName"));
        columns.add(new OutputColumnModel("Import Cable ID", "importCableId"));
        columns.add(new OutputColumnModel("Alternate Cable ID", "alternateCableId"));
        columns.add(new OutputColumnModel("Description", "description"));
        columns.add(new OutputColumnModel("Laying", "laying"));
        columns.add(new OutputColumnModel("Voltage", "voltage"));
        columns.add(new OutputColumnModel("Owner", "team"));
        columns.add(new OutputColumnModel("Project", "itemProjectString"));
        columns.add(new OutputColumnModel("Type", "catalogItemString"));
        columns.add(new OutputColumnModel("Endpoint1", "endpoint1String"));
        columns.add(new OutputColumnModel("Endpoint1 Desc", "endpoint1Description"));
        columns.add(new OutputColumnModel("Endpoint2", "endpoint2String"));
        columns.add(new OutputColumnModel("Endpoint2 Desc", "endpoint2Description"));

        return columns;
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
    protected ParseInfo postParseCell(Object parsedValue, String columnName, String id) {
        
        if (columnName.equals(COLUMN_MODEL_NAME)) {        
            String idPattern = "#cdbid#";
            String replacePattern = "#cdbid-" + id + "#";
            String result = (String)parsedValue;
            result = result.replaceAll(idPattern, replacePattern);
            return new ParseInfo<>(result, true, "");
        }
        
        return new ParseInfo<>(parsedValue, true, "");
    }

    /*
    * Finds strings matching the pattern "#cdbid*#" in the name and alt name fields
    * and replace them with the internal cdb identifier.
    */
    @Override
    protected ValidInfo postImport() {
        
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
        
        return new ValidInfo(true, message);
    }
}
