/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.common.utilities.StringUtility;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyMetadataFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@Named("sparePartsBean")
@SessionScoped
public class SparePartsBean implements Serializable {
    
    protected static final String SPARE_PARTS_BEAN_NAME = "sparePartsBean"; 
    private static final Logger logger = Logger.getLogger(SparePartsBean.class.getName());

    protected static final String SPARE_PARTS_CONFIGURATION_PROPERTY_TYPE_NAME = "Spare Parts Configuration";
    protected static final String SPARE_PARTS_INDICATION_PROPERTY_TYPE_NAME = "Spare Part Indication";
    protected final String SPARE_PARTS_EMAIL_KEY = "email";
    protected final String SPARE_PARTS_MIN_KEY = "minQuantity";
    protected final String ITEM_INVENTORY_CONTROLLER_NAME = "itemDomainInventoryController";

    protected ItemDomainInventoryController itemDomainInventoryController = null;

    protected final String NO_EMAIL_VALUE = "None";

    protected PropertyType sparePartsConfigurationPropertyType = null;
    protected PropertyType sparePartIndicationPropertyType = null;    
    protected Item currentItem;

    protected String selectedEmailOption = null;
    protected List<String> emailOptionsList = null;

    private PropertyValue sparePartsConfigurationPropertyValue = null;

    @EJB
    private PropertyMetadataFacade propertyMetadataFacade;

    private enum EmailOptions {
        ownerEmail("Owner Email"),
        customEmail("Specified Email"),
        noNotification("None");

        private final String displayValue;

        private EmailOptions(String displayValue) {
            this.displayValue = displayValue;
        }

        public String getDisplayValue() {
            return displayValue;
        }
    };

    @EJB
    PropertyTypeFacade propertyTypeFacade;

    public void resetSparePartsVariables() {
        selectedEmailOption = null;
        sparePartsConfigurationPropertyType = null;
    }

    public void loadSparePartsConfiguration(CdbEntityController entityController, String onSuccessCommand) {
        resetSparePartsVariables();
        try {
            Item item = getCurrentCatalogItemForController(entityController);
            sparePartsConfigurationPropertyValue = prepareSparePartsConfigurationPropertyValue(item);
            RequestContext.getCurrentInstance().execute(onSuccessCommand);
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
        }
    }

    public String saveSparePartsConfiguration(CdbEntityController entityController) {
        try {
            Item item = getCurrentCatalogItemForController(entityController);
            prepareSavePartsConfiurgationForItem(item);
            ItemDomainCatalogController itemController = getItemController(entityController);
            String result = itemController.update();
            if (result != null) {
                resetSparePartsVariables();
            }
            return result;
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
        }

        return null;
    }

    public String removeSparePartsConfiguration(CdbEntityController entityController) {
        try {
            Item item = getCurrentCatalogItemForController(entityController);
            if (sparePartsConfigurationPropertyValue.getId() != null) {
                item.getPropertyValueList().remove(sparePartsConfigurationPropertyValue);
            }
            ItemDomainCatalogController itemController = getItemController(entityController);
            return itemController.update();
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
        }
        return null;
    }
    
    public static int getSparePartsMinimumForItem(Item item) {
        SparePartsBean sparePartsBean = (SparePartsBean) SessionUtility.findBean(SPARE_PARTS_BEAN_NAME); 
        return sparePartsBean.getSparePartsMinimumForItemCached(item);        
    }
    
    private int getSparePartsMinimumForItemCached(Item item) {
        if (!ObjectUtility.equals(currentItem, item)) {
            sparePartsConfigurationPropertyValue = getStoredSparePartsConfigrationPropertyValue(item);             
        }
        
        if (sparePartsConfigurationPropertyValue != null) {
            try {
                return Integer.parseInt(getSparePartsMinimumValue());
            } catch (NumberFormatException ex) {
                SessionUtility.addErrorMessage("Error", ex.getMessage());
                logger.error(ex);
                return -1; 
            }            
        }
        return -1;
    }

    /**
     * Used by the catalog item controller to identify when a property value
     * list contains configuration.
     *
     * @param item
     * @return
     */
    public static boolean isItemContainSparePartConfiguration(Item item) {
        if (item != null) {
            return getStoredSparePartsConfigrationPropertyValue(item) != null;
        }
        return false; 
    }

    private void prepareSavePartsConfiurgationForItem(Item item) throws CdbException {
        if (sparePartsConfigurationPropertyValue == null) {
            throw new CdbException("No item specified in controller.");
        }
        String minValue = getSparePartsMinimumValue();
        if (minValue == null || minValue.isEmpty()) {
            throw new CdbException("A minimum value must be specified.");
        }
        if (selectedEmailOption.equals(EmailOptions.ownerEmail.getDisplayValue())) {
            PropertyMetadata emailMetadata;
            emailMetadata = sparePartsConfigurationPropertyValue
                    .getPropertyMetadataForKey(SPARE_PARTS_EMAIL_KEY);
            if (emailMetadata != null) {
                propertyMetadataFacade.remove(emailMetadata);
                sparePartsConfigurationPropertyValue.removePropertyMetadataKey(SPARE_PARTS_EMAIL_KEY);
            }
            
        } else if (selectedEmailOption.equals(EmailOptions.noNotification.getDisplayValue())) {
            sparePartsConfigurationPropertyValue.setPropertyMetadataValue(SPARE_PARTS_EMAIL_KEY, NO_EMAIL_VALUE);
        } else {
            // custom email must be specified
            String email = getSparePartsNotificationEmail();
            if (email == null || email.isEmpty()) {
                throw new CdbException("An email must be specified or owner email selected.");
            }
            if (!StringUtility.isEmailAddressValid(email)) {
                throw new CdbException("Please enter a valid email address");
            }
        }
        if (sparePartsConfigurationPropertyValue.getId() == null) {
            item.getPropertyValueList().add(sparePartsConfigurationPropertyValue);
        }
    }

    private Item getCurrentCatalogItemForController(CdbEntityController entityController) throws CdbException {
        ItemDomainCatalogController itemController = getItemController(entityController);
        currentItem = itemController.getCurrent();
        if (currentItem == null) {
            throw new CdbException("No item specified in controller.");
        }
        return currentItem;
    }

    private ItemDomainCatalogController getItemController(CdbEntityController entityController) throws CdbException {
        if (entityController instanceof ItemDomainCatalogController) {
            return (ItemDomainCatalogController) entityController;
        } else {
            throw new CdbException(entityController.getEntityTypeName() + " Controller is not supported.");
        }

    }

    private static PropertyValue getStoredSparePartsIndicationPropertyValue(Item item) {
        return getPropertyValueByType(item, SPARE_PARTS_INDICATION_PROPERTY_TYPE_NAME);
    }

    private static PropertyValue getStoredSparePartsConfigrationPropertyValue(Item item) {
        return getPropertyValueByType(item, SPARE_PARTS_CONFIGURATION_PROPERTY_TYPE_NAME);
    }

    private static PropertyValue getPropertyValueByType(Item item, String propertyTypeName) {
        List<PropertyValue> propertyValueList = item.getPropertyValueList();
        if (propertyValueList != null && !propertyValueList.isEmpty()) {
            for (PropertyValue propertyValue : propertyValueList) {
                PropertyType propertyType = propertyValue.getPropertyType();
                if (propertyType.getName().equals(propertyTypeName)) {
                    return propertyValue;
                }
            }
        }
        return null;
    }

    private PropertyValue prepareSparePartsConfigurationPropertyValue(Item item) throws CdbException {
        // Only one property type of spare parts can be added. 
        PropertyValue propertyValue = getStoredSparePartsConfigrationPropertyValue(item);
        if (propertyValue == null) {
            // Add property value
            List<PropertyValue> propertyValueList = item.getPropertyValueList();
            propertyValue = new PropertyValue();
            propertyValue.setPropertyType(getSparePartsConfigurationPropertyType());
        }
        return propertyValue;
    }

    private PropertyType getSparePartsConfigurationPropertyType() throws CdbException {
        if (sparePartsConfigurationPropertyType == null) {
            sparePartsConfigurationPropertyType = propertyTypeFacade.findByName(SPARE_PARTS_CONFIGURATION_PROPERTY_TYPE_NAME);
            if (sparePartsConfigurationPropertyType == null) {
                throw new CdbException(SPARE_PARTS_CONFIGURATION_PROPERTY_TYPE_NAME + " property type cannot be found.");
            }
        }
        return sparePartsConfigurationPropertyType;
    }

    public PropertyType getSparePartIndicationPropertyType() throws CdbException {
        if (sparePartIndicationPropertyType == null) {
            sparePartIndicationPropertyType = propertyTypeFacade.findByName(SPARE_PARTS_INDICATION_PROPERTY_TYPE_NAME);
            if (sparePartIndicationPropertyType == null) {
                throw new CdbException(SPARE_PARTS_INDICATION_PROPERTY_TYPE_NAME + " property type cannot be found.");
            }
        }
        return sparePartIndicationPropertyType;
    }

    public List<String> getEmailOptionsList() {
        if (emailOptionsList == null) {
            emailOptionsList = new ArrayList<>();
            for (EmailOptions emailOption : EmailOptions.values()) {
                emailOptionsList.add(emailOption.getDisplayValue());
            }
        }

        return emailOptionsList;
    }

    public String getSelectedEmailOption() {
        if (selectedEmailOption == null) {
            if (sparePartsConfigurationPropertyValue != null) {
                String email = sparePartsConfigurationPropertyValue.getPropertyMetadataValueForKey(SPARE_PARTS_EMAIL_KEY);
                if (email != null && !email.isEmpty()) {
                    if (email.equals(NO_EMAIL_VALUE)) {
                        selectedEmailOption = EmailOptions.noNotification.getDisplayValue();
                    } else {
                        selectedEmailOption = EmailOptions.customEmail.getDisplayValue();
                    }
                } else {
                    selectedEmailOption = EmailOptions.ownerEmail.getDisplayValue();
                }
            }
        }
        return selectedEmailOption;
    }

    public void setSelectedEmailOption(String selectedEmailOption) {
        // Radio button selection does not have a null option. 
        if (selectedEmailOption != null) {
            this.selectedEmailOption = selectedEmailOption;
            if (selectedEmailOption.equals(EmailOptions.customEmail.getDisplayValue())) {
                String emailValue = getSparePartsNotificationEmail();
                if (emailValue != null && emailValue.equals(NO_EMAIL_VALUE)) {
                    setSparePartsNotificationEmail("");
                }
            }
        }
    }

    public String getSparePartsNotificationEmail() {
        if (getSelectedEmailOption().equals(EmailOptions.ownerEmail.getDisplayValue())) {
            UserInfo ownerUser = currentItem.getEntityInfo().getOwnerUser();
            return ownerUser.getEmail();
        } else if (getSelectedEmailOption().equals(EmailOptions.noNotification.getDisplayValue())) {
            return NO_EMAIL_VALUE;
        } else {
            // Editable value 
            return sparePartsConfigurationPropertyValue.getPropertyMetadataValueForKey(SPARE_PARTS_EMAIL_KEY);
        }
    }

    public void setSparePartsNotificationEmail(String email) {
        if (getSelectedEmailOption().equals(EmailOptions.customEmail.getDisplayValue())) {
            sparePartsConfigurationPropertyValue.setPropertyMetadataValue(SPARE_PARTS_EMAIL_KEY, email);
        }
    }

    public boolean isNotificationEmailEditDisabled() {
        return !getSelectedEmailOption().equals(EmailOptions.customEmail.getDisplayValue());
    }

    public boolean isRemoveNotificationOptionAvailable() {
        if (sparePartsConfigurationPropertyValue != null) {
            return sparePartsConfigurationPropertyValue.getId() != null;
        }
        return false;
    }

    public String getSparePartsMinimumValue() {
        return sparePartsConfigurationPropertyValue.getPropertyMetadataValueForKey(SPARE_PARTS_MIN_KEY);
    }

    public void setSparePartsMinimumValue(String minimumValue) {
        sparePartsConfigurationPropertyValue.setPropertyMetadataValue(SPARE_PARTS_MIN_KEY, minimumValue);
    }

    public boolean isRenderConfigurationPanel() {
        return sparePartsConfigurationPropertyValue != null;
    }

    public boolean getSparePartsIndication(Item inventoryItem) {
        PropertyValue propertyValue = getStoredSparePartsIndicationPropertyValue(inventoryItem);
        if (propertyValue != null) {
            return Boolean.parseBoolean(propertyValue.getValue());
        }

        return false;
    }

    public void setSparePartsIndication(Item inventoryItem) {
        PropertyValue spareIndicatorPropertyValue = getStoredSparePartsIndicationPropertyValue(inventoryItem);
        boolean currentSpareValue = inventoryItem.getSparePartIndicator();
        
        if (spareIndicatorPropertyValue == null && currentSpareValue == false) {
            // No need to save ... missing property means it is not a spare.
            return; 
        }

        if (spareIndicatorPropertyValue == null) {
            spareIndicatorPropertyValue = new PropertyValue();
            inventoryItem.getPropertyValueList().add(spareIndicatorPropertyValue);
            try {
                spareIndicatorPropertyValue.setPropertyType(getSparePartIndicationPropertyType());
            } catch (CdbException ex) {
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                return;
            }
        }
        
        Boolean previousSpareValue;
        previousSpareValue = Boolean.parseBoolean(spareIndicatorPropertyValue.getValue());
        if (previousSpareValue == currentSpareValue) {
            return;
        }
        
        try {
            updateSparePartsIndicatorPropertyValue(spareIndicatorPropertyValue, currentSpareValue);
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            return;
        }
        getItemDomainInventoryController().setCurrent(inventoryItem);
        getItemDomainInventoryController().update(); 
    }

    private void updateSparePartsIndicatorPropertyValue(PropertyValue propertyValue, boolean newValue) throws CdbException {
        UserInfo enteredByUser = (UserInfo) SessionUtility.getUser();
        if (enteredByUser == null) {
            throw new CdbException("No session user... cannot update property value. ");
        }

        propertyValue.setValue(Boolean.toString(newValue));
        propertyValue.setEnteredOnDateTime(new Date());

        propertyValue.setEnteredByUser(enteredByUser);
    }

    public ItemDomainInventoryController getItemDomainInventoryController() {
        if (itemDomainInventoryController == null) {
            itemDomainInventoryController = (ItemDomainInventoryController) SessionUtility.findBean(ITEM_INVENTORY_CONTROLLER_NAME);
        }
        return itemDomainInventoryController;
    }

}
