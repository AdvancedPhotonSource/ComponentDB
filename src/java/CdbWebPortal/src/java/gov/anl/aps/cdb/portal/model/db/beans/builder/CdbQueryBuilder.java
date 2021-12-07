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

    protected void appendWhere(String comparator, String key, Object object) {
        if (wherePart.isEmpty()) {
            wherePart += " WHERE ";
        } else {
            wherePart += "AND ";
        }

        String value = "NULL";
        if (object != null) {
            value = object.toString();
        }

        if (object != null && object instanceof String) {
            if (comparator.equalsIgnoreCase(QUERY_LIKE)) {
                value = ((String)value).replace('*', '%'); 
                value = "%" + value + "%";
            }

            value = "'" + escapeCharacters(value) + "'";
        }

        wherePart += key + " " + comparator + " " + value + " ";
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
