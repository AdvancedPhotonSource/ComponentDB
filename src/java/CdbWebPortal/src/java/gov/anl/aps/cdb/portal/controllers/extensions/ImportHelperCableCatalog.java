/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;

/**
 *
 * @author craig
 */
public class ImportHelperCableCatalog extends ImportHelperBase {


    protected static String completionUrlValue = "/views/itemDomainCableCatalog/list?faces-redirect=true";
    
    @Override
    protected void createColumnModels_() {
        columns.add(new ColumnModel("Cable Type", "cableType", ColType.STRING, "setCableType", true, "M 24"));
        columns.add(new ColumnModel("Description", "description", ColType.STRING, "setDescription", false, "24 fiber single-unit"));
        columns.add(new ColumnModel("Link URL", "urlDisplay", ColType.URL, "setUrl", false, "http://www.example.com/example"));
        columns.add(new ColumnModel("Image URL", "imageUrlDisplay", ColType.URL, "setImageUrl", false, "http://www.example.com/example"));
        columns.add(new ColumnModel("Manufacturer", "manufacturer", ColType.STRING, "setManufacturer", false, "CommScope"));
        columns.add(new ColumnModel("Part Number", "partNumber", ColType.STRING, "setPartNumber", false, "R-024-DS-5K-FSUBR"));
        columns.add(new ColumnModel("Diameter", "diameter", ColType.NUMERIC, "setDiameter", false, "0.4"));
        columns.add(new ColumnModel("Weight", "weight", ColType.NUMERIC, "setWeight", false, "75.2"));
        columns.add(new ColumnModel("Conductors", "conductors", ColType.NUMERIC, "setConductors", false, "24"));
        columns.add(new ColumnModel("Insulation", "insulation", ColType.STRING, "setInsulation", false, "PVC"));
        columns.add(new ColumnModel("Jacket Color", "jacketColor", ColType.STRING, "setJacketColor", false, "brown"));
        columns.add(new ColumnModel("Voltage Rating", "voltageRating", ColType.NUMERIC, "setVoltageRating", false, "123.45"));
        columns.add(new ColumnModel("Fire Load", "fireLoad", ColType.NUMERIC, "setFireLoad", false, "123.45"));
        columns.add(new ColumnModel("Heat Limit", "heatLimit", ColType.NUMERIC, "setHeatLimit", false, "123.45"));
        columns.add(new ColumnModel("Bend Radius", "bendRadius", ColType.NUMERIC, "setBendRadius", false, "4.9"));
        columns.add(new ColumnModel("Rad Tolerance", "radTolerance", ColType.NUMERIC, "setRadTolerance", false, "123.45"));
        columns.add(new ColumnModel("Category Id", "team", ColType.STRING, "setTeam", true, "26"));
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
