/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class ImportHelperCableCatalog extends ImportHelperCatalogBase<ItemDomainCableCatalog, ItemDomainCableCatalogController> {

    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(existingItemIdColumnSpec());
        specs.add(deleteExistingItemColumnSpec());
        
        specs.add(new StringColumnSpec(
                "Name", 
                "name", 
                "setName", 
                "Cable type name, uniquely identifies cable type.", 
                "getName",
                ColumnModeOptions.rCREATErUPDATE(),
                128));
        
        specs.add(new StringColumnSpec(
                "Alt Name", 
                "alternateName", 
                "setAlternateName", 
                "Alternate cable type name.", 
                "getAlternateName",
                ColumnModeOptions.oCREATEoUPDATE(), 
                128));
        
        specs.add(new StringColumnSpec(
                "Description", 
                "description", 
                "setDescription", 
                "Textual description of cable type.", 
                "getDescription",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Documentation URL", 
                "urlDisplay", 
                "setUrl", 
                "Raw URL for documentation pdf file, e.g., http://www.example.com/documentation.pdf", 
                "getUrl",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Image URL", 
                "imageUrlDisplay", 
                "setImageUrl", 
                "Raw URL for image file, e.g., http://www.example.com/image.jpg", 
                "getImageUrl",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(sourceColumnSpec("Manufacturer"));
        
        specs.add(new StringColumnSpec(
                "Part Number", 
                ImportHelperCatalogBase.KEY_PART_NUM, 
                "setPartNumber", 
                "Manufacturer's part number.", 
                "getPartNumber",
                ColumnModeOptions.oCREATEoUPDATE(), 
                32));
        
        specs.add(new StringColumnSpec(
                "Alt Part Num", 
                "altPartNumber", 
                "setAltPartNumber", 
                "Manufacturer's alternate part number, e.g., 760152413", 
                "getAltPartNumber",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Diameter", 
                "diameter", 
                "setDiameter", 
                "Diameter in inches (max).", 
                "getDiameter",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Weight", 
                "weight", 
                "setWeight", 
                "Nominal weight in lbs/1000 feet.", 
                "getWeight",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Conductors", 
                "conductors", 
                "setConductors", 
                "Number of conductors/fibers", 
                "getConductors",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Insulation", 
                "insulation", 
                "setInsulation", 
                "Description of cable insulation.", 
                "getInsulation",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Jacket Color", 
                "jacketColor", 
                "setJacketColor", 
                "Jacket color.", 
                "getJacketColor",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Voltage Rating", 
                "voltageRating", 
                "setVoltageRating", 
                "Voltage rating (VRMS).", 
                "getVoltageRating",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Fire Load", 
                "fireLoad", 
                "setFireLoad", 
                "Fire load rating.", 
                "getFireLoad",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Heat Limit", 
                "heatLimit", 
                "setHeatLimit", 
                "Heat limit.", 
                "getHeatLimit",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Bend Radius", 
                "bendRadius", 
                "setBendRadius", 
                "Bend radius in inches.", 
                "getBendRadius",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Rad Tolerance", 
                "radTolerance", 
                "setRadTolerance", 
                "Radiation tolerance rating.", 
                "getRadTolerance",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Total Length", 
                "totalLength", 
                "setTotalLength", 
                "Total cable length required.", 
                "getTotalLength",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Reel Length", 
                "reelLength", 
                "setReelLength", 
                "Standard reel length for this type of cable.", 
                "getReelLength",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Reel Quantity", 
                "reelQuantity", 
                "setReelQuantity", 
                "Number of standard reels required for total length.", 
                "getReelQuantity",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Lead Time", 
                "leadTime", 
                "setLeadTime", 
                "Standard procurement lead time for this type of cable.", 
                "getLeadTime",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Procurement Status", 
                "procurementStatus", 
                "setProcurementStatus", 
                "Procurement status.", 
                "getProcurementStatus",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(projectListColumnSpec());
        specs.add(technicalSystemListColumnSpec(ItemDomainName.cableCatalog.getValue()));
        specs.add(ownerUserColumnSpec());
        specs.add(ownerGroupColumnSpec());

        return specs;
    }
    
    @Override
    public ItemDomainCableCatalogController getEntityController() {
        return ItemDomainCableCatalogController.getInstance();
    }

    @Override
    public boolean supportsModeUpdate() {
        return true;
    }

    @Override
    public boolean supportsModeDelete() {
        return true;
    }

    @Override
    public String getFilenameBase() {
        return "Cable Type Catalog";
    }
    
    @Override 
    protected ValidInfo preImport() {
        getEntityController().createOrMigrateCoreMetadataPropertyType();
        return new ValidInfo(true, "");
    }
    
    @Override
    protected ItemDomainCableCatalog newInvalidUpdateInstance() {
        return getEntityController().createEntityInstance();
    }

}
