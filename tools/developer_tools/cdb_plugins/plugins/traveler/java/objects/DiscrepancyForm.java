/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.objects;

import java.util.HashMap;

/**
 *
 * @author darek
 */
public class DiscrepancyForm {
    
    private String html;
    private String reference; 
    private HashMap<String, String> mapping; 
    private HashMap<String, String> labels; 

    public String getHtml() {
        return html;
    }

    public String getReference() {
        return reference;
    }

    public HashMap<String, String> getMapping() {
        return mapping;
    }

    public HashMap<String, String> getLabels() {
        return labels;
    }
    
}
