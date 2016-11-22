/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.objects;

/**
 *
 * @author djarosz
 */
public class SharedWith extends TravelerObject {
    private String username;
    private int access; 

    public String getUsername() {
        return username;
    }

    public int getAccess() {
        return access;
    }
    
    
}
