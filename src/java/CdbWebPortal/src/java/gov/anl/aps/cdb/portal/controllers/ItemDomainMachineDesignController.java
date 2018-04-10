/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.KeyValueObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainMachineDesignController.controllerNamed)
@SessionScoped
public class ItemDomainMachineDesignController extends ItemController<ItemDomainMachineDesign, ItemDomainMachineDesignFacade, ItemDomainMachineDesignSettings> {

    public final static String controllerNamed = "itemDomainMachineDesignController";

    // <editor-fold defaultstate="collapsed" desc="Element edit variables ">
    private Boolean createCatalogElement = null;
    private Boolean machineDesignItemCreateFromTemplate = null;
    private Item inventoryForElement = null;
    private Item catalogForElement = null;
    private Item originalForElement = null;
    private ItemDomainMachineDesign templateForElement = null;
    protected DataModel installedInventorySelectionForCurrentElement;
    protected DataModel machineDesignTemplatesSelectionList;
    private DataModel topLevelMachineDesignSelectionList;
    private List<KeyValueObject> machineDesignNameList = null;
    private List<String> nameParts = null;
    private String machineDesignName = null;
    private boolean displayAssignToDataTable = false;
    private boolean displayCreateItemElementContent = false;
    private boolean displayCreateMachineDesignFromTemplateContent = false;
    // </editor-fold>

    private List<String> selectedListDisplayOptions = null;
    private boolean templateRelationshipInfoLoaded = false;
    private Item createdFromTemplateForCurrentItem = null;
    private List<Item> machineDesignItemsCreatedFromCurrent = null;

    @EJB
    ItemDomainMachineDesignFacade itemDomainMachineDesignFacade;

    @EJB
    RelationshipTypeFacade relationshipTypeFacade;

    public ItemDomainMachineDesign getInstance() {
        return (ItemDomainMachineDesign) SessionUtility.findBean(controllerNamed);
    }

    public boolean getCurrentHasInventoryItem() {
        return !isCurrentItemTemplate();
    }

    public boolean isCurrentItemTemplate() {
        return current.getIsItemTemplate();
    }

    private String getMachineDesignTemplateRelationshipName() {
        return ItemElementRelationshipTypeNames.machineDesignTemplate.getValue();
    }

    public List<Item> getMachineDesignItemsCreatedFromCurrent() {
        if (!templateRelationshipInfoLoaded) {
            if (isCurrentItemTemplate()) {
                if (current != null) {
                    String machineDesignTemplateRelationshipTypeName = getMachineDesignTemplateRelationshipName();
                    machineDesignItemsCreatedFromCurrent = new ArrayList<>();

                    for (ItemElementRelationship ier : current.getItemElementRelationshipList1()) {
                        if (ier.getRelationshipType().getName().equals(machineDesignTemplateRelationshipTypeName)) {
                            Item parentItem = ier.getFirstItemElement().getParentItem();
                            machineDesignItemsCreatedFromCurrent.add(parentItem);
                        }
                    }
                }
                templateRelationshipInfoLoaded = true;
            }
        }
        return machineDesignItemsCreatedFromCurrent;
    }

    public Item getCreatedFromTemplateForCurrentItem() {
        if (!templateRelationshipInfoLoaded) {
            if (!isCurrentItemTemplate()) {
                if (current != null) {
                    String machineDesignTemplateRelationshipTypeName = getMachineDesignTemplateRelationshipName();
                    for (ItemElementRelationship ier : current.getItemElementRelationshipList()) {
                        if (ier.getRelationshipType().getName().equals(machineDesignTemplateRelationshipTypeName)) {
                            createdFromTemplateForCurrentItem = ier.getSecondItemElement().getParentItem();
                        }
                    }

                }
                templateRelationshipInfoLoaded = true;
            }
        }
        return createdFromTemplateForCurrentItem;
    }

    public boolean verifyValidTemplateName(String templateName, boolean printMessage) {
        boolean validTitle = false;
        if (templateName.contains("{")) {
            int openBraceIndex = templateName.indexOf("{");
            int closeBraceIndex = templateName.indexOf("}");
            if (openBraceIndex < closeBraceIndex) {
                validTitle = true;
            }
        }
        if (!validTitle && printMessage) {
            SessionUtility.addWarningMessage(
                    "Template names require parameters",
                    "Place parements within {} in template name. Example: 'templateName {paramName}'");

        }

        return validTitle;
    }

    public String prepareCreateTemplate() {
        String createRedirect = super.prepareCreate();

        ItemDomainMachineDesign current = getCurrent();
        String templateEntityTypeName = EntityTypeName.template.getValue();
        EntityType templateEntityType = entityTypeFacade.findByName(templateEntityTypeName);
        try {
            current.setEntityTypeList(new ArrayList<>());
        } catch (CdbException ex) {
            Logger.getLogger(ItemDomainMachineDesignController.class.getName()).log(Level.SEVERE, null, ex);
        }
        current.getEntityTypeList().add(templateEntityType);

        return createRedirect;

    }

    // <editor-fold defaultstate="collapsed" desc="Element creation implementation ">   
    // <editor-fold defaultstate="collapsed" desc="Functionality">
    public void machineDesignElementAddTypeSelectionChange(String intermediateStepCommand, String finalStepCommand) {
        if (!createCatalogElement) {
            if (isCurrentItemTemplate()) {
                machineDesignItemCreateFromTemplate = false;
                machineDesignItemCreateFromTemplateChange(finalStepCommand);
                return;
            }
        }

        SessionUtility.executeRemoteCommand(intermediateStepCommand);
    }

    public void machineDesignItemCreateFromTemplateChange(String finalStepCommand) {
        if (!machineDesignItemCreateFromTemplate) {
            // Create New 
            ItemDomainMachineDesign newMachineDesign = createEntityInstance();

            if (isCurrentItemTemplate()) {
                try {
                    List<EntityType> entityTypeList = new ArrayList<>();
                    EntityType templateEntity = entityTypeFacade.findByName(EntityTypeName.template.getValue());
                    entityTypeList.add(templateEntity);
                    newMachineDesign.setEntityTypeList(entityTypeList);
                } catch (CdbException ex) {
                    Logger.getLogger(ItemDomainMachineDesignController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            currentEditItemElement.setContainedItem(newMachineDesign);

            SessionUtility.executeRemoteCommand(finalStepCommand);
        }
    }

    public void newMachineDesignElementContainedItemValueChanged() {
        String name = currentEditItemElement.getContainedItem().getName();
        if (!name.equals("")) {
            if (isCurrentItemTemplate()) {
                if (!verifyValidTemplateName(name, true)) {
                    currentEditItemElementSaveButtonEnabled = false;
                    return;
                }
            }
            if (itemDomainMachineDesignFacade.findByName(name).size() != 0) {
                SessionUtility.addWarningMessage("Non-unique name", "Please change the name and try again");
                currentEditItemElementSaveButtonEnabled = false;
            } else {
                currentEditItemElementSaveButtonEnabled = true;
            }
        } else {
            currentEditItemElementSaveButtonEnabled = false;
        }

    }

    public void setMachineDesignItemCreateFromTemplate(Boolean machineDesignItemCreateFromTemplate) {
        this.machineDesignItemCreateFromTemplate = machineDesignItemCreateFromTemplate;
    }

    public void updateInstalledInventoryItem() {
        boolean updateNecessary = false;
        Item currentContainedItem = currentEditItemElement.getContainedItem();

        if (inventoryForElement != null) {
            if (currentContainedItem.equals(inventoryForElement)) {
                SessionUtility.addInfoMessage("No update", "Inventory selected is same as before");
            } else if (verifyValidUnusedInventoryItem(inventoryForElement)) {
                updateNecessary = true;
                currentEditItemElement.setContainedItem(inventoryForElement);
            }
        } else if (currentContainedItem.getDomain().getId() == ItemDomainName.INVENTORY_ID) {
            // Item is unselected, select catalog item
            updateNecessary = true;
            currentEditItemElement.setContainedItem(currentContainedItem.getDerivedFromItem());
        } else {
            SessionUtility.addInfoMessage("No update", "Inventory item not selected");
        }

        if (updateNecessary) {
            ItemElementController itemElementController = ItemElementController.getInstance();
            itemElementController.setCurrent(currentEditItemElement);
            itemElementController.update();
        }

        resetItemElementEditVariables();
    }

    private boolean verifyValidUnusedInventoryItem(Item inventoryItem) {
        for (ItemElement itemElement : inventoryItem.getItemElementMemberList()) {
            Item item = itemElement.getParentItem();
            if (item instanceof ItemDomainMachineDesign) {
                SessionUtility.addWarningMessage("Inventory item used",
                        "Inventory item cannot be saved, used in: " + item.toString());
                return false;
            }
        }

        return true;

    }

    @Override
    public void cancelCreateSingleItemElementSimpleDialog() {
        super.cancelCreateSingleItemElementSimpleDialog();
        resetItemElementEditVariables();
    }

    public void prepareCreateMachineDesignFromTemplate() {
        resetItemElementEditVariables();
        displayCreateMachineDesignFromTemplateContent = true;
        templateForElement = currentEditItemElement.getMachineDesignItem();
        generateTemplateForElementMachineDesignNameVars();
    }

    public void createMachineDesignFromTemplate(String onSucess) {
        try {
            if (!allValuesForTitleGenerationsFilledIn()) {
                SessionUtility.addWarningMessage("Missing data", "Please fill in all name parameters");
                return;
            }

            createMachineDesignFromTemplateForEditItemElement();
            ItemDomainMachineDesign newItem = (ItemDomainMachineDesign) currentEditItemElement.getContainedItem();
            // Create item
            performCreateOperations(newItem, false, true);

            // Update element 
            ItemElementController instance = ItemElementController.getInstance();
            instance.setCurrent(currentEditItemElement);
            instance.update();

            SessionUtility.executeRemoteCommand(onSucess);
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
        } catch (CloneNotSupportedException ex) {
            SessionUtility.addErrorMessage("Error", ex.getMessage());
        }
    }

    public void prepareUpdateInstalledInventoryItem() {
        resetItemElementEditVariables();
        displayAssignToDataTable = true;
        catalogForElement = currentEditItemElement.getCatalogItem();
    }

    public DataModel getInstalledInventorySelectionForCurrentElement() {
        if (installedInventorySelectionForCurrentElement == null) {
            if (catalogForElement != null) {
                List<Item> derivedFromItemList = catalogForElement.getDerivedFromItemList();
                installedInventorySelectionForCurrentElement = new ListDataModel(derivedFromItemList);
            }

        }
        return installedInventorySelectionForCurrentElement;
    }

    public DataModel getMachineDesignTemplatesSelectionList() {
        if (machineDesignTemplatesSelectionList == null) {
            List<ItemDomainMachineDesign> machineDesignTemplates = itemDomainMachineDesignFacade.getMachineDesignTemplates();
            machineDesignTemplatesSelectionList = new ListDataModel(machineDesignTemplates);
        }
        return machineDesignTemplatesSelectionList;
    }

    public DataModel getTopLevelMachineDesignSelectionList() {
        if (topLevelMachineDesignSelectionList == null) {
            List<ItemDomainMachineDesign> itemsWithoutParents = getItemsWithoutParents();
            List<ItemElement> itemElementMemberList = current.getItemElementMemberList();

            if (itemElementMemberList != null) {
                if (itemElementMemberList.size() == 0) {
                    // current item has no parents
                    itemsWithoutParents.remove(current);
                } else {
                    // Be definition machine design item should only have one parent
                    Item parentItem = null;

                    while (itemElementMemberList.size() != 0) {
                        ItemElement parentElement = itemElementMemberList.get(0);
                        parentItem = parentElement.getParentItem();

                        itemElementMemberList = parentItem.getItemElementMemberList();
                    }

                    itemsWithoutParents.remove(parentItem);
                }
            }

            removeTemplatesFromList(itemsWithoutParents);

            topLevelMachineDesignSelectionList = new ListDataModel(itemsWithoutParents);
        }
        return topLevelMachineDesignSelectionList;
    }

    private void removeTemplatesFromList(List<ItemDomainMachineDesign> itemList) {
        String templateEntityName = EntityTypeName.template.getValue();
        EntityType templateEntityType = entityTypeFacade.findByName(templateEntityName);

        int index = 0;
        while (index < itemList.size()) {
            Item item = itemList.get(index);
            if (item.getEntityTypeList().contains(templateEntityType)) {
                itemList.remove(index);
            } else {
                index++;
            }
        }
    }

    private void removeMachineDesignFromList(List<ItemDomainMachineDesign> itemList) {
        String templateEntityName = EntityTypeName.template.getValue();
        EntityType templateEntityType = entityTypeFacade.findByName(templateEntityName);

        int index = 0;
        while (index < itemList.size()) {
            Item item = itemList.get(index);
            // Does not contain template entity type
            if (!item.getEntityTypeList().contains(templateEntityType)) {
                itemList.remove(index);
            } else {
                index++;
            }
        }
    }

    public void resetItemElementEditVariables() {
        currentEditItemElementSaveButtonEnabled = false;
        displayAssignToDataTable = false;
        displayCreateItemElementContent = false;
        displayCreateMachineDesignFromTemplateContent = false;

        installedInventorySelectionForCurrentElement = null;
        createCatalogElement = null;
        machineDesignItemCreateFromTemplate = null;
        inventoryForElement = null;
        catalogForElement = null;
        inventoryForElement = null;
        templateForElement = null;
        machineDesignTemplatesSelectionList = null;
        topLevelMachineDesignSelectionList = null;
        machineDesignNameList = null;
        machineDesignName = null;
        nameParts = null;
    }

    @Override
    public void prepareCreateSingleItemElementSimpleDialog() {
        super.prepareCreateSingleItemElementSimpleDialog();
        resetItemElementEditVariables();
        displayCreateItemElementContent = true;
    }

    public void verifyItemElementContainedItemSelection() {
        if (currentEditItemElement != null) {
            if (createCatalogElement) {
                if (catalogForElement != null || inventoryForElement != null) {
                            currentEditItemElementSaveButtonEnabled = true;
                        }
            } else if (!createCatalogElement) {
                // Machine design
                if (machineDesignItemCreateFromTemplate == null) {
                    if (currentEditItemElement.getContainedItem() != null) {
                        currentEditItemElementSaveButtonEnabled = true;
                    }
                } else if (machineDesignItemCreateFromTemplate) {
                    if (templateForElement != null) {
                        generateTemplateForElementMachineDesignNameVars();
                    }
                }
            }
        }
    }

    private void generateTemplateForElementMachineDesignNameVars() {
        String name = templateForElement.getName();
        int firstVar = name.indexOf('{');
        int secondVar;

        machineDesignNameList = new ArrayList<>();
        nameParts = new ArrayList<>();

        while (firstVar != -1) {
            nameParts.add(name.substring(0, firstVar));
            name = name.substring(firstVar);
            secondVar = name.indexOf('}');

            String key = name.substring(1, secondVar);

            KeyValueObject keyValue = new KeyValueObject(key);

            machineDesignNameList.add(keyValue);

            name = name.substring(secondVar + 1);

            firstVar = name.indexOf('{');
        }
        generateMachineDesignName();
    }

    public void titleGenerationValueChange() {
        generateMachineDesignName();

        currentEditItemElementSaveButtonEnabled = allValuesForTitleGenerationsFilledIn();
    }

    private boolean allValuesForTitleGenerationsFilledIn() {
        for (KeyValueObject keyValue : machineDesignNameList) {
            if (keyValue.getValue() == null || keyValue.getValue().equals("")) {
                return false;
            }
        }
        return true;
    }

    public void generateMachineDesignName() {
        machineDesignName = "";
        for (int i = 0; i < nameParts.size(); i++) {
            machineDesignName += nameParts.get(i);
            KeyValueObject keyValue = machineDesignNameList.get(i);

            if (keyValue.getValue() != null && !keyValue.getValue().equals("")) {
                machineDesignName += keyValue.getValue();
            } else {
                machineDesignName += "{" + keyValue.getKey() + "}";
            }
        }
    }

    @Override
    public void beforeValidateItemElement() throws CloneNotSupportedException, CdbException {
        super.beforeValidateItemElement();
        if (createCatalogElement) {
            originalForElement = currentEditItemElement.getContainedItem();
            if (inventoryForElement != null) {
                if (verifyValidUnusedInventoryItem(inventoryForElement)) {
                    currentEditItemElement.setContainedItem(inventoryForElement);    
                } else {
                    throw new CdbException("Inventory item selected has already been used."); 
                }                
            } else if (catalogForElement != null) {
                currentEditItemElement.setContainedItem(catalogForElement);
            }
        } else if (!createCatalogElement) {
            if (machineDesignItemCreateFromTemplate == null) {

            } else if (machineDesignItemCreateFromTemplate) {
                createMachineDesignFromTemplateForEditItemElement();
            }

            ItemDomainMachineDesign containedItem = (ItemDomainMachineDesign) currentEditItemElement.getContainedItem();
            checkItem(containedItem);
        }
    }

    private void createMachineDesignFromTemplateForEditItemElement() throws CdbException, CloneNotSupportedException {
        cloneProperties = true;
        cloneCreateItemElementPlaceholders = false;

        ItemDomainMachineDesign clone = (ItemDomainMachineDesign) templateForElement.clone();
        cloneCreateItemElements(clone, templateForElement, true);
        clone.setName(machineDesignName);

        RelationshipType templateRelationship
                = relationshipTypeFacade.findByName(getMachineDesignTemplateRelationshipName());

        // Create item element relationship between the template and the clone 
        ItemElementRelationship itemElementRelationship = new ItemElementRelationship();
        itemElementRelationship.setRelationshipType(templateRelationship);
        itemElementRelationship.setFirstItemElement(clone.getSelfElement());
        itemElementRelationship.setSecondItemElement(templateForElement.getSelfElement());

        clone.setItemElementRelationshipList(new ArrayList<>());
        clone.getItemElementRelationshipList().add(itemElementRelationship);

        clone.setEntityTypeList(new ArrayList<>());
        currentEditItemElement.setContainedItem(clone);
    }

    @Override
    public void failedValidateItemElement() {
        super.failedValidateItemElement();
        currentEditItemElement.setContainedItem(originalForElement);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Accessors">
    public boolean isDisplayCreateItemElementContent() {
        return displayCreateItemElementContent;
    }

    public boolean isDisplayAssignToDataTable() {
        return displayAssignToDataTable;
    }

    public Boolean getCreateCatalogElement() {
        return createCatalogElement;
    }

    public void setCreateCatalogElement(Boolean createCatalogElement) {
        this.createCatalogElement = createCatalogElement;
    }

    public Boolean getMachineDesignItemCreateFromTemplate() {
        return machineDesignItemCreateFromTemplate;
    }

    public String getMachineDesignName() {
        return machineDesignName;
    }

    public List<KeyValueObject> getMachineDesignNameList() {
        return machineDesignNameList;
    }

    public Item getInventoryForElement() {
        return inventoryForElement;
    }

    public void setInventoryForElement(Item inventoryForElement) {
        this.inventoryForElement = inventoryForElement;
    }

    public Item getCatalogForElement() {
        return catalogForElement;
    }

    public void setCatalogForElement(Item catalogForElement) {
        this.catalogForElement = catalogForElement;
    }

    public ItemDomainMachineDesign getTemplateForElement() {
        return templateForElement;
    }

    public void setTemplateForElement(ItemDomainMachineDesign templateForElement) {
        this.templateForElement = templateForElement;
    }

    public boolean isDisplayCreateMachineDesignFromTemplateContent() {
        return displayCreateMachineDesignFromTemplateContent;
    }

    // </editor-fold>    // </editor-fold>
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Base class overrides">               
    @Override
    public void processPreRenderList() {
        super.processPreRenderList();

        if (selectedListDisplayOptions == null) {
            selectedListDisplayOptions = new ArrayList<>();
            selectedListDisplayOptions.add("t");
            selectedListDisplayOptions.add("m");
        }
    }

    @Override
    public void createListDataModel() {
        if (selectedListDisplayOptions.size() == 0) {
            setListDataModel(new ListDataModel());
        } else if (!selectedListDisplayOptions.contains("c")) {
            // No children
            List<ItemDomainMachineDesign> itemsWithoutParents = getItemsWithoutParents();
            if (!selectedListDisplayOptions.contains("t")) {
                // remove templates 
                removeTemplatesFromList(itemsWithoutParents);
            } else if (!selectedListDisplayOptions.contains("m")) {
                // remove machine desings 
                removeMachineDesignFromList(itemsWithoutParents);
            }

            setListDataModel(new ListDataModel(itemsWithoutParents));

        } else // with Children 
        {
            if (selectedListDisplayOptions.size() == 1) {
                // Only children is selected 
                setListDataModel(new ListDataModel());
            } else {
                List<ItemDomainMachineDesign> itemList = getItemList();
                if (!selectedListDisplayOptions.contains("t")) {
                    // remove templates                
                    removeTemplatesFromList(itemList);
                } else if (!selectedListDisplayOptions.contains("m")) {
                    removeMachineDesignFromList(itemList);
                }
                setListDataModel(new ListDataModel(itemList));
            }
        }

    }

    @Override
    protected void checkItem(ItemDomainMachineDesign item) throws CdbException {
        super.checkItem(item);

        if (item.getIsItemTemplate()) {
            if (!verifyValidTemplateName(item.getName(), false)) {
                throw new CdbException("Place parements within {} in template name. Example: 'templateName {paramName}'");
            }
        }
    }

    @Override
    protected void resetVariablesForCurrent() {
        super.resetVariablesForCurrent();

        createdFromTemplateForCurrentItem = null;
        templateRelationshipInfoLoaded = false;
        machineDesignItemsCreatedFromCurrent = null;

        resetItemElementEditVariables();
    }

    @Override
    protected ItemDomainMachineDesign instenciateNewItemDomainEntity() {
        return new ItemDomainMachineDesign();
    }

    @Override
    protected ItemDomainMachineDesignSettings createNewSettingObject() {
        return new ItemDomainMachineDesignSettings(this);
    }

    @Override
    protected ItemDomainMachineDesignFacade getEntityDbFacade() {
        return itemDomainMachineDesignFacade;
    }

    @Override
    public String getEntityTypeName() {
        return "itemMachineDesign";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Machine Design Item";
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.machineDesign.getValue();
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemName() {
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
    public boolean getEntityDisplayItemGallery() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return false;
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
        return false;
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return true;
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStyleName() {
        return "machineDesign";
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // </editor-fold>
    public List<String> getSelectedListDisplayOptions() {
        return selectedListDisplayOptions;
    }

    public void setSelectedListDisplayOptions(List<String> selectedListDisplayOptions) {
        this.selectedListDisplayOptions = selectedListDisplayOptions;
        listDataModel = null;
    }

}
