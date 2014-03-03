/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.admin.cfw;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// other IRMIS sub-applications

// persistence layer
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.login.Role;
import gov.anl.aps.irmis.persistence.login.RoleName;
import gov.anl.aps.irmis.persistence.component.ComponentType;

// service layer
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.shared.PersonService;

/**
 * Application data model for IRMIS Admin console. 
 * A list of listeners interested in this data is maintained, and
 * notified as requested.
 */
public class AdminModel {

    // list of others interested in changes to the data model
    private List adminModelListenersList = new ArrayList();

    // used to flag a problem talking to db
    private IRMISException ie = null;

    // for Users tab
    private List persons = new ArrayList();
    private Person selectedPerson = null;
    private Role selectedRole = null;
    private List roleNameList = new ArrayList();

    // for Export/Import tabs
    private List componentTypeList = new ArrayList();
    private List filteredComponentTypeList = new ArrayList();
    private ComponentType selectedComponentType;

	/**
	 * Do nothing constructor
	 */
	public AdminModel() {
	}

    public void reset() {
        persons = new ArrayList();
        roleNameList = new ArrayList();
    }

    public List getPersons() {
        return persons;
    }
    public void setPersons(List value) {
        persons = value;
    }

    public List getRoleNameList() {
        return roleNameList;
    }
    public void setRoleNameList(List value) {
        roleNameList = value;
    }

    public Person getSelectedPerson() {
        return selectedPerson;
    }
    public void setSelectedPerson(Person value) {
        selectedPerson = value;
    }

    public Role getSelectedRole() {
        return selectedRole;
    }
    public void setSelectedRole(Role value) {
        selectedRole = value;
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

    public void addAdminModelListener(AdminModelListener l) {
        adminModelListenersList.add(l);
    }

    public void notifyAdminModelListeners() {
        notifyAdminModelListeners(null);
    }

    /**
     * Notify registered listeners of the given <code>AdminModelEvent</code>.
     *
     * @param modelEvent encapsulates one of several possible events
     */
    public void notifyAdminModelListeners(AdminModelEvent modelEvent) {
        Iterator it = adminModelListenersList.iterator();
        while (it.hasNext()) {
            AdminModelListener l = (AdminModelListener)it.next();
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
