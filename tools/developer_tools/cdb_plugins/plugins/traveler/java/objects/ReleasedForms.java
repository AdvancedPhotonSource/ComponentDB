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
public class ReleasedForms extends TravelerObject {
    
    private LinkedList<ReleasedForm> releasedForms;     

    public LinkedList<ReleasedForm> getReleasedForms() {
        return releasedForms;
    }
    
}
