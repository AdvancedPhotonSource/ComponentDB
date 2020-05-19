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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author craig
 */
public class ImportHelperCableDesign extends ImportHelperBase<ItemDomainCableDesign, ItemDomainCableDesignController> {

    public class NameHandler extends SingleColumnInputHandler {
        
        protected int maxLength = 0;
        protected String importName = null;
        protected int entityNum = 0;
        
        public NameHandler(int columnIndex, int maxLength) {
            super(columnIndex);
            this.maxLength = maxLength;
        }
        
        public int getMaxLength() {
            return maxLength;
        }

        protected String getImportName() {
            if (importName == null) {
                importName = "import-" + java.time.Instant.now().getEpochSecond();
            }
            return importName;
        }
        
        protected String getUniqueId() {
            return getImportName() + "-" + entityNum;
        }
        
        public ValidInfo handleInput(
                Row row,
                Map<Integer, String> cellValueMap,
                ItemDomainCableDesign entity) {
            
            boolean isValid = true;
            String validString = "";
            
            String parsedValue = cellValueMap.get(columnIndex);

            // check column length is valid
            if ((getMaxLength() > 0) && (parsedValue.length() > getMaxLength())) {
                isValid = false;
                validString = appendToString(validString, 
                        "Value length exceeds " + getMaxLength() + 
                                " characters for column " + columnNameForIndex(columnIndex));
            }
                
            // replace "#cdbid#" with a unique identifier
            entityNum = entityNum + 1;
            String idPattern = "#cdbid#";
            String replacePattern = "#cdbid-" + getUniqueId() + "#";
            String result = parsedValue.replaceAll(idPattern, replacePattern);
            entity.setName(result);
            
            return new ValidInfo(isValid, validString);

        }

    }

    protected static String completionUrlValue = "/views/itemDomainCableDesign/list?faces-redirect=true";
    
    @Override
    protected List<InputColumnModel> initializeInputColumns_(
            int actualColumnCount,
            Map<Integer, String> headerValueMap) {
        
        List<InputColumnModel> cols = new ArrayList<>();
        
        cols.add(new InputColumnModel(0, "Name", true, "Cable name, uniquely identifies cable in CDB. Embedded '#cdbid# tag will be replaced with the internal CDB identifier (integer)."));
        cols.add(new InputColumnModel(1, "Alt Name", false, "Alternate cable name. Embedded '#cdbid# tag will be replaced with the internal CDB identifier (integer)."));
        cols.add(new InputColumnModel(2, "Ext Cable Name", false, "Cable name in external system (e.g., CAD, routing tool) e.g., SR_R_401_D1109_RR8G[low] | SR_M_A02_C61_64_02-00[high]"));
        cols.add(new InputColumnModel(3, "Import Cable ID", false, "Import cable identifier."));
        cols.add(new InputColumnModel(4, "Alternate Cable ID", false, "Alternate (e.g., group-specific) cable identifier."));
        cols.add(new InputColumnModel(5, "Description", false, "Description of cable."));
        cols.add(new InputColumnModel(6, "Laying", false, "Laying style e.g., S=single-layer, M=multi-layer, T=triangular, B=bundle"));
        cols.add(new InputColumnModel(7, "Voltage", false, "Voltage aplication e.g., COM=communication, CTRL=control, IW=instrumentation, LV=low voltage, MV=medium voltage"));
        cols.add(new InputColumnModel(8, "Owner", false, "Numeric ID of CDB technical system."));
        cols.add(new InputColumnModel(9, "Project", true, "Numeric ID of CDB project."));
        cols.add(new InputColumnModel(10, "Type", false, "Numeric ID of CDB cable type catalog item."));
        cols.add(new InputColumnModel(11, "Endpoint1", false, "Numeric ID of CDB machine design item for first endpoint."));
        cols.add(new InputColumnModel(12, "Endpoint1 Desc", false, "Endpoint details useful for external editing."));
        cols.add(new InputColumnModel(13, "Endpoint2", false, "Numeric ID of CDB machine design item for second endpoint."));
        cols.add(new InputColumnModel(14, "Endpoint2 Desc", false, "Endpoint details useful for external editing."));

        return cols;
    }
    
    @Override
    protected List<InputHandler> initializeInputHandlers_(
            int actualColumnCount, 
            Map<Integer, String> headerValueMap) {
        
        List<InputHandler> specs = new ArrayList<>();
        
        specs.add(new NameHandler(0, 128));
        specs.add(new ImportHelperBase.StringInputHandler(1, "setAlternateName", 32));
        specs.add(new ImportHelperBase.StringInputHandler(2, "setExternalCableName", 256));
        specs.add(new ImportHelperBase.StringInputHandler(3, "setImportCableId", 256));
        specs.add(new ImportHelperBase.StringInputHandler(4, "setAlternateCableId", 256));
        specs.add(new ImportHelperBase.StringInputHandler(5, "setDescription", 256));
        specs.add(new ImportHelperBase.StringInputHandler(6, "setLaying", 256));
        specs.add(new ImportHelperBase.StringInputHandler(7, "setVoltage", 256));
        specs.add(new ImportHelperBase.IdOrNameRefInputHandler(8, "setTeam", ItemCategoryController.getInstance(), ItemCategory.class, ItemDomainName.cableDesign.getValue()));
        specs.add(new ImportHelperBase.IdOrNameRefInputHandler(9, "setProject", ItemProjectController.getInstance(), ItemProject.class, null));
        specs.add(new ImportHelperBase.IdOrNameRefInputHandler(10, "setCatalogItem", ItemDomainCableCatalogController.getInstance(), Item.class, null));
        specs.add(new ImportHelperBase.IdRefInputHandler(11, "setEndpoint1", ItemDomainMachineDesignController.getInstance(), Item.class));
        specs.add(new ImportHelperBase.StringInputHandler(12, "setEndpoint1Description", 256));
        specs.add(new ImportHelperBase.IdRefInputHandler(13, "setEndpoint2", ItemDomainMachineDesignController.getInstance(), Item.class));
        specs.add(new ImportHelperBase.StringInputHandler(14, "setEndpoint2Description", 256));

        return specs;
    }
    
    @Override
    protected List<OutputColumnModel> initializeTableViewColumns_(
            int actualColumnCount,
            Map<Integer, String> headerValueMap) {
        
        List<OutputColumnModel> columns = new ArrayList<>();
        
        columns.add(new OutputColumnModel("Name", "name"));
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
