/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignBaseController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ItemDomainMachineDesignDeletedItemSettings extends ItemDomainMachineDesignSettings {

    private static final String DisplayAlternateNameSettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.AlternateName";
    private static final String DisplayDesignDescriptionSettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.Description";
    private static final String DisplayLocationSettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.Location";
    private static final String DisplayLocationDetailsSettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.LocationDetails";
    private static final String DisplayProjectSettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.ItemProject";
    private static final String DisplayIdSettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.Id";
    private static final String DisplayQrIdSettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.QrId";
    private static final String DisplayOwnerUserSettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.OwnerGroup";
    private static final String DisplayCreatedByUserSettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.CreatedOnDateTime";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.LastModifiedOnDateTime";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.NumberOfItemsPerPage";


    public ItemDomainMachineDesignDeletedItemSettings(ItemDomainMachineDesignBaseController parentController) {
        super(parentController);
        displayNumberOfItemsPerPage = 25; 
    }  
    
    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        super.updateSettingsFromSettingTypeDefaults(settingTypeMap);
        displayAlternateName = Boolean.parseBoolean(settingTypeMap.get(DisplayAlternateNameSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDesignDescriptionSettingTypeKey).getDefaultValue());
        displayItemProject = Boolean.parseBoolean(settingTypeMap.get(DisplayProjectSettingTypeKey).getDefaultValue());
        displayLocation = Boolean.parseBoolean(settingTypeMap.get(DisplayLocationSettingTypeKey).getDefaultValue());
        displayLocationDetails = Boolean.parseBoolean(settingTypeMap.get(DisplayLocationDetailsSettingTypeKey).getDefaultValue());

        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayQrId = Boolean.parseBoolean(settingTypeMap.get(DisplayQrIdSettingTypeKey).getDefaultValue()); 
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());
        
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        super.updateSettingsFromSessionSettingEntity(settingEntity);
        displayAlternateName = settingEntity.getSettingValueAsBoolean(DisplayAlternateNameSettingTypeKey, displayAlternateName);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDesignDescriptionSettingTypeKey, displayDescription);
        displayItemProject = settingEntity.getSettingValueAsBoolean(DisplayProjectSettingTypeKey, displayItemProject);
        displayLocation = settingEntity.getSettingValueAsBoolean(DisplayLocationSettingTypeKey, displayLocation);
        displayLocationDetails = settingEntity.getSettingValueAsBoolean(DisplayLocationDetailsSettingTypeKey, displayLocationDetails);

        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayQrId = settingEntity.getSettingValueAsBoolean(DisplayQrIdSettingTypeKey, displayQrId);
        displayOwnerUser = settingEntity.getSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = settingEntity.getSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = settingEntity.getSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
        
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        super.saveSettingsForSessionSettingEntity(settingEntity);
        settingEntity.setSettingValue(DisplayAlternateNameSettingTypeKey, displayAlternateName);
        settingEntity.setSettingValue(DisplayDesignDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayProjectSettingTypeKey, displayItemProject);
        settingEntity.setSettingValue(DisplayLocationSettingTypeKey, displayLocation);
        settingEntity.setSettingValue(DisplayLocationDetailsSettingTypeKey, displayLocationDetails);

        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayQrIdSettingTypeKey, displayQrId);
        settingEntity.setSettingValue(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        settingEntity.setSettingValue(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        settingEntity.setSettingValue(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        settingEntity.setSettingValue(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        settingEntity.setSettingValue(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        settingEntity.setSettingValue(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
        
        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
    }

    @Override
    public Boolean getDisplayRowExpansion() {
        return true;
    }
    
    public Boolean getDisplayStatus() {
        return true;
    }

}
