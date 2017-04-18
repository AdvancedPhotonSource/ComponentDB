/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.common.utilities.CryptUtility;
import gov.anl.aps.cdb.common.utilities.StringUtility;
import gov.anl.aps.cdb.portal.controllers.settings.UserInfoSettings;
import gov.anl.aps.cdb.portal.model.db.beans.SettingTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserRole;
import gov.anl.aps.cdb.portal.model.db.entities.UserRolePK;
import gov.anl.aps.cdb.portal.model.db.entities.UserSetting;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import org.apache.log4j.Logger;

@Named("userInfoController")
@SessionScoped
public class UserInfoController extends CdbEntityController<UserInfo, UserInfoFacade, UserInfoSettings> implements Serializable {   

    private static final Logger logger = Logger.getLogger(UserInfoController.class.getName());

    @EJB
    private UserInfoFacade userInfoFacade;
    
    @EJB
    private SettingTypeFacade settingTypeFacade; 
    
    private String passwordEntry = null;   

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
        for (SettingType settingType : settingTypeFacade.findAll()) {
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
    public void prepareEntityInsert(UserInfo userInfo) throws ObjectAlreadyExists, CdbException {
        UserInfo existingUser = userInfoFacade.findByUsername(userInfo.getUsername());       
        if (existingUser != null) {
            throw new ObjectAlreadyExists("User " + userInfo.getUsername() + " already exists.");
        }
        
        validateUserInformation(userInfo);
        
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
        validateUserInformation(userInfo);
        
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
    
    private void validateUserInformation(UserInfo userInfo) throws CdbException {
        String email = userInfo.getEmail(); 
        if (email != null && !email.isEmpty()) {
            // validate email
            if (!StringUtility.isEmailAddressValid(email)) {
                throw new CdbException("Invalid email address was entered");
            }
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

    @Override
    protected UserInfoSettings createNewSettingObject() {
        return new UserInfoSettings(this);
    }

    /**
     * Converter class for user info objects.
     */
    @FacesConverter(forClass = UserInfo.class)
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
