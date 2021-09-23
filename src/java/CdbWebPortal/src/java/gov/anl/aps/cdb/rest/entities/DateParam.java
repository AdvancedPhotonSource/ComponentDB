/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.DatatypeConverter;

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
        
        final DateFormat pythonDateStrFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final DateFormat justDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String exceptionMessage = ""; 
        
        Date parsed = null;

        try {
            Calendar parseDateTime = DatatypeConverter.parseDateTime(dateStr);
            parsed = parseDateTime.getTime();
        } catch (IllegalArgumentException ex) {
            exceptionMessage = ex.getMessage(); 
        }

        if (parsed == null) {
            try {
                parsed = pythonDateStrFormat.parse(dateStr);
            } catch (ParseException e) {
                exceptionMessage += "\n" + e.getMessage();
            }
        }

        if (parsed == null) {
            try {
                parsed = justDateFormat.parse(dateStr);
            } catch (ParseException e) {
                exceptionMessage += "\n" + e.getMessage();
            }
        }

        if (parsed != null) {
            this.date = parsed;
        } else {
            String message = "Error parsing date string. " + exceptionMessage;            

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
