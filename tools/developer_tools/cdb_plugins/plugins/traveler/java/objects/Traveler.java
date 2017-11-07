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
public class Traveler extends TravelerObject {
    
    private String title; 
    private String description; 
    private double status; 
    private String createdBy; 
    private String createdOn; 
    private String referenceForm; 
    private int totalInput;
    private int finishedInput; 
    private String updatedBy;
    private String updatedOn; 
    private boolean archived; 
    private LinkedList<String> notes; 
    private LinkedList<String> data; 
    private int activeForm; 
    
    private LinkedList<FormRef> forms; 
    private String html; 
    
    private String deadline; 
    
    private LinkedList<SharedGroup> sharedGroup;
    private LinkedList<SharedWith> sharedWith; 
    private LinkedList<String> devices; 
    
    // Calculated cache variables 
    private Integer progress; 
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

    public String getReferenceForm() {
        return referenceForm;
    }

    public int getTotalInput() {
        return totalInput;
    }

    public int getFinishedInput() {
        return finishedInput;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public boolean isArchived() {
        return archived;
    }

    public LinkedList<String> getNotes() {
        return notes;
    }

    public LinkedList<String> getData() {
        return data;
    }

    public int getActiveForm() {
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

    public Integer getProgress() {
        if (progress == null) {
            //Load completion pie chart model                
            double doubleProgress = (finishedInput * 1.0) / (totalInput * 1.0) * 100; 
            this.progress = (int) doubleProgress; 
        }
        return progress;
    }

    public String getFormName() {
        return FormName;
    }

    public void setFormName(String FormName) {
        this.FormName = FormName;
    }
      
}
