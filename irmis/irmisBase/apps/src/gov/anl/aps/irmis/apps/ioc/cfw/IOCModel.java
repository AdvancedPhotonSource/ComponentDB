/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.ioc.cfw;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import gov.anl.aps.irmis.persistence.pv.IOC;
import gov.anl.aps.irmis.apps.ioc.cfw.plugins.IOCExtendedInfoPlugin;

// service layer
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Application data model for IRMIS IOC app. Stores graph of current set of ioc's
 * and related data. 
 * A list of listeners interested in this data is maintained, and
 * notified as requested.
 */
public class IOCModel {

    // list of others interested in changes to the data model
    private List iocModelListenersList = new ArrayList();

    // used to flag a problem talking to db
    private IRMISException ie = null;

    // list of IOC records in irmis db
    private List iocList;

    // filtered versions of above
    private List filteredIocList;        // both
    private List nameFilteredIocList;    // just name
    private List systemFilteredIocList;  // just system

    // list of possible system names
    private List systems;

    // list of possible IOC status
    private List statusList;

    // ioc chosen from above list
    private IOC selectedIoc;

    // plugin for managing the righthand GUI component for extended ioc info
    private IOCExtendedInfoPlugin iocPlugin;

	/**
	 * Do nothing constructor
	 */
	public IOCModel() {
	}

    public void addIOCModelListener(IOCModelListener l) {
        iocModelListenersList.add(l);
    }

    public void notifyIOCModelListeners() {
        notifyIOCModelListeners(null);
    }

    /**
     * Notify registered listeners of the given <code>IOCModelEvent</code>.
     *
     * @param modelEvent encapsulates one of several possible events
     */
    public void notifyIOCModelListeners(IOCModelEvent modelEvent) {
        Iterator it = iocModelListenersList.iterator();
        while (it.hasNext()) {
            IOCModelListener l = (IOCModelListener)it.next();
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
     * Clear out data.
     */
    public void reset() {
        ie = null;
        iocList = null;
        filteredIocList = null;
        nameFilteredIocList = null;
        systemFilteredIocList = null;
        systems = null;
        statusList = null;
        selectedIoc = null;
    }

    public List getIocList() {
        return iocList;
    }
    public void setIocList(List iocList) {
        this.iocList = iocList;
    }

    public List getFilteredIocList() {
        return filteredIocList;
    }
    public void setFilteredIocList(List iocList) {
        this.filteredIocList = iocList;
    }

    public List getNameFilteredIocList() {
        return nameFilteredIocList;
    }
    public void setNameFilteredIocList(List iocList) {
        this.nameFilteredIocList = iocList;
    }

    public List getSystemFilteredIocList() {
        return systemFilteredIocList;
    }
    public void setSystemFilteredIocList(List iocList) {
        this.systemFilteredIocList = iocList;
    }

    public IOC getSelectedIoc() {
        return selectedIoc;
    }
    public void setSelectedIoc(IOC value) {
        selectedIoc = value;
    }

    public List getSystems() {
        return systems;
    }
    public void setSystems(List value) {
        systems = value;
    }

    public List getStatusList() {
        return statusList;
    }
    public void setStatusList(List value) {
        statusList = value;
    }

    public IOCExtendedInfoPlugin getIOCPlugin() {
        return iocPlugin;
    }
    public void setIOCPlugin(IOCExtendedInfoPlugin value) {
        iocPlugin = value;
    }

}
