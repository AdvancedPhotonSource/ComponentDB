/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.view.jsf.utilities;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.primefaces.component.selectonemenu.SelectOneMenu;

/**
 * Utility class for assisting with common java server faces tasks. 
 */
@Named("facesUtility")
@RequestScoped
public class FacesUtility {
    
    private SelectOneMenu selectOneMenu; 

    public SelectOneMenu getSelectOneMenu() {
        return selectOneMenu;
    }

    public void setSelectOneMenu(SelectOneMenu selectOneMenu) {
        this.selectOneMenu = selectOneMenu;
    }
    
    public void setSelectOneMenuValue(Object value) {
        selectOneMenu.setSubmittedValue(value);
    }
    
}
