/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.controllers.ItemMemberViewController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;

/**
 *
 * @author djarosz
 */
public class ItemMemberViewSettings extends ItemSettings<ItemMemberViewController> {
    
    private static final String DisplayMemberNumberOfItemsPerPageSettingTypeKey = "Item.Member.List.Display.NumberOfItemsPerPage";
    private static final String DisplayMemberIdSettingTypeKey = "Item.Member.List.Display.Id";
    private static final String DisplayMemberDescriptionSettingTypeKey = "Item.Member.List.Display.Description";
    private static final String DisplayMemberOwnerUserSettingTypeKey = "Item.Member.List.Display.OwnerUser";
    private static final String DisplayMemberOwnerGroupSettingTypeKey = "Item.Member.List.Display.OwnerGroup";
    private static final String DisplayMemberCreatedByUserSettingTypeKey = "Item.Member.List.Display.CreatedByUser";
    private static final String DisplayMemberCreatedOnDateTimeSettingTypeKey = "Item.Member.List.Display.CreatedOnDateTime";
    private static final String DisplayMemberLastModifiedByUserSettingTypeKey = "Item.Member.List.Display.LastModifiedByUser";
    private static final String DisplayMemberLastModifiedOnDateTimeSettingTypeKey = "Item.Member.List.Display.LastModifiedOnDateTime";

    public ItemMemberViewSettings(ItemMemberViewController parentController) {
        super(parentController);
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
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
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayMemberNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayMemberIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayMemberDescriptionSettingTypeKey, displayDescription);
        displayOwnerUser = settingEntity.getSettingValueAsBoolean(DisplayMemberOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = settingEntity.getSettingValueAsBoolean(DisplayMemberOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = settingEntity.getSettingValueAsBoolean(DisplayMemberCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayMemberCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = settingEntity.getSettingValueAsBoolean(DisplayMemberLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayMemberLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        settingEntity.setSettingValue(DisplayMemberNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayMemberIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayMemberDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayMemberOwnerUserSettingTypeKey, displayOwnerUser);
        settingEntity.setSettingValue(DisplayMemberOwnerGroupSettingTypeKey, displayOwnerGroup);
        settingEntity.setSettingValue(DisplayMemberCreatedByUserSettingTypeKey, displayCreatedByUser);
        settingEntity.setSettingValue(DisplayMemberCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        settingEntity.setSettingValue(DisplayMemberLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        settingEntity.setSettingValue(DisplayMemberLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
    }
    
}
