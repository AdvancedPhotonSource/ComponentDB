/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import java.util.Map;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignIOCController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;

/**
 *
 * @author djarosz
 */
public class ItemDomainMachineDesignIOCSettings extends ItemSettings<ItemDomainMachineDesignIOCController> {

    private static final String DISPLAY_ALTERNATE_NAME_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.AlternateName";
    private static final String DISPLAY_DESIGN_DESCRIPTION_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.Description";
    private static final String DISPLAY_PROJECT_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.ItemProject";
    private static final String DISPLAY_ID_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.Id";
    private static final String DISPLAY_QR_ID_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.QrId";
    private static final String DISPLAY_OWNER_USER_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.OwnerUser";
    private static final String DISPLAY_OWNER_GROUP_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.OwnerGroup";
    private static final String DISPLAY_CREATED_BY_USER_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.CreatedByUser";
    private static final String DISPLAY_CREATED_ON_DATE_TIME_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.CreatedOnDateTime";
    private static final String DISPLAY_LAST_MODIFIED_BY_USER_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.LastModifiedByUser";
    private static final String DISPLAY_LAST_MODIFIED_ON_DATE_TIME_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.LastModifiedOnDateTime";

    private static final String DISPLAY_PROPERTY_TYPE_ID_1_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.PropertyTypeId1";
    private static final String DISPLAY_PROPERTY_TYPE_ID_2_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.PropertyTypeId2";
    private static final String DISPLAY_PROPERTY_TYPE_ID_3_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.PropertyTypeId3";
    private static final String DISPLAY_PROPERTY_TYPE_ID_4_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.PropertyTypeId4";
    private static final String DISPLAY_PROPERTY_TYPE_ID_5_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.PropertyTypeId5";

    private static final String DISPLAY_MACHINE_TAG_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.MachineTag";
    private static final String DISPLAY_FUNCTION_TAG_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.FunctionTag";
    private static final String DISPLAY_DEPLOYMENT_STATUS_SETTING_TYPE_KEY = "ItemDomainMachineDesignIOC.List.Display.DeploymentStatus";

    private boolean machineTagDisplay;
    private boolean functionTagDisplay;
    private boolean deploymentStatusDisplay;

    private String machineTagFilter;
    private String functionTagFilter;
    private String deploymentStatusFilter;

    public ItemDomainMachineDesignIOCSettings(ItemDomainMachineDesignIOCController parentController) {
        super(parentController);
        displayNumberOfItemsPerPage = 25;
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        super.updateSettingsFromSettingTypeDefaults(settingTypeMap);

        displayItemIdentifier1 = Boolean.valueOf(settingTypeMap.get(DISPLAY_ALTERNATE_NAME_SETTING_TYPE_KEY).getDefaultValue());
        displayDescription = Boolean.valueOf(settingTypeMap.get(DISPLAY_DESIGN_DESCRIPTION_SETTING_TYPE_KEY).getDefaultValue());
        displayItemProject = Boolean.valueOf(settingTypeMap.get(DISPLAY_PROJECT_SETTING_TYPE_KEY).getDefaultValue());

        displayId = Boolean.valueOf(settingTypeMap.get(DISPLAY_ID_SETTING_TYPE_KEY).getDefaultValue());
        displayQrId = Boolean.valueOf(settingTypeMap.get(DISPLAY_QR_ID_SETTING_TYPE_KEY).getDefaultValue());
        displayOwnerUser = Boolean.valueOf(settingTypeMap.get(DISPLAY_OWNER_USER_SETTING_TYPE_KEY).getDefaultValue());
        displayOwnerGroup = Boolean.valueOf(settingTypeMap.get(DISPLAY_OWNER_GROUP_SETTING_TYPE_KEY).getDefaultValue());
        displayCreatedByUser = Boolean.valueOf(settingTypeMap.get(DISPLAY_CREATED_BY_USER_SETTING_TYPE_KEY).getDefaultValue());
        displayCreatedOnDateTime = Boolean.valueOf(settingTypeMap.get(DISPLAY_CREATED_ON_DATE_TIME_SETTING_TYPE_KEY).getDefaultValue());
        displayLastModifiedByUser = Boolean.valueOf(settingTypeMap.get(DISPLAY_LAST_MODIFIED_BY_USER_SETTING_TYPE_KEY).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.valueOf(settingTypeMap.get(DISPLAY_LAST_MODIFIED_ON_DATE_TIME_SETTING_TYPE_KEY).getDefaultValue());

        displayPropertyTypeId1 = parseSettingValueAsInteger(settingTypeMap.get(DISPLAY_PROPERTY_TYPE_ID_1_SETTING_TYPE_KEY).getDefaultValue());
        displayPropertyTypeId2 = parseSettingValueAsInteger(settingTypeMap.get(DISPLAY_PROPERTY_TYPE_ID_2_SETTING_TYPE_KEY).getDefaultValue());
        displayPropertyTypeId3 = parseSettingValueAsInteger(settingTypeMap.get(DISPLAY_PROPERTY_TYPE_ID_3_SETTING_TYPE_KEY).getDefaultValue());
        displayPropertyTypeId4 = parseSettingValueAsInteger(settingTypeMap.get(DISPLAY_PROPERTY_TYPE_ID_4_SETTING_TYPE_KEY).getDefaultValue());
        displayPropertyTypeId5 = parseSettingValueAsInteger(settingTypeMap.get(DISPLAY_PROPERTY_TYPE_ID_5_SETTING_TYPE_KEY).getDefaultValue());

        machineTagDisplay = Boolean.valueOf(settingTypeMap.get(DISPLAY_MACHINE_TAG_SETTING_TYPE_KEY).getDefaultValue());
        functionTagDisplay = Boolean.valueOf(settingTypeMap.get(DISPLAY_FUNCTION_TAG_SETTING_TYPE_KEY).getDefaultValue());
        deploymentStatusDisplay = Boolean.valueOf(settingTypeMap.get(DISPLAY_DEPLOYMENT_STATUS_SETTING_TYPE_KEY).getDefaultValue());
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        super.updateSettingsFromSessionSettingEntity(settingEntity);

        displayItemIdentifier1 = settingEntity.getSettingValueAsBoolean(DISPLAY_ALTERNATE_NAME_SETTING_TYPE_KEY, displayItemIdentifier1);
        displayDescription = settingEntity.getSettingValueAsBoolean(DISPLAY_DESIGN_DESCRIPTION_SETTING_TYPE_KEY, displayDescription);
        displayItemProject = settingEntity.getSettingValueAsBoolean(DISPLAY_PROJECT_SETTING_TYPE_KEY, displayItemProject);

        displayId = settingEntity.getSettingValueAsBoolean(DISPLAY_ID_SETTING_TYPE_KEY, displayId);
        displayQrId = settingEntity.getSettingValueAsBoolean(DISPLAY_QR_ID_SETTING_TYPE_KEY, displayQrId);
        displayOwnerUser = settingEntity.getSettingValueAsBoolean(DISPLAY_OWNER_USER_SETTING_TYPE_KEY, displayOwnerUser);
        displayOwnerGroup = settingEntity.getSettingValueAsBoolean(DISPLAY_OWNER_GROUP_SETTING_TYPE_KEY, displayOwnerGroup);
        displayCreatedByUser = settingEntity.getSettingValueAsBoolean(DISPLAY_CREATED_BY_USER_SETTING_TYPE_KEY, displayCreatedByUser);
        displayCreatedOnDateTime = settingEntity.getSettingValueAsBoolean(DISPLAY_CREATED_ON_DATE_TIME_SETTING_TYPE_KEY, displayCreatedOnDateTime);
        displayLastModifiedByUser = settingEntity.getSettingValueAsBoolean(DISPLAY_LAST_MODIFIED_BY_USER_SETTING_TYPE_KEY, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = settingEntity.getSettingValueAsBoolean(DISPLAY_LAST_MODIFIED_ON_DATE_TIME_SETTING_TYPE_KEY, displayLastModifiedOnDateTime);

        displayPropertyTypeId1 = settingEntity.getSettingValueAsInteger(DISPLAY_PROPERTY_TYPE_ID_1_SETTING_TYPE_KEY, displayPropertyTypeId1);
        displayPropertyTypeId2 = settingEntity.getSettingValueAsInteger(DISPLAY_PROPERTY_TYPE_ID_2_SETTING_TYPE_KEY, displayPropertyTypeId2);
        displayPropertyTypeId3 = settingEntity.getSettingValueAsInteger(DISPLAY_PROPERTY_TYPE_ID_3_SETTING_TYPE_KEY, displayPropertyTypeId3);
        displayPropertyTypeId4 = settingEntity.getSettingValueAsInteger(DISPLAY_PROPERTY_TYPE_ID_4_SETTING_TYPE_KEY, displayPropertyTypeId4);
        displayPropertyTypeId5 = settingEntity.getSettingValueAsInteger(DISPLAY_PROPERTY_TYPE_ID_5_SETTING_TYPE_KEY, displayPropertyTypeId5);

        machineTagDisplay = settingEntity.getSettingValueAsBoolean(DISPLAY_MACHINE_TAG_SETTING_TYPE_KEY, machineTagDisplay);
        functionTagDisplay = settingEntity.getSettingValueAsBoolean(DISPLAY_FUNCTION_TAG_SETTING_TYPE_KEY, functionTagDisplay);
        deploymentStatusDisplay = settingEntity.getSettingValueAsBoolean(DISPLAY_DEPLOYMENT_STATUS_SETTING_TYPE_KEY, deploymentStatusDisplay);
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        super.saveSettingsForSessionSettingEntity(settingEntity);

        settingEntity.setSettingValue(DISPLAY_ALTERNATE_NAME_SETTING_TYPE_KEY, displayItemIdentifier1);
        settingEntity.setSettingValue(DISPLAY_DESIGN_DESCRIPTION_SETTING_TYPE_KEY, displayDescription);
        settingEntity.setSettingValue(DISPLAY_PROJECT_SETTING_TYPE_KEY, displayItemProject);

        settingEntity.setSettingValue(DISPLAY_ID_SETTING_TYPE_KEY, displayId);
        settingEntity.setSettingValue(DISPLAY_QR_ID_SETTING_TYPE_KEY, displayQrId);
        settingEntity.setSettingValue(DISPLAY_OWNER_USER_SETTING_TYPE_KEY, displayOwnerUser);
        settingEntity.setSettingValue(DISPLAY_OWNER_GROUP_SETTING_TYPE_KEY, displayOwnerGroup);
        settingEntity.setSettingValue(DISPLAY_CREATED_BY_USER_SETTING_TYPE_KEY, displayCreatedByUser);
        settingEntity.setSettingValue(DISPLAY_CREATED_ON_DATE_TIME_SETTING_TYPE_KEY, displayCreatedOnDateTime);
        settingEntity.setSettingValue(DISPLAY_LAST_MODIFIED_BY_USER_SETTING_TYPE_KEY, displayLastModifiedByUser);
        settingEntity.setSettingValue(DISPLAY_LAST_MODIFIED_ON_DATE_TIME_SETTING_TYPE_KEY, displayLastModifiedOnDateTime);

        settingEntity.setSettingValue(DISPLAY_PROPERTY_TYPE_ID_1_SETTING_TYPE_KEY, displayPropertyTypeId1);
        settingEntity.setSettingValue(DISPLAY_PROPERTY_TYPE_ID_2_SETTING_TYPE_KEY, displayPropertyTypeId2);
        settingEntity.setSettingValue(DISPLAY_PROPERTY_TYPE_ID_3_SETTING_TYPE_KEY, displayPropertyTypeId3);
        settingEntity.setSettingValue(DISPLAY_PROPERTY_TYPE_ID_4_SETTING_TYPE_KEY, displayPropertyTypeId4);
        settingEntity.setSettingValue(DISPLAY_PROPERTY_TYPE_ID_5_SETTING_TYPE_KEY, displayPropertyTypeId5);

        settingEntity.setSettingValue(DISPLAY_MACHINE_TAG_SETTING_TYPE_KEY, machineTagDisplay);
        settingEntity.setSettingValue(DISPLAY_FUNCTION_TAG_SETTING_TYPE_KEY, functionTagDisplay);
        settingEntity.setSettingValue(DISPLAY_DEPLOYMENT_STATUS_SETTING_TYPE_KEY, deploymentStatusDisplay);
    }

    public boolean isMachineTagDisplay() {
        return machineTagDisplay;
    }

    public void setMachineTagDisplay(boolean machineTagDisplay) {
        this.machineTagDisplay = machineTagDisplay;
    }

    public boolean isFunctionTagDisplay() {
        return functionTagDisplay;
    }

    public void setFunctionTagDisplay(boolean functionTagDisplay) {
        this.functionTagDisplay = functionTagDisplay;
    }

    public String getMachineTagFilter() {
        return machineTagFilter;
    }

    public void setMachineTagFilter(String machineTagFilter) {
        this.machineTagFilter = machineTagFilter;
    }

    public String getFunctionTagFilter() {
        return functionTagFilter;
    }

    public void setFunctionTagFilter(String functionTagFilter) {
        this.functionTagFilter = functionTagFilter;
    }

    public boolean isDeploymentStatusDisplay() {
        return deploymentStatusDisplay;
    }

    public void setDeploymentStatusDisplay(boolean deploymentStatusDisplay) {
        this.deploymentStatusDisplay = deploymentStatusDisplay;
    }

    public String getDeploymentStatusFilter() {
        return deploymentStatusFilter;
    }

    public void setDeploymentStatusFilter(String deploymentStatusFilter) {
        this.deploymentStatusFilter = deploymentStatusFilter;
    }

}
