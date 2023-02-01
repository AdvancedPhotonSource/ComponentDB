/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ExternalServiceError;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainAppController;
import gov.anl.aps.cdb.portal.model.db.entities.CdbDomainEntity;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Form;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Forms;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Named(ItemTravelerDomainAppController.controllerNamed)
@SessionScoped
public class ItemTravelerDomainAppController extends ItemTravelerDomainInstanceControllerBase implements Serializable {

    public final static String controllerNamed = "itemTravelerDomainAppController";

    private static final Logger logger = LogManager.getLogger(ItemTravelerDomainAppController.class.getName());

    private ItemDomainAppController itemDomainAppController;

    @Override
    protected ItemController getItemController() {
        if (itemDomainAppController == null) {
            itemDomainAppController = ItemDomainAppController.getInstance();
        }

        return itemDomainAppController;
    }

    @Override
    public void loadEntityActiveAvailableTemplateList(CdbDomainEntity domainEntity, List<Form> outTemplateList) {
        try {
            Forms forms_obj = travelerApi.getForms();
            LinkedList<Form> forms = forms_obj.getForms();

            for (Form form : forms) {
                outTemplateList.add(form);
            }
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
        }

    }

    @Override
    public boolean isRenderMoveTraveler() {
        return false;
    }

    @Override
    public boolean isRenderMoveTravelerContents() {
        return false;
    }

}
