/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.cable.cfw;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

// persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentPort;
import gov.anl.aps.irmis.persistence.component.ComponentRelationship;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.pv.IOC;

// service layer
import gov.anl.aps.irmis.service.component.ComponentService;
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.IRMISException;

// application helpers
import gov.anl.aps.irmis.apps.AppsUtil;

/**
 * Extension of ComponentPortTreeModel which can apply various application-specific filters to 
 * what components are shown in the tree. The main purpose is to handle the adjusted
 * child indices that occur when components are filtered out. 
 */
public class FilteredControlComponentPortTreeModel extends ComponentPortTreeModel {

    List iocComponentList = null;    // only show these iocs

    public FilteredControlComponentPortTreeModel() {
        super(ComponentRelationshipType.CONTROL);
    }

    public TreePath getTreePath(Object node) {
        if (node instanceof Component) {
            List tempPath = ComponentService.getComponentPathToRoot((Component)node, hierarchy, iocComponentList);
            if (tempPath == null)
                return null;
            Collections.reverse(tempPath);
            return new TreePath(tempPath.toArray());
            
        } else {
            ComponentPort port = (ComponentPort)node;
            Component c = port.getComponent();
            List tempPath = ComponentService.getComponentPathToRoot(c, hierarchy);
            Collections.reverse(tempPath);
            tempPath.add(port);
            return new TreePath(tempPath.toArray());
        }
    }

    public Object getChild(Object parent, int index) {
        if (parent instanceof ComponentPort)
            return super.getChild(parent, index);

        Component c = (Component)parent;
        List childList = c.getChildRelationships(hierarchy);
        if (childList == null || childList.size()==0) {
            return super.getChild(parent, index);

        } else {
            // apply control filter (if any)
            if (iocComponentList != null &&
                c.getComponentType().getComponentTypeName().equals("Network")) {
                return iocComponentList.get(index);

            } else {  // no filter
                return super.getChild(parent, index);
            }
        }
    }

    public int getChildCount(Object parent) {
        if (parent instanceof ComponentPort)
            return super.getChildCount(parent);

        Component c = (Component)parent;
        List childRelationships = c.getChildRelationships(hierarchy);
        if (childRelationships == null || childRelationships.size()==0) {
            return super.getChildCount(parent);

        } else {
            // apply control filter (if any)
            if (iocComponentList != null &&
                c.getComponentType().getComponentTypeName().equals("Network")) {
                return iocComponentList.size();

            } else {
                return super.getChildCount(parent);
            }
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (child instanceof ComponentPort) {
            return super.getIndexOfChild(parent, child);

        } else {
            Component pc = (Component)parent;
            Component childc = (Component)child;
            ComponentRelationship parentRel = childc.getParentRelationship(hierarchy);
            if (parentRel == null) {
                return super.getIndexOfChild(parent, child);

            } else {
                // can't use logical order when we have filtered display
                if (iocComponentList != null &&
                    pc.getComponentType().getComponentTypeName().equals("Network")) {
                    return iocComponentList.indexOf(childc);
                    
                } else {
                    return super.getIndexOfChild(parent, child);
                }
            }
        }
    }

    /**
     * Filters the control display to only iocs that are part of given system(s).
     */
    public void setIOCConstraint(List iocList) {
        int hierarchy = ComponentRelationshipType.CONTROL;
        
        if (iocList != null && iocList.size() > 0) {

            // build up list of just ioc names
            Iterator iocIt = iocList.iterator();
            List iocNameList = new ArrayList();
            while (iocIt.hasNext()) {
                IOC ioc = (IOC)iocIt.next();
                iocNameList.add(ioc.getIocName());
            }

            // then, figure out set of components which match IOC objects
            iocComponentList = new ArrayList();
            List childRList = root.getChildRelationships(hierarchy);
            if (childRList != null && childRList.size() > 0) {
                Iterator childRIt = childRList.iterator();
                while (childRIt.hasNext()) {
                    ComponentRelationship iocCR = (ComponentRelationship)childRIt.next();
                    
                    if (iocNameList.contains(iocCR.getLogicalDescription()))
                        iocComponentList.add(iocCR.getChildComponent());
                }
            }
            
        }  else {
            iocComponentList = null;
        }
    }

    public List getIOCComponentList() {
        return iocComponentList;
    }


}
