/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * This base class defines default methods for an entity that may have CDB
 * settings.
 *
 * @author djarosz
 */
public abstract class SettingEntity extends CdbEntity implements Serializable {

    protected transient HashMap<String, EntitySetting> entitySettingMap = null;
    private transient Date userSettingsModificationDate = null;

    public abstract List<EntitySetting> getSettingList();
    
    public abstract void populateDefaultSettingList(List<SettingType> settingTypeList);

    protected void createSettingMap() {
        entitySettingMap = new HashMap<>();
        for (EntitySetting setting : getSettingList()) {
            entitySettingMap.put(setting.getSettingType().getName(), setting);
        }
        updateSettingsModificationDate();
    }

    public void setSetting(String name, EntitySetting entitySetting) {
        if (entitySettingMap == null) {
            createSettingMap();
        }
        UserSetting oldUserSetting = (UserSetting) entitySettingMap.get(name);
        if (oldUserSetting != null) {
            oldUserSetting.setValue(entitySetting.getValue());
        } else {
            entitySettingMap.put(name, entitySetting);
        }
        updateSettingsModificationDate();
    }

    public void setSettingValue(String name, Object value) {
        EntitySetting entitySetting = getSetting(name);
        if (entitySetting != null && value != null) {
            entitySetting.setValue(value.toString());
        } else if (entitySetting != null) {
            entitySetting.setValue("");
        }
    }

    public void updateSettingsModificationDate() {
        userSettingsModificationDate = new Date();
    }

    public Date getSettingsModificationDate() {
        if (userSettingsModificationDate == null) {
            updateSettingsModificationDate();
        }
        return userSettingsModificationDate;
    }

    public boolean areSettingsModifiedAfterDate(Date date) {
        return date == null || userSettingsModificationDate == null || userSettingsModificationDate.after(date);
    }

    public EntitySetting getSetting(String name) {
        if (entitySettingMap == null) {
            createSettingMap();
        }
        return entitySettingMap.get(name);
    }

    public String getSettingValueAsString(String name, String defaultValue) {
        EntitySetting entitySetting = getSetting(name);
        if (entitySetting == null) {
            return defaultValue;
        }
        return entitySetting.getValue();
    }

    public Boolean getSettingValueAsBoolean(String name, Boolean defaultValue) {
        EntitySetting entitySetting = getSetting(name);
        if (entitySetting == null) {
            return defaultValue;
        }
        String settingValue = entitySetting.getValue();
        if (settingValue == null || settingValue.isEmpty()) {
            return false;
        }
        return Boolean.parseBoolean(settingValue);
    }

    public Integer getSettingValueAsInteger(String name, Integer defaultValue) {
        EntitySetting entitySetting = getSetting(name);
        if (entitySetting == null) {
            return defaultValue;
        }
        String settingValue = entitySetting.getValue();
        if (settingValue == null || settingValue.isEmpty()) {
            return null;
        }
        return Integer.parseInt(settingValue);
    }

    public Float getSettingValueAsFloat(String name, Float defaultValue) {
        EntitySetting entitySetting = getSetting(name);
        if (entitySetting == null) {
            return defaultValue;
        }
        String settingValue = entitySetting.getValue();
        if (settingValue == null || settingValue.isEmpty()) {
            return null;
        }
        return Float.parseFloat(settingValue);
    }

    public boolean hasSettings() {
        return getSettingList() != null && !getSettingList().isEmpty();
    }

}
