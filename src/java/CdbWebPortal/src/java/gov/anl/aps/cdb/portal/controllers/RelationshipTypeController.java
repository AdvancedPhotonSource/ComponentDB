/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.RelationshipTypeSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.RelationshipTypeControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("relationshipTypeController")
@SessionScoped
public class RelationshipTypeController extends CdbEntityController<RelationshipTypeControllerUtility, RelationshipType, RelationshipTypeFacade, RelationshipTypeSettings> implements Serializable {

    @EJB
    RelationshipTypeFacade relationshipTypeFacade; 
    
    public static RelationshipTypeController getInstance() {
        return (RelationshipTypeController) SessionUtility.findBean("relationshipTypeController");
    }
    
    @Override
    protected RelationshipTypeFacade getEntityDbFacade() {
        return relationshipTypeFacade;
    }

    @Override
    protected RelationshipTypeSettings createNewSettingObject() {
        return new RelationshipTypeSettings();
    }

    @Override
    protected RelationshipTypeControllerUtility createControllerUtilityInstance() {
        return new RelationshipTypeControllerUtility(); 
    }

    @FacesConverter(forClass = RelationshipType.class)
    public static class RelationshipTypeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RelationshipTypeController controller = (RelationshipTypeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "relationshipTypeController");
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
            if (object instanceof RelationshipType) {
                RelationshipType o = (RelationshipType) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + RelationshipType.class.getName());
            }
        }

    }

}
