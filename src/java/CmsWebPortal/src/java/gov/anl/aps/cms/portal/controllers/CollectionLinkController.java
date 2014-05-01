package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.model.entities.CollectionLink;
import gov.anl.aps.cms.portal.model.beans.CollectionLinkFacade;
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

@Named("collectionLinkController")
@SessionScoped
public class CollectionLinkController extends CrudEntityController<CollectionLink, CollectionLinkFacade> implements Serializable
{
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "CollectionLink.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "CollectionLink.List.Display.Id";
    private static final String DisplayTagSettingTypeKey = "CollectionLink.List.Display.Tag";
    private static final String DisplayDescriptionSettingTypeKey = "CollectionLink.List.Display.Description";

    @EJB
    private CollectionLinkFacade collectionLinkFacade;

    private Boolean displayTag = null;
    private static final Logger logger = Logger.getLogger(CollectionLinkController.class.getName());


    public CollectionLinkController() {
    }

    @Override
    protected CollectionLinkFacade getFacade() {
        return collectionLinkFacade;
    }

    @Override
    protected CollectionLink createEntityInstance() {
        CollectionLink collectionLink = new CollectionLink();
        return collectionLink;
    }

    @Override
    public String getEntityTypeName() {
        return "collection link";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            Integer id = getCurrent().getId();
            if (id != null) {
                return id.toString();
            }
        }
        return "";
    }

    @Override
    public List<CollectionLink> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayTag = Boolean.parseBoolean(settingTypeMap.get(DisplayTagSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
    }

    @Override
    public void updateSettingsFromSessionUser(User sessionUser) {
        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayTag = sessionUser.getUserSettingValueAsBoolean(DisplayTagSettingTypeKey, displayTag);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
    }

    @Override
    public void saveSettingsForSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayTagSettingTypeKey, displayTag);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
    }
    
    public Boolean getDisplayTag() {
        return displayTag;
    }

    public void setDisplayTag(Boolean displayTag) {
        this.displayTag = displayTag;
    }
    
    @FacesConverter(forClass = CollectionLink.class)
    public static class CollectionLinkControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CollectionLinkController controller = (CollectionLinkController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "collectionLinkController");
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
            if (object instanceof CollectionLink) {
                CollectionLink o = (CollectionLink) object;
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + CollectionLink.class.getName());
            }
        }

    }

}
