/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/

package gov.anl.aps.irmis.apps.componenthistory.echo2;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

// Echo2 
import nextapp.echo2.app.*;
import nextapp.echo2.app.button.*;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import nextapp.echo2.app.layout.*;

// EchoPointNG

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
 * Dialog box for creating a new component instance.
 */
public class ComponentHistoryEditSerialDialog extends DialogBox {

    static final String title = "Edit Instance Serial Number";
    static final int width = 400;
    static final Extent height = new Extent(200);
    static final Extent fieldWidth = new Extent(250);

    private Column userColumn;

    private ComponentHistoryController controller;
    private ComponentHistoryModel model;
    private TextField snTextField;

    public ComponentHistoryEditSerialDialog(ComponentHistoryController ctrl) {
        super(title, width, height.getValue());

        controller = ctrl;
        model = controller.getModel();
        
        userColumn = getUserColumn();
        userColumn.setCellSpacing(new Extent(5));
        userColumn.setInsets(new Insets(5));

        // prompt for serial number
        Label snLabel = new Label("Serial Number / Identifier:");
        snTextField = new TextField();
        String currentSerialNumber = model.getSelectedComponentInstance().getSerialNumber();
        snTextField.setText(currentSerialNumber);
        snTextField.setWidth(fieldWidth);
        userColumn.add(snLabel);
        userColumn.add(snTextField);

        // display note about editing restrictions
        Label editDescLabel = new Label("Note: All other changes are made via the Component History tabs. Any more significant changes require removing the current instance and creating a new one.");
        Row editDescRow = new Row();
        editDescRow.setStyleName("RadioButton.Row");
        editDescRow.add(editDescLabel);
        userColumn.add(editDescRow);

    }


    public boolean okAction(ActionEvent e) {

        // use this field to save the serial number 
        model.setNewInstanceSerialNumber(snTextField.getText());

        return true;
    }

    public void cancelAction(ActionEvent e) {

        model.setNewInstanceSerialNumber(null);
    }


}