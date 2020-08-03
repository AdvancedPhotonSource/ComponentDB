/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Binder;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Form;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Traveler;
import gov.anl.aps.cdb.portal.plugins.support.traveler.view.objects.CreatedFromTemplateSummaryObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemTravelerDomainMachineDesignController.controllerNamed)
@SessionScoped
public class ItemTravelerDomainMachineDesignController extends ItemTravelerController implements Serializable {

    public final static String controllerNamed = "itemTravelerDomainMachineDesignController";

    private ItemDomainMachineDesignController itemDomainMachineDesignController = null;

    private boolean isDisplayMultiEditTravelerTemplate = false;

    private Boolean applyAllCreateNew = null;

    private List<CreatedFromTemplateSummaryObject> travelersCreatedFromTemplate = null;
    private Form selectedTemplateForTravelersCreatedFromTemplate = null;

    @Override
    protected ItemController getItemController() {
        if (itemDomainMachineDesignController == null) {
            itemDomainMachineDesignController = ItemDomainMachineDesignController.getInstance();
        }
        return itemDomainMachineDesignController;
    }

    public boolean getIsCollapsedTravelerTemplates() {
        List<Form> templatesForCurrent = getTemplatesForCurrent();

        return !(templatesForCurrent.size() > 0);
    }

    public boolean isIsDisplayMultiEditTravelerTemplate() {
        return isDisplayMultiEditTravelerTemplate;
    }

    public void setIsDisplayMultiEditTravelerTemplate(boolean isDisplayMultiEditTravelerTemplate) {
        this.isDisplayMultiEditTravelerTemplate = isDisplayMultiEditTravelerTemplate;
    }

    public Boolean getApplyAllCreateNew() {
        return applyAllCreateNew;
    }

    public void setApplyAllCreateNew(Boolean applyAllCreateNew) {
        this.applyAllCreateNew = applyAllCreateNew;
    }

    public void prepareMultiEditAppplyTempalteToAllItems() {
        applyAllCreateNew = null;
        multiEditSelectedTemplate = null;
        loadActiveTravelerTemplates();
    }

    @Override
    public void resetExtensionVariablesForCurrent() {
        super.resetExtensionVariablesForCurrent();
        travelersCreatedFromTemplate = null;
        selectedTemplateForTravelersCreatedFromTemplate = null;
    }

    public Form getSelectedTemplateForTravelersCreatedFromTemplate() {
        return selectedTemplateForTravelersCreatedFromTemplate;
    }

    public void setSelectedTemplateForTravelersCreatedFromTemplate(Form selectedTemplateForTravelersCreatedFromTemplate) {
        this.selectedTemplateForTravelersCreatedFromTemplate = selectedTemplateForTravelersCreatedFromTemplate;
    }

    public List<CreatedFromTemplateSummaryObject> getTravelersCreatedFromTemplate() {
        return travelersCreatedFromTemplate;
    }

    public void prepareTravlersCreatedFromTemplate() {
        travelersCreatedFromTemplate = new ArrayList<>();

        ItemDomainCatalog current = (ItemDomainCatalog) getCurrent();
        List<ItemDomainInventory> inventoryItemList = current.getInventoryItemList();

        String templateId = selectedTemplateForTravelersCreatedFromTemplate.getId();

        for (int i = 0; i < inventoryItemList.size(); i++) {
            Item item = inventoryItemList.get(i);
            List<Traveler> travelerList = new ArrayList<>();
            loadPropertyTravelerInstanceList(item.getPropertyValueInternalList(), travelerList);

            populateTravelersCreatedFromTemplate(templateId, travelerList, item);

            List<Binder> binderList = new ArrayList<>();
            loadPropertyTravelerBinderList(item.getPropertyValueInternalList(), binderList);

            for (Binder binder : binderList) {
                loadTravelerListForBinder(binder);
                List<Traveler> binderTravelerList = binder.getTravelerList();
                populateTravelersCreatedFromTemplate(templateId, binderTravelerList, item);
            }

        }
    }

    private void populateTravelersCreatedFromTemplate(String templateId, List<Traveler> travelerList, Item item) {
        for (Traveler traveler : travelerList) {
            String referenceFormId = getReferenceFormId(traveler);
            if (referenceFormId != null) {
                if (referenceFormId.equals(templateId)) {
                    CreatedFromTemplateSummaryObject obj;
                    if (traveler.isArchived() == false) {
                        obj = new CreatedFromTemplateSummaryObject(item, traveler);
                        travelersCreatedFromTemplate.add(obj);                        
                    } 
                }
            }
        }
    }
    
}
