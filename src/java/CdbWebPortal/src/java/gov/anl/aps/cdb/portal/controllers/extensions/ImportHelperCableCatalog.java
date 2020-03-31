/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;

/**
 *
 * @author craig
 */
public class ImportHelperCableCatalog extends ImportHelperBase<ItemDomainCableCatalog, ItemDomainCableCatalogController> {


    protected static String completionUrlValue = "/views/itemDomainCableCatalog/list?faces-redirect=true";
    
    @Override
    protected void createColumnModels_() {
        columns.add(new StringColumnModel("Name", "name", "setName", true, "Cable type name, uniquely identifies cable type."));
        columns.add(new StringColumnModel("Description", "description", "setDescription", false, "Textual description of cable type."));
        columns.add(new UrlColumnModel("Documentation URL", "urlDisplay", "setUrl", false, "Raw URL for documentation pdf file, e.g., http://www.example.com/documentation.pdf"));
        columns.add(new UrlColumnModel("Image URL", "imageUrlDisplay", "setImageUrl", false, "Raw URL for image file, e.g., http://www.example.com/image.jpg"));
        columns.add(new IdRefColumnModel("Manufacturer", "manufacturer", "setManufacturerId", false, "Manufacturer or vendor, e.g., CommScope", SourceController.getInstance()));
        columns.add(new StringColumnModel("Part Number", "partNumber", "setPartNumber", false, "Manufacturer's part number, e.g., R-024-DS-5K-FSUBR"));
        columns.add(new StringColumnModel("Alt Part Num", "altPartNumber", "setAltPartNumber", false, "Manufacturer's alternate part number, e.g., 760152413"));
        columns.add(new NumericColumnModel("Diameter", "diameter", "setDiameter", false, "Diameter in inches (max)."));
        columns.add(new NumericColumnModel("Weight", "weight", "setWeight", false, "Nominal weight in lbs/1000 feet."));
        columns.add(new NumericColumnModel("Conductors", "conductors", "setConductors", false, "Number of conductors/fibers"));
        columns.add(new StringColumnModel("Insulation", "insulation", "setInsulation", false, "Description of cable insulation."));
        columns.add(new StringColumnModel("Jacket Color", "jacketColor", "setJacketColor", false, "Jacket color."));
        columns.add(new NumericColumnModel("Voltage Rating", "voltageRating", "setVoltageRating", false, "Voltage rating (VRMS)."));
        columns.add(new NumericColumnModel("Fire Load", "fireLoad", "setFireLoad", false, "Fire load rating."));
        columns.add(new NumericColumnModel("Heat Limit", "heatLimit", "setHeatLimit", false, "Heat limit."));
        columns.add(new NumericColumnModel("Bend Radius", "bendRadius", "setBendRadius", false, "Bend radius in inches."));
        columns.add(new NumericColumnModel("Rad Tolerance", "radTolerance", "setRadTolerance", false, "Radiation tolerance rating."));
        columns.add(new IdRefColumnModel("Owner", "team", "setTeamId", true, "Numeric ID of CDB technical system.", ItemCategoryController.getInstance()));
    }
    
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    public int getDataStartRow() {
        return 1;
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
