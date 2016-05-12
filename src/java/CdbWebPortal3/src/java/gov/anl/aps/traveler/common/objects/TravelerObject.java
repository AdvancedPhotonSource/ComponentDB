/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.traveler.common.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Serializable;

/**
 *
 * @author djarosz
 */
public class TravelerObject implements Serializable{
    
    private String _id; 
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
