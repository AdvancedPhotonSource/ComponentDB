/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.UserRoleController;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import java.util.Map;

/**
 *
 * @author djarosz
 */
public class UserRoleSettings extends CdbEntitySettingsBase<UserRoleController> {

    public UserRoleSettings(UserRoleController parentController) {
        super(parentController);
        displayNumberOfItemsPerPage = 25; 
    }

    @Override
    protected void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        // No user role settings to load or save.
    }

    @Override
    protected void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        // No user role settings to load or save.
    }

    @Override
    protected void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        // No user role settings to load or save.
    }
    
    
}
