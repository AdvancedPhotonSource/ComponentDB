/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.io.Serializable;

/**
 * This base class defines default methods for entities that hold setting information. 
 * @author djarosz
 */
public abstract class EntitySetting extends CdbEntity {
       
    public abstract SettingType getSettingType(); 
    
    public abstract void setSettingType(SettingType settingType); 
    
    public abstract void setSettingEntity(SettingEntity settingEntity);
    
    public abstract String getValue(); 
    
    public abstract void setValue(String value); 

}
