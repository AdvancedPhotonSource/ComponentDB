/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemElementRelationshipSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemElementRelationshipControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementRelationshipFacade;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named(ItemElementRelationshipController.controllerNamed)
@SessionScoped
public class ItemElementRelationshipController extends CdbEntityController<ItemElementRelationshipControllerUtility, ItemElementRelationship, ItemElementRelationshipFacade, ItemElementRelationshipSettings> implements Serializable {
    
    public final static String controllerNamed = "itemElementRelationshipController";
    
    @EJB
    ItemElementRelationshipFacade itemElementRelationshipFacade; 

  
    public ItemElementRelationshipController() {
    }
    
    public static ItemElementRelationshipController getInstance() {
        return (ItemElementRelationshipController) SessionUtility.findBean(controllerNamed);
    }

    @Override
    protected ItemElementRelationshipSettings createNewSettingObject() {
        return new ItemElementRelationshipSettings(this); 
    }

    @Override
    protected ItemElementRelationshipFacade getEntityDbFacade() {
        return itemElementRelationshipFacade; 
    }

    @Override
    protected ItemElementRelationshipControllerUtility createControllerUtilityInstance() {
        return new ItemElementRelationshipControllerUtility(); 
    }   

    @FacesConverter(forClass = ItemElementRelationship.class)
    public static class ItemElementRelationshipControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemElementRelationshipController controller = (ItemElementRelationshipController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemElementRelationshipController");
            return controller.getEntity(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof ItemElementRelationship) {
                ItemElementRelationship o = (ItemElementRelationship) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ItemElementRelationship.class.getName());
            }
        }

    }

}
