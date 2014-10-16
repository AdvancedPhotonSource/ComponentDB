package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.beans.UserGroupFacade;
import gov.anl.aps.cdb.portal.model.entities.SettingType;
import gov.anl.aps.cdb.portal.model.entities.UserInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("userGroupController")
@SessionScoped
public class UserGroupController extends CrudEntityController<UserGroup, UserGroupFacade> implements Serializable
{

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "UserGroup.List.Display.NumberOfItemsPerPage";

    @EJB
    private UserGroupFacade ejbFacade;

    public UserGroupController() {
    }

    @Override
    protected UserGroupFacade getFacade() {
        return ejbFacade;
    }

    @Override
    protected UserGroup createEntityInstance() {
        return new UserGroup();
    }

    @Override
    public String getEntityTypeName() {
        return "userGroup";
    }
    
    @Override
    public String getDisplayEntityTypeName() {
        return "user group";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<UserGroup> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
    }
    
    @FacesConverter(value = "userGroupConverter", forClass = UserGroup.class)
    public static class UserGroupControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0 || value.equals("Select")) {
                return null;
            }
            UserGroupController controller = (UserGroupController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userGroupController");
            return controller.getEntity(getKey(value));
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
            if (object instanceof UserGroup) {
                UserGroup o = (UserGroup) object;
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + UserGroup.class.getName());
            }
        }

    }

}
