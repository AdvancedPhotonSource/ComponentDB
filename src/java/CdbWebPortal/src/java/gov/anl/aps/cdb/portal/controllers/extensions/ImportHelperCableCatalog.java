/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class ImportHelperCableCatalog extends ImportHelperBase<ItemDomainCableCatalog, ItemDomainCableCatalogController> {


    protected static String completionUrlValue = "/views/itemDomainCableCatalog/list?faces-redirect=true";
    
    @Override
    protected List<InputSpec> initializeInputSpecs_() {
        List<InputSpec> specs = new ArrayList<>();
        
        specs.add(new StringInputSpec("Name", "setName", true, "Cable type name, uniquely identifies cable type.", 128));
        specs.add(new StringInputSpec("Description", "setDescription", false, "Textual description of cable type.", 256));
        specs.add(new StringInputSpec("Documentation URL", "setUrl", false, "Raw URL for documentation pdf file, e.g., http://www.example.com/documentation.pdf", 256));
        specs.add(new StringInputSpec("Image URL", "setImageUrl", false, "Raw URL for image file, e.g., http://www.example.com/image.jpg", 256));
        specs.add(new IdOrNameRefInputSpec("Manufacturer", "setManufacturerSource", false, "Manufacturer or vendor, e.g., CommScope", SourceController.getInstance(), Source.class, null));
        specs.add(new StringInputSpec("Part Number", "setPartNumber", false, "Manufacturer's part number, e.g., R-024-DS-5K-FSUBR", 32));
        specs.add(new StringInputSpec("Alt Part Num", "setAltPartNumber", false, "Manufacturer's alternate part number, e.g., 760152413", 256));
        specs.add(new StringInputSpec("Diameter", "setDiameter", false, "Diameter in inches (max).", 256));
        specs.add(new StringInputSpec("Weight", "setWeight", false, "Nominal weight in lbs/1000 feet.", 256));
        specs.add(new StringInputSpec("Conductors", "setConductors", false, "Number of conductors/fibers", 256));
        specs.add(new StringInputSpec("Insulation", "setInsulation", false, "Description of cable insulation.", 256));
        specs.add(new StringInputSpec("Jacket Color", "setJacketColor", false, "Jacket color.", 256));
        specs.add(new StringInputSpec("Voltage Rating", "setVoltageRating", false, "Voltage rating (VRMS).", 256));
        specs.add(new StringInputSpec("Fire Load", "setFireLoad", false, "Fire load rating.", 256));
        specs.add(new StringInputSpec("Heat Limit", "setHeatLimit", false, "Heat limit.", 256));
        specs.add(new StringInputSpec("Bend Radius", "setBendRadius", false, "Bend radius in inches.", 256));
        specs.add(new StringInputSpec("Rad Tolerance", "setRadTolerance", false, "Radiation tolerance rating.", 256));
        specs.add(new IdOrNameRefInputSpec("Owner", "setTeam", false, "Numeric ID of CDB technical system.", ItemCategoryController.getInstance(), ItemCategory.class, ItemDomainName.cableCatalog.getValue()));

        return specs;
    }
    
    @Override
    protected List<OutputColumnModel> initializeTableViewColumns_() {
        List<OutputColumnModel> columns = new ArrayList<>();
        
        columns.add(new OutputColumnModel("Name", "name"));
        columns.add(new OutputColumnModel("Description", "description"));
        columns.add(new OutputColumnModel("Documentation URL", "urlDisplay"));
        columns.add(new OutputColumnModel("Image URL", "imageUrlDisplay"));
        columns.add(new OutputColumnModel("Manufacturer", "manufacturer"));
        columns.add(new OutputColumnModel("Part Number", "partNumber"));
        columns.add(new OutputColumnModel("Alt Part Num", "altPartNumber"));
        columns.add(new OutputColumnModel("Diameter", "diameter"));
        columns.add(new OutputColumnModel("Weight", "weight"));
        columns.add(new OutputColumnModel("Conductors", "conductors"));
        columns.add(new OutputColumnModel("Insulation", "insulation"));
        columns.add(new OutputColumnModel("Jacket Color", "jacketColor"));
        columns.add(new OutputColumnModel("Voltage Rating", "voltageRating"));
        columns.add(new OutputColumnModel("Fire Load", "fireLoad"));
        columns.add(new OutputColumnModel("Heat Limit", "heatLimit"));
        columns.add(new OutputColumnModel("Bend Radius", "bendRadius"));
        columns.add(new OutputColumnModel("Rad Tolerance", "radTolerance"));
        columns.add(new OutputColumnModel("Owner", "team"));

        return columns;
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
    protected ItemDomainCableCatalog createEntityInstance() {
        return getEntityController().createEntityInstance();
    }
}
