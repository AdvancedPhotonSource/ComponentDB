/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import static gov.anl.aps.cdb.portal.controllers.CdbEntityController.parseSettingValueAsInteger;
import gov.anl.aps.cdb.portal.model.db.beans.DomainFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FlowEvent;

/**
 *
 * @author djarosz
 */
@Named("itemDomainCatalogController")
@SessionScoped
public class ItemDomainCatalogController extends ItemController {

    private final String ENTITY_TYPE_NAME = "Component";
    private final String DOMAIN_TYPE_NAME = "Catalog";
    private final String DERIVED_DOMAIN_NAME = "Inventory";
    private final String DOMAIN_HANDLER_NAME = "Catalog";

    /*
     * Controller specific settings
     */
    private static final String DisplayCategorySettingTypeKey = "Component.List.Display.Category";
    private static final String DisplayCreatedByUserSettingTypeKey = "Component.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "Component.List.Display.CreatedOnDateTime";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Component.List.Display.NumberOfItemsPerPage";
    private static final String DisplayDescriptionSettingTypeKey = "Component.List.Display.Description";
    private static final String DisplayIdSettingTypeKey = "Component.List.Display.Id";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "Component.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "Component.List.Display.LastModifiedOnDateTime";
    private static final String DisplayModelNumberSettingTypeKey = "Component.List.Display.ModelNumber";
    private static final String DisplayOwnerUserSettingTypeKey = "Component.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "Component.List.Display.OwnerGroup";
    private static final String DisplayPropertyTypeId1SettingTypeKey = "Component.List.Display.PropertyTypeId1";
    private static final String DisplayPropertyTypeId2SettingTypeKey = "Component.List.Display.PropertyTypeId2";
    private static final String DisplayPropertyTypeId3SettingTypeKey = "Component.List.Display.PropertyTypeId3";
    private static final String DisplayPropertyTypeId4SettingTypeKey = "Component.List.Display.PropertyTypeId4";
    private static final String DisplayPropertyTypeId5SettingTypeKey = "Component.List.Display.PropertyTypeId5";
    private static final String DisplayTypeSettingTypeKey = "Component.List.Display.Type";
    private static final String DisplayRowExpansionSettingTypeKey = "Component.List.Display.RowExpansion";
    private static final String DisplayComponentInstanceRowExpansionSettingTypeKey = "Component.List.Display.ComponentInstance.RowExpansion";
    private static final String LoadRowExpansionPropertyValueSettingTypeKey = "Component.List.Load.RowExpansionPropertyValue";
    private static final String LoadComponentInstanceRowExpansionPropertyValueSettingTypeKey = "Component.List.Load.ComponentInstance.RowExpansionPropertyValue";
    private static final String FilterByCategorySettingTypeKey = "Component.List.FilterBy.Category";
    private static final String FilterByCreatedByUserSettingTypeKey = "Component.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "Component.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByDescriptionSettingTypeKey = "Component.List.FilterBy.Description";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "Component.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "Component.List.FilterBy.LastModifiedOnDateTime";
    private static final String FilterByNameSettingTypeKey = "Component.List.FilterBy.Name";
    private static final String FilterByModelNumberSettingTypeKey = "Component.List.FilterBy.ModelNumber";
    private static final String FilterByOwnerUserSettingTypeKey = "Component.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "Component.List.FilterBy.OwnerGroup";
    private static final String FilterByPropertyValue1SettingTypeKey = "Component.List.FilterBy.PropertyValue1";
    private static final String FilterByPropertyValue2SettingTypeKey = "Component.List.FilterBy.PropertyValue2";
    private static final String FilterByPropertyValue3SettingTypeKey = "Component.List.FilterBy.PropertyValue3";
    private static final String FilterByPropertyValue4SettingTypeKey = "Component.List.FilterBy.PropertyValue4";
    private static final String FilterByPropertyValue5SettingTypeKey = "Component.List.FilterBy.PropertyValue5";
    private static final String FilterByTypeSettingTypeKey = "Component.List.FilterBy.Type";
    private static final String FilterByPropertiesAutoLoadTypeKey = "Component.List.AutoLoad.FilterBy.Properties";

    private static final String DisplayListPageHelpFragmentSettingTypeKey = "Component.Help.ListPage.Display.Fragment";

    private static final Logger logger = Logger.getLogger(ItemDomainCatalogController.class.getName());

    @EJB
    private ItemFacade itemFacade;

    @EJB
    private DomainFacade domainFacade;

    private Boolean displayModelNumber = null;

    private String filterByType = null;
    private String filterByCategory = null;
    private String filterByModelNumber = null;
    private String filterBySources = null;

    private Boolean selectDisplayType = true;
    private Boolean selectDisplayCategory = true;
    private Boolean selectDisplayModelNumber = true;

    private String selectFilterByType = null;
    private String selectFilterByCategory = null;
    private String selectFilterByModelNumber = null;

    private Boolean loadComponentInstanceRowExpansionPropertyValues = null;
    private Boolean displayComponentInstanceRowExpansion = null;

    public ItemDomainCatalogController() {
        super();
    }

    @Override
    public String getEntityTypeName() {
        return "component";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Catalog Item";
    }

    public Boolean getLoadComponentInstanceRowExpansionPropertyValues() {
        return loadComponentInstanceRowExpansionPropertyValues;
    }

    @Override
    public String getDisplayListPageHelpFragmentSettingTypeKey() {
        if (getListEntityType() == null) {
            return null;
        } else {
            // TODO add setting for both entity types. 
            return DisplayListPageHelpFragmentSettingTypeKey;
        }
    }

    @Override
    protected void checkItem(Item item) throws CdbException {
        super.checkItem(item);

        //Check needed to make sure it is not attempting to save one of the instances.
        if (getItemDomainHandler().getName().equals(item.getDomain().getDomainHandler().getName())) {
            // Verify that atleast one entity type is selected.
            checkEntityTypeSpecified(item);
        }
    }

    private void checkEntityTypeSpecified(Item item) throws CdbException {
        List<EntityType> entityTypeList = item.getEntityTypeList();
        if (entityTypeList == null || entityTypeList.isEmpty()) {
            throw new CdbException("At least one entity type must be specified for a catalog item.");
        }
    }

    @Override
    public String getNextStepForCreateItemWizard(FlowEvent event) {
        String currentStep = event.getOldStep();

        if (currentStep.equals(ItemCreateWizardSteps.classification.getValue())) {
            try {
                checkEntityTypeSpecified(getCurrent());
            } catch (CdbException ex) {
                SessionUtility.addWarningMessage("Entity type not specified", ex.getErrorMessage());
                return ItemCreateWizardSteps.classification.getValue();
            }
        }

        return super.createItemWizardFlowListener(event);
    }

    @Override
    public String getFirstCreateWizardStep() {
        return ItemCreateWizardSteps.basicInformation.getValue();
    }

    @Override
    protected Item createEntityInstance() {
        Item item = super.createEntityInstance();

        // The user was on the list for the specific entity type when clickin add button. 
        if (getListEntityType() != null) {
            List<EntityType> itemEntityTypeList = item.getEntityTypeList();
            if (itemEntityTypeList == null) {
                itemEntityTypeList = new ArrayList<>();
                try {
                    item.setEntityTypeList(itemEntityTypeList);
                } catch (Exception ex) {
                }
            }
            item.getEntityTypeList().add(getListEntityType());
        }

        return item;
    }

    public void setLoadComponentInstanceRowExpansionPropertyValues(Boolean loadComponentInstanceRowExpansionPropertyValues) {
        this.loadComponentInstanceRowExpansionPropertyValues = loadComponentInstanceRowExpansionPropertyValues;
    }

    public Boolean getDisplayComponentInstanceRowExpansion() {
        return displayComponentInstanceRowExpansion;
    }

    public void setDisplayComponentInstanceRowExpansion(Boolean displayComponentInstanceRowExpansion) {
        this.displayComponentInstanceRowExpansion = displayComponentInstanceRowExpansion;
    }

    public Boolean getDisplayModelNumber() {
        return displayModelNumber;
    }

    public void setDisplayModelNumber(Boolean displayModelNumber) {
        this.displayModelNumber = displayModelNumber;
    }

    @Override
    public Boolean getDisplayItemIdentifier1() {
        return displayModelNumber;
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        super.updateSettingsFromSettingTypeDefaults(settingTypeMap);
        if (settingTypeMap == null) {
            return;
        }

        logger.debug("Updating list settings from setting type defaults");

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayItemType = Boolean.parseBoolean(settingTypeMap.get(DisplayTypeSettingTypeKey).getDefaultValue());
        displayItemCategory = Boolean.parseBoolean(settingTypeMap.get(DisplayCategorySettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());
        displayModelNumber = Boolean.parseBoolean(settingTypeMap.get(DisplayModelNumberSettingTypeKey).getDefaultValue());

        displayRowExpansion = Boolean.parseBoolean(settingTypeMap.get(DisplayRowExpansionSettingTypeKey).getDefaultValue());
        displayComponentInstanceRowExpansion = Boolean.parseBoolean(settingTypeMap.get(DisplayComponentInstanceRowExpansionSettingTypeKey).getDefaultValue());
        loadRowExpansionPropertyValues = Boolean.parseBoolean(settingTypeMap.get(LoadRowExpansionPropertyValueSettingTypeKey).getDefaultValue());
        loadComponentInstanceRowExpansionPropertyValues = Boolean.parseBoolean(settingTypeMap.get(LoadComponentInstanceRowExpansionPropertyValueSettingTypeKey).getDefaultValue());

        displayPropertyTypeId1 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId1SettingTypeKey).getDefaultValue());
        displayPropertyTypeId2 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId2SettingTypeKey).getDefaultValue());
        displayPropertyTypeId3 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId3SettingTypeKey).getDefaultValue());
        displayPropertyTypeId4 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId4SettingTypeKey).getDefaultValue());
        displayPropertyTypeId5 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId5SettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByType = settingTypeMap.get(FilterByTypeSettingTypeKey).getDefaultValue();
        filterByCategory = settingTypeMap.get(FilterByCategorySettingTypeKey).getDefaultValue();
        filterByModelNumber = settingTypeMap.get(FilterByModelNumberSettingTypeKey).getDefaultValue();
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

        logger.debug("Updating list settings from session user");

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayItemType = sessionUser.getUserSettingValueAsBoolean(DisplayTypeSettingTypeKey, displayItemType);
        displayItemCategory = sessionUser.getUserSettingValueAsBoolean(DisplayCategorySettingTypeKey, displayItemCategory);
        displayOwnerUser = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);
        displayModelNumber = sessionUser.getUserSettingValueAsBoolean(DisplayModelNumberSettingTypeKey, displayModelNumber);

        displayRowExpansion = sessionUser.getUserSettingValueAsBoolean(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        displayComponentInstanceRowExpansion = sessionUser.getUserSettingValueAsBoolean(DisplayComponentInstanceRowExpansionSettingTypeKey, displayComponentInstanceRowExpansion);
        loadRowExpansionPropertyValues = sessionUser.getUserSettingValueAsBoolean(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);
        loadComponentInstanceRowExpansionPropertyValues = sessionUser.getUserSettingValueAsBoolean(LoadRowExpansionPropertyValueSettingTypeKey, loadComponentInstanceRowExpansionPropertyValues);

        displayPropertyTypeId1 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        displayPropertyTypeId2 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        displayPropertyTypeId3 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        displayPropertyTypeId4 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        displayPropertyTypeId5 = sessionUser.getUserSettingValueAsInteger(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByType = sessionUser.getUserSettingValueAsString(FilterByTypeSettingTypeKey, filterByType);
        filterByCategory = sessionUser.getUserSettingValueAsString(FilterByCategorySettingTypeKey, filterByCategory);
        filterByModelNumber = sessionUser.getUserSettingValueAsString(FilterByModelNumberSettingTypeKey, filterByModelNumber);
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
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, Object> filters = dataTable.getFilters();
        filterByType = (String) filters.get("componentType");
        filterByCategory = (String) filters.get("componentTypeCategory");
        filterByModelNumber = (String) filters.get("modelNumber");

        filterByPropertyValue1 = (String) filters.get("propertyValue1");
        filterByPropertyValue2 = (String) filters.get("propertyValue2");
        filterByPropertyValue3 = (String) filters.get("propertyValue3");
        filterByPropertyValue4 = (String) filters.get("propertyValue4");
        filterByPropertyValue5 = (String) filters.get("propertyValue5");
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
        sessionUser.setUserSettingValue(DisplayModelNumberSettingTypeKey, displayModelNumber);

        sessionUser.setUserSettingValue(DisplayTypeSettingTypeKey, displayItemType);
        sessionUser.setUserSettingValue(DisplayCategorySettingTypeKey, displayItemCategory);

        sessionUser.setUserSettingValue(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        sessionUser.setUserSettingValue(DisplayComponentInstanceRowExpansionSettingTypeKey, displayComponentInstanceRowExpansion);
        sessionUser.setUserSettingValue(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);
        sessionUser.setUserSettingValue(LoadComponentInstanceRowExpansionPropertyValueSettingTypeKey, loadComponentInstanceRowExpansionPropertyValues);

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

        sessionUser.setUserSettingValue(FilterByTypeSettingTypeKey, filterByType);
        sessionUser.setUserSettingValue(FilterByCategorySettingTypeKey, filterByCategory);
        sessionUser.setUserSettingValue(FilterByModelNumberSettingTypeKey, filterByModelNumber);

        sessionUser.setUserSettingValue(FilterByPropertyValue1SettingTypeKey, filterByPropertyValue1);
        sessionUser.setUserSettingValue(FilterByPropertyValue2SettingTypeKey, filterByPropertyValue2);
        sessionUser.setUserSettingValue(FilterByPropertyValue3SettingTypeKey, filterByPropertyValue3);
        sessionUser.setUserSettingValue(FilterByPropertyValue4SettingTypeKey, filterByPropertyValue4);
        sessionUser.setUserSettingValue(FilterByPropertyValue5SettingTypeKey, filterByPropertyValue5);
        sessionUser.setUserSettingValue(FilterByPropertiesAutoLoadTypeKey, filterByPropertiesAutoLoad);

        sessionUser.setUserSettingValue(DisplayListPageHelpFragmentSettingTypeKey, displayListPageHelpFragment);

    }

    @Override
    public boolean getEntityDisplayItemIdentifier1() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemIdentifier2() {
        return false;
    }
    
    @Override
    public boolean getEntityDisplayItemSources() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemType() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemCategory() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return false;
    }

    @Override
    public String getItemIdentifier1Title() {
        return "Model Number";
    }

    @Override
    public String getItemIdentifier2Title() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        return "Physical Instances";
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return true;
    }

    @Override
    public String getStyleName() {
        return "designComponentCatalog";
    }

    @Override
    public Domain getDefaultDomain() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Domain getDerivedDomain() {
        return domainFacade.findByName(DERIVED_DOMAIN_NAME);
    }

    @Override
    public String getDomainHandlerName() {
        return DOMAIN_HANDLER_NAME;
    }

    @Override
    public String getListDomainName() {
        return DOMAIN_TYPE_NAME;
    }

    @Override
    public String getItemDerivedFromDomainHandlerName() {
        return null;
    }

}
