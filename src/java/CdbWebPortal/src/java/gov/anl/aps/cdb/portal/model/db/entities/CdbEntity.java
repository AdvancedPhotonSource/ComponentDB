/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Base class for all CDB entities.
 */
public class CdbEntity implements Serializable, Cloneable {
    
    @JsonIgnore
    private transient String viewUUID;
    
    @JsonIgnore
    private transient String persitanceErrorMessage = null; 
    
    private transient Map<String, String> apiProperties;
    
    // import wizard variables
    private transient boolean isValidImport = true;
    private transient boolean isDuplicateImport = false;
    private transient String validStringImport;
    private transient Integer importExistingItemId;
    private transient Boolean importDeleteExistingItem;
    
    protected static final long serialVersionUID = 1L;
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public Object getId() {
        return null;
    }
    
    @JsonIgnore
    public Object getEntityInfo() {
        return null;
    }
    
    public SearchResult search(Pattern searchPattern) {
        return null;
    }        
    
    @JsonIgnore
    public String getViewUUID() {
        if (viewUUID == null) {
            viewUUID = UUID.randomUUID().toString().replaceAll("[-]", "");
        }
        return viewUUID;
    }

    public String getPersitanceErrorMessage() {
        return persitanceErrorMessage;
    }

    public void setPersitanceErrorMessage(String persitanceErrorMessage) {
        this.persitanceErrorMessage = persitanceErrorMessage;
    }

    @JsonAnySetter
    public void addJsonProperty(String key, String value) {
        if (apiProperties == null) {
            apiProperties = new HashMap<>(); 
        }
        apiProperties.put(key, value);
    }
    
    @JsonIgnore
    public String getSystemLogString() {
        return toString(); 
    }
    
    @JsonIgnore
    public boolean getIsValidImport() {
        return isValidImport;
    }
    
    @JsonIgnore
    public String getIsValidImportString() {
        if (isValidImport) {
            return "yes";
        } else {
            return "no";
        }
    }
    
    public void setIsValidImport(boolean b) {
        isValidImport = b;
    }
    
    
    @JsonIgnore
    public boolean getIsDuplicateImport() {
        return isDuplicateImport;
    }
    
    @JsonIgnore
    public String getIsDuplicateImportString() {
        if (isDuplicateImport) {
            return "yes";
        } else {
            return "no";
        }
    }
    
    public void setIsDuplicateImport(boolean b) {
        isDuplicateImport = b;
    }
    
    @JsonIgnore
    public String getValidStringImport() {
        return validStringImport;
    }
    
    public void setValidStringImport(String s) {
        validStringImport = s;
    }
    
    public void setImportExistingItemId(Integer id) {
        importExistingItemId = id;
    }
    
    @JsonIgnore
    public Integer getImportExistingItemId() {
        return importExistingItemId;
    }
    
    public void setImportDeleteExistingItem(Boolean b) {
        importDeleteExistingItem = b;
    }
    
    @JsonIgnore
    public Boolean getImportDeleteExistingItem() {
        return importDeleteExistingItem;
    }
    
}
