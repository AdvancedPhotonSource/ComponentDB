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
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperCableCatalog extends ImportHelperBase<ItemDomainCableCatalog, ItemDomainCableCatalogController> {


    protected static String completionUrlValue = "/views/itemDomainCableCatalog/list?faces-redirect=true";
    
    @Override
    protected InitializeInfo initialize_(
            int actualColumnCount,
            Map<Integer, String> headerValueMap) {

        List<InputColumnModel> inputColumns = new ArrayList<>();        
        inputColumns.add(new InputColumnModel(0, "Name", true, "Cable type name, uniquely identifies cable type."));
        inputColumns.add(new InputColumnModel(1, "Description", false, "Textual description of cable type."));
        inputColumns.add(new InputColumnModel(2, "Documentation URL", false, "Raw URL for documentation pdf file, e.g., http://www.example.com/documentation.pdf"));
        inputColumns.add(new InputColumnModel(3, "Image URL", false, "Raw URL for image file, e.g., http://www.example.com/image.jpg"));
        inputColumns.add(new InputColumnModel(4, "Manufacturer", false, "Manufacturer or vendor, e.g., CommScope"));
        inputColumns.add(new InputColumnModel(5, "Part Number", false, "Manufacturer's part number, e.g., R-024-DS-5K-FSUBR"));
        inputColumns.add(new InputColumnModel(6, "Alt Part Num", false, "Manufacturer's alternate part number, e.g., 760152413"));
        inputColumns.add(new InputColumnModel(7, "Diameter", false, "Diameter in inches (max)."));
        inputColumns.add(new InputColumnModel(8, "Weight", false, "Nominal weight in lbs/1000 feet."));
        inputColumns.add(new InputColumnModel(9, "Conductors", false, "Number of conductors/fibers"));
        inputColumns.add(new InputColumnModel(10, "Insulation", false, "Description of cable insulation."));
        inputColumns.add(new InputColumnModel(11, "Jacket Color", false, "Jacket color."));
        inputColumns.add(new InputColumnModel(12, "Voltage Rating", false, "Voltage rating (VRMS)."));
        inputColumns.add(new InputColumnModel(13, "Fire Load", false, "Fire load rating."));
        inputColumns.add(new InputColumnModel(14, "Heat Limit", false, "Heat limit."));
        inputColumns.add(new InputColumnModel(15, "Bend Radius", false, "Bend radius in inches."));
        inputColumns.add(new InputColumnModel(16, "Rad Tolerance", false, "Radiation tolerance rating."));
        inputColumns.add(new InputColumnModel(17, "Owner", false, "Numeric ID of CDB technical system."));
        
        List<InputHandler> handlers = new ArrayList<>();        
        handlers.add(new StringInputHandler(0, "setName", 128));
        handlers.add(new StringInputHandler(1, "setDescription", 256));
        handlers.add(new StringInputHandler(2, "setUrl", 256));
        handlers.add(new StringInputHandler(3, "setImageUrl", 256));
        handlers.add(new IdOrNameRefInputHandler(4, "setManufacturerSource", SourceController.getInstance(), Source.class, null));
        handlers.add(new StringInputHandler(5, "setPartNumber", 32));
        handlers.add(new StringInputHandler(6, "setAltPartNumber", 256));
        handlers.add(new StringInputHandler(7, "setDiameter", 256));
        handlers.add(new StringInputHandler(8, "setWeight", 256));
        handlers.add(new StringInputHandler(9, "setConductors", 256));
        handlers.add(new StringInputHandler(10, "setInsulation", 256));
        handlers.add(new StringInputHandler(11, "setJacketColor", 256));
        handlers.add(new StringInputHandler(12, "setVoltageRating", 256));
        handlers.add(new StringInputHandler(13, "setFireLoad", 256));
        handlers.add(new StringInputHandler(14, "setHeatLimit", 256));
        handlers.add(new StringInputHandler(15, "setBendRadius", 256));
        handlers.add(new StringInputHandler(16, "setRadTolerance", 256));
        handlers.add(new IdOrNameRefInputHandler(17, "setTeam", ItemCategoryController.getInstance(), ItemCategory.class, ItemDomainName.cableCatalog.getValue()));

        List<OutputColumnModel> outputColumns = new ArrayList<>();        
        outputColumns.add(new OutputColumnModel("Name", "name"));
        outputColumns.add(new OutputColumnModel("Description", "description"));
        outputColumns.add(new OutputColumnModel("Documentation URL", "urlDisplay"));
        outputColumns.add(new OutputColumnModel("Image URL", "imageUrlDisplay"));
        outputColumns.add(new OutputColumnModel("Manufacturer", "manufacturer"));
        outputColumns.add(new OutputColumnModel("Part Number", "partNumber"));
        outputColumns.add(new OutputColumnModel("Alt Part Num", "altPartNumber"));
        outputColumns.add(new OutputColumnModel("Diameter", "diameter"));
        outputColumns.add(new OutputColumnModel("Weight", "weight"));
        outputColumns.add(new OutputColumnModel("Conductors", "conductors"));
        outputColumns.add(new OutputColumnModel("Insulation", "insulation"));
        outputColumns.add(new OutputColumnModel("Jacket Color", "jacketColor"));
        outputColumns.add(new OutputColumnModel("Voltage Rating", "voltageRating"));
        outputColumns.add(new OutputColumnModel("Fire Load", "fireLoad"));
        outputColumns.add(new OutputColumnModel("Heat Limit", "heatLimit"));
        outputColumns.add(new OutputColumnModel("Bend Radius", "bendRadius"));
        outputColumns.add(new OutputColumnModel("Rad Tolerance", "radTolerance"));
        outputColumns.add(new OutputColumnModel("Owner", "team"));

        ValidInfo validInfo = new ValidInfo(true, "");
        
        return new InitializeInfo(inputColumns, handlers, outputColumns, validInfo);
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
