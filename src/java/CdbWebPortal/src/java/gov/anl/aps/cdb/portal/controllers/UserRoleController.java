/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.UserRoleSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.UserRoleControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.UserRole;
import gov.anl.aps.cdb.portal.model.db.beans.UserRoleFacade;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("userRoleController")
@SessionScoped
public class UserRoleController extends CdbEntityController<UserRoleControllerUtility, UserRole, UserRoleFacade, UserRoleSettings> implements Serializable {

    @EJB
    UserRoleFacade userRoleFacade;
    
    public UserRoleController(){
        super();
    }
    
    public UserRole getUserRole(gov.anl.aps.cdb.portal.model.db.entities.UserRolePK id) {
        return userRoleFacade.find(id);
    }

    @Override
    protected UserRoleFacade getEntityDbFacade() {
        return userRoleFacade; 
    }
    
    public void prepareAddUserRole(UserInfo userInfoObject) {
        UserRole userRole = new UserRole();
        userRole.init(userInfoObject);
        userInfoObject.getUserRoleList().add(0, userRole); 
    }

    @Override
    protected UserRoleSettings createNewSettingObject() {
        return new UserRoleSettings(this); 
    }

    @Override
    protected UserRoleControllerUtility createControllerUtilityInstance() {
        return new UserRoleControllerUtility(); 
    }

    @FacesConverter(forClass = UserRole.class)
    public static class UserRoleControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserRoleController controller = (UserRoleController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userRoleController");
            return controller.getUserRole(getKey(value));
        }

        gov.anl.aps.cdb.portal.model.db.entities.UserRolePK getKey(String value) {
            gov.anl.aps.cdb.portal.model.db.entities.UserRolePK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new gov.anl.aps.cdb.portal.model.db.entities.UserRolePK();
            key.setUserId(Integer.parseInt(values[0]));
            key.setRoleTypeId(Integer.parseInt(values[1]));
            key.setUserGroupId(Integer.parseInt(values[2]));
            return key;
        }

        String getStringKey(gov.anl.aps.cdb.portal.model.db.entities.UserRolePK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getUserId());
            sb.append(SEPARATOR);
            sb.append(value.getRoleTypeId());
            sb.append(SEPARATOR);
            sb.append(value.getUserGroupId());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof UserRole) {
                UserRole o = (UserRole) object;
                return getStringKey(o.getUserRolePK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + UserRole.class.getName());
            }
        }

    }

}
