/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/

package gov.anl.aps.irmis.apps.componenthistory.echo2;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

// Echo2 
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.button.*;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import nextapp.echo2.app.event.WindowPaneListener;
import nextapp.echo2.app.event.WindowPaneEvent;
import nextapp.echo2.app.layout.*;

// EchoPointNG
import echopointng.able.Scrollable;
import echopointng.ContainerEx;
import echopointng.Tree;
import echopointng.tree.TreePath;
import echopointng.tree.DefaultMutableTreeNode;
import echopointng.tree.TreeModelEvent;
import echopointng.tree.TreeSelectionModel;
import echopointng.tree.TreeSelectionEvent;
import echopointng.tree.TreeSelectionListener;
import echopointng.tree.DefaultTreeSelectionModel;
import echopointng.tree.DefaultTreeCellRenderer;

// Echo2 support
import gov.anl.aps.irmis.apps.echo2support.DialogBox;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstance;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstanceState;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentState;
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.DAOStaleObjectStateException;
import gov.anl.aps.irmis.persistence.DAOException;

// IRMIS service layer
import gov.anl.aps.irmis.service.IRMISException;

/**
 * IRMIS Component History Application.
 */
public class ComponentHistoryLocationFindDialog extends DialogBox {

    static final String title = "Find Existing Installed Component";
    static final int width = 400;
    static final int height = 500;

    private ComponentHistoryController controller = null;
    private ComponentHistoryModel model = null;

    private Tree housingTree;
    private TreeSelectionModel treeSelectionModel;
    private FilteredHousingComponentTreeModel housingTreeModel;

    private TreePath selectedComponentPath;

    public ComponentHistoryLocationFindDialog(ComponentHistoryController controller) {
        super(title, width, height);

        this.controller = controller;
        model = controller.getModel();
        model.addComponentHistoryModelListener(new ComponentHistoryModelListener() {
                public void modified(ComponentHistoryModelEvent e) {
                    updateView(e);
                }
            });

        Column userColumn = getUserColumn();

        ContainerEx treeContainer = new ContainerEx();
        treeContainer.setBackground(Color.WHITE);
        treeContainer.setHeight(new Extent(422));
        treeContainer.setScrollBarPolicy(Scrollable.ALWAYS);
        housingTree = new Tree();
        housingTree.setStyleName("Default.Tree");
        treeSelectionModel = new DefaultTreeSelectionModel();
        treeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeSelectionModel.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    selectedComponentPath = e.getNewLeadSelectionPath();
                }
            });
        housingTree.setSelectionModel(treeSelectionModel);
        ComponentTreeCellRenderer treeCellRenderer = new ComponentTreeCellRenderer();
        treeCellRenderer.setStyleName("Default.ComponentTreeCellRenderer");
        housingTree.setCellRenderer(treeCellRenderer);
        housingTree.setRootVisible(true);
        treeContainer.add(housingTree);
        
        userColumn.add(treeContainer);
    }

    /**
     * Update graphical view of IOCModel data based on event type.
     *
     * @param event type of data model change
     */
    public void updateView(ComponentHistoryModelEvent event) {

        // check first for any exception
        IRMISException ie = model.getIRMISException();
        if (ie != null) {  // uh-oh: figure out if it's fatal or we just need to reload data
            // don't do anything but return, since we're all embedded in a dialog box
            return;
        }

        switch(event.getType()) {
            
        case ComponentHistoryModelEvent.RELOAD_COMPLETE: {
            housingTreeModel = new FilteredHousingComponentTreeModel();
            housingTreeModel.setRoot(model.getSiteComponent());
            housingTree.setModel(housingTreeModel);
            TreeModelEvent he = 
                new TreeModelEvent(this, new Object[] {model.getSiteComponent()});
            housingTreeModel.fireTreeStructureChanged(he);            
            break;
        }

        default: {}
        }
            
    }

    public boolean okAction(ActionEvent e) {
        // put component and component tree path into model
        if (selectedComponentPath != null) {

            // Build slash separated string representing housing path to component
            String stringTreePath = "";
            int pathLength = selectedComponentPath.getPathCount();
            for (int i=0 ; i < pathLength ; i++) {
                Component c = (Component)selectedComponentPath.getPathComponent(i);
                stringTreePath = stringTreePath + "/" + c.toString(ComponentRelationshipType.HOUSING);
            }
            model.setSelectedComponentPath(stringTreePath);

            model.setSelectedComponent((gov.anl.aps.irmis.persistence.component.Component)selectedComponentPath.getLastPathComponent());

            // clear out other search terms
            model.setSelectedComponentType(null);
            model.setSelectedSerialNumber(null);
        }
        return true;
    }

    public void cancelAction(ActionEvent e) {
        model.setSelectedComponentPath(null);
        model.setSelectedComponent(null);
    }

    public class ComponentTreeCellRenderer extends DefaultTreeCellRenderer {

        public ComponentTreeCellRenderer() {
            super();
        }

        public Label getTreeCellRendererText(Tree tree,
                                             Object node,
                                             boolean selected,
                                             boolean expanded,
                                             boolean leaf) {

            gov.anl.aps.irmis.persistence.component.Component c = 
                (gov.anl.aps.irmis.persistence.component.Component)node;
            String text = c.toString(ComponentRelationshipType.HOUSING);
            Label label = super.getTreeCellRendererText(tree, node, selected, expanded, leaf);
            label.setText(text);
            label.setIcon(null);
            return label;

        }

    }

}