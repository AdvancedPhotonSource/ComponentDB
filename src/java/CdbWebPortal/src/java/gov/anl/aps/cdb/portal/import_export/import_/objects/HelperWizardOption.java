/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

/**
 *
 * @author craig
 */
public class HelperWizardOption {

    private String label;
    private String description;
    private String property;
    private HelperOptionType type;
    private ImportMode mode;
    
    public HelperWizardOption(
            String label, String description, String property, HelperOptionType type, ImportMode mode) {
        
        this.label = label;
        this.description = description;
        this.property = property;
        this.type = type;
        this.mode = mode;
        
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public HelperOptionType getType() {
        return type;
    }

    public void setType(HelperOptionType type) {
        this.type = type;
    }

    public ImportMode getMode() {
        return mode;
    }

    public void setMode(ImportMode mode) {
        this.mode = mode;
    }
    
    public static ValidInfo validateIntegerOption(String optionVal, String optionName) {
        boolean isValid = true;
        String validString = "";
        try {
            int intVal = Integer.valueOf(optionVal);
            if (intVal < 0) {
                isValid = false;
                validString = "Option: " + optionName 
                        + " value must be positive integer.";
            }
        } catch (NumberFormatException ex) {
            isValid = false;
            validString = "Option: " + optionName 
                    + " value must be integer, specified value: " + optionVal + ".";
        }
        return new ValidInfo(isValid, validString);
    }
    
}
