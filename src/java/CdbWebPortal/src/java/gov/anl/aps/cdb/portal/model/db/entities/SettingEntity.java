/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.ArrayList;
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

    @JsonIgnore
    public abstract List<EntitySetting> getSettingList();
    
    public abstract void setSettingList(List<EntitySetting> entitySettingList); 
        
    public abstract EntitySetting createNewEntitySetting();
    
    public void populateDefaultSettingList(List<SettingType> settingTypeList) {
        List<EntitySetting> settingList = getSettingList();         
        if (settingList == null) {
            settingList = new ArrayList(); 
        }
        
        if (settingList.size() < settingTypeList.size()) {
            boolean settingListUpdate = !settingList.isEmpty(); 
            
            for (SettingType settingType: settingTypeList) {
                if (settingListUpdate) {
                    if (isSettingTypeInEntitySettingList(settingList, settingType)) {
                        continue;
                    }
                }
                EntitySetting entitySetting = createNewEntitySetting(); 
                entitySetting.setSettingEntity(this);
                entitySetting.setSettingType(settingType);
                entitySetting.setValue(settingType.getDefaultValue());
                settingList.add(entitySetting);                                 
            }
            
            setSettingList(settingList);
        }
    };
    
    private static boolean isSettingTypeInEntitySettingList(List<EntitySetting> entitySettingList, SettingType settingType) {
        for (EntitySetting entitySetting : entitySettingList) {
            if (entitySetting.getSettingType().equals(settingType)) {
                return true;
            }
        }
        return false; 
    }
    
    public abstract List<ListTbl> getItemElementLists();

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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonIgnore
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
        String value = entitySetting.getValue();
        if (value.equals("")) {
            value = null; 
        }
        return value;
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

    @Override
    public Integer getId() {
        return null; 
    }

}
