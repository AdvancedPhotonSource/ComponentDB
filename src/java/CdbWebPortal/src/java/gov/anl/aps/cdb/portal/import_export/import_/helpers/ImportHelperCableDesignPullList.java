/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class ImportHelperCableDesignPullList 
        extends ImportHelperBase<ItemDomainCableDesign, ItemDomainCableDesignController> {
    
    @Override
    public ItemDomainCableDesignController getEntityController() {
        return ItemDomainCableDesignController.getInstance();
    }

    @Override
    public String getFilenameBase() {
        return "Cable Design Pull List";
    }
    
    public boolean supportsModeCreate() {
        return false;
    }

    public boolean supportsModeExport() {
        return true;
    }

    @Override
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new StringColumnSpec(
                "Name", 
                "name", 
                null, 
                "Cable name, uniquely identifies cable.", 
                "getName",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "Technical System",
                "itemCategoryString",
                null,
                "CDB technical system name (or pipe-separated list of names).",
                "getItemCategoryString",
                null,
                0));
        
        specs.add(new StringColumnSpec(
                "Description", 
                "description", 
                null, 
                "Description of cable.", 
                "getDescription",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "CDB ID", 
                "id", 
                null, 
                "CDB ID of cable.", 
                "getId",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "Endpoints", 
                "endpointsString", 
                null, 
                "Pipe-separated endpoint list.", 
                "getEndpointsString",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "Cable Type", 
                "catalogItemString", 
                null, 
                "Name of assigned CDB cable catalog item.", 
                "getCatalogItemString",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "End1 Device", 
                "endpoint1String", 
                null, 
                "Name of end1 primary connected device.", 
                "getEndpoint1String",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "End1 Port", 
                "endpoint1Port", 
                null, 
                "Name of port on end1 primary connected device.", 
                "getEndpoint1Port",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "End1 Connector", 
                "endpoint1Connector", 
                null, 
                "Name of cable connector for end1 primary connection.", 
                "getEndpoint1Connector",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "End1 Route", 
                "endpoint1Route", 
                null, 
                "Name of routing waypoint/penetration for end1.", 
                "getEndpoint1Route",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "End1 Rack/Location", 
                "endpoint1Location", 
                null, 
                "Name of rack or location for end1 primary device.", 
                "getEndpoint1Location",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "End2 Device", 
                "endpoint2String", 
                null, 
                "Name of end2 primary connected device.", 
                "getEndpoint2String",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "End2 Port", 
                "endpoint2Port", 
                null, 
                "Name of port on end2 primary connected device.", 
                "getEndpoint2Port",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "End2 Connector", 
                "endpoint2Connector", 
                null, 
                "Name of cable connector for end2 primary connection.", 
                "getEndpoint2Connector",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "End2 Route", 
                "endpoint2Route", 
                null, 
                "Name of routing waypoint/penetration for end2.", 
                "getEndpoint2Route",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "End2 Rack/Location", 
                "endpoint2Location", 
                null, 
                "Name of rack or location for end2 primary device.", 
                "getEndpoint2Location",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "External Cable Name", 
                "externalCableName", 
                null, 
                "External name for cable.", 
                "getExternalCableName",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "Import Cable ID", 
                "importCableId", 
                null, 
                "Import identifier for cable.", 
                "getImportCableId",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "Legacy Cable ID", 
                "alternateCableId", 
                null, 
                "Alternate/Legacy identifier for cable.", 
                "getAlternateCableId",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "End1 Description", 
                "endpoint1Description", 
                null, 
                "Description for cable end1.", 
                "getEndpoint1Description",
                null, 
                0));
        
        specs.add(new StringColumnSpec(
                "End2 Description", 
                "endpoint2Description", 
                null, 
                "Description for cable end2.", 
                "getEndpoint2Description",
                null, 
                0));
        
        return specs;
    }
}
