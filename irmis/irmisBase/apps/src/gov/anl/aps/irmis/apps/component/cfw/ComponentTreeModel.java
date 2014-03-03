/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw;

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
import gov.anl.aps.irmis.persistence.component.ComponentRelationship;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;

// service layer
import gov.anl.aps.irmis.service.component.ComponentService;
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.IRMISException;

// application helpers
import gov.anl.aps.irmis.apps.AbstractTreeModel;
import gov.anl.aps.irmis.apps.AppsUtil;

/**
 * Custom tree model for rendering component hierarchy.
 */
public class ComponentTreeModel extends AbstractTreeModel {

    Component root = null;
    int hierarchy;

    List componentList = null;        // components for find prev/next
    int componentListFindIndex = 0;   // current index of prev/next progress

    /**
     * Hide no-arg constructor.
     */
    private ComponentTreeModel() {
    }

    public ComponentTreeModel(int hierarchy) {
        super();
        this.hierarchy = hierarchy;
    }

    public void reset() {
        componentList = null;
        componentListFindIndex = 0;
    }

    public Object getRoot() {
        return root;
    }
    public void setRoot(Object o) {
        root = (Component)o;
    }

    /**
     * Get list of components that the find prev/next function is using.
     */
    public List getComponentList() {
        return componentList;
    }
    /**
     * Set list of components that the find prev/next function is using.
     */
    public void setComponentList(List value) {
        componentList = value;
    }
    public int getComponentListFindIndex() {
        return componentListFindIndex;
    }
    public void setComponentListFindIndex(int value) {
        componentListFindIndex = value;
    }

    public TreePath getTreePath(Component node) {
        List tempPath = ComponentService.getComponentPathToRoot(node, hierarchy);
        Collections.reverse(tempPath);
        return new TreePath(tempPath.toArray());
    }

    public Object getChild(Object parent, int index) {
        Component c = (Component)parent;
        List childList = c.getChildRelationships(hierarchy);

        if (childList == null || childList.size()==0) {
            return null;

        } else {
            ComponentRelationship cr =
                (ComponentRelationship)childList.get(index);
            return cr.getChildComponent();
        }
    }

    public int getChildCount(Object parent) {
        Component c = (Component)parent;
        List childRelationships = c.getChildRelationships(hierarchy);
        if (childRelationships == null || childRelationships.size()==0) {
            return 0;
        } else {
            return childRelationships.size();
        }
    }

    public boolean isLeaf(Object node) {
        if (getChildCount(node) == 0)
            return true;
        else
            return false;
    }

    public int getIndexOfChild(Object parent, Object child) {
        Component childc = (Component)child;
        ComponentRelationship parentRel = childc.getParentRelationship(hierarchy);
        if (parentRel == null) {
            return -1;
        } else {
            return parentRel.getLogicalOrder();
        }
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        System.out.println("*** valueForPathChanged");
    }

}
