package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.beans.ItemTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
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


@Named("itemTypeController")
@SessionScoped
public class ItemTypeController extends CdbEntityController<ItemType, ItemTypeFacade>implements Serializable {

    /*
     * Controller specific settings
     */
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ComponentType.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "ComponentType.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "ComponentType.List.Display.Description";
    private static final String DisplayCategorySettingTypeKey = "ComponentType.List.Display.Category";
    private static final String FilterByNameSettingTypeKey = "ComponentType.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "ComponentType.List.FilterBy.Description";
    private static final String FilterByCategorySettingTypeKey = "ComponentType.List.FilterBy.Category";

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
    protected ItemType createEntityInstance() {
        ItemType itemType = new ItemType();
        return itemType;
    }

    @Override
    public String getEntityTypeName() {
        return "itemType";
    }

    @Override
    public String getEntityTypeCategoryName() {
        return "itemTypeCategory";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "component type";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
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
        displayCategory = Boolean.parseBoolean(settingTypeMap.get(DisplayCategorySettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();

        filterByCategory = settingTypeMap.get(FilterByCategorySettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayCategory = sessionUser.getUserSettingValueAsBoolean(DisplayCategorySettingTypeKey, displayCategory);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);

        filterByCategory = sessionUser.getUserSettingValueAsString(FilterByCategorySettingTypeKey, filterByCategory);

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
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        sessionUser.setUserSettingValue(DisplayCategorySettingTypeKey, displayCategory);

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);

        sessionUser.setUserSettingValue(FilterByCategorySettingTypeKey, filterByCategory);
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

    @Override
    public boolean entityHasCategories() {
        return true;
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
