/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class ColumnModeOptions {

    private ImportMode mode;
    private boolean required;
                    
    public ColumnModeOptions(ImportMode mode, boolean required) {
        this.mode = mode;
        this.required = required;
    }

    public ImportMode getMode() {
        return mode;
    }

    public boolean isRequired() {
        return required;
    }
    
    public static List<ColumnModeOptions> rCREATE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.CREATE, true));
        return options;
    }
    
    public static List<ColumnModeOptions> oCREATE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.CREATE, false));
        return options;
    }
    
    public static List<ColumnModeOptions> oCREATEoUPDATE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.CREATE, false));
        options.add(new ColumnModeOptions(ImportMode.UPDATE, false));
        return options;
    }
    
    public static List<ColumnModeOptions> oCREATErUPDATE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.CREATE, false));
        options.add(new ColumnModeOptions(ImportMode.UPDATE, true));
        return options;
    }
    
    public static List<ColumnModeOptions> rCREATErUPDATE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.CREATE, true));
        options.add(new ColumnModeOptions(ImportMode.UPDATE, true));
        return options;
    }
    
    public static List<ColumnModeOptions> rUPDATErDELETE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.UPDATE, true));
        options.add(new ColumnModeOptions(ImportMode.DELETE, true));
        return options;
    }
    
    public static List<ColumnModeOptions> rUPDATErDELETErCOMPARE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.UPDATE, true));
        options.add(new ColumnModeOptions(ImportMode.DELETE, true));
        options.add(new ColumnModeOptions(ImportMode.COMPARE, true));
        return options;
    }
    
    public static List<ColumnModeOptions> rDELETE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.DELETE, true));
        return options;
    }
    
}
