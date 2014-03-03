/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/

package gov.anl.aps.irmis.apps.componenthistory.echo2;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

// Echo2 
import nextapp.echo2.app.Row;
import nextapp.echo2.app.CheckBox;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.SelectField;
import nextapp.echo2.app.TextField;
import nextapp.echo2.app.list.AbstractListModel;
import nextapp.echo2.app.list.ListModel;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;

// EchoPointNG
import echopointng.ContainerEx;
import echopointng.Tree;
import echopointng.tree.TreeSelectionModel;
import echopointng.tree.DefaultTreeSelectionModel;
import echopointng.tree.TreeSelectionListener;
import echopointng.tree.TreeSelectionEvent;
import echopointng.tree.TreePath;
import echopointng.tree.DefaultTreeCellRenderer;
import echopointng.tree.TreeModelEvent;
import echopointng.able.Scrollable;
import echopointng.DateChooser;

// Echo2 support
import gov.anl.aps.irmis.apps.echo2support.DialogBox;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentInstance;
import gov.anl.aps.irmis.persistence.componenthistory.ComponentState;

/**
 * IRMIS component history update location dialox box.
 */
public class ComponentHistoryUpdateLocationDialog extends DialogBox {

    static final String title = "Update Location";
    static final int width = 350;
    static final int height = 228 + 50;
    static final Extent locationHeight = new Extent(height);
    static final Extent treeHeight = new Extent(456+50);
    static final int treeHeightOffset = 228;
    static final int treeErrorHeightOffset = 18;
    static final int eventDateHeightOffset = 185;
    static final Extent fieldWidth = new Extent(250);

    private Component selectedComponent;
    
    private ComponentHistoryController controller;
    private ComponentHistoryModel model;
    private boolean showTree;
    private Column userColumn;

    private SelectField stateSelectField;
    private TextField commentTextField;
    private Column treeColumn;
    private Label treeErrorLabel;
    private boolean hasInstance;
    private Tree housingTree;
    private DefaultTreeSelectionModel treeSelectionModel;
    private FilteredHousingComponentTreeModel housingTreeModel;
    private Column locationColumn;
    private TextField locationTextField;
    private final DateFormat sdf = new SimpleDateFormat("MM/dd/yy");        
    private DateChooser eventDateChooser;
    private Date eventDate;
    private Label eventDateLabel;

    public ComponentHistoryUpdateLocationDialog(ComponentHistoryController ctrl) {
        super(title, width, height);

        controller = ctrl;
        model = controller.getModel();

        userColumn = getUserColumn();
        userColumn.setCellSpacing(new Extent(5));
        userColumn.setInsets(new Insets(5));

        ComponentInstance ci = model.getSelectedComponentInstance();
        Component c = ci.getComponent();
        boolean installed = (c != null);

        // prompt for date of event
        eventDate = new Date();
        String nowString = sdf.format(eventDate);
        Label eventLabel = new Label("Date of history entry:");
        userColumn.add(eventLabel);
        Row eventDateRow = new Row();
        eventDateRow.setCellSpacing(new Extent(8));
        eventDateLabel = new Label(nowString);
        eventDateRow.add(eventDateLabel);
        eventDateChooser = new DateChooser();
        eventDateChooser.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    eventDate = eventDateChooser.getSelectedDate().getTime();
                    String eventDateString = sdf.format(eventDate);
                    eventDateLabel.setText(eventDateString);
                }
            });
        CheckBox selectEventDateCheckBox = new CheckBox("pick a different date");
        selectEventDateCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    CheckBox cb = (CheckBox)e.getSource();
                    if (cb.isSelected()) {
                        getUserColumn().add(eventDateChooser, 2);
                        //setHeight(eventDateHeightExtent);
                        setHeight(new Extent(getHeight().getValue()+eventDateHeightOffset));
                    } else {
                        getUserColumn().remove(2);
                        //setHeight(heightExtent);
                        setHeight(new Extent(getHeight().getValue()-eventDateHeightOffset));
                    }
                }
            });
        eventDateRow.add(selectEventDateCheckBox);
        userColumn.add(eventDateRow);

        // prompt for new location state (if not installed)
        Label stateSelectLabel = new Label("Pick new location state:");
        userColumn.add(stateSelectLabel);

        stateSelectField = new SelectField();
        stateSelectField.setWidth(fieldWidth);
        stateSelectField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String choice = (String)stateSelectField.getSelectedItem();
                    if (choice.equals("installed") && showTree == false) {
                        // display tree
                        userColumn.remove(locationColumn);
                        userColumn.add(treeColumn);
                        showTree = true;
                        //setHeight(treeHeight);
                        setHeight(new Extent(getHeight().getValue()+treeHeightOffset));

                    } else if (!choice.equals("installed") && showTree == true) {
                        // display location text field
                        selectedComponent = null;
                        userColumn.remove(treeColumn);
                        userColumn.add(locationColumn);
                        showTree = false;
                        //setHeight(locationHeight);
                        setHeight(new Extent(getHeight().getValue()-treeHeightOffset));
                    }
                }
            });

        ListModel stateListModel = new StateListModel(installed);
        stateSelectField.setModel(stateListModel);
        if (installed)
            stateSelectField.setSelectedIndex(2);  // preselect "installed"
        else
            stateSelectField.setSelectedIndex(0);  // preselect first choice

        userColumn.add(stateSelectField);

        // prompt for comment
        Label commentLabel = new Label("Enter any comment:");
        userColumn.add(commentLabel);
        commentTextField = new TextField();
        commentTextField.setWidth(fieldWidth);
        userColumn.add(commentTextField);

        // create tree column for showing housing tree
        treeColumn = new Column();
        ContainerEx treeContainer = new ContainerEx();
        treeContainer.setBackground(Color.WHITE);
        treeContainer.setHeight(new Extent(250));
        treeContainer.setScrollBarPolicy(Scrollable.ALWAYS);
        Label treeLocationLabel = new Label("Pick new location from housing tree:");
        treeColumn.add(treeLocationLabel);
        treeErrorLabel = new Label("");
        treeErrorLabel.setForeground(Color.RED);
        treeColumn.add(treeErrorLabel);
        housingTree = new Tree();
        housingTree.setStyleName("Default.Tree");
        treeSelectionModel = new DefaultTreeSelectionModel();
        treeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeSelectionModel.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    TreePath path = e.getNewLeadSelectionPath();
                    if (path != null) {
                        selectedComponent = (Component)path.getLastPathComponent();
                        hasInstance = 
                            controller.actionComponentHasInstance(selectedComponent);
                        if (hasInstance) {
                            //setHeight(treeErrorHeight);
                            setHeight(new Extent(getHeight().getValue()+treeErrorHeightOffset));
                            treeErrorLabel.setText("This component slot already occupied.");
                        } else {
                            //setHeight(treeHeight);
                            setHeight(new Extent(getHeight().getValue()-treeErrorHeightOffset));
                            treeErrorLabel.setText(null);
                        }
                    } else {
                        selectedComponent = null;
                    }
                }
            });
        housingTree.setSelectionModel(treeSelectionModel);
        ComponentTreeCellRenderer treeCellRenderer = new ComponentTreeCellRenderer(ci);
        treeCellRenderer.setStyleName("Default.ComponentTreeCellRenderer");
        housingTree.setCellRenderer(treeCellRenderer);
        housingTree.setRootVisible(true);
        
        housingTreeModel = new FilteredHousingComponentTreeModel();
        housingTreeModel.setRoot(model.getSiteComponent());
        housingTree.setModel(housingTreeModel);
        TreeModelEvent he = 
            new TreeModelEvent(this, new Object[] {model.getSiteComponent()});
        housingTreeModel.fireTreeStructureChanged(he);            
        treeContainer.add(housingTree);
        treeColumn.add(treeContainer);
        
        // create location column for showing location text field
        locationColumn = new Column();
        Label locationLabel = new Label("Enter location as a string:");
        locationColumn.add(locationLabel);
        locationTextField = new TextField();
        locationTextField.setWidth(fieldWidth);
        locationColumn.add(locationTextField);
        
        // figure out whether to display tree or location text field
        if (installed) {
            showTree = true;
            setHeight(treeHeight);
            userColumn.add(treeColumn);
            
        } else {  // else, show location text field (pre-populated)
            showTree = false;
            setHeight(locationHeight);
            userColumn.add(locationColumn);
        }


    }

    public boolean okAction(ActionEvent e) {
        ComponentInstance ci = model.getSelectedComponentInstance();

        // do nothing if user didn't select a housing component
        if (showTree && selectedComponent == null)
            return false;

        // do nothing if housing component selected and types don't match
        if (selectedComponent != null && 
            (!ci.getComponentType().equals(selectedComponent.getComponentType())))
            return false;
        
        // show error if selection already has a component instance defined
        if (selectedComponent != null && hasInstance) {
            setHeight(new Extent(getHeight().getValue()+treeErrorHeightOffset));
            treeErrorLabel.setText("This component slot already occupied.");
            return false;
        }

        String stateChoice = (String)stateSelectField.getSelectedItem();
        String comment = commentTextField.getText();
        String locationString = locationTextField.getText();
        controller.actionNewLocationHistory(eventDate, stateChoice, comment, locationString, selectedComponent);
        return true;
    }

    public void cancelAction(ActionEvent e) {
        // do nothing
    }

    public class StateListModel extends AbstractListModel {

        private List stateList;

        public StateListModel(boolean installed) {
            stateList = model.getLocationStateList();
        }

        public int size() {
            return stateList.size();
        }
        
        public Object get(int index) {
            ComponentState cs = (ComponentState)stateList.get(index);
            return cs.getState();
        }
        
    }

    public class ComponentTreeCellRenderer extends DefaultTreeCellRenderer {

        private Color darkGreen = new Color(0, 186, 0);
        private ComponentInstance ci;

        public ComponentTreeCellRenderer(ComponentInstance ci) {
            super();
            this.ci = ci;
        }

        public Label getTreeCellRendererText(Tree tree,
                                             Object node,
                                             boolean selected,
                                             boolean expanded,
                                             boolean leaf) {

            gov.anl.aps.irmis.persistence.component.Component c = 
                (gov.anl.aps.irmis.persistence.component.Component)node;
            boolean sameType = (c.getComponentType().equals(ci.getComponentType()));
            //boolean openSlot = (model.getOpenComponentSlots().contains(c));
            
            String text = c.toString(ComponentRelationshipType.HOUSING);
            Label label = super.getTreeCellRendererText(tree, node, selected, expanded, leaf);
            label.setText(text);
            label.setIcon(null);
            // adjust color based on whether component type matches with ci's type
            //   and the component slot is open (ie. does not have associated ci)
            if (sameType /*&& openSlot*/) 
                label.setForeground(darkGreen);
            else
                label.setForeground(Color.LIGHTGRAY);

            // adjust selection highlighting depending on whether component type matches
            if (selected) {
                if (sameType /*&& openSlot*/)
                    label.setBackground(Color.LIGHTGRAY);
                else
                    label.setBackground(Color.WHITE);

            } else {
                label.setBackground(Color.WHITE);
            }
            return label;

        }

    }
    
}