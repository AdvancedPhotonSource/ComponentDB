package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.exceptions.CdbPortalException;
import gov.anl.aps.cdb.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.entities.SettingType;
import gov.anl.aps.cdb.portal.model.entities.UserSetting;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.ArrayList;
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

@Named("userInfoController")
@SessionScoped
public class UserInfoController extends CrudEntityController<UserInfo, UserInfoFacade> implements Serializable
{

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "UserInfo.List.Display.NumberOfItemsPerPage";

    private static final Logger logger = Logger.getLogger(ComponentController.class.getName());

    @EJB
    private UserInfoFacade userFacade;

    private String passwordEntry = null;

    public UserInfoController() {
    }

    @Override
    protected UserInfoFacade getFacade() {
        return userFacade;
    }

    @Override
    protected UserInfo createEntityInstance() {
        return new UserInfo();
    }

    @Override
    public String getEntityTypeName() {
        return "userInfo";
    }

    @Override
    public String getEntityTypeGroupName() {
        return "userGroup";
    }
    
    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getUsername();
        }
        return "";
    }

    @Override
    public List<UserInfo> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public String prepareEdit(UserInfo user) {
        ArrayList<UserSetting> userSettingList = new ArrayList<>();
        for (SettingType settingType : getSettingTypeList()) {
            UserSetting setting = user.getUserSetting(settingType.getName());
            if (setting == null) {
                setting = new UserSetting();
                setting.setUser(user);
                setting.setSettingType(settingType);
            }

            String settingValue = setting.getValue();
            if (settingValue == null || settingValue.isEmpty()) {
                setting.setValue(settingType.getDefaultValue());
            }
            userSettingList.add(setting);
        }
        user.setUserSettingList(userSettingList);
        passwordEntry = null;
        return super.prepareEdit(user);
    }

    @Override
    public void prepareEntityInsert(UserInfo user) throws ObjectAlreadyExists {
        UserInfo existingUser = userFacade.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new ObjectAlreadyExists("User " + user.getUsername() + " already exists.");
        }
        logger.debug("Inserting new user " + user.getUsername());
    }

    @Override
    public void prepareEntityUpdate(UserInfo user) throws CdbPortalException {
        logger.debug("Updating user " + user.getUsername());
        List<UserSetting> userSettingList = user.getUserSettingList();
        for (UserSetting userSetting : userSettingList) {
            if (userSetting.getValue() == null) {
                userSetting.setValue(userSetting.getSettingType().getDefaultValue());
            }
            if (passwordEntry != null && !passwordEntry.isEmpty()) {
                user.setPassword(passwordEntry);
            }
        }
    }

    public void saveSettingList() {
        update();
        UserInfo selectedUser = getSelected();
        selectedUser.updateSettingsModificationDate();
        logger.debug("Settings for user " + selectedUser.getUsername() + " have been modified at " + selectedUser.getUserSettingsModificationDate());
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser.getId().equals(selectedUser.getId())) {
            logger.debug("Settings modified for session user");
            sessionUser.setUserSettingList(selectedUser.getUserSettingList());
        }
    }

    public void saveSettingListForSessionUser() {
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser != null) {
            logger.debug("Saving settings for session user");
            UserInfo user = getEntity(sessionUser.getId());
            user.setUserSettingList(sessionUser.getUserSettingList());
            setCurrent(user);
            update();
            SessionUtility.setUser(user);
        }

    }

    public String prepareSessionUserEdit(String viewPath) {
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser == null) {
            return null;
        }
        prepareEdit(sessionUser);
        return viewPath + "?faces-redirect=true";
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

    @Override
    public boolean entityHasGroups() {
        return true;
    }
        
    public String getPasswordEntry() {
        return passwordEntry;
    }

    public void setPasswordEntry(String passwordEntry) {
        this.passwordEntry = passwordEntry;
    }

    @FacesConverter(forClass = UserInfo.class)
    public static class UserInfoControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserInfoController controller = (UserInfoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userInfoController");
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
            if (object instanceof UserInfo) {
                UserInfo o = (UserInfo) object;
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + UserInfo.class.getName());
            }
        }

    }

}
