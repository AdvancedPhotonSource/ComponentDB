/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.event.FlowEvent;

/**
 *
 * @author djarosz
 */
@Named("itemCreateWizardDomainCatalogController")
@SessionScoped
public class ItemCreateWizardDomainCatalogController extends ItemCreateWizardController implements Serializable {

    ItemDomainCatalogController itemDomainController = null; 
    @Override
    public ItemController getItemController() {
        if (itemDomainController == null) {
            itemDomainController = ItemDomainCatalogController.getInstance(); 
        }
        return itemDomainController;         
    }
    
    public static ItemCreateWizardDomainCatalogController getInstance() {
        return (ItemCreateWizardDomainCatalogController) SessionUtility.findBean("itemCreateWizardDomainCatalogController");
    }
    
    @Override
    public String getNextStepForCreateItemWizard(FlowEvent event) {
        String currentStep = event.getOldStep();

        if (currentStep.equals(ItemCreateWizardSteps.classification.getValue())) {
            // Nothing needs to be verified for classification step at this point. 
        }

        return super.getNextStepForCreateItemWizard(event);
    }

    @Override
    public String getFirstCreateWizardStep() {
        return ItemCreateWizardSteps.basicInformation.getValue();
    }   
}
