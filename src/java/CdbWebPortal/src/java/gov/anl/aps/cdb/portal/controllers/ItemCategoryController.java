package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.beans.ItemCategoryFacade;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;

@Named("itemCategoryController")
@SessionScoped
public class ItemCategoryController extends CdbEntityController<ItemCategory, ItemCategoryFacade> implements Serializable {

    @EJB
    ItemCategoryFacade itemCategoryFacade; 
    
    private static final Logger logger = Logger.getLogger(ItemCategoryController.class.getName());
    
    public ItemCategoryController() {
        super();
        displayDescription = true; 
    }

    @Override
    protected ItemCategoryFacade getEntityDbFacade() {
        return itemCategoryFacade; 
    }       

    @Override
    public String getDisplayEntityTypeName() {
        return "Item Category"; 
    }

    @Override
    public ItemCategory findById(Integer id) {
        return itemCategoryFacade.find(id); 
    }

    @Override
    protected ItemCategory createEntityInstance() {
        ItemCategory itemCategory = new ItemCategory();
        return itemCategory; 
    }

    @Override
    public String getEntityTypeName() {
        return "itemCategory";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }
    
    /**
     * Converter class for component category objects.
     */
    @FacesConverter(value = "itemCategoryConverter", forClass = ItemCategory.class)
    public static class ItemCategoryControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
            if (value == null || value.length() == 0 || value.equals("Select")) {
                return null;
            }
            try {
                ItemCategoryController controller = (ItemCategoryController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "itemCategoryController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to component category object.");
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
            if (object instanceof ItemCategory) {
                ItemCategory o = (ItemCategory) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of category " + object.getClass().getName() + "; expected category: " + ItemCategory.class.getName());
            }
        }

    }

}
