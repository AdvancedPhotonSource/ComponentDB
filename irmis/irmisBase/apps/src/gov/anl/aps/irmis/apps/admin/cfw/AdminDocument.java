/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.admin.cfw;

import java.net.URL;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.Collections;
import javax.swing.SwingUtilities;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import java.util.logging.Logger;
import java.util.logging.Level;

// dom4j XML
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

// XAL framework stuff
import gov.sns.application.*;

// general app utilities
import gov.anl.aps.irmis.apps.SwingThreadWork;
import gov.anl.aps.irmis.apps.QueuedExecutorUtil;
import gov.anl.aps.irmis.login.LoginUtil;

// other IRMIS sub-applications

// persistence layer
import gov.anl.aps.irmis.persistence.audit.AuditAction;
import gov.anl.aps.irmis.persistence.audit.AuditActionType;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.DAOException;

// service layer
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.shared.AuditService;
import gov.anl.aps.irmis.service.shared.PersonService;
import gov.anl.aps.irmis.service.component.ComponentTypeService;


/**
 * The primary controller class for IRMIS administrative console.
 * Provides "action" methods to to query the IRMIS database for user/etc.
 * related data. These action methods are typically invoked by the 
 * <code>AdminWindow</code>.
 * Any time-consuming activity should be queued up using the 
 * <code>queuedExecutor</code>, which is similar to the SwingWorker concept.
 */
public class AdminDocument extends XalInternalDocument {

	/** The main data model of this application */
	private AdminModel _model;

    // Hold reference to parent desktop frame. Used to put up busy icon during work.
    JFrame _appFrame;

	/** Create a new empty document */
    public AdminDocument() {
        this(null);
        _model = new AdminModel();
    }

	/** Create a new document from url */
    public AdminDocument(final URL url) {
        //setSource(url);
        _model = new AdminModel();
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
        String modifiedTitle = "idt::admin" + newTitle;
		super.setTitle( modifiedTitle );
        _documentListenerProxy.titleChanged( this, title );
    }	
            
    /**
     * Make a main window by instantiating the my custom window.
     */
    public void makeMainWindow() {
        _mainWindow = new AdminWindow(this);
        actionReload();  // load app data from database, redraw GUI
        actionRedraw();
    }

    /**
     * Queue up job for background thread to gather the current set of Person
     * objects. When complete, fire off <code>AdminModelEvent</code> to notify GUI
     * that new list of persons is available for display. 
     */
    public void loadInitialPersonData() {

        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private List personList = null;
                private List roleNameList = null;

                public void doNonUILogic() {
                    _model.setIRMISException(null);
                    // Get current set of Person objects, and put into AdminModel
                    try {
                        // get list of persons
                        personList = PersonService.findPersonList();

                        // get list of all possible role names
                        roleNameList = PersonService.findRoleNameList();

                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        _model.setIRMISException(ie);
                    }
                }
                
                public void doUIUpdateLogic() {
                    _model.setPersons(personList);
                    _model.setRoleNameList(roleNameList);
                    // fire off notification of modification to listeners
                    //_model.notifyAdminModelListeners(new AdminModelEvent(AdminModelEvent.NEW_PERSONS));
                }
            });
    }

    /*
     * Queue up job for background thread to gather the current set of ComponentType
     * objects. When complete, fire off <code>AdminModelEvent</code> to notify GUI
     * that new list of component types is available for display. 
     */
    public void loadInitialComponentTypes() {

        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private List ctList = null;

                public void doNonUILogic() {
                    _model.setIRMISException(null);
                    // Get current set ComponentType objects, and put into AdminModel
                    try {
                        ctList = ComponentTypeService.findComponentTypeList();

                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        _model.setIRMISException(ie);
                    }
                }
                
                public void doUIUpdateLogic() {
                    _model.setComponentTypeList(ctList);
                    _model.setFilteredComponentTypeList(ctList);
                    // fire off notification of modification to listeners
                    //_model.notifyAdminModelListeners(new AdminModelEvent(AdminModelEvent.NEW_COMPONENT_TYPES));
                }
            });
    }

    public void actionSavePerson(Person p) {
        final Person person = p;

        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                public void doNonUILogic() {
                    _model.setIRMISException(null);
                    // Save the person object
                    try {
                        PersonService.savePerson(person);
                        List persons = _model.getPersons();
                        if (!persons.contains(person))
                            persons.add(person);
                        Collections.sort(persons);

                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        _model.setIRMISException(ie);
                    }
                }
                
                public void doUIUpdateLogic() {
                    _model.setSelectedPerson(person);
                    // fire off notification to listeners
                    _model.notifyAdminModelListeners(new AdminModelEvent(AdminModelEvent.PERSON_SAVED));
                }
            });


    }

    /**
     * Save the given component type to an XML file.
     */
    public void actionExportComponentTypeToFile(File file, ComponentType componentType) {

        try {
            FileWriter fw = new FileWriter(file);
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("irmis-data");
            root.add(componentType.toElement());
            document.write(fw);
            fw.flush();
            fw.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
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
                    _model.notifyAdminModelListeners(new AdminModelEvent(AdminModelEvent.NEW_PERSONS));
                    _model.notifyAdminModelListeners(new AdminModelEvent(AdminModelEvent.NEW_COMPONENT_TYPES));
                }
            });
    }

    /**
     * Queue up job for background thread to prepare application reset. This action
     * is invoked when another irmis app has
     * modified the underlying data enough to invalidate current display.
     */
    public void actionReload() {
        loadInitialPersonData();
        loadInitialComponentTypes();
    }

    public void actionRedraw() {
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                public void doNonUILogic() {
                }
                
                public void doUIUpdateLogic() {
                    _model.notifyAdminModelListeners(new AdminModelEvent(AdminModelEvent.NEW_PERSONS));
                    _model.notifyAdminModelListeners(new AdminModelEvent(AdminModelEvent.NEW_COMPONENT_TYPES));
                }
            });
    }

    /**
     * Goes through IRMIS desktop list of documents and does an actionReset()
     * on those that are dependent on our state.
     */
    private void requestResetOfDependentDocuments() {
        Application app = Application.getApp();
        List docs = app.getDocuments();
        
        // iterate, looking for all other idt::cable docs
        /*
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
	public AdminModel getModel() {
		return _model;
	}

    private AdminWindow myWindow() {
        return (AdminWindow)_mainWindow;
    }
}
