/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

/**
 *
 * @author craig
 */
public class AdvancedFilterParameter {

    private String name;
    private String description;
    private String value;
    private AdvancedFilter filter;
    
    public AdvancedFilterParameter(AdvancedFilter filter, String name, String description) {
        this.name = name;
        this.description = description;
        this.filter = filter;
    }
    
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value != null && !value.equals(this.value)) {
            filter.changedParameterValue(name);
        }
        this.value = value;
    }

}
