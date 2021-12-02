/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemControllerExtensionHelper;
import gov.anl.aps.cdb.portal.controllers.PropertyTypeController;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeCategoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;

/**
 *
 * @author djarosz
 */
public abstract class ItemEnforcedPropertiesController extends ItemControllerExtensionHelper {

    protected String ENFORCED_CONFIGURATION_PROPERTY_TYPE_NAME = "itemPropertyRequirements";

    @EJB
    protected PropertyTypeCategoryFacade propertyTypeCategoryFacade;

    @EJB
    protected PropertyTypeFacade propertyTypeFacade;

    @EJB
    protected PropertyValueFacade propertyValueFacade;

    protected List<PropertyTypeCategory> elevatedCategories = null;

    protected PropertyTypeCategory selectedPropertyTypeCategory = null;

    protected List<EnforcedPropertyInformation> possibleEnforcedPropertyInformation = null;

    protected PropertyType enforcedConfigurationPropertyType = null;

    protected PropertyValue itemEnforcedConfigurationPropertyValue = null;

    protected boolean renderDisplayPropertyRequirementsOnView = false;

    protected abstract String[] getElevatedCategoryNames();

    @PostConstruct
    public void initialize() {
        getItemController().subscribeResetVariablesForCurrent(this);
        getItemController().subscribePrepareInsertForCurrent(this);
    }

    public void revertChangesMadeToEnforcedPropertiesForCurrent() {
        PropertyValue propertyValue = getItemEnforcedConfigurationPropertyValue();
        if (propertyValue.getId() != null) {
            int propertyValueId = propertyValue.getId();
            PropertyValue dbStoredPropertyValue = propertyValueFacade.find(propertyValueId);
            propertyValue.setValue(dbStoredPropertyValue.getValue());
            propertyValue.setPropertyMetadataList(dbStoredPropertyValue.getPropertyMetadataList());
        } else {
            // Record does not yet exist in DB. 
            Item item = getCurrent();
            for (int i = 0; i < item.getPropertyValueList().size(); i++) {
                PropertyValue itemPV = item.getPropertyValueList().get(i);
                if (itemPV.getPropertyType().equals(getEnfrocedConfigurationPropertyType())) {
                    item.getPropertyValueList().remove(i);
                    break;
                }
            }
        }

        resetExtensionVariablesForCurrent();

    }

    public String saveChangesMadeToEnforcedPropertiesForCurrent() {
        prepareSaveChangesMadeToEnforcedPropertiesForCurrent();

        return update();
    }

    protected void prepareSaveChangesMadeToEnforcedPropertiesForCurrent() {
        Item item = getCurrent();        
        List<PropertyType> requiredPropertyTypeListForItem = getRequiredPropertyTypeListForItem(item);
        
        if (requiredPropertyTypeListForItem != null) {        
            if (item.getPropertyValueList() == null) {
                item.setPropertyValueList(new ArrayList<>());
            }

            // Remove property types that do not need to be added. 
            for (PropertyValue propertyValue : item.getPropertyValueList()) {
                PropertyType propertyType = propertyValue.getPropertyType();
                if (requiredPropertyTypeListForItem.contains(propertyType)) {
                    requiredPropertyTypeListForItem.remove(propertyType);
                }
            }

            for (PropertyType propertyType : requiredPropertyTypeListForItem) {
                preparePropertyTypeValueAdd(propertyType);
            }
        }
    }

    public boolean isItemHasEditableEnforcedProperties() {
        String[] elevatedCategories = getElevatedCategoryNames();
        return (elevatedCategories != null && elevatedCategories.length != 0);
    }

    @Override
    public void resetExtensionVariablesForCurrent() {
        super.resetExtensionVariablesForCurrent();

        elevatedCategories = null;
        selectedPropertyTypeCategory = null;
        possibleEnforcedPropertyInformation = null;
        itemEnforcedConfigurationPropertyValue = null;
        renderDisplayPropertyRequirementsOnView = false;
    }

    public boolean isCatalogAllowedDomain(PropertyType propertyType) {
        return isDomainInPropertyType(ItemDomainName.catalog.getValue(), propertyType);

    }

    public boolean isInventoryAllowedDomain(PropertyType propertyType) {
        return isDomainInPropertyType(ItemDomainName.inventory.getValue(), propertyType);
    }

    private boolean isDomainInPropertyType(String domainName, PropertyType propertyType) {
        for (Domain domain : propertyType.getAllowedDomainList()) {
            if (domain.getName().equals(domainName)) {
                return true;
            }
        }
        return false;
    }

    public boolean getIsRequiredForInventoryItem(PropertyType propertyType) {
        return getIsRequiredForCurrent(propertyType, ItemDomainName.INVENTORY_ID);
    }

    public boolean getIsRequiredForCatalogItem(PropertyType propertyType) {
        return getIsRequiredForCurrent(propertyType, ItemDomainName.CATALOG_ID);
    }

    private boolean getIsRequiredForCurrent(PropertyType propertyType, int domainId) {
        PropertyValue propertyValue = getItemEnforcedConfigurationPropertyValue();
        String key = generatePropertyTypeMetadataKey(propertyType, domainId);
        String value = propertyValue.getPropertyMetadataValueForKey(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return false;
    }

    public void setIsRequiredForInventoryItem(PropertyType propertyType, boolean isRequired) {
        setIsRequiredForCurrent(propertyType, ItemDomainName.INVENTORY_ID, isRequired);
    }

    public void setIsRequiredForCatalogItem(PropertyType propertyType, boolean isRequired) {
        setIsRequiredForCurrent(propertyType, ItemDomainName.CATALOG_ID, isRequired);
    }

    private void setIsRequiredForCurrent(PropertyType propertyType, int domainId, boolean boolVal) {
        PropertyValue propertyValue = getItemEnforcedConfigurationPropertyValue();
        String key = generatePropertyTypeMetadataKey(propertyType, domainId);
        String value = Boolean.toString(boolVal);
        propertyValue.setPropertyMetadataValue(key, value);
    }

    private String generatePropertyTypeMetadataKey(PropertyType propertyType, int domainId) {
        return propertyType.getId() + "-" + domainId;
    }

    public List<PropertyType> getRequiredPropertyTypeListForItem(Item item) {
        int reqDomainId = getDomainId();
        return getRequiredPropertyTypeListForItem(item, reqDomainId);
    }

    protected List<PropertyType> getRequiredPropertyTypeListForItem(Item item, int requiredByDomainId) {
        PropertyValue propertyValue = getItemEnforcedConfigurationPropertyValue(item);
        if (propertyValue != null) {
            List<PropertyMetadata> propertyMetadataList = propertyValue.getPropertyMetadataList();

            List<PropertyType> propertyTypes = new ArrayList<>();

            if (propertyMetadataList != null) {
                for (PropertyMetadata propertyMetadata : propertyMetadataList) {
                    String metadataValue = propertyMetadata.getMetadataValue();
                    boolean isRequired = Boolean.parseBoolean(metadataValue);
                    if (isRequired) {
                        String metadataKey = propertyMetadata.getMetadataKey();
                        String[] split = metadataKey.split("-");
                        int domainId = Integer.parseInt(split[1]);
                        if (domainId != requiredByDomainId) {
                            continue;
                        }

                        int propertyTypeId = Integer.parseInt(split[0]);
                        PropertyType propertyType = propertyTypeFacade.findById(propertyTypeId);
                        if (propertyType != null) {
                            propertyTypes.add(propertyType);
                        }
                    }
                }
            }
            return propertyTypes;
        }
        return null;

    }

    public PropertyValue getItemEnforcedConfigurationPropertyValue(Item item) {
        if (item.getPropertyValueList() != null) {
            for (PropertyValue propertyValue : item.getPropertyValueList()) {
                String propertyTypeName = propertyValue.getPropertyType().getName();
                if (propertyTypeName.equals(ENFORCED_CONFIGURATION_PROPERTY_TYPE_NAME)) {
                    return propertyValue;
                }
            }
        }
        return null;
    }

    public PropertyValue getItemEnforcedConfigurationPropertyValue() {
        if (itemEnforcedConfigurationPropertyValue == null) {
            Item item = getCurrent();
            if (item.getPropertyValueList() == null) {
                item.setPropertyValueList(new ArrayList<>());
            }

            itemEnforcedConfigurationPropertyValue = getItemEnforcedConfigurationPropertyValue(item);

            if (itemEnforcedConfigurationPropertyValue == null) {
                itemEnforcedConfigurationPropertyValue = getItemController().preparePropertyTypeValueAdd(getEnfrocedConfigurationPropertyType());
            }
        }
        return itemEnforcedConfigurationPropertyValue;
    }

    public List<PropertyTypeCategory> getElevatedCategories() {
        if (elevatedCategories == null) {
            elevatedCategories = new ArrayList<>();
            for (String categoryName : getElevatedCategoryNames()) {
                PropertyTypeCategory propertyTypeCategory = propertyTypeCategoryFacade.findByName(categoryName);
                elevatedCategories.add(propertyTypeCategory);
            }
        }
        return elevatedCategories;
    }

    public List<EnforcedPropertyInformation> getPossibleEnforcedPropertyInformation() {
        if (possibleEnforcedPropertyInformation == null) {
            possibleEnforcedPropertyInformation = new ArrayList<>();
            List<PropertyType> propertyTypes = propertyTypeFacade.findByPropertyTypeCategory(getSelectedPropertyTypeCategory());
            for (PropertyType possiblePropertyType : propertyTypes) {
                if (isInventoryAllowedDomain(possiblePropertyType)
                        || isCatalogAllowedDomain(possiblePropertyType)) {
                    possibleEnforcedPropertyInformation.add(new EnforcedPropertyInformation(possiblePropertyType, this));
                }
            }
        }
        return possibleEnforcedPropertyInformation;
    }

    public boolean renderCategorySelection() {
        return getElevatedCategories().size() > 1;
    }

    public PropertyTypeCategory getSelectedPropertyTypeCategory() {
        if (selectedPropertyTypeCategory == null) {
            if (getElevatedCategories().size() > 0) {
                selectedPropertyTypeCategory = getElevatedCategories().get(0);
            }
        }
        return selectedPropertyTypeCategory;
    }

    public void setSelectedPropertyTypeCategory(PropertyTypeCategory selectedPropertyTypeCategory) {
        this.selectedPropertyTypeCategory = selectedPropertyTypeCategory;
    }

    public PropertyType getEnfrocedConfigurationPropertyType() {
        if (enforcedConfigurationPropertyType == null) {
            enforcedConfigurationPropertyType = propertyTypeFacade.findByName(ENFORCED_CONFIGURATION_PROPERTY_TYPE_NAME);
            if (enforcedConfigurationPropertyType == null) {
                PropertyTypeController propertyTypeController = PropertyTypeController.getInstance();
                propertyTypeController.prepareCreate();
                enforcedConfigurationPropertyType = propertyTypeController.getCurrent();
                enforcedConfigurationPropertyType.setName(ENFORCED_CONFIGURATION_PROPERTY_TYPE_NAME);
                enforcedConfigurationPropertyType.setIsInternal(true);
                propertyTypeController.create(true);
            }
        }

        return enforcedConfigurationPropertyType;
    }

    public boolean isRenderDisplayPropertyRequirementsOnView() {
        return renderDisplayPropertyRequirementsOnView;
    }

    public void setRenderDisplayPropertyRequirementsOnView(boolean renderDisplayPropertyRequirementsOnView) {
        this.renderDisplayPropertyRequirementsOnView = renderDisplayPropertyRequirementsOnView;
    }

    public class EnforcedPropertyInformation {

        private ItemEnforcedPropertiesController itemEnforcedPropertiesController;
        protected PropertyType propertyType;
        protected Boolean isCatalogRequired = null;
        protected Boolean isInventoryRequired = null;

        public EnforcedPropertyInformation(PropertyType propertyType, ItemEnforcedPropertiesController itemEnforcedPropertiesController) {
            this.itemEnforcedPropertiesController = itemEnforcedPropertiesController;
            this.propertyType = propertyType;
        }

        public PropertyType getPropertyType() {
            return propertyType;
        }

        public boolean isIsCatalogRequired() {
            if (isCatalogRequired == null) {
                isCatalogRequired = itemEnforcedPropertiesController.getIsRequiredForCatalogItem(propertyType);
            }
            return isCatalogRequired;
        }

        public void setIsCatalogRequired(boolean isCatalogRequired) {
            if (this.isCatalogRequired != isCatalogRequired) {
                this.isCatalogRequired = isCatalogRequired;
                itemEnforcedPropertiesController.setIsRequiredForCatalogItem(propertyType, isCatalogRequired);
            }
        }

        public boolean isIsInventoryRequired() {
            if (isInventoryRequired == null) {
                isInventoryRequired = itemEnforcedPropertiesController.getIsRequiredForInventoryItem(propertyType);
            }
            return isInventoryRequired;
        }

        public void setIsInventoryRequired(boolean isInventoryRequired) {
            if (this.isInventoryRequired != isInventoryRequired) {
                this.isInventoryRequired = isInventoryRequired;
                itemEnforcedPropertiesController.setIsRequiredForInventoryItem(propertyType, isInventoryRequired);
            }
        }
    }
}
