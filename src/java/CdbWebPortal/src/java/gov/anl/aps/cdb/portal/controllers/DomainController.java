/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.DomainSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.DomainControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("domainController")
@SessionScoped
public class DomainController extends CdbEntityController<DomainControllerUtility, Domain, DomainFacade, DomainSettings> implements Serializable {

    @EJB
    DomainFacade domainFacade; 
    
    public Domain createDomainWithName(String domainName){
        Domain domain = createEntityInstance();
        domain.setName(domainName);
        setCurrent(domain);
        create(); 
        return domain; 
    }
    
    @Override
    protected DomainFacade getEntityDbFacade() {
        return domainFacade;
    }

    @Override
    protected DomainSettings createNewSettingObject() {
        return new DomainSettings(); 
    }

    @Override
    protected DomainControllerUtility createControllerUtilityInstance() {
        return new DomainControllerUtility();
    }
    
    @FacesConverter(value = "domainConverter", forClass = Domain.class)
    public static class DomainControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DomainController controller = (DomainController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "domainController");
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
            if (object instanceof Domain) {
                Domain o = (Domain) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Domain.class.getName());
            }
        }

    }
}
