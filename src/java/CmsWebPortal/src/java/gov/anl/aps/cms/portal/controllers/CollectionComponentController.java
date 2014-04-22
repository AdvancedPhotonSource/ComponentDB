package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.model.entities.CollectionComponent;
import gov.anl.aps.cms.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cms.portal.model.beans.CollectionComponentFacade;
import gov.anl.aps.cms.portal.model.entities.SettingType;
import gov.anl.aps.cms.portal.model.entities.User;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;

@Named("collectionComponentController")
@SessionScoped
public class CollectionComponentController extends CrudEntityController<CollectionComponent, CollectionComponentFacade> implements Serializable
{

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "CollectionComponent.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "CollectionComponent.List.Display.Id";
    private static final String DisplayTagSettingTypeKey = "CollectionComponent.List.Display.Tag";
    private static final String DisplayQuantitySettingTypeKey = "CollectionComponent.List.Display.Quantity";
    private static final String DisplayPrioritySettingTypeKey = "CollectionComponent.List.Display.Priority";
    private static final String DisplayDescriptionSettingTypeKey = "CollectionComponent.List.Display.Description";

    private static final Logger logger = Logger.getLogger(CollectionComponentController.class.getName());

    @EJB
    private CollectionComponentFacade collectionComponentFacade;

    private Boolean displayTag = null;
    private Boolean displayQuantity = null;
    private Boolean displayPriority = null;

    public CollectionComponentController() {
    }

    @Override
    protected CollectionComponentFacade getFacade() {
        return collectionComponentFacade;
    }

    @Override
    protected CollectionComponent createEntityInstance() {
        CollectionComponent collectionComponent = new CollectionComponent();
        return collectionComponent;
    }

    @Override
    public String getEntityTypeName() {
        return "collection component";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getComponent().getName();
        }
        return "";
    }

    @Override
    public List<CollectionComponent> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(CollectionComponent collectionComponent) throws ObjectAlreadyExists {
    }

    @Override
    public void prepareEntityUpdate(CollectionComponent collectionComponent) throws ObjectAlreadyExists {
    }

    @Override
    public void updateListSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayTag = Boolean.parseBoolean(settingTypeMap.get(DisplayTagSettingTypeKey).getDefaultValue());
        displayQuantity = Boolean.parseBoolean(settingTypeMap.get(DisplayQuantitySettingTypeKey).getDefaultValue());
        displayPriority = Boolean.parseBoolean(settingTypeMap.get(DisplayPrioritySettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
    }

    @Override
    public void updateListSettingsFromSessionUser(User sessionUser) {
        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayTag = sessionUser.getUserSettingValueAsBoolean(DisplayTagSettingTypeKey, displayTag);
        displayQuantity = sessionUser.getUserSettingValueAsBoolean(DisplayQuantitySettingTypeKey, displayQuantity);
        displayPriority = sessionUser.getUserSettingValueAsBoolean(DisplayPrioritySettingTypeKey, displayPriority);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
    }

    @Override
    public void saveListSettingsForSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayTagSettingTypeKey, displayTag);
        sessionUser.setUserSettingValue(DisplayQuantitySettingTypeKey, displayQuantity);
        sessionUser.setUserSettingValue(DisplayPrioritySettingTypeKey, displayPriority);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
    }
    
    public Boolean getDisplayTag() {
        return displayTag;
    }

    public void setDisplayTag(Boolean displayTag) {
        this.displayTag = displayTag;
    }

    public Boolean getDisplayQuantity() {
        return displayQuantity;
    }

    public void setDisplayQuantity(Boolean displayQuantity) {
        this.displayQuantity = displayQuantity;
    }

    public Boolean getDisplayPriority() {
        return displayPriority;
    }

    public void setDisplayPriority(Boolean displayPriority) {
        this.displayPriority = displayPriority;
    }

    @FacesConverter(forClass = CollectionComponent.class)
    public static class CollectionComponentControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CollectionComponentController controller = (CollectionComponentController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "collectionComponentController");
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
            if (object instanceof CollectionComponent) {
                CollectionComponent o = (CollectionComponent) object;
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + CollectionComponent.class.getName());
            }
        }

    }

}
