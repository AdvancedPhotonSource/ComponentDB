/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.LoginController;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.AuthorizationUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.KeyValueObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.model.TreeNode;

/**
 *
 * @author djarosz
 */
@Named(ItemMultiEditDomainMachineDesignController.controllerNamed)
@SessionScoped
public class ItemMultiEditDomainMachineDesignController extends ItemMultiEditController implements Serializable {

    public final static String controllerNamed = "itemMultiEditDomainMachineDesignController";

    protected boolean updateFulfillment = false;    

    private ItemDomainMachineDesignController itemDomainMachineDesignController = null;
    private ItemDomainMachineDesignControllerUtility itemDomainMachineDesignControllerUtility; 

    public ItemDomainMachineDesignController getItemDomainMachineDesignController() {
        if (itemDomainMachineDesignController == null) {
            itemDomainMachineDesignController = ItemDomainMachineDesignController.getInstance();
        }
        return itemDomainMachineDesignController;
    }

    public ItemDomainMachineDesignControllerUtility getItemDomainMachineDesignControllerUtility() {
        if (itemDomainMachineDesignControllerUtility == null) {
            itemDomainMachineDesignControllerUtility = new ItemDomainMachineDesignControllerUtility(); 
        }
        return itemDomainMachineDesignControllerUtility;
    }

    @Override
    protected ItemController getItemController() {
        return getItemDomainMachineDesignController();
    }

    @Override
    protected String getControllerNamedConstant() {
        return controllerNamed;
    }

    public boolean isCurrentViewIsTemplate() {
        return getItemDomainMachineDesignController().isCurrentViewIsTemplate();
    }

    public String getMdNodeRepIcon(ItemElement ie) {
        return getItemDomainMachineDesignController().getMdNodeRepIcon(ie);
    }

    public static ItemMultiEditDomainMachineDesignController getInstance() {
        return (ItemMultiEditDomainMachineDesignController) SessionUtility.findBean(controllerNamed);
    }

    @Override
    public void resetMultiEditVariables() {
        updateFulfillment = false; 
        super.resetMultiEditVariables();        
    }

    public TreeNode getMachineDesignTemplateRootTreeNode() {
        return getItemDomainMachineDesignController().getMachineDesignTemplateRootTreeNode();
    }
    
    public void editAllItemsCreatedFromTemplate(ItemDomainMachineDesign item) {
        resetMultiEditVariables();

        LoginController loginController = LoginController.getInstance();
        List<Item> createdFromTemplateItem = item.getItemsCreatedFromThisTemplateItemWithUnfulfilledReferences();
        List<Item> editableItemsList = new ArrayList<>();        
        for (Item derivedItem : createdFromTemplateItem) {
            if (loginController.isEntityWriteable(derivedItem.getEntityInfo())) {
                editableItemsList.add(derivedItem);
            }
        }
        
        editableItems = editableItemsList; 
        
        if (editableItemsList.size() > 25) {            
            setSelectedItemsToEdit(new ArrayList<>()); 
            setActiveIndex(MultipleEditMenu.selection.ordinal());
            multiEditMode = MultiEditMode.update;
        } else {
            setSelectedItemsToEdit(editableItemsList);             
            setActiveIndex(MultipleEditMenu.updateItems.ordinal());
            multiEditMode = MultiEditMode.update;
        }

        String desiredPath = getEntityApplicationViewPath() + "/" + EDIT_MULTIPLE_REDIRECT;
        SessionUtility.navigateTo(desiredPath);
    } 
    
    private boolean isUnfulfilledTemplate(Item item) {
        return item.getId() == null && item.getCreatedFromTemplate() != null; 
    }
    
    private boolean fulfillMachineDesign(Item item) {
        if (isUnfulfilledTemplate(item)) {
            ItemDomainMachineDesign mockItem = (ItemDomainMachineDesign) item;
            // Verify acklowledgement of fullfilment.
            if (mockItem.isAcknowledgeMultiEditFulfillment()) {
                // Create a new item from template
                ItemDomainMachineDesignControllerUtility utility;
                utility = getItemDomainMachineDesignControllerUtility();
                List<KeyValueObject> machineDesignNameList = mockItem.getMachineDesignNameList();
                ItemElement templateElement = mockItem.getCurrentHierarchyItemElement();
                ItemDomainMachineDesign templateItem = (ItemDomainMachineDesign) templateElement.getContainedItem();
                ItemDomainMachineDesign parentItem = (ItemDomainMachineDesign) templateElement.getParentItem();
                parentItem.removeItemElement(templateElement);
                                
                UserInfo user = SessionUtility.getUser();
                UserGroup ownerGroup = user.getUserGroupList().get(0);
                
                ItemElement newElement = utility.createItemElement(parentItem, user);                
                try { 
                    ItemDomainMachineDesign newMachine = utility.createMachineDesignFromTemplateHierachically(newElement, templateItem, user, ownerGroup, machineDesignNameList);
                    utility.saveNewItemElement(newElement, user);
                    // This id will be used for fetching the correct machine. 
                    mockItem.setId(newMachine.getId());
                    return true;                     
                } catch (CdbException ex) {
                    item.setPersitanceErrorMessage(ex.getErrorMessage());
                } catch (CloneNotSupportedException ex) {
                   item.setPersitanceErrorMessage(ex.getMessage());
                }                                
            } else {
                item.setPersitanceErrorMessage("Please ackowledge the fullfillment.");
                return false;                 
            }
        }
        
        return false;        
    }
       
    @Override
    public void performCreateOperations(Item item) throws CdbException, RuntimeException {
        if (isUnfulfilledTemplate(item)) {
            if (fulfillMachineDesign(item) == false) {
                throw new CdbException("Failed to fulfill machine design. Please ackowledge the fullfillment.");
            }
        } else {
            super.performCreateOperations(item);             
        }        
    }      

    @Override
    // TODO display unfulfilled elements in items created from tempalte table. 
    public boolean performSaveOperationsOnItem(Item item) {         
        // Multi-create not supported for machine design currently. 
        if (isUnfulfilledTemplate(item)) {
            return fulfillMachineDesign(item); 
        }
        
        return super.performSaveOperationsOnItem(item); 
    }

    @Override
    protected List<Item> getEditableItemsForCurrentNonAdminUser() {
        List<Item> itemList = getItemList();
        UserInfo user = (UserInfo) SessionUtility.getUser();
        
        List<Item> editableItems = new ArrayList<>(); 
        
        for (Item item : itemList) {
            EntityInfo entityInfo = item.getEntityInfo();
            if (AuthorizationUtility.isEntityWriteableByUser(entityInfo, user)) {
                editableItems.add(item); 
            }
        }
        
        return editableItems; 
    }

    @Override
    protected boolean checkCreateConfig() {
        if (derivedFromItemForNewItems == null) {
            SessionUtility.addErrorMessage("No Template Selected", "Please select a machine template item.");
            return false; 
        }
        return true; 
    }      

    public void acknowledgeAllFulfillments() {
        List<Item> selectedItemsToEdit = getSelectedItemsToEdit();
        for (Item item : selectedItemsToEdit) {
            ItemDomainMachineDesign machineItem = (ItemDomainMachineDesign) item;
            machineItem.setAcknowledgeMultiEditFulfillment(true);
        }
    } 

    @Override
    public void setActiveIndex(int activeIndex) {
        super.setActiveIndex(activeIndex); 
        
        if (activeIndex == MultipleCreateMenu.updateNewItems.ordinal()) {
            // Rerun the update fulfillment generation when needed. 
            setUpdateFulfillment(updateFulfillment); 
        }
    }
    
    public boolean isUpdateFulfillment() {
        return updateFulfillment;
    }    

    @Override
    public boolean getRenderDeleteAllButton() {
        // Machine designs are deleted using the trash functionality.
        return false; 
    }

    public void setUpdateFulfillment(boolean updateFulfillment) {
        if (updateFulfillment) {
            // Generate all of the variable name substitutions. 
            List<Item> selectedItemsToEdit = getSelectedItemsToEdit();
            for (Item item : selectedItemsToEdit) {
                if (isUnfulfilledTemplate(item)) {
                    ItemDomainMachineDesign machineItem = (ItemDomainMachineDesign) item;
                    ItemDomainMachineDesignControllerUtility utility = getItemDomainMachineDesignControllerUtility();
                    ItemDomainMachineDesign template = (ItemDomainMachineDesign) item.getCreatedFromTemplate();
                    List<KeyValueObject> nameList = utility.generateMachineDesignTemplateNameListRecursivelly(template); 
                    
                    machineItem.setMachineDesignNameList(nameList);
                    
                    // Try to autofill it based on parent.
                    List<KeyValueObject> parentNameList = null; 
                    ItemElement currentEditItemElement = machineItem.getCurrentHierarchyItemElement();
                    ItemDomainMachineDesign parentMachineDesign = (ItemDomainMachineDesign) currentEditItemElement.getParentItem();
                    if (parentMachineDesign != null) {
                        Item createdFromTemplate = parentMachineDesign.getCreatedFromTemplate();
                        if (createdFromTemplate != null) {
                            parentNameList = utility.generateMachineDesignTemplateNameListRecursivelly(template); 
                            String name = createdFromTemplate.getName();
                            String parentName = parentMachineDesign.getName();
                            
                            for (KeyValueObject parentKv : parentNameList) {
                                String parentKey = parentKv.getKey();
                                KeyValueObject new_kv = null; 
                                for (KeyValueObject name_kv : nameList) {
                                    if (name_kv.getKey().equals(parentKey)) {
                                        new_kv = name_kv; 
                                        break; 
                                    }                                    
                                }
                                
                                if (new_kv == null) {
                                    continue;
                                }
                                
                                String regEx = name.replace("{" + parentKey + "}", "(.+)"); 
                                Pattern r = Pattern.compile(regEx);

                                // Now create matcher object.
                                Matcher m = r.matcher(parentName);
                                m.find();
                                
                                try {
                                    String oldParam = m.group(1);                                    
                                    new_kv.setValue(oldParam);
                                } catch (IndexOutOfBoundsException ex) {
                                    
                                }
                            }                                                        
                        }
                    }                                        
                }                
            }
        }
        this.updateFulfillment = updateFulfillment;
    }
}
