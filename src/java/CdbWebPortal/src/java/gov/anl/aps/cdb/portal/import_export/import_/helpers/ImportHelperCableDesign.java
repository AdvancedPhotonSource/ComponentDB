/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.SingleColumnInputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.CustomColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
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
        
        public NameHandler(int maxLength) {
            super(LABEL_NAME);
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
            
            String parsedValue = cellValueMap.get(getColumnIndex());

            // check column length is valid
            if ((getMaxLength() > 0) && (parsedValue.length() > getMaxLength())) {
                isValid = false;
                validString = 
                        "Value length exceeds " + 
                        String.valueOf(getMaxLength()) + 
                        " characters for column: " + LABEL_NAME;
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
                CdbEntity entity) {
            
            ((ItemDomainCableDesign)entity).setName((String)rowMap.get(PROPERTY));
            
            return new ValidInfo(true, "");
        }
    }
    
    private static final String LABEL_NAME = "Name";

    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(existingItemIdColumnSpec());

        InputHandler nameHandler = new NameHandler(128);
        specs.add(new CustomColumnSpec(LABEL_NAME, "name", true, "Cable name, uniquely identifies cable in CDB. Embedded '#cdbid# tag will be replaced with the internal CDB identifier (integer).", nameHandler, "getName"));
        
        specs.add(new StringColumnSpec("Alt Name", "alternateName", "setAlternateName", false, "Alternate cable name. Embedded '#cdbid# tag will be replaced with the internal CDB identifier (integer).", 128, "getAlternateName"));
        specs.add(new StringColumnSpec("Ext Cable Name", "externalCableName", "setExternalCableName", false, "Cable name in external system (e.g., CAD, routing tool).", 256, "getExternalCableName"));
        specs.add(new StringColumnSpec("Import Cable ID", "importCableId", "setImportCableId", false, "Import cable identifier.", 256, "getImportCableId"));
        specs.add(new StringColumnSpec("Alternate Cable ID", "alternateCableId", "setAlternateCableId", false, "Alternate (e.g., group-specific) cable identifier.", 256, "getAlternateCableId"));
        specs.add(new StringColumnSpec("Legacy QR ID", "legacyQrId", "setLegacyQrId", false, "Legacy QR identifier, e.g., for cables that have already been assigned a QR code.", 256, "getLegacyQrId"));
        specs.add(new StringColumnSpec("Description", "description", "setDescription", false, "Description of cable.", 256, "getDescription"));
        specs.add(new StringColumnSpec("Laying", "laying", "setLaying", false, "Laying style e.g., S=single-layer, M=multi-layer, T=triangular, B=bundle", 256, "getLaying"));
        specs.add(new StringColumnSpec("Voltage", "voltage", "setVoltage", false, "Voltage aplication e.g., COM=communication, CTRL=control, IW=instrumentation, LV=low voltage, MV=medium voltage", 256, "getVoltage"));
        specs.add(new IdOrNameRefColumnSpec("Type", "catalogItemString", "setCatalogItem", false, "Numeric ID or name of CDB cable type catalog item. Name must be unique and prefixed with '#'.", ItemDomainCableCatalogController.getInstance(), Item.class, "", "getCatalogItem"));
        specs.add(new IdOrNameRefColumnSpec("Endpoint1", "endpoint1String", "setEndpoint1", false, "Numeric ID or name of CDB machine design item for first endpoint. Name must be unique and prefixed with '#'.", ItemDomainMachineDesignController.getInstance(), Item.class, "", "getEndpoint1"));
        specs.add(new StringColumnSpec("Endpoint1 Desc", "endpoint1Description", "setEndpoint1Description", false, "Endpoint details useful for external editing.", 256, "getEndpoint1Description"));
        specs.add(new StringColumnSpec("Endpoint1 Route", "endpoint1Route", "setEndpoint1Route", false, "Routing waypoint for first endpoint.", 256, "getEndpoint1Route"));        
        specs.add(new IdOrNameRefColumnSpec("Endpoint2", "endpoint2String", "setEndpoint2", false, "Numeric ID or name of CDB machine design item for second endpoint. Name must be unique and prefixed with '#'.", ItemDomainMachineDesignController.getInstance(), Item.class, "", "getEndpoint2"));
        specs.add(new StringColumnSpec("Endpoint2 Desc", "endpoint2Description", "setEndpoint2Description", false, "Endpoint details useful for external editing.", 256, "getEndpoint2Description"));
        specs.add(new StringColumnSpec("Endpoint2 Route", "endpoint2Route", "setEndpoint2Route", false, "Routing waypoint for second endpoint.", 256, "getEndpoint2Route"));        
        specs.add(projectListColumnSpec());
        specs.add(technicalSystemListColumnSpec(ItemDomainName.cableDesign.getValue()));
        specs.add(ownerUserColumnSpec());
        specs.add(ownerGroupColumnSpec());


        return specs;
    }
    
    @Override
    public ItemDomainCableDesignController getEntityController() {
        return ItemDomainCableDesignController.getInstance();
    }

    /**
     * Specifies whether helper supports updating existing instances.  Defaults
     * to false. Subclasses override to customize.
     */
    @Override
    public boolean supportsModeUpdate() {
        return true;
    }

    @Override
    public String getFilenameBase() {
        return "Cable Design";
    }
    
    @Override
    protected ItemDomainCableDesign newInvalidUpdateInstance() {
        return getEntityController().createEntityInstance();
    }

    @Override 
    protected ValidInfo preImport() {
        getEntityController().migrateCoreMetadataPropertyType();
        return new ValidInfo(true, "");
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
    protected ValidInfo postCreate() {
        
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
