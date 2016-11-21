/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.objects;

/**
 *
 * @author djarosz
 */
public class TravelerDatum extends TravelerObject {
    private String name; 
    private String value; 
    private String inputType; 
    private String inputBy; 
    private String inputOn; 
    private String traveler; 

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getInputType() {
        return inputType;
    }

    public String getInputBy() {
        return inputBy;
    }

    public String getInputOn() {
        return inputOn;
    }

    public String getTraveler() {
        return traveler;
    }
}
