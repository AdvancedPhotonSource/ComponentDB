/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.AssignedItemHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.LocationHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.CustomColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.FloatColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import java.util.List;

/**
 *
 * @author craig
 */
public class MachineImportCommon {
    
    public static final String KEY_SORT_ORDER = "importSortOrder";
    public static final String KEY_ASSIGNED_ITEM = "assignedItem";
    public static final String KEY_LOCATION = "location";

    public static final String HEADER_NAME = "Name";
    public static final String HEADER_ALT_NAME = "Alternate Name";
    public static final String HEADER_DESCRIPTION = "Description";
    public static final String HEADER_SORT_ORDER = "Sort Order";
    public static final String HEADER_ASSIGNED_ITEM_DESCRIPTION = "Assigned Catalog/Inventory Item Description";
    public static final String HEADER_ASSIGNED_ITEM = "Assigned Catalog/Inventory Item";
    public static final String HEADER_LOCATION = "Location";

    public static StringColumnSpec nameColumnSpec(List<ColumnModeOptions> options) {
        return new StringColumnSpec(
                HEADER_NAME,
                "name",
                "setName",
                "Machine element name.",
                "getName",
                options,
                128);
    }
    
    public static StringColumnSpec altNameColumnSpec(List<ColumnModeOptions> options) {
        return new StringColumnSpec(
                HEADER_ALT_NAME,
                "alternateName",
                "setAlternateName",
                "Machine element alternate name.",
                "getAlternateName",
                options,
                128);
    }
    
    public static StringColumnSpec descriptionColumnSpec(List<ColumnModeOptions> options) {
        return new StringColumnSpec(
                HEADER_DESCRIPTION,
                "description",
                "setDescription",
                "Machine element description.",
                "getDescription",
                options,
                256);
    }
    
    public static FloatColumnSpec sortOrderColumnSpec(List<ColumnModeOptions> options) {
        return new FloatColumnSpec(
                HEADER_SORT_ORDER,
                KEY_SORT_ORDER,
                "setImportSortOrder",
                "Sort order within parent item (as decimal), defaults to order in input sheet when creating new items.",
                null,
                options);
    }
    
    public static StringColumnSpec assignedItemDescriptionColumnSpec(List<ColumnModeOptions> options) {
        return new StringColumnSpec(
                HEADER_ASSIGNED_ITEM_DESCRIPTION,
                "importAssignedItemDescription",
                "setImportAssignedItemDescription",
                "Description of catalog or inventory item assigned to machine element (for documentation purposes only).",
                null,
                options,
                256);
    }
    
    public static CustomColumnSpec assignedItemColumnSpec(List<ColumnModeOptions> options) {
        AssignedItemHandler assignedItemHandler = new AssignedItemHandler();
        return new CustomColumnSpec(
                HEADER_ASSIGNED_ITEM,
                "importAssignedItemString",
                "CDB ID or name of assigned catalog or inventory item. Name must be unique and prefixed with '#'.",
                null,
                options,
                assignedItemHandler);
    }
    
    public static CustomColumnSpec locationColumnSpec(List<ColumnModeOptions> options) {
        LocationHandler locationHandler = new LocationHandler();        
        return new CustomColumnSpec(
                HEADER_LOCATION,
                "importLocationItemString",
                "CDB ID or name of CDB location item (use of word 'parent' allowed for documentation purposes, it is ignored). Name must be unique and prefixed with '#'.",
                null,
                options,
                locationHandler);
    }
    
}
