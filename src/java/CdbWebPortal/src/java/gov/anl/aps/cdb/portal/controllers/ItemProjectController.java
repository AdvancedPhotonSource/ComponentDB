package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.beans.ItemProjectFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;

@Named("itemProjectController")
@SessionScoped
public class ItemProjectController extends CdbEntityController<ItemProject, ItemProjectFacade> implements Serializable {

    @EJB
    ItemProjectFacade itemProjectFacade; 
    
    private static final Logger logger = Logger.getLogger(ItemProjectController.class.getName());
    
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
