/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Binder;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Form;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.ReleasedForms;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Traveler;
import gov.anl.aps.cdb.portal.plugins.support.traveler.view.objects.CreatedFromTemplateSummaryObject;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
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
    private List<CreatedFromTemplateSummaryObject> selectedTravelersCreatedFromTemplate = null;
    private Form selectedTemplateForTravelersCreatedFromTemplate = null;

    private boolean travelersForTemplateArchiveOptionsEnabled = false;
    private boolean showArchived = false; 

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
        prepareTravlersCreatedFromTemplate(false); 
    }

    public void prepareTravlersCreatedFromTemplate(boolean showArchived) {
        travelersCreatedFromTemplate = new ArrayList<>();
        selectedTravelersCreatedFromTemplate = null; 
        travelersForTemplateArchiveOptionsEnabled = false;
        this.showArchived = showArchived; 
        
        List<Item> itemList = getItemListForTravelersCreatedFromTemplate(); 

        String templateId = selectedTemplateForTravelersCreatedFromTemplate.getId();
        String latestVer = "";
        try {
            ReleasedForms releasedFormsCreatedFromForm = travelerApi.getReleasedFormsCreatedFromForm(templateId);
            int releases = releasedFormsCreatedFromForm.getReleasedForms().size();
            if (releases > 0) {
                latestVer = releasedFormsCreatedFromForm.getReleasedForms().get(releases - 1).getVer();
            }
        } catch (Exception ex) {
            SessionUtility.addErrorMessage("Error fetching template", "Could not fetch released form details: " + templateId);
        }

        for (Item item : itemList) {
            List<Traveler> travelerList = new ArrayList<>();
            loadPropertyTravelerInstanceList(item.getPropertyValueInternalList(), travelerList);

            populateTravelersCreatedFromTemplate(templateId, travelerList, item, latestVer);

            List<Binder> binderList = new ArrayList<>();
            loadPropertyTravelerBinderList(item.getPropertyValueInternalList(), binderList);

            for (Binder binder : binderList) {
                loadTravelerListForBinder(binder);
                List<Traveler> binderTravelerList = binder.getTravelerList();
                populateTravelersCreatedFromTemplate(templateId, binderTravelerList, item, latestVer);
            }

        }
    }
    
    protected List<Item> getItemListForTravelersCreatedFromTemplate() {
        Item current = getCurrent();
        return current.getDerivedFromItemList();
    }

    private void populateTravelersCreatedFromTemplate(String templateId, List<Traveler> travelerList, Item item, String latestVersion) {
        for (Traveler traveler : travelerList) {
            String referenceFormId = getReferenceFormId(traveler);
            if (referenceFormId != null) {
                if (referenceFormId.equals(templateId)) {
                    CreatedFromTemplateSummaryObject obj;
                    if (traveler.isArchived() == showArchived) {
                        obj = new CreatedFromTemplateSummaryObject(item, traveler, latestVersion);
                        travelersCreatedFromTemplate.add(obj);
                    }
                }
            }
        }
    }
    
    public void archiveSelectedTravelersCreatedFromTemplate() {
        for (CreatedFromTemplateSummaryObject obj : selectedTravelersCreatedFromTemplate) {
            Traveler traveler = obj.getTraveler();
            String id = traveler.getId();
            try { 
                travelerApi.updateTravelerArchived(id, !showArchived);
            } catch (CdbException ex) {
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }
        }
        
        prepareTravlersCreatedFromTemplate();
    }

    public void selectAllTravelersForTemplate(boolean noInput) {
        resetTravelersForTemplateSelection();
        for (CreatedFromTemplateSummaryObject obj : travelersCreatedFromTemplate) {
            // Nonlatest
            if (noInput) {
                Traveler traveler = obj.getTraveler();
                if (traveler.getFinishedInput() > 0) {
                    continue;
                }
            }
            selectedTravelersCreatedFromTemplate.add(obj);
        }
    }

    public void selectTravelersForTemplate(boolean latest, boolean noInput) {
        resetTravelersForTemplateSelection();

        for (CreatedFromTemplateSummaryObject obj : travelersCreatedFromTemplate) {
            String referenceReleasedFormVer = obj.getTraveler().getReferenceReleasedFormVer();
            String latestFormVersion = obj.getLatestFormVersion();

            if (referenceReleasedFormVer.equals(latestFormVersion) == latest) {
                // Nonlatest
                if (noInput) {
                    Traveler traveler = obj.getTraveler();
                    if (traveler.getFinishedInput() > 0) {
                        continue;
                    }
                }
                selectedTravelersCreatedFromTemplate.add(obj);
            }
        }
    }

    public void resetTravelersForTemplateSelection() {
        selectedTravelersCreatedFromTemplate = new ArrayList<>();
    }

    public boolean isTravelersForTemplateArchiveOptionsEnabled() {
        return travelersForTemplateArchiveOptionsEnabled;
    }

    public boolean isTravelersForTemplateArchiveCollapsed() {
        return !travelersForTemplateArchiveOptionsEnabled;
    }

    public void setTravelersForTemplateArchiveCollapsed(boolean travelersForTemplateArchiveOptionsEnabled) {
        // Do nothing
    }
    
    public void toggleArchivedPanel() {
        travelersForTemplateArchiveOptionsEnabled = !travelersForTemplateArchiveOptionsEnabled;        
        
        if (showArchived) {
            prepareTravlersCreatedFromTemplate();
        }
        
        resetTravelersForTemplateSelection();         
    }
    
    public String getArchiveMode() {
        if (showArchived) {
            return "Unarchive"; 
        }
        return "Archive"; 
    }

    public boolean isShowArchived() {
        return showArchived;
    }

    public void toggleShowArchived() {
        showArchived = !showArchived;
        
        prepareTravlersCreatedFromTemplate(showArchived);
        travelersForTemplateArchiveOptionsEnabled = true; 
    }

    public List<CreatedFromTemplateSummaryObject> getSelectedTravelersCreatedFromTemplate() {
        return selectedTravelersCreatedFromTemplate;
    }

    public void setSelectedTravelersCreatedFromTemplate(List<CreatedFromTemplateSummaryObject> selectedTravelersCreatedFromTemplate) {
        this.selectedTravelersCreatedFromTemplate = selectedTravelersCreatedFromTemplate;
    }

}
