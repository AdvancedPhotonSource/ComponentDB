/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Binder;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Form;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Traveler;
import gov.anl.aps.cdb.portal.plugins.support.traveler.view.objects.CreatedFromTemplateSummaryObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public abstract class ItemTravelerDomainTemplateControllerBase extends ItemTravelerController {
    
    private boolean isDisplayMultiEditTravelerTemplate = false;

    private Boolean applyAllCreateNew = null;

    private List<CreatedFromTemplateSummaryObject> travelersCreatedFromTemplate = null;
    private Form selectedTemplateForTravelersCreatedFromTemplate = null;

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

        Item current = getCurrent();
        List<Item> itemList = current.getDerivedFromItemList();

        String templateId = selectedTemplateForTravelersCreatedFromTemplate.getId();

        for (Item item : itemList) {
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
