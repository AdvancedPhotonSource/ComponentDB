/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;


/**
 * Class overrides the session utility for test purposes. 
 * 
 * @author djarosz
 */
public class SessionUtility {    
    
    private static UserInfo userInfo;
    
    public static Object getUser() {                
        return userInfo;
    }
    
}
