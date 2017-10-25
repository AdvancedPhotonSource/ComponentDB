/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@SessionScoped
@Named("docManagamentBean")
public class DocManagamentBean implements Serializable {
    
    private String currentIFrameSrc = null;
    private PropertyValue currentPropertyValue; 
    
    public static DocManagamentBean getInstance() {
        return (DocManagamentBean) SessionUtility.findBean("docManagamentBean");
    }
    
    public String getDialogHeaderText() {
        String result = "";
        if (currentPropertyValue != null) {
            switch(currentPropertyValue.getPropertyType().getPropertyTypeHandler().getName()) {
                case (DocManagamentCollectionPropertyTypeHandler.HANDLER_NAME):
                    result += "DMS Collection: ";
                    break;
                case (DocManagamentContainerPropertyTypeHandler.HANDLER_NAME):
                    result += "DMS Container: ";
                    break;                
            }            
            result += currentPropertyValue.getDisplayValue(); 
        }
        return result;  
    }
    
    protected void updateCurrentIFrameDialog(String iframeUrl, PropertyValue value) {
        currentPropertyValue = value;
        currentIFrameSrc = DocManagerPlugin.getContextRootUrlProperty() + iframeUrl + value.getValue(); 
    }    

    public String getCurrentIFrameSrc() {
        return currentIFrameSrc;
    }
    
}
