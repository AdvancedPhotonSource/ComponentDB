/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperCableCatalog extends ImportHelperCatalogBase<ItemDomainCableCatalog, ItemDomainCableCatalogController> {

    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new StringColumnSpec("Name", "name", "setName", true, "Cable type name, uniquely identifies cable type.", 128));
        specs.add(new StringColumnSpec("Alt Name", "alternateName", "setAlternateName", false, "Alternate cable type name.", 128));
        specs.add(new StringColumnSpec("Description", "description", "setDescription", false, "Textual description of cable type.", 256));
        specs.add(new StringColumnSpec("Documentation URL", "urlDisplay", "setUrl", false, "Raw URL for documentation pdf file, e.g., http://www.example.com/documentation.pdf", 256));
        specs.add(new StringColumnSpec("Image URL", "imageUrlDisplay", "setImageUrl", false, "Raw URL for image file, e.g., http://www.example.com/image.jpg", 256));
        specs.add(new IdOrNameRefColumnSpec("Manufacturer", ImportHelperCatalogBase.KEY_MFR, "", false, "ID or name of CDB source for manufacturer. Name must be unique and prefixed with '#'.", SourceController.getInstance(), Source.class, ""));
        specs.add(new StringColumnSpec("Part Number", ImportHelperCatalogBase.KEY_PART_NUM, "setPartNumber", false, "Manufacturer's part number.", 32));
        specs.add(new StringColumnSpec("Alt Part Num", "altPartNumber", "setAltPartNumber", false, "Manufacturer's alternate part number, e.g., 760152413", 256));
        specs.add(new StringColumnSpec("Diameter", "diameter", "setDiameter", false, "Diameter in inches (max).", 256));
        specs.add(new StringColumnSpec("Weight", "weight", "setWeight", false, "Nominal weight in lbs/1000 feet.", 256));
        specs.add(new StringColumnSpec("Conductors", "conductors", "setConductors", false, "Number of conductors/fibers", 256));
        specs.add(new StringColumnSpec("Insulation", "insulation", "setInsulation", false, "Description of cable insulation.", 256));
        specs.add(new StringColumnSpec("Jacket Color", "jacketColor", "setJacketColor", false, "Jacket color.", 256));
        specs.add(new StringColumnSpec("Voltage Rating", "voltageRating", "setVoltageRating", false, "Voltage rating (VRMS).", 256));
        specs.add(new StringColumnSpec("Fire Load", "fireLoad", "setFireLoad", false, "Fire load rating.", 256));
        specs.add(new StringColumnSpec("Heat Limit", "heatLimit", "setHeatLimit", false, "Heat limit.", 256));
        specs.add(new StringColumnSpec("Bend Radius", "bendRadius", "setBendRadius", false, "Bend radius in inches.", 256));
        specs.add(new StringColumnSpec("Rad Tolerance", "radTolerance", "setRadTolerance", false, "Radiation tolerance rating.", 256));
        specs.add(new StringColumnSpec("Total Length", "totalLength", "setTotalLength", false, "Total cable length required.", 256));
        specs.add(new StringColumnSpec("Reel Length", "reelLength", "setReelLength", false, "Standard reel length for this type of cable.", 256));
        specs.add(new StringColumnSpec("Reel Quantity", "reelQuantity", "setReelQuantity", false, "Number of standard reels required for total length.", 256));
        specs.add(new StringColumnSpec("Lead Time", "leadTime", "setLeadTime", false, "Standard procurement lead time for this type of cable.", 256));
        specs.add(new StringColumnSpec("Procurement Status", "procurementStatus", "setProcurementStatus", false, "Procurement status.", 256));
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
    public String getFilenameBase() {
        return "Cable Type Catalog";
    }
    
    @Override 
    protected ValidInfo preImport() {
        getEntityController().migrateCoreMetadataPropertyType();
        return new ValidInfo(true, "");
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        return super.createEntityInstance(rowMap);
    }  
}
