/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.LoginController;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Binder;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.BinderTraveler;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Traveler;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.TravelerDiscrepancyLog;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author craig
 */
public abstract class ItemTravelerDomainInstanceControllerBase extends ItemTravelerController {

    private static final Logger logger = LogManager.getLogger(ItemTravelerDomainInstanceControllerBase.class.getName());

    public static final double TRAVELER_COMPLETED_STATUS = 2.0;

    private boolean isDisplayMultiEditTravelerInstance = false;
    private boolean renderArchivedTravelerListDialog;

    private boolean renderMoveTravelerContents;

    private List<BinderTraveler> activeTravelersForCurrent;
    private List<Traveler> archivedTravelersForCurrent;

    private TravelerDiscrepancyLog discrepancyLog;

    private Item itemToMoveCurrentTraveler;

    @Override
    public void resetExtensionVariablesForCurrent() {
        super.resetExtensionVariablesForCurrent();

        archivedTravelersForCurrent = null;
        activeTravelersForCurrent = null;
        itemToMoveCurrentTraveler = null;
    }

    @Override
    protected void resetRenderBooleans() {
        super.resetRenderBooleans();
        renderArchivedTravelerListDialog = false;
        renderMoveTravelerContents = false;
    }

    public boolean isIsDisplayMultiEditTravelerInstance() {
        return isDisplayMultiEditTravelerInstance;
    }

    public void setIsDisplayMultiEditTravelerInstance(boolean isDisplayMultiEditTravelerInstance) {
        this.isDisplayMultiEditTravelerInstance = isDisplayMultiEditTravelerInstance;
    }

    public boolean getIsCollapsedTravelerInstances() {
        return !(getTravelersForCurrent().size() > 0);
    }

    public void archiveTraveler(Traveler traveler) {
        // Check if property value for traveler exists
        if (getCurrent() == null) {
            SessionUtility.addErrorMessage("Error", "Current item is not loaded properly. Cannot proceed.");
            logger.error("Cannot archive traveler. Current item is not loaded properly.");
            return;
        }

        String travelerId = traveler.getId();

        // Check if traveler first needs to be removed from a binder. 
        List<PropertyValue> travelerInstanceTypePropertyValueList = getTravelerInstanceTypePropertyValueList();
        Boolean travelerInBinder = true;
        for (PropertyValue pv : travelerInstanceTypePropertyValueList) {
            if (pv.getValue().equals(travelerId)) {
                travelerInBinder = false;
                break;
            }
        }

        if (travelerInBinder) {
            List<PropertyValue> binderPropertyValueList = getBinderPropertyValueList();
            List<Binder> binderList = new ArrayList<>();
            int lastSize = binderList.size();
            Boolean travelerRemovedFromBinder = false;
            for (PropertyValue pv : binderPropertyValueList) {
                String binderId = pv.getValue();
                addBinderFromBinderId(binderId, binderList);

                int size = binderList.size();
                if (lastSize == size) {
                    // Traveler binder cannot be inspected. 
                    continue;
                } else {
                    Binder binder = binderList.get(size - 1);
                    loadTravelerListForBinder(binder);
                    lastSize = size;

                    for (Traveler binderTraveler : binder.getTravelerList()) {
                        if (binderTraveler.getId().equals(travelerId)) {
                            try {
                                removeTravelerFromBinder(binder, traveler);
                                travelerRemovedFromBinder = true;
                            } catch (CdbException ex) {
                                logger.error(ex);
                                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                                return;
                            }
                            break;
                        }
                    }
                }
            }
            if (!travelerRemovedFromBinder) {
                SessionUtility.addErrorMessage("Error", "Couldn't find traveler reference in binder.");
                logger.error("Couldn't find traveler reference in binder.");
                return;
            }
        }

        try {
            travelerApi.updateTravelerArchived(travelerId, true);
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
        }
        resetExtensionVariablesForCurrent();
    }

    public void unarchiveTraveler(Traveler traveler) {
        String travelerId = traveler.getId();
        try {
            travelerApi.updateTravelerArchived(travelerId, false);
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
        }
        resetExtensionVariablesForCurrent();
    }

    public void removeTravelerFromBinder(Binder binder, Traveler traveler) throws CdbException {
        String travelerId = traveler.getId();

        PropertyType travelerInstancePropertyType = getTravelerInstancePropertyType();
        if (travelerInstancePropertyType != null && getCurrent().getId() != null) {
            propertyValue = getItemController().preparePropertyTypeValueAdd(travelerInstancePropertyType);
            propertyValue.setValue(travelerId);
        } else {
            SessionUtility.addErrorMessage("Error", "Traveler instance reference must be created before removal from binder.");
            throw new CdbException("Cannot create a traveler instance");
        }

        String binderId = binder.getId();
        String username = ((UserInfo) SessionUtility.getUser()).getUsername();

        try {
            travelerApi.removeWorkFromBinder(binderId, travelerId, username);
            savePropertyList();
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            throw ex;
        }
    }

    public List<BinderTraveler> getActiveTravelersForCurrent() {
        if (activeTravelersForCurrent == null) {
            activeTravelersForCurrent = new ArrayList<>();
            for (BinderTraveler travelerBinder : getTravelersForCurrent()) {
                if (travelerBinder instanceof Traveler) {
                    if (((Traveler) travelerBinder).isArchived()) {
                        continue;
                    }
                }
                activeTravelersForCurrent.add(travelerBinder);
            }
        }
        return activeTravelersForCurrent;
    }

    public List<Traveler> getArchivedTravelersForCurrent() {
        if (archivedTravelersForCurrent == null) {
            archivedTravelersForCurrent = new ArrayList<>();
            for (BinderTraveler travelerBinder : getTravelersForCurrent()) {
                if (travelerBinder instanceof Traveler) {
                    Traveler traveler = (Traveler) travelerBinder;
                    if (traveler.isArchived()) {
                        archivedTravelersForCurrent.add(traveler);
                    }
                }
            }
        }
        return archivedTravelersForCurrent;
    }

    public boolean isTravelerCompleted(BinderTraveler traveler) {
        if (traveler instanceof Traveler) {
            return ((Traveler) traveler).getStatus() == TRAVELER_COMPLETED_STATUS;
        } else if (traveler instanceof Binder) {
            Integer progress = traveler.getProgress();
            return progress == 100;
        }
        return false;
    }

    public String getTravelerInstanceProgressLabel(BinderTraveler traveler) {
        Double finish = traveler.getProgressFinished();
        Double total = traveler.getProgressTotal();

        return String.format("%.2f/%.2f", finish, total);
    }

    public void prepareShowArchivedTravelerListDialog() {
        renderArchivedTravelerListDialog = true;
    }

    public void prepareTravelerInstanceMove() {
        renderMoveTravelerContents = true;
        itemToMoveCurrentTraveler = null;
    }

    public void moveTravelerInstanceToNewItem(String onSuccessCommand) {
        Item current = getCurrent();
        Traveler currentTravelerInstance = getCurrentTravelerInstance();

        if (itemToMoveCurrentTraveler == null) {
            SessionUtility.addWarningMessage("No item selected", "Please select a new inventory item to move traveler to.");
            return;
        }

        if (current.equals(itemToMoveCurrentTraveler)) {
            SessionUtility.addWarningMessage("Current item selected", "Traveler is already assigned to selected inventory item.");
            return;
        }

        List<Item> itemsToUpdate = new ArrayList<>();
        itemsToUpdate.add(current);
        itemsToUpdate.add(itemToMoveCurrentTraveler);

        //Verify Permissions
        LoginController loginController = LoginController.getInstance();
        for (Item item : itemsToUpdate) {
            EntityInfo ei = item.getEntityInfo();
            if (loginController.isEntityWriteable(ei) == false) {
                SessionUtility.addErrorMessage("Insufficient privilages", "The user does not have sufficient privilages for item: " + item.toString());
                return;
            }
        }

        LinkedList<String> revertDevices = currentTravelerInstance.getDevices();
        String deviceName;
        try {
            deviceName = getDeviceName(itemToMoveCurrentTraveler);
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            return;
        }

        String id = currentTravelerInstance.getId();

        String title = currentTravelerInstance.getTitle();
        String description = currentTravelerInstance.getDescription();
        Date deadline = getCurrentTravelerDeadline();
        double status = currentTravelerInstance.getStatus();

        UserInfo currentUser = (UserInfo) SessionUtility.getUser();
        String userName = currentUser.getUsername();

        try {
            travelerApi.updateTraveler(id, userName, title, description, deadline, status, deviceName);
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            return;
        }

        List<PropertyValue> internalPVs = current.getPropertyValueInternalList();
        List<PropertyValue> travelerPVs = getTravelerInstanceTypePropertyValueList(internalPVs);

        PropertyValue currentPV = null;
        for (PropertyValue pv : travelerPVs) {
            if (pv.getValue().equals(id)) {
                currentPV = pv;
                break;
            }
        }

        if (currentPV == null) {
            SessionUtility.addErrorMessage("Unexpected error occurred", "Traveler property not found.");
            return;
        }

        current.getPropertyValueList().remove(currentPV);
        itemToMoveCurrentTraveler.getPropertyValueList().add(currentPV);

        boolean revert = false;
        boolean revertItem = false;

        try {
            performUpdateOperations(itemToMoveCurrentTraveler);
        } catch (Exception ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
            revert = true;
        }
        if (revert == false) {
            try {
                performUpdateOperations(current);
            } catch (Exception ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getMessage());
                revertItem = true;
                revert = true;
            }
        }

        if (revertItem) {
            itemToMoveCurrentTraveler.getPropertyValueList().remove(currentPV);
            try {
                performUpdateOperations(itemToMoveCurrentTraveler);
            } catch (Exception ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getMessage());
                revert = true;
            }
        }

        if (revert) {
            try {
                String device = revertDevices.get(0);
                travelerApi.updateTraveler(id, userName, title, description, deadline, status, device);
            } catch (CdbException ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
                return;
            }
        }

        SessionUtility.executeRemoteCommand(onSuccessCommand);
    }

    public void loadDiscrepancyLog(Traveler traveler, String onSuccessCommand) {
        try {
            String travelerId = traveler.getId();
            discrepancyLog = travelerApi.getTravelerDiscrepancyLog(travelerId);
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage(), true);
            return;
        }

        if (discrepancyLog.getDiscrepancyForm() == null) {
            SessionUtility.addWarningMessage("No discrepancy form to display", "Selected traveler has no discrepancy log.", true);
            discrepancyLog = null; 
            return;
        }
        
        SessionUtility.executeRemoteCommand(onSuccessCommand);
    }

    public boolean isRenderArchivedTravelerListDialog() {
        return renderArchivedTravelerListDialog;
    }

    public Item getItemToMoveCurrentTraveler() {
        return itemToMoveCurrentTraveler;
    }

    public void setItemToMoveCurrentTraveler(Item itemToMoveCurrentTraveler) {
        this.itemToMoveCurrentTraveler = itemToMoveCurrentTraveler;
    }

    public TravelerDiscrepancyLog getDiscrepancyLog() {
        return discrepancyLog;
    }

    public boolean isRenderMoveTravelerContents() {
        return renderMoveTravelerContents;
    }

    public boolean isRenderMoveTraveler() {
        return true;
    }
}
