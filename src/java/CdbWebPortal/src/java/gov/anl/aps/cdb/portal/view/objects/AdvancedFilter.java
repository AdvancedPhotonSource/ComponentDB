/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class AdvancedFilter {
    
    private String name = null;
    private String description = null;
    private List<AdvancedFilterParameter> parameters = new ArrayList<>();
    
    public AdvancedFilter(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    public List<AdvancedFilterParameter> getParameters() {
        return parameters;
    }
    
    public void addParameter(String name, String description) {
        AdvancedFilterParameter parameter = new AdvancedFilterParameter(name, description);
        parameters.add(parameter);
    }

}
