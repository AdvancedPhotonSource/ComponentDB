/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemSourceSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemSourceControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.ItemSource;
import gov.anl.aps.cdb.portal.model.db.beans.ItemSourceFacade;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named("itemSourceController")
@SessionScoped
public class ItemSourceController extends CdbEntityController<ItemSourceControllerUtility, ItemSource, ItemSourceFacade, ItemSourceSettings> implements Serializable {
    

    @EJB
    private ItemSourceFacade itemSourceFacade;
    private static final Logger logger = LogManager.getLogger(ItemSourceController.class.getName());   

    public ItemSourceController() {
    }

    @Override
    protected ItemSourceFacade getEntityDbFacade() {
        return itemSourceFacade;
    }   

    /*
    @Override
    public List<ComponentSource> getAvailableItems() {
        return super.getAvailableItems();
    }

    public DataModel getListDataModelByComponentId(Integer componentId) {
        return new ListDataModel(itemSourceFacade.findAllByComponentId(componentId));
    }

    public List<ComponentSource> findAllByComponentId(Integer componentId) {
        return itemSourceFacade.findAllByComponentId(componentId);
    }
    */
       
    @Override
    protected ItemSourceSettings createNewSettingObject() {
        return new ItemSourceSettings(this);
    }

    @Override
    protected ItemSourceControllerUtility createControllerUtilityInstance() {
        return new ItemSourceControllerUtility(); 
    }

    /**
     * Converter class for component source objects.
     */
    @FacesConverter(forClass = ItemSource.class)
    public static class ComponentSourceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                ItemSourceController controller = (ItemSourceController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "componentSourceController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to component source object.");
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
            if (object instanceof ItemSource) {
                ItemSource o = (ItemSource) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ItemSource.class.getName());
            }
        }

    }
}
