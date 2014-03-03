/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenthistory.echo2;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;
import java.util.Collections;
import java.util.logging.*;

// Echo2
import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.TaskQueueHandle;

// normally would not use "view" classes in controller, but need to for login
import nextapp.echo2.app.ContentPane;
import nextapp.echo2.app.Label;

// general app utilities
import gov.anl.aps.irmis.apps.echo2support.Echo2ThreadWork;
import gov.anl.aps.irmis.login.echo2support.LoginUtil;
import gov.anl.aps.irmis.login.SimpleCallbackHandler;

// persistence layer
import gov.anl.aps.irmis.persistence.DAOContext;
import gov.anl.aps.irmis.persistence.DAOException;
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentTypeStatus;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstance;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentState;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstanceState;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentStateCategory;

// service layer
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.pv.PVService;
import gov.anl.aps.irmis.service.shared.PersonService;
import gov.anl.aps.irmis.service.component.ComponentService;
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.componenthistory.ComponentHistoryService;
import gov.anl.aps.irmis.service.componenthistory.ComponentInstanceSearchParameters;


/**
 * The primary controller class for IRMIS Component History application.
 * Provides "action" methods to to query the IRMIS database for component instances
 * and their history. These action methods are typically invoked by the 
 * <code>ComponentHistoryApp</code>.
 * Any time-consuming activity should be queued up using the 
 * <code>QueuedExecutor</code> and <code>Echo2ThreadWork</code>.
 */
public class ComponentHistoryController {

	/** The main data model of this application */
	private ComponentHistoryModel model;
    private ApplicationInstance appInstance;
    private LoginUtil loginUtil;

	/** 
     * Create initialized controller. The tqh and ai are used to request AJAX user
     * interface updates whenever background work is completed (ex. db queries).
     */
    public ComponentHistoryController(ApplicationInstance ai, LoginUtil loginUtil) {
        appInstance = ai;
        model = new ComponentHistoryModel();     
        this.loginUtil = loginUtil;
    }

    /**
     * Gather all initial data from IRMIS needed to begin component history application.
     * When complete, fire off <code>ComponentHistoryModelEvent</code> to notify GUI
     * that the data is available for display. Note that this controller method is more
     * complicated than any of the others, in that we use a background thread to get
     * the data, and then asynchronously notify the GUI when we are complete. This
     * technique is necessary upon the first loading of the Echo application, otherwise
     * the user sees a blank white page for several seconds while this long set
     * of queries executes. For all the remaining controller methods, the Echo GUI
     * automatically shows a nice "Please wait..." while the queries execute in
     * the current servlet thread, so all this executor/taskQueueHandle/Echo2ThreadWork
     * stuff isn't necessary.
     */
    public void actionInitializeData() {

        List componentTypeList = null;
        Component siteComponent = null;
        List categoryList = null;
        List locationStateList = null;
        List operationStateList = null;
        List nrtlStateList = null;
        List mfgList = null;
        
        model.setIRMISException(null);
        
        try {
            // get list of component types
            componentTypeList = ComponentTypeService.findComponentTypeList();
            
            // Get top of housing hierarchy (Site component)
            ComponentType siteCT = 
                ComponentTypeService.findComponentTypeByName("Site");
            List sites = ComponentService.findComponentsByType(siteCT);
            // there better be just one
            if (sites.size() != 1) {
                throw new IRMISException("Unable to find root of housing hierarchy (Component Type: Site)");
            }
            siteComponent = (Component)sites.get(0);
            
            // get list of categories
            categoryList = ComponentHistoryService.findComponentStateCategoryList();
            
            // get list of location category states
            ComponentStateCategory locationCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("location");
            locationStateList = 
                ComponentHistoryService.findComponentStatesByCategory(locationCategory);
            
            // get list of operation category states
            ComponentStateCategory operationCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("operation");
            operationStateList = 
                ComponentHistoryService.findComponentStatesByCategory(operationCategory);

            // get list of NRTL category states
            ComponentStateCategory nrtlCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("NRTL");
            nrtlStateList = 
                ComponentHistoryService.findComponentStatesByCategory(nrtlCategory);

            // get list of Manufacturers
            mfgList = ComponentTypeService.findManufacturerList();
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }
        
        model.setComponentTypeList(componentTypeList);
        model.setFilteredComponentTypeList(componentTypeList);
        model.setSiteComponent(siteComponent);
        model.setCategoryList(categoryList);
        model.setLocationStateList(locationStateList);
        model.setOperationStateList(operationStateList);
        model.setNRTLStateList(nrtlStateList);
        model.setMfgList(mfgList);
        
        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.RELOAD_COMPLETE));
        
    }
    
    /**
     * Find component instances that match search terms.
     * When complete, fire off <code>ComponentHistoryModelEvent</code> to notify GUI
     * that the data is available for display. 
     */
    public void actionSearchForComponentInstances() {

        String ctName = model.getSelectedComponentTypeName();
        String sn = model.getSelectedSerialNumber();
        String location = model.getSelectedLocation();
        Component component = model.getSelectedComponent();
        boolean matchAll = model.getMatchAllOption();
        List componentInstanceList = null;
        List currentLocationList = null;
        ComponentInstanceSearchParameters searchParams = new ComponentInstanceSearchParameters();
        
        model.setIRMISException(null);

        searchParams.setMatchAll(matchAll);
        searchParams.setComponentTypeName(ctName);
        searchParams.setSerialNumber(sn);
        searchParams.setLocation(location);
        searchParams.setComponent(component);
        
        // Get component instances based on search terms
        try {

            // clear cache of previous instances and history to keep data as fresh as possible
            if (model.getComponentInstanceList() != null)
                ComponentHistoryService.clearCache(model.getComponentInstanceList());
            if (model.getLocationHistoryList() != null)
                ComponentHistoryService.clearCache(model.getLocationHistoryList());
            if (model.getOperationHistoryList() != null)
                ComponentHistoryService.clearCache(model.getOperationHistoryList());
            if (model.getCalibrationHistoryList() != null)
                ComponentHistoryService.clearCache(model.getCalibrationHistoryList());
            if (model.getNRTLHistoryList() != null)
                ComponentHistoryService.clearCache(model.getNRTLHistoryList());

            componentInstanceList = 
                ComponentHistoryService.findComponentInstanceList(searchParams);
            
            if (componentInstanceList != null) {
                // for each component instance, get current location
                currentLocationList = new ArrayList();
                Iterator it = componentInstanceList.iterator();
                while (it.hasNext()) {
                    ComponentInstance ci = (ComponentInstance)it.next();
                    String currentLocation = 
                        ComponentHistoryService.findComponentInstanceCurrentLocation(ci);
                    ci.setCurrentLocation(currentLocation);
                }
            }
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }
        
        model.setComponentInstanceList(componentInstanceList);
        
        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.INSTANCE_SEARCH_COMPLETE));
    }

    /**
     * Extract out each category of component instance state for given component instance.
     * When complete, fire off <code>ComponentHistoryModelEvent</code> to notify GUI
     * that the data is available for display. 
     */
    public void actionGetHistory(ComponentInstance ci) {

        List locationList = null;
        List operationList = null;
        List calibrationList = null;
        List nrtlList = null;

        model.setSelectedComponentInstance(ci);
        model.setIRMISException(null);
        
        // get categories
        List categories = model.getCategoryList();
        Iterator catIt = categories.iterator();
        while (catIt.hasNext()) {
            ComponentStateCategory cat = (ComponentStateCategory)catIt.next();
            String catName = cat.getCategory();
            List states = 
                ComponentHistoryService.findComponentInstanceStatesByCategory(ci,cat);
            
            if (catName.equals("location")) {
                locationList = states;

            } else if (catName.equals("operation") ||    // combine 3 into one list for GUI
                       catName.equals("workmanship") ||
                       catName.equals("procedure")) {
                if (operationList == null)
                    operationList = new ArrayList();
                operationList.addAll(states);

            } else if (catName.equals("calibration") ||  // combine 3 into one list for GUI
                       catName.equals("battery") ||
                       catName.equals("firmware")) {
                if (calibrationList == null)
                    calibrationList = new ArrayList();
                calibrationList.addAll(states);

            } else if (catName.equals("NRTL")) {
                nrtlList = states;
            }
        }
        if (calibrationList != null)
            Collections.sort(calibrationList);   // sort them all by date
        if (operationList != null)
            Collections.sort(operationList);   // sort them all by date

        // reverse date order so we show latest item first in tables
        Collections.reverse(locationList);
        Collections.reverse(operationList);
        Collections.reverse(calibrationList);
        Collections.reverse(nrtlList);
        
        model.setLocationHistoryList(locationList);
        model.setOperationHistoryList(operationList);
        model.setCalibrationHistoryList(calibrationList);
        model.setNRTLHistoryList(nrtlList);
        
        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.HISTORY_SEARCH_COMPLETE));
    }

    /**
     * Create a new ComponentInstance, save it to the database, and add it to the display.
     * This will also add an initial "location" ComponentInstanceState, and also possibly
     * a "NRTL" ComponentInstanceState.
     */
    public void actionNewInstance() {

        ComponentInstance ci = null;
        String locationString = null;
        String initialState = null;
        
        model.setIRMISException(null);
        try {
            
            // first create the component instance
            ci = new ComponentInstance();
            ci.setSerialNumber(model.getNewInstanceSerialNumber());
            Component c = model.getNewInstanceAssociatedComponent();
            if (c != null) {
                ci.setComponent(c);
                ci.setComponentType(c.getComponentType());
                locationString = buildComponentHousingPath(c);
                initialState = "installed";
                
            } else {
                ComponentType ct = 
                    model.findComponentType(model.getNewInstanceComponentTypeName());
                ci.setComponentType(ct);
                locationString = model.getNewInstanceLocationString();
                initialState = model.getNewInstanceInitialState();
            }
            
            // now tack on a component instance state for initial location
            ComponentInstanceState cis = new ComponentInstanceState();
            ComponentStateCategory locationCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("location");
            ComponentState installedState = 
                ComponentHistoryService.findComponentStateByCategoryAndState(locationCategory, initialState);
            Person userPerson = PersonService.findPersonByUserName(loginUtil.getUsername());
            cis.setEnteredDate(model.getNewInstanceEventDate());
            cis.setPerson(userPerson);
            cis.setComponentState(installedState);
            cis.setComment("location automatically added");
            cis.setReferenceData1(locationString);
            ci.addComponentInstanceState(cis);

            // and also tack one on for NRTL state if applicable
            ComponentStateCategory nrtlCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("NRTL");
            ComponentTypeStatus cts = ci.getComponentType().getComponentTypeStatus();
            String nrtlStatus = cts.getNrtlStatus();
            String nrtlAgency = cts.getNrtlAgency();
            String nrtlStateName = null;
            if (nrtlStatus == null || nrtlStatus.length() == 0)
                nrtlStateName = null;
            else if (nrtlStatus.equals("NA") || nrtlStatus.equals("NH"))
                nrtlStateName = "not required";
            else if (nrtlStatus.equals("NRTL"))
                nrtlStateName = "NRTL approved";
            else if (nrtlStatus.equals("ANL"))
                nrtlStateName = "ANL inspection required";
            else
                nrtlStateName = null;

            if (nrtlStateName != null) {
                cis = new ComponentInstanceState();
                ComponentState nrtlState = 
                    ComponentHistoryService.findComponentStateByCategoryAndState(nrtlCategory, nrtlStateName);
                cis.setEnteredDate(model.getNewInstanceEventDate());
                cis.setPerson(userPerson);
                cis.setComponentState(nrtlState);
                cis.setComment("initialized from component type");
                cis.setReferenceData1(nrtlAgency);
                ci.addComponentInstanceState(cis);            
            }
            
            // save it (i)
            ComponentHistoryService.saveComponentInstance(ci);
            
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }
        
        if (locationString != null && locationString.length() > 0)
            ci.setCurrentLocation(locationString);
        else
            ci.setCurrentLocation("unknown");

        List ciList = model.getComponentInstanceList();
        if (ciList == null)
            ciList = new ArrayList();
        ciList.add(0, ci);
        model.setComponentInstanceList(ciList);
        
        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.INSTANCE_SEARCH_COMPLETE));                    
        
    }
    
    /**
     * Create a new location history entry. Adds a <code>ComponentInstanceState</code> for
     * the current component instance being worked with.
     */
    public void actionNewLocationHistory(Date eventDate,
                                         String stateChoice, 
                                         String comment, 
                                         String locationString,
                                         Component selectedComponent) {

        model.setIRMISException(null);
        ComponentInstance ci = model.getSelectedComponentInstance();
        try {
            ComponentInstanceState cis = new ComponentInstanceState();
            Person userPerson = PersonService.findPersonByUserName(loginUtil.getUsername());
            ComponentStateCategory locationCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("location");
            ComponentState selectedState = 
                ComponentHistoryService.findComponentStateByCategoryAndState(locationCategory, stateChoice);
            cis.setEnteredDate(eventDate);
            cis.setPerson(userPerson);
            cis.setComponentState(selectedState);
            cis.setComment(comment);
            
            if (stateChoice.equals("installed")) {
                ci.setComponent(selectedComponent);
                String housingLocation = buildComponentHousingPath(selectedComponent);
                cis.setReferenceData1(housingLocation);
                ci.setCurrentLocation(housingLocation);
                
            } else {
                cis.setReferenceData1(locationString);
                ci.setCurrentLocation(locationString);
                ci.setComponent(null);
            }
            
            ci.addComponentInstanceState(cis);
            
            // save (update) component instance
            ComponentHistoryService.saveComponentInstance(ci);
            
            // add to model's location history list
            List locationList = model.getLocationHistoryList();
            if (locationList == null)
                locationList = new ArrayList();
            locationList.add(0,cis);
            model.setLocationHistoryList(locationList);
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }

        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.NEW_LOCATION_HISTORY));                    

    }

    /**
     * Creates two new component history entries. One Operation-failed, and 
     * one Location change. Adds <code>ComponentInstanceState</code> entries to 
     * the current component instance being worked with.
     */
    public void actionNewFailureHistory(Date eventDate,
                                        boolean failed,
                                        String comment, 
                                        String ctllogNumber, 
                                        boolean relocatingComponent,
                                        String stateChoice,
                                        String locationString) {

        model.setIRMISException(null);
        ComponentInstance ci = model.getSelectedComponentInstance();
        try {
            // look up person
            Person userPerson = PersonService.findPersonByUserName(loginUtil.getUsername());
            
            // look up location category and desired new state
            ComponentStateCategory locationCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("location");
            ComponentState selectedState = 
                ComponentHistoryService.findComponentStateByCategoryAndState(locationCategory, stateChoice);
            
            // look up operation category and "failed" state
            ComponentStateCategory operationCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("operation");

            ComponentState failedState = null;
            if (failed)
                failedState = ComponentHistoryService.findComponentStateByCategoryAndState(operationCategory, "failed");
            else
                failedState = ComponentHistoryService.findComponentStateByCategoryAndState(operationCategory, "questionable");
            
            // create new operation state entry
            ComponentInstanceState operationCIS = new ComponentInstanceState();
            operationCIS.setEnteredDate(eventDate);
            operationCIS.setPerson(userPerson);
            operationCIS.setComponentState(failedState);
            operationCIS.setComment(comment);
            operationCIS.setReferenceData1(ctllogNumber);
            
            ci.addComponentInstanceState(operationCIS);
            
            ComponentInstanceState locationCIS = null;
            if (relocatingComponent) {
                // create new location state entry
                locationCIS = new ComponentInstanceState();
                locationCIS.setEnteredDate(eventDate);
                locationCIS.setPerson(userPerson);
                locationCIS.setComponentState(selectedState);
                locationCIS.setComment("component failed");
                locationCIS.setReferenceData1(locationString);
                
                ci.addComponentInstanceState(locationCIS);
                // update current location to that of new location state entry
                ci.setCurrentLocation(locationString);
            }
            
            // remember any previous association with an installed component
            model.setLastFailedComponent(ci.getComponent());
            
            // clear out any association with installed component
            ci.setComponent(null);
            
            // update component instance
            ComponentHistoryService.saveComponentInstance(ci);
            
            // add to model's operation history list
            List operationList = model.getOperationHistoryList();
            if (operationList == null)
                operationList = new ArrayList();
            operationList.add(operationCIS);
            Collections.sort(operationList);
            Collections.reverse(operationList);
            model.setOperationHistoryList(operationList);

            if (relocatingComponent) {
                // add to model's location history list
                List locationList = model.getLocationHistoryList();
                if (locationList == null)
                    locationList = new ArrayList();
                locationList.add(0,locationCIS);
                model.setLocationHistoryList(locationList);
            }
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }
        
        // fire off notification of modification to listeners
        if (relocatingComponent)
            model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.NEW_LOCATION_HISTORY));
        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.NEW_OPERATION_HISTORY));

    }

    /**
     * Creates a new operation functional component history entry.
     */
    public void actionNewRepairHistory(Date eventDate,
                                       String comment, 
                                       String test) {

        model.setIRMISException(null);
        ComponentInstance ci = model.getSelectedComponentInstance();
        try {
            // look up person
            Person userPerson = PersonService.findPersonByUserName(loginUtil.getUsername());
            
            // look up operation category and "functional" state
            ComponentStateCategory operationCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("operation");
            ComponentState functionalState = 
                ComponentHistoryService.findComponentStateByCategoryAndState(operationCategory, "functional");
            
            // create new operation state entry
            ComponentInstanceState operationCIS = new ComponentInstanceState();
            operationCIS.setEnteredDate(eventDate);
            operationCIS.setPerson(userPerson);
            operationCIS.setComponentState(functionalState);
            operationCIS.setComment(comment);
            operationCIS.setReferenceData1(test);
            
            ci.addComponentInstanceState(operationCIS);

            // clear last failed component
            model.setLastFailedComponent(null);
            
            // update component instance
            ComponentHistoryService.saveComponentInstance(ci);
            
            // add to model's operation history list
            List operationList = model.getOperationHistoryList();
            if (operationList == null)
                operationList = new ArrayList();
            operationList.add(operationCIS);
            Collections.sort(operationList);
            Collections.reverse(operationList);
            model.setOperationHistoryList(operationList);
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }
        
        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.NEW_OPERATION_HISTORY));

    }

    /**
     * Creates a new workmanship inspected component history entry.
     */
    public void actionNewInspectedHistory(Date eventDate,
                                          String comment, 
                                          String test) {

        model.setIRMISException(null);
        ComponentInstance ci = model.getSelectedComponentInstance();
        try {
            // look up person
            Person userPerson = PersonService.findPersonByUserName(loginUtil.getUsername());
            
            // look up workmanship category and "inspected" state
            ComponentStateCategory workmanshipCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("workmanship");
            ComponentState inspectedState = 
                ComponentHistoryService.findComponentStateByCategoryAndState(workmanshipCategory, "inspected");
            
            // create new workmanship state entry
            ComponentInstanceState workmanshipCIS = new ComponentInstanceState();
            workmanshipCIS.setEnteredDate(eventDate);
            workmanshipCIS.setPerson(userPerson);
            workmanshipCIS.setComponentState(inspectedState);
            workmanshipCIS.setComment(comment);
            workmanshipCIS.setReferenceData1(test);
            
            ci.addComponentInstanceState(workmanshipCIS);

            // update component instance
            ComponentHistoryService.saveComponentInstance(ci);
            
            // add to model's operation history list
            List operationList = model.getOperationHistoryList();
            if (operationList == null)
                operationList = new ArrayList();
            operationList.add(workmanshipCIS);
            Collections.sort(operationList);
            Collections.reverse(operationList);
            model.setOperationHistoryList(operationList);
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }
        
        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.NEW_OPERATION_HISTORY));

    }

    /**
     * Creates a new procedure validated component history entry.
     */
    public void actionNewValidationHistory(Date eventDate,
                                           String comment, 
                                          String test) {

        model.setIRMISException(null);
        ComponentInstance ci = model.getSelectedComponentInstance();
        try {
            // look up person
            Person userPerson = PersonService.findPersonByUserName(loginUtil.getUsername());
            
            // look up procedure category and "validated" state
            ComponentStateCategory procedureCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("procedure");
            ComponentState validatedState = 
                ComponentHistoryService.findComponentStateByCategoryAndState(procedureCategory, "validated");
            
            // create new procedure state entry
            ComponentInstanceState procedureCIS = new ComponentInstanceState();
            procedureCIS.setEnteredDate(eventDate);
            procedureCIS.setPerson(userPerson);
            procedureCIS.setComponentState(validatedState);
            procedureCIS.setComment(comment);
            procedureCIS.setReferenceData1(test);
            
            ci.addComponentInstanceState(procedureCIS);

            // update component instance
            ComponentHistoryService.saveComponentInstance(ci);
            
            // add to model's operation history list
            List operationList = model.getOperationHistoryList();
            if (operationList == null)
                operationList = new ArrayList();
            operationList.add(procedureCIS);
            Collections.sort(operationList);
            Collections.reverse(operationList);
            model.setOperationHistoryList(operationList);
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }
        
        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.NEW_OPERATION_HISTORY));

    }

    /**
     * Creates a new calibration component history entry.
     */
    public void actionNewCalibrationHistory(Date eventDate, String comment, 
                                            Date nextCalibrationDate) {

        model.setIRMISException(null);
        ComponentInstance ci = model.getSelectedComponentInstance();
        try {
            // look up person
            Person userPerson = PersonService.findPersonByUserName(loginUtil.getUsername());
            
            // look up calibration category and "calibrated" state
            ComponentStateCategory calibrationCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("calibration");
            ComponentState calibratedState = 
                ComponentHistoryService.findComponentStateByCategoryAndState(calibrationCategory, "calibrated");
            
            // create new calibration state entry
            ComponentInstanceState calibrationCIS = new ComponentInstanceState();
            calibrationCIS.setEnteredDate(eventDate);
            calibrationCIS.setPerson(userPerson);
            calibrationCIS.setComponentState(calibratedState);
            calibrationCIS.setComment(comment);
            calibrationCIS.setReferenceData2(nextCalibrationDate);
            
            ci.addComponentInstanceState(calibrationCIS);

            // update component instance
            ComponentHistoryService.saveComponentInstance(ci);
            
            // add to model's calibration history list
            List calibrationList = model.getCalibrationHistoryList();
            if (calibrationList == null)
                calibrationList = new ArrayList();
            calibrationList.add(calibrationCIS);
            Collections.sort(calibrationList);
            Collections.reverse(calibrationList);
            model.setCalibrationHistoryList(calibrationList);
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }
        
        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.NEW_CALIBRATION_HISTORY));

    }

    /**
     * Creates a new battery component history entry.
     */
    public void actionNewBatteryHistory(Date eventDate, String comment, 
                                        Date nextChangeDate) {

        model.setIRMISException(null);
        ComponentInstance ci = model.getSelectedComponentInstance();
        try {
            // look up person
            Person userPerson = PersonService.findPersonByUserName(loginUtil.getUsername());
            
            // look up battery category and "changed" state
            ComponentStateCategory batteryCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("battery");
            ComponentState changedState = 
                ComponentHistoryService.findComponentStateByCategoryAndState(batteryCategory, "changed");
            
            // create new battery changed state entry
            ComponentInstanceState changedCIS = new ComponentInstanceState();
            changedCIS.setEnteredDate(eventDate);
            changedCIS.setPerson(userPerson);
            changedCIS.setComponentState(changedState);
            changedCIS.setComment(comment);
            changedCIS.setReferenceData2(nextChangeDate);
            
            ci.addComponentInstanceState(changedCIS);

            // update component instance
            ComponentHistoryService.saveComponentInstance(ci);
            
            // add to model's calibration history list
            List calibrationList = model.getCalibrationHistoryList();
            if (calibrationList == null)
                calibrationList = new ArrayList();
            calibrationList.add(changedCIS);
            Collections.sort(calibrationList);
            Collections.reverse(calibrationList);
            model.setCalibrationHistoryList(calibrationList);
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }
        
        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.NEW_CALIBRATION_HISTORY));

    }

    /**
     * Creates a new firmware update component history entry.
     */
    public void actionNewFirmwareHistory(Date eventDate,
                                         String comment, 
                                         String firmwareVersion) {

        model.setIRMISException(null);
        ComponentInstance ci = model.getSelectedComponentInstance();
        try {
            // look up person
            Person userPerson = PersonService.findPersonByUserName(loginUtil.getUsername());
            
            // look up firmware category and "udpated" state
            ComponentStateCategory firmwareCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("firmware");
            ComponentState updatedState = 
                ComponentHistoryService.findComponentStateByCategoryAndState(firmwareCategory, "updated");
            
            // create new firmware updated state entry
            ComponentInstanceState updatedCIS = new ComponentInstanceState();
            updatedCIS.setEnteredDate(eventDate);
            updatedCIS.setPerson(userPerson);
            updatedCIS.setComponentState(updatedState);
            updatedCIS.setComment(comment);
            updatedCIS.setReferenceData1(firmwareVersion);
            
            ci.addComponentInstanceState(updatedCIS);

            // update component instance
            ComponentHistoryService.saveComponentInstance(ci);
            
            // add to model's calibration history list
            List calibrationList = model.getCalibrationHistoryList();
            if (calibrationList == null)
                calibrationList = new ArrayList();
            calibrationList.add(updatedCIS);
            Collections.sort(calibrationList);
            Collections.reverse(calibrationList);
            model.setCalibrationHistoryList(calibrationList);
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }
        
        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.NEW_CALIBRATION_HISTORY));

    }

    /**
     * Create a new nrtl history entry. Adds a <code>ComponentInstanceState</code> for
     * the current component instance being worked with.
     */
    public void actionNewNRTLHistory(Date eventDate,
                                     String stateChoice, 
                                     String comment, 
                                     String agencyString) {
        
        model.setIRMISException(null);
        ComponentInstance ci = model.getSelectedComponentInstance();
        try {
            ComponentInstanceState cis = new ComponentInstanceState();
            Person userPerson = PersonService.findPersonByUserName(loginUtil.getUsername());
            ComponentStateCategory nrtlCategory = 
                ComponentHistoryService.findComponentStateCategoryByCategory("NRTL");
            ComponentState selectedState = 
                ComponentHistoryService.findComponentStateByCategoryAndState(nrtlCategory, stateChoice);
            cis.setEnteredDate(eventDate);
            cis.setPerson(userPerson);
            cis.setComponentState(selectedState);
            cis.setComment(comment);
            
            if (stateChoice.equals("NRTL approved")) {
                cis.setReferenceData1(agencyString);
            } 
            
            ci.addComponentInstanceState(cis);
            
            // save (update) component instance
            ComponentHistoryService.saveComponentInstance(ci);
            
            // add to model's nrtl history list
            List nrtlList = model.getNRTLHistoryList();
            if (nrtlList == null)
                nrtlList = new ArrayList();
            nrtlList.add(0,cis);
            model.setNRTLHistoryList(nrtlList);
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }

        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.NEW_NRTL_HISTORY));                    

    }

    /**
     * Save any changes to the selected component instance to the database. The component instance
     * table will be redrawn as a result.
     */
    public void actionSaveInstance() {
        
        model.setIRMISException(null);
        ComponentInstance ci = model.getSelectedComponentInstance();
        try {
            if (ci != null) {
                // save (update) component instance
                ComponentHistoryService.saveComponentInstance(ci);
            }
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }

        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.NEW_LOCATION_HISTORY));                    

    }

    /**
     * Remove the selected component instance from the database, along with any history
     * that has been added to it.
     */
    public void actionRemoveInstance() {
        
        model.setIRMISException(null);
        ComponentInstance ci = model.getSelectedComponentInstance();
        try {
            if (ci != null) {
                // remove component instance
                ComponentHistoryService.removeComponentInstance(ci);
            }
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }

        model.getComponentInstanceList().remove(ci);

        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.INSTANCE_SEARCH_COMPLETE));                    

    }


    /**
     * Find list of open housing installation component slots based on the component 
     * type of the selected component instance, and whether or not the target slot
     * is already occupied or not. Store result in model.
     */
    public void actionDetermineOpenInstallSlots() {

        List openComponentSlots = new ArrayList();
        
        model.setIRMISException(null);
        ComponentInstance ci = model.getSelectedComponentInstance();
        try {
            System.out.println("determining open install slots - begin");
            ComponentType ct = ci.getComponentType();
            System.err.println("determining open install slots - got ct of "+ct.toString());
            
            // find all ci's of given ct that have an associated component
            List candidates = 
                ComponentService.findComponentsByType(ct, ComponentRelationshipType.HOUSING);
            System.err.println("got candidates");
            
            // iterate to see which have an associated component instance
            if (candidates != null) {
                System.err.println("found candidates");
                System.err.println("in fact, found "+candidates.size()+" of them");
                Iterator it = candidates.iterator();
                while (it.hasNext()) {
                    Component c = (Component)it.next();
                    ComponentInstance candidateCI = 
                        ComponentHistoryService.findComponentInstanceByComponent(c);
                    if (candidateCI == null)
                        openComponentSlots.add(c);
                }
            }
            model.setOpenComponentSlots(openComponentSlots);
            System.err.println("determining open install slots - end");
            
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        } 

        // fire off notification of modification to listeners
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.OPEN_INSTALL_SLOTS_FOUND));

    }

    /**
     * Take a component c, and return a string representing the full housing path
     * from the root Site component to the given component.
     */
    private String buildComponentHousingPath(Component c) {
        int housing = ComponentRelationshipType.HOUSING;
        List path = ComponentService.getComponentPathToRoot(c, housing);
        Collections.reverse(path);
        // build up string path
        Iterator sit = path.iterator();
        String strPath = "";
        while (sit.hasNext()) {
            Component pathComponent = (Component)sit.next();
            strPath = strPath + "/" + pathComponent.toString(housing);
        }
        return strPath;
    }

    public boolean actionComponentHasInstance(Component component) {
        ComponentInstance ci = null;

        try {
            ci = ComponentHistoryService.findComponentInstanceByComponent(component);
        } catch (IRMISException ie) {
            ie.printStackTrace();
            model.setIRMISException(ie);
        }
        if (ci != null)
            return true;
        else
            return false;
    }
    
    /**
     * Do login work in executor, since it will access IRMIS database for authorization.
     *
     * @param loginCallbackHandler this is object returned by login dialog containing credentials
     */
    public void login(SimpleCallbackHandler loginCallbackHandler) {

        loginUtil.login(loginCallbackHandler);
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.LOGIN_COMPLETE));                    
        
    }
    
    /**
     * Do logout. This doesn't touch the database, so don't need executor.
     *
     */
    public void logout() {

        loginUtil.logout();
        model.notifyComponentHistoryModelListeners(new ComponentHistoryModelEvent(ComponentHistoryModelEvent.LOGIN_COMPLETE));                    
        
    }
    
    
	/**
	 * Get the main model of this document
	 * @return the main model of this document
	 */
	public ComponentHistoryModel getModel() {
		return model;
	}

	/**
	 * Get the Echo2 application instance running this application.
	 * @return the <code>ApplicationInstance</code>
	 */
	public ApplicationInstance getApplicationInstance() {
		return appInstance;
	}

}
