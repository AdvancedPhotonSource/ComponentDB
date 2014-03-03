/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;
import java.util.Date;
import javax.swing.SwingUtilities;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import java.util.logging.Logger;
import java.util.logging.Level;

// XAL framework stuff
import gov.sns.application.*;

// general app utilities
import gov.anl.aps.irmis.apps.SwingThreadWork;
import gov.anl.aps.irmis.apps.QueuedExecutorUtil;
import gov.anl.aps.irmis.apps.ProgressDialog;
import gov.anl.aps.irmis.login.LoginUtil;

// other IRMIS sub-applications
import gov.anl.aps.irmis.apps.componenttype.cfw.ComponentTypeDocument;
import gov.anl.aps.irmis.apps.cable.cfw.CableDocument;

// persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentPort;
import gov.anl.aps.irmis.persistence.component.ComponentTypeStatus;
import gov.anl.aps.irmis.persistence.component.ComponentRelationship;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.login.GroupName;
import gov.anl.aps.irmis.persistence.audit.AuditAction;
import gov.anl.aps.irmis.persistence.audit.AuditActionType;
import gov.anl.aps.irmis.persistence.DAOException;

// service layer
import gov.anl.aps.irmis.service.component.ComponentService;
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.pv.PVService;
import gov.anl.aps.irmis.service.shared.AuditService;
import gov.anl.aps.irmis.service.shared.PersonService;
import gov.anl.aps.irmis.service.IRMISException;


/**
 * The primary controller class for IRMIS Component Viewer application.
 * Provides "action" methods to to query the IRMIS database for component
 * related data. These action methods are typically invoked by the 
 * <code>ComponentWindow</code>.
 * Any time-consuming activity should be queued up using the 
 * <code>queuedExecutor</code>, which is similar to the SwingWorker concept.
 */
public class ComponentDocument extends XalInternalDocument {

	/** The main data model of this application */
	private ComponentModel _model;

    // Hold reference to parent desktop frame. Used to put up busy icon during work.
    JFrame _appFrame;

	/** Create a new empty document */
    public ComponentDocument() {
        this(null);
        _model = new ComponentModel();
    }

	/** Create a new document from url */
    public ComponentDocument(final URL url) {
        //setSource(url);
        _model = new ComponentModel();
       _appFrame = ((DesktopApplication)Application.getApp()).getDesktopFrame();
        // don't have anything here yet
    }

    public void saveDocumentAs(final URL url) {
        return;
    }

    /**
     * Specifies the document file suffixes supported by this document.
     * @return An array of file suffixes corresponding to writable files
     */
    public String[] writableDocumentTypes() {
        return new String[] {};
	}

    /**
	 * Overriding default window title to my liking.
     * @param newTitle The new title for this document.
     */
    public void setTitle( final String newTitle ) {
        String modifiedTitle = "idt::component" + newTitle;
		super.setTitle( modifiedTitle );
        _documentListenerProxy.titleChanged( this, title );
    }	

    /**
     * Clear out all the data in the model.
     */
    public void actionResetModel() {
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                public void doNonUILogic() {
                    actionUndoComponentConfigure(true);  // if any
                    _model.reset();
                }
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.REDRAW));
                }
            });
    }

    /**
     * Queue up job for background thread to gather the current housing,
     * control, and power hierarchies, along with any other data we will need
     * regularly during execution. When complete, fire off 
     * <code>ComponentModelEvent</code> to notify GUI that new data is available
     * for display.
     */
    public void actionReload() {
        
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private Component siteComponent = null;
                private List roomComponents = null;
                private Component networkComponent = null;
                private Component utilityComponent = null;
                private List componentTypes = null;
                private List systemList = null;
                private List groupNameList = null;
                public void doNonUILogic() {

                    // Get current set of Components of type room, and put into model
                    _model.setIRMISException(null);
                    try {
                        // put up progress dialog, since this can take a while
                        ProgressDialog.show(_appFrame,1,4);
                        ProgressDialog.setValue(1);
                        //System.out.println("*** start block 1");

                        // get this info, since we need it many times
                        componentTypes = ComponentTypeService.findComponentTypeList();

                        // housing hierarchy
                        ComponentType siteCT = 
                            ComponentTypeService.findComponentTypeByName("Site");
                        List sites = ComponentService.findComponentsByType(siteCT);
                        // there better be just one
                        if (sites.size() != 1) {
                            throw new IRMISException("Unable to find root of housing hierarchy (Component Type: Site)");
                        }
                        siteComponent = (Component)sites.get(0);
                        ProgressDialog.setValue(2);
                        //System.out.println("*** start block 2");
                        // get all 2nd level housing components (rooms) into one list
                        List childRels = 
                            siteComponent.getChildRelationships(ComponentRelationshipType.HOUSING);
                        roomComponents = new ArrayList();
                        if (childRels != null) {
                            Iterator childIt = childRels.iterator();
                            while (childIt.hasNext()) {
                                ComponentRelationship rel = (ComponentRelationship)childIt.next();
                                Component buildingComponent = rel.getChildComponent();
                                if (buildingComponent.getComponentType().getComponentTypeName().equals("Room")) {
                                    roomComponents.add(buildingComponent); // check for rooms at first level
                                } else {
                                    List roomList = 
                                        buildingComponent.getChildRelationships(ComponentRelationshipType.HOUSING);
                                    if (roomList != null && roomList.size() > 0) {
                                        Iterator roomIt = roomList.iterator();
                                        while (roomIt.hasNext()) {
                                            ComponentRelationship rr = (ComponentRelationship)roomIt.next();
                                            Component room = rr.getChildComponent();
                                            if (room.getComponentType().getComponentTypeName().equals("Room"))
                                                roomComponents.add(room);
                                        }
                                    }
                                }
                            }
                        }

                        ProgressDialog.setValue(3);
                        //System.out.println("*** start block 3");

                        // control hierarchy
                        ComponentType networkCT = 
                            ComponentTypeService.findComponentTypeByName("Network");
                        List networks = ComponentService.findComponentsByType(networkCT);
                        // there better be just one
                        if (networks.size() != 1) {
                            throw new IRMISException("Unable to find root of control hierarchy (Component Type: Network)");
                        }
                        networkComponent = (Component)networks.get(0);

                        // power hierarchy
                        ComponentType utilityCT = 
                            ComponentTypeService.findComponentTypeByName("Utility");
                        List utilities = ComponentService.findComponentsByType(utilityCT);
                        // there better be just one
                        if (utilities.size() != 1) {
                            throw new IRMISException("Unable to find root of power hierarchy (Component Type: Utility)");
                        }
                        utilityComponent = (Component)utilities.get(0);

                        ProgressDialog.setValue(4);
                        //System.out.println("*** start block 4");

                        // get list of ioc system names
                        systemList = PVService.findSystems();
                        // get list of group names
                        groupNameList = PersonService.findGroupNameList();

                        // if we had a previously selected component, reload it explicitly
                        Long cId = _model.getSelectedComponentId();
                        Component c = null;
                        if (cId != null && cId.longValue() != 0)
                            c = ComponentService.findComponentById(cId);
                        _model.setSelectedComponent(c);

                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        _model.setIRMISException(ie);

                    } finally {
                        ProgressDialog.destroy();
                    }
                }
                
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                    _model.setSiteComponent(siteComponent);
                    _model.setRoomComponents(roomComponents);
                    _model.setNetworkComponent(networkComponent);
                    _model.setUtilityComponent(utilityComponent);
                    _model.setComponentTypes(componentTypes);
                    _model.setSystems(systemList);
                    _model.setGroupNames(groupNameList);
                    Date currDate = new Date();
                    _model.setDataTimestamp(currDate.getTime());
                }
            });
    }

    public void actionRedraw() {
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                public void doNonUILogic() {
                }
                
                public void doUIUpdateLogic() {
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.REDRAW));
                }
            });
    }

            
    /**
     * Make a main window by instantiating the my custom window.
     */
    public void makeMainWindow() {
        _mainWindow = new ComponentWindow(this);

        // Initialize application data from database and do a full redraw of GUI.
        actionReload();
        actionRedraw();
    }


    /**
     * Queue up job for background thread to find the restricted list of
     * potential parent component types based on the chosen "component to configure".
     * If the _model.getComponentToConfigure() is null, undo any filters.
     * When complete, fire off <code>ComponentModelEvent</code> to notify GUI 
     * that filtered list is available for display.
     */
    public void actionFilterComponentTypes() {
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private List filteredHousingComponentTypes = null;
                private List filteredControlComponentTypes = null;
                private List filteredPowerComponentTypes = null;
                public void doNonUILogic() {
                    // Get current set of Components of type room, and put into model
                    _model.setIRMISException(null);
                    Component ctc = _model.getComponentToConfigure();
                    int hierarchy = 0;
                    if (ctc != null) {
                        hierarchy = ComponentRelationshipType.HOUSING;
                        filteredHousingComponentTypes = 
                            ComponentTypeService.
                            filterComponentTypeListByPeerInterface(_model.getComponentTypes(),
                                                                   ctc.getComponentType(),
                                                                   true, false, 
                                                                   hierarchy);
                        
                        hierarchy = ComponentRelationshipType.CONTROL;
                        filteredControlComponentTypes = 
                            ComponentTypeService.
                            filterComponentTypeListByPeerInterface(_model.getComponentTypes(),
                                                                   ctc.getComponentType(),
                                                                   true, false, 
                                                                   hierarchy);
                        
                        hierarchy = ComponentRelationshipType.POWER;
                        filteredPowerComponentTypes = 
                            ComponentTypeService.
                            filterComponentTypeListByPeerInterface(_model.getComponentTypes(),
                                                                   ctc.getComponentType(),
                                                                   true, false, 
                                                                   hierarchy);
                    }
                }
                
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                    _model.setFilteredComponentTypes(filteredHousingComponentTypes,
                                                     ComponentRelationshipType.HOUSING);
                    _model.setFilteredComponentTypes(filteredControlComponentTypes,
                                                     ComponentRelationshipType.CONTROL);
                    _model.setFilteredComponentTypes(filteredPowerComponentTypes,
                                                     ComponentRelationshipType.POWER);
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.NEW_FILTERED_TYPES));
                }
            });
    }

    /**
     * Queue up job for background thread to commit a new, or modified component.
     * When complete, fire off <code>ComponentModelEvent</code> to notify GUI 
     * that component is committed.
     */
    public void actionCommitComponent(Component componentToConfigure) {
        final Component ctc = componentToConfigure;
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private boolean stale = false;
                public void doNonUILogic() {
                    _model.setIRMISException(null);
                    try {

                        // Make sure the semaphore we have is still valid before committing.
                        if (!ComponentService.isValidSemaphore(_model.getSemaphoreValue())) {
                            actionUndoComponentConfigure(false);
                            stale = true;
                            return;
                        }

                        ArrayList parentsOfComponentModified = new ArrayList();
                        ArrayList configureActionList = new ArrayList();

                        // Process the stack of configure actions, looking for ones
                        // that require special last-step logic.
                        ConfigureAction action = _model.popConfigureUndoStack();
                        while (action != null) {
                            // make reverse order list of configure actions
                            configureActionList.add(0,action);

                            switch (action.getType()) {
                            case ConfigureAction.DELETE_CHILD: {
                                // We must remove all child relationships in hierarchy
                                // and set them markForDelete=true.
                                int hierarchy = action.getHierarchy();
                                ComponentService.removeAllChildRelationships(ctc, hierarchy);

                                // We do a save on the parent of the component-to-configure
                                // instead of on the component-to-configure directly.
                                Component parent = 
                                    action.getParentRelationship().getParentComponent();
                                parentsOfComponentModified.add(parent);
                                break;
                            }
                            case ConfigureAction.ADD_CHILD: {
                                // We need to mark component type as instantiated
                                Component ctc = action.getComponentToConfigure();
                                ComponentTypeStatus cts = 
                                    ctc.getComponentType().getComponentTypeStatus();
                                cts.setInstantiated(true);
                                break;
                            }
                            case ConfigureAction.INSERT_PARENT: {
                                // We need to mark component type as instantiated
                                Component ctc = action.getComponentToConfigure();
                                ComponentTypeStatus cts = 
                                    ctc.getComponentType().getComponentTypeStatus();
                                cts.setInstantiated(true);

                                // We do a save on the parent of the component-to-configure
                                // instead of on the component-to-configure directly.
                                Component parent = 
                                    action.getParentRelationship().getParentComponent();
                                parentsOfComponentModified.add(parent);
                                break;
                            }
                            case ConfigureAction.REPLACE_COMPONENT: {
                                // make sure new component type is marked as instantiated
                                Component ctc = action.getComponentToConfigure();
                                ComponentType ct = ctc.getComponentType();
                                ct.getComponentTypeStatus().setInstantiated(true);
                                break;
                            }
                            }
                            action = _model.popConfigureUndoStack();
                        }
                        _model.clearConfigureUndoStack();
                        
                        if (parentsOfComponentModified.size() > 0) {
                            // modification affects parent too, so save parent instead
                            Iterator parentIt = parentsOfComponentModified.iterator();
                            while (parentIt.hasNext()) {
                                Component parent = (Component)parentIt.next();
                                ComponentService.saveComponent(parent);
                            }

                        } else {
                            // No insert done in object graph, so do regular save of ctc.
                            ComponentService.saveComponent(ctc);
                        }

                        // save list of configure actions performed
                        logAuditActions(configureActionList);

                        // make sure any other idt::component or idt::cable see these changes
                        requestResetOfDependentDocuments();

                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        _model.setIRMISException(ie);
                    }
                }
                
                public void doUIUpdateLogic() {
                    Boolean staleData = new Boolean(stale);
                    // fire off notification of modification to listeners
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.COMMIT_COMPLETE, staleData));
                }
            });
    }

    /**
     * Log the list of configure actions taken to the audit_action table in database.
     * Converts each ConfigureAction into appropriate AuditAction, and then saves
     * the AuditAction. Will log any exceptions, but won't prevent caller from
     * completing.
     */
    private void logAuditActions(List configureActions) {

        // log actions using audit service
        Iterator configureActionIt = configureActions.iterator();
        try {
            while (configureActionIt.hasNext()) {
                ConfigureAction configureAction = (ConfigureAction)configureActionIt.next();
                AuditAction auditAction = null;
                
                // create AuditAction based on ConfigureAction
                switch (configureAction.getType()) {
                case ConfigureAction.ADD_CHILD: {
                    Component ctc = configureAction.getComponentToConfigure();
                    int hierarchy = configureAction.getHierarchy();
                    String desc = "add component to hierarchy "+hierarchy;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.ADD_COMPONENT,
                                                       desc, LoginUtil.getUsername(),
                                                       ctc.getId());
                    break;
                }
                case ConfigureAction.DELETE_CHILD: {
                    Component ctc = configureAction.getComponentToConfigure();
                    int hierarchy = configureAction.getHierarchy();
                    String desc = "delete component from hierarchy "+hierarchy;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.DELETE_COMPONENT,
                                                       desc, LoginUtil.getUsername(),
                                                       ctc.getId());
                    break;
                }
                case ConfigureAction.INSERT_PARENT: {
                    Component newParentComponent = configureAction.getComponentToConfigure();
                    int hierarchy = configureAction.getHierarchy();
                    String desc = "insert parent component in hierarchy "+hierarchy;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.ADD_COMPONENT,
                                                       desc, LoginUtil.getUsername(),
                                                       newParentComponent.getId());
                    break;
                }
                    /*
                case ConfigureAction.RELOCATE_CHILD: {
                    Component ctc = configureAction.getComponentToConfigure();
                    int hierarchy = configureAction.getHierarchy();
                    int index = configureAction.getIndex();
                    String direction = "up";
                    if (index == -1)
                        direction = "down";
                    String desc = "relocate child "+direction+" in hierarchy "+hierarchy;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.MODIFY_LOGICAL_ORDER,
                                                       desc, LoginUtil.getUsername(),
                                                       ctc.getId());
                    break;
                }
                    */
                case ConfigureAction.MODIFY_LOGICAL_DESCRIPTION: {
                    Component ctc = configureAction.getComponentToConfigure();
                    int hierarchy = configureAction.getHierarchy();
                    String origDesc = configureAction.getOriginalString();
                    String newDesc = ctc.getParentRelationship(hierarchy).getLogicalDescription();
                    String desc = "modified ld from "+origDesc+" to "+newDesc+
                        " in hierarchy "+hierarchy;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.MODIFY_LOGICAL_DESCRIPTION,
                                                       desc, LoginUtil.getUsername(),
                                                       ctc.getId());
                    break;
                }
                case ConfigureAction.MODIFY_COMPONENT_NAME: {
                    Component ctc = configureAction.getComponentToConfigure();
                    String origName = configureAction.getOriginalString();
                    String newName = ctc.getComponentName();
                    String desc = "modified component name from "+origName+" to "+newName;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.MODIFY_COMPONENT_NAME,
                                                       desc, LoginUtil.getUsername(),
                                                       ctc.getId());
                    break;
                }
                case ConfigureAction.MODIFY_PARENT: {
                    Component oldParent = configureAction.getComponentToConfigure();
                    int hierarchy = configureAction.getHierarchy();
                    ComponentRelationship oldcr = configureAction.getParentRelationship();
                    String desc = "change parent from id "+oldParent.getId()+" in hierarchy "+hierarchy;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.MODIFY_PARENT,
                                                       desc, LoginUtil.getUsername(),
                                                       oldcr.getChildComponent().getId());
                    break;
                }
                case ConfigureAction.REPLACE_COMPONENT: {
                    Component ctc = configureAction.getComponentToConfigure();
                    ComponentType origType = configureAction.getComponentType();
                    String desc = "modified component type from "+origType.toString();
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.MODIFY_COMPONENT_TYPE,
                                                       desc, LoginUtil.getUsername(),
                                                       ctc.getId());
                    break;
                }
                case ConfigureAction.MODIFY_SERIAL_NUMBER: {
                    Component ctc = configureAction.getComponentToConfigure();
                    String origSerialNumber = configureAction.getOriginalString();
                    String newSerialNumber = ctc.getApsComponent().getSerialNumber();
                    String desc = "modified serial number from "+origSerialNumber+" to "+newSerialNumber;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.MODIFY_SERIAL_NUMBER,
                                                       desc, LoginUtil.getUsername(),
                                                       ctc.getId());
                    break;
                }
                case ConfigureAction.MODIFY_GROUP_NAME: {
                    Component ctc = configureAction.getComponentToConfigure();
                    String origGroupName = configureAction.getOriginalString();
                    String newGroupName = ctc.getApsComponent().getGroupName().getGroupName();
                    String desc = "modified group name from "+origGroupName+" to "+newGroupName;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.MODIFY_GROUP_NAME,
                                                       desc, LoginUtil.getUsername(),
                                                       ctc.getId());
                    break;
                }
                case ConfigureAction.MODIFY_VERIFIED_FLAG: {
                    Component ctc = configureAction.getComponentToConfigure();
                    String origFlag = configureAction.getOriginalString();
                    String newFlag = null;
                    if (ctc.getApsComponent().getVerified())
                        newFlag = "true";
                    else
                        newFlag = "false";
                    String desc = "modified verified from "+origFlag+" to "+newFlag;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.MODIFY_VERIFIED_FLAG,
                                                       desc, LoginUtil.getUsername(),
                                                       ctc.getId());
                    break;
                }
                case ConfigureAction.COMPONENT_VERIFIED: {
                    Component ctc = configureAction.getComponentToConfigure();
                    int hierarchy = configureAction.getHierarchy();
                    String desc = "component verified in hierarchy "+hierarchy;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.MODIFY_VERIFIED_FLAG,
                                                       desc, LoginUtil.getUsername(),
                                                       ctc.getId());
                    break;
                }
                case ConfigureAction.COMPONENT_UNVERIFIED: {
                    Component ctc = configureAction.getComponentToConfigure();
                    int hierarchy = configureAction.getHierarchy();
                    String desc = "component unverified in hierarchy "+hierarchy;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.MODIFY_VERIFIED_FLAG,
                                                       desc, LoginUtil.getUsername(),
                                                       ctc.getId());
                    break;
                }
                case ConfigureAction.MODIFY_COMPONENT_IMAGE_URI: {
                    Component ctc = configureAction.getComponentToConfigure();
                    String origName = configureAction.getOriginalString();
                    String newName = ctc.getImageURI();
                    String desc = "modified component image URL from "+origName+" to "+newName;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.MODIFY_COMPONENT_IMAGE_URI,
                                                       desc, LoginUtil.getUsername(),
                                                       ctc.getId());
                    break;
                }
                default: {}
                }
                if (auditAction != null)
                    AuditService.saveAuditAction(auditAction);
            }
        } catch (IRMISException ie) {
            ie.printStackTrace();
            Logger.getLogger("global").log(Level.WARNING, "IRMIS Service Error.", ie);
        }
    }

    /**
     * Add the given child component to the parent using a relationship type
     * given by hierarchy.
     *
     * @return number of children the parent has after the addition
     */
    public int actionAddNewChildComponent(Component parentComponent,
                                           Component childComponent,
                                           int hierarchy) {

        int childListSize = 0;
        try {
            ComponentService.addChildComponent(parentComponent, childComponent, hierarchy);
            
            ComponentRelationship newCR = childComponent.getParentRelationship(hierarchy);
            List childList = parentComponent.getChildRelationships(hierarchy);
            childListSize = childList.size();
            int addIndex = childList.indexOf(newCR);

            // add this to undo configure stack
            ConfigureAction action = 
                new ConfigureAction(ConfigureAction.ADD_CHILD, childComponent, newCR,
                                    addIndex, hierarchy);
            _model.pushConfigureUndoStack(action);
            requestResetOfDependentDocuments();

        } catch (IRMISException ie) {
            ie.printStackTrace();
        }

        return childListSize;
    }

    /**
     * Insert the given newParentComponent as the new parent for the existing
     * currentComponent. Do this in the given hierarchy.
     *
     */
    public void actionInsertNewParentComponent(Component currentComponent,
                                               Component newParentComponent,
                                               int hierarchy) {

        ComponentRelationship currentComponentParentRel = 
            currentComponent.getParentRelationship(hierarchy);
        int subtreeChildIndex = currentComponentParentRel.getLogicalOrder();
        Person verifiedPerson = currentComponentParentRel.getVerifiedPerson();
        
        try {
            ComponentService.insertParentComponent(currentComponent, newParentComponent,
                                                   hierarchy);
            // add this to undo configure stack
            ConfigureAction action = 
                new ConfigureAction(ConfigureAction.INSERT_PARENT, newParentComponent, 
                                    currentComponentParentRel, subtreeChildIndex, hierarchy, 
                                    null, null, verifiedPerson);
            _model.pushConfigureUndoStack(action);
            requestResetOfDependentDocuments();

        } catch (IRMISException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Change the current parent of componentToConfigure to newParentComponent.
     * currentComponent. Do this in the given hierarchy.
     *
     */
    public void actionChangeParentOfComponent(Component newParentComponent,
                                              Component componentToConfigure,
                                              int hierarchy) {

        ComponentRelationship oldParentRel = 
            componentToConfigure.getParentRelationship(hierarchy);
        Person verifiedPerson = oldParentRel.getVerifiedPerson();
        Component oldParentComp = oldParentRel.getParentComponent();
        int subtreeChildIndex = oldParentRel.getLogicalOrder();
        
        try {
            ComponentService.changeParentComponent(componentToConfigure, newParentComponent, -1,
                                                   hierarchy);
            // add this to undo configure stack
            ConfigureAction action = 
                new ConfigureAction(ConfigureAction.MODIFY_PARENT, oldParentComp, 
                                    oldParentRel, subtreeChildIndex, hierarchy,
                                    null, null, verifiedPerson);
            _model.pushConfigureUndoStack(action);
            requestResetOfDependentDocuments();

        } catch (IRMISException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Delete requested component from given hierarchy.
     */
    public int actionDeleteComponent(Component c, int hierarchy) {
        
        ComponentRelationship cr = c.getParentRelationship(hierarchy);
        int childIndex = -1;
        try {
            childIndex = 
                ComponentService.deleteChildComponent(c, true, hierarchy);
            if (childIndex != -1) {
                // add this to undo configure stack
                ConfigureAction action = 
                    new ConfigureAction(ConfigureAction.DELETE_CHILD, c, cr,
                                        childIndex, hierarchy);
                _model.pushConfigureUndoStack(action);
                requestResetOfDependentDocuments();
            }
        } catch (IRMISException ie) {
            ie.printStackTrace();
        }
        return childIndex;
    }

    /**
     * Move a child component up or down in relation to the other children.
     */
    public void actionRelocateComponent(Component c, int hierarchy, boolean up) {
        ComponentRelationship cr = 
            c.getParentRelationship(hierarchy);
        if (cr != null) {
            int cindex = cr.getLogicalOrder();
            if (up)
                cindex--;
            else
                cindex++;
            try {
                ComponentService.
                    relocateChildComponent(c, hierarchy, cindex);
                // add this to undo configure stack
                ConfigureAction action = 
                    new ConfigureAction(ConfigureAction.RELOCATE_CHILD, c, cr,
                                        (up?1:-1), hierarchy);            
                _model.pushConfigureUndoStack(action);
                requestResetOfDependentDocuments();

            } catch (IRMISException ie) {
                // index out of range. do nothing.
            }
        }
    }

    /**
     * Move a child component up or down in relation to the other children.
     */
    public void actionReplaceComponentType(Component c, ComponentType newType) {

        ComponentType originalType = c.getComponentType();
        c.setComponentType(newType);
        
        // add this to undo configure stack
        ConfigureAction action = 
            new ConfigureAction(ConfigureAction.REPLACE_COMPONENT, c, null,
                                0, 0, null, originalType, null);            
        _model.pushConfigureUndoStack(action);
        requestResetOfDependentDocuments();

        // re-use this event to tell gui to redraw this component's info
        _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.MODIFY_COMPONENT_NAME, action));
        
    }

    /**
     * Mark a component as verified or unverified within the given hierarchy.
     * The component is assumed to be selected already.
     *
     * @param hierarchy integer hierarchy as defined in ComponentRelationshipType
     * @param username unix userid or null if unverifying the component
     */
    public void actionSetVerifiedPerson(int hierarchy, String username) {
        Component c = _model.getSelectedComponent();
        ComponentRelationship cr = 
            c.getParentRelationship(hierarchy);
        Person verifiedPerson = null;
        ConfigureAction action = null;
        if (username != null) {
            try {
                verifiedPerson = PersonService.findPersonByUserName(username);
            } catch (IRMISException ie) {
            }
            if (cr.getVerifiedPerson() == null) {
                cr.setVerifiedPerson(verifiedPerson);
                action = new ConfigureAction(ConfigureAction.COMPONENT_VERIFIED, c, cr,
                                             0, hierarchy, username);            
                _model.pushConfigureUndoStack(action);
            }
        } else {
            if (cr.getVerifiedPerson() != null) {
                Person person = cr.getVerifiedPerson();
                cr.setVerifiedPerson(null);
                action = 
                    new ConfigureAction(ConfigureAction.COMPONENT_UNVERIFIED, c, cr,
                                        0, hierarchy, person.getUserid());
                _model.pushConfigureUndoStack(action);            
            }
        }
    }
    
    /**
     * Undo any changes that have been made since the last commit. Works
     * through an undo stack until it's empty. We might be called from
     * an "actionRedraw()", in which case we skip a few steps.
     *
     * @param redrawInProgress true if we are being called from actionRedraw method
     */
    public void actionUndoComponentConfigure(boolean redrawInProgress) {

        // process the stack of configure actions, undoing each in turn
        ConfigureAction action = _model.popConfigureUndoStack();
        while (action != null) {
            switch (action.getType()) {
            case ConfigureAction.ADD_CHILD: {
                Component ctc = action.getComponentToConfigure();
                int hierarchy = action.getHierarchy();
                try {
                    ComponentService.deleteChildComponent(ctc, true, hierarchy);
                } catch (IRMISException ie) {
                    ie.printStackTrace();
                }

                if (!redrawInProgress) {
                    // notify GUI to update display
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.CHILD_REMOVED, action));
                }
                break;
            }
            case ConfigureAction.DELETE_CHILD: {
                Component ctc = action.getComponentToConfigure();
                int hierarchy = action.getHierarchy();
                int index = action.getIndex();
                ComponentRelationship cr = action.getParentRelationship();
                Component pc = null;
                List children = null;
                if (cr != null) {
                    ctc.setParentRelationship(cr);
                    pc = cr.getParentComponent();
                    children = pc.getChildRelationships(hierarchy);
                    children.add(index,cr);

                    // un-mark for delete
                    cr.setMarkForDelete(false);
                    ctc.setMarkForDeleteAndPropagate(false);
                    
                    // renumber logical order of all children
                    int i = 0;
                    Iterator childIt = children.iterator();
                    while (childIt.hasNext()) {
                        ComponentRelationship tcr = (ComponentRelationship)childIt.next();
                        tcr.setLogicalOrder(i++);
                    }
                }
                if (!redrawInProgress) {
                    // notify GUI to update display
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.CHILD_ADDED, action));
                }
                break;
            }
            case ConfigureAction.INSERT_PARENT: {
                int hierarchy = action.getHierarchy();
                Component ctc = action.getComponentToConfigure();
                ComponentRelationship ctcParentRel = ctc.getParentRelationship(hierarchy);
                Component ctcParent = ctcParentRel.getParentComponent();
                int index = action.getIndex();
                ComponentRelationship cr = action.getParentRelationship();
                Person verifiedPerson = action.getVerifiedPerson();
                List children = ctcParent.getChildRelationships(hierarchy);
                children.remove(ctcParentRel);
                cr.setLogicalOrder(index);
                cr.setVerifiedPerson(verifiedPerson);
                cr.setParentComponent(ctcParent);
                ctcParent.addChildRelationship(cr);

                if (!redrawInProgress) {
                    // notify GUI to update display
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.CHILD_ADDED, action));
                }
                break;
            }
            case ConfigureAction.RELOCATE_CHILD: {
                int hierarchy = action.getHierarchy();
                Component ctc = action.getComponentToConfigure();
                int index = action.getIndex();
                ComponentRelationship cr = action.getParentRelationship();
                int cindex = cr.getLogicalOrder();
                if (index == 1)  // will be 1 for up, or -1 for down
                    cindex++;
                else
                    cindex--;
                try {
                    ComponentService.
                        relocateChildComponent(ctc, hierarchy, cindex);
                } catch (IRMISException ie) {
                    // do nothing
                    ie.printStackTrace();
                }
                 
                if (!redrawInProgress) {   
                    // notify GUI to update display
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.CHILD_RELOCATED, action));
                }
                break;
            }
            case ConfigureAction.MODIFY_LOGICAL_DESCRIPTION: {
                int hierarchy = action.getHierarchy();
                Component ctc = action.getComponentToConfigure();
                String origDesc = action.getOriginalString();
                ComponentRelationship cr = action.getParentRelationship();
                cr.setLogicalDescription(origDesc);
                 
                if (!redrawInProgress) {   
                    // notify GUI to update display
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.MODIFY_LOGICAL_DESCRIPTION, action));
                }
                break;
            }
            case ConfigureAction.MODIFY_COMPONENT_NAME: {
                Component ctc = action.getComponentToConfigure();
                String origName = action.getOriginalString();
                ctc.setComponentName(origName);
                 
                if (!redrawInProgress) {   
                    // notify GUI to update display
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.MODIFY_COMPONENT_NAME, action));
                }
                break;
            }
            case ConfigureAction.MODIFY_PARENT: {
                Component oldParent = action.getComponentToConfigure();
                int hierarchy = action.getHierarchy();
                int index = action.getIndex();
                Person verifiedPerson = action.getVerifiedPerson();
                ComponentRelationship cr = action.getParentRelationship();
                Component ctc = cr.getChildComponent();
                Component pc = null;
                List children = null;

                try {
                    ComponentService.changeParentComponent(ctc, oldParent, index, hierarchy);
                    cr.setVerifiedPerson(verifiedPerson);
                } catch (IRMISException ie) {
                    ie.printStackTrace();
                }

                if (!redrawInProgress) {
                    // notify GUI to update display
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.CHILD_REMOVED, action));
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.CHILD_ADDED, action));
                }
                break;
            }
            case ConfigureAction.REPLACE_COMPONENT: {
                Component ctc = action.getComponentToConfigure();
                ComponentType origType = action.getComponentType();
                ctc.setComponentType(origType);
                 
                if (!redrawInProgress) {   
                    // notify GUI to update display (re-use existing event here)
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.MODIFY_COMPONENT_NAME, action));
                }
                break;
            }
            case ConfigureAction.MODIFY_SERIAL_NUMBER: {
                Component ctc = action.getComponentToConfigure();
                String origSerialNumber = action.getOriginalString();
                ctc.getApsComponent().setSerialNumber(origSerialNumber);
                 
                if (!redrawInProgress) {   
                    // notify GUI to update display
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.MODIFY_COMPONENT_NAME, action));
                }
                break;
            }
            case ConfigureAction.MODIFY_GROUP_NAME: {
                Component ctc = action.getComponentToConfigure();
                String origGroupName = action.getOriginalString();
                GroupName gn = _model.findGroupName(origGroupName);
                ctc.getApsComponent().setGroupName(gn);
                 
                if (!redrawInProgress) {   
                    // notify GUI to update display
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.MODIFY_COMPONENT_NAME, action));
                }
                break;
            }
            case ConfigureAction.MODIFY_VERIFIED_FLAG: {
                Component ctc = action.getComponentToConfigure();
                String origFlag = action.getOriginalString();
                if (origFlag.equals("true"))
                    ctc.getApsComponent().setVerified(true);
                else
                    ctc.getApsComponent().setVerified(false);
                 
                if (!redrawInProgress) {   
                    // notify GUI to update display
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.MODIFY_COMPONENT_NAME, action));
                }
                break;
            }
            case ConfigureAction.COMPONENT_VERIFIED: {
                Component ctc = action.getComponentToConfigure();
                String username = action.getOriginalString();
                int hierarchy = action.getHierarchy();
                 
                ComponentRelationship cr = ctc.getParentRelationship(hierarchy);
                if (cr != null) {
                    cr.setVerifiedPerson(null);
                }
                if (!redrawInProgress) {   
                    // notify GUI to update display
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.MODIFY_LOGICAL_DESCRIPTION, action));
                }
                break;
            }
            case ConfigureAction.COMPONENT_UNVERIFIED: {
                Component ctc = action.getComponentToConfigure();
                String username = action.getOriginalString();
                int hierarchy = action.getHierarchy();
                 
                ComponentRelationship cr = ctc.getParentRelationship(hierarchy);
                if (cr != null) {
                    Person verifiedPerson = null;
                    try {
                        verifiedPerson = PersonService.findPersonByUserName(username);
                    } catch (IRMISException ie) {
                    }
                    cr.setVerifiedPerson(verifiedPerson);
                }
                if (!redrawInProgress) {   
                    // notify GUI to update display
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.MODIFY_LOGICAL_DESCRIPTION, action));
                }
                break;
            }
            case ConfigureAction.MODIFY_COMPONENT_IMAGE_URI: {
                Component ctc = action.getComponentToConfigure();
                String origName = action.getOriginalString();
                ctc.setImageURI(origName);
                 
                if (!redrawInProgress) {   
                    // notify GUI to update display
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.MODIFY_COMPONENT_NAME, action));
                }
                break;
            }
            }
            action = _model.popConfigureUndoStack();
        }

        if (!redrawInProgress) {
            requestResetOfDependentDocuments();
        }
    }

    /**
     * Pop up the ComponentType Viewer application on the desktop, preselected for
     * the given component type.
     */
    public void actionProduceComponentTypeDocument(String ctName) {
        Application app = Application.getApp();
        List docs = app.getDocuments();
        
        // first see if it is already on desktop 
        Iterator docIt = docs.iterator();
        ComponentTypeDocument ctDoc = null;
        while (docIt.hasNext()) {
            XalInternalDocument doc = (XalInternalDocument)docIt.next();
            if (doc.getTitle().equals("idt::component-type")) {
                ctDoc = (ComponentTypeDocument)doc;
                break;
            }
        }
        if (ctDoc == null) {
            ctDoc = new ComponentTypeDocument();
            app.produceDocument(ctDoc);
        }

        // now tell the doc to preselect for our component type
        ctDoc.actionSelectComponentType(ctName);
    }

    /**
     * Load up all children of given component c in a particular hierarchy. 
     * Normally these children are lazily loaded, so this is explicit way 
     * to get it done all at once.
     */
    public void actionFindAllChildren(Component c, int hierarchy) {
        final Component selectComponent = c;
        final int h = hierarchy;
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {

                public void doNonUILogic() {
                    _model.setIRMISException(null);
                    // ignore returned list since we are just making sure
                    // object cache is pre-loaded
                    ComponentService.findAllChildren(selectComponent, h);
                }

                public void doUIUpdateLogic() {
                    // add this selection request to Swing event queue
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.EXPAND_CHILDREN, selectComponent));
                }
            });

    }

    /**
     * Scan all component types and determine which can serve as an
     * in-place replacement for the given component instance c. Also
     * make sure the ports and cables of c are instantiated.
     */
    public void actionPrepareReplacementList(Component c) {
        final Component selectComponent = c;
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private List replacementTypes = null;

                public void doNonUILogic() {
                    _model.setIRMISException(null);

                    // find which component types are possible replacements for c
                    List ctList = _model.getComponentTypes();
                    Iterator ctIt = ctList.iterator();
                    replacementTypes = new ArrayList();
                    while (ctIt.hasNext()) {
                        ComponentType ctCandidate = (ComponentType)ctIt.next();
                        if (!ctCandidate.equals(selectComponent.getComponentType()) &&
                            ComponentService.isValidReplacementComponentType(selectComponent, ctCandidate))
                            replacementTypes.add(ctCandidate);
                    }

                    // make sure all ports and cables of c are instantiated
                    List ports = selectComponent.getComponentPorts();
                    Iterator portIt = ports.iterator();
                    while (portIt.hasNext()) {
                        ComponentPort port = (ComponentPort)portIt.next();
                        List cables = port.getCables();
                        if (cables != null)
                            cables.iterator();
                    }
                }

                public void doUIUpdateLogic() {
                    _model.setReplacementComponentTypes(replacementTypes);
                    // add this selection request to Swing event queue
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.REPLACEMENT_LIST, selectComponent));
                }
            });

    }

    /**
     * Programatically select given component in housing hierarchy.
     */
    public void actionSelectComponent(Component c) {
        final Component selectComponent = c;
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {

                public void doNonUILogic() {
                    _model.setIRMISException(null);
                    // do nothing really, but we need to queue up
                    // in case this document is not finished
                    // with its initial setup and rendering
                }

                public void doUIUpdateLogic() {
                    // add this selection request to Swing event queue
                    _model.notifyComponentModelListeners(new ComponentModelEvent(ComponentModelEvent.SELECT_COMPONENT_IN_HOUSING, selectComponent));
                }
            });

    }

    /**
     * Update our current component type list using given list. This is usually
     * invoked when idt::component-type has added a new component type.
     */
    public void actionUpdateComponentTypeList(List newComponentTypeList) {
        _model.setComponentTypes(newComponentTypeList);
    }

	/**
	 * Get the launch model which represents the main model of this document
	 * @return the main model of this document
	 */
	public ComponentModel getModel() {
		return _model;
	}

    /**
     * Goes through IRMIS desktop list of documents and does an actionReset()
     * on those that are dependent on our state.
     */
    private void requestResetOfDependentDocuments() {
        //Application app = Application.getApp();
        //List docs = app.getDocuments();

        /*
        // iterate, looking for all other idt::component docs
        Iterator docIt = docs.iterator();
        while (docIt.hasNext()) {
            XalInternalDocument doc = (XalInternalDocument)docIt.next();

            // If doc is an instance of idt::component (and not us), request reset
            if (doc.getTitle().equals("idt::component") && !doc.equals(this)) {
                ComponentDocument cDoc = (ComponentDocument)doc;
                cDoc.actionReset();

            } else if (doc.getTitle().equals("idt::cable")) {  // ditto for idt::cable
                CableDocument cDoc = (CableDocument)doc;
                cDoc.actionReset();
            }
        }
        */
    }

    private ComponentWindow myWindow() {
        return (ComponentWindow)_mainWindow;
    }
}
