/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Serializable;

/**
 *
 * @author djarosz
 */
public class TravelerObject implements Serializable{
    
    protected String _id; 
    private int __v;

    public String getId() {
        return _id;
    }

    public int getV() {
        return __v;
    }
    
    /**
     * Conversion to JSON string representation.
     *
     * @return JSON string
     */
    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
}
