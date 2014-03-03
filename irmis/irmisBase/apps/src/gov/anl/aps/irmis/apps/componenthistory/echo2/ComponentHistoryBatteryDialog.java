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
 * IRMIS component history changed battery dialox box (battery category).
 */
public class ComponentHistoryBatteryDialog extends DialogBox {

    static final String title = "Changed Component Battery";
    static final int width = 300;
    static final int height = 354 + 50;
    static final Extent heightExtent = new Extent(height);
    static final int eventDateHeight = height + 185;
    static final Extent eventDateHeightExtent = new Extent(eventDateHeight);
    static final Extent fieldWidth = new Extent(250);

    private ComponentHistoryController controller;
    private ComponentHistoryModel model;

    private TextField commentTextField;
    private DateChooser nextChangeDateChooser;
    private final DateFormat sdf = new SimpleDateFormat("MM/dd/yy");        
    private DateChooser eventDateChooser;
    private Date eventDate;
    private Label eventDateLabel;
    private CheckBox naCheckBox;

    public ComponentHistoryBatteryDialog(ComponentHistoryController controller) {
        super(title, width, height);

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
                        setHeight(eventDateHeightExtent);
                    } else {
                        getUserColumn().remove(2);
                        setHeight(heightExtent);
                    }
                }
            });
        eventDateRow.add(selectEventDateCheckBox);
        userColumn.add(eventDateRow);

        // prompt for description of work (comment)
        Label batteryWorkLabel = new Label("Description of work:");
        userColumn.add(batteryWorkLabel);
        commentTextField = new TextField();
        commentTextField.setWidth(fieldWidth);
        userColumn.add(commentTextField);

        // prompt for next change date
        Label nextChangeLabel = new Label("Select next required replacement date:");
        userColumn.add(nextChangeLabel);
        naCheckBox = new CheckBox("not applicable");
        userColumn.add(naCheckBox);
        nextChangeDateChooser = new DateChooser();
        nextChangeDateChooser.setYearSelectable(true);
        userColumn.add(nextChangeDateChooser);

    }

    public boolean okAction(ActionEvent e) {

        String comment = commentTextField.getText();
        Calendar cal = nextChangeDateChooser.getSelectedDate();
        Date nextChangeDate;
        if (naCheckBox.isSelected())
            nextChangeDate = null;
        else
            nextChangeDate = cal.getTime();

        controller.actionNewBatteryHistory(eventDate, comment, nextChangeDate);
        return true;
    }

    public void cancelAction(ActionEvent e) {

    }
    
}