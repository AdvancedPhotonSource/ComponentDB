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
import nextapp.echo2.app.CheckBox;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.RadioButton;
import nextapp.echo2.app.button.ButtonGroup;
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
 * IRMIS component history failure dialox box (operational state).
 */
public class ComponentHistoryFailureDialog extends DialogBox {

    static final String title = "Component Failed/Questionable";
    static final int width = 300;
    static final Extent fullHeight = new Extent(375 + 50);
    //static final Extent shortHeight = new Extent(280 + 50);
    static final int shortHeightOffset = 95;
    static final int eventDateHeightOffset = 185;
    static final Extent fieldWidth = new Extent(250);

    private Component originalInstalledComponent;
    
    private ComponentHistoryController controller;
    private ComponentHistoryModel model;

    private SelectField stateSelectField;
    private Label stateSelectLabel;
    private TextField commentTextField;
    private TextField ctllogTextField;
    private TextField locationTextField;
    private Label locationLabel;
    private RadioButton failedRadioButton;
    private RadioButton questionableRadioButton;
    private RadioButton yesRadioButton;
    private RadioButton noRadioButton;
    private final DateFormat sdf = new SimpleDateFormat("MM/dd/yy");        
    private DateChooser eventDateChooser;
    private Date eventDate;
    private Label eventDateLabel;

    public ComponentHistoryFailureDialog(ComponentHistoryController controller) {
        super(title, width, fullHeight.getValue());

        this.controller = controller;
        model = controller.getModel();

        final Column userColumn = getUserColumn();
        userColumn.setCellSpacing(new Extent(5));
        userColumn.setInsets(new Insets(5));

        ComponentInstance ci = model.getSelectedComponentInstance();
        Component c = ci.getComponent();

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
                        setHeight(new Extent(getHeight().getValue()+eventDateHeightOffset));
                    } else {
                        getUserColumn().remove(2);
                        setHeight(new Extent(getHeight().getValue()-eventDateHeightOffset));
                    }
                }
            });
        eventDateRow.add(selectEventDateCheckBox);
        userColumn.add(eventDateRow);

        // Component Failed, or just questionable.
        Label stateLabel = new Label("Is component failed, or questionable?");
        userColumn.add(stateLabel);
        Row failedQuestionableRow = new Row();
        failedQuestionableRow.setStyleName("RadioButton.Row");
        failedRadioButton = new RadioButton("Failed");
        questionableRadioButton = new RadioButton("Questionable");
        ButtonGroup fqButtonGroup = new ButtonGroup();
        failedRadioButton.setGroup(fqButtonGroup);
        questionableRadioButton.setGroup(fqButtonGroup);
        failedRadioButton.setSelected(true);
        failedQuestionableRow.add(failedRadioButton);
        failedQuestionableRow.add(questionableRadioButton);
        userColumn.add(failedQuestionableRow);

        // prompt for description of failure (comment)
        Label failureDescLabel = new Label("Enter description of problem:");
        userColumn.add(failureDescLabel);
        commentTextField = new TextField();
        commentTextField.setWidth(fieldWidth);
        userColumn.add(commentTextField);

        // prompt for CTLLOG number
        Label ctllogLabel = new Label("Enter CTLLOG # (if any):");
        userColumn.add(ctllogLabel);
        ctllogTextField = new TextField();
        ctllogTextField.setWidth(fieldWidth);
        userColumn.add(ctllogTextField);

        // Are you relocating the component?
        Label relocateLabel = new Label("Are you relocating the component?");
        userColumn.add(relocateLabel);
        Row relocateRow = new Row();
        relocateRow.setStyleName("RadioButton.Row");
        yesRadioButton = new RadioButton("Yes");
        yesRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    userColumn.add(stateSelectLabel);
                    userColumn.add(stateSelectField);
                    userColumn.add(locationLabel);
                    userColumn.add(locationTextField);
                    setHeight(new Extent(getHeight().getValue()+shortHeightOffset));
                }
            });
        noRadioButton = new RadioButton("No");
        noRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    userColumn.remove(stateSelectLabel);
                    userColumn.remove(stateSelectField);
                    userColumn.remove(locationLabel);
                    userColumn.remove(locationTextField);
                    setHeight(new Extent(getHeight().getValue()-shortHeightOffset));
                }
            });
        ButtonGroup ynButtonGroup = new ButtonGroup();
        yesRadioButton.setGroup(ynButtonGroup);
        noRadioButton.setGroup(ynButtonGroup);
        yesRadioButton.setSelected(true);
        relocateRow.add(yesRadioButton);
        relocateRow.add(noRadioButton);
        userColumn.add(relocateRow);

        // prompt for new location state (if not installed)
        stateSelectLabel = new Label("Pick new location state:");
        userColumn.add(stateSelectLabel);
        stateSelectField = new SelectField();
        stateSelectField.setWidth(fieldWidth);
        ListModel stateListModel = new StateListModel(false);
        stateSelectField.setModel(stateListModel);
        stateSelectField.setSelectedIndex(0);
        userColumn.add(stateSelectField);

        locationLabel = new Label("Enter new location as a string:");
        userColumn.add(locationLabel);
        locationTextField = new TextField();
        locationTextField.setWidth(fieldWidth);
        userColumn.add(locationTextField);
        

    }

    public boolean okAction(ActionEvent e) {

        boolean failed = failedRadioButton.isSelected();
        String stateChoice = (String)stateSelectField.getSelectedItem();
        String comment = commentTextField.getText();
        boolean relocatingComponent = yesRadioButton.isSelected();
        String locationString = locationTextField.getText();
        String ctllogNum = ctllogTextField.getText();

        controller.actionNewFailureHistory(eventDate, failed, comment, ctllogNum, relocatingComponent, 
                                           stateChoice, locationString);
        return true;
    }

    public void cancelAction(ActionEvent e) {
    }

    public class StateListModel extends AbstractListModel {

        private List stateList;

        public StateListModel(boolean installed) {
            
            // build up modified state list
            List fullStateList = model.getLocationStateList();
            stateList = new ArrayList();
            Iterator it = fullStateList.iterator();
            while (it.hasNext()) {
                ComponentState cs = (ComponentState)it.next();
                if (installed) {
                    if (cs.getState().equals("installed")) {
                        stateList.add(cs);
                        break;
                    }
                } else { 
                    if (!(cs.getState().equals("installed"))) {
                        stateList.add(cs);
                    }                    
                }
            }
        }

        public int size() {
            return stateList.size();
        }
        
        public Object get(int index) {
            ComponentState cs = (ComponentState)stateList.get(index);
            return cs.getState();
        }
        
    }
    
}