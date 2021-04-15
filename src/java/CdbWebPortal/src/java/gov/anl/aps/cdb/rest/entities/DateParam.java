/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author darek
 */
public class DateParam {
    
  private final Date date;

  public DateParam(String dateStr) throws WebApplicationException {
    if (dateStr.isEmpty()) {
      this.date = null;
      return;
    }
    String isoDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    final DateFormat isoDateFormat = new SimpleDateFormat(isoDatePattern);    
    final DateFormat justDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    ParseException parseException1 = null; 
    ParseException parseException2 = null; 
    
    Date parsed = null; 
    
    try {
      parsed = isoDateFormat.parse(dateStr);      
    } catch (ParseException e) {
        // Try just date format
        parseException1 = e; 
    }
    
    if (parsed == null) {
        try {
            parsed = justDateFormat.parse(dateStr);         
        } catch (ParseException e) { 
            parseException2 = e; 
        }
    }
    
    if (parsed != null) {
        this.date = parsed; 
    } else {
        String message = "Error parsing date string. "; 
        if (parseException1 != null) {
            message += "\n " + parseException1.getMessage(); 
        }
        if (parseException2 != null) {
            message += "\n " + parseException2.getMessage(); 
        }
        
        throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
            .entity(message)
            .build());
    }
  }

  public Date getDate() {
    return date;
  }

    @Override
    public String toString() {
        if (date != null) {
            return date.toString(); 
        }
        return toString(); 
    }
    
}
