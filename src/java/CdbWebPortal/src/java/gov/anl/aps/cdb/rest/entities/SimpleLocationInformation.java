/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

/**
 *
 * @author djarosz
 */
public class SimpleLocationInformation {
    
    private int locatableItemId; 
    private Integer locationItemId;
    private String locationDetails; 
    
    public SimpleLocationInformation() {
        
    }

    public SimpleLocationInformation(int locatableItemId, Integer locationItemId, String locationDetails) {
        this.locatableItemId = locatableItemId;
        this.locationItemId = locationItemId;
          this.locationDetails = locationDetails;
    }

    public int getLocatableItemId() {
        return locatableItemId;
    }

    public void setLocatableItemId(int locatableItemId) {
        this.locatableItemId = locatableItemId;
    }

    public Integer getLocationItemId() {
        return locationItemId;
    }

    public void setLocationItemId(Integer locationItemId) {
        this.locationItemId = locationItemId;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }
}
