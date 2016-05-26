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
public class Form extends TravelerObject {
    private String html; 
    private String title;
    private String createdBy; 
    private String createdOn; 
    private LinkedList<SharedGroup> sharedGroup; 
    private LinkedList<SharedWith> sharedWith; 
    private String updatedBy; 
    private String updatedOn; 

    public String getHtml() {
        return html;
    }

    public String getTitle() {
        return title;
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
    
    
}
