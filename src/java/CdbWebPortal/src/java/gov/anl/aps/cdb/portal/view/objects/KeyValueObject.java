/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

/**
 *
 * @author djarosz
 */
public class KeyValueObject {
    
    String key;
    String value; 

    public KeyValueObject(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }    
    
}
