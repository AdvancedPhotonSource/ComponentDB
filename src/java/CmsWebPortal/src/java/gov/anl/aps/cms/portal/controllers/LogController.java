package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.model.entities.Log;
import gov.anl.aps.cms.portal.model.beans.LogFacade;
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
import org.primefaces.component.datatable.DataTable;

@Named("logController")
@SessionScoped
public class LogController extends CrudEntityController<Log, LogFacade> implements Serializable
{

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Log.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "Log.List.Display.Id";
    private static final String DisplayCreatedByUserSettingTypeKey = "Log.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "Log.List.Display.CreatedOnDateTime";

    @EJB
    private LogFacade logFacade;

    private DataTable collectionLogListDataTable = null;
    private DataTable componentLogListDataTable = null;

    public LogController() {
        super();
    }

    @Override
    protected LogFacade getFacade() {
        return logFacade;
    }

    @Override
    protected Log createEntityInstance() {
        return new Log();
    }

    @Override
    public String getEntityTypeName() {
        return "log";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getId().toString();
        }
        return "";
    }

    @Override
    public List<Log> getAvailableItems() {
        return super.getAvailableItems();
    }

    public DataTable getCollectionLogListDataTable() {
        if (userSettingsChanged()) {
            collectionLogListDataTable = new DataTable();
        }
        return collectionLogListDataTable;
    }
 
    public void setCollectionLogListDataTable(DataTable collectionLogListDataTable) {
        this.collectionLogListDataTable = collectionLogListDataTable;
    }
     
    public DataTable getComponentLogListDataTable() {
        if (userSettingsChanged()) {
            componentLogListDataTable = new DataTable();
        }
        return componentLogListDataTable;
    }

    public void setComponentLogListDataTable(DataTable componentLogListDataTable) {
        this.componentLogListDataTable = componentLogListDataTable;
    }
    
    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
    }

    @Override
    public void updateSettingsFromSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
    }

    @Override
    public void saveSettingsForSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        sessionUser.setUserSettingValue(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
    }

    @FacesConverter(forClass = Log.class)
    public static class LogControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LogController controller = (LogController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "logController");
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
            if (object instanceof Log) {
                Log o = (Log) object;
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Log.class.getName());
            }
        }

    }

}
