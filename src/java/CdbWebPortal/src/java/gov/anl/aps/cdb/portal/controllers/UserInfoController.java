/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.UserInfoSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.UserInfoControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperUserInfo;
import gov.anl.aps.cdb.portal.model.db.beans.SettingTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserRole;
import gov.anl.aps.cdb.portal.model.db.entities.UserSetting;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named(UserInfoController.CONTROLLER_NAMED)
@SessionScoped
public class UserInfoController extends CdbEntityController<UserInfoControllerUtility, UserInfo, UserInfoFacade, UserInfoSettings> implements Serializable {   

    private static final Logger logger = LogManager.getLogger(UserInfoController.class.getName());
    public static final String CONTROLLER_NAMED = "userInfoController";

    @EJB
    private UserInfoFacade userInfoFacade;
    
    @EJB
    private SettingTypeFacade settingTypeFacade;         

    private Integer loadedDataModelHashCode = null;
    
    private UserInfoControllerUtility controllerUtility; 

    @Override
    protected UserInfoControllerUtility getControllerUtility() {
        if (controllerUtility == null) {
            controllerUtility = new UserInfoControllerUtility();
        }
        return controllerUtility; 
    }

    public static UserInfoController getInstance() {
        return (UserInfoController) SessionUtility.findBean(UserInfoController.CONTROLLER_NAMED);
    }

    @Override
    protected UserInfoFacade getEntityDbFacade() {
        return userInfoFacade;
    }

    @Override
    public String getEntityTypeGroupName() {
        return "userGroup";
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
    public void processCreateRequestParams() {
        super.processCreateRequestParams();
        updateUserGroupListStringForCurrent();
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
        String passwordEntry = userInfo.getPasswordEntry();
        passwordEntry = null;
        updateUserGroupListStringForItem(userInfo);
        return super.prepareEdit(userInfo);
    } 

    @Override
    protected boolean verifyUserIsAuthorizedToEdit(UserInfo entity, UserInfo userInfo) {
        if (entity.equals(userInfo)) {
            // User can edit their own user profile. 
            return true; 
        }
        return super.verifyUserIsAuthorizedToEdit(entity, userInfo); 
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
    
    public void resetAllSettingsForCurrentUser() {
        getCurrent().getUserSettingList().clear();         
        
        //Save new settings 
        saveSettingListForSessionUser();
        
        // Load default settings 
        UserInfo current = getCurrent();
        SettingController.getInstance().loadSessionUser(current);
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
        UserInfo current = getCurrent();
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
                updateUserGroupListStringForItem(userInfo);
            }
            loadedDataModelHashCode = userInfoDataModel.hashCode();
        }

        return userInfoDataModel;
    }
    
    public void updateUserGroupListStringForCurrent() {
        UserInfo current = getCurrent();
        updateUserGroupListStringForItem(current);
    }
    
    private void updateUserGroupListStringForItem(UserInfo userInfo) {        
        String userGroupString = CdbEntityController.displayEntityList(userInfo.getUserGroupList());
        if (userGroupString.isEmpty()) {
            userGroupString = "No groups assigned"; 
        }
        userInfo.setUserGroupListString(userGroupString);
    }
    
    @Override
    public boolean entityHasGroups() {
        return true;
    }

    @Override
    protected UserInfoSettings createNewSettingObject() {
        return new UserInfoSettings(this);
    }

    @Override
    protected UserInfoControllerUtility createControllerUtilityInstance() {
        return new UserInfoControllerUtility();
    }

    /**
     * Converter class for user info objects.
     */
    @FacesConverter(value = "userInfoConverter")
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

    @Override
    public boolean getEntityDisplayImportButton() {
        return true;
    }

    @Override
    protected DomainImportExportInfo initializeDomainImportInfo() {
        
        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();
        
        formatInfo.add(new ImportExportFormatInfo(
                "Basic User Info Create/Update/Delete Format", ImportHelperUserInfo.class));
        
        String completionUrl = "/views/userInfo/list?faces-redirect=true";
        
        return new DomainImportExportInfo(formatInfo, completionUrl);
    }
    
    @Override
    public boolean getEntityDisplayExportButton() {
        return true;
    }
    
    @Override
    protected DomainImportExportInfo initializeDomainExportInfo() {
        
        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();
        
        formatInfo.add(new ImportExportFormatInfo(
                "Basic User Info Create/Update/Delete Format", ImportHelperUserInfo.class));
        
        String completionUrl = "/views/userInfo/list?faces-redirect=true";
        
        return new DomainImportExportInfo(formatInfo, completionUrl);
    }
    
}
