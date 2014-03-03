/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw.wizard;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.login.GroupName;

// IRMIS service layer
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.component.ComponentService;
import gov.anl.aps.irmis.service.shared.PersonService;
import gov.anl.aps.irmis.service.IRMISException;

public class NewComponentWizardModel {

    // final step sets this to false
    private boolean cancelled = true;

    // new/edited component
    private Component component = null;
    
    // intended parent of new/edited component
    private Component parentComponent = null;
    private int hierarchy;

    // flags indicate if new component can be automatically added to other hierarchies
    private boolean canAddToControl = false;
    private boolean canAddToPower = false;

    private GroupName selectedGroupName = null;

    // enum lists (ie. available types)
    private List componentTypes = null;
    private List filteredComponentTypes = null;
    private List groupNames = null;

    /**
     * Constructor used when creating new component type in wizard.
     */
    public NewComponentWizardModel(List componentTypes, Component parent, int hierarchy) {
        this.componentTypes = componentTypes;
        if (this.componentTypes == null) 
            this.componentTypes = new ArrayList();
        this.filteredComponentTypes = componentTypes;
        this.parentComponent = parent;
        this.hierarchy = hierarchy;
        component = new Component();
        initializePickLists();
    }

    private void initializePickLists() {
        // load up pick-list data from database
        try {
            groupNames = PersonService.findGroupNameList();
            if (groupNames == null)
                groupNames = new ArrayList();

        } catch (IRMISException ie) {
            ie.printStackTrace();
        }
    }

    public Component getComponent() {
        return component;
    }
    public void setComponent(Component value) {
        component = value;
    }

    public Component getParentComponent() {
        return parentComponent;
    }
    public int getHierarchy() {
        return hierarchy;
    }

    public boolean getCanAddToControl() {
        return canAddToControl;
    }
    public void setCanAddToControl(boolean value) {
        canAddToControl = value;
    }
    public boolean getCanAddToPower() {
        return canAddToPower;
    }
    public void setCanAddToPower(boolean value) {
        canAddToPower = value;
    }
        

    /**
     * Read-only list of <code>ComponentType</code> objects.
     */
    public List getComponentTypes() {
        return componentTypes;
    }

    public List getFilteredComponentTypes() {
        return filteredComponentTypes;
    }
    public void setFilteredComponentTypes(List value) {
        filteredComponentTypes = value;
    }

    /**
     * Read-only list of <code>GroupName</code> objects.
     */
    public List getGroupNames() {
        return groupNames;
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

    public GroupName getSelectedGroupName() {
        return selectedGroupName;
    }
    public void setSelectedGroupName(GroupName value) {
        selectedGroupName = value;
    }

    /**
     * Find componentType on read-only list. Return null if
     * not found.
     */
    public ComponentType findComponentType(String typeName) {
        Iterator ctIt = componentTypes.iterator();
        ComponentType ct = null;
        while (ctIt.hasNext()) {
            ComponentType temp = (ComponentType)ctIt.next();
            if (temp.getComponentTypeName().equals(typeName)) {
                ct = temp;
                break;
            }
        }
        return ct;
    }

    public boolean getCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean value) {
        cancelled = value;
    }
    

}
