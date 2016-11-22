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
public class Travelers extends TravelerObject {
    
    private LinkedList<Traveler> travelers; 

    public LinkedList<Traveler> getTravelers() {
        return travelers;
    }
    
}
