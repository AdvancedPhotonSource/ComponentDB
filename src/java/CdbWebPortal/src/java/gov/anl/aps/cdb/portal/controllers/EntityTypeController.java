/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.beans.EntityTypeFacade;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("entityTypeController")
@SessionScoped
public class EntityTypeController extends CdbEntityController<EntityType, EntityTypeFacade>implements Serializable {

    @EJB
    EntityTypeFacade entityTypeFacade;
    
    @Override
    protected EntityTypeFacade getEntityDbFacade() {
        return entityTypeFacade; 
    }

    @Override
    protected EntityType createEntityInstance() {
        EntityType entityType = new EntityType(); 
        return entityType; 
    }

    @Override
    public String getEntityTypeName() {
        return "entityType"; 
    }

    @Override
    public String getCurrentEntityInstanceName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EntityType findById(Integer id) {
        return entityTypeFacade.find(id);
    }    

    @FacesConverter(value = "entityTypeConverter", forClass = EntityType.class)
    public static class EntityTypeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EntityTypeController controller = (EntityTypeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "entityTypeController");
            return controller.findById(getKey(value));
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
            if (object instanceof EntityType) {
                EntityType o = (EntityType) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + EntityType.class.getName());
            }
        }

    }

}
