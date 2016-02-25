/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.controllers.view;

import gov.anl.aps.cdb.portal.controllers.DesignController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * Controller used to manage settings for the design member view for the Design Entity. 
 */
@Named("designMemberViewController")
@SessionScoped
public class DesignMemberController extends DesignController {

    private static final String DisplayMemberNumberOfItemsPerPageSettingTypeKey = "Design.Member.List.Display.NumberOfItemsPerPage";
    private static final String DisplayMemberIdSettingTypeKey = "Design.Member.List.Display.Id";
    private static final String DisplayMemberDescriptionSettingTypeKey = "Design.Member.List.Display.Description";
    private static final String DisplayMemberOwnerUserSettingTypeKey = "Design.Member.List.Display.OwnerUser";
    private static final String DisplayMemberOwnerGroupSettingTypeKey = "Design.Member.List.Display.OwnerGroup";
    private static final String DisplayMemberCreatedByUserSettingTypeKey = "Design.Member.List.Display.CreatedByUser";
    private static final String DisplayMemberCreatedOnDateTimeSettingTypeKey = "Design.Member.List.Display.CreatedOnDateTime";
    private static final String DisplayMemberLastModifiedByUserSettingTypeKey = "Design.Member.List.Display.LastModifiedByUser";
    private static final String DisplayMemberLastModifiedOnDateTimeSettingTypeKey = "Design.Member.List.Display.LastModifiedOnDateTime";
    
    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }
        
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayMemberNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberDescriptionSettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayMemberLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());
    }
    
    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }
        
        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayMemberNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayMemberIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayMemberDescriptionSettingTypeKey, displayDescription);
        displayOwnerUser = sessionUser.getUserSettingValueAsBoolean(DisplayMemberOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(DisplayMemberOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayMemberCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayMemberCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayMemberLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayMemberLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
    }
    
    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }
        
        sessionUser.setUserSettingValue(DisplayMemberNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayMemberIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayMemberDescriptionSettingTypeKey, displayDescription);
        sessionUser.setUserSettingValue(DisplayMemberOwnerUserSettingTypeKey, displayOwnerUser);
        sessionUser.setUserSettingValue(DisplayMemberOwnerGroupSettingTypeKey, displayOwnerGroup);
        sessionUser.setUserSettingValue(DisplayMemberCreatedByUserSettingTypeKey, displayCreatedByUser);
        sessionUser.setUserSettingValue(DisplayMemberCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        sessionUser.setUserSettingValue(DisplayMemberLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        sessionUser.setUserSettingValue(DisplayMemberLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
    }

}
