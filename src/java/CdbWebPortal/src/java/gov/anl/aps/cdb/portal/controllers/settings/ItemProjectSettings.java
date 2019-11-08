/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author djarosz
 */
public class ItemProjectSettings extends CdbEntitySettingsBase<ItemProjectController> {
    
    private static final String SystemItemProjectIdSettingTypeKey = "ItemProject.System.ItemProjectId";
    
    private Integer systemItemProjectId = null; 

    public ItemProjectSettings(ItemProjectController parentController) {
        super(parentController);
        displayDescription = true; 
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        systemItemProjectId = parseSettingValueAsInteger(settingTypeMap.get(SystemItemProjectIdSettingTypeKey).getDefaultValue());
        
        parentController.updateCurrentItemProjectFromSetting();
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        systemItemProjectId = settingEntity.getSettingValueAsInteger(SystemItemProjectIdSettingTypeKey, systemItemProjectId);
        
        parentController.updateCurrentItemProjectFromSetting();
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        settingEntity.setSettingValue(SystemItemProjectIdSettingTypeKey, systemItemProjectId);
    }

    public Integer getSystemItemProjectId() {
        return systemItemProjectId;
    }

    public void setSystemItemProjectId(Integer systemItemProjectId) {
        this.systemItemProjectId = systemItemProjectId;
    }
    
}
