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
public class TravelerNotes extends TravelerObject {
    LinkedList<TravelerNote> notes; 

    public LinkedList<TravelerNote> getNotes() {
        return notes;
    }
    
}
