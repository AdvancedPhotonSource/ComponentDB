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
        this.value = "";
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

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof KeyValueObject) {
                KeyValueObject object = (KeyValueObject) obj; 
                return this.key.equals(object.key) && this.value.equals(object.value); 
            }
            
        }
        return false;
    }

}
