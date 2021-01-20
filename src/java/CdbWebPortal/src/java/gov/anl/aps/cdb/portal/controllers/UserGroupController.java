/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.UserGroupSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.UserGroupControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.beans.UserGroupFacade;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

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

@Named(UserGroupController.CONTROLLER_NAMED)
@SessionScoped
public class UserGroupController extends CdbEntityController<UserGroupControllerUtility, UserGroup, UserGroupFacade, UserGroupSettings> implements Serializable {   

    private static final Logger logger = LogManager.getLogger(UserGroupController.class.getName());
    public static final String CONTROLLER_NAMED = "userGroupController";

    @EJB
    private UserGroupFacade userGroupFacade;

    public UserGroupController() {
    }

    public static UserGroupController getInstance() {
        return (UserGroupController) SessionUtility.findBean(UserGroupController.CONTROLLER_NAMED);
    }

    @Override
    protected UserGroupFacade getEntityDbFacade() {
        return userGroupFacade;
    }

    @Override
    public List<UserGroup> getAvailableItems() {
        return super.getAvailableItems();
    }
    
    @Override
    public UserGroup findById(Integer id) {
        return userGroupFacade.findById(id);
    }

    @Override
    protected UserGroupSettings createNewSettingObject() {
        return new UserGroupSettings(this);
    }

    @Override
    protected UserGroupControllerUtility createControllerUtilityInstance() {
        return new UserGroupControllerUtility();
    }

    /**
     * Converter class for user group objects.
     */
    @FacesConverter(value = "userGroupConverter", forClass = UserGroup.class)
    public static class UserGroupControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0 || value.equals("Select")) {
                return null;
            }
            try {
                UserGroupController controller = (UserGroupController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "userGroupController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to user group object.");
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
            if (object instanceof UserGroup) {
                UserGroup o = (UserGroup) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + UserGroup.class.getName());
            }
        }

    }

}
