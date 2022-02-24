/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class AdvancedFilter {
    
    private String name = null;
    private String description = null;
    private List<AdvancedFilterParameter> parameters = new ArrayList<>();
    private ItemController controller = null;
    
    public AdvancedFilter(String name, String description, ItemController controller) {
        this.name = name;
        this.description = description;
        this.controller = controller;
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
        AdvancedFilterParameter parameter = new AdvancedFilterParameter(this, name, description);
        parameters.add(parameter);
    }
    
    public String getParametersString() {
        
        String result = "";
        boolean first = true;
        for (AdvancedFilterParameter parameter : getParameters()) {
            String value = parameter.getValue();
            if (value != null && !value.isEmpty()) {
                if (!first) {
                    result = result + ", ";
                } else {
                    first = false;
                }
                result  = result + parameter.getName() + ": " + value;
            }
        }
        return result;
    }

    public Map<String, String> getParameterValueMap() {
        Map<String, String> parameterValueMap = new HashMap();
        for (AdvancedFilterParameter parameter : getParameters()) {
            parameterValueMap.put(parameter.getName(), parameter.getValue());
        }
        return parameterValueMap;
    }

    void changedParameterValue(String name) {
        controller.advancedFilterChanged(name);
    }

}
