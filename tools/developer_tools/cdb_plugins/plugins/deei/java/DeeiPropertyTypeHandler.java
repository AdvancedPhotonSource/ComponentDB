/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.deei;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.AbstractPropertyTypeHandler;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AMOS property type handler.
 * 
 * Uses the inspection number metadata key to generate a url for a property. 
 */
public class DeeiPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "DEEI";

    public static final String DEEI_INSPECTION_NUMBER_KEY = "DEEI Inspection #";
    private static final String DEEI_NUM_STRIP_REG_EX = "E0+";

    private static final String DEEI_URL_REPLACE_STRING = "REPLACE_WITH_DEEI_NUM";

    private static final Pattern DEEI_NUM_STRIP_PATTERN = Pattern.compile(DEEI_NUM_STRIP_REG_EX);

    public static final List<String> REQUIRED_METADATA_KEYS = Arrays.asList(DEEI_INSPECTION_NUMBER_KEY);
    
    private static final String DEEI_URL = DeeiPluginManager.getUrlStringProperty(); 

    public DeeiPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.GENERATED_HTTP_LINK);
        
        
    }

    @Override
    public List<String> getRequiredMetadataKeys() {
        return REQUIRED_METADATA_KEYS;
    }

    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        String deeiInspectionNum = propertyValue.getPropertyMetadataValueForKey(DEEI_INSPECTION_NUMBER_KEY);

        if (deeiInspectionNum != null && !deeiInspectionNum.equals("")) {
            Matcher matcher = DEEI_NUM_STRIP_PATTERN.matcher(deeiInspectionNum);

            if (matcher.find()) {
                String strip = matcher.group(0);

                deeiInspectionNum = deeiInspectionNum.replace(strip, "");
            }
            String url = DEEI_URL; 

            url = url.replace(DEEI_URL_REPLACE_STRING, deeiInspectionNum);

            propertyValue.setTargetValue(url);

        } else {
            propertyValue.setTargetValue("");
        }
    }

}
