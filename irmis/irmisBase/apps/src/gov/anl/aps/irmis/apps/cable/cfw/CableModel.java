/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.cable.cfw;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// other IRMIS sub-applications
import gov.anl.aps.irmis.apps.component.cfw.ComponentDocument;

// persistence layer
import gov.anl.aps.irmis.persistence.SemaphoreValue;
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentPort;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.Cable;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.audit.AuditAction;

// service layer
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.pv.PVSearchParameters;

/**
 * Application data model for IRMIS Cable Viewer. 
 * A list of listeners interested in this data is maintained, and
 * notified as requested.
 */
public class CableModel {

    // list of others interested in changes to the data model
    private List cableModelListenersList = new ArrayList();

    // used to flag a problem talking to db
    private IRMISException ie = null;

    private long dataTimestamp = 0;  // timestamp of our snapshot of component data
    private SemaphoreValue sv = new SemaphoreValue(); // semaphore used to manage concurrent component editing

    private ComponentPort selectedPort1 = null;
    private ComponentPort selectedPort2 = null;

    private List cables = new ArrayList();
    private Cable selectedCable = null;

    private Component siteComponent = null;  // housing root
    private List roomComponents = null;      // all 1st level housing components (rooms)
    private Component networkComponent = null;   // control root

    private Component selectedComponent1 = null; // selection in hierarchy
    private Component selectedComponent2 = null; // selection in hierarchy

    private List cableUndoStack = new ArrayList();  // list of CableAction

    private List componentTypes = null; // list of possible types
    private List filteredHousingComponentTypes = null; // same list after constraints applied
    private List filteredControlComponentTypes = null; // same list after constraints applied

    private List systems = null; // list of unique ioc system names
    // end from ComponentModel

	/**
	 * Do nothing constructor
	 */
	public CableModel() {
	}

    public void reset() {
        ie = null;
        siteComponent = null;
        roomComponents = null;
        networkComponent = null;
        //selectedComponent1 = null;
        selectedComponent2 = null;
        componentTypes = null;
        filteredHousingComponentTypes = null;
        filteredControlComponentTypes = null;
        systems = null;
        //selectedPort1 = null;
        selectedPort2 = null;
        cables = new ArrayList();
        cableUndoStack = new ArrayList();
        selectedCable = null;
    }

    public void pushCableUndoStack(CableAction action) {
        cableUndoStack.add(0, action);
    }

    public CableAction popCableUndoStack() {
        if (cableUndoStack.size() == 0)
            return null;
        else
            return (CableAction)cableUndoStack.remove(0);
    }
    
    public void clearCableUndoStack() {
        cableUndoStack.clear();
    }

    public ComponentPort getSelectedPort1() {
        return this.selectedPort1;
    }
    public void setSelectedPort1(ComponentPort value) {
        this.selectedPort1 = value;
    }

    public ComponentPort getSelectedPort2() {
        return this.selectedPort2;
    }
    public void setSelectedPort2(ComponentPort value) {
        this.selectedPort2 = value;
    }

    public List getCables() {
        return this.cables;
    }
    public void setCables(List value) {
        this.cables = value;
    }

    public Cable getSelectedCable() {
        return this.selectedCable;
    }
    public void setSelectedCable(Cable value) {
        this.selectedCable = value;
    }

    public void addCableModelListener(CableModelListener l) {
        cableModelListenersList.add(l);
    }

    public void notifyCableModelListeners() {
        notifyCableModelListeners(null);
    }



    // begin from ComponentModel

    public Component getSiteComponent() {
        return siteComponent;
    }
    public void setSiteComponent(Component value) {
        siteComponent = value;
    }

    public List getRoomComponents() {
        return roomComponents;
    }
    public void setRoomComponents(List value) {
        roomComponents = value;
    }

    public Component getNetworkComponent() {
        return networkComponent;
    }
    public void setNetworkComponent(Component value) {
        networkComponent = value;
    }

    public Component getSelectedComponent1() {
        return selectedComponent1;
    }
    public void setSelectedComponent1(Component value) {
        selectedComponent1 = value;
    }

    public Component getSelectedComponent2() {
        return selectedComponent2;
    }
    public void setSelectedComponent2(Component value) {
        selectedComponent2 = value;
    }

    public List getComponentTypes() {
        return componentTypes;
    }
    public void setComponentTypes(List value) {
        componentTypes = value;
    }

    public List getSystems() {
        return systems;
    }
    public void setSystems(List value) {
        systems = value;
    }

    public List getFilteredComponentTypes(int relationshipType) {
        switch (relationshipType) {
        case ComponentRelationshipType.CONTROL: {
            return filteredControlComponentTypes;
        }
        case ComponentRelationshipType.HOUSING: {
            return filteredHousingComponentTypes;
        }
        default: {
            return null;
        }
        }
    }
    public void setFilteredComponentTypes(List value, int relationshipType) {
        switch (relationshipType) {
        case ComponentRelationshipType.CONTROL: {
            filteredControlComponentTypes = value;
            break;
        }
        case ComponentRelationshipType.HOUSING: {
            filteredHousingComponentTypes = value;
            break;
        }
        }
    }

    public long getDataTimestamp() {
        return dataTimestamp;
    }
    public void setDataTimestamp(long value) {
        dataTimestamp = value;
    }

    public SemaphoreValue getSemaphoreValue() {
        return sv;
    }
    public void setSemaphoreValue(SemaphoreValue value) {
        sv = value;
    }
    // end from ComponentModel


    /**
     * Notify registered listeners of the given <code>CableModelEvent</code>.
     *
     * @param modelEvent encapsulates one of several possible events
     */
    public void notifyCableModelListeners(CableModelEvent modelEvent) {
        Iterator it = cableModelListenersList.iterator();
        while (it.hasNext()) {
            CableModelListener l = (CableModelListener)it.next();
            l.modified(modelEvent);
        }
    }

    public IRMISException getIRMISException() {
        return this.ie;
    }
    public void setIRMISException(IRMISException ie) {
        this.ie = ie;
    }

}
