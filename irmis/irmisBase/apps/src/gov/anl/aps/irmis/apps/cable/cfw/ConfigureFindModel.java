/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.cable.cfw;

import java.util.List;
import java.util.ArrayList;

// persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentType;

// application helpers
import gov.anl.aps.irmis.apps.AbstractTreeModel;
import gov.anl.aps.irmis.apps.AppsUtil;

/**
 * Simple Java bean to hold various component find constraints.
 */
public class ConfigureFindModel {

    private List componentTypes = null;
    private List filteredComponentTypes = null;
    private List roomComponents = null;
    private List systems = null;

    private Long componentId = null;
    private List selectedComponentTypes = null;
    private List selectedRoomComponents = null;
    private List selectedSystems = null;
    private boolean clearSearchField = false;
    private boolean cancelled = false;

    private int componentIndex = 1;

    public ConfigureFindModel() {
        reset();
    }

    public void reset() {
        componentId = null;
        selectedComponentTypes = new ArrayList();
        selectedRoomComponents = new ArrayList();
        selectedSystems = new ArrayList();
        clearSearchField = false;
        cancelled = false;
        filteredComponentTypes = new ArrayList();
    }

    public boolean filterApplied() {
        if ((selectedRoomComponents != null && selectedRoomComponents.size() > 0) ||
            (selectedSystems != null && selectedSystems.size() > 0))
            return true;
        else
            return false;
    }

    public List getComponentTypes() {
        return componentTypes;
    }
    public void setComponentTypes(List value) {
        componentTypes = value;
    }

    public List getFilteredComponentTypes() {
        return filteredComponentTypes;
    }
    public void setFilteredComponentTypes(List value) {
        filteredComponentTypes = value;
    }

    public List getRoomComponents() {
        return roomComponents;
    }
    public void setRoomComponents(List value) {
        roomComponents = value;
    }

    public List getSystems() {
        return systems;
    }
    public void setSystems(List value) {
        systems = value;
    }

    public Long getComponentId() {
        return componentId;
    }
    public void setComponentId(Long value) {
        componentId = value;
    }

    public List getSelectedComponentTypes() {
        return selectedComponentTypes;
    }
    public void setSelectedComponentTypes(List value) {
        selectedComponentTypes = value;
    }

    public List getSelectedRoomComponents() {
        return selectedRoomComponents;
    }
    public void setSelectedRoomComponents(List value) {
        selectedRoomComponents = value;
    }

    public List getSelectedSystems() {
        return selectedSystems;
    }
    public void setSelectedSystems(List value) {
        selectedSystems = value;
    }

    public boolean getClearSearchField() {
        return clearSearchField;
    }
    public void setClearSearchField(boolean value) {
        clearSearchField = value;
    }

    public boolean getCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean value) {
        cancelled = value;
    }

    public int getComponentIndex() {
        return componentIndex;
    }
    public void setComponentIndex(int value) {
        componentIndex = value;
    }

}
