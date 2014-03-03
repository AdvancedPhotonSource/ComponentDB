/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenttype.cfw;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentType;

// service layer
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Application data model for IRMIS Component Viewer/Editor. 
 * A list of listeners interested in this data is maintained, and
 * notified as requested.
 */
public class ComponentTypeModel {

    // list of others interested in changes to the data model
    private List componentTypeModelListenersList = new ArrayList();

    // used to flag a problem talking to db
    private IRMISException ie = null;

    private List componentTypeList;
    private List filteredComponentTypeList;

    // component type chosen from above list
    private ComponentType selectedComponentType;

	/**
	 * Do nothing constructor
	 */
	public ComponentTypeModel() {
	}

    public IRMISException getIRMISException() {
        return this.ie;
    }
    public void setIRMISException(IRMISException ie) {
        this.ie = ie;
    }

    /**
     * Clear out all data in model.
     */
    public void reset() {
        ie = null;
        componentTypeList = null;
        filteredComponentTypeList = null;
        selectedComponentType = null;
    }

    public List getComponentTypeList() {
        return componentTypeList;
    }
    public void setComponentTypeList(List compTypeList) {
        this.componentTypeList = compTypeList;
    }

    public List getFilteredComponentTypeList() {
        return filteredComponentTypeList;
    }
    public void setFilteredComponentTypeList(List compTypeList) {
        this.filteredComponentTypeList = compTypeList;
    }

    public ComponentType getSelectedComponentType() {
        return selectedComponentType;
    }
    public void setSelectedComponentType(ComponentType value) {
        selectedComponentType = value;
    }


    public void addComponentTypeModelListener(ComponentTypeModelListener l) {
        componentTypeModelListenersList.add(l);
    }

    public void notifyComponentTypeModelListeners() {
        notifyComponentTypeModelListeners(null);
    }

    /**
     * Notify registered listeners of the given <code>ComponentTypeModelEvent</code>.
     *
     * @param modelEvent encapsulates one of several possible events
     */
    public void notifyComponentTypeModelListeners(ComponentTypeModelEvent modelEvent) {
        Iterator it = componentTypeModelListenersList.iterator();
        while (it.hasNext()) {
            ComponentTypeModelListener l = (ComponentTypeModelListener)it.next();
            l.modified(modelEvent);
        }
    }
}
