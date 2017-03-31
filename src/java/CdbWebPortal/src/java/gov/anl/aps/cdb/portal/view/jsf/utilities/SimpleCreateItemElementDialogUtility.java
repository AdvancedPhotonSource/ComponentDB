/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.jsf.utilities;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.primefaces.component.panel.Panel;

/**
 * Utility class for assisting with common java server faces tasks.
 */
@Named("simpleCreateItemElementDialogUtility")
@RequestScoped
public class SimpleCreateItemElementDialogUtility {
    
    Panel selectItemPanel; 
    Panel selectOptionsPanel; 

    public Panel getSelectItemPanel() {
        return selectItemPanel;
    }

    public void setSelectItemPanel(Panel selectItemPanel) {
        this.selectItemPanel = selectItemPanel;
    }

    public Panel getSelectOptionsPanel() {
        return selectOptionsPanel;
    }

    public void setSelectOptionsPanel(Panel selectOptionsPanel) {
        this.selectOptionsPanel = selectOptionsPanel;
    }
    
    public void resetToggleConfigurationToStep1Defaults() {
        if (isReady()) {            
            selectItemPanel.setCollapsed(false);
            selectOptionsPanel.setCollapsed(true);
        }
    }
    
    public void resetToggleConfigurationToStep2Defaults() {
        if (isReady()) {            
            selectItemPanel.setCollapsed(true);
            selectOptionsPanel.setCollapsed(false);
        }
    }   
    
    private boolean isReady() {
        return selectItemPanel != null && selectOptionsPanel != null; 
    }
}
