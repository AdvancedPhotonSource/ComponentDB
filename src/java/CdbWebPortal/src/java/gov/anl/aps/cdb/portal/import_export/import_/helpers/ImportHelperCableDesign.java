/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperBase;
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
        
        private static final String PROPERTY = "name";
        
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
        
        @Override
        public ValidInfo handleInput(
                Row row,
                Map<Integer, String> cellValueMap,
                Map<String, Object> rowMap) {
            
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
            rowMap.put(PROPERTY, result);
            
            return new ValidInfo(isValid, validString);
        }

        @Override
        public ValidInfo updateEntity(
                Map<String, Object> rowMap, 
                ItemDomainCableDesign entity) {
            
            entity.setName((String)rowMap.get(PROPERTY));
            
            return new ValidInfo(true, "");
        }
    }

    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new StringColumnSpec(1, "Alt Name", "alternateName", "setAlternateName", false, "Alternate cable name. Embedded '#cdbid# tag will be replaced with the internal CDB identifier (integer).", 128));
        specs.add(new StringColumnSpec(2, "Ext Cable Name", "externalCableName", "setExternalCableName", false, "Cable name in external system (e.g., CAD, routing tool) e.g., SR_R_401_D1109_RR8G[low] | SR_M_A02_C61_64_02-00[high]", 256));
        specs.add(new StringColumnSpec(3, "Import Cable ID", "importCableId", "setImportCableId", false, "Import cable identifier.", 256));
        specs.add(new StringColumnSpec(4, "Alternate Cable ID", "alternateCableId", "setAlternateCableId", false, "Alternate (e.g., group-specific) cable identifier.", 256));
        specs.add(new StringColumnSpec(5, "Legacy QR ID", "legacyQrId", "setLegacyQrId", false, "Legacy QR identifier, e.g., for cables that have already been assigned a QR code.", 256));
        specs.add(new StringColumnSpec(6, "Description", "description", "setDescription", false, "Description of cable.", 256));
        specs.add(new StringColumnSpec(7, "Laying", "laying", "setLaying", false, "Laying style e.g., S=single-layer, M=multi-layer, T=triangular, B=bundle", 256));
        specs.add(new StringColumnSpec(8, "Voltage", "voltage", "setVoltage", false, "Voltage aplication e.g., COM=communication, CTRL=control, IW=instrumentation, LV=low voltage, MV=medium voltage", 256));
        specs.add(new IdOrNameRefColumnSpec(9, "Owner", "team", "setTeam", false, "Numeric ID of CDB technical system.", ItemCategoryController.getInstance(), ItemCategory.class, ItemDomainName.cableDesign.getValue()));
        specs.add(new IdOrNameRefColumnSpec(10, "Project", "itemProjectString", "setProject", true, "Numeric ID of CDB project.", ItemProjectController.getInstance(), ItemProject.class, ""));
        specs.add(new IdOrNameRefColumnSpec(11, "Type", "catalogItemString", "setCatalogItem", false, "Numeric ID of CDB cable type catalog item.", ItemDomainCableCatalogController.getInstance(), Item.class, ""));
        specs.add(new IdOrNameRefColumnSpec(12, "Endpoint1", "endpoint1String", "setEndpoint1", false, "Numeric ID of CDB machine design item for first endpoint.", ItemDomainMachineDesignController.getInstance(), Item.class, ""));
        specs.add(new StringColumnSpec(13, "Endpoint1 Desc", "endpoint1Description", "setEndpoint1Description", false, "Endpoint details useful for external editing.", 256));
        specs.add(new IdOrNameRefColumnSpec(14, "Endpoint2", "endpoint2String", "setEndpoint2", false, "Numeric ID of CDB machine design item for second endpoint.", ItemDomainMachineDesignController.getInstance(), Item.class, ""));
        specs.add(new StringColumnSpec(15, "Endpoint2 Desc", "endpoint2Description", "setEndpoint2Description", false, "Endpoint details useful for external editing.", 256));

        return specs;
    }
    
    @Override
    protected ValidInfo initialize_(
            int actualColumnCount,
            Map<Integer, String> headerValueMap,
            List<InputColumnModel> inputColumns,
            List<InputHandler> inputHandlers,
            List<OutputColumnModel> outputColumns) {
        
        inputColumns.add(new InputColumnModel(0, "Name", true, "Cable name, uniquely identifies cable in CDB. Embedded '#cdbid# tag will be replaced with the internal CDB identifier (integer)."));
        inputHandlers.add(new NameHandler(0, 128));
        outputColumns.add(new OutputColumnModel(0, "Name", "name"));
        
        return new ValidInfo(true, "");
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
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        ItemDomainCableDesign entity = getEntityController().createEntityInstance();
        return new CreateInfo(entity, true, "");
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
