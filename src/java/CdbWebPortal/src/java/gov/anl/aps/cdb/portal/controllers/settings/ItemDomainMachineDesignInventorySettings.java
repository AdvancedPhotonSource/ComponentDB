/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;

/**
 *
 * @author djarosz
 */
public class ItemDomainMachineDesignInventorySettings extends ItemDomainMachineDesignSettings {

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
    
    private static final String DisplayPropertyTypeId1SettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.PropertyTypeId1";
    private static final String DisplayPropertyTypeId2SettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.PropertyTypeId2";
    private static final String DisplayPropertyTypeId3SettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.PropertyTypeId3";
    private static final String DisplayPropertyTypeId4SettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.PropertyTypeId4";
    private static final String DisplayPropertyTypeId5SettingTypeKey = "ItemDomainMachineDesignInventory.List.Display.PropertyTypeId5";


    public ItemDomainMachineDesignInventorySettings(ItemDomainMachineDesignController parentController) {
        super(parentController);        
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
        
        displayPropertyTypeId1 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId1SettingTypeKey).getDefaultValue());
        displayPropertyTypeId2 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId2SettingTypeKey).getDefaultValue());
        displayPropertyTypeId3 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId3SettingTypeKey).getDefaultValue());
        displayPropertyTypeId4 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId4SettingTypeKey).getDefaultValue());
        displayPropertyTypeId5 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId5SettingTypeKey).getDefaultValue());
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
        
        displayPropertyTypeId1 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        displayPropertyTypeId2 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        displayPropertyTypeId3 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        displayPropertyTypeId4 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        displayPropertyTypeId5 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);
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
        
        settingEntity.setSettingValue(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        settingEntity.setSettingValue(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        settingEntity.setSettingValue(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        settingEntity.setSettingValue(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        settingEntity.setSettingValue(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);
    }

    @Override
    public Boolean getDisplayRowExpansion() {
        return true;
    }
    
    public Boolean getDisplayStatus() {
        return true;
    }

}
