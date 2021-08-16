/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainMachineDesignController.controllerNamed)
@SessionScoped
public class ItemDomainMachineDesignController extends ItemDomainMachineDesignBaseController implements ItemDomainCableDesignWizardClient {

    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesignController.class.getName());

    public final static String controllerNamed = "itemDomainMachineDesignController";  
    
    @Override
    protected ItemDomainMachineDesignSettings createNewSettingObject() {
        return new ItemDomainMachineDesignSettings(this);
    }
    
    public static ItemDomainMachineDesignController getInstance() {
        return (ItemDomainMachineDesignController) SessionUtility.findBean(controllerNamed);
    }

    @Override
    protected ItemDomainMachineDesignControllerUtility createControllerUtilityInstance() {
        return new ItemDomainMachineDesignControllerUtility(); 
    }
    
}
