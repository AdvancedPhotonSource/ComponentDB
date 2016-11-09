/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Base class for all CDB entities.
 */
public class CdbEntity implements Serializable, Cloneable {
    
    private transient String viewUUID;
    protected static final long serialVersionUID = 1L;
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public Object getId() {
        return null;
    }
    
    public Object getEntityInfo() {
        return null;
    }
    
    public SearchResult search(Pattern searchPattern) {
        return null;
    }
    
    
    public String getViewUUID() {
        if (viewUUID == null) {
            viewUUID = UUID.randomUUID().toString().replaceAll("[-]", "");
        }
        return viewUUID;
    }
    
}
