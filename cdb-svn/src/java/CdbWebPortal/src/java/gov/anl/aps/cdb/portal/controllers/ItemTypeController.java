package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.beans.ItemTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;

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

@Named("itemTypeController")
@SessionScoped
public class ItemTypeController extends ItemTypeCategoryController<ItemType, ItemTypeFacade> implements Serializable {

    /*
     * Controller specific settings
     */
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ItemType.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "ItemType.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "ItemType.List.Display.Description";
    private static final String FilterByNameSettingTypeKey = "ItemType.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "ItemType.List.FilterBy.Description";

    private static final Logger logger = Logger.getLogger(ItemTypeController.class.getName());

    @EJB
    private ItemTypeFacade itemTypeFacade;

    private Boolean displayCategory = null;

    private Boolean selectDisplayCategory = true;

    private String filterByCategory = null;

    private String selectFilterByCategory = null;

    private ItemType selectedItemType = null;

    public ItemTypeController() {
        super();
    }

    @Override
    protected ItemTypeFacade getEntityDbFacade() {
        return itemTypeFacade;
    }

    @Override
    public ItemType createItemTypeCategoryEntity() {
        ItemType itemType = new ItemType();
        itemType.setDomain(getCurrentViewDomain());
        return itemType;
    }

    @Override
    public String getEntityTypeName() {
        return "itemType";
    }

    @Override
    public String getDefaultDisplayEntityTypeName() {
        return "Item Type";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public ItemType findById(Integer id) {
        return itemTypeFacade.find(id);
    }

    @Override
    public List<ItemType> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);

        filterByName = settingEntity.getSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);

    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, Object> filters = dataTable.getFilters();
        filterByCategory = (String) filters.get("itemTypeCategory.name");
    }

    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);

        settingEntity.setSettingValue(FilterByNameSettingTypeKey, filterByName);
        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);

    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByCategory = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        selectFilterByCategory = null;
    }

    public String getFilterByCategory() {
        return filterByCategory;
    }

    public void setFilterByCategory(String filterByCategory) {
        this.filterByCategory = filterByCategory;
    }

    public String getSelectFilterByCategory() {
        return selectFilterByCategory;
    }

    public void setSelectFilterByCategory(String selectFilterByCategory) {
        this.selectFilterByCategory = selectFilterByCategory;
    }

    public Boolean getDisplayCategory() {
        return displayCategory;
    }

    public void setDisplayCategory(Boolean displayCategory) {
        this.displayCategory = displayCategory;
    }

    public Boolean getSelectDisplayCategory() {
        return selectDisplayCategory;
    }

    public void setSelectDisplayCategory(Boolean selectDisplayCategory) {
        this.selectDisplayCategory = selectDisplayCategory;
    }

    public void savePropertyTypeList() {
        update();
    }

    @Override
    public List<ItemType> getItemTypeCategoryEntityListByDomainName(String domainName) {
        return itemTypeFacade.findByDomainName(domainName);
    }

    /**
     * Converter class for component type objects.
     */
    @FacesConverter(value = "itemTypeConverter", forClass = ItemType.class)
    public static class ItemTypeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
            if (value == null || value.length() == 0 || value.equals("Select")) {
                return null;
            }
            try {
                ItemTypeController controller = (ItemTypeController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "itemTypeController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to component type object.");
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
            if (object instanceof ItemType) {
                ItemType o = (ItemType) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ItemType.class.getName());
            }
        }

    }

    public ItemType getSelectedItemType() {
        return selectedItemType;
    }

    public void setSelectedItemType(ItemType selectedItemType) {
        this.selectedItemType = selectedItemType;
    }

}
