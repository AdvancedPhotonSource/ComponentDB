/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author darek
 */
public class PropertyValueControllerUtility extends CdbEntityControllerUtility<PropertyValue, PropertyValueFacade> {

    @Override
    protected PropertyValueFacade getEntityDbFacade() {
        return PropertyValueFacade.getInstance(); 
    }        

    @Override
    public String getEntityTypeName() {
        return "propertyValue";
    }

    @Override
    public PropertyValue createEntityInstance(UserInfo sessionUser) {
        return new PropertyValue();
    }

    @Override
    public List<PropertyValue> searchEntities(String searchString) {
        return getEntityDbFacade().searchPropertyValues(searchString);
    }

    /**
     * Builds a word-order independent pattern: each whitespace-delimited word of
     * the search string is matched independently (alternation), so a field is
     * recorded as a match when it contains any of the search words. This mirrors
     * the facade's per-word query so DB-returned rows are never dropped.
     *
     * Each word is wrapped in its own capturing group so consumers can identify
     * which word a match belongs to (used to build the shortest match snippet).
     */
    @Override
    protected Pattern buildSearchPattern(String searchString, boolean caseInsensitive) {
        String[] tokens = searchString.trim().split("\\s+");
        StringBuilder patternString = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            if (i > 0) {
                patternString.append("|");
            }
            String token = tokens[i];
            String tokenRegex;
            if (token.contains("*") || token.contains("?")) {
                tokenRegex = token.replace("*", ".*").replace("?", ".");
            } else {
                tokenRegex = Pattern.quote(token);
            }
            patternString.append("(").append(tokenRegex).append(")");
        }

        int flags = caseInsensitive ? Pattern.CASE_INSENSITIVE : 0;
        return Pattern.compile(patternString.toString(), flags);
    }

}
