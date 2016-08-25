package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.common.utilities.CryptUtility;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserRole;
import gov.anl.aps.cdb.portal.model.db.entities.UserRolePK;
import gov.anl.aps.cdb.portal.model.db.entities.UserSetting;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

@Named("userInfoController")
@SessionScoped
public class UserInfoController extends CdbEntityController<UserInfo, UserInfoFacade> implements Serializable {

    /*
     * Controller specific settings
     */
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

    private static final Logger logger = Logger.getLogger(UserInfoController.class.getName());

    @EJB
    private UserInfoFacade userInfoFacade;
    
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

    private Integer loadedDataModelHashCode = null;

    public UserInfoController() {
    }

    @Override
    protected UserInfoFacade getEntityDbFacade() {
        return userInfoFacade;
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
    public String getDisplayEntityTypeName() {
        return "user";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getUsername();
        }
        return "";
    }

    @Override
    public UserInfo findById(Integer id) {
        return userInfoFacade.findById(id);
    }

    @Override
    public List<UserInfo> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public String prepareEdit(UserInfo userInfo) {
        ArrayList<UserSetting> userSettingList = new ArrayList<>();
        for (SettingType settingType : getSettingTypeList()) {
            UserSetting setting = (UserSetting) userInfo.getSetting(settingType.getName());
            if (setting == null) {
                setting = new UserSetting();
                setting.setUser(userInfo);
                setting.setSettingType(settingType);
            }

            String settingValue = setting.getValue();
            if (settingValue == null || settingValue.isEmpty()) {
                setting.setValue(settingType.getDefaultValue());
            }
            userSettingList.add(setting);
        }
        userInfo.setUserSettingList(userSettingList);
        passwordEntry = null;
        return super.prepareEdit(userInfo);
    }

    @Override
    public void prepareEntityInsert(UserInfo userInfo) throws ObjectAlreadyExists {
        UserInfo existingUser = userInfoFacade.findByUsername(userInfo.getUsername());
        if (existingUser != null) {
            throw new ObjectAlreadyExists("User " + userInfo.getUsername() + " already exists.");
        }
        logger.debug("Inserting new user " + userInfo.getUsername());
        if (passwordEntry != null && !passwordEntry.isEmpty()) {
            String cryptedPassword = CryptUtility.cryptPasswordWithPbkdf2(passwordEntry);
            userInfo.setPassword(cryptedPassword);
            logger.debug("New user crypted password: " + cryptedPassword);
        }
    }

    @Override
    public void prepareEntityUpdate(UserInfo userInfo) throws CdbException {
        prepareSaveUserRoleList();
        
        UserInfo existingUser = userInfoFacade.findByUsername(userInfo.getUsername());
        if (existingUser != null && !existingUser.getId().equals(userInfo.getId())) {
            throw new ObjectAlreadyExists("User " + userInfo.getUsername() + " already exists.");
        }

        logger.debug("Updating user " + userInfo.getUsername());
        List<UserSetting> userSettingList = userInfo.getUserSettingList();
        for (UserSetting userSetting : userSettingList) {
            if (userSetting.getValue() == null) {
                userSetting.setValue(userSetting.getSettingType().getDefaultValue());
            }
        }
        if (passwordEntry != null && !passwordEntry.isEmpty()) {
            String cryptedPassword = CryptUtility.cryptPasswordWithPbkdf2(passwordEntry);
            userInfo.setPassword(cryptedPassword);
            logger.debug("Updated crypted password: " + cryptedPassword);
        }
    }
    
    public void deleteUserRole(UserRole userRole) {
        UserInfo userInfo = getCurrent();
        List<UserRole> userRoleList = userInfo.getUserRoleList();
        userRoleList.remove(userRole);
        if (userRole.getUserRolePK() != null) {
            updateOnRemoval();
        } else {
            SessionUtility.addInfoMessage("Success", "Removed new user role.");
        }
        
    }
    
    public void prepareSaveUserRoleList() throws CdbException {
        for (UserRole currentUserRole : getCurrent().getUserRoleList()) {
            UserRolePK currentPK = UserRole.createPrimaryKeyObject(currentUserRole);
            for (UserRole userRole : getCurrent().getUserRoleList()) {
                UserRolePK pk = UserRole.createPrimaryKeyObject(userRole);
                // Ensure that same object is not being compared 
                if (userRole != currentUserRole) {
                    if (pk.equals(currentPK)) {
                        throw new CdbException("Duplicate role entry exists: " 
                                + currentUserRole.getRoleType().getName() + " | " 
                                + currentUserRole.getUserGroup().getName());
                    }
                }
            }            
            currentUserRole.setUserRolePK(currentPK);
        }
    }
    
    public void saveUserRoleList() {
        update();
    }

    public void saveSettingList() {
        update();
        UserInfo selectedUser = getSelected();
        selectedUser.updateSettingsModificationDate();
        logger.debug("Settings for user " + selectedUser.getUsername() + " have been modified at " + selectedUser.getSettingsModificationDate());
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
        }
    }

    @Override
    public String update() {
        String result = super.update();
        SessionUtility.setUser(current);
        return result;
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
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayEmail = settingEntity.getSettingValueAsBoolean(DisplayEmailSettingTypeKey, displayEmail);
        displayFirstName = settingEntity.getSettingValueAsBoolean(DisplayFirstNameSettingTypeKey, displayFirstName);
        displayGroups = settingEntity.getSettingValueAsBoolean(DisplayGroupsSettingTypeKey, displayGroups);
        displayLastName = settingEntity.getSettingValueAsBoolean(DisplayLastNameSettingTypeKey, displayLastName);
        displayMiddleName = settingEntity.getSettingValueAsBoolean(DisplayMiddleNameSettingTypeKey, displayMiddleName);

        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByEmail = settingEntity.getSettingValueAsString(FilterByEmailSettingTypeKey, filterByEmail);
        filterByFirstName = settingEntity.getSettingValueAsString(FilterByFirstNameSettingTypeKey, filterByFirstName);
        filterByGroups = settingEntity.getSettingValueAsString(FilterByGroupsSettingTypeKey, filterByGroups);
        filterByLastName = settingEntity.getSettingValueAsString(FilterByLastNameSettingTypeKey, filterByLastName);
        filterByMiddleName = settingEntity.getSettingValueAsString(FilterByMiddleNameSettingTypeKey, filterByMiddleName);
        filterByUsername = settingEntity.getSettingValueAsString(FilterByUsernameSettingTypeKey, filterByUsername);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, Object> filters = dataTable.getFilters();
        filterByEmail = (String) filters.get("email");
        filterByFirstName = (String) filters.get("firstName");
        filterByGroups = (String) filters.get("groups");
        filterByLastName = (String) filters.get("lastName");
        filterByMiddleName = (String) filters.get("middleName");
        filterByUsername = (String) filters.get("username");
    }

    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayEmailSettingTypeKey, displayEmail);
        settingEntity.setSettingValue(DisplayFirstNameSettingTypeKey, displayFirstName);
        settingEntity.setSettingValue(DisplayGroupsSettingTypeKey, displayGroups);
        settingEntity.setSettingValue(DisplayLastNameSettingTypeKey, displayLastName);
        settingEntity.setSettingValue(DisplayMiddleNameSettingTypeKey, displayMiddleName);

        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByEmailSettingTypeKey, filterByEmail);
        settingEntity.setSettingValue(FilterByFirstNameSettingTypeKey, filterByFirstName);
        settingEntity.setSettingValue(FilterByGroupsSettingTypeKey, filterByGroups);
        settingEntity.setSettingValue(FilterByLastNameSettingTypeKey, filterByLastName);
        settingEntity.setSettingValue(FilterByMiddleNameSettingTypeKey, filterByMiddleName);
        settingEntity.setSettingValue(FilterByUsernameSettingTypeKey, filterByUsername);
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

    @Override
    public DataModel getListDataModel() {
        DataModel userInfoDataModel = super.getListDataModel();

        if (loadedDataModelHashCode == null || loadedDataModelHashCode != userInfoDataModel.hashCode()) {
            Iterator<UserInfo> userInfoIterator = userInfoDataModel.iterator();
            while (userInfoIterator.hasNext()) {
                UserInfo userInfo = userInfoIterator.next();
                String userGroupString = CdbEntityController.displayEntityList(userInfo.getUserGroupList());
                userInfo.setUserGroupListString(userGroupString);
            }
            loadedDataModelHashCode = userInfoDataModel.hashCode();
        }

        return userInfoDataModel;
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

    /**
     * Converter class for user info objects.
     */
    @FacesConverter(value = "userInfoConverter", forClass = UserInfo.class)
    public static class UserInfoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                UserInfoController controller = (UserInfoController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "userInfoController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to user info object.");
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
            if (object instanceof UserInfo) {
                UserInfo o = (UserInfo) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + UserInfo.class.getName());
            }
        }

    }
}
