/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.objects;

/**
 *
 * @author djarosz
 */
public abstract class BinderTraveler extends TravelerObject {
    
    protected String title;    
    protected String createdBy; 
    protected String createdOn; 
    protected String description; 
    protected String updatedBy; 
    protected String updatedOn; 
    
    // Calculated cache or temporary variables 
    private Integer progress; 
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }
    
    public Integer getProgress() {
        if (progress == null) {
            //Load completion pie chart model                
            double doubleProgress = (getProgressFinished()* 1.0) / (getProgressTotal()* 1.0) * 100; 
            this.progress = (int) doubleProgress; 
        }
        return progress;
    }
    
    public boolean isBinder() {
        return this instanceof Binder; 
    }
    
    public boolean isTraveler() {
        return this instanceof Traveler; 
    }
    
    public abstract double getProgressTotal();

    public abstract double getProgressFinished();
    
    public abstract String getFormName();
}
