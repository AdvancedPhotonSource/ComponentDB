/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans.builder;

import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 * 
 * @author Dariusz
 */
public abstract class CdbQueryBuilder {       
    
    private static final CharSequence[] ESCAPE_QUERY_CHARACTERS = {"'"};

    protected static final String QUERY_LIKE = "LIKE";
    protected static final String QUERY_EQUALS = "=";

    protected Map filterMap;
    protected String sortField;
    protected SortOrder sortOrder;

    protected String wherePart;
    protected String sortPart;
    protected String joinPart;
    
    protected void appendRawWhere(String rawWhere) {
        if (wherePart.isEmpty()) {
            wherePart += " WHERE ";
        } else {
            wherePart += "AND ";
        }
        
        wherePart += rawWhere; 
    }

    protected void appendWhere(String comparator, String key, Object object) {        
        String value = "NULL";
        if (object != null) {
            value = object.toString();
        }

        if (object != null && object instanceof String) {
            if (comparator.equalsIgnoreCase(QUERY_LIKE)) {
                if (value.contains("*") || value.contains("?")) {
                    value = ((String)value).replace('*', '%');                     
                    value = ((String)value).replace('?', '_');   
                } else {
                    value = "%" + value + "%";
                }
            }

            value = "'" + escapeCharacters(value) + "'";
        }

        String wherePart = key + " " + comparator + " " + value + " ";
        appendRawWhere(wherePart); 
    }
    
    protected static String escapeCharacters(String queryParameter) {
        for (CharSequence cs : ESCAPE_QUERY_CHARACTERS) {
            if (queryParameter.contains(cs)) {
                queryParameter = queryParameter.replace(cs, "'" + cs);
            }
        }
        return queryParameter;
    }

}
