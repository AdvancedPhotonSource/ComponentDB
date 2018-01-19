/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.objects;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author djarosz
 */
public class Binder extends BinderTraveler {
    
    public Binder() {
        super();
    }
    
    public Binder(String id) {
        super();
        this._id = id; 
    }
        
    private double totalValue;
    private double inProgressValue;
    private double finishedValue;
    
    private boolean archived;
    private Integer publicAccess; 
    private Integer status;
    
    private LinkedList<String> tags;     
    private LinkedList<BinderWorksReference> works; 
    
    private LinkedList<SharedGroup> sharedGroup; 
    private LinkedList<SharedWith> sharedWith;    
    
    // Temporary or cached variables
    private List<Traveler> travelerList; 
    
    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(int totalValue) {
        this.totalValue = totalValue;
    }

    public double getInProgressValue() {
        return inProgressValue;
    }

    public void setInProgressValue(int inProgressValue) {
        this.inProgressValue = inProgressValue;
    }
    
    public double getFinishedValue() {
        return finishedValue;
    }

    public void setFinishedValue(int finishedValue) {
        this.finishedValue = finishedValue;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Integer getPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(Integer publicAccess) {
        this.publicAccess = publicAccess;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LinkedList<String> getTags() {
        return tags;
    }

    public void setTags(LinkedList<String> tags) {
        this.tags = tags;
    }

    public LinkedList<BinderWorksReference> getWorks() {
        return works;
    }

    public void setWorks(LinkedList<BinderWorksReference> works) {
        this.works = works;
    }

    public LinkedList<SharedGroup> getSharedGroup() {
        return sharedGroup;
    }

    public void setSharedGroup(LinkedList<SharedGroup> sharedGroup) {
        this.sharedGroup = sharedGroup;
    }

    public LinkedList<SharedWith> getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(LinkedList<SharedWith> sharedWith) {
        this.sharedWith = sharedWith;
    }

    @Override
    public String getFormName() {
        return "N/A - Binder"; 
    }
    
    @Override
    public double getProgressTotal() {
        return getTotalValue(); 
    }

    @Override
    public double getProgressFinished() {
        return getFinishedValue()+ getInProgressValue(); 
    } 

    public List<Traveler> getTravelerList() {
        return travelerList;
    }

    public void setTravelerList(List<Traveler> travelerList) {
        this.travelerList = travelerList;
    }
    
}
