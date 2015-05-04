/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.common.utilities.HttpLinkUtility;

/**
 * Abstract property type handler.
 *
 * This class is used as a base for all property type handlers.
 */
public abstract class AbstractPropertyTypeHandler implements PropertyTypeHandlerInterface {

    private String name = null;
    private DisplayType displayType = null;

    public static String shortenHttpLinkDisplayValueIfNeeded(String displayValue) {
        return HttpLinkUtility.prepareHttpLinkDisplayValue(displayValue);
    }

    public AbstractPropertyTypeHandler(String name) {
        this.name = name;
    }
    
    public AbstractPropertyTypeHandler(String name, DisplayType displayType) {
        this.name = name;
        this.displayType = displayType;
    }

    public String getName() {
        return name;
    }

    @Override
    public DisplayType getValueDisplayType() {
        return displayType;
    }
    
    @Override
    public String getEditActionOncomplete() {
        return null;
    }

    @Override
    public String getEditActionIcon() {
        return null;
    }

    @Override
    public String getEditActionBean() {
        return null;
    }

    @Override
    public Boolean getDisplayEditActionButton() {
        return getEditActionIcon() != null;
    }

    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        propertyValue.setDisplayValueToValue();
    }

    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setDisplayValueToValue();
    }

    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        propertyValue.setTargetValueToValue();
    }

    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setTargetValueToValue();
    }
}
