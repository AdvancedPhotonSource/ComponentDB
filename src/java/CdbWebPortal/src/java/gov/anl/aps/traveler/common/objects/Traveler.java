/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.traveler.common.objects;

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
    
    private LinkedList<SharedGroup> sharedGroup;
    private LinkedList<SharedWith> sharedWith; 
    private LinkedList<String> devices; 

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
    
    
}
