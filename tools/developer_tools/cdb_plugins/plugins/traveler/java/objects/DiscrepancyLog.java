/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.objects;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author darek
 */
public class DiscrepancyLog extends TravelerObject {
    
    private String referenceForm;
    private LinkedList<DiscrepancyLogRecord> records; 
    private String inputBy;
    private String inputOn; 
    
    private HashMap<String, String> recordsMap; 

    public String getReferenceForm() {
        return referenceForm;
    }

    public LinkedList<DiscrepancyLogRecord> getRecords() {
        return records;
    }

    public String getInputBy() {
        return inputBy;
    }

    public String getInputOn() {
        return inputOn;
    }

    public HashMap<String, String> getRecordsMap() {
        if (recordsMap == null) {
            recordsMap = new HashMap<>(); 
            for (DiscrepancyLogRecord rec : records) {
                String name = rec.getName();
                String value = rec.getValue();
                
                recordsMap.put(name, value); 
            }
        }
        return recordsMap;
    }
    
    public String getRecordByKey(String key) {
        HashMap<String, String> recordsMap = getRecordsMap();
        return recordsMap.get(key); 
    }
    
}
