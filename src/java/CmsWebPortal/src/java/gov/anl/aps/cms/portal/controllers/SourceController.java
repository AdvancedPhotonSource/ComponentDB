package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.model.entities.Source;
import gov.anl.aps.cms.portal.model.beans.SourceFacade;
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

@Named("sourceController")
@SessionScoped
public class SourceController extends CrudEntityController<Source, SourceFacade> implements Serializable
{

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Source.List.Display.NumberOfItemsPerPage";

    @EJB
    private SourceFacade sourceFacade;
    private static final Logger logger = Logger.getLogger(SourceController.class.getName());

    public SourceController() {
    }

    @Override
    protected SourceFacade getFacade() {
        return sourceFacade;
    }

    @Override
    protected Source createEntityInstance() {
        Source source = new Source();
        return source;
    }

    @Override
    public String getEntityTypeName() {
        return "source";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<Source> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
    }

    @Override
    public void updateSettingsFromSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
    }

    @FacesConverter(forClass = Source.class)
    public static class SourceControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SourceController controller = (SourceController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "sourceController");
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
            if (object instanceof Source) {
                Source o = (Source) object;
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Source.class.getName());
            }
        }

    }

}
