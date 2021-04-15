/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.EntityTypeSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.EntityTypeControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.beans.EntityTypeFacade;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

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
public class EntityTypeController extends CdbEntityController<EntityTypeControllerUtility, EntityType, EntityTypeFacade, EntityTypeSettings> implements Serializable {

    @EJB
    EntityTypeFacade entityTypeFacade;
    
    public static final String CONTROLLER_NAMED = "entityTypeController";

    public static EntityTypeController getInstance() {
        return (EntityTypeController) SessionUtility.findBean(EntityTypeController.CONTROLLER_NAMED);
    }
    
    @Override
    protected EntityTypeFacade getEntityDbFacade() {
        return entityTypeFacade; 
    }   

    @Override
    public EntityType findById(Integer id) {
        return entityTypeFacade.find(id);
    }    
    
    public EntityType findByName(String name) {
        return entityTypeFacade.findByName(name);
    }

    @Override
    protected EntityTypeSettings createNewSettingObject() {
        return new EntityTypeSettings();
    }

    @Override
    protected EntityTypeControllerUtility createControllerUtilityInstance() {
        return new EntityTypeControllerUtility();
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
