package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.model.entities.Person;
import gov.anl.aps.cms.portal.model.beans.PersonFacade;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@Named("personController")
@SessionScoped
public class PersonController extends EntityController<Person, PersonFacade> implements Serializable {

    @EJB
    private PersonFacade ejbFacade;
    
    public PersonController() {
        super();
    }

    @Override
    protected PersonFacade getFacade() {
        return ejbFacade;
    }
    
    @Override
    protected Person createEntityInstance() {
        return new Person();
    }
    
    @Override
    public String getEntityTypeName() {
        return "person";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getFullName();
        }
        return "";    
    }

    @Override
    public List<Person> getAvailableItems() {
        return super.getAvailableItems();
    }    
    
    @FacesConverter(forClass=Person.class)
    public static class PersonControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PersonController controller = (PersonController)facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "personController");
            return controller.getEntity(getKey(value));
        }

        Integer getKey(String value) {
            Integer key;
            key = Integer.valueOf(value);
            return key;
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
            if (object instanceof Person) {
                Person o = (Person) object;
                return getStringKey(o.getPersonId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: "+Person.class.getName());
            }
        }
    }

}
