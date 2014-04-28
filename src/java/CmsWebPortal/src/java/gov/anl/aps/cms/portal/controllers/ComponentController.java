package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.exceptions.CmsPortalException;
import gov.anl.aps.cms.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cms.portal.model.entities.Component;
import gov.anl.aps.cms.portal.model.beans.ComponentFacade;
import gov.anl.aps.cms.portal.model.beans.ComponentStateFacade;
import gov.anl.aps.cms.portal.model.entities.ComponentProperty;
import gov.anl.aps.cms.portal.model.entities.ComponentSource;
import gov.anl.aps.cms.portal.model.entities.ComponentState;
import gov.anl.aps.cms.portal.model.entities.EntityInfo;
import gov.anl.aps.cms.portal.model.entities.SettingType;
import gov.anl.aps.cms.portal.model.entities.User;
import gov.anl.aps.cms.portal.model.entities.UserGroup;
import gov.anl.aps.cms.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.Date;
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

@Named("componentController")
@SessionScoped
public class ComponentController extends CrudEntityController<Component, ComponentFacade> implements Serializable
{

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Component.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "Component.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "Component.List.Display.Description";
    private static final String DisplayDocumentationUriSettingTypeKey = "Component.List.Display.DocumentationUri";
    private static final String DisplayStateSettingTypeKey = "Component.List.Display.State";
    private static final String DisplayTypeSettingTypeKey = "Component.List.Display.Type";
    private static final String DisplayTypeCategorySettingTypeKey = "Component.List.Display.TypeCategory";
    private static final String DisplayOwnerUserSettingTypeKey = "Component.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "Component.List.Display.OwnerGroup";
    private static final String DisplayCreatedByUserSettingTypeKey = "Component.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "Component.List.Display.CreatedOnDateTime";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "Component.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "Component.List.Display.LastModifiedOnDateTime";
    private static final String DisplayEstimatedCostSettingTypeKey = "Component.List.Display.EstimatedCost";

    private static final String FilterByNameSettingTypeKey = "Component.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "Component.List.FilterBy.Description";
    private static final String FilterByDocumentationUriSettingTypeKey = "Component.List.FilterBy.DocumentationUri";
    private static final String FilterByStateSettingTypeKey = "Component.List.FilterBy.State";
    private static final String FilterByTypeSettingTypeKey = "Component.List.FilterBy.Type";
    private static final String FilterByTypeCategorySettingTypeKey = "Component.List.FilterBy.TypeCategory";
    private static final String FilterByOwnerUserSettingTypeKey = "Component.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "Component.List.FilterBy.OwnerGroup";
    private static final String FilterByCreatedByUserSettingTypeKey = "Component.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "Component.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "Component.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "Component.List.FilterBy.LastModifiedOnDateTime";
    private static final String FilterByEstimatedCostSettingTypeKey = "Component.List.FilterBy.EstimatedCost";

    private static final Logger logger = Logger.getLogger(ComponentController.class.getName());

    @EJB
    private ComponentFacade componentFacade;
    @EJB
    private ComponentStateFacade componentStateFacade;

    private Boolean displayDocumentationUri = null;
    private Boolean displayState = null;
    private Boolean displayType = null;
    private Boolean displayTypeCategory = null;
    private Boolean displayEstimatedCost = null;

    private String filterByDocumentationUri = null;
    private String filterByState = null;
    private String filterByType = null;
    private String filterByTypeCategory = null;
    private String filterByEstimatedCost = null;

    private Boolean selectDisplayDocumentationUri = false;
    private Boolean selectDisplayState = true;
    private Boolean selectDisplayType = true;
    private Boolean selectDisplayTypeCategory = true;
    private Boolean selectDisplayEstimatedCost = false;

    private String selectFilterByDocumentationUri = null;
    private String selectFilterByState = null;
    private String selectFilterByType = null;
    private String selectFilterByTypeCategory = null;
    private String selectFilterByEstimatedCost = null;

    public ComponentController() {
        super();
    }

    @Override
    protected ComponentFacade getFacade() {
        return componentFacade;
    }

    @Override
    protected Component createEntityInstance() {
        Component component = new Component();
        User ownerUser = (User) SessionUtility.getUser();
        List<ComponentState> componentStateList = componentStateFacade.findAll();
        if (!componentStateList.isEmpty()) {
            component.setComponentState(componentStateList.get(0));
        }
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setOwnerUser(ownerUser);
        List<UserGroup> ownerUserGroupList = ownerUser.getUserGroupList();
        if (!ownerUserGroupList.isEmpty()) {
            entityInfo.setOwnerUserGroup(ownerUserGroupList.get(0));
        }
        component.setEntityInfo(entityInfo);
        return component;
    }

    @Override
    public Component cloneEntityInstance(Component component) {
        Component clonedComponent = super.cloneEntityInstance(component);
        User ownerUser = (User) SessionUtility.getUser();
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setOwnerUser(ownerUser);
        List<UserGroup> ownerUserGroupList = ownerUser.getUserGroupList();
        if (!ownerUserGroupList.isEmpty()) {
            entityInfo.setOwnerUserGroup(ownerUserGroupList.get(0));
        }
        clonedComponent.setEntityInfo(entityInfo);
        return clonedComponent;
    }

    @Override
    public String getEntityTypeName() {
        return "component";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<Component> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(Component component) throws ObjectAlreadyExists {
        Component existingComponent = componentFacade.findByName(component.getName());
        if (existingComponent != null) {
            throw new ObjectAlreadyExists("Component " + component.getName() + " already exists.");
        }
        EntityInfo entityInfo = component.getEntityInfo();
        User createdByUser = (User) SessionUtility.getUser();
        Date createdOnDateTime = new Date();
        entityInfo.setCreatedOnDateTime(createdOnDateTime);
        entityInfo.setCreatedByUser(createdByUser);
        entityInfo.setLastModifiedOnDateTime(createdOnDateTime);
        entityInfo.setLastModifiedByUser(createdByUser);
        logger.debug("Inserting new component " + component.getName() + " (user: " + createdByUser.getUsername() + ")");
    }

    @Override
    public void prepareEntityUpdate(Component component) throws CmsPortalException {
        EntityInfo entityInfo = component.getEntityInfo();
        User lastModifiedByUser = (User) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();
        entityInfo.setLastModifiedOnDateTime(lastModifiedOnDateTime);
        entityInfo.setLastModifiedByUser(lastModifiedByUser);
        logger.debug("Updating component " + component.getName() + " (user: " + lastModifiedByUser.getUsername() + ")");
    }

    public Component findById(Integer id) {
        return componentFacade.findById(id);
    }

    @Override
    public void selectByRequestParams() {
        if (idViewParam != null) {
            Component component = findById(idViewParam);
            setCurrent(component);
            idViewParam = null;
        }
    }

    public void prepareAddProperty() {
        Component component = getCurrent();
        List<ComponentProperty> propertyList = component.getComponentPropertyList();
        ComponentProperty property = new ComponentProperty();
        property.setComponent(component);
        propertyList.add(property);
    }

    public void savePropertyList() {
        update();
    }

    public void deleteProperty(ComponentProperty componentProperty) {
        Component component = getCurrent();
        List<ComponentProperty> componentPropertyList = component.getComponentPropertyList();
        componentPropertyList.remove(componentProperty);
    }

    public void prepareAddSource() {
        Component component = getCurrent();
        List<ComponentSource> sourceList = component.getComponentSourceList();
        ComponentSource source = new ComponentSource();
        source.setComponent(component);
        sourceList.add(source);
    }

    public void saveSourceList() {
        update();
    }

    public void deleteSource(ComponentSource source) {
        Component component = getCurrent();
        List<ComponentSource> sourceList = component.getComponentSourceList();
        sourceList.remove(source);
        update();
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        logger.debug("Updating list settings from setting type defaults");

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayDocumentationUri = Boolean.parseBoolean(settingTypeMap.get(DisplayDocumentationUriSettingTypeKey).getDefaultValue());
        displayState = Boolean.parseBoolean(settingTypeMap.get(DisplayStateSettingTypeKey).getDefaultValue());
        displayType = Boolean.parseBoolean(settingTypeMap.get(DisplayTypeSettingTypeKey).getDefaultValue());
        displayTypeCategory = Boolean.parseBoolean(settingTypeMap.get(DisplayTypeCategorySettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());
        displayEstimatedCost = Boolean.parseBoolean(settingTypeMap.get(DisplayEstimatedCostSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByDocumentationUri = settingTypeMap.get(FilterByDocumentationUriSettingTypeKey).getDefaultValue();
        filterByState = settingTypeMap.get(FilterByStateSettingTypeKey).getDefaultValue();
        filterByType = settingTypeMap.get(FilterByTypeSettingTypeKey).getDefaultValue();
        filterByTypeCategory = settingTypeMap.get(FilterByTypeCategorySettingTypeKey).getDefaultValue();
        filterByOwnerUser = settingTypeMap.get(FilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(FilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(FilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(FilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(FilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(FilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByEstimatedCost = settingTypeMap.get(FilterByEstimatedCostSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateListSettingsFromSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        logger.debug("Updating list settings from session user");

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDocumentationUriSettingTypeKey, displayDescription);
        displayDocumentationUri = sessionUser.getUserSettingValueAsBoolean(DisplayDocumentationUriSettingTypeKey, displayDocumentationUri);
        displayState = sessionUser.getUserSettingValueAsBoolean(DisplayStateSettingTypeKey, displayState);
        displayType = sessionUser.getUserSettingValueAsBoolean(DisplayTypeSettingTypeKey, displayType);
        displayTypeCategory = sessionUser.getUserSettingValueAsBoolean(DisplayTypeCategorySettingTypeKey, displayTypeCategory);
        displayOwnerUser = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
        displayEstimatedCost = sessionUser.getUserSettingValueAsBoolean(DisplayEstimatedCostSettingTypeKey, displayEstimatedCost);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByDocumentationUri = sessionUser.getUserSettingValueAsString(FilterByDocumentationUriSettingTypeKey, filterByDocumentationUri);
        filterByState = sessionUser.getUserSettingValueAsString(FilterByStateSettingTypeKey, filterByState);
        filterByType = sessionUser.getUserSettingValueAsString(FilterByTypeSettingTypeKey, filterByType);
        filterByTypeCategory = sessionUser.getUserSettingValueAsString(FilterByTypeCategorySettingTypeKey, filterByTypeCategory);
        filterByOwnerUser = sessionUser.getUserSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = sessionUser.getUserSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = sessionUser.getUserSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = sessionUser.getUserSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);
        filterByEstimatedCost = sessionUser.getUserSettingValueAsString(FilterByEstimatedCostSettingTypeKey, filterByEstimatedCost);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, String> filters = dataTable.getFilters();
        filterByDocumentationUri = filters.get("documentationUriShortDisplay");
        filterByState = filters.get("componentState.name");
        filterByType = filters.get("componentTypeList");
        filterByTypeCategory = filters.get("componentTypeCategoryList");
        filterByEstimatedCost = filters.get("estimatedCost");
    }

    @Override
    public void saveSettingsForSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        sessionUser.setUserSettingValue(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        sessionUser.setUserSettingValue(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        sessionUser.setUserSettingValue(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        sessionUser.setUserSettingValue(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        sessionUser.setUserSettingValue(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        sessionUser.setUserSettingValue(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        sessionUser.setUserSettingValue(DisplayDocumentationUriSettingTypeKey, displayDocumentationUri);
        sessionUser.setUserSettingValue(DisplayEstimatedCostSettingTypeKey, displayEstimatedCost);
        sessionUser.setUserSettingValue(DisplayStateSettingTypeKey, displayState);
        sessionUser.setUserSettingValue(DisplayTypeSettingTypeKey, displayType);
        sessionUser.setUserSettingValue(DisplayTypeCategorySettingTypeKey, displayTypeCategory);

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        sessionUser.setUserSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        sessionUser.setUserSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        sessionUser.setUserSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        sessionUser.setUserSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        sessionUser.setUserSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        sessionUser.setUserSettingValue(FilterByDocumentationUriSettingTypeKey, filterByDocumentationUri);
        sessionUser.setUserSettingValue(FilterByEstimatedCostSettingTypeKey, filterByEstimatedCost);
        sessionUser.setUserSettingValue(FilterByStateSettingTypeKey, filterByState);        
        sessionUser.setUserSettingValue(FilterByTypeSettingTypeKey, filterByType);
        sessionUser.setUserSettingValue(FilterByTypeCategorySettingTypeKey, filterByTypeCategory);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByDocumentationUri = null;
        filterByState = null;
        filterByType = null;
        filterByTypeCategory = null;
        filterByEstimatedCost = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        selectFilterByDocumentationUri = null;
        selectFilterByState = null;
        selectFilterByType = null;
        selectFilterByTypeCategory = null;
        selectFilterByEstimatedCost = null;
    }

    public Boolean getDisplayDocumentationUri() {
        return displayDocumentationUri;
    }

    public void setDisplayDocumentationUri(Boolean displayDocumentationUri) {
        this.displayDocumentationUri = displayDocumentationUri;
    }

    public Boolean getDisplayState() {
        return displayState;
    }

    public void setDisplayState(Boolean displayState) {
        this.displayState = displayState;
    }

    public Boolean getDisplayType() {
        return displayType;
    }

    public void setDisplayType(Boolean displayType) {
        this.displayType = displayType;
    }

    public Boolean getDisplayTypeCategory() {
        return displayTypeCategory;
    }

    public void setDisplayTypeCategory(Boolean displayTypeCategory) {
        this.displayTypeCategory = displayTypeCategory;
    }

    public Boolean getDisplayEstimatedCost() {
        return displayEstimatedCost;
    }

    public void setDisplayEstimatedCost(Boolean displayEstimatedCost) {
        this.displayEstimatedCost = displayEstimatedCost;
    }

    public String getFilterByDocumentationUri() {
        return filterByDocumentationUri;
    }

    public void setFilterByDocumentationUri(String filterByDocumentationUri) {
        this.filterByDocumentationUri = filterByDocumentationUri;
    }

    public String getFilterByState() {
        return filterByState;
    }

    public void setFilterByState(String filterByState) {
        this.filterByState = filterByState;
    }

    public String getFilterByType() {
        return filterByType;
    }

    public void setFilterByType(String filterByType) {
        this.filterByType = filterByType;
    }

    public String getFilterByEstimatedCost() {
        return filterByEstimatedCost;
    }

    public void setFilterByEstimatedCost(String filterByEstimatedCost) {
        this.filterByEstimatedCost = filterByEstimatedCost;
    }

    public String getFilterByTypeCategory() {
        return filterByTypeCategory;
    }

    public void setFilterByTypeCategory(String filterByTypeCategory) {
        this.filterByTypeCategory = filterByTypeCategory;
    }

    public Boolean getSelectDisplayDocumentationUri() {
        return selectDisplayDocumentationUri;
    }

    public void setSelectDisplayDocumentationUri(Boolean selectDisplayDocumentationUri) {
        this.selectDisplayDocumentationUri = selectDisplayDocumentationUri;
    }

    public Boolean getSelectDisplayState() {
        return selectDisplayState;
    }

    public void setSelectDisplayState(Boolean selectDisplayState) {
        this.selectDisplayState = selectDisplayState;
    }

    public Boolean getSelectDisplayType() {
        return selectDisplayType;
    }

    public void setSelectDisplayType(Boolean selectDisplayType) {
        this.selectDisplayType = selectDisplayType;
    }

    public Boolean getSelectDisplayTypeCategory() {
        return selectDisplayTypeCategory;
    }

    public void setSelectDisplayTypeCategory(Boolean selectDisplayTypeCategory) {
        this.selectDisplayTypeCategory = selectDisplayTypeCategory;
    }

    public Boolean getSelectDisplayEstimatedCost() {
        return selectDisplayEstimatedCost;
    }

    public void setSelectDisplayEstimatedCost(Boolean selectDisplayEstimatedCost) {
        this.selectDisplayEstimatedCost = selectDisplayEstimatedCost;
    }

    public String getSelectFilterByDocumentationUri() {
        return selectFilterByDocumentationUri;
    }

    public void setSelectFilterByDocumentationUri(String selectFilterByDocumentationUri) {
        this.selectFilterByDocumentationUri = selectFilterByDocumentationUri;
    }

    public String getSelectFilterByState() {
        return selectFilterByState;
    }

    public void setSelectFilterByState(String selectFilterByState) {
        this.selectFilterByState = selectFilterByState;
    }

    public String getSelectFilterByType() {
        return selectFilterByType;
    }

    public void setSelectFilterByType(String selectFilterByType) {
        this.selectFilterByType = selectFilterByType;
    }

    public String getSelectFilterByTypeCategory() {
        return selectFilterByTypeCategory;
    }

    public void setSelectFilterByTypeCategory(String selectFilterByTypeCategory) {
        this.selectFilterByTypeCategory = selectFilterByTypeCategory;
    }

    public String getSelectFilterByEstimatedCost() {
        return selectFilterByEstimatedCost;
    }

    public void setSelectFilterByEstimatedCost(String selectFilterByEstimatedCost) {
        this.selectFilterByEstimatedCost = selectFilterByEstimatedCost;
    }

    @FacesConverter(value = "componentConverter", forClass = Component.class)
    public static class ComponentControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0 || value.equals("Select")) {
                return null;
            }
            ComponentController controller = (ComponentController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "componentController");
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
            if (object instanceof Component) {
                Component o = (Component) object;
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Component.class.getName());
            }
        }

    }

}
