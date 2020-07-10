/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperCableCatalog extends ImportHelperBase<ItemDomainCableCatalog, ItemDomainCableCatalogController> {


    protected static String completionUrlValue = "/views/itemDomainCableCatalog/list?faces-redirect=true";
    
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new StringColumnSpec(0, "Name", "name", "setName", true, "Cable type name, uniquely identifies cable type.", 128));
        specs.add(new StringColumnSpec(1, "Alt Name", "alternateName", "setAlternateName", false, "Alternate cable type name.", 128));
        specs.add(new StringColumnSpec(2, "Description", "description", "setDescription", false, "Textual description of cable type.", 256));
        specs.add(new StringColumnSpec(3, "Documentation URL", "urlDisplay", "setUrl", false, "Raw URL for documentation pdf file, e.g., http://www.example.com/documentation.pdf", 256));
        specs.add(new StringColumnSpec(4, "Image URL", "imageUrlDisplay", "setImageUrl", false, "Raw URL for image file, e.g., http://www.example.com/image.jpg", 256));
        specs.add(new IdOrNameRefColumnSpec(5, "Manufacturer", "manufacturer", "setManufacturerSource", false, "Manufacturer or vendor, e.g., CommScope", SourceController.getInstance(), Source.class, null));
        specs.add(new StringColumnSpec(6, "Part Number", "partNumber", "setPartNumber", false, "Manufacturer's part number, e.g., R-024-DS-5K-FSUBR", 32));
        specs.add(new StringColumnSpec(7, "Alt Part Num", "altPartNumber", "setAltPartNumber", false, "Manufacturer's alternate part number, e.g., 760152413", 256));
        specs.add(new IdOrNameRefColumnSpec(8, "Owner", "team", "setTeam", false, "Numeric ID of CDB technical system.", ItemCategoryController.getInstance(), ItemCategory.class, ItemDomainName.cableCatalog.getValue()));
        specs.add(new IdOrNameRefColumnSpec(9, "Project", "itemProjectString", "setProject", true, "Numeric ID of CDB project.", ItemProjectController.getInstance(), ItemProject.class, ""));
        specs.add(new StringColumnSpec(10, "Diameter", "diameter", "setDiameter", false, "Diameter in inches (max).", 256));
        specs.add(new StringColumnSpec(11, "Weight", "weight", "setWeight", false, "Nominal weight in lbs/1000 feet.", 256));
        specs.add(new StringColumnSpec(12, "Conductors", "conductors", "setConductors", false, "Number of conductors/fibers", 256));
        specs.add(new StringColumnSpec(13, "Insulation", "insulation", "setInsulation", false, "Description of cable insulation.", 256));
        specs.add(new StringColumnSpec(14, "Jacket Color", "jacketColor", "setJacketColor", false, "Jacket color.", 256));
        specs.add(new StringColumnSpec(15, "Voltage Rating", "voltageRating", "setVoltageRating", false, "Voltage rating (VRMS).", 256));
        specs.add(new StringColumnSpec(16, "Fire Load", "fireLoad", "setFireLoad", false, "Fire load rating.", 256));
        specs.add(new StringColumnSpec(17, "Heat Limit", "heatLimit", "setHeatLimit", false, "Heat limit.", 256));
        specs.add(new StringColumnSpec(18, "Bend Radius", "bendRadius", "setBendRadius", false, "Bend radius in inches.", 256));
        specs.add(new StringColumnSpec(19, "Rad Tolerance", "radTolerance", "setRadTolerance", false, "Radiation tolerance rating.", 256));

        return specs;
    }
    
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    public ItemDomainCableCatalogController getEntityController() {
        return ItemDomainCableCatalogController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Cable Type Catalog Template";
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        ItemDomainCableCatalog entity = getEntityController().createEntityInstance();
        return new CreateInfo(entity, true, "");
    }
}
