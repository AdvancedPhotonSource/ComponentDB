/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemProjectSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemProjectControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemProjectFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named("itemProjectController")
@SessionScoped
public class ItemProjectController extends CdbEntityController<ItemProjectControllerUtility, ItemProject, ItemProjectFacade, ItemProjectSettings> implements Serializable {

    @EJB
    ItemProjectFacade itemProjectFacade; 
    
    private static final Logger logger = LogManager.getLogger(ItemProjectController.class.getName());        
    
    private Set<IItemController> itemProjectChangeListeners = null;  
    
    // Current item project is used to determine what project receives precedence on pages. 
    private ItemProject currentItemProject = null; 
    
    public static ItemProjectController getInstance() {
        return (ItemProjectController) SessionUtility.findBean("itemProjectController"); 
    }
    
    public ItemProjectController() {
        super();        
    }
    
    @Override
    protected ItemProjectFacade getEntityDbFacade() {
        return itemProjectFacade;
    }
    
    public void addItemControllerProjectChangeListener(IItemController itemDomainView) {
        if (itemProjectChangeListeners == null) {
            itemProjectChangeListeners = new HashSet<>();
        }
        itemProjectChangeListeners.add(itemDomainView); 
        
    }
    
    public static ItemProject getSelectedItemProject() {
        return getInstance().getCurrentItemProject(); 
    }
    
    public ItemProject getCurrentItemProject() {
        return currentItemProject;
    }

    public void setCurrentItemProject(ItemProject currentItemProject) {
        if (!Objects.equals(this.currentItemProject, currentItemProject)) {
            this.currentItemProject = currentItemProject;
            if (currentItemProject != null) {
                settingObject.setSystemItemProjectId(currentItemProject.getId());
            } else {
                settingObject.setSystemItemProjectId(null);
            }
            notifyItemProjectChangeListeners();            
        }
    }
    
    private void notifyItemProjectChangeListeners() {
        for (IItemController iItemController : itemProjectChangeListeners) {
            iItemController.itemProjectChanged();
        }
    }
    
    public String getCurrentItemProjectLabel() {
        if (currentItemProject != null) {
            return currentItemProject.getName(); 
        }
        return "All"; 
    }

    public void updateCurrentItemProjectFromSetting() {
        Integer systemItemProjectId = settingObject.getSystemItemProjectId();
        if (systemItemProjectId == null) {
            currentItemProject = null; 
        } else {
            if (currentItemProject != null) {
                if (Objects.equals(currentItemProject.getId(), systemItemProjectId)) {
                    return; 
                }                
            }
            setCurrentItemProject(findById(systemItemProjectId)); 
        }
    }

    @Override
    protected ItemProjectSettings createNewSettingObject() {
        return new ItemProjectSettings(this);
    }

    @Override
    protected ItemProjectControllerUtility createControllerUtilityInstance() {
        return new ItemProjectControllerUtility(); 
    }
    
    /**
     * Converter class for component project objects.
     */
    @FacesConverter(value = "itemProjectConverter")
    public static class ItemProjectControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
            if (value == null || value.length() == 0 || value.equals("Select")) {
                return null;
            }
            try {
                ItemProjectController controller = (ItemProjectController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "itemProjectController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to component project object.");
                return null;
            }
        }

        Integer getIntegerKey(String value) {
            return Integer.valueOf(value);
        }

        String getStringKey(Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof String) {
                return (String) object;
            }
            
            if (object instanceof ItemProject) {
                ItemProject o = (ItemProject) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of project " + object.getClass().getName() + "; expected project: " + ItemProject.class.getName());
            }
        }

    }

}
