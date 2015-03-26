package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.beans.DesignElementDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Design;
import gov.anl.aps.cdb.portal.model.db.beans.DesignDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import gov.anl.aps.cdb.portal.model.db.entities.DesignElement;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.DesignElementUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
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
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

@Named("designController")
@SessionScoped
public class DesignController extends CrudEntityController<Design, DesignDbFacade> implements Serializable {

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

    private DataTable designPropertyValueListDataTable = null;

    private List<PropertyValue> filteredPropertyValueList;

    @EJB
    private DesignDbFacade designFacade;

    @EJB
    private DesignElementDbFacade designElementFacade;

    public DesignController() {
        super();
    }

    @Override
    protected DesignDbFacade getFacade() {
        return designFacade;
    }

    @Override
    protected Design createEntityInstance() {
        Design design = new Design();
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        design.setEntityInfo(entityInfo);
        return design;
    }

    @Override
    public Design cloneEntityInstance(Design design) {
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        Design clonedDesign = design.copyAndSetEntityInfo(entityInfo);
        setLogText("Cloned from " + design.getName());
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
        if (logText != null && !logText.isEmpty()) {
            Log logEntry = LogUtility.createLogEntry(logText);
            List<Log> logList = new ArrayList<>();
            logList.add(logEntry);
            design.setLogList(logList);
            resetLogText();
        }
        logger.debug("Inserting new design " + design.getName() + " (user: "
                + entityInfo.getCreatedByUser().getUsername() + ")");
    }

    @Override
    public void prepareEntityUpdate(Design design) throws CdbException {
        String designName = design.getName();
        if (designName == null || designName.isEmpty()) {
            throw new ObjectAlreadyExists("Design name cannot be empty.");
        }
        Design existingDesign = designFacade.findByName(design.getName());
        if (existingDesign != null && !existingDesign.getId().equals(design.getId())) {
            throw new ObjectAlreadyExists("Design " + design.getName() + " already exists.");
        }

        EntityInfo entityInfo = design.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);
        if (logText != null && !logText.isEmpty()) {
            Log logEntry = LogUtility.createLogEntry(logText);
            design.getLogList().add(logEntry);
            resetLogText();
        }

        for (DesignElement designElement : design.getDesignElementList()) {
            String designElementName = designElement.getName();
            if (designElementName == null || designElementName.isEmpty()) {
                throw new InvalidObjectState("Design element name cannot be empty.");
            }
            if (designElement.getComponent() != null && designElement.getChildDesign() != null) {
                throw new InvalidObjectState("Design element cannot have both component and child design.");
            }
            DesignElement existingDesignElement = designElementFacade.findByNameAndParentDesign(designElementName, design);
            if (existingDesignElement != null && !existingDesignElement.getId().equals(designElement.getId())) {
                throw new ObjectAlreadyExists("Design element with name " + designElementName
                        + " already exists.");
            }

            // If id is null, this is a new design element; check its properties
            if (designElement.getId() == null) {
                UserInfo createdByUser = designElement.getEntityInfo().getCreatedByUser();
                Date createdOnDateTime = designElement.getEntityInfo().getCreatedOnDateTime();
                designElement.updateDynamicProperties(createdByUser, createdOnDateTime);
            }

        }

        // Catch circular dependency issues.
        DesignElementUtility.createDesignElementRoot(design);
        prepareDesignImageList(design);
        logger.debug("Updating design " + design.getName()
                + " (user: " + entityInfo.getLastModifiedByUser().getUsername() + ")");
    }

    @Override
    public void prepareEntityUpdateOnRemoval(Design design) {
        EntityInfo entityInfo = design.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);
        prepareDesignImageList(design);
    }

    @Override
    public Design findById(Integer id) {
        return designFacade.findById(id);
    }

    @Override
    public EntityInfo getEntityInfo(Design entity) {
        if (entity != null) {
            return entity.getEntityInfo();
        }
        return null;
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
        updateOnRemoval();
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
        updateOnRemoval();
    }

    public void saveDesignElementList() {
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
            if (settingsTimestamp == null || sessionUser.areUserSettingsModifiedAfterDate(settingsTimestamp)) {
                updateSettingsFromSessionUser(sessionUser);
                settingsTimestamp = new Date();
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

    public Boolean getDisplayDesignImages() {
        List<PropertyValue> designImageList = getDesignImageList();
        return (designImageList != null && !designImageList.isEmpty());
    }

    public List<PropertyValue> prepareDesignImageList(Design design) {
        List<PropertyValue> designImageList = PropertyValueUtility.prepareImagePropertyValueList(design.getPropertyValueList());
        design.setImagePropertyList(designImageList);
        return designImageList;
    }

    public List<PropertyValue> getDesignImageList() {
        Design design = getCurrent();
        List<PropertyValue> designImageList = design.getImagePropertyList();
        if (designImageList == null) {
            designImageList = prepareDesignImageList(design);
        }
        return designImageList;
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
