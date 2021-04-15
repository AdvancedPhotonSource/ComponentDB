/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.PropertyTypeHandlerSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.PropertyTypeHandlerControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeHandlerFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.plugins.CdbPluginManager;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named("propertyTypeHandlerController")
@SessionScoped
public class PropertyTypeHandlerController extends CdbEntityController<PropertyTypeHandlerControllerUtility, PropertyTypeHandler, PropertyTypeHandlerFacade, PropertyTypeHandlerSettings> implements Serializable {

    private static final Logger logger = LogManager.getLogger(PropertyTypeHandlerController.class.getName());
    
    private CdbPluginManager cdbPluginManager; 
    
    private List<PropertyTypeHandler> allPossiblePropertyTypeHandlersForNewPropertyType = null; 

    @EJB
    private PropertyTypeHandlerFacade propertyTypeHandlerFacade;

    public PropertyTypeHandlerController() {
    }

    @Override
    protected PropertyTypeHandlerFacade getEntityDbFacade() {
        return propertyTypeHandlerFacade;
    }

    @Override
    public List<PropertyTypeHandler> getAvailableItems() {
        return super.getAvailableItems();
    }
    
    public List<PropertyTypeHandler> getAllPossiblePropertyTypeHandlersForNewPropertyType() {
        List<PropertyTypeHandler> availableItems = getAvailableItems();
        if (cdbPluginManager == null) {
            cdbPluginManager = CdbPluginManager.getInstance();
        }
        
        
        // Add property type handlers that are plugins that do not yet have a db record. 
        List<String> allPluginPropertyTypeHandlerNames = cdbPluginManager.getAllPluginPropertyTypeHandlerNames();
        
        for (PropertyTypeHandler propertyTypeHandler : availableItems) {             
            for (String pluginPropertyTypeHandlerName : allPluginPropertyTypeHandlerNames) {
                if (propertyTypeHandler.getName().equals(pluginPropertyTypeHandlerName)) {
                    allPluginPropertyTypeHandlerNames.remove(pluginPropertyTypeHandlerName); 
                    break; 
                }
            }
        }
        
        if (allPluginPropertyTypeHandlerNames.size() > 0) {
            for (int i = 0; i < allPluginPropertyTypeHandlerNames.size(); i++) {
                String pluginPropertyTypeHanlder = allPluginPropertyTypeHandlerNames.get(i); 
                PropertyTypeHandler newHandler = createEntityInstance();            
                // Temporarly assigned to identifiy the list item. 
                newHandler.setId((i+1)*-1);
                newHandler.setName(pluginPropertyTypeHanlder);
                newHandler.setDescription("Automatically added for plugin");
                availableItems.add(newHandler); 
            }

            allPossiblePropertyTypeHandlersForNewPropertyType = availableItems; 
        }
                
        return availableItems;                         
    }
    
   public PropertyTypeHandler getPropertyTypeHandlerFromAllPossible(int id) {
       if (allPossiblePropertyTypeHandlersForNewPropertyType != null) {
        for (PropertyTypeHandler propertyTypeHandler : allPossiblePropertyTypeHandlersForNewPropertyType) {
            if (propertyTypeHandler.getId() == id) {                
                return propertyTypeHandler; 
            }
        }
       }
       return getEntity(id);
   }      

    @Override
    protected PropertyTypeHandlerSettings createNewSettingObject() {
        return new PropertyTypeHandlerSettings(this);
    }

    @Override
    protected PropertyTypeHandlerControllerUtility createControllerUtilityInstance() {
        return new PropertyTypeHandlerControllerUtility(); 
    }

    /**
     * Converter class for property handler objects.
     */    
    @FacesConverter(value = "propertyTypeHandlerConverter", forClass = PropertyTypeHandler.class)
    public static class PropertyHandlerControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                PropertyTypeHandlerController controller = (PropertyTypeHandlerController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "propertyTypeHandlerController");
                int id = getIntegerKey(value);
                return controller.getPropertyTypeHandlerFromAllPossible(id); 
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to property type handler object.");
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
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof PropertyTypeHandler) {
                PropertyTypeHandler o = (PropertyTypeHandler) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyTypeHandler.class.getName());
            }
        }

    }

}
