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
public class ReleasedForm extends TravelerObject {
    
    private static final String FAKE_ITEM_CHOICE_ID="NULL";
        
    private String title;
    private String description;
    private String releasedBy;
    private String releasedOn;
    private LinkedList<String> tags;
    private int status; 
    private String formType;
    private String archivedOn;
    private String archivedBy;
    private String ver; 
    
    private FormContent base;
    private FormContent discrepancy;

    public static ReleasedForm createCustomAlwaysSelectLatest() {
        ReleasedForm releasedForm = new ReleasedForm(); 
        releasedForm.ver = "N/A"; 
        releasedForm.releasedOn = "Automatically select latest";
        releasedForm.releasedBy = "N/A";
        releasedForm._id = FAKE_ITEM_CHOICE_ID;
        return releasedForm; 
    }
    
    public boolean isItemFake() {
        return _id.equals(FAKE_ITEM_CHOICE_ID); 
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getReleasedBy() {
        return releasedBy;
    }

    public String getReleasedOn() {
        return releasedOn;
    }

    public LinkedList<String> getTags() {
        return tags;
    }

    public int getStatus() {
        return status;
    }

    public String getFormType() {
        return formType;
    }

    public String getArchivedOn() {
        return archivedOn;
    }

    public String getArchivedBy() {
        return archivedBy;
    }

    public String getVer() {
        return ver;
    }

    public FormContent getBase() {
        return base;
    }

    public FormContent getDiscrepancy() {
        return discrepancy;
    }
    
    
    
}
