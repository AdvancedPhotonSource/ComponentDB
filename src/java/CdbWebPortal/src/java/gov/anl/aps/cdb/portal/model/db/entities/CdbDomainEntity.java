/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.db.entities;

import java.util.List;

/**
 *
 * @author djarosz
 */
public abstract class CdbDomainEntity extends CdbEntity {
    
    private transient List<PropertyValue> imagePropertyList = null;
    
    public List<PropertyValue> getPropertyValueList(){
        return null; 
    }
    
    public List<Log> getLogList() {
        return null;
    }
    
    public List<PropertyValue> getImagePropertyList() {
        return imagePropertyList;
    }

    public void setImagePropertyList(List<PropertyValue> imagePropertyList) {
        this.imagePropertyList = imagePropertyList;
    }

    public void resetImagePropertyList() {
        this.imagePropertyList = null;
    }
}
