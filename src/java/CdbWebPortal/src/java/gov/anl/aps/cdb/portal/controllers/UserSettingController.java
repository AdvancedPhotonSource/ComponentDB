package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.UserSetting;
import gov.anl.aps.cdb.portal.model.db.beans.UserSettingFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

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
import org.primefaces.component.datatable.DataTable;

@Named("userSettingController")
@SessionScoped
public class UserSettingController extends CrudEntityController<UserSetting, UserSettingFacade> implements Serializable {

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "UserSetting.List.Display.NumberOfItemsPerPage";

    @EJB
    private UserSettingFacade userSettingFacade;
    private static final Logger logger = Logger.getLogger(UserSettingController.class.getName());

    // These do not correspond to setting types.
    private String filterBySettingType = null;
    private String filterByValue = null;

    public UserSettingController() {
    }

    @Override
    protected UserSettingFacade getFacade() {
        return userSettingFacade;
    }

    @Override
    protected UserSetting createEntityInstance() {
        UserSetting userSetting = new UserSetting();
        return userSetting;
    }

    @Override
    public String getEntityTypeName() {
        return "userSetting";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "user setting";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getId().toString();
        }
        return "";
    }

    @Override
    public List<UserSetting> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void destroy(UserSetting userSetting) {
        if (userSetting.getId() != null) {
            super.destroy(userSetting);
        }
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, String> filters = dataTable.getFilters();
        filterBySettingType = filters.get("settingType");
        filterByValue = filters.get("value");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterBySettingType = null;
        filterByValue = null;
    }

    public String getFilterBySettingType() {
        return filterBySettingType;
    }

    public void setFilterBySettingType(String filterBySettingType) {
        this.filterBySettingType = filterBySettingType;
    }

    public String getFilterByValue() {
        return filterByValue;
    }

    public void setFilterByValue(String filterByValue) {
        this.filterByValue = filterByValue;
    }

    @FacesConverter(forClass = UserSetting.class)
    public static class UserSettingControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                UserSettingController controller = (UserSettingController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "userSettingController");
                return controller.getEntity(getKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to user setting object.");
                return null;
            }
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
            if (object instanceof UserSetting) {
                UserSetting o = (UserSetting) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + UserSetting.class.getName());
            }
        }

    }

}
