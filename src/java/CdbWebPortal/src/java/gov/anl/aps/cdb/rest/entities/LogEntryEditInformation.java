/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class LogEntryEditInformation {

    private int itemId;
    private String logEntry;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Date effectiveDate;

    public LogEntryEditInformation() {
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getLogEntry() {
        return logEntry;
    }

    public void setLogEntry(String logEntry) {
        this.logEntry = logEntry;
    }
    
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    
}
