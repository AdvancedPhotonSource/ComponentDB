/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenttype.cfw;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.Collections;
import java.util.logging.*;
import javax.swing.SwingUtilities;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;

// XAL framework stuff
import gov.sns.application.*;

// general app utilities
import gov.anl.aps.irmis.apps.SwingThreadWork;
import gov.anl.aps.irmis.apps.QueuedExecutorUtil;
import gov.anl.aps.irmis.login.LoginUtil;

// other IRMIS sub-applications
import gov.anl.aps.irmis.apps.component.cfw.ComponentDocument;

// persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentTypeDAO;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentPortTemplate;
import gov.anl.aps.irmis.persistence.audit.AuditAction;
import gov.anl.aps.irmis.persistence.audit.AuditActionType;
import gov.anl.aps.irmis.persistence.DAOException;

// service layer
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.shared.AuditService;


/**
 * The primary controller class for IRMIS Component Type Viewer/Editor application.
 * Provides "action" methods to to query the IRMIS database for component type
 * related data. These action methods are typically invoked by the 
 * <code>ComponentTypeWindow</code>.
 * Any time-consuming activity should be queued up using the 
 * <code>QueuedExecutor</code>, which is similar to the SwingWorker concept.
 */
public class ComponentTypeDocument extends XalInternalDocument {

	/** The main data model of this application */
	private ComponentTypeModel _model;

    // Hold reference to parent desktop frame. Used to put up busy icon during work.
    JFrame _appFrame;

	/** Create a new empty document */
    public ComponentTypeDocument() {
        this(null);
    }

	/** Create a new document from url */
    public ComponentTypeDocument(final URL url) {
        //setSource(url);
        _model = new ComponentTypeModel();
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
        String modifiedTitle = "idt::component-type" + newTitle;
		super.setTitle( modifiedTitle );
        _documentListenerProxy.titleChanged( this, title );
    }	

    /**
     * Clear out all the data in the model.
     */
    public void actionResetModel() {
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                public void doNonUILogic() {
                    _model.reset();
                }
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                    _model.notifyComponentTypeModelListeners(new ComponentTypeModelEvent(ComponentTypeModelEvent.NEW_COMPONENT_TYPE_LIST));
                }
            });                
    }

    /**
     * Queue up job for background thread to gather the current set of ComponentType
     * objects. When complete, fire off <code>ComponentTypeModelEvent</code> to notify GUI
     * that new list of comp types is available for display. 
     */
    public void actionReload() {

        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private List compTypeList = null;

                public void doNonUILogic() {
                    _model.setIRMISException(null);
                    // Get current set of ComponentType objects
                    try {
                        // get list of comp types
                        compTypeList = ComponentTypeService.findComponentTypeList();
                        Collections.sort(compTypeList);

                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        _model.setIRMISException(ie);
                    }
                }
                
                public void doUIUpdateLogic() {
                    _model.setComponentTypeList(compTypeList);
                    _model.setFilteredComponentTypeList(compTypeList);
                    // fire off notification of modification to listeners
                    //_model.notifyComponentTypeModelListeners(new ComponentTypeModelEvent(ComponentTypeModelEvent.NEW_COMPONENT_TYPE_LIST));
                }
            });
    }

    public void actionRedraw() {
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                public void doNonUILogic() {
                }
                
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                    _model.notifyComponentTypeModelListeners(new ComponentTypeModelEvent(ComponentTypeModelEvent.NEW_COMPONENT_TYPE_LIST));
                }
            });
    }
            
    /**
     * Make a main window by instantiating the my custom window.
     */
    public void makeMainWindow() {
        _mainWindow = new ComponentTypeWindow(this);
        actionReload();  // initialize data from database and do full draw of GUI
        actionRedraw();
    }


    /**
     * Queue up job for background thread to draw in all the various lazy ancillary
     * info about the selected component type.
     */
    public void actionAttributeSearch(ComponentType selectedComponentType) {
        final ComponentType selectedType = selectedComponentType;
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                
                public void doNonUILogic() {
                    
                    // force read of lazy attributes from db
                    initializeLazyAttributes(selectedType);
                }
                
                public void doUIUpdateLogic() {
                    _model.setSelectedComponentType(selectedType);
                    // fire off notification of modification to listeners
                    _model.notifyComponentTypeModelListeners(new ComponentTypeModelEvent(ComponentTypeModelEvent.NEW_ATTR_LIST));
                }
            });
    }

    private void initializeLazyAttributes(ComponentType ct) {
        if (ct != null) {
            ct.getComponentPortTemplates().iterator();
            ct.getComponentTypeFunctions().iterator();
            ct.getComponentTypeInterfaces().iterator();
            ct.getComponentTypePersons().iterator();
            ct.getComponentTypeDocuments().iterator();
            ct.getBeamlineInterest();
            ct.getChcContact();
            ct.getComponentTypeStatus();
        }        
    }

    /**
     * Save a new component type or update an existing one.
     */
    public void actionSaveComponentType(ComponentType ct) {
        final ComponentType componentType = ct;
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                boolean newType = false;
                public void doNonUILogic() {
                    _model.setIRMISException(null);
                    boolean saveSuccessful = false;
                    newType = (componentType.getId()==null)?true:false;
                    // Save new or update existing
                    try {
                        ComponentTypeService.saveComponentType(componentType); 
                        if (newType) {
                            List compTypes = _model.getComponentTypeList();
                            compTypes.add(componentType);
                            Collections.sort(compTypes);
                        }
                        saveSuccessful = true;

                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        _model.setIRMISException(ie);
                    }
                    if (saveSuccessful) {
                        // notify any idt::component of new addition
                        if (newType)
                            requestUpdateOfDependentDocuments();

                        // log to audit table
                        String desc = null;
                        int actionType = 0;
                        if (!newType) {
                            actionType = AuditActionType.EDIT_COMPONENT_TYPE;
                            desc = "editing existing component type";
                        } else {
                            actionType = AuditActionType.ADD_COMPONENT_TYPE;
                            desc = "creating new component type";
                        }
                        try {
                            AuditAction auditAction = 
                                AuditService.createAuditAction(actionType, desc,
                                                               LoginUtil.getUsername(),
                                                               componentType.getId());        
                            AuditService.saveAuditAction(auditAction);
                        } catch (IRMISException ie) {
                            ie.printStackTrace();
                            Logger.getLogger("global").log(Level.WARNING, "IRMIS Service Error.", ie);
                        }
                    }
                }
                
                public void doUIUpdateLogic() {
                    _model.setSelectedComponentType(componentType);
                    if (newType) {
                        _model.notifyComponentTypeModelListeners(new ComponentTypeModelEvent(ComponentTypeModelEvent.NEW_COMPONENT_TYPE));
                    } else {
                        actionAttributeSearch(componentType);  // redisplay attrs
                    }
                }
            });
    }

    /**
     * Delete given port template from component type, and save component type to db.
     */
    public void actionDeletePortFromComponentType(ComponentType ct, 
                                                  ComponentPortTemplate cpt) {
        
        List cpts = ct.getComponentPortTemplates();
        cpts.remove(cpt);
        actionSaveComponentType(ct);
    }

    /**
     * Programatically selects a component type for display. Has the same effect 
     * as a user clicking a type from the type list in the GUI. Provides means for external
     * document to command us to display a particular component type.
     *
     * @param ctName component type to select
     */
    public void actionSelectComponentType(String ctName) {
        final String selectedCtName = ctName;
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private ComponentType selectedCt = null;
                public void doNonUILogic() {
                    // find ComponentType object by given component type name
                    List ctList = _model.getComponentTypeList();
                    if (ctList != null) {
                        Iterator ctIt = ctList.iterator();
                        while (ctIt.hasNext()) {
                            ComponentType ct = (ComponentType)ctIt.next();
                            if (selectedCtName == null ||
                                ct.getComponentTypeName().equals(selectedCtName)) {
                                selectedCt = ct;
                                initializeLazyAttributes(selectedCt);
                                _model.setSelectedComponentType(selectedCt);
                                break;
                            }
                        }
                    }
                }
                
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                    _model.notifyComponentTypeModelListeners(new ComponentTypeModelEvent(ComponentTypeModelEvent.NEW_COMPONENT_TYPE_SELECTION));
                }
            });
    }

    /**
     * Goes through IRMIS desktop list of documents and does an actionUpdateComponentTypeList()
     * on those that are dependent on our state.
     */
    private void requestUpdateOfDependentDocuments() {
        Application app = Application.getApp();
        List docs = app.getDocuments();
        
        // iterate, looking for all other idt::component docs
        Iterator docIt = docs.iterator();
        while (docIt.hasNext()) {
            XalInternalDocument doc = (XalInternalDocument)docIt.next();

            // If doc is an instance of idt::component (and not us), request reset
            if (doc.getTitle().equals("idt::component")) {
                ComponentDocument cDoc = (ComponentDocument)doc;
                cDoc.actionUpdateComponentTypeList(_model.getComponentTypeList());
            } 
        }
    }


	/**
	 * Get the launch model which represents the main model of this document
	 * @return the main model of this document
	 */
	public ComponentTypeModel getModel() {
		return _model;
	}

    private ComponentTypeWindow myWindow() {
        return (ComponentTypeWindow)_mainWindow;
    }
}
