/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// persistence layer
import gov.anl.aps.irmis.persistence.SemaphoreValue;
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.login.GroupName;

// service layer
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.pv.PVSearchParameters;

/**
 * Application data model for IRMIS PV Viewer. Stores graph of current set of ioc's
 * and related data. Stores record search result set, and the individual record
 * of interest. A list of listeners interested in this data is maintained, and
 * notified as requested.
 */
public class ComponentModel {

    // list of others interested in changes to the data model
    private List componentModelListenersList = new ArrayList();

    // used to flag a problem talking to db
    private IRMISException ie = null;

    private long dataTimestamp = 0;  // timestamp of our snapshot of component data
    private SemaphoreValue sv = new SemaphoreValue(); // semaphore used to manage concurrent component editing

    private Component siteComponent = null;  // housing root
    private List roomComponents = null;      // all 1st level housing components (rooms)
    private Component networkComponent = null;   // control root
    private Component utilityComponent = null;  // power root

    private Component selectedComponent = null; // selection in hierarchy
    private Long selectedComponentId = null;  // need this to retain last selection across refresh

    private Component componentToConfigure = null; // latched component
    private List configureUndoStack = new ArrayList();  // list of ConfigureAction

    private List componentTypes = null; // list of possible types
    private List filteredHousingComponentTypes = null; // same list after constraints applied
    private List filteredControlComponentTypes = null; // same list after constraints applied
    private List filteredPowerComponentTypes = null; // same list after constraints applied

    private List replacementComponentTypes = null; // list of potential replacements for selectedComponent

    private List systems = null; // list of unique ioc system names
    private List groupNames = null; // list of unique group names (ie. controls, rf, etc..)

	/**
	 * Do nothing constructor
	 */
	public ComponentModel() {
	}

    public void addComponentModelListener(ComponentModelListener l) {
        componentModelListenersList.add(l);
    }

    public void notifyComponentModelListeners() {
        notifyComponentModelListeners(null);
    }

    /**
     * Notify registered listeners of the given <code>ComponentModelEvent</code>.
     *
     * @param modelEvent encapsulates one of several possible events
     */
    public void notifyComponentModelListeners(ComponentModelEvent modelEvent) {
        Iterator it = componentModelListenersList.iterator();
        while (it.hasNext()) {
            ComponentModelListener l = (ComponentModelListener)it.next();
            l.modified(modelEvent);
        }
    }

    public IRMISException getIRMISException() {
        return this.ie;
    }
    public void setIRMISException(IRMISException ie) {
        this.ie = ie;
    }

    // clear out all the data
    public void reset() {
        ie = null;
        siteComponent = null; 
        roomComponents = null; 
        networkComponent = null;
        utilityComponent = null;
        componentToConfigure = null;
        configureUndoStack = new ArrayList();
        componentTypes = null;
        filteredHousingComponentTypes = null;
        filteredControlComponentTypes = null;
        filteredPowerComponentTypes = null;
        replacementComponentTypes = null;
        systems = null;
        groupNames = null;
        // hold on to id of selected component
        if (selectedComponent != null)
            selectedComponentId = selectedComponent.getId();
        else
            selectedComponentId = null;
        selectedComponent = null;
    }

    public void pushConfigureUndoStack(ConfigureAction action) {
        configureUndoStack.add(0, action);
    }

    public ConfigureAction popConfigureUndoStack() {
        if (configureUndoStack.size() == 0)
            return null;
        else
            return (ConfigureAction)configureUndoStack.remove(0);
    }
    
    public void clearConfigureUndoStack() {
        configureUndoStack.clear();
    }

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

    public Component getUtilityComponent() {
        return utilityComponent;
    }
    public void setUtilityComponent(Component value) {
        utilityComponent = value;
    }

    public Component getSelectedComponent() {
        return selectedComponent;
    }
    public void setSelectedComponent(Component value) {
        selectedComponent = value;
    }

    public Long getSelectedComponentId() {
        return selectedComponentId;
    }
    public void setSelectedComponentId(Long value) {
        selectedComponentId = value;
    }

    public Component getComponentToConfigure() {
        return componentToConfigure;
    }
    public void setComponentToConfigure(Component value) {
        componentToConfigure = value;
    }

    public List getComponentTypes() {
        return componentTypes;
    }
    public void setComponentTypes(List value) {
        componentTypes = value;
    }

    public List getReplacementComponentTypes() {
        return replacementComponentTypes;
    }
    public void setReplacementComponentTypes(List value) {
        replacementComponentTypes = value;
    }

    public List getSystems() {
        return systems;
    }
    public void setSystems(List value) {
        systems = value;
    }

    public List getGroupNames() {
        return groupNames;
    }
    public void setGroupNames(List value) {
        groupNames = value;
    }

    /**
     * Find group name on read-only list. Return null if
     * not found.
     */
    public GroupName findGroupName(String groupName) {
        Iterator gnIt = groupNames.iterator();
        GroupName gn = null;
        while (gnIt.hasNext()) {
            GroupName temp = (GroupName)gnIt.next();
            if (temp.getGroupName().equals(groupName)) {
                gn = temp;
                break;
            }
        }
        return gn;
    }

    public List getFilteredComponentTypes(int relationshipType) {
        switch (relationshipType) {
        case ComponentRelationshipType.CONTROL: {
            return filteredControlComponentTypes;
        }
        case ComponentRelationshipType.HOUSING: {
            return filteredHousingComponentTypes;
        }
        case ComponentRelationshipType.POWER: {
            return filteredPowerComponentTypes;
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
        case ComponentRelationshipType.POWER: {
            filteredPowerComponentTypes = value;
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

    /**
     * Use undo stack to determine if a delete has occurred.
     */
    public boolean deleteHasOccurred() {
        if (configureUndoStack == null ||
            configureUndoStack.size() == 0)
            return false;
        ConfigureAction action = (ConfigureAction)configureUndoStack.get(0);
        if (action.getType() == ConfigureAction.DELETE_CHILD)
            return true;
        else
            return false;
    }

}
