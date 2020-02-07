/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author djarosz
 */
public class TravelerObject implements Serializable{
    
    private final String DATE_TIME_FORMAT = "E MMM dd HH:mm:ss z yyyy";         
    
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
    
    public String getLocalTime(String travelerAppTime) {
        if (travelerAppTime == null) {
            return null;
        }
        Instant parse = Instant.parse(travelerAppTime);      
        ZoneId systemTimezone = ZoneOffset.systemDefault();
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);        
        
        ZonedDateTime date = ZonedDateTime.ofInstant(parse, systemTimezone);                
        
        return date.format(FORMATTER);
    }
}
