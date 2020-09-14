/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.controllers.UserGroupController;
import gov.anl.aps.cdb.portal.controllers.UserInfoController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefListColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
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
        
        specs.add(new StringColumnSpec(0, "Name", "name", "setName", true, "Cable type name, uniquely identifies cable type.", 128));
        specs.add(new StringColumnSpec(1, "Alt Name", "alternateName", "setAlternateName", false, "Alternate cable type name.", 128));
        specs.add(new StringColumnSpec(2, "Description", "description", "setDescription", false, "Textual description of cable type.", 256));
        specs.add(new StringColumnSpec(3, "Documentation URL", "urlDisplay", "setUrl", false, "Raw URL for documentation pdf file, e.g., http://www.example.com/documentation.pdf", 256));
        specs.add(new StringColumnSpec(4, "Image URL", "imageUrlDisplay", "setImageUrl", false, "Raw URL for image file, e.g., http://www.example.com/image.jpg", 256));
        specs.add(new IdOrNameRefColumnSpec(5, "Manufacturer", ImportHelperCatalogBase.KEY_MFR, "", false, "ID or name of CDB source for manufacturer.", SourceController.getInstance(), Source.class, ""));
        specs.add(new StringColumnSpec(6, "Part Number", ImportHelperCatalogBase.KEY_PART_NUM, "setPartNumber", false, "Manufacturer's part number.", 32));
        specs.add(new StringColumnSpec(7, "Alt Part Num", "altPartNumber", "setAltPartNumber", false, "Manufacturer's alternate part number, e.g., 760152413", 256));
        specs.add(new IdOrNameRefListColumnSpec(8, "Technical System", "itemCategoryString", "setItemCategoryList", false, "Numeric ID of CDB technical system.", ItemCategoryController.getInstance(), List.class, ItemDomainName.cableCatalog.getValue()));
        specs.add(new IdOrNameRefListColumnSpec(9, "Project", "itemProjectString", "setItemProjectList", true, "Comma-separated list of IDs of CDB project(s).", ItemProjectController.getInstance(), List.class, ""));
        specs.add(new IdOrNameRefColumnSpec(10, "Owner User", "ownerUserName", "setOwnerUser", false, "ID or name of CDB owner user.", UserInfoController.getInstance(), UserInfo.class, ""));
        specs.add(new IdOrNameRefColumnSpec(11, "Owner Group", "ownerUserGroupName", "setOwnerUserGroup", false, "ID or name of CDB owner user group.", UserGroupController.getInstance(), UserGroup.class, ""));
        specs.add(new StringColumnSpec(12, "Diameter", "diameter", "setDiameter", false, "Diameter in inches (max).", 256));
        specs.add(new StringColumnSpec(13, "Weight", "weight", "setWeight", false, "Nominal weight in lbs/1000 feet.", 256));
        specs.add(new StringColumnSpec(14, "Conductors", "conductors", "setConductors", false, "Number of conductors/fibers", 256));
        specs.add(new StringColumnSpec(15, "Insulation", "insulation", "setInsulation", false, "Description of cable insulation.", 256));
        specs.add(new StringColumnSpec(16, "Jacket Color", "jacketColor", "setJacketColor", false, "Jacket color.", 256));
        specs.add(new StringColumnSpec(17, "Voltage Rating", "voltageRating", "setVoltageRating", false, "Voltage rating (VRMS).", 256));
        specs.add(new StringColumnSpec(18, "Fire Load", "fireLoad", "setFireLoad", false, "Fire load rating.", 256));
        specs.add(new StringColumnSpec(19, "Heat Limit", "heatLimit", "setHeatLimit", false, "Heat limit.", 256));
        specs.add(new StringColumnSpec(20, "Bend Radius", "bendRadius", "setBendRadius", false, "Bend radius in inches.", 256));
        specs.add(new StringColumnSpec(21, "Rad Tolerance", "radTolerance", "setRadTolerance", false, "Radiation tolerance rating.", 256));

        return specs;
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
        return super.createEntityInstance(rowMap);
    }  
}
