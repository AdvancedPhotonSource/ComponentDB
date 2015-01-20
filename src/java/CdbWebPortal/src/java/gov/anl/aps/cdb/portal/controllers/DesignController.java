package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.entities.Design;
import gov.anl.aps.cdb.portal.model.db.beans.DesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import gov.anl.aps.cdb.portal.model.db.entities.DesignElement;
import gov.anl.aps.cdb.portal.model.db.entities.DesignLink;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.component.UIComponent;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

@Named("designController")
@SessionScoped
public class DesignController extends CrudEntityController<Design, DesignFacade> implements Serializable {

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Design.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "Design.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "Design.List.Display.Description";
    private static final String DisplayOwnerUserSettingTypeKey = "Design.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "Design.List.Display.OwnerGroup";
    private static final String DisplayCreatedByUserSettingTypeKey = "Design.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "Design.List.Display.CreatedOnDateTime";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "Design.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "Design.List.Display.LastModifiedOnDateTime";

    private static final String FilterByNameSettingTypeKey = "Design.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "Design.List.FilterBy.Description";
    private static final String FilterByOwnerUserSettingTypeKey = "Design.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "Design.List.FilterBy.OwnerGroup";
    private static final String FilterByCreatedByUserSettingTypeKey = "Design.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "Design.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "Design.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "Design.List.FilterBy.LastModifiedOnDateTime";

    private static final Logger logger = Logger.getLogger(DesignController.class.getName());

    private boolean selectChildDesigns = false;

    private DataTable designPropertyValueListDataTable = null;

    private List<PropertyValue> filteredPropertyValueList;

    @EJB
    private DesignFacade designFacade;

    public DesignController() {
        super();
    }

    @Override
    protected DesignFacade getFacade() {
        return designFacade;
    }

    @Override
    protected Design createEntityInstance() {
        Design design = new Design();
        EntityInfo entityInfo = new EntityInfo();
        UserInfo ownerUser = (UserInfo) SessionUtility.getUser();
        entityInfo.setOwnerUser(ownerUser);
        List<UserGroup> ownerUserGroupList = ownerUser.getUserGroupList();
        if (!ownerUserGroupList.isEmpty()) {
            entityInfo.setOwnerUserGroup(ownerUserGroupList.get(0));
        }
        design.setEntityInfo(entityInfo);
        return design;
    }

    @Override
    public Design cloneEntityInstance(Design design) {
        Design clonedDesign = super.cloneEntityInstance(design);
        UserInfo ownerUser = (UserInfo) SessionUtility.getUser();
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setOwnerUser(ownerUser);
        List<UserGroup> ownerUserGroupList = ownerUser.getUserGroupList();
        if (!ownerUserGroupList.isEmpty()) {
            entityInfo.setOwnerUserGroup(ownerUserGroupList.get(0));
        }
        clonedDesign.setEntityInfo(entityInfo);
        return clonedDesign;
    }

    @Override
    public String getEntityTypeName() {
        return "design";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<Design> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(Design design) throws ObjectAlreadyExists {
        Design existingElement = designFacade.findByName(design.getName());
        if (existingElement != null) {
            throw new ObjectAlreadyExists("Design " + design.getName() + " already exists.");
        }
        EntityInfo entityInfo = design.getEntityInfo();
        UserInfo createdByUser = (UserInfo) SessionUtility.getUser();
        Date createdOnDateTime = new Date();
        entityInfo.setCreatedOnDateTime(createdOnDateTime);
        entityInfo.setCreatedByUser(createdByUser);
        entityInfo.setLastModifiedOnDateTime(createdOnDateTime);
        entityInfo.setLastModifiedByUser(createdByUser);
        String logText = getLogText();
        if (logText != null && !logText.isEmpty()) {
            Log logEntry = new Log();
            logEntry.setText(logText);
            logEntry.setEnteredByUser(createdByUser);
            logEntry.setEnteredOnDateTime(createdOnDateTime);
            List<Log> logList = new ArrayList<>();
            logList.add(logEntry);
            design.setLogList(logList);
            resetLogText();
        }
        logger.debug("Inserting new design " + design.getName() + " (user: " + createdByUser.getUsername() + ")");
    }

    @Override
    public void prepareEntityUpdate(Design design) throws ObjectAlreadyExists {
        EntityInfo entityInfo = design.getEntityInfo();
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();
        entityInfo.setLastModifiedOnDateTime(lastModifiedOnDateTime);
        entityInfo.setLastModifiedByUser(lastModifiedByUser);
        String logText = getLogText();
        if (logText != null && !logText.isEmpty()) {
            Log logEntry = new Log();
            logEntry.setText(logText);
            logEntry.setEnteredByUser(lastModifiedByUser);
            logEntry.setEnteredOnDateTime(lastModifiedOnDateTime);
            design.getLogList().add(logEntry);
            resetLogText();
        }
        logger.debug("Updating design " + design.getName() + " (user: " + lastModifiedByUser.getUsername() + ")");
    }

    public Design findById(Integer id) {
        return designFacade.findById(id);
    }

    @Override
    public void selectByRequestParams() {
        if (idViewParam != null) {
            Design design = findById(idViewParam);
            setCurrent(design);
            idViewParam = null;
        }
    }

        public void prepareAddProperty() {
        Design design = getCurrent();
        List<PropertyValue> propertyList = design.getPropertyValueList();
        PropertyValue property = new PropertyValue();
        propertyList.add(property);
    }

    public void savePropertyList() {
        update();
    }

    public void selectPropertyTypes(List<PropertyType> propertyTypeList) {
        Design design = getCurrent();
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();
        List<PropertyValue> propertyValueList = design.getPropertyValueList();
        for (PropertyType propertyType : propertyTypeList) {
            PropertyValue propertyValue = new PropertyValue();
            propertyValue.setPropertyType(propertyType);
            propertyValue.setValue(propertyType.getDefaultValue());
            propertyValue.setUnits(propertyType.getDefaultUnits());
            propertyValueList.add(propertyValue);
            propertyValue.setEnteredByUser(lastModifiedByUser);
            propertyValue.setEnteredOnDateTime(lastModifiedOnDateTime);
        }
    }

    public void deleteProperty(PropertyValue designProperty) {
        Design design = getCurrent();
        List<PropertyValue> designPropertyList = design.getPropertyValueList();
        designPropertyList.remove(designProperty);
        update();
    }
    
    public void prepareAddDesignElement(Design design) {
        List<DesignElement> designElementList = design.getDesignElementList();
        DesignElement designElement = new DesignElement();
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        designElement.setEntityInfo(entityInfo);
        designElement.setParentDesign(design);
        designElementList.add(designElement);
    }

    public void deleteDesignElement(DesignElement designElement) {
        Design design = getCurrent();
        List<DesignElement> designElementList = design.getDesignElementList();
        designElementList.remove(designElement);
        update();
    }

    public void selectComponents(List<Component> componentList) {
        Design design = getCurrent();
        List<DesignElement> designElementList = design.getDesignElementList();
        for (Component component : componentList) {
            DesignElement designElement = new DesignElement();
            designElement.setParentDesign(design);
            designElement.setComponent(component);
            designElementList.add(designElement);
        }
    }

    public void saveElementList() {
        update();
    }

    public void deleteElement(DesignElement designElement) {
        logger.debug("Removing element " + designElement.getName() + " from design " + designElement.getParentDesign().getName());
        Design design = getCurrent();
        List<DesignElement> designElementList = design.getDesignElementList();
        designElementList.remove(designElement);
    }

    public void prepareAddLog(Design design) {
        Log logEntry = LogUtility.createLogEntry();
        List<Log> componentLogList = design.getLogList();
        componentLogList.add(0, logEntry);
    }

    public void deleteLog(Log designLog) {
        Design design = getCurrent();
        List<Log> designLogList = design.getLogList();
        designLogList.remove(designLog);
    }

    public List<Log> getLogList() {
        Design design = getCurrent();
        List<Log> designLogList = design.getLogList();
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser != null) {
            Date settingsTimestamp = getSettingsTimestamp();
            if (settingsTimestamp == null || sessionUser.areUserSettingsModifiedAfterDate(settingsTimestamp)) {
                updateSettingsFromSessionUser(sessionUser);
                settingsTimestamp = new Date();
                setSettingsTimestamp(settingsTimestamp);
            }
        }
        return designLogList;
    }

    public void saveLogList() {
        update();
    }

    @Override
    public void prepareEntityListForSelection(List<Design> selectEntityList) {
        // Need to prevent selecting current design, or any children or parents.
        Design currentDesign = getCurrent();
        selectEntityList.remove(currentDesign);

    }

    public void selectDesigns(List<Design> designList) {
        if (selectChildDesigns) {
            selectChildDesigns(designList);
        } else {
            selectParentDesigns(designList);
        }
    }

    public void prepareSelectChildDesignsActionListener(ActionEvent actionEvent) {
        prepareSelectChildDesigns();
    }

    public void prepareSelectChildDesigns() {
        clearSelectFiltersAndResetSelectDataModel();
        selectChildDesigns = true;
    }

    public void selectChildDesigns(List<Design> childDesignList) {
        Design design = getCurrent();
        List<DesignLink> childDesignLinkList = design.getChildDesignLinkList();
        for (Design childDesign : childDesignList) {
            DesignLink designLink = new DesignLink();
            designLink.setParentDesign(design);
            designLink.setChildDesign(childDesign);
            childDesignLinkList.add(designLink);
        }
    }

    public void saveChildDesignList() {
        update();
    }

    public void deleteChildDesignLink(DesignLink childDesignLink) {
        Design design = getCurrent();
        List<DesignLink> childDesignLinkList = design.getChildDesignLinkList();
        childDesignLinkList.remove(childDesignLink);
    }

    public void prepareSelectParentDesignsActionListener(ActionEvent actionEvent) {
        prepareSelectParentDesigns();
    }

    public void prepareSelectParentDesigns() {
        clearSelectFiltersAndResetSelectDataModel();
        selectChildDesigns = false;
    }

    public void selectParentDesigns(List<Design> parentDesignList) {
        Design design = getCurrent();
        List<DesignLink> parentDesignLinkList = design.getParentDesignLinkList();
        for (Design parentDesign : parentDesignList) {
            DesignLink designLink = new DesignLink();
            designLink.setParentDesign(parentDesign);
            designLink.setChildDesign(design);
            parentDesignLinkList.add(designLink);
        }
    }

    public void saveParentDesignList() {
        update();
    }

    public void deleteParentDesignLink(DesignLink parentDesignLink) {
        Design design = getCurrent();
        List<DesignLink> parentDesignLinkList = design.getParentDesignLinkList();
        parentDesignLinkList.remove(parentDesignLink);
    }

    public void saveDesignElementList() {
        update();
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByOwnerUser = settingTypeMap.get(FilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(FilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(FilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(FilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(FilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(FilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayOwnerUser = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByOwnerUser = sessionUser.getUserSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = sessionUser.getUserSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = sessionUser.getUserSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = sessionUser.getUserSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
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

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        sessionUser.setUserSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        sessionUser.setUserSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        sessionUser.setUserSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        sessionUser.setUserSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        sessionUser.setUserSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);
    }

    public boolean getSelectChildDesigns() {
        return selectChildDesigns;
    }

    public void setSelectChildDesigns(boolean selectChildDesigns) {
        this.selectChildDesigns = selectChildDesigns;
    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }

    @FacesConverter(value = "designConverter", forClass = Design.class)
    public static class DesignControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                DesignController controller = (DesignController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "designController");
                return controller.getEntity(getKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to design object.");
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
            if (object instanceof Design) {
                Design o = (Design) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Design.class.getName());
            }
        }

    }

    public DataTable getDesignPropertyValueListDataTable() {
        return designPropertyValueListDataTable;
    }

    public void setDesignPropertyValueListDataTable(DataTable designPropertyValueListDataTable) {
        this.designPropertyValueListDataTable = designPropertyValueListDataTable;
    }

    public List<PropertyValue> getFilteredPropertyValueList() {
        return filteredPropertyValueList;
    }

    public void setFilteredPropertyValueList(List<PropertyValue> filteredPropertyValueList) {
        this.filteredPropertyValueList = filteredPropertyValueList;
    }

}
