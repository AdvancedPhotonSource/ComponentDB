/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/

package gov.anl.aps.irmis.apps.componenthistory.echo2;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Calendar;
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
 * IRMIS component history NRTL dialox box (nrtl state).
 */
public class ComponentHistoryNRTLDialog extends DialogBox {

    static final String title = "Component NRTL Status";
    static final int width = 300;
    static final Extent height = new Extent(185 + 50);
    static final int agencyHeightOffset = 43;
    static final int eventDateHeightOffset = 185;
    static final Extent fieldWidth = new Extent(250);

    private ComponentHistoryController controller;
    private ComponentHistoryModel model;

    private Column agencyColumn;
    private boolean showAgency;
    private TextField commentTextField;
    private SelectField stateSelectField;
    private TextField agencyTextField;
    private final DateFormat sdf = new SimpleDateFormat("MM/dd/yy");        
    private DateChooser eventDateChooser;
    private Date eventDate;
    private Label eventDateLabel;

    public ComponentHistoryNRTLDialog(ComponentHistoryController controller) {
        super(title, width, height.getValue());

        this.controller = controller;
        model = controller.getModel();

        Column userColumn = getUserColumn();
        userColumn.setCellSpacing(new Extent(5));
        userColumn.setInsets(new Insets(5));

        ComponentInstance ci = model.getSelectedComponentInstance();

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

        // prompt for new location state (if not installed)
        Label stateSelectLabel = new Label("Pick new NRTL state:");
        userColumn.add(stateSelectLabel);

        stateSelectField = new SelectField();
        stateSelectField.setWidth(fieldWidth);
        stateSelectField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String choice = (String)stateSelectField.getSelectedItem();
                    if (choice.equals("NRTL approved") && showAgency == false) {
                        // display agency text field
                        getUserColumn().add(agencyColumn);
                        showAgency = true;
                        setHeight(new Extent(getHeight().getValue()+agencyHeightOffset));

                    } else if (!choice.equals("NRTL approved") && showAgency == true) {
                        // display location text field
                        getUserColumn().remove(agencyColumn);
                        showAgency = false;
                        setHeight(new Extent(getHeight().getValue()-agencyHeightOffset));
                    }
                }
            });

        ListModel stateListModel = new StateListModel();
        stateSelectField.setModel(stateListModel);
        stateSelectField.setSelectedIndex(0);  // preselect first choice
        userColumn.add(stateSelectField);

        // prompt for description of nrtl status change (comment)
        Label nrtlDescLabel = new Label("Comment on NRTL status change:");
        userColumn.add(nrtlDescLabel);
        commentTextField = new TextField();
        commentTextField.setWidth(fieldWidth);
        userColumn.add(commentTextField);

        // create agency column for showing agency text field
        agencyColumn = new Column();
        Label agencyLabel = new Label("Enter NRTL agency:");
        agencyColumn.add(agencyLabel);
        agencyTextField = new TextField();
        agencyTextField.setWidth(fieldWidth);
        agencyColumn.add(agencyTextField);

    }

    public boolean okAction(ActionEvent e) {

        String stateChoice = (String)stateSelectField.getSelectedItem();        
        String comment = commentTextField.getText();
        String agency = agencyTextField.getText();

        controller.actionNewNRTLHistory(eventDate, stateChoice, comment, agency);
        return true;
    }

    public void cancelAction(ActionEvent e) {

    }

    public class StateListModel extends AbstractListModel {

        private List stateList;

        public StateListModel() {
            stateList = model.getNRTLStateList();
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