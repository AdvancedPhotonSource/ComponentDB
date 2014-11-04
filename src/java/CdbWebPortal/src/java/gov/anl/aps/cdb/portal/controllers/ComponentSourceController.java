package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.entities.ComponentSource;
import gov.anl.aps.cdb.portal.model.beans.ComponentSourceFacade;
import gov.anl.aps.cdb.portal.model.entities.SettingType;
import gov.anl.aps.cdb.portal.model.entities.UserInfo;

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
    private static final String DisplayIdSettingTypeKey = "ComponentSource.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "ComponentSource.List.Display.Description";
    private static final String DisplayCostSettingTypeKey = "ComponentSource.List.Display.Cost";
    private static final String DisplayPartNumberSettingTypeKey = "ComponentSource.List.Display.PartNumber";

    private static final String FilterByCostSettingTypeKey = "ComponentSource.List.FilterBy.Cost";
    private static final String FilterByDescriptionSettingTypeKey = "ComponentSource.List.FilterBy.Description";
    private static final String FilterByPartNumberSettingTypeKey = "ComponentSource.List.FilterBy.PartNumber";
    private static final String FilterBySourceNameSettingTypeKey = "ComponentSource.List.FilterBy.SourceName";

    @EJB
    private ComponentSourceFacade componentSourceFacade;
    private static final Logger logger = Logger.getLogger(ComponentSourceController.class.getName());

    private Boolean displayCost = null;
    private Boolean displayPartNumber = null;

    private String filterByCost = null;
    private String filterByPartNumber = null;
    private String filterBySourceName = null;

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
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayCost = Boolean.parseBoolean(settingTypeMap.get(DisplayCostSettingTypeKey).getDefaultValue());
        displayPartNumber = Boolean.parseBoolean(settingTypeMap.get(DisplayPartNumberSettingTypeKey).getDefaultValue());

        filterByCost = settingTypeMap.get(FilterByCostSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByPartNumber = settingTypeMap.get(FilterByPartNumberSettingTypeKey).getDefaultValue();
        filterBySourceName = settingTypeMap.get(FilterBySourceNameSettingTypeKey).getDefaultValue();

    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayCost = sessionUser.getUserSettingValueAsBoolean(DisplayCostSettingTypeKey, displayCost);
        displayPartNumber = sessionUser.getUserSettingValueAsBoolean(DisplayPartNumberSettingTypeKey, displayPartNumber);

        filterByCost = sessionUser.getUserSettingValueAsString(FilterByCostSettingTypeKey, filterByCost);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByPartNumber = sessionUser.getUserSettingValueAsString(FilterByPartNumberSettingTypeKey, filterByPartNumber);
        filterBySourceName = sessionUser.getUserSettingValueAsString(FilterBySourceNameSettingTypeKey, filterBySourceName);

    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, String> filters = dataTable.getFilters();
        filterByCost = filters.get("cost");
        filterByPartNumber = filters.get("partNumber");
        filterBySourceName = filters.get("source.name");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        sessionUser.setUserSettingValue(DisplayCostSettingTypeKey, displayCost);
        sessionUser.setUserSettingValue(DisplayPartNumberSettingTypeKey, displayPartNumber);

        sessionUser.setUserSettingValue(FilterByCostSettingTypeKey, filterByCost);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByPartNumberSettingTypeKey, filterByPartNumber);
        sessionUser.setUserSettingValue(FilterBySourceNameSettingTypeKey, filterBySourceName);

    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByCost = null;
        filterByPartNumber = null;
        filterBySourceName = null;
    }

    @FacesConverter(forClass = ComponentSource.class)
    public static class ComponentSourceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ComponentSourceController controller = (ComponentSourceController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "componentSourceController");
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

}
