/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named("clientViewManagerController")
@SessionScoped
public class ClientViewManagerController implements Serializable {
        
    String viewUUID = null; 
    String lastGeneratedViewUUID = null; 
    CdbEntityController entityController = null;    
    String lastUrl = null; 
    
    public void generateNewViewUUID() {
        viewUUID = UUID.randomUUID().toString();        
        lastUrl = SessionUtility.getRedirectToCurrentView();
        lastGeneratedViewUUID = viewUUID; 
    }

    public String getViewUUID() {        
        return viewUUID;
    }

    public String getLastGeneratedViewUUID() {
        String tmp = lastGeneratedViewUUID; 
        lastGeneratedViewUUID = null;         
        return tmp;
    }            
    
    public String redrectToLastShownUrl() {
        return lastUrl; 
    }
    
    public int getCurrentTimeHash() {
        return LocalDateTime.now().hashCode(); 
    }
}
