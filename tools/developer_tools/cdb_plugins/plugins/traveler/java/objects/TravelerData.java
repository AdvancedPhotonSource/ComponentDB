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
public class TravelerData extends TravelerObject {
    
    private LinkedList<TravelerDatum> data;

    public LinkedList<TravelerDatum> getData() {
        return data;
    }
    
}
