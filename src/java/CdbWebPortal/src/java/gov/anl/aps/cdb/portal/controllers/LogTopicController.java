/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.LogTopicSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.LogTopicControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.LogTopic;
import gov.anl.aps.cdb.portal.model.db.beans.LogTopicFacade;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named("logTopicController")
@SessionScoped
public class LogTopicController extends CdbEntityController<LogTopicControllerUtility, LogTopic, LogTopicFacade, LogTopicSettings> implements Serializable {

    private static final Logger logger = LogManager.getLogger(LogTopicController.class.getName());

    @EJB
    private LogTopicFacade logTopicFacade;

    public LogTopicController() {
        super();
    }

    @Override
    protected LogTopicFacade getEntityDbFacade() {
        return logTopicFacade;
    }

    @Override
    public List<LogTopic> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    protected LogTopicSettings createNewSettingObject() {
        return new LogTopicSettings(); 
    }

    @Override
    protected LogTopicControllerUtility createControllerUtilityInstance() {
        return new LogTopicControllerUtility(); 
    }

    @FacesConverter(forClass = LogTopic.class)
    public static class LogTopicControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            try {
                if (value == null || value.length() == 0) {
                    return null;
                }
                LogTopicController controller = (LogTopicController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "logTopicController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to log topic object.");
                return null;
            }
        }

        Integer getIntegerKey(String value) {
            return Integer.valueOf(value);
        }

        String getStringKey(Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof LogTopic) {
                LogTopic o = (LogTopic) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + LogTopic.class.getName());
            }
        }

    }

}
