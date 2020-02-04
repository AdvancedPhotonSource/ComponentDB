/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Binder;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.BinderTraveler;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Traveler;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Named(ItemTravelerDomainInventoryController.controllerNamed)
@SessionScoped
public class ItemTravelerDomainInventoryController extends ItemTravelerController implements Serializable {
    
    public final static String controllerNamed = "itemTravelerDomainInventoryController";
    private static final Logger logger = Logger.getLogger(ItemTravelerDomainInventoryController.class.getName());
    
    public static final double TRAVELER_COMPLETED_STATUS = 2.0; 
    
    private boolean isDisplayMultiEditTravelerInstance = false; 
    private boolean renderArchivedTravelerListDialog;
    
    private ItemDomainInventoryController itemDomainInventoryController = null; 
    
    private List<BinderTraveler> activeTravelersForCurrent;
    private List<Traveler> archivedTravelersForCurrent; 

    @Override
    protected ItemController getItemController() {
        if (itemDomainInventoryController == null) {
            itemDomainInventoryController = ItemDomainInventoryController.getInstance();           
        }
                
        return itemDomainInventoryController; 
    } 

    @Override
    public void resetExtensionVariablesForCurrent() {
        super.resetExtensionVariablesForCurrent(); 
        
        archivedTravelersForCurrent = null; 
        activeTravelersForCurrent = null; 
    }

    @Override
    protected void resetRenderBooleans() {
        super.resetRenderBooleans(); 
        renderArchivedTravelerListDialog = false; 
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
            for (PropertyValue pv: binderPropertyValueList) {
                String binderId = pv.getValue();
                addBinderFromBinderId(binderId, binderList);
                
                int size = binderList.size(); 
                if (lastSize == size) {
                    // Traveler binder cannot be inspected. 
                    continue;
                } else {
                    Binder binder = binderList.get(size -1); 
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
                    if (!travelerRemovedFromBinder) {
                        SessionUtility.addErrorMessage("Error", "Couldn't find traveler reference in binder.");
                        logger.error("Couldn't find traveler reference in binder.");
                        return;
                    }
                }
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
        }
        return false; 
    }
    
    public void prepareShowArchivedTravelerListDialog() {
        renderArchivedTravelerListDialog = true; 
    }

    public boolean isRenderArchivedTravelerListDialog() {
        return renderArchivedTravelerListDialog;
    }
    
}
