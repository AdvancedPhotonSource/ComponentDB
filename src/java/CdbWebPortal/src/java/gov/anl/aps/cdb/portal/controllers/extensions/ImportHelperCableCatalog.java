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
        columns.add(new StringColumnModel("Name", "name", "setName", true, "Cable type name, uniquely identifies cable type.", 128));
        columns.add(new StringColumnModel("Description", "description", "setDescription", false, "Textual description of cable type.", 256));
        columns.add(new UrlColumnModel("Documentation URL", "urlDisplay", "setUrl", false, "Raw URL for documentation pdf file, e.g., http://www.example.com/documentation.pdf", 256));
        columns.add(new UrlColumnModel("Image URL", "imageUrlDisplay", "setImageUrl", false, "Raw URL for image file, e.g., http://www.example.com/image.jpg", 256));
        columns.add(new IdRefColumnModel("Manufacturer", "manufacturer", "setManufacturerId", false, "Manufacturer or vendor, e.g., CommScope", 0, SourceController.getInstance()));
        columns.add(new StringColumnModel("Part Number", "partNumber", "setPartNumber", false, "Manufacturer's part number, e.g., R-024-DS-5K-FSUBR", 32));
        columns.add(new StringColumnModel("Alt Part Num", "altPartNumber", "setAltPartNumber", false, "Manufacturer's alternate part number, e.g., 760152413", 256));
        columns.add(new NumericColumnModel("Diameter", "diameter", "setDiameter", false, "Diameter in inches (max).", 256));
        columns.add(new NumericColumnModel("Weight", "weight", "setWeight", false, "Nominal weight in lbs/1000 feet.", 256));
        columns.add(new NumericColumnModel("Conductors", "conductors", "setConductors", false, "Number of conductors/fibers", 256));
        columns.add(new StringColumnModel("Insulation", "insulation", "setInsulation", false, "Description of cable insulation.", 256));
        columns.add(new StringColumnModel("Jacket Color", "jacketColor", "setJacketColor", false, "Jacket color.", 256));
        columns.add(new NumericColumnModel("Voltage Rating", "voltageRating", "setVoltageRating", false, "Voltage rating (VRMS).", 256));
        columns.add(new NumericColumnModel("Fire Load", "fireLoad", "setFireLoad", false, "Fire load rating.", 256));
        columns.add(new NumericColumnModel("Heat Limit", "heatLimit", "setHeatLimit", false, "Heat limit.", 256));
        columns.add(new NumericColumnModel("Bend Radius", "bendRadius", "setBendRadius", false, "Bend radius in inches.", 256));
        columns.add(new NumericColumnModel("Rad Tolerance", "radTolerance", "setRadTolerance", false, "Radiation tolerance rating.", 256));
        columns.add(new IdRefColumnModel("Owner", "team", "setTeamId", false, "Numeric ID of CDB technical system.", 0, ItemCategoryController.getInstance()));
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
