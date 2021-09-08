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
    private boolean unchangeable = false;
    private boolean displayed = false;
                    
    public ColumnModeOptions(ImportMode mode, boolean required, boolean unchangeable, boolean displayed) {
        this.mode = mode;
        this.required = required;
        this.displayed = displayed;
    }

    public ImportMode getMode() {
        return mode;
    }

    public boolean isRequired() {
        return required;
    }
    
    public boolean isUnchangeable() {
        return unchangeable;
    }
    
    public boolean isDisplayed() {
        return displayed;
    }
    
    public static List<ColumnModeOptions> empty() {
        List<ColumnModeOptions> options = new ArrayList<>();
        return options;
    }
    
    public static List<ColumnModeOptions> rCREATE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.CREATE, true, false, false));
        return options;
    }
    
    public static List<ColumnModeOptions> oCREATE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.CREATE, false, false, false));
        return options;
    }
    
    public static List<ColumnModeOptions> rUPDATE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.UPDATE, true, false, false));
        options.add(new ColumnModeOptions(ImportMode.COMPARE, true, false, false));
        return options;
    }
    
    public static List<ColumnModeOptions> oUPDATE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.UPDATE, false, false, false));
        options.add(new ColumnModeOptions(ImportMode.COMPARE, false, false, false));
        return options;
    }
    
    public static List<ColumnModeOptions> uUPDATE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.UPDATE, false, true, false));
        options.add(new ColumnModeOptions(ImportMode.COMPARE, false, true, false));
        return options;
    }
    
    public static List<ColumnModeOptions> oCREATEoUPDATE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.CREATE, false, false, false));
        options.add(new ColumnModeOptions(ImportMode.UPDATE, false, false, false));
        options.add(new ColumnModeOptions(ImportMode.COMPARE, false, false, false));
        return options;
    }
    
    public static List<ColumnModeOptions> oCREATErUPDATE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.CREATE, false, false, false));
        options.add(new ColumnModeOptions(ImportMode.UPDATE, true, false, false));
        options.add(new ColumnModeOptions(ImportMode.COMPARE, true, false, false));
        return options;
    }
    
    public static List<ColumnModeOptions> rCREATErUPDATE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.CREATE, true, false, false));
        options.add(new ColumnModeOptions(ImportMode.UPDATE, true, false, false));
        options.add(new ColumnModeOptions(ImportMode.COMPARE, true, false, false));
        return options;
    }
    
    public static List<ColumnModeOptions> rdCREATErUPDATE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.CREATE, true, false, true));
        options.add(new ColumnModeOptions(ImportMode.UPDATE, true, false, false));
        options.add(new ColumnModeOptions(ImportMode.COMPARE, true, false, false));
        return options;
    }
    
    public static List<ColumnModeOptions> rUPDATErDELETE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.UPDATE, true, false, false));
        options.add(new ColumnModeOptions(ImportMode.COMPARE, true, false, false));
        options.add(new ColumnModeOptions(ImportMode.DELETE, true, false, false));
        return options;
    }
    
    public static List<ColumnModeOptions> rUPDATErCOMPARE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.UPDATE, true, false, false));
        options.add(new ColumnModeOptions(ImportMode.COMPARE, true, false, false));
        return options;
    }
    
    public static List<ColumnModeOptions> rUPDATErDELETErCOMPARE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.UPDATE, true, false, false));
        options.add(new ColumnModeOptions(ImportMode.DELETE, true, false, false));
        options.add(new ColumnModeOptions(ImportMode.COMPARE, true, false, false));
        return options;
    }
    
     public static List<ColumnModeOptions> rdUPDATErdDELETErdCOMPARE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.UPDATE, true, false, true));
        options.add(new ColumnModeOptions(ImportMode.DELETE, true, false, true));
        options.add(new ColumnModeOptions(ImportMode.COMPARE, true, false, true));
        return options;
    }
    
   public static List<ColumnModeOptions> rDELETE() {
        List<ColumnModeOptions> options = new ArrayList<>();
        options.add(new ColumnModeOptions(ImportMode.DELETE, true, false, false));
        return options;
    }
    
}
