/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament;

import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@SessionScoped
@Named("docManagamentBean")
public class DocManagamentBean implements Serializable {
    
    private String currentIFrameSrc = null;
    
    public static DocManagamentBean getInstance() {
        return (DocManagamentBean) SessionUtility.findBean("docManagamentBean");
    }
    
    protected void updateCurrentIFrameSrc(String iframeUrl, String value) {
        currentIFrameSrc = DocManagerPlugin.getContextRootUrlProperty() + iframeUrl + value; 
    }    

    public String getCurrentIFrameSrc() {
        return currentIFrameSrc;
    }
    
}
