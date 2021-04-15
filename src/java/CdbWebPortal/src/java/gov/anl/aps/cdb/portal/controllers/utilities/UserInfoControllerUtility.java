/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.common.utilities.CryptUtility;
import gov.anl.aps.cdb.common.utilities.StringUtility;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.entities.UserRole;
import gov.anl.aps.cdb.portal.model.db.entities.UserRolePK;
import gov.anl.aps.cdb.portal.model.db.entities.UserSetting;
import java.util.List;
import javax.ejb.EJB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class UserInfoControllerUtility extends CdbEntityControllerUtility<UserInfo, UserInfoFacade> {

    @EJB
    UserInfoFacade userInfoFacade;

    private static final Logger logger = LogManager.getLogger(UserInfoControllerUtility.class.getName());

    public UserInfoControllerUtility() {
        if (userInfoFacade == null) {
            userInfoFacade = UserInfoFacade.getInstance();
        }

    }

    @Override
    protected UserInfoFacade getEntityDbFacade() {
        return userInfoFacade;
    }

    @Override
    protected void prepareEntityInsert(UserInfo userInfo, UserInfo insertyByUser) throws CdbException {
        UserInfo existingUser = userInfoFacade.findByUsername(userInfo.getUsername());
        if (existingUser != null) {
            throw new ObjectAlreadyExists("User " + userInfo.getUsername() + " already exists.");
        }

        validateUserInformation(userInfo);

        logger.debug("Inserting new user " + userInfo.getUsername());
        String passwordEntry = userInfo.getPasswordEntry();
        if (passwordEntry != null && !passwordEntry.isEmpty()) {
            String cryptedPassword = CryptUtility.cryptPasswordWithPbkdf2(passwordEntry);
            userInfo.setPassword(cryptedPassword);
            logger.debug("New user crypted password: " + cryptedPassword);
        }
    }

    @Override
    public void prepareEntityUpdate(UserInfo userInfo, UserInfo updatedByUserInfo) throws CdbException {
        prepareSaveUserRoleList(userInfo);
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
        String passwordEntry = userInfo.getPasswordEntry();
        if (passwordEntry != null && !passwordEntry.isEmpty()) {
            String cryptedPassword = CryptUtility.cryptPasswordWithPbkdf2(passwordEntry);
            userInfo.setPassword(cryptedPassword);
            logger.debug("Updated crypted password: " + cryptedPassword);
        }
    }

    public void prepareSaveUserRoleList(UserInfo userInfo) throws CdbException {
        for (UserRole currentUserRole : userInfo.getUserRoleList()) {
            UserRolePK currentPK = UserRole.createPrimaryKeyObject(currentUserRole);
            for (UserRole userRole : userInfo.getUserRoleList()) {
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

    private void validateUserInformation(UserInfo userInfo) throws CdbException {
        String email = userInfo.getEmail();
        if (email != null && !email.isEmpty()) {
            // validate email
            if (!StringUtility.isEmailAddressValid(email)) {
                throw new CdbException("Invalid email address was entered");
            }
        }
    }

    @Override
    public String getEntityInstanceName(UserInfo entity) {
        if (entity != null) {
            return entity.getUsername();
        }
        return "";
    }
    
    @Override
    public String getEntityTypeName() {
        return "userInfo";
    }
    
    @Override
    public String getDisplayEntityTypeName() {
        return "user";
    }   

    @Override
    public UserInfo createEntityInstance(UserInfo sessionUser) {
        return new UserInfo();
    }

}
