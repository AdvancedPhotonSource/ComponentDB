/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.beans.ItemProjectFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;
import org.jboss.weld.util.collections.ArraySet;

@Named("itemProjectController")
@SessionScoped
public class ItemProjectController extends CdbEntityController<ItemProject, ItemProjectFacade> implements Serializable {

    @EJB
    ItemProjectFacade itemProjectFacade; 
    
    private static final Logger logger = Logger.getLogger(ItemProjectController.class.getName());
    
    private static final String SystemItemProjectIdSettingTypeKey = "ItemProject.System.ItemProjectId";
    
    private Integer systemItemProjectId = null; 
    
    private Set<IItemController> itemProjectChangeListeners = null;  
    
    // Current item project is used to determine what project receives precedence on pages. 
    private ItemProject currentItemProject = null; 
    
    public static ItemProjectController getInstance() {
        return (ItemProjectController) SessionUtility.findBean("itemProjectController"); 
    }
    
    public ItemProjectController() {
        super();
        displayDescription = true; 
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Item Project"; 
    }

    @Override
    public String getEntityTypeName() {
        return "itemProject";
    }
    
    @Override
    protected ItemProjectFacade getEntityDbFacade() {
        return itemProjectFacade;
    }

    @Override
    protected ItemProject createEntityInstance() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }
    
    public void addItemControllerProjectChangeListener(IItemController itemDomainView) {
        if (itemProjectChangeListeners == null) {
            itemProjectChangeListeners = new ArraySet<>();
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
                systemItemProjectId = currentItemProject.getId();
            } else {
                systemItemProjectId = null; 
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
    
    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }
        
        systemItemProjectId = parseSettingValueAsInteger(settingTypeMap.get(SystemItemProjectIdSettingTypeKey).getDefaultValue());
        updateCurrentItemProjectFromSetting();
    }
    
    @Override
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }
        
        systemItemProjectId = settingEntity.getSettingValueAsInteger(SystemItemProjectIdSettingTypeKey, systemItemProjectId);
        updateCurrentItemProjectFromSetting();
    }
    
    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }
        
        settingEntity.setSettingValue(SystemItemProjectIdSettingTypeKey, systemItemProjectId);
    }
    
    private void updateCurrentItemProjectFromSetting() {
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
    
    /**
     * Converter class for component project objects.
     */
    @FacesConverter(value = "itemProjectConverter", forClass = ItemProject.class)
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
            if (object instanceof ItemProject) {
                ItemProject o = (ItemProject) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of project " + object.getClass().getName() + "; expected project: " + ItemProject.class.getName());
            }
        }

    }

}
