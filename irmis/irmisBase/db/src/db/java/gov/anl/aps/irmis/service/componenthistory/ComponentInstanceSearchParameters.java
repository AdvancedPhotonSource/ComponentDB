/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.service.componenthistory;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.Component;

/**
 * Container for component instance search parameters.
 */
public class ComponentInstanceSearchParameters {

    private boolean matchAll = true;
    private String ctName = null;
    private String sn = null;
    private String location = null;
    private Component component = null;

	public ComponentInstanceSearchParameters() {
	}

    public boolean getMatchAll() {
        return matchAll;
    }
    public void setMatchAll(boolean val) {
        matchAll = val;
    }

    public String getComponentTypeName() {
        return ctName;
    }
    public void setComponentTypeName(String val) {
        ctName = val;
    }

    public String getSerialNumber() {
        return sn;
    }
    public void setSerialNumber(String val) {
        sn = val;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String val) {
        location = val;
    }

    public Component getComponent() {
        return component;
    }
    public void setComponent(Component val) {
        component = val;
    }
}
