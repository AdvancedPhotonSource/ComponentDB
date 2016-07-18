package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.DomainHandler;
import gov.anl.aps.cdb.portal.model.db.beans.DomainHandlerFacade;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("domainHandlerController")
@SessionScoped
public class DomainHandlerController extends CdbEntityController<DomainHandler, DomainHandlerFacade>implements Serializable {
    
    @EJB
    DomainHandlerFacade domainHandlerFacade; 

    
    public DomainHandler getDomainHandler(int id) {
        return domainHandlerFacade.find(id);
    }

    @Override
    protected DomainHandlerFacade getEntityDbFacade() {
        return domainHandlerFacade; 
    }

    @Override
    protected DomainHandler createEntityInstance() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getEntityTypeName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCurrentEntityInstanceName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @FacesConverter(forClass = DomainHandler.class)
    public static class DomainHandlerControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DomainHandlerController controller = (DomainHandlerController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "domainHandlerController");
            return controller.getDomainHandler(getKey(value));
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
            if (object instanceof DomainHandler) {
                DomainHandler o = (DomainHandler) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + DomainHandler.class.getName());
            }
        }

    }

}
