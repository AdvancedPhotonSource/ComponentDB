/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.view.objects;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Traveler;

/**
 *
 * @author djarosz
 */
public class CreatedFromTemplateSummaryObject {
    
    private Item assignedToItem; 
    private Traveler traveler; 
    private String latestFormVersion; 

    public CreatedFromTemplateSummaryObject(Item assignedToItem, Traveler traveler, String latestFormVersion) {
        this.assignedToItem = assignedToItem;
        this.traveler = traveler;
        this.latestFormVersion = latestFormVersion;
    }   

    public Item getAssignedToItem() {
        return assignedToItem;
    }

    public Traveler getTraveler() {
        return traveler;
    }

    public String getLatestFormVersion() {
        return latestFormVersion;
    }
    
}
