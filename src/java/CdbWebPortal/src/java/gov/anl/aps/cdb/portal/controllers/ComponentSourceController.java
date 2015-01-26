package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.ComponentSource;
import gov.anl.aps.cdb.portal.model.db.beans.ComponentSourceFacade;
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
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

@Named("componentSourceController")
@SessionScoped
public class ComponentSourceController extends CrudEntityController<ComponentSource, ComponentSourceFacade> implements Serializable {

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ComponentSource.List.Display.NumberOfItemsPerPage";
    private static final String DisplayContactInfoSettingTypeKey = "ComponentSource.List.Display.ContactInfo";
    private static final String DisplayCostSettingTypeKey = "ComponentSource.List.Display.Cost";
    private static final String DisplayDescriptionSettingTypeKey = "ComponentSource.List.Display.Description";
    private static final String DisplayIdSettingTypeKey = "ComponentSource.List.Display.Id";
    private static final String DisplayIsManufacturerSettingTypeKey = "ComponentSource.List.Display.IsManufacturer";
    private static final String DisplayIsVendorSettingTypeKey = "ComponentSource.List.Display.IsVendor";
    private static final String DisplayPartNumberSettingTypeKey = "ComponentSource.List.Display.PartNumber";
    private static final String DisplayUrlSettingTypeKey = "ComponentSource.List.Display.Url";

    private static final String FilterByContactInfoSettingTypeKey = "ComponentSource.List.FilterBy.ContactInfo";
    private static final String FilterByCostSettingTypeKey = "ComponentSource.List.FilterBy.Cost";
    private static final String FilterByDescriptionSettingTypeKey = "ComponentSource.List.FilterBy.Description";
    private static final String FilterByIsManufacturerSettingTypeKey = "ComponentSource.List.FilterBy.IsManufacturer";
    private static final String FilterByIsVendorSettingTypeKey = "ComponentSource.List.FilterBy.IsVendor";
    private static final String FilterByPartNumberSettingTypeKey = "ComponentSource.List.FilterBy.PartNumber";
    private static final String FilterBySourceNameSettingTypeKey = "ComponentSource.List.FilterBy.SourceName";
    private static final String FilterByUrlSettingTypeKey = "ComponentSource.List.FilterBy.Url";

    @EJB
    private ComponentSourceFacade componentSourceFacade;
    private static final Logger logger = Logger.getLogger(ComponentSourceController.class.getName());

    private Boolean displayContactInfo = null;
    private Boolean displayCost = null;
    private Boolean displayIsManufacturer = null;
    private Boolean displayIsVendor = null;
    private Boolean displayPartNumber = null;
    private Boolean displayUrl = null;

    private String filterByContactInfo = null;
    private String filterByCost = null;
    private String filterByIsManufacturer = null;
    private String filterByIsVendor = null;
    private String filterByPartNumber = null;
    private String filterBySourceName = null;
    private String filterByUrl = null;

    public ComponentSourceController() {
    }

    @Override
    protected ComponentSourceFacade getFacade() {
        return componentSourceFacade;
    }

    @Override
    protected ComponentSource createEntityInstance() {
        ComponentSource componentSource = new ComponentSource();
        return componentSource;
    }

    @Override
    public String getEntityTypeName() {
        return "componentSource";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "component source";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getId().toString();
        }
        return "";
    }

    @Override
    public List<ComponentSource> getAvailableItems() {
        return super.getAvailableItems();
    }

    public DataModel getListDataModelByComponentId(Integer componentId) {
        return new ListDataModel(componentSourceFacade.findAllByComponentId(componentId));
    }

    public List<ComponentSource> findAllByComponentId(Integer componentId) {
        return componentSourceFacade.findAllByComponentId(componentId);
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayContactInfo = Boolean.parseBoolean(settingTypeMap.get(DisplayContactInfoSettingTypeKey).getDefaultValue());
        displayCost = Boolean.parseBoolean(settingTypeMap.get(DisplayCostSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayIsManufacturer = Boolean.parseBoolean(settingTypeMap.get(DisplayIsManufacturerSettingTypeKey).getDefaultValue());
        displayIsVendor = Boolean.parseBoolean(settingTypeMap.get(DisplayIsVendorSettingTypeKey).getDefaultValue());
        displayPartNumber = Boolean.parseBoolean(settingTypeMap.get(DisplayPartNumberSettingTypeKey).getDefaultValue());
        displayUrl = Boolean.parseBoolean(settingTypeMap.get(DisplayUrlSettingTypeKey).getDefaultValue());

        filterByContactInfo = settingTypeMap.get(FilterByContactInfoSettingTypeKey).getDefaultValue();
        filterByCost = settingTypeMap.get(FilterByCostSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByIsManufacturer = settingTypeMap.get(FilterByIsManufacturerSettingTypeKey).getDefaultValue();
        filterByIsVendor = settingTypeMap.get(FilterByIsVendorSettingTypeKey).getDefaultValue();
        filterByPartNumber = settingTypeMap.get(FilterByPartNumberSettingTypeKey).getDefaultValue();
        filterBySourceName = settingTypeMap.get(FilterBySourceNameSettingTypeKey).getDefaultValue();
        filterByUrl = settingTypeMap.get(FilterByUrlSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayContactInfo = sessionUser.getUserSettingValueAsBoolean(DisplayContactInfoSettingTypeKey, displayContactInfo);
        displayCost = sessionUser.getUserSettingValueAsBoolean(DisplayCostSettingTypeKey, displayCost);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayIsManufacturer = sessionUser.getUserSettingValueAsBoolean(DisplayIsManufacturerSettingTypeKey, displayIsManufacturer);
        displayIsVendor = sessionUser.getUserSettingValueAsBoolean(DisplayIsVendorSettingTypeKey, displayIsVendor);
        displayPartNumber = sessionUser.getUserSettingValueAsBoolean(DisplayPartNumberSettingTypeKey, displayPartNumber);
        displayUrl = sessionUser.getUserSettingValueAsBoolean(DisplayUrlSettingTypeKey, displayUrl);

        filterByContactInfo = sessionUser.getUserSettingValueAsString(FilterByContactInfoSettingTypeKey, filterByContactInfo);
        filterByCost = sessionUser.getUserSettingValueAsString(FilterByCostSettingTypeKey, filterByCost);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByIsManufacturer = sessionUser.getUserSettingValueAsString(FilterByIsManufacturerSettingTypeKey, filterByIsManufacturer);
        filterByIsVendor = sessionUser.getUserSettingValueAsString(FilterByIsVendorSettingTypeKey, filterByIsVendor);

        filterByPartNumber = sessionUser.getUserSettingValueAsString(FilterByPartNumberSettingTypeKey, filterByPartNumber);
        filterBySourceName = sessionUser.getUserSettingValueAsString(FilterBySourceNameSettingTypeKey, filterBySourceName);
        filterByUrl = sessionUser.getUserSettingValueAsString(FilterByUrlSettingTypeKey, filterByUrl);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, String> filters = dataTable.getFilters();
        filterByCost = filters.get("cost");
        filterByContactInfo = filters.get("contactInfo");
        filterByIsManufacturer = filters.get("isManufacturer");
        filterByIsVendor = filters.get("isVendor");
        filterByPartNumber = filters.get("partNumber");
        filterBySourceName = filters.get("source.name");
        filterByUrl = filters.get("url");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayContactInfoSettingTypeKey, displayContactInfo);
        sessionUser.setUserSettingValue(DisplayCostSettingTypeKey, displayCost);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        sessionUser.setUserSettingValue(DisplayIsManufacturerSettingTypeKey, displayIsManufacturer);
        sessionUser.setUserSettingValue(DisplayIsVendorSettingTypeKey, displayIsVendor);
        sessionUser.setUserSettingValue(DisplayPartNumberSettingTypeKey, displayPartNumber);
        sessionUser.setUserSettingValue(DisplayUrlSettingTypeKey, displayUrl);

        sessionUser.setUserSettingValue(FilterByContactInfoSettingTypeKey, filterByContactInfo);
        sessionUser.setUserSettingValue(FilterByCostSettingTypeKey, filterByCost);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByIsManufacturerSettingTypeKey, filterByIsManufacturer);
        sessionUser.setUserSettingValue(FilterByIsVendorSettingTypeKey, filterByIsVendor);
        sessionUser.setUserSettingValue(FilterByPartNumberSettingTypeKey, filterByPartNumber);
        sessionUser.setUserSettingValue(FilterBySourceNameSettingTypeKey, filterBySourceName);
        sessionUser.setUserSettingValue(FilterByUrlSettingTypeKey, filterByUrl);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByCost = null;
        filterByContactInfo = null;
        filterByIsManufacturer = null;
        filterByIsVendor = null;
        filterByPartNumber = null;
        filterBySourceName = null;
        filterByUrl = null;
    }

    @FacesConverter(forClass = ComponentSource.class)
    public static class ComponentSourceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                ComponentSourceController controller = (ComponentSourceController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "componentSourceController");
                return controller.getEntity(getKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to component source object.");
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
            if (object instanceof ComponentSource) {
                ComponentSource o = (ComponentSource) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ComponentSource.class.getName());
            }
        }

    }

    public Boolean getDisplayCost() {
        return displayCost;
    }

    public void setDisplayCost(Boolean displayCost) {
        this.displayCost = displayCost;
    }

    public Boolean getDisplayPartNumber() {
        return displayPartNumber;
    }

    public void setDisplayPartNumber(Boolean displayPartNumber) {
        this.displayPartNumber = displayPartNumber;
    }

    public String getFilterByCost() {
        return filterByCost;
    }

    public void setFilterByCost(String filterByCost) {
        this.filterByCost = filterByCost;
    }

    public String getFilterByPartNumber() {
        return filterByPartNumber;
    }

    public void setFilterByPartNumber(String filterByPartNumber) {
        this.filterByPartNumber = filterByPartNumber;
    }

    public String getFilterBySourceName() {
        return filterBySourceName;
    }

    public void setFilterBySourceName(String filterBySourceName) {
        this.filterBySourceName = filterBySourceName;
    }

    public Boolean getDisplayContactInfo() {
        return displayContactInfo;
    }

    public void setDisplayContactInfo(Boolean displayContactInfo) {
        this.displayContactInfo = displayContactInfo;
    }

    public Boolean getDisplayIsManufacturer() {
        return displayIsManufacturer;
    }

    public void setDisplayIsManufacturer(Boolean displayIsManufacturer) {
        this.displayIsManufacturer = displayIsManufacturer;
    }

    public Boolean getDisplayIsVendor() {
        return displayIsVendor;
    }

    public void setDisplayIsVendor(Boolean displayIsVendor) {
        this.displayIsVendor = displayIsVendor;
    }

    public Boolean getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(Boolean displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getFilterByContactInfo() {
        return filterByContactInfo;
    }

    public void setFilterByContactInfo(String filterByContactInfo) {
        this.filterByContactInfo = filterByContactInfo;
    }

    public String getFilterByIsManufacturer() {
        return filterByIsManufacturer;
    }

    public void setFilterByIsManufacturer(String filterByIsManufacturer) {
        this.filterByIsManufacturer = filterByIsManufacturer;
    }

    public String getFilterByIsVendor() {
        return filterByIsVendor;
    }

    public void setFilterByIsVendor(String filterByIsVendor) {
        this.filterByIsVendor = filterByIsVendor;
    }

    public String getFilterByUrl() {
        return filterByUrl;
    }

    public void setFilterByUrl(String filterByUrl) {
        this.filterByUrl = filterByUrl;
    }

}
