/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenthistory.echo2;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;

// persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.Manufacturer;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstance;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentState;

// service layer
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Application data model for IRMIS component history app. Stores graph component
 * instances and related history data. 
 * A list of listeners interested in this data is maintained, and
 * notified as requested.
 */
public class ComponentHistoryModel {

    // list of others interested in changes to the data model
    private List chModelListenersList = new ArrayList();

    // used to flag a problem talking to db
    private IRMISException ie = null;

    // list of component types in irmis db
    private List componentTypeList;
    private List filteredComponentTypeList;
    private Component siteComponent = null;  // housing root

    // list of all categories (ComponentStateCategory)
    private List categoryList;

    // list of all states by category
    private List locationStateList;
    private List operationStateList;
    private List nrtlStateList;

    // search terms
    private boolean matchAllOption = true;
    private ComponentType selectedComponentType;
    private String selectedComponentTypeName;
    private String selectedSerialNumber;
    private String selectedLocation;
    private String selectedComponentPath;  // installed component selection path
    private Component selectedComponent; // component from end of selection path

    // component instances
    private Component lastFailedComponent;
    private List openComponentSlots;
    private List componentInstanceList;
    private ComponentInstance selectedComponentInstance;

    // new component instance info
    private Date newInstanceEventDate;
    private String newInstanceSerialNumber;
    private String newInstanceComponentTypeName;
    private String newInstanceLocationString;
    private Component newInstanceAssociatedComponent;
    private String newInstanceInitialState;

    // list of ComponentInstanceState for each possible category
    private List locationHistoryList;
    private List operationHistoryList;
    private List calibrationHistoryList;
    private List nrtlHistoryList;

    // list of all component type manufacturers
    private List mfgList;

	/**
	 * Create some initial empty lists.
	 */
	public ComponentHistoryModel() {
        componentInstanceList = new ArrayList();
        locationHistoryList = new ArrayList();
        operationHistoryList = new ArrayList();
        calibrationHistoryList = new ArrayList();
        nrtlHistoryList = new ArrayList();
	}

    public void addComponentHistoryModelListener(ComponentHistoryModelListener l) {
        chModelListenersList.add(l);
    }

    public void notifyComponentHistoryModelListeners() {
        notifyComponentHistoryModelListeners(null);
    }

    /**
     * Notify registered listeners of the given <code>ComponentHistoryModelEvent</code>.
     *
     * @param modelEvent encapsulates one of several possible events
     */
    public void notifyComponentHistoryModelListeners(ComponentHistoryModelEvent modelEvent) {
        Iterator it = chModelListenersList.iterator();
        while (it.hasNext()) {
            ComponentHistoryModelListener l = (ComponentHistoryModelListener)it.next();
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
     * Clear out selected data.
     */
    public void resetInstanceList() {
        componentInstanceList = new ArrayList();
    }

    /**
     * Clear out selected data.
     */
    public void resetHistoryLists() {
        locationHistoryList = new ArrayList();
        operationHistoryList = new ArrayList();
        calibrationHistoryList = new ArrayList();
        nrtlHistoryList = new ArrayList();
    }

    public List getComponentInstanceList() {
        return componentInstanceList;
    }
    public void setComponentInstanceList(List componentInstanceList) {
        this.componentInstanceList = componentInstanceList;
    }


    // Component instance history by category
    public List getLocationHistoryList() {
        return locationHistoryList;
    }
    public void setLocationHistoryList(List val) {
        locationHistoryList = val;
    }

    public List getOperationHistoryList() {
        return operationHistoryList;
    }
    public void setOperationHistoryList(List val) {
        operationHistoryList = val;
    }

    public List getCalibrationHistoryList() {
        return calibrationHistoryList;
    }
    public void setCalibrationHistoryList(List val) {
        calibrationHistoryList = val;
    }

    public List getNRTLHistoryList() {
        return nrtlHistoryList;
    }
    public void setNRTLHistoryList(List val) {
        nrtlHistoryList = val;
    }

    public List getComponentTypeList() {
        return componentTypeList;
    }
    public void setComponentTypeList(List componentTypeList) {
        this.componentTypeList = componentTypeList;
    }

    public ComponentType findComponentType(String ctName) {
        if (componentTypeList == null)
            return null;

        Iterator it = componentTypeList.iterator();
        while (it.hasNext()) {
            ComponentType ct = (ComponentType)it.next();
            if (ct.getComponentTypeName().equals(ctName))
                return ct;
        }
        return null;
    }

    public List getFilteredComponentTypeList() {
        return filteredComponentTypeList;
    }
    public void setFilteredComponentTypeList(List componentTypeList) {
        this.filteredComponentTypeList = componentTypeList;
    }
    
    // Component Instance search terms
    public boolean getMatchAllOption() {
        return matchAllOption;
    }
    public void setMatchAllOption(boolean val) {
        matchAllOption = val;
    }

    public ComponentType getSelectedComponentType() {
        return selectedComponentType;
    }
    public void setSelectedComponentType(ComponentType value) {
        selectedComponentType = value;
    }

    public String getSelectedComponentTypeName() {
        return selectedComponentTypeName;
    }
    public void setSelectedComponentTypeName(String value) {
        selectedComponentTypeName = value;
    }

    public Component getSiteComponent() {
        return siteComponent;
    }
    public void setSiteComponent(Component value) {
        siteComponent = value;
    }

    public List getOpenComponentSlots() {
        return openComponentSlots;
    }
    public void setOpenComponentSlots(List val) {
        openComponentSlots = val;
    }

    public Component getLastFailedComponent() {
        return lastFailedComponent;
    }
    public void setLastFailedComponent(Component value) {
        lastFailedComponent = value;
    }

    public ComponentInstance getSelectedComponentInstance() {
        return selectedComponentInstance;
    }
    public void setSelectedComponentInstance(ComponentInstance value) {
        selectedComponentInstance = value;
    }

    public String getSelectedSerialNumber() {
        return selectedSerialNumber;
    }
    public void setSelectedSerialNumber(String value) {
        selectedSerialNumber = value;
    }

    public String getSelectedLocation() {
        return selectedLocation;
    }
    public void setSelectedLocation(String value) {
        selectedLocation = value;
    }

    public String getSelectedComponentPath() {
        return selectedComponentPath;
    }
    public void setSelectedComponentPath(String value) {
        selectedComponentPath = value;
    }

    public Component getSelectedComponent() {
        return selectedComponent;
    }
    public void setSelectedComponent(Component value) {
        selectedComponent = value;
    }


    // list of all possible ComponentStateCategory
    public List getCategoryList() {
        return categoryList;
    }
    public void setCategoryList(List val) {
        categoryList = val;
    }

    // list of ComponentState by category
    public List getLocationStateList() {
        return locationStateList;
    }
    public void setLocationStateList(List val) {
        locationStateList = val;
    }
    public ComponentState findLocationState(String csName) {
        if (locationStateList == null)
            return null;

        Iterator it = locationStateList.iterator();
        while (it.hasNext()) {
            ComponentState cs = (ComponentState)it.next();
            if (cs.getState().equals(csName))
                return cs;
        }
        return null;
    }

    public List getOperationStateList() {
        return operationStateList;
    }
    public void setOperationStateList(List val) {
        operationStateList = val;
    }
    public ComponentState findOperationState(String csName) {
        if (operationStateList == null)
            return null;

        Iterator it = operationStateList.iterator();
        while (it.hasNext()) {
            ComponentState cs = (ComponentState)it.next();
            if (cs.getState().equals(csName))
                return cs;
        }
        return null;
    }

    public List getNRTLStateList() {
        return nrtlStateList;
    }
    public void setNRTLStateList(List val) {
        nrtlStateList = val;
    }
    public ComponentState findNRTLState(String csName) {
        if (nrtlStateList == null)
            return null;

        Iterator it = nrtlStateList.iterator();
        while (it.hasNext()) {
            ComponentState cs = (ComponentState)it.next();
            if (cs.getState().equals(csName))
                return cs;
        }
        return null;
    }

    public List getMfgList() {
        return mfgList;
    }
    public void setMfgList(List val) {
        mfgList = val;
    }
    public Manufacturer findMfg(String mfgName) {
        if (mfgList == null)
            return null;

        Iterator it = mfgList.iterator();
        while (it.hasNext()) {
            Manufacturer mfg = (Manufacturer)it.next();
            if (mfg.getManufacturerName().equals(mfgName))
                return mfg;
        }
        return null;
    }

    public Date getNewInstanceEventDate() {
        return newInstanceEventDate;
    }
    public void setNewInstanceEventDate(Date val) {
        newInstanceEventDate = val;
    }

    public String getNewInstanceSerialNumber() {
        return newInstanceSerialNumber;
    }
    public void setNewInstanceSerialNumber(String val) {
        newInstanceSerialNumber = val;
    }

    public String getNewInstanceComponentTypeName() {
        return newInstanceComponentTypeName;
    }
    public void setNewInstanceComponentTypeName(String val) {
        newInstanceComponentTypeName = val;
    }

    public String getNewInstanceLocationString() {
        return newInstanceLocationString;
    }
    public void setNewInstanceLocationString(String val) {
        newInstanceLocationString = val;
    }

    public Component getNewInstanceAssociatedComponent() {
        return newInstanceAssociatedComponent;
    }
    public void setNewInstanceAssociatedComponent(Component c) {
        newInstanceAssociatedComponent = c;
    }

    public String getNewInstanceInitialState() {
        return newInstanceInitialState;
    }
    public void setNewInstanceInitialState(String val) {
        newInstanceInitialState = val;
    }

}
