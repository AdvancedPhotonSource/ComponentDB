/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.UserSettingSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.UserSettingControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.UserSetting;
import gov.anl.aps.cdb.portal.model.db.beans.UserSettingFacade;

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

@Named("userSettingController")
@SessionScoped
public class UserSettingController extends CdbEntityController<UserSettingControllerUtility, UserSetting, UserSettingFacade,  UserSettingSettings> implements Serializable {    

    @EJB
    private UserSettingFacade userSettingFacade;
    private static final Logger logger = LogManager.getLogger(UserSettingController.class.getName());
    

    public UserSettingController() {
    }

    @Override
    protected UserSettingFacade getEntityDbFacade() {
        return userSettingFacade;
    }   

    @Override
    public List<UserSetting> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void destroy(UserSetting userSetting) {
        if (userSetting.getId() != null) {
            super.destroy(userSetting);
        }
    }

    @Override
    protected UserSettingSettings createNewSettingObject() {
        return new UserSettingSettings(this);
    }

    @Override
    protected UserSettingControllerUtility createControllerUtilityInstance() {
        return new UserSettingControllerUtility(); 
    }

    /**
     * Converter class for user setting objects.
     */
    @FacesConverter(forClass = UserSetting.class)
    public static class UserSettingControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                UserSettingController controller = (UserSettingController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "userSettingController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to user setting object.");
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
            if (object instanceof UserSetting) {
                UserSetting o = (UserSetting) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + UserSetting.class.getName());
            }
        }

    }
}
