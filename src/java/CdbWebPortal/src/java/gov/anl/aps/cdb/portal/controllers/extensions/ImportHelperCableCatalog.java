/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.SourceController;

/**
 *
 * @author craig
 */
public class ImportHelperCableCatalog extends ImportHelperBase {


    protected static String completionUrlValue = "/views/itemDomainCableCatalog/list?faces-redirect=true";
    
    @Override
    protected void createColumnModels_() {
        columns.add(new StringColumnModel("Cable Type", "cableType", "setCableType", true, "M 24"));
        columns.add(new StringColumnModel("Description", "description", "setDescription", false, "24 fiber single-unit"));
        columns.add(new UrlColumnModel("Link URL", "urlDisplay", "setUrl", false, "http://www.example.com/example"));
        columns.add(new UrlColumnModel("Image URL", "imageUrlDisplay", "setImageUrl", false, "http://www.example.com/example"));
        columns.add(new IdRefColumnModel("Manufacturer", "manufacturer", "setManufacturer", false, "CommScope", SourceController.getInstance()));
        columns.add(new StringColumnModel("Part Number", "partNumber", "setPartNumber", false, "R-024-DS-5K-FSUBR"));
        columns.add(new NumericColumnModel("Diameter", "diameter", "setDiameter", false, "0.4"));
        columns.add(new NumericColumnModel("Weight", "weight", "setWeight", false, "75.2"));
        columns.add(new NumericColumnModel("Conductors", "conductors", "setConductors", false, "24"));
        columns.add(new StringColumnModel("Insulation", "insulation", "setInsulation", false, "PVC"));
        columns.add(new StringColumnModel("Jacket Color", "jacketColor", "setJacketColor", false, "brown"));
        columns.add(new NumericColumnModel("Voltage Rating", "voltageRating", "setVoltageRating", false, "123.45"));
        columns.add(new NumericColumnModel("Fire Load", "fireLoad", "setFireLoad", false, "123.45"));
        columns.add(new NumericColumnModel("Heat Limit", "heatLimit", "setHeatLimit", false, "123.45"));
        columns.add(new NumericColumnModel("Bend Radius", "bendRadius", "setBendRadius", false, "4.9"));
        columns.add(new NumericColumnModel("Rad Tolerance", "radTolerance", "setRadTolerance", false, "123.45"));
        columns.add(new IdRefColumnModel("Owner", "team", "setTeam", true, "26", ItemCategoryController.getInstance()));
    }
    
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    protected boolean isValidationOnly() {
        return false;
    }
    
    @Override
    public int getDataStartRow() {
        return 1;
    }

    
    @Override
    public ItemController getEntityController() {
        return ItemDomainCableCatalogController.getInstance();
    }
}
