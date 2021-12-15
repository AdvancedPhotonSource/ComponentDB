/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author darek
 */
public class TravelerDiscrepancyLog extends TravelerObject {

    private DiscrepancyForm discrepancyForm;
    private LinkedList<DiscrepancyLog> discrepancyLogs;

    private List<ColumnsObject> columns;

    public DiscrepancyForm getDiscrepancyForm() {
        return discrepancyForm;
    }

    public LinkedList<DiscrepancyLog> getDiscrepancyLogs() {
        return discrepancyLogs;
    }

    public List<ColumnsObject> getColumns() {
        if (columns == null) {
            columns = ColumnsObject.createColumns(this);
        }

        return columns;
    }

    public static class ColumnsObject implements Serializable {

        private String header;
        private String value;

        private ColumnsObject(String header, String value) {
            this.header = header;
            this.value = value;
        }

        public String getHeader() {
            return header;
        }

        public String getValue() {
            return value;
        }

        public static List<ColumnsObject> createColumns(TravelerDiscrepancyLog log) {
            List<ColumnsObject> columns = new ArrayList<>();

            DiscrepancyForm discrepancyForm = log.discrepancyForm;
            HashMap<String, String> labels = discrepancyForm.getLabels();

            for (String key : labels.keySet()) {
                String value = labels.get(key); 
                
                ColumnsObject col = new ColumnsObject(value, key); 
                columns.add(col); 
            }

            return columns;
        }
    }

}
