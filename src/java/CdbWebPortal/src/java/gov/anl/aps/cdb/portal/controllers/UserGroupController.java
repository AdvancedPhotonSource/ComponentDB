package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.beans.UserGroupFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

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
import org.apache.log4j.Logger;

@Named("userGroupController")
@SessionScoped
public class UserGroupController extends CrudEntityController<UserGroup, UserGroupFacade> implements Serializable {

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "UserGroup.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "UserGroup.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "UserGroup.List.Display.Description";

    private static final String FilterByNameSettingTypeKey = "UserGroup.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "UserGroup.List.FilterBy.Description";

    private static final Logger logger = Logger.getLogger(UserGroupController.class.getName());

    @EJB
    private UserGroupFacade userGroupFacade;

    public UserGroupController() {
    }

    @Override
    protected UserGroupFacade getFacade() {
        return userGroupFacade;
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
    public UserGroup findById(Integer id) {
        return userGroupFacade.findById(id);
    }

    @Override
    public List<UserGroup> getAvailableItems() {
        return super.getAvailableItems();
    }
    
    @Override
    public void prepareEntityInsert(UserGroup userGroup) throws ObjectAlreadyExists {
        UserGroup existingUserGroup = userGroupFacade.findByName(userGroup.getName());
        if (existingUserGroup != null) {
            throw new ObjectAlreadyExists("User group " + userGroup.getName() + " already exists.");
        }
        logger.debug("Inserting new user group " + userGroup.getName());
    }

    @Override
    public void prepareEntityUpdate(UserGroup userGroup) throws ObjectAlreadyExists {
        UserGroup existingUserGroup = userGroupFacade.findByName(userGroup.getName());
        if (existingUserGroup != null && !existingUserGroup.getId().equals(userGroup.getId())) {
            throw new ObjectAlreadyExists("User group " + userGroup.getName() + " already exists.");
        }
        logger.debug("Updating user group " + userGroup.getName());
    }    

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
    }

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
                return controller.getEntity(getKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to user group object.");
                return null;
            }
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
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + UserGroup.class.getName());
            }
        }

    }

}
