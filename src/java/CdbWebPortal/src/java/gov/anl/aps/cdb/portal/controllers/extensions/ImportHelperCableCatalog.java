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
    protected List<InputColumnModel> initializeInputColumns_() {
        List<InputColumnModel> cols = new ArrayList<>();
        
        cols.add(new InputColumnModel(0, "Name", true, "Cable type name, uniquely identifies cable type."));
        cols.add(new InputColumnModel(1, "Description", false, "Textual description of cable type."));
        cols.add(new InputColumnModel(2, "Documentation URL", false, "Raw URL for documentation pdf file, e.g., http://www.example.com/documentation.pdf"));
        cols.add(new InputColumnModel(3, "Image URL", false, "Raw URL for image file, e.g., http://www.example.com/image.jpg"));
        cols.add(new InputColumnModel(4, "Manufacturer", false, "Manufacturer or vendor, e.g., CommScope"));
        cols.add(new InputColumnModel(5, "Part Number", false, "Manufacturer's part number, e.g., R-024-DS-5K-FSUBR"));
        cols.add(new InputColumnModel(6, "Alt Part Num", false, "Manufacturer's alternate part number, e.g., 760152413"));
        cols.add(new InputColumnModel(7, "Diameter", false, "Diameter in inches (max)."));
        cols.add(new InputColumnModel(8, "Weight", false, "Nominal weight in lbs/1000 feet."));
        cols.add(new InputColumnModel(9, "Conductors", false, "Number of conductors/fibers"));
        cols.add(new InputColumnModel(10, "Insulation", false, "Description of cable insulation."));
        cols.add(new InputColumnModel(11, "Jacket Color", false, "Jacket color."));
        cols.add(new InputColumnModel(12, "Voltage Rating", false, "Voltage rating (VRMS)."));
        cols.add(new InputColumnModel(13, "Fire Load", false, "Fire load rating."));
        cols.add(new InputColumnModel(14, "Heat Limit", false, "Heat limit."));
        cols.add(new InputColumnModel(15, "Bend Radius", false, "Bend radius in inches."));
        cols.add(new InputColumnModel(16, "Rad Tolerance", false, "Radiation tolerance rating."));
        cols.add(new InputColumnModel(17, "Owner", false, "Numeric ID of CDB technical system."));

        return cols;
    }
    
    @Override
    protected List<InputHandler> initializeInputHandlers_() {
        List<InputHandler> specs = new ArrayList<>();
        
        specs.add(new StringInputHandler(0, "setName", 128));
        specs.add(new StringInputHandler(1, "setDescription", 256));
        specs.add(new StringInputHandler(2, "setUrl", 256));
        specs.add(new StringInputHandler(3, "setImageUrl", 256));
        specs.add(new IdOrNameRefInputHandler(4, "setManufacturerSource", SourceController.getInstance(), Source.class, null));
        specs.add(new StringInputHandler(5, "setPartNumber", 32));
        specs.add(new StringInputHandler(6, "setAltPartNumber", 256));
        specs.add(new StringInputHandler(7, "setDiameter", 256));
        specs.add(new StringInputHandler(8, "setWeight", 256));
        specs.add(new StringInputHandler(9, "setConductors", 256));
        specs.add(new StringInputHandler(10, "setInsulation", 256));
        specs.add(new StringInputHandler(11, "setJacketColor", 256));
        specs.add(new StringInputHandler(12, "setVoltageRating", 256));
        specs.add(new StringInputHandler(13, "setFireLoad", 256));
        specs.add(new StringInputHandler(14, "setHeatLimit", 256));
        specs.add(new StringInputHandler(15, "setBendRadius", 256));
        specs.add(new StringInputHandler(16, "setRadTolerance", 256));
        specs.add(new IdOrNameRefInputHandler(17, "setTeam", ItemCategoryController.getInstance(), ItemCategory.class, ItemDomainName.cableCatalog.getValue()));

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
