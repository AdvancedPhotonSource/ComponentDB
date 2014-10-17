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
import org.primefaces.component.datatable.DataTable;

@Named("userInfoController")
@SessionScoped
public class UserInfoController extends CrudEntityController<UserInfo, UserInfoFacade> implements Serializable
{

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "UserInfo.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "UserInfo.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "UserInfo.List.Display.Description";
    private static final String DisplayEmailSettingTypeKey = "UserInfo.List.Display.Email";
    private static final String DisplayFirstNameSettingTypeKey = "UserInfo.List.Display.FirstName";
    private static final String DisplayGroupsSettingTypeKey = "UserInfo.List.Display.Groups";
    private static final String DisplayLastNameSettingTypeKey = "UserInfo.List.Display.LastName";
    private static final String DisplayMiddleNameSettingTypeKey = "UserInfo.List.Display.MiddleName";

    private static final String FilterByDescriptionSettingTypeKey = "UserInfo.List.FilterBy.Description";
    private static final String FilterByEmailSettingTypeKey = "UserInfo.List.FilterBy.Email";
    private static final String FilterByFirstNameSettingTypeKey = "UserInfo.List.FilterBy.FirstName";
    private static final String FilterByGroupsSettingTypeKey = "UserInfo.List.FilterBy.Groups";
    private static final String FilterByLastNameSettingTypeKey = "UserInfo.List.FilterBy.LastName";
    private static final String FilterByMiddleNameSettingTypeKey = "UserInfo.List.FilterBy.MiddleName";
    private static final String FilterByUsernameSettingTypeKey = "UserInfo.List.FilterBy.Username";
 
    private static final Logger logger = Logger.getLogger(ComponentController.class.getName());

    @EJB
    private UserInfoFacade userFacade;

    private String passwordEntry = null;

    private Boolean displayEmail = null;
    private Boolean displayFirstName = null;
    private Boolean displayGroups = null;
    private Boolean displayLastName = null;
    private Boolean displayMiddleName = null;

    private String filterByEmail = null;
    private String filterByFirstName = null;
    private String filterByGroups = null;
    private String filterByLastName = null;
    private String filterByMiddleName = null;
    private String filterByUsername = null;
   
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
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayEmail = Boolean.parseBoolean(settingTypeMap.get(DisplayEmailSettingTypeKey).getDefaultValue());
        displayFirstName = Boolean.parseBoolean(settingTypeMap.get(DisplayFirstNameSettingTypeKey).getDefaultValue());
        displayGroups = Boolean.parseBoolean(settingTypeMap.get(DisplayGroupsSettingTypeKey).getDefaultValue());
        displayLastName = Boolean.parseBoolean(settingTypeMap.get(DisplayLastNameSettingTypeKey).getDefaultValue());
        displayMiddleName = Boolean.parseBoolean(settingTypeMap.get(DisplayMiddleNameSettingTypeKey).getDefaultValue());
 
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByEmail = settingTypeMap.get(FilterByEmailSettingTypeKey).getDefaultValue();
        filterByFirstName = settingTypeMap.get(FilterByFirstNameSettingTypeKey).getDefaultValue();
        filterByGroups = settingTypeMap.get(FilterByGroupsSettingTypeKey).getDefaultValue();
        filterByLastName = settingTypeMap.get(FilterByLastNameSettingTypeKey).getDefaultValue();
        filterByMiddleName = settingTypeMap.get(FilterByMiddleNameSettingTypeKey).getDefaultValue();
        filterByUsername = settingTypeMap.get(FilterByUsernameSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayEmail = sessionUser.getUserSettingValueAsBoolean(DisplayEmailSettingTypeKey, displayEmail);
        displayFirstName = sessionUser.getUserSettingValueAsBoolean(DisplayFirstNameSettingTypeKey, displayFirstName);
        displayGroups = sessionUser.getUserSettingValueAsBoolean(DisplayGroupsSettingTypeKey, displayGroups);
        displayLastName = sessionUser.getUserSettingValueAsBoolean(DisplayLastNameSettingTypeKey, displayLastName);
        displayMiddleName = sessionUser.getUserSettingValueAsBoolean(DisplayMiddleNameSettingTypeKey, displayMiddleName);
 
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByEmail = sessionUser.getUserSettingValueAsString(FilterByEmailSettingTypeKey, filterByEmail);
        filterByFirstName = sessionUser.getUserSettingValueAsString(FilterByFirstNameSettingTypeKey, filterByFirstName);
        filterByGroups = sessionUser.getUserSettingValueAsString(FilterByGroupsSettingTypeKey, filterByGroups);
        filterByLastName = sessionUser.getUserSettingValueAsString(FilterByLastNameSettingTypeKey, filterByLastName);
        filterByMiddleName = sessionUser.getUserSettingValueAsString(FilterByMiddleNameSettingTypeKey, filterByMiddleName);
        filterByUsername = sessionUser.getUserSettingValueAsString(FilterByUsernameSettingTypeKey, filterByUsername);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, String> filters = dataTable.getFilters();
        filterByEmail = filters.get("email");
        filterByFirstName = filters.get("firstName");
        filterByGroups = filters.get("groups");
        filterByLastName = filters.get("lastName");
        filterByMiddleName = filters.get("middleName");
        filterByUsername = filters.get("username");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        sessionUser.setUserSettingValue(DisplayEmailSettingTypeKey, displayEmail);
        sessionUser.setUserSettingValue(DisplayFirstNameSettingTypeKey, displayFirstName);
        sessionUser.setUserSettingValue(DisplayGroupsSettingTypeKey, displayGroups);
        sessionUser.setUserSettingValue(DisplayLastNameSettingTypeKey, displayLastName);
        sessionUser.setUserSettingValue(DisplayMiddleNameSettingTypeKey, displayMiddleName);

        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByEmailSettingTypeKey, filterByEmail);
        sessionUser.setUserSettingValue(FilterByFirstNameSettingTypeKey, filterByFirstName);
        sessionUser.setUserSettingValue(FilterByGroupsSettingTypeKey, filterByGroups);
        sessionUser.setUserSettingValue(FilterByLastNameSettingTypeKey, filterByLastName);
        sessionUser.setUserSettingValue(FilterByMiddleNameSettingTypeKey, filterByMiddleName);
        sessionUser.setUserSettingValue(FilterByUsernameSettingTypeKey, filterByUsername);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByEmail = null;
        filterByFirstName = null;
        filterByGroups = null;
        filterByLastName = null;
        filterByMiddleName = null;
        filterByUsername = null;
    }

    public Boolean getDisplayEmail() {
        return displayEmail;
    }

    public void setDisplayEmail(Boolean displayEmail) {
        this.displayEmail = displayEmail;
    }

    public Boolean getDisplayFirstName() {
        return displayFirstName;
    }

    public void setDisplayFirstName(Boolean displayFirstName) {
        this.displayFirstName = displayFirstName;
    }

    public Boolean getDisplayGroups() {
        return displayGroups;
    }

    public void setDisplayGroups(Boolean displayGroups) {
        this.displayGroups = displayGroups;
    }

    public Boolean getDisplayLastName() {
        return displayLastName;
    }

    public void setDisplayLastName(Boolean displayLastName) {
        this.displayLastName = displayLastName;
    }

    public Boolean getDisplayMiddleName() {
        return displayMiddleName;
    }

    public void setDisplayMiddleName(Boolean displayMiddleName) {
        this.displayMiddleName = displayMiddleName;
    }

    public String getFilterByEmail() {
        return filterByEmail;
    }

    public void setFilterByEmail(String filterByEmail) {
        this.filterByEmail = filterByEmail;
    }

    public String getFilterByFirstName() {
        return filterByFirstName;
    }

    public void setFilterByFirstName(String filterByFirstName) {
        this.filterByFirstName = filterByFirstName;
    }

    public String getFilterByGroups() {
        return filterByGroups;
    }

    public void setFilterByGroups(String filterByGroups) {
        this.filterByGroups = filterByGroups;
    }

    public String getFilterByLastName() {
        return filterByLastName;
    }

    public void setFilterByLastName(String filterByLastName) {
        this.filterByLastName = filterByLastName;
    }

    public String getFilterByMiddleName() {
        return filterByMiddleName;
    }

    public void setFilterByMiddleName(String filterByMiddleName) {
        this.filterByMiddleName = filterByMiddleName;
    }

    public String getFilterByUsername() {
        return filterByUsername;
    }

    public void setFilterByUsername(String filterByUsername) {
        this.filterByUsername = filterByUsername;
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
