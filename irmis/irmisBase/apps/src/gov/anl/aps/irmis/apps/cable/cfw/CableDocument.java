/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.cable.cfw;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;
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
import gov.anl.aps.irmis.apps.component.cfw.ComponentDocument;
import gov.anl.aps.irmis.apps.irmis.cfw.Main;

// persistence layer
import gov.anl.aps.irmis.persistence.SemaphoreValue;
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentRelationship;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.component.ComponentPort;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.Cable;
import gov.anl.aps.irmis.persistence.audit.AuditAction;
import gov.anl.aps.irmis.persistence.audit.AuditActionType;
import gov.anl.aps.irmis.persistence.DAOException;

// service layer
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.shared.AuditService;
import gov.anl.aps.irmis.service.pv.PVService;
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.component.ComponentService;


/**
 * The primary controller class for IRMIS Cable Viewer application.
 * Provides "action" methods to to query the IRMIS database for cable/conductor
 * related data. These action methods are typically invoked by the 
 * <code>CableWindow</code>.
 * Any time-consuming activity should be queued up using the 
 * <code>queuedExecutor</code>, which is similar to the SwingWorker concept.
 */
public class CableDocument extends XalInternalDocument {

	/** The main data model of this application */
	private CableModel _model;

    // Hold reference to parent desktop frame. Used to put up busy icon during work.
    JFrame _appFrame;

	/** Create a new empty document */
    public CableDocument() {
        this(null);
        _model = new CableModel();
    }

	/** Create a new document from url */
    public CableDocument(final URL url) {
        //setSource(url);
        _model = new CableModel();
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
        String modifiedTitle = "idt::cable" + newTitle;
		super.setTitle( modifiedTitle );
        _documentListenerProxy.titleChanged( this, title );
    }	
            
    /**
     * Make a main window by instantiating the my custom window.
     */
    public void makeMainWindow() {
        _mainWindow = new CableWindow(this);

        // Initialize application data from database and do a full redraw of GUI.
        actionReload();
        actionRedraw();
    }

    /**
     * Clear out all the data in the model.
     */
    public void actionResetModel() {
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                public void doNonUILogic() {
                    actionUndo(true);
                    _model.reset();
                }
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                    _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.REDRAW));
                }
            });
    }

    /**
     * Queue up job for background thread to gather the current housing and
     * control hierarchies, along with any other data we will need
     * regularly during execution. When complete, fire off 
     * <code>CableModelEvent</code> to notify GUI that new data is available
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
                public void doNonUILogic() {

                    // Get current set of Components of type room, and put into model
                    _model.setIRMISException(null);
                    try {
                        // put up progress dialog, since this can take a while
                        ProgressDialog.show(_appFrame,1,4);

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

                        ProgressDialog.setValue(1);

                        // control hierarchy
                        ComponentType networkCT = 
                            ComponentTypeService.findComponentTypeByName("Network");
                        List networks = ComponentService.findComponentsByType(networkCT);
                        // there better be just one
                        if (networks.size() != 1) {
                            throw new IRMISException("Unable to find root of control hierarchy (Component Type: Network)");
                        }
                        networkComponent = (Component)networks.get(0);
                        ProgressDialog.setValue(2);

                        // get list of ioc system names
                        systemList = PVService.findSystems();

                        ProgressDialog.setValue(3);

                        // if we had a previously selected component or port, reload it explicitly
                        Component c1 = _model.getSelectedComponent1();
                        /* haven't got this working yet
                        ComponentPort p1 = _model.getSelectedPort1();
                        System.out.println("reloading selected component and/or port");
                        if (p1 != null) {
                            System.out.println("reloading selected port: "+p1);
                            Component c = ComponentService.findComponentById(p1.getComponent().getId());
                            List ports = c.getComponentPorts();
                            Iterator portIt = ports.iterator();
                            while (portIt.hasNext()) {
                                ComponentPort p = (ComponentPort)portIt.next();
                                System.out.println("iterating on port: "+p);
                                if (p.equals(p1)) {
                                    p1 = p;
                                    System.out.println("found port!");
                                    break;
                                }
                            }
                            _model.setSelectedComponent1(null);
                            _model.setSelectedPort1(p1);
                        } else if (c1 != null && c1.getId() != null) {
                            c1 = ComponentService.findComponentById(c1.getId());
                            _model.setSelectedComponent1(c1);
                            _model.setSelectedPort1(null);
                        }
                        if (p1 == null) 
                            _model.setSelectedComponent1(c1);
                        else
                            _model.setSelectedPort1(p1);
                        */
                        if (c1 != null && c1.getId() != null) {
                            c1 = ComponentService.findComponentById(c1.getId());
                            _model.setSelectedComponent1(c1);
                            _model.setSelectedPort1(null);
                        }

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
                    _model.setComponentTypes(componentTypes);
                    _model.setSystems(systemList);
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
                    _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.REDRAW));
                }
            });
    }


    public void actionSetComponentBUsingCable(Cable cable) {
        final Cable selectedCable = cable;
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                ComponentPort port = null;
                public void doNonUILogic() {
                    // cable can attach at either end, so figure out which is which
                    ComponentPort portA = selectedCable.getComponentPortA();
                    ComponentPort portB = selectedCable.getComponentPortB();
                    if (portA != null && portA.equals(_model.getSelectedPort1()))
                        port = portB;
                    else
                        port = portA;
                }
                
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                    _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.NEW_COMPONENT_B_AND_PORT, port));
                }
            });        
    }

    public Cable actionAddNewCable(ComponentPort selectedPort1, ComponentPort selectedPort2) {
        Cable newCable = null;
        newCable = new Cable();
        newCable.setLabel("");
        newCable.setColor("");
        newCable.setDestinationDescription("");
        newCable.setVirtual(false);
        selectedPort1.addCableA(newCable);
        if (selectedPort2 != null)  // don't do this if single-ended
            selectedPort2.addCableB(newCable);
        
        CableAction action = new CableAction(CableAction.ADD_CABLE, newCable, selectedPort1, selectedPort2);
        _model.pushCableUndoStack(action);
        
        requestResetOfDependentDocuments();
        return newCable;
    }

    public void actionUpdateCableSingleToDoubleEnded(Cable cable, ComponentPort selectedPortA, 
                                                     ComponentPort selectedPortB) {
        
        ComponentPort cablePortA = cable.getComponentPortA();
        ComponentPort cablePortB = cable.getComponentPortB();
        CableAction action = null;
        if (cablePortA != null && cablePortA.equals(selectedPortA)) {
            selectedPortB.addCableB(cable);
            action = new CableAction(CableAction.SINGLE_TO_DOUBLE_ENDED, 
                                     cable, "B", selectedPortA, selectedPortB);
            _model.pushCableUndoStack(action);
        } else if (cablePortB != null && cablePortB.equals(selectedPortA)) {
            selectedPortB.addCableA(cable);
            action = new CableAction(CableAction.SINGLE_TO_DOUBLE_ENDED, 
                                     cable, "A", selectedPortA, selectedPortB);
            _model.pushCableUndoStack(action);
        }
        requestResetOfDependentDocuments();
    }

    public void actionUpdateCableDoubleToSingleEnded(Cable cable, ComponentPort selectedPortA, 
                                                     ComponentPort selectedPortB) {
        
        ComponentPort cablePortA = cable.getComponentPortA();
        ComponentPort cablePortB = cable.getComponentPortB();
        CableAction action = null;
        if (cablePortA != null && cablePortA.equals(selectedPortB)) {
            selectedPortB.removeCableA(cable);
            action = new CableAction(CableAction.DOUBLE_TO_SINGLE_ENDED, 
                                     cable, "A", selectedPortA, selectedPortB);
            _model.pushCableUndoStack(action);
            
        } else if (cablePortB != null && cablePortB.equals(selectedPortB)) {
            selectedPortB.removeCableB(cable);
            action = new CableAction(CableAction.DOUBLE_TO_SINGLE_ENDED, 
                                     cable, "B", selectedPortA, selectedPortB);
            _model.pushCableUndoStack(action);
        }        
        requestResetOfDependentDocuments();
    }

    public void actionUpdateCableVirtualFlag(Cable cable, boolean virtual) {
        
        cable.setVirtual(virtual);
        CableAction action = new CableAction(CableAction.UPDATE_VIRTUAL, cable, null);
        _model.pushCableUndoStack(action);
        
        requestResetOfDependentDocuments();
    }

    public void actionUpdateCableLabel(Cable cable, String newStr) {

        String origStr = cable.getLabel();        
        cable.setLabel(newStr);
        CableAction action = new CableAction(CableAction.UPDATE_LABEL, cable, origStr);
        _model.pushCableUndoStack(action);
        
        requestResetOfDependentDocuments();
    }    

    public void actionUpdateCableColor(Cable cable, String newStr) {
        
        String origStr = cable.getColor();
        cable.setColor(newStr);
        CableAction action = new CableAction(CableAction.UPDATE_COLOR, cable, origStr);
        _model.pushCableUndoStack(action);
        
        requestResetOfDependentDocuments();
    }    

    public void actionUpdateCableDestination(Cable cable, String newStr) {
        
        String origStr = cable.getDestinationDescription();
        cable.setDestinationDescription(newStr);        
        CableAction action = new CableAction(CableAction.UPDATE_DEST, cable, origStr);
        _model.pushCableUndoStack(action);
        
        requestResetOfDependentDocuments();
    }    

    public void actionSave() {
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private boolean stale = false;
                public void doNonUILogic() {
                    _model.setIRMISException(null);
                    try {
                        // Take semaphore to ensure this is a safe edit
                        try {
                            SemaphoreValue sv = ComponentService.takeSemaphore(LoginUtil.getUsername());
                            _model.setSemaphoreValue(sv);
                            if (sv.getSemaphoreValue() == 0) {
                                String userid = "unknown";
                                if (sv.getUserid() != null)
                                    userid = sv.getUserid();
                                Application.displayWarning("IRMIS Service Warning","User "+userid+" is currently editing, please try again in a few moments.");
                                return;
                                
                            } else if (sv.getModifiedDate() > _model.getDataTimestamp()) {
                                Application.displayWarning("IRMIS Service Warning","Your data is stale. We will reload the data, after which you may try again.");
                                Main.requestResetOfDocuments();                    
                                // give back semaphore
                                if (ComponentService.giveSemaphore(_model.getSemaphoreValue()))
                                    _model.setDataTimestamp(_model.getSemaphoreValue().getModifiedDate());
                                return;
                            }
                        } catch (IRMISException ie) {
                            Logger.getLogger("global").log(Level.WARNING, "IRMIS Service Warning.", ie);
                            ie.printStackTrace();
                            return;
                        }  // done taking semaphore

                        ArrayList cableActionList = new ArrayList();

                        // Process the stack of cable actions.
                        CableAction action = _model.popCableUndoStack();
                        while (action != null) {
                            // make reverse order list of configure actions
                            cableActionList.add(0, action);

                            // do any last-step logic here (if needed)

                            action = _model.popCableUndoStack();
                        }
                        _model.clearCableUndoStack();

                        // save data in order they were performed
                        Iterator actionIt = cableActionList.iterator();
                        while (actionIt.hasNext()) {
                            action = (CableAction)actionIt.next();
                            Cable cable = (Cable)action.getCable();
                            ComponentService.saveCable(cable);
                        }

                        // give semaphore back
                        try {
                            if (ComponentService.giveSemaphore(_model.getSemaphoreValue()))
                                _model.setDataTimestamp(_model.getSemaphoreValue().getModifiedDate());
                            
                        } catch (IRMISException ie) {
                            Logger.getLogger("global").log(Level.WARNING, "IRMIS Service Warning.", ie);
                            ie.printStackTrace();
                        }

                        logAuditActions(cableActionList);
                        requestResetOfDependentDocuments();
                        
                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        _model.setIRMISException(ie);
                    }
                }
                
                public void doUIUpdateLogic() {
                    Boolean staleData = new Boolean(stale);
                    _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.SAVE_COMPLETE, staleData));
                }
            });        
    }

    /**
     * Log the list of cable actions taken to the audit_action table in database.
     * Converts each CableAction into appropriate AuditAction, and then saves
     * the AuditAction. Will log any exceptions, but won't prevent caller from
     * completing.
     */
    private void logAuditActions(List cableActions) {

        // log actions using audit service
        Iterator cableActionIt = cableActions.iterator();
        try {
            while (cableActionIt.hasNext()) {
                CableAction cableAction = (CableAction)cableActionIt.next();
                AuditAction auditAction = null;
                
                // create AuditAction based on CableAction
                switch (cableAction.getType()) {
                case CableAction.ADD_CABLE: {
                    Cable cable = cableAction.getCable();
                    String desc = "new cable";
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.ADD_CABLE,
                                                       desc, LoginUtil.getUsername(),
                                                       cable.getId());                        
                    break;
                }
                case CableAction.REMOVE_CABLE: {
                    Cable cable = cableAction.getCable();
                    String desc = "remove cable";
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.REMOVE_CABLE,
                                                       desc, LoginUtil.getUsername(),
                                                       cable.getId());                        
                    break;
                }
                case CableAction.SINGLE_TO_DOUBLE_ENDED: {
                    Cable cable = cableAction.getCable();
                    String desc = "single to double ended";
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.EDIT_CABLE,
                                                       desc, LoginUtil.getUsername(),
                                                       cable.getId());                        
                    break;
                }
                case CableAction.DOUBLE_TO_SINGLE_ENDED: {
                    Cable cable = cableAction.getCable();
                    String desc = "double to single ended";
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.EDIT_CABLE,
                                                       desc, LoginUtil.getUsername(),
                                                       cable.getId());                        
                    break;
                }
                case CableAction.UPDATE_VIRTUAL: {
                    Cable cable = cableAction.getCable();
                    boolean virt = cable.getVirtual();
                    String desc = "virtual from "+!virt+" to "+virt;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.EDIT_CABLE,
                                                       desc, LoginUtil.getUsername(),
                                                       cable.getId());                        
                    break;
                }
                case CableAction.UPDATE_LABEL: {
                    Cable cable = cableAction.getCable();
                    String newLabel = cable.getLabel();
                    String oldLabel = cableAction.getOriginalString();
                    String desc = "label from "+oldLabel+" to "+newLabel;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.EDIT_CABLE,
                                                       desc, LoginUtil.getUsername(),
                                                       cable.getId());                        
                    break;
                }
                case CableAction.UPDATE_COLOR: {
                    Cable cable = cableAction.getCable();
                    String newColor = cable.getColor();
                    String oldColor = cableAction.getOriginalString();
                    String desc = "color from "+oldColor+" to "+newColor;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.EDIT_CABLE,
                                                       desc, LoginUtil.getUsername(),
                                                       cable.getId());                        
                    break;
                }
                case CableAction.UPDATE_DEST: {
                    Cable cable = cableAction.getCable();
                    String newDest = cable.getDestinationDescription();
                    String oldDest = cableAction.getOriginalString();
                    String desc = "dest from "+oldDest+" to "+newDest;
                    auditAction = 
                        AuditService.createAuditAction(AuditActionType.EDIT_CABLE,
                                                       desc, LoginUtil.getUsername(),
                                                       cable.getId());                        
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

    public void actionRemoveCable(Cable cable) {

        boolean newCable = (cable.getId() == null);
        cable.setMarkForDelete(true);
        ComponentPort p1 = cable.getComponentPortA();
        ComponentPort p2 = cable.getComponentPortB();
        if (p1 != null) {
            p1.removeCableA(cable);
        }
        if (p2 != null) {
            p2.removeCableB(cable);
        }

        CableAction action = new CableAction(CableAction.REMOVE_CABLE, cable, p1, p2);
        _model.pushCableUndoStack(action);

        requestResetOfDependentDocuments();

    }

    /**
     * Query db to find the cable between port1 and port2 (if any).
     */
    public void actionFindCable(ComponentPort port1, ComponentPort port2) {
        final ComponentPort selectedPort1 = port1;
        final ComponentPort selectedPort2 = port2;
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                List cables = null;
                Cable cable = null;
                public void doNonUILogic() {
                    _model.setIRMISException(null);                    
                    try {
                        // find new cables 
                        cables = ComponentService.findCablesByPort(selectedPort1);
                        if (cables != null) {
                            Iterator cableIt = cables.iterator();
                            while (cableIt.hasNext()) {
                                Cable tempCable = (Cable)cableIt.next();
                                ComponentPort portA = tempCable.getComponentPortA();
                                ComponentPort portB = tempCable.getComponentPortB();
                                if ((portA != null && portA.equals(selectedPort2)) ||
                                    (portB != null && portB.equals(selectedPort2))) {
                                    cable = tempCable;
                                    break;
                                }
                            }
                        }
                        _model.setSelectedCable(cable);
                        //System.out.println("actionFindCable:");
                        
                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        _model.setIRMISException(ie);
                    }
                }
                
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                        _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.NEW_CABLE,cable));
                }
            });

    }

    /**
     * Query db to find cable(s) attached to given port (if any).
     */
    public void actionFindCables(ComponentPort port) {
        final ComponentPort selectedPort = port;
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                List cables = null;
                public void doNonUILogic() {
                    _model.setIRMISException(null);                    
                    try {
                        // get new list of cables
                        cables = ComponentService.findCablesByPort(selectedPort);
                        //System.out.println("actionFindCables: found "+cables.size()+" cables");
                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        _model.setIRMISException(ie);
                    }
                }
                
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                        _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.NEW_CABLES,cables));
                }
            });

    }

    /**
     * Query db to find cable(s) attached to given port (if any).
     */
    public void actionFindCablesByLabel(String labelSearch) {
        final String labelSearchText = labelSearch;
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                List cables = null;
                public void doNonUILogic() {
                    _model.setIRMISException(null);                    
                    try {
                        // get new list of cables
                        cables = ComponentService.findCablesByLabel(labelSearchText);
                        //System.out.println("actionFindCabledByLabel: found "+cables.size()+" cables");
                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        _model.setIRMISException(ie);
                    }
                }
                
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                        _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.NEW_CABLES,cables));
                }
            });

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
                    _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.EXPAND_CHILDREN, selectComponent));
                }
            });

    }

   /**
     * Undo any changes that have been made since the last commit. Works
     * through an undo stack until it's empty. We might be called from
     * an "actionRedraw()", in which case we skip a few steps.
     *
     * @param redrawInProgress true if we are being called from actionRedraw method
     */
    public void actionUndo(boolean redrawInProgress) {

        // process the stack of cable actions, undoing each in turn
        CableAction action = _model.popCableUndoStack();
        while (action != null) {
            switch (action.getType()) {
            case CableAction.ADD_CABLE: {
                Cable cable = action.getCable();
                ComponentPort selectedPort1 = action.getComponentPort1();
                ComponentPort selectedPort2 = action.getComponentPort2();

                selectedPort1.removeCableA(cable);
                if (selectedPort2 != null) {
                    selectedPort2.removeCableB(cable);
                }
                if (!redrawInProgress) {
                    // notify GUI to update display
                    _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.CABLE_REMOVED, action));
                }
                break;
            }
            case CableAction.REMOVE_CABLE: {
                Cable cable = action.getCable();
                ComponentPort selectedPort1 = action.getComponentPort1();
                ComponentPort selectedPort2 = action.getComponentPort2();

                cable.setMarkForDelete(false);
                selectedPort1.addCableA(cable);
                if (selectedPort2 != null)  // don't do this if single-ended
                    selectedPort2.addCableB(cable);
                if (!redrawInProgress) {
                    // notify GUI to update display
                    _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.CABLE_ADDED, action));
                }
                break;
            }
            case CableAction.SINGLE_TO_DOUBLE_ENDED: {
                Cable cable = action.getCable();
                String cableCollection = action.getOriginalString();
                ComponentPort selectedPortA = action.getComponentPort1();
                ComponentPort selectedPortB = action.getComponentPort2();

                if (cableCollection.equals("B")) {
                    selectedPortB.removeCableB(cable);
                } else if (cableCollection.equals("A")) {
                    selectedPortB.removeCableA(cable);
                }
                if (!redrawInProgress) {
                    // notify GUI to update display
                    _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.CABLE_UPDATED, action));
                }
                break;
            }
            case CableAction.DOUBLE_TO_SINGLE_ENDED: {
                Cable cable = action.getCable();
                String cableCollection = action.getOriginalString();
                ComponentPort selectedPortA = action.getComponentPort1();
                ComponentPort selectedPortB = action.getComponentPort2();
                if (cableCollection.equals("A")) {
                    selectedPortB.addCableA(cable);
                    
                } else if (cableCollection.equals("B")) {
                    selectedPortB.addCableB(cable);
                }
                if (!redrawInProgress) {
                    // notify GUI to update display
                    _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.CABLE_UPDATED, action));
                }
                break;
            }
            case CableAction.UPDATE_VIRTUAL: {
                Cable cable = action.getCable();
                boolean virt = cable.getVirtual();
                cable.setVirtual(!virt);
                if (!redrawInProgress) {
                    // notify GUI to update display
                    _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.CABLE_UPDATED, action));
                }
                break;
            }
            case CableAction.UPDATE_LABEL: {
                Cable cable = action.getCable();
                String oldLabel = action.getOriginalString();
                cable.setLabel(oldLabel);
                if (!redrawInProgress) {
                    // notify GUI to update display
                    _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.CABLE_UPDATED, action));
                }
                break;
            }
            case CableAction.UPDATE_COLOR: {
                Cable cable = action.getCable();
                String oldColor = action.getOriginalString();
                cable.setColor(oldColor);
                if (!redrawInProgress) {
                    // notify GUI to update display
                    _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.CABLE_UPDATED, action));
                }
                break;
            }
            case CableAction.UPDATE_DEST: {
                Cable cable = action.getCable();
                String oldDest = action.getOriginalString();
                cable.setDestinationDescription(oldDest);
                if (!redrawInProgress) {
                    // notify GUI to update display
                    _model.notifyCableModelListeners(new CableModelEvent(CableModelEvent.CABLE_UPDATED, action));
                }
                break;
            }
            }
            action = _model.popCableUndoStack();
        }
        if (!redrawInProgress) {
            requestResetOfDependentDocuments();
        }
    }
                

    /**
     * Goes through IRMIS desktop list of documents and does an actionReset()
     * on those that are dependent on our state.
     */
    private void requestResetOfDependentDocuments() {
        //Application app = Application.getApp();
        //List docs = app.getDocuments();
        
        /*
        // iterate, looking for all other idt::cable docs
        Iterator docIt = docs.iterator();
        CableDocument cDoc = null;
        while (docIt.hasNext()) {
            XalInternalDocument doc = (XalInternalDocument)docIt.next();

            // If doc is an instance of idt::cable (and not us), request reset
            if (doc.getTitle().equals("idt::cable") && !doc.equals(this)) {
                cDoc = (CableDocument)doc;
                cDoc.actionReset();
            }
        }
        */
    }

	/**
	 * Get the launch model which represents the main model of this document
	 * @return the main model of this document
	 */
	public CableModel getModel() {
		return _model;
	}

    private CableWindow myWindow() {
        return (CableWindow)_mainWindow;
    }
}
