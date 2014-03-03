/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.ioc.cfw;

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
import gov.anl.aps.irmis.apps.ioc.cfw.plugins.IOCExtendedInfoPlugin;

// persistence layer
import gov.anl.aps.irmis.persistence.pv.IOC;
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.component.ComponentRelationship;

// service layer
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.pv.PVService;
import gov.anl.aps.irmis.service.component.ComponentService;
import gov.anl.aps.irmis.service.component.ComponentTypeService;


/**
 * The primary controller class for IRMIS IOC application.
 * Provides "action" methods to to query the IRMIS database for ioc's and
 * related data. These action methods are typically invoked by the 
 * <code>IOCWindow</code>.
 * Any time-consuming activity should be queued up using the 
 * <code>queuedExecutor</code>, which is similar to the SwingWorker concept.
 */
public class IOCDocument extends XalInternalDocument {

	/** The main data model of this application */
	private IOCModel _model;

    // Hold reference to parent desktop frame. Used to put up busy icon during work.
    JFrame _appFrame;

	/** Create a new empty document */
    public IOCDocument() {
        this(null);
    }

	/** Create a new document from url */
    public IOCDocument(final URL url) {
        //setSource(url);
        _model = new IOCModel();
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
        String modifiedTitle = "idt::ioc" + newTitle;
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
                    _model.notifyIOCModelListeners(new IOCModelEvent(IOCModelEvent.NEW_IOC_LIST));
                }
            });
    }

    /**
     * Queue up job for background thread to gather the current set of IOC
     * objects. When complete, fire off <code>IOCModelEvent</code> to notify GUI
     * that new list of iocs is available for display. 
     */
    public void actionReload() {

        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private List iocList = null;
                private List iocAttrTypeList = null;
                private List systems = null;
                private List statusList = null;

                public void doNonUILogic() {
                    _model.setIRMISException(null);
                    // Get current set of IOC objects, and put into IOCModel
                    try {
                        // Instantiate plugin given in irmis.ioc.plugin property
                        Class pluginClass = null;
                        IOCExtendedInfoPlugin plugin = null;
                        try {
                            String pluginPath = System.getProperty("irmis.ioc.plugin");
                            if (pluginPath == null || pluginPath.length() == 0)
                                pluginPath = "gov.anl.aps.irmis.apps.ioc.cfw.plugins.APSIOCInfo";
                            pluginClass = Class.forName(pluginPath); 
                            plugin = (IOCExtendedInfoPlugin)pluginClass.newInstance();
                            _model.setIOCPlugin(plugin);
                            
                        } catch (ClassNotFoundException cnfe) {
                            cnfe.printStackTrace();
                        } catch (InstantiationException ie) {
                            ie.printStackTrace();
                        } catch (IllegalAccessException iae) {
                            iae.printStackTrace();
                        }

                        // get list of ioc's
                        iocList = PVService.findIOCList();
                        Collections.sort(iocList);

                        // get list of system names
                        systems = PVService.findSystems();

                        // get list of ioc status
                        statusList = PVService.findAllIOCStatus();

                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        _model.setIRMISException(ie);
                    }
                    _model.setIocList(iocList);
                    _model.setFilteredIocList(iocList);
                    _model.setNameFilteredIocList(iocList);
                    _model.setSystemFilteredIocList(iocList);
                    _model.setSystems(systems);
                    _model.setStatusList(statusList);
                }
                
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                    //_model.notifyIOCModelListeners(new IOCModelEvent(IOCModelEvent.NEW_IOC_LIST));
                }
            });
    }

    public void actionRedraw() {
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                public void doNonUILogic() {
                }
                
                public void doUIUpdateLogic() {
                    _model.notifyIOCModelListeners(new IOCModelEvent(IOCModelEvent.NEW_IOC_LIST));
                }
            });
    }
    
    /**
     * Make a main window by instantiating the my custom window.
     */
    public void makeMainWindow() {
        _mainWindow = new IOCWindow(this);
        actionReload();  // initialize data from database, and do full GUI draw
        actionRedraw();
        actionSelectIoc(null); // select first in list
    }

    /**
     * Programatically selects an ioc for display. Has the same effect as a user
     * clicking an ioc from the ioc list in the GUI. Provides means for external
     * document to command us to display a particular ioc.
     *
     * @param iocName ioc to select, or null to indicate first in list
     */
    public void actionSelectIoc(String iocName) {
        final String selectedIocName = iocName;
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private IOC selectedIoc = null;
                public void doNonUILogic() {
                    // find IOC object by given ioc name
                    List iocList = _model.getIocList();
                    //IOC selectedIoc = null;
                    if (iocList != null) {
                        Iterator iocIt = iocList.iterator();
                        while (iocIt.hasNext()) {
                            IOC ioc = (IOC)iocIt.next();
                            if (selectedIocName == null ||
                                ioc.getIocName().equals(selectedIocName)) {
                                selectedIoc = ioc;
                                break;
                            }
                        }
                    }
                }
                
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                    _model.setSelectedIoc(selectedIoc);                    
                    _model.notifyIOCModelListeners(new IOCModelEvent(IOCModelEvent.NEW_IOC_SELECTION));
                }
            });
    }
    

    /**
     * Queue up job to update/save given ioc object. Notify GUI when complete.
     */
    public void actionSaveIoc(IOC ioc, boolean notifyListeners) {
        final IOC saveThisIoc = ioc;
        final boolean notify = notifyListeners;

        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private boolean newIocAdded = false;
                private List iocList = null;                
                public void doNonUILogic() {
                    _model.setIRMISException(null);
                    if (saveThisIoc != null) {
                        try {
                            boolean newIoc = (saveThisIoc.getId()==null)?true:false;
                            boolean hasCompRef = (saveThisIoc.getComponent()==null)?false:true;

                            // If ioc being saved has no component reference, try and find one
                            if (!hasCompRef) {
                                Component iocComponent = null;
                                ComponentType ct = ComponentTypeService.
                                    findComponentTypeByName("Network");
                                List comps = ComponentService.
                                    findComponentsByType(ct);
                                if (comps != null && comps.size() == 1) {
                                    Component netComponent = (Component)comps.get(0);
                                    List childComps = netComponent.getChildRelationships(ComponentRelationshipType.CONTROL);
                                    Iterator childCRIt = childComps.iterator();
                                    while (childCRIt.hasNext()) {
                                        ComponentRelationship childCR = 
                                            (ComponentRelationship)childCRIt.next();
                                        if (saveThisIoc.getIocName().equals(childCR.getLogicalDescription())) {
                                            iocComponent = childCR.getChildComponent();
                                            saveThisIoc.setComponent(iocComponent);
                                            break;
                                        }
                                    }
                                }
                            }

                            // save ioc
                            PVService.saveIOC(saveThisIoc);

                            // If this is a new ioc, make sure extended ioc object is created
                            if (newIoc) {
                                // request creation of companion extended ioc object
                                IOCExtendedInfoPlugin plugin = _model.getIOCPlugin();
                                plugin.addNewExtendedIOC(saveThisIoc);
                            } 

                        } catch (IRMISException ie) {
                            ie.printStackTrace();
                            _model.setIRMISException(ie);

                        } catch (Exception e) {
                            e.printStackTrace();
                            // log error
                        }
                        // add ioc to model if it is new
                        //List iocList = _model.getIocList();
                        iocList = _model.getIocList();
                        if (!iocList.contains(saveThisIoc)) {
                            newIocAdded = true;
                            iocList.add(saveThisIoc);
                            Collections.sort(iocList);
                        }

                    }
                }
                
                public void doUIUpdateLogic() {
                    if (newIocAdded) {
                        _model.setIocList(iocList);
                        _model.setFilteredIocList(iocList);
                        _model.setNameFilteredIocList(iocList);
                        _model.setSystemFilteredIocList(iocList);
                    }
                    if (notify) {
                        // fire off notification of modification to listeners
                        _model.notifyIOCModelListeners(new IOCModelEvent(IOCModelEvent.IOC_SAVED));
                    }
                }
            });

    }


	/**
	 * Get the launch model which represents the main model of this document
	 * @return the main model of this document
	 */
	public IOCModel getModel() {
		return _model;
	}

    private IOCWindow myWindow() {
        return (IOCWindow)_mainWindow;
    }
}
