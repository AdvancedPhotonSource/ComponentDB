/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignWizardBase;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * Controller for adding cable bundles.
 *
 * @author cmcchesney
 */
@Named(BundleWizard.CONTROLLER_NAMED)
@SessionScoped
public class BundleWizard extends ItemDomainCableDesignWizardBase implements Serializable {

    public static final String CONTROLLER_NAMED = "bundleWizard";
    
    public static BundleWizard getInstance() {
        return (BundleWizard) SessionUtility.findBean(BundleWizard.CONTROLLER_NAMED);
    } 

    /**
     * Implements the save operation, invoked by the wizard's "Save" navigation
     * button.
     */
    public String save() {

        SessionUtility.addErrorMessage(
                "Could not save cable bundle",
                "Feature not yet implemented.");
        return "";
    }
}
