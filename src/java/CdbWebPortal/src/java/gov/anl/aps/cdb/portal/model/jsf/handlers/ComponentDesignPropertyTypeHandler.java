/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.beans.DesignDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Design;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

public class ComponentDesignPropertyTypeHandler extends AbstractPropertyTypeHandler {

    @EJB
    DesignDbFacade designDbFacade;

    public static final String HANDLER_NAME = "Component Design";

    private static final String PROPERTY_EDIT_PAGE = "componentDesignPropertyValueEditPanel";

    private static final Logger logger = Logger.getLogger(ComponentDesignPropertyTypeHandler.class.getName());

    private Design design;

    public ComponentDesignPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.TABLE_RECORD_REFERENCE);
        try {
            this.designDbFacade = (DesignDbFacade) new InitialContext().lookup("java:module/DesignDbFacade");
        } catch (NamingException ex) {
            logger.error("Cannot instantiate design DB bean: " + ex);
        }
    }

    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        design = getDesign(propertyValue.getValue(), true);
        if (design == null) {
            propertyValue.setDisplayValue("Design with id: \"" + propertyValue.getValue() + "\" could not be found");
        } else {
            propertyValue.setDisplayValue(design.getName());
        }
    }

    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        design = getDesign(propertyValue.getValue(), false);
        if (design == null) {
            propertyValue.setTargetValue(null);
        } else {
            propertyValue.setTargetValue("/cdb/views/design/view.xhtml?id=" + propertyValue.getValue());
        }
    }

    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        design = getDesign(propertyValueHistory.getValue(), true);
        if (design == null) {
            propertyValueHistory.setDisplayValue("Design with id: \"" + propertyValueHistory.getValue() + "\" could not be found");
        } else {
            propertyValueHistory.setDisplayValue(design.getName());
        }
    }

    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        design = getDesign(propertyValueHistory.getValue(), false);
        if (design == null) {
            propertyValueHistory.setTargetValue(null);
        } else {
            propertyValueHistory.setTargetValue("/cdb/views/design/view.xhtml?id=" + propertyValueHistory.getValue());
        }
    }

    @Override
    public String getPropertyEditPage() {
        return PROPERTY_EDIT_PAGE;
    }

    @Override
    public void resetOneTimeUseVariables() {
        design = null;
    }

    private Design getDesign(String value, boolean displayErrorIfNotFound) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        if (design != null) {
            if (design.getId() == Integer.parseInt(value)) {
                return design;
            }
        }

        if (designDbFacade == null) {
            logger.error("Cannot get design for id: " + value + ": Design DB interface is not accessible.");
            return null;
        }

        design = null;
        int id = Integer.parseInt(value);
        logger.debug("Getting design for id: " + id);
        design = designDbFacade.findById(id);
        if (design == null && displayErrorIfNotFound) {
            String errMsg = "Cannot find design id: " + id;
            logger.error(errMsg);
            SessionUtility.addErrorMessage("Error", errMsg);
        }
        return design;

    }

}
