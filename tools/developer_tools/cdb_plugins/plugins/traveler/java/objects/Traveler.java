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
public class Traveler extends BinderTraveler {
    
    public Traveler(String id) {
        super();
        this._id = id; 
    }
        
    private double status; 
    private String referenceForm; 
    private double totalInput;
    private double finishedInput;
    private boolean archived; 
    private String archivedOn; 
    private LinkedList<String> notes; 
    private LinkedList<String> data; 
    private String activeForm; 
    
    private LinkedList<FormRef> forms; 
    private String html; 
    
    private String deadline; 
    
    private LinkedList<SharedGroup> sharedGroup;
    private LinkedList<SharedWith> sharedWith; 
    private LinkedList<String> devices; 
    
    private String referenceReleasedForm; 
    private String referenceReleasedFormVer; 
    
    // Calculated cache or temporary variables     
    private String FormName; 

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getStatus() {
        return status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }
    
    public String getLocalCreatedOn() {
        return getLocalTime(createdOn); 
    }

    public String getReferenceForm() {
        return referenceForm;
    }

    public double getTotalInput() {
        return totalInput;
    }

    public double getFinishedInput() {
        return finishedInput;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }
    
    public String getLocalUpdatedOn() {
        return getLocalTime(updatedOn); 
    }

    public boolean isArchived() {
        return archived;
    }

    public String getArchivedOn() {
        return archivedOn;
    }
    
    public String getLocalArchivedOn() {
        return getLocalTime(archivedOn);
    }

    public LinkedList<String> getNotes() {
        return notes;
    }

    public LinkedList<String> getData() {
        return data;
    }

    public String getActiveForm() {
        return activeForm;
    }

    public LinkedList<FormRef> getForms() {
        return forms;
    }

    public String getHtml() {
        return html;
    }

    public LinkedList<SharedGroup> getSharedGroup() {
        return sharedGroup;
    }

    public LinkedList<SharedWith> getSharedWith() {
        return sharedWith;
    }

    public LinkedList<String> getDevices() {
        return devices;
    }

    public String getDeadline() {
        return deadline;
    }    

    public String getFormName() {
        return FormName;
    }

    public void setFormName(String FormName) {
        this.FormName = FormName;
    }
    
    public double getTotalValue() {
        return getTotalInput();
    }
    
    public double getFinishedValue() {
        return getFinishedInput(); 
    }

    public String getReferenceReleasedForm() {
        return referenceReleasedForm;
    }

    public String getReferenceReleasedFormVer() {
        return referenceReleasedFormVer;
    }

    @Override
    public double getProgressTotal() {
        return getTotalInput(); 
    }

    @Override
    public double getProgressFinished() {
        return getFinishedInput(); 
    }
      
}
