/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.objects;

/**
 *
 * @author djarosz
 */
public class TravelerNote extends TravelerObject {
    private String name; 
    private String value; 
    private String inputBy; 
    private String inputOn; 

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getInputBy() {
        return inputBy;
    }

    public String getInputOn() {
        return inputOn;
    }
    
}
