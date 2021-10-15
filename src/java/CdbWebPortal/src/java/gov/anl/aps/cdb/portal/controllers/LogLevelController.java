/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.LogLevelSettings;
import gov.anl.aps.cdb.portal.model.db.entities.LogLevel;
import gov.anl.aps.cdb.portal.controllers.util.PaginationHelper;
import gov.anl.aps.cdb.portal.controllers.utilities.LogLevelControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.LogLevelFacade;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;

@Named("logLevelController")
@SessionScoped
public class LogLevelController extends CdbEntityController<LogLevelControllerUtility, LogLevel, LogLevelFacade, LogLevelSettings> implements Serializable {

    private LogLevel current;
    private DataModel items = null;
    @EJB
    LogLevelFacade logLevelFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public LogLevelController() {
    }

    @Override
    protected LogLevelFacade getEntityDbFacade() {
        return logLevelFacade; 
    }

    @Override
    protected LogLevelSettings createNewSettingObject() {
        return new LogLevelSettings();
    }

    @Override
    protected LogLevelControllerUtility createControllerUtilityInstance() {
        return new LogLevelControllerUtility(); 
    }


    @FacesConverter(value = "logLevelConverter")
    public static class LogLevelControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LogLevelController controller = (LogLevelController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "logLevelController");
            return controller.findById(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof LogLevel) {
                LogLevel o = (LogLevel) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + LogLevel.class.getName());
            }
        }

    }

}
