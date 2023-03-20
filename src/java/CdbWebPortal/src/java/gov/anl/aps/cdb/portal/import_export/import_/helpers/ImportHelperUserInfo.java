/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.UserGroupController;
import gov.anl.aps.cdb.portal.controllers.UserInfoController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.BooleanColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefListColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperUserInfo extends ImportHelperBase<UserInfo, UserInfoController> {

    private static final String KEY_SET_PASSWORD = "importSetPassword";
    private static final String KEY_PASSWORD = "importPassword";
    
    @Override
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(existingItemIdColumnSpec());
        specs.add(deleteExistingItemColumnSpec());
        
        specs.add(new StringColumnSpec(
                "Username", 
                "username", 
                "setUsername", 
                "User's username", 
                "getUsername", 
                ColumnModeOptions.rdCREATErUPDATE(), 
                16));
        
        specs.add(new StringColumnSpec(
                "First Name", 
                "firstName", 
                "setFirstName", 
                "User's first name", 
                "getFirstName", 
                ColumnModeOptions.rCREATErUPDATE(), 
                16));
        
        specs.add(new StringColumnSpec(
                "Last Name", 
                "lastName", 
                "setLastName", 
                "User's last name", 
                "getLastName", 
                ColumnModeOptions.rCREATErUPDATE(), 
                16));
        
        specs.add(new StringColumnSpec(
                "Middle Name", 
                "middleName", 
                "setMiddleName", 
                "User's middle name", 
                "getMiddleName", 
                ColumnModeOptions.oCREATEoUPDATE(), 
                16));
        
        specs.add(new StringColumnSpec(
                "Email", 
                "email", 
                "setEmail", 
                "User's email", 
                "getEmail", 
                ColumnModeOptions.oCREATEoUPDATE(), 
                64));
        
        specs.add(new BooleanColumnSpec(
                "Set Password", 
                KEY_SET_PASSWORD, 
                "setImportSetPassword", 
                "Specify TRUE/yes/1 to set password for user.",
                "getImportSetPassword",
                ColumnModeOptions.oCREATEoUPDATE()));
        
        specs.add(new StringColumnSpec(
                "Password", 
                KEY_PASSWORD, 
                "setImportPassword", 
                "User's password (blank for ldap credentials), ignored unless 'Set Password' is specified", 
                "getImportPassword", 
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Description", 
                "description", 
                "setDescription", 
                "Description of user group", 
                "getDescription", 
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new IdOrNameRefListColumnSpec(
                "Groups",
                "userGroupListString",
                "setUserGroupList",
                "Comma-separated list of IDs or names of CDB UserGroups. List of names must be prefixed with '#'.",
                "getUserGroupListString",
                "getUserGroupListString",
                ColumnModeOptions.oCREATEoUPDATE(),
                UserGroupController.getInstance(),
                List.class,
                ""));
        
        return specs;
    } 
   
    @Override
    public UserInfoController getEntityController() {
        return UserInfoController.getInstance();
    }
    
    /**
     * Specifies whether helper supports updating existing instances.  Defaults
     * to false. Subclasses override to customize.
     */
    @Override
    public boolean supportsModeUpdate() {
        return true;
    }

    @Override
    public boolean supportsModeDelete() {
        return true;
    }

    @Override
    public String getFilenameBase() {
        return "UserInfo";
    }
    
    @Override
    protected UserInfo newInvalidUpdateInstance() {
        return new UserInfo();
    }
    
    private void updatePassword(UserInfo entity, Map<String, Object> rowMap) {
        Boolean setPassword = (Boolean) rowMap.get(KEY_SET_PASSWORD);
        if (setPassword == null) {
            setPassword = false;
        }
        if (setPassword) {
            String passwordVal = (String) rowMap.get(KEY_PASSWORD);
            if ((passwordVal != null) && (!passwordVal.isEmpty())) {
                entity.setPasswordEntry(passwordVal);
            } else {
                entity.setPassword(null);
            }
        }
    }

    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        UserInfo entity = getEntityController().createEntityInstance();
        updatePassword(entity, rowMap);
        return new CreateInfo(entity, true, "");
    }  
    
    @Override
    protected ValidInfo updateEntityInstance(UserInfo entity, Map<String, Object> rowMap) {
        updatePassword(entity, rowMap);
        return new ValidInfo(true, "");
    }
}
