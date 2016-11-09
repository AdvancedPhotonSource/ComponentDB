/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.utilities;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Search result class.
 */
public class SearchResult {

    private final Integer objectId;
    private final String objectName;
    private HashMap<String, String> objectAttributeMatchMap = new HashMap();

    public SearchResult(Integer objectId, String objectName) {
        this.objectId = objectId;
        this.objectName = objectName;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void addAttributeMatch(String key, String value) {
        objectAttributeMatchMap.put(key, value);
    }

    public HashMap<String, String> getObjectAttributeMatchMap() {
        return objectAttributeMatchMap;
    }

    public void setObjectAttributeMatchMap(HashMap<String, String> objectAttributeMatchMap) {
        this.objectAttributeMatchMap = objectAttributeMatchMap;
    }

    public boolean isEmpty() {
        return objectAttributeMatchMap.isEmpty();
    }

    public boolean doesValueContainPattern(String key, Object value, Pattern searchPattern) {
        if (value != null) {
            return doesValueContainPattern(key, value.toString(), searchPattern);
        }
        return false;
    }

    public boolean doesValueContainPattern(String key, String value, Pattern searchPattern) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        boolean searchResult = searchPattern.matcher(value).find();
        if (searchResult) {
            addAttributeMatch(key, value);
        }
        return searchResult;
    }

    public String getDisplay() {
        String result = "";
        String keyDelimiter = ": ";
        String entryDelimiter = "";
        for (String key : objectAttributeMatchMap.keySet()) {
            result += entryDelimiter + key + keyDelimiter + objectAttributeMatchMap.get(key);
            entryDelimiter = "; ";
        }
        return result;
    }
}
