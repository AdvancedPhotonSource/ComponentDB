/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

/**
 *
 * @author darek
 */
public enum SystemLogLevel {
    
    entityInfo("cdbEntityInfo"),
    entityWarning("cdbEntityWarning"),
    loginInfo("loginInfo"),
    loginWarning("loginWarning"); 
    
    private String logLevelName; 

    private SystemLogLevel(String logLevelName) {
        this.logLevelName = logLevelName;
    }

    @Override
    public String toString() {
        return logLevelName; 
    }
}
