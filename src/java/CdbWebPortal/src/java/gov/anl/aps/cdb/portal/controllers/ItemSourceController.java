package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.ItemSource;
import gov.anl.aps.cdb.portal.model.db.beans.ItemSourceFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

import java.io.Serializable;
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

@Named("itemSourceController")
@SessionScoped
public class ItemSourceController extends CdbEntityController<ItemSource, ItemSourceFacade>implements Serializable {

    /*
     * Controller specific settings
     */
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
    private ItemSourceFacade itemSourceFacade;
    private static final Logger logger = Logger.getLogger(ItemSourceController.class.getName());

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

    public ItemSourceController() {
    }

    @Override
    protected ItemSourceFacade getEntityDbFacade() {
        return itemSourceFacade;
    }

    @Override
    protected ItemSource createEntityInstance() {
        ItemSource itemSource = new ItemSource();
        return itemSource;
    }

    @Override
    public String getEntityTypeName() {
        return "itemSource";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "item source";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getId().toString();
        }
        return "";
    }

    /*
    @Override
    public List<ComponentSource> getAvailableItems() {
        return super.getAvailableItems();
    }

    public DataModel getListDataModelByComponentId(Integer componentId) {
        return new ListDataModel(itemSourceFacade.findAllByComponentId(componentId));
    }

    public List<ComponentSource> findAllByComponentId(Integer componentId) {
        return itemSourceFacade.findAllByComponentId(componentId);
    }
    */
    
    
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
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayContactInfo = settingEntity.getSettingValueAsBoolean(DisplayContactInfoSettingTypeKey, displayContactInfo);
        displayCost = settingEntity.getSettingValueAsBoolean(DisplayCostSettingTypeKey, displayCost);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayIsManufacturer = settingEntity.getSettingValueAsBoolean(DisplayIsManufacturerSettingTypeKey, displayIsManufacturer);
        displayIsVendor = settingEntity.getSettingValueAsBoolean(DisplayIsVendorSettingTypeKey, displayIsVendor);
        displayPartNumber = settingEntity.getSettingValueAsBoolean(DisplayPartNumberSettingTypeKey, displayPartNumber);
        displayUrl = settingEntity.getSettingValueAsBoolean(DisplayUrlSettingTypeKey, displayUrl);

        filterByContactInfo = settingEntity.getSettingValueAsString(FilterByContactInfoSettingTypeKey, filterByContactInfo);
        filterByCost = settingEntity.getSettingValueAsString(FilterByCostSettingTypeKey, filterByCost);
        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByIsManufacturer = settingEntity.getSettingValueAsString(FilterByIsManufacturerSettingTypeKey, filterByIsManufacturer);
        filterByIsVendor = settingEntity.getSettingValueAsString(FilterByIsVendorSettingTypeKey, filterByIsVendor);

        filterByPartNumber = settingEntity.getSettingValueAsString(FilterByPartNumberSettingTypeKey, filterByPartNumber);
        filterBySourceName = settingEntity.getSettingValueAsString(FilterBySourceNameSettingTypeKey, filterBySourceName);
        filterByUrl = settingEntity.getSettingValueAsString(FilterByUrlSettingTypeKey, filterByUrl);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, Object> filters = dataTable.getFilters();
        filterByCost = (String) filters.get("cost");
        filterByContactInfo = (String) filters.get("contactInfo");
        filterByIsManufacturer = (String) filters.get("isManufacturer");
        filterByIsVendor = (String) filters.get("isVendor");
        filterByPartNumber = (String) filters.get("partNumber");
        filterBySourceName = (String) filters.get("source.name");
        filterByUrl = (String) filters.get("url");
    }

    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayContactInfoSettingTypeKey, displayContactInfo);
        settingEntity.setSettingValue(DisplayCostSettingTypeKey, displayCost);
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayIsManufacturerSettingTypeKey, displayIsManufacturer);
        settingEntity.setSettingValue(DisplayIsVendorSettingTypeKey, displayIsVendor);
        settingEntity.setSettingValue(DisplayPartNumberSettingTypeKey, displayPartNumber);
        settingEntity.setSettingValue(DisplayUrlSettingTypeKey, displayUrl);

        settingEntity.setSettingValue(FilterByContactInfoSettingTypeKey, filterByContactInfo);
        settingEntity.setSettingValue(FilterByCostSettingTypeKey, filterByCost);
        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByIsManufacturerSettingTypeKey, filterByIsManufacturer);
        settingEntity.setSettingValue(FilterByIsVendorSettingTypeKey, filterByIsVendor);
        settingEntity.setSettingValue(FilterByPartNumberSettingTypeKey, filterByPartNumber);
        settingEntity.setSettingValue(FilterBySourceNameSettingTypeKey, filterBySourceName);
        settingEntity.setSettingValue(FilterByUrlSettingTypeKey, filterByUrl);
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

    /**
     * Converter class for component source objects.
     */
    @FacesConverter(forClass = ItemSource.class)
    public static class ComponentSourceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                ItemSourceController controller = (ItemSourceController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "componentSourceController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to component source object.");
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
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof ItemSource) {
                ItemSource o = (ItemSource) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ItemSource.class.getName());
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
