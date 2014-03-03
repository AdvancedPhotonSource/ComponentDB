/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.pv.cfw;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

// persistence layer
import gov.anl.aps.irmis.persistence.pv.Record;

// service layer
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.pv.PVSearchParameters;

/**
 * Application data model for IRMIS PV Viewer. Stores graph of current set of ioc's
 * and related data. Stores record search result set, and the individual record
 * of interest. A list of listeners interested in this data is maintained, and
 * notified as requested.
 */
public class PVModel {

    // list of others interested in changes to the data model
    private List pvModelListenersList = new ArrayList();

    // used to flag a problem talking to db
    private IRMISException ie = null;

    // pv subgraph rooted in list of current IOCBoot objects
    private List iocBootList; 

    // list of unique RecordType for populating record type search list box
    private List recordTypeList;

    // pv subgraph root in list of searched Record objects
    private List recordList;

    /** 
        Stores same data as recordList, but used when recordList may have
        repeated entries with the same record name. For example, when retrieving
        the history of a pv or set of pvs. Here there will be one element
        for each record name, with each entry value being a list of ocurrences
        of the given pv (over ioc boots).
    */
    private List collapsedRecordList;

    // reference to current record-of-interest
    private Record selectedRecord;

    // the list of IOCBoot occurrences corresponding to selectedRecord (history mode only)
    private List selectedRecordBootHistory;

    // list of fields that have reference (link) to record-of-interest
    private List linkList;

    // list of pv clients taht have reference to record-of-interest
    private List clientList;
    
    // pv search parameters from view
    private PVSearchParameters searchParams;

    // compute and show pv clients
    private boolean showClients = false;

	/**
	 * Do nothing constructor
	 */
	public PVModel() {
	}

    public void addPvModelListener(PVModelListener l) {
        pvModelListenersList.add(l);
    }

    public void notifyPvModelListeners() {
        notifyPvModelListeners(null);
    }

    /**
     * Notify registered listeners of the given <code>PVModelEvent</code>.
     *
     * @param modelEvent encapsulates one of several possible events
     */
    public void notifyPvModelListeners(PVModelEvent modelEvent) {
        Iterator it = pvModelListenersList.iterator();
        while (it.hasNext()) {
            PVModelListener l = (PVModelListener)it.next();
            l.modified(modelEvent);
        }
    }

    public IRMISException getIRMISException() {
        return this.ie;
    }
    public void setIRMISException(IRMISException ie) {
        this.ie = ie;
    }

    /**
     * Clear out all data.
     */
    public void reset() {
        ie = null;
        iocBootList = null; 
        recordTypeList = null;
        recordList = null;
        collapsedRecordList = null;
        selectedRecord = null;
        selectedRecordBootHistory = null;
        linkList = null;
        clientList = null;
        searchParams = null;
        showClients = false;
    }

    public List getIocBootList() {
        return iocBootList;
    }
    public void setIocBootList(List iocBootList) {
        this.iocBootList = iocBootList;
    }

    public List getRecordTypeList() {
        return recordTypeList;
    }
    public void setRecordTypeList(List recordTypeList) {
        this.recordTypeList = recordTypeList;
    }

    public List getRecordList() {
        return recordList;
    }
    public void setRecordList(List recordList) {
        this.recordList = recordList;
    }

    public List getCollapsedRecordList() {
        return this.collapsedRecordList;
    }

    public void setCollapsedRecordList(List value) {
        this.collapsedRecordList = value;
    }

    public Record getSelectedRecord() {
        return selectedRecord;
    }
    public void setSelectedRecord(Record rec) {
        selectedRecord = rec;
    }

    public List getSelectedRecordBootHistory() {
        return selectedRecordBootHistory;
    }

    public void setSelectedRecordBootHistory(List history) {
        selectedRecordBootHistory = history;
    }

    public List getLinkList() {
        return linkList;
    }
    public void setLinkList(List linkList) {
        this.linkList = linkList;
    }

    public List getClientList() {
        return clientList;
    }
    public void setClientList(List clientList) {
        this.clientList = clientList;
    }

    public PVSearchParameters getPVSearchParameters() {
        return this.searchParams;
    }
    public void setPVSearchParameters(PVSearchParameters searchParams) {
        this.searchParams = searchParams;
    }

    public boolean getShowClients() {
        return showClients;
    }
    public void setShowClients(boolean value) {
        showClients = value;
    }

}
