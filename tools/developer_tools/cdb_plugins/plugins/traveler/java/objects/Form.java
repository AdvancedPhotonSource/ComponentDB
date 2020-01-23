/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.objects;

import java.util.LinkedList;

/**
 *
 * @author djarosz
 */
public class Form extends TravelerObject {
    private String html; 
    private String title;
    private String createdBy; 
    private String createdOn; 
    private LinkedList<SharedGroup> sharedGroup; 
    private LinkedList<SharedWith> sharedWith; 
    private String updatedBy; 
    private String updatedOn; 
    private double status; 
    private String formType;
    
    
    // Calculated cache or temporary variables 
    private transient String travelerInstanceName; 
    private transient ReleasedForms releasedForms;
    private transient ReleasedForm selectedReleasedForm; 
    
    private transient String preferredReleasedId;
    private transient String preferredReleasedVerCache; 
    
    public Form() {
        super();
    }
    public Form(String formId) {
        super();
        _id = formId; 
    }

    public String getHtml() {
        return html;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public LinkedList<SharedGroup> getSharedGroup() {
        return sharedGroup;
    }

    public LinkedList<SharedWith> getSharedWith() {
        return sharedWith;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public double getStatus() {
        return status;
    }

    public String getFormType() {
        return formType;
    }

    public String getTravelerInstanceName() {
        if (travelerInstanceName == null) {
            travelerInstanceName = title; 
        }
        return travelerInstanceName;
    }

    public void setTravelerInstanceName(String travelerInstanceName) {
        this.travelerInstanceName = travelerInstanceName;
    }

    public ReleasedForms getReleasedForms() {
        return releasedForms;
    }

    public void setReleasedForms(ReleasedForms releasedForms) {
        this.releasedForms = releasedForms;
    }

    public ReleasedForm getSelectedReleasedForm() {
        return selectedReleasedForm;
    }

    public void setSelectedReleasedForm(ReleasedForm selectedReleasedForm) {
        this.selectedReleasedForm = selectedReleasedForm;
    }

    public String getPreferredReleasedId() {
        return preferredReleasedId;
    }

    public void setPreferredReleasedId(String preferredReleasedId) {
        this.preferredReleasedId = preferredReleasedId;
    }

    public String getPreferredReleasedVerCache() {
        if (preferredReleasedVerCache == null) {
            return "latest"; 
        }
        return preferredReleasedVerCache;
    }

    public void setPreferredReleasedVerCache(String preferredReleasedVerCache) {
        this.preferredReleasedVerCache = preferredReleasedVerCache;
    }
    
    
}
