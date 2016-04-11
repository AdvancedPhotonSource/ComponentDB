/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.constants.DesignElementType;
import gov.anl.aps.cdb.portal.model.db.beans.DesignElementDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Design;
import gov.anl.aps.cdb.portal.model.db.beans.DesignDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import gov.anl.aps.cdb.portal.model.db.entities.DesignElement;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.DesignElementUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.DesignUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;

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
import org.primefaces.model.TreeNode;

/**
 * Design controller class.
 */
@Named("designController")
@SessionScoped
public class DesignController extends CdbAbstractDomainEntityController<Design, DesignDbFacade> implements Serializable {

    /*
     * Controller specific settings
     */
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Design.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "Design.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "Design.List.Display.Description";
    private static final String DisplayOwnerUserSettingTypeKey = "Design.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "Design.List.Display.OwnerGroup";
    private static final String DisplayCreatedByUserSettingTypeKey = "Design.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "Design.List.Display.CreatedOnDateTime";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "Design.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "Design.List.Display.LastModifiedOnDateTime";
    private static final String DisplayPropertyTypeId1SettingTypeKey = "Design.List.Display.PropertyTypeId1";
    private static final String DisplayPropertyTypeId2SettingTypeKey = "Design.List.Display.PropertyTypeId2";
    private static final String DisplayPropertyTypeId3SettingTypeKey = "Design.List.Display.PropertyTypeId3";
    private static final String DisplayPropertyTypeId4SettingTypeKey = "Design.List.Display.PropertyTypeId4";
    private static final String DisplayPropertyTypeId5SettingTypeKey = "Design.List.Display.PropertyTypeId5";
    private static final String DisplayRowExpansionSettingTypeKey = "Design.List.Display.RowExpansion";
    private static final String DisplayDesignElementRowExpansionSettingTypeKey = "Design.List.Display.DesignElement.RowExpansion";
    private static final String LoadRowExpansionPropertyValueSettingTypeKey = "Design.List.Load.RowExpansionPropertyValue";
    private static final String LoadDesignElementRowExpansionPropertyValueSettingTypeKey = "Design.List.Load.DesignElement.RowExpansionPropertyValue";
    private static final String FilterByNameSettingTypeKey = "Design.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "Design.List.FilterBy.Description";
    private static final String FilterByOwnerUserSettingTypeKey = "Design.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "Design.List.FilterBy.OwnerGroup";
    private static final String FilterByCreatedByUserSettingTypeKey = "Design.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "Design.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "Design.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "Design.List.FilterBy.LastModifiedOnDateTime";
    private static final String FilterByPropertyValue1SettingTypeKey = "Design.List.FilterBy.PropertyValue1";
    private static final String FilterByPropertyValue2SettingTypeKey = "Design.List.FilterBy.PropertyValue2";
    private static final String FilterByPropertyValue3SettingTypeKey = "Design.List.FilterBy.PropertyValue3";
    private static final String FilterByPropertyValue4SettingTypeKey = "Design.List.FilterBy.PropertyValue4";
    private static final String FilterByPropertyValue5SettingTypeKey = "Design.List.FilterBy.PropertyValue5";
    private static final String FilterByPropertiesAutoLoadTypeKey = "ComponentInstance.List.AutoLoad.FilterBy.Properties";
    private static final String DisplayListPageHelpFragmentSettingTypeKey = "Design.Help.ListPage.Display.Fragment";

    private static final Logger logger = Logger.getLogger(DesignController.class.getName());
   
    private TreeNode designElementListTreeTableRootNode = null;
    
    private Boolean loadDesignElementRowExpansionPropertyValues = null; 
    private Boolean displayDesignElementRowExpansion = null; 

    private List<PropertyValue> filteredPropertyValueList;

    @EJB
    private DesignDbFacade designFacade;

    @EJB
    private DesignElementDbFacade designElementFacade;

    private List<Design> selectDesignCandidateList = null;

    public DesignController() {
        super();
    }

    @Override
    protected DesignDbFacade getEntityDbFacade() {
        return designFacade;
    }

    @Override
    protected Design createEntityInstance() {
        Design design = new Design();
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        design.setEntityInfo(entityInfo);

        selectDesignCandidateList = null;
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
    public String getDisplayEntityTypeName() {
        return "Design";
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

    void prepareDesignElementListTreeTable(Design design) {
        try {
            designElementListTreeTableRootNode = DesignElementUtility.createDesignElementRoot(design);
        } catch (CdbException ex) {
            logger.warn("Could not create design element list for tree view: " + ex.toString());
        }
    }

    @Override
    public void prepareEntityView(Design design) {
        prepareDesignElementListTreeTable(design);
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
            if (designElement.getEditDesignElementType() != null) {
                if (designElement.getEditDesignElementType().equals(DesignElementType.DESIGN.toString())) {
                    designElement.setComponent(null);
                } else if (designElement.getEditDesignElementType().equals(DesignElementType.COMPONENT.toString())) {
                    designElement.setChildDesign(null);
                }
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

        // Compare properties with what is in the db
        List<PropertyValue> originalPropertyValueList = designFacade.findById(design.getId()).getPropertyValueList();
        List<PropertyValue> newPropertyValueList = design.getPropertyValueList();
        logger.debug("Verifying properties for design " + design);
        PropertyValueUtility.preparePropertyValueHistory(originalPropertyValueList, newPropertyValueList, entityInfo);
        prepareImageList(design);
        logger.debug("Updating design " + design.getName()
                + " (user: " + entityInfo.getLastModifiedByUser().getUsername() + ")");
    }

    @Override
    public void prepareEntityUpdateOnRemoval(Design design) {
        EntityInfo entityInfo = design.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);
        prepareImageList(design);
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

    public void prepareAddDesignElement(Design design) {
        List<DesignElement> designElementList = design.getDesignElementList();
        DesignElement designElement = new DesignElement();
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        designElement.setEntityInfo(entityInfo);
        designElement.setParentDesign(design);
        designElementList.add(0, designElement);
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

    public boolean getDisplayDesignElementList() {
        Design currentDesign = getCurrent();
        List<DesignElement> designElementList = currentDesign.getDesignElementList();
        return designElementList != null && !designElementList.isEmpty();
    }

    @Override
    public void prepareEntityListForSelection(List<Design> selectEntityList) {
        // Need to prevent selecting current design, or any children or parents.
        Design currentDesign = getCurrent();
        selectEntityList.remove(currentDesign);

    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        super.updateSettingsFromSettingTypeDefaults(settingTypeMap);
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
        
        displayRowExpansion = Boolean.parseBoolean(settingTypeMap.get(DisplayRowExpansionSettingTypeKey).getDefaultValue());
        displayDesignElementRowExpansion = Boolean.parseBoolean(settingTypeMap.get(DisplayDesignElementRowExpansionSettingTypeKey).getDefaultValue());
        loadRowExpansionPropertyValues = Boolean.parseBoolean(settingTypeMap.get(LoadRowExpansionPropertyValueSettingTypeKey).getDefaultValue());
        loadDesignElementRowExpansionPropertyValues = Boolean.parseBoolean(settingTypeMap.get(LoadDesignElementRowExpansionPropertyValueSettingTypeKey).getDefaultValue());

        displayPropertyTypeId1 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId1SettingTypeKey).getDefaultValue());
        displayPropertyTypeId2 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId2SettingTypeKey).getDefaultValue());
        displayPropertyTypeId3 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId3SettingTypeKey).getDefaultValue());
        displayPropertyTypeId4 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId4SettingTypeKey).getDefaultValue());
        displayPropertyTypeId5 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId5SettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByOwnerUser = settingTypeMap.get(FilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(FilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(FilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(FilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(FilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(FilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();

        filterByPropertyValue1 = settingTypeMap.get(FilterByPropertyValue1SettingTypeKey).getDefaultValue();
        filterByPropertyValue2 = settingTypeMap.get(FilterByPropertyValue2SettingTypeKey).getDefaultValue();
        filterByPropertyValue3 = settingTypeMap.get(FilterByPropertyValue3SettingTypeKey).getDefaultValue();
        filterByPropertyValue4 = settingTypeMap.get(FilterByPropertyValue4SettingTypeKey).getDefaultValue();
        filterByPropertyValue5 = settingTypeMap.get(FilterByPropertyValue5SettingTypeKey).getDefaultValue();
        filterByPropertiesAutoLoad = Boolean.parseBoolean(settingTypeMap.get(FilterByPropertiesAutoLoadTypeKey).getDefaultValue());

        displayListPageHelpFragment = Boolean.parseBoolean(settingTypeMap.get(DisplayListPageHelpFragmentSettingTypeKey).getDefaultValue());

        resetDomainEntityPropertyTypeIdIndexMappings();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        super.updateSettingsFromSessionUser(sessionUser);
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
        
        displayRowExpansion = sessionUser.getUserSettingValueAsBoolean(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        displayDesignElementRowExpansion = sessionUser.getUserSettingValueAsBoolean(DisplayDesignElementRowExpansionSettingTypeKey, displayDesignElementRowExpansion);
        loadRowExpansionPropertyValues = sessionUser.getUserSettingValueAsBoolean(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);
        loadDesignElementRowExpansionPropertyValues = sessionUser.getUserSettingValueAsBoolean(LoadDesignElementRowExpansionPropertyValueSettingTypeKey, loadDesignElementRowExpansionPropertyValues);

        displayPropertyTypeId1 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        displayPropertyTypeId2 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        displayPropertyTypeId3 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        displayPropertyTypeId4 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        displayPropertyTypeId5 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByOwnerUser = sessionUser.getUserSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = sessionUser.getUserSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = sessionUser.getUserSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = sessionUser.getUserSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        filterByPropertyValue1 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        filterByPropertyValue2 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        filterByPropertyValue3 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        filterByPropertyValue4 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        filterByPropertyValue5 = sessionUser.getUserSettingValueAsString(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);
        filterByPropertiesAutoLoad = sessionUser.getUserSettingValueAsBoolean(FilterByPropertiesAutoLoadTypeKey, filterByPropertiesAutoLoad);

        displayListPageHelpFragment = sessionUser.getUserSettingValueAsBoolean(DisplayListPageHelpFragmentSettingTypeKey, displayListPageHelpFragment);

        resetDomainEntityPropertyTypeIdIndexMappings();

    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        super.saveSettingsForSessionUser(sessionUser);
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
        
        sessionUser.setUserSettingValue(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        sessionUser.setUserSettingValue(DisplayDesignElementRowExpansionSettingTypeKey, displayDesignElementRowExpansion);
        sessionUser.setUserSettingValue(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);
        sessionUser.setUserSettingValue(LoadDesignElementRowExpansionPropertyValueSettingTypeKey, loadDesignElementRowExpansionPropertyValues);

        sessionUser.setUserSettingValue(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        sessionUser.setUserSettingValue(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);
        
        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        sessionUser.setUserSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        sessionUser.setUserSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        sessionUser.setUserSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        sessionUser.setUserSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        sessionUser.setUserSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        sessionUser.setUserSettingValue(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        sessionUser.setUserSettingValue(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        sessionUser.setUserSettingValue(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        sessionUser.setUserSettingValue(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        sessionUser.setUserSettingValue(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);
        sessionUser.setUserSettingValue(FilterByPropertiesAutoLoadTypeKey, filterByPropertiesAutoLoad);

        sessionUser.setUserSettingValue(DisplayListPageHelpFragmentSettingTypeKey, displayListPageHelpFragment);
    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }

    /**
     * Converter class for design objects.
     */
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
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to design object.");
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
            if (object instanceof Design) {
                Design o = (Design) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Design.class.getName());
            }
        }

    }

    public List<Design> getSelectDesignCandidateList() {
        if (selectDesignCandidateList == null) {
            selectDesignCandidateList = getAvailableItems();
        }
        return selectDesignCandidateList;
    }

    @Override
    public String getDisplayListPageHelpFragmentSettingTypeKey() {
        return DisplayListPageHelpFragmentSettingTypeKey;
    }

    public List<Design> completeDesign(String query) {
        return DesignUtility.filterDesign(query, getSelectDesignCandidateList());
    }

    public List<PropertyValue> getFilteredPropertyValueList() {
        return filteredPropertyValueList;
    }

    public void setFilteredPropertyValueList(List<PropertyValue> filteredPropertyValueList) {
        this.filteredPropertyValueList = filteredPropertyValueList;
    }

    public Boolean getLoadDesignElementRowExpansionPropertyValues() {
        return loadDesignElementRowExpansionPropertyValues;
    }

    public void setLoadDesignElementRowExpansionPropertyValues(Boolean loadDesignElementRowExpansionPropertyValues) {
        this.loadDesignElementRowExpansionPropertyValues = loadDesignElementRowExpansionPropertyValues;
    }

    public Boolean getDisplayDesignElementRowExpansion() {
        return displayDesignElementRowExpansion;
    }

    public void setDisplayDesignElementRowExpansion(Boolean displayDesignElementRowExpansion) {
        this.displayDesignElementRowExpansion = displayDesignElementRowExpansion;
    }

    public TreeNode getDesignElementListTreeTableRootNode() {
        return designElementListTreeTableRootNode;
    }

    public TreeNode getDesignElementListTreeTableRootNode(DesignElement parent) {

        if (parent.getChildDesignElementListTreeTableRootNode()== null) {
            try{
                if(parent.getChildDesign() != null){
                    parent.setChildDesignElementListTreeTableRootNode(DesignElementUtility.createDesignElementRoot(parent.getChildDesign()));
                }
            } catch (CdbException ex) {
                logger.debug(ex);
            }
        }
        
        return parent.getChildDesignElementListTreeTableRootNode(); 
    }

    public void setDesignElementListTreeTableRootNode(TreeNode designElementListTreeTableRootNode) {
        this.designElementListTreeTableRootNode = designElementListTreeTableRootNode;
    }

}
