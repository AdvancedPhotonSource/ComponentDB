/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.constants.CdbRole;
import gov.anl.aps.cdb.portal.model.db.beans.SettingTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.UserGroupFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Setting controller.
 */
@Named("settingController")
@SessionScoped
public class SettingController implements Serializable {

    private final String USER_GROUP_CONTROLLER_NAME = "userGroupController";
    private final String USER_INFO_CONTROLLER_NAME = "userInfoController";

    @EJB
    private SettingTypeFacade settingTypeFacade;

    @EJB
    private UserGroupFacade userGroupFacade;

    private static final Logger logger = Logger.getLogger(SettingController.class.getName());

    private UserInfoController userInfoController = null;
    private UserGroupController userGroupController = null;

    private UserGroup userGroupForSettingsView = null;
    private SettingEntity currentSettingEntity = null;
    private Date settingsTimestamp = null;
    private List<UserGroup> availableUserGroupsForSelection = null;

    public SettingController() {
        settingsTimestamp = new Date();
    }

    public SettingEntity getCurrentSettingEntity() {
        if (settingsTimestamp == null) {
            currentSettingEntity = getUserGroupForSettingsView();
            if (currentSettingEntity == null) {
                currentSettingEntity = (SettingEntity) SessionUtility.getUser();
            }
            settingsTimestamp = new Date();
        }
        return currentSettingEntity;
    }

    public boolean SettingsRequireLoading(Date lastSettingLoadTime) {
        if (lastSettingLoadTime == null) {
            return true;
        } else if (getCurrentSettingEntity() != null) {
            if (getCurrentSettingEntity().areSettingsModifiedAfterDate(lastSettingLoadTime)) {
                return true;
            }
        }

        return areSettingListsDifferentAfterDate(lastSettingLoadTime);
    }

    public boolean areSettingListsDifferentAfterDate(Date date) {
        return date == null || settingsTimestamp == null || settingsTimestamp.after(date);
    }

    public void saveSettingListForSettingEntity() {
        UserGroup sessionUserGroup = getUserGroupForSettingsView();
        if (sessionUserGroup != null) {
            if (userGroupController == null) {
                userGroupController = (UserGroupController) SessionUtility.findBean(USER_GROUP_CONTROLLER_NAME);
            }

            logger.debug("Saving settings for group view.");
            UserGroup group = userGroupController.getEntity(sessionUserGroup.getId());
            group.setUserGroupSettingList(sessionUserGroup.getUserGroupSettingList());
            userGroupController.setCurrent(group);
            userGroupController.update();
            return;
        }
        //TODO add permission checks for group setting mod. 

        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser != null) {
            if (userInfoController == null) {
                userInfoController = (UserInfoController) SessionUtility.findBean(USER_INFO_CONTROLLER_NAME);
            }

            logger.debug("Saving settings for session user");
            UserInfo user = userInfoController.getEntity(sessionUser.getId());
            user.setUserSettingList(sessionUser.getUserSettingList());
            userInfoController.setCurrent(user);
            userInfoController.update();
        }
    }

    public UserGroup getUserGroupForSettingsView() {
        return userGroupForSettingsView;
    }

    public void setUserGroupForSettingsView(UserGroup userGroupSettingsView) {
        UserGroup lastUserGroupSettingView = getUserGroupForSettingsView();
        if ((lastUserGroupSettingView != null && userGroupSettingsView != null
                && !Objects.equals(userGroupSettingsView.getId(), lastUserGroupSettingView.getId()))
                || (userGroupSettingsView == null && lastUserGroupSettingView != null)
                || (userGroupSettingsView != null && lastUserGroupSettingView == null)) {
            settingsTimestamp = null;
            populateSessionSettingEntityFromSettingTypeDefaults(userGroupSettingsView);
            this.userGroupForSettingsView = userGroupSettingsView;
            try {
                reloadCurrentViewToApplySettings();
            } catch (IOException ex) {
                SessionUtility.addErrorMessage("Could not reload page",
                        "Settings were not applied... could not reload page.");
            }
        }
    }

    public void reloadCurrentViewToApplySettings() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
    }

    private void resetSessionVariables() {
        settingsTimestamp = null;
        currentSettingEntity = null;
        userGroupForSettingsView = null;
        availableUserGroupsForSelection = null;
    }

    public void loadSessionUser(UserInfo sessionUser) {
        resetSessionVariables();
        populateSessionSettingEntityFromSettingTypeDefaults(sessionUser);
    }

    // TODO add checks that determine if any additional settings need to be added. 
    private void populateSessionSettingEntityFromSettingTypeDefaults(SettingEntity settingEntity) {
        if (settingEntity != null) {
            if (!settingEntity.hasSettings()) {
                List<SettingType> settingTypeList = settingTypeFacade.findAll();
                settingEntity.populateDefaultSettingList(settingTypeList);
            }
        }
    }

    private UserInfo getSessionUser() {
        return (UserInfo) SessionUtility.getUser();
    }

    public String getDefaultSelectionLabel() {
        UserInfo sessionUser = getSessionUser();
        if (sessionUser != null) {
            return "User(" + sessionUser.getUsername() + ")";
        }
        return "Default";
    }

    public String getViewAsLabel() {
        SettingEntity settingEntity = getCurrentSettingEntity();
        if (settingEntity instanceof UserGroup) {
            return "Group(" + ((UserGroup) settingEntity).getName() + ")";
        }

        return getDefaultSelectionLabel();
    }

    /**
     * Fetch user groups with settings and all user groups that current user has
     * permission to edit.
     *
     * @return
     */
    public List<UserGroup> getAvailableUserGroupsForSelection() {
        if (availableUserGroupsForSelection == null) {
            if (SessionUtility.getUser() != null) {
                availableUserGroupsForSelection = sortUserGroupsForUser(userGroupFacade.findAll());
            } else {
                availableUserGroupsForSelection = userGroupFacade.findUserGroupsWithSettings();
            }
        }
        return availableUserGroupsForSelection;
    }

    /**
     * Sort groups for user based on groups with settings and groups with
     * permission to edit for the highest priority.
     *
     * @param userGroupList
     * @return
     */
    private List<UserGroup> sortUserGroupsForUser(List<UserGroup> userGroupList) {
        List<UserGroup> newUserGroupList = new ArrayList<>();

        // Remove all without settings and permissions. 
        List<UserGroup> toRemoveUserGroupList = new ArrayList<>();
        for (UserGroup userGroup : userGroupList) {
            if (userGroup.getUserGroupSettingList() == null
                    || userGroup.getUserGroupSettingList().isEmpty()
                    && !isSessionUserHaveGroupWritePermission(userGroup)) {
                toRemoveUserGroupList.add(userGroup);
            }
        }

        // Remove the un needed groups. 
        for (UserGroup userGroup : toRemoveUserGroupList) {
            userGroupList.remove(userGroup);
        }

        // Add groups user is member of. 
        for (UserGroup userGroup : userGroupList) {
            if (isSessionUserMemeberOfGroup(userGroup)) {
                newUserGroupList.add(userGroup);
            }
        }

        // Add groups user has permission for 
        for (UserGroup userGroup : userGroupList) {
            if (isSessionUserHaveGroupWritePermission(userGroup)
                    && !newUserGroupList.contains(userGroup)) {
                newUserGroupList.add(userGroup);
            }
        }

        // Add remaining groups. 
        for (UserGroup userGroup : userGroupList) {
            if (!newUserGroupList.contains(userGroup)) {
                newUserGroupList.add(userGroup);
            }
        }

        return newUserGroupList;
    }

    public boolean isSessionUserHaveGroupWritePermission(UserGroup userGroup) {
        UserInfo sessionUser = getSessionUser();
        if (sessionUser != null) {
            // TODO user the group settings writeable role that oculd be assigned to users. 
            return SessionUtility.getRole().equals(CdbRole.ADMIN);
        }
        return false;
    }

    public boolean isSessionUserMemeberOfGroup(UserGroup userGroup) {
        UserInfo sessionUser = getSessionUser();
        if (sessionUser != null) {
            return userGroup.getUserInfoList().contains(sessionUser);
        }
        return false;
    }
}
