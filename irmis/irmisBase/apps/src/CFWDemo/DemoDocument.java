/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package CFWDemo;

import javax.swing.JFrame;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

// XAL/CFW framework stuff
import gov.sns.application.*;

// general app utilities
import gov.anl.aps.irmis.apps.SwingThreadWork;
import gov.anl.aps.irmis.apps.QueuedExecutorUtil;

// IRMIS persistence layer (this is where our domain data objects are defined)
import gov.anl.aps.irmis.persistence.pv.IOC;

// IRMIS service layer (this is what we call to get data from db)
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.pv.PVService;


/**
 * The primary document and controller class for the Demo application. 
 * Provides "action" methods to query the database for data, or whatever you wish to do.
 * These action methods are typically invoked by the GUI in <code>DemoWindow</code>.
 * Any time-consuming activity should be queued up using the 
 * <code>QueuedExecutorUtil</code>, as shown below.
 */
public class DemoDocument extends XalInternalDocument {

	/** The main data model of this application */
	private DemoModel _model;

    // Hold reference to parent desktop frame. Used to put up busy icon during work.
    private JFrame _appFrame;

	/*********************** Document Constructors ******************************/

	/** 
     * Create a new empty document. Invoked by menubar "New", or when Desktop first
     * starts up.
     */
    public DemoDocument() {
        this(null);
    }

    /**
     * Create document from url. This would get invoked by CFW if you "opened"
     * a file that this document had previously saved.
     */
    public DemoDocument(final URL url) {
        _model = new DemoModel();
        _appFrame = ((DesktopApplication)Application.getApp()).getDesktopFrame();

        // don't have anything here yet
    }

    /*********************** CFW required methods  ******************************/    

    /**
     * This handles a menubar "Save" or "Save As...". We don't do anything with
     * document save/open yet.
     */
    public void saveDocumentAs(final URL url) {
        return;
    }
    
    /**
     * We don't write any documents out, so return empty array.
     */
    public String[] writableDocumentTypes() {
        return new String[] {};
    }

   /**
	 * Overriding default window title scheme from XAL.
     * @param newTitle The new title for this document.
     */
    public void setTitle( final String newTitle ) {
        String modifiedTitle = "Demo IOC Viewer" + newTitle;
		super.setTitle( modifiedTitle );
        _documentListenerProxy.titleChanged( this, title );
    }	
            
    /**
     * Make the GUI that is the View of our Model.
     */
    public void makeMainWindow() {

        _mainWindow = new DemoWindow(this);

    }

	
	/**
	 * Get the main model of this document
	 * @return the main model of this document
	 */
	public DemoModel getModel() {
		return _model;
	}

    /************************** ACTION (Controller) METHODS *********************/
    /**** Add your application specific activities here *************/

    /**
     * Action method which is typically invoked by the GUI (DemoWindow) class.
     * Queue up job for background thread to gather the current set of IOC objects.
     * When complete, fire off <code>DemoModelEvent</code> to notify GUI (DemoWindow)
     * that new list of iocs is available for display. During this
     * job, the main window cursor will change to an hourglass icon.
     */
    public void actionIOCSearch() {
        /**
         * Queue up a chunk of work defined as a SwingThreadWork instance.
         * Note the _appFrame argument just tells where the "busy" hourglass
         * cursor should show up while this stuff is executed.
         */
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                
                /**
                 * Do time-consuming work in this method. In this case, we
                 * conduct the database query here. This method will be run
                 * in a background thread. Put the results of your work into
                 * the _model at the end of the method.
                 */
                public void doNonUILogic() {
                    // Get current set of IOC objects, and put into DemoModel
                    List iocList = null;
                    try {
                        iocList = PVService.findIOCList();
                    } catch (Exception e) {
                        // Since we are running in a background thread, we
                        // can't throw an exception to the caller (who is
                        // probably long gone by now). But at least print
                        // it out.
                        e.printStackTrace();
                        // log error
                    }
                    _model.setIocList(iocList);
                }
                
                /**
                 * Use this method to tell model it needs to notify listeners
                 * that it has new data. This method actually winds up getting
                 * executed on the Swing event queue thread.
                 */
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                    _model.notifyDemoModelListeners(new DemoModelEvent(DemoModelEvent.NEW_IOC_LIST));
                }
            });
    }


    /************************** PRIVATE METHODS *******************************/

    /**
	 * Convenience method for getting the main window cast to the proper subclass of XalWindow.
     * This allows me to avoid casting the window every time I reference it.
     * @return The main window cast to its dynamic runtime class
     */
    private DemoWindow myWindow() {
        return (DemoWindow)_mainWindow;
    }
}
