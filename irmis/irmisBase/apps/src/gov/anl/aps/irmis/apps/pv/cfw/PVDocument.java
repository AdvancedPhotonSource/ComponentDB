/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.pv.cfw;

// java imports
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.table.TableModel;

// XAL framework stuff
import gov.sns.application.*;

// general app utilities
import gov.anl.aps.irmis.apps.SwingThreadWork;
import gov.anl.aps.irmis.apps.QueuedExecutorUtil;

// other IRMIS sub-applications
import gov.anl.aps.irmis.apps.ioc.cfw.IOCDocument;

// persistence layer
import gov.anl.aps.irmis.persistence.pv.Record;
import gov.anl.aps.irmis.persistence.pv.RecordType;
import gov.anl.aps.irmis.persistence.pv.Field;
import gov.anl.aps.irmis.persistence.pv.FieldType;

// service layer
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.pv.PVService;
import gov.anl.aps.irmis.service.pv.PVSearchParameters;


/**
 * The primary controller class for IRMIS PV Viewer application. 
 * Provides "action" methods to query the IRMIS database for pv-related data.
 * These action methods are typically invoked by the GUI in <code>PVWindow</code>.
 * Any time-consuming activity should be queued up using the 
 * <code>QueuedExecutor</code>, which is similar to the SwingWorker concept.
 */
public class PVDocument extends XalInternalDocument {

	/** The main data model of this application */
	private PVModel _model;

    // Hold reference to parent desktop frame. Used to put up busy icon during work.
    JFrame _appFrame;

	/** Create a new empty document */
    public PVDocument() {
        this(null);
    }

    public PVDocument(final URL url) {
        //setSource(url);
        _model = new PVModel();
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
        String modifiedTitle = "idt::pv" + newTitle;
		super.setTitle( modifiedTitle );
        _documentListenerProxy.titleChanged( this, title );
    }	
    
    /**
     * Clear out all the data in the model.
     */
    public void actionResetModel() {
        _model.reset();
    }

    /**
     * Queue up job for background thread to gather the current set of IOCBoot
     * objects, and the unique list of RecordType. When complete, fire off 
     * <code>PVModelEvent</code> to notify GUI that new list of iocs, db 
     * files, and record types is available for display.
     */
    public void actionReload() {
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private List bootList = null;
                private List rtList = null;
                public void doNonUILogic() {
                    // Get current set of IOCBoot objects, and put into PVModel
                    // Also get a unique list of current record types.
                    _model.setIRMISException(null);
                    try {
                        bootList = PVService.findCurrentIOCBootList();
                        rtList = PVService.findRecordTypeList();
                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        _model.setIRMISException(ie);
                    }
                }
                
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                    _model.setIocBootList(bootList);
                    _model.setRecordTypeList(rtList);
                    //_model.notifyPvModelListeners(new PVModelEvent(PVModelEvent.NEW_IOC_BOOT_LIST));
                }
            });
    }

    public void actionRedraw() {
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                public void doNonUILogic() {
                }
                
                public void doUIUpdateLogic() {
                    _model.notifyPvModelListeners(new PVModelEvent(PVModelEvent.NEW_IOC_BOOT_LIST));
                }
            });
    }

            
    /**
     * Make the window for this document.
     */
    public void makeMainWindow() {

        _mainWindow = new PVWindow(this);

        // Initialize application data from database and do a full redraw of GUI.        
        actionReload();
        actionRedraw();
    }

	
	/**
	 * Get the main model of this document
	 * @return the main model of this document
	 */
	public PVModel getModel() {
		return _model;
	}

    /**
     * Conduct search for process variables using given search parameters. Work queued
     * up on background thread, after which <code>PVModel</code> listeners are notified.
     *
     * @param pvSearchParams container for a variety of search constraints
     * @param historyMode indicates that we need to collapse the search results afterwards
     */
    public void actionPVSearch(PVSearchParameters pvSearchParams, boolean historyMode) {
        final boolean histMode = historyMode;
        _model.setPVSearchParameters(pvSearchParams);
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private List recordList = null;
                private List collapsedRecordList = null;

                public void doNonUILogic() {
                    
                    PVSearchParameters pvSearchParams = _model.getPVSearchParameters();
                    try {
                        // Clear the cache of previous results, since our searches
                        // can potentially bloat the cache with thousands of objects.
                        PVService.clearCache(_model.getRecordList());
                        PVService.clearCache(_model.getLinkList());
                        
                        // clear last results in model
                        _model.setSelectedRecord(null);
                        _model.setSelectedRecordBootHistory(null);
                        _model.setLinkList(null);
                        _model.setRecordList(null);                        
                        recordList = PVService.findRecordList(pvSearchParams);
                        if (histMode) 
                            collapsedRecordList = PVService.collapseRecordList(recordList);

                    } catch (Exception e) {
                        e.printStackTrace();
                        // log error
                    }
                }
                public void doUIUpdateLogic() {
                    // set new record list
                    _model.setRecordList(recordList);
                    if (histMode)
                        _model.setCollapsedRecordList(collapsedRecordList);
                    // fire off notification of modification to listeners
                    _model.notifyPvModelListeners(new PVModelEvent(PVModelEvent.NEW_RECORD_LIST));
                }
                
            });
    }

    /**
     * Conduct search for a particular record using search parameters with just the
     * full record name set. Work queued up on background thread, after which 
     * <code>PVModel</code> listeners are notified.
     *
     * @param recordName name of pv to search for (with or without trailing field name and modifiers)
     */
    public void actionSinglePVSearch(String recordName) {
        final String selectedRecordName = recordName;
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private List recordList = null;
                public void doNonUILogic() {
                    
                    PVSearchParameters pvSearchParams = new PVSearchParameters();
                    List iocBootList = _model.getIocBootList();
                    try {
                        pvSearchParams.setIocBootList(iocBootList);
                        pvSearchParams.setRecNameGlob(PVUtils.basePVName(selectedRecordName));
                        recordList = PVService.findRecordList(pvSearchParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // log error
                    }
                }
                public void doUIUpdateLogic() {
                    // clear any previous data from model
                    _model.setLinkList(null);

                    // set new record selection 
                    if (recordList != null && recordList.size() > 0)
                        _model.setSelectedRecord((Record)recordList.get(0));
                    else
                        _model.setSelectedRecord(null);

                    // fire off notification of modification to listeners
                    _model.notifyPvModelListeners(new PVModelEvent(PVModelEvent.NEW_RECORD));
                }
                
            });
    }

    /**
     * Conduct search for fields of selected record. For efficiency, when a Record
     * object is loaded, it's fields are not. This updates the selected record object
     * in place, adding it's fields.
     * Lastly, we request
     * notification of any model listeners that new field data is here.
     */
    public void actionFieldSearch() {
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                public void doNonUILogic() {
                    Record selectedRecord = _model.getSelectedRecord();
                    PVService.findFieldsForRecord(selectedRecord);
                    
                }
                public void doUIUpdateLogic() {
                    // fire off notification of modification to listeners
                    _model.notifyPvModelListeners(new PVModelEvent(PVModelEvent.NEW_FIELD_LIST));
                }
                
            });
    }

    /**
     * Conduct search for any fields (in other records) that link to the given record.
     * Then notify any model listeners that new link data is here.
     */
    public void actionLinkSearch() {
        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private List fieldList = null;
                public void doNonUILogic() {
                    Record selectedRecord = _model.getSelectedRecord();
                    List iocBootList = _model.getIocBootList();
                    try {
                        fieldList = PVService
                            .findReferringFieldsForRecord(iocBootList, 
                                                          selectedRecord);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // log error
                    }                        
                }
                public void doUIUpdateLogic() {
                    _model.setLinkList(fieldList);
                    // fire off notification of modification to listeners
                    _model.notifyPvModelListeners(new PVModelEvent(PVModelEvent.NEW_LINK_LIST));
                }
                
            });
    }

    /**
     * Conduct search for any clients of a given pv name.
     *
     */
    public void actionClientSearch() {
        if (_model.getShowClients() && _model.getSelectedRecord() != null) {
            QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                    private List recordClientList = null;
                    
                    public void doNonUILogic() {
                        Record selectedRecord = _model.getSelectedRecord();                        
                        try {
                            recordClientList = PVService.
                                findReferringClientsForRecord(selectedRecord);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // log error
                        }
                    }
                    public void doUIUpdateLogic() {
                        _model.setClientList(recordClientList);
                        // fire off notification of modification to listeners
                        _model.notifyPvModelListeners(new PVModelEvent(PVModelEvent.NEW_CLIENT_LIST));
                    }
                    
                });
        }
    }

    /**
     * Search for the IOCBoot history for selected record.
     */
    public void actionBootHistorySearch() {

        QueuedExecutorUtil.execute(new SwingThreadWork(_appFrame) {
                private List bootHistoryList = null;
                public void doNonUILogic() {
                    Record selectedRecord = _model.getSelectedRecord();
                    try {
                        bootHistoryList = PVService
                            .findCompleteRecordBootHistory(selectedRecord);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // log error
                    }                        
                }
                public void doUIUpdateLogic() {
                    _model.setSelectedRecordBootHistory(bootHistoryList);
                    // fire off notification of modification to listeners
                    _model.notifyPvModelListeners(new PVModelEvent(PVModelEvent.NEW_RECORD_BOOT_HISTORY_LIST));
                }
                
            });
    }

    /**
     * Pop up the IOC Viewer application on the desktop, preselected for
     * the ioc of our currently selected record.
     */
    public void actionProduceIOCDocument() {
        Application app = Application.getApp();
        List docs = app.getDocuments();
        
        // first see if it is already on desktop 
        Iterator docIt = docs.iterator();
        IOCDocument iocDoc = null;
        while (docIt.hasNext()) {
            XalInternalDocument doc = (XalInternalDocument)docIt.next();
            if (doc.getTitle().equals("idt::ioc")) {
                iocDoc = (IOCDocument)doc;
                break;
            }
        }
        if (iocDoc == null) {
            iocDoc = new IOCDocument();
            app.produceDocument(iocDoc);
        }

        // now tell the ioc doc to preselect for our ioc
        String iocName = _model.getSelectedRecord().getIocBoot().getIoc().getIocName();
        iocDoc.actionSelectIoc(iocName);
    }

    /**
     * Save the current 
     * the ioc of our currently selected record.
     */
    public void actionSaveResultsToFile(File file, TableModel tableModel) {
        int rows = tableModel.getRowCount();
        int cols = tableModel.getColumnCount();
        try {
            FileWriter fw = new FileWriter(file);
            StringBuffer header = new StringBuffer();
            for (int j=0 ; j < cols ; j++) {
                header.append(tableModel.getColumnName(j)).append("\t");
            }
            header.append("\n");
            fw.write(header.toString());
            for (int i=0 ; i < rows ; i++) {
                StringBuffer dataLine = new StringBuffer();
                for (int j=0 ; j < cols ; j++) {
                    dataLine.append(tableModel.getValueAt(i,j).toString()).append("\t");
                }
                dataLine.append("\n");
                fw.write(dataLine.toString());
            }
            fw.flush();
            fw.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    /**
	 * Convenience method for getting the main window cast to the proper subclass of XalWindow.
     * This allows me to avoid casting the window every time I reference it.
     * @return The main window cast to its dynamic runtime class
     */
    private PVWindow myWindow() {
        return (PVWindow)_mainWindow;
    }
}
