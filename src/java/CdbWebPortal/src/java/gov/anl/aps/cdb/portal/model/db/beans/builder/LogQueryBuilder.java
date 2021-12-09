/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans.builder;

import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.entities.LogLevel;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Dariusz
 */
public class LogQueryBuilder extends CdbQueryBuilder {

    private static final Logger logger = LogManager.getLogger(LogQueryBuilder.class.getName());

    private static final String QUERY_COUNT_STRING_START = "SELECT count(DISTINCT(l)) FROM Log l ";
    private static final String QUERY_STRING_START = "SELECT DISTINCT(l) FROM Log l ";

    private static final String SYSTEM_LOG_LEVEL_LIST_JOIN_NAME = "sll";

    boolean include_sll = false;

    List<LogLevel> systemLogLevels;

    public LogQueryBuilder(Map filterMap, String sortField, SortOrder sortOrder, List<LogLevel> systemLogLevels) {
        this.filterMap = filterMap;
        this.sortField = sortField;
        this.sortOrder = sortOrder;
        this.systemLogLevels = systemLogLevels;

        include_sll = true;

        generateWhereString();
        generateSortString();

        generateJoinString();
    }

    public String getCountQueryForLogs() {
        String fullQuery = QUERY_COUNT_STRING_START + joinPart + wherePart + sortPart;

        return fullQuery;
    }

    public String getQueryForLogs() {
        String fullQuery = QUERY_STRING_START + joinPart + wherePart + sortPart;

        return fullQuery;
    }

    /**
     * Adds the joins but run generate where and sort first to gather info on
     * which joins are relevant.
     *
     * @return
     */
    private String generateJoinString() {
        joinPart = "";

        if (include_sll) {
            joinPart += " JOIN l.logLevelList " + SYSTEM_LOG_LEVEL_LIST_JOIN_NAME;
        }

        return joinPart;

    }

    private void generateWhereString() {
        wherePart = " WHERE ";
        for (LogLevel logLevel : systemLogLevels) {
            if (systemLogLevels.indexOf(logLevel) != 0) {
                wherePart += " OR";
            }
            Integer logLevelId = logLevel.getId();
            wherePart += " sll.id = " + logLevelId;
        }
        wherePart += " ";

        for (Object key : filterMap.keySet()) {
            Object filterValue = filterMap.get(key);
            if (filterValue instanceof FilterMeta) {
                filterValue = ((FilterMeta) filterValue).getFilterValue();
            }

            if (filterValue != null) {
                String keyString = key.toString();
                String valueString = filterValue.toString();

                QueryTranslator qt = QueryTranslator.getQueryTranslatorByValue(keyString);

                if (qt != null) {
                    String queryNameField = qt.getQueryNameField();

                    appendWhere(QUERY_LIKE, queryNameField, valueString);
                }
            }
        }

    }

    private void generateSortString() {
        // Sort support not needed right now. Just default sort order applied.
        sortPart = " ";
        sortPart += "ORDER BY l.id DESC";
    }

    public enum QueryTranslator {
        text("text", "l"),
        effectiveFromDateTime("effectiveFromDateTime", "l"),
        effectiveToDateTime("effectiveToDateTime", "l"),
        enteredOnDateTime("enteredOnDateTime", "l"),
        enteredByUser("enteredByUser", "l");

        private String value;
        private String customDesignation;
        private String parent;

        private QueryTranslator(String value, String parent) {
            this.value = value;
            this.parent = parent;
        }

        private QueryTranslator(String value, String parent, String customDesignation) {
            this.value = value;
            this.parent = parent;
            this.customDesignation = customDesignation;
        }

        public String getValue() {
            return value;
        }

        public String getParent() {
            return parent;
        }

        public String getQueryNameField() {
            String result = getParent() + ".";
            if (customDesignation != null) {
                result += customDesignation;
            } else {
                result += getValue();
            }
            return result;
        }

        public static QueryTranslator getQueryTranslatorByValue(String value) {
            if (value != null) {
                for (QueryTranslator qt : QueryTranslator.values()) {
                    if (qt.getValue().equals(value)) {
                        return qt;
                    }
                }
            }
            return null;
        }
    };

}
