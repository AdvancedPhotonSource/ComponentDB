/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;

/**
 * Property type handler interface.
 */
public interface PropertyTypeHandlerInterface {

    /**
     * Get name of the property type handler.
     *
     * @return name of property type handler.
     */
    public abstract String getName();

    /**
     * Defines what is executed when the edit button is clicked on the action
     * column of the edit page for the particular property value.
     *
     * @return onComplete action - example: PF('stuff').show().
     */
    public abstract String getEditActionOncomplete();

    /**
     * Defines what icon the edit button should have on the action column of the
     * edit page for the particular property value.
     *
     * @return
     */
    public abstract String getEditActionIcon();

    /**
     * Defines if the edit button should be shown on the action column of the
     * edit page for the particular property value.
     *
     * @return
     */
    public abstract Boolean getDisplayEditActionButton();

    /**
     * Get display type of the property handler. A property value could be
     * displayed in different ways.
     *
     * See DisplayType constant for more details.
     *
     * @return display type of the particular property handler.
     */
    public abstract DisplayType getValueDisplayType();

    /**
     * Command to be executed when the 'i' button is pressed in the action
     * column.
     *
     * example: 'PF('stuff').show()' or a remote command could also be executed.
     *
     * @param propertyValue
     */
    public abstract void setInfoActionCommand(PropertyValue propertyValue);

    /**
     * InfoAction command to be executed for the property value history record.
     *
     * Due to lack of action column on property value history, it will only be
     * executed for property handler with display type of info action.
     *
     * @param propertyValueHistory
     */
    public abstract void setInfoActionCommand(PropertyValueHistory propertyValueHistory);

    /**
     * Update the value the user sees to something meaningful.
     *
     * Display value is used to show user a meaningful title, for example: a
     * property value may contain an id to an external system, display value
     * could be a fetched name from the external system.
     *
     * @param propertyValue
     */
    public abstract void setDisplayValue(PropertyValue propertyValue);

    /**
     * Same as the version of this method for PropertyValue.
     *
     * @param propertyValueHistory
     */
    public abstract void setDisplayValue(PropertyValueHistory propertyValueHistory);

    /**
     * Generate a URL to an external system from the property value.
     *
     * For example: A property value may contain only an id of something in an
     * external system, This method could generate a meaningful url to the
     * external system using that id.
     *
     * @param propertyValue
     */
    public abstract void setTargetValue(PropertyValue propertyValue);

    /**
     * Same as the version of this method for PropertyValue.
     *
     * @param propertyValueHistory
     */
    public abstract void setTargetValue(PropertyValueHistory propertyValueHistory);

    /**
     * Allows for specifying a custom edit fragment that is placed on the value
     * section while editing a property value.
     *
     * Xhtl page must check for the matching editPageString for rendering of the
     * custom fragment.
     *
     * @return
     */
    public abstract String getPropertyEditPage();
}
