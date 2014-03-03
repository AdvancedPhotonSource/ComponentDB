/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/

package gov.anl.aps.irmis.apps.echo2support;

// Echo2 
import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.Button;
import nextapp.echo2.app.WindowPane;
import nextapp.echo2.app.SplitPane;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Label;

import nextapp.echo2.app.button.*;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.layout.*;

/**
 * General application modal message box. Construct with title and message.
 * To detect the closure of the message box, you add a WindowPaneListener 
 * to your message box object.
 */
public class MessageBox extends WindowPane {

    private SplitPane bottomSplitPane;
    private Column mainColumn;
    private Column userColumn;
    private Row userButtonRow;

    /**
     * Constructor builds basic message box with ok button and message for interior.
     */
    public MessageBox(String title, String message) {
        super();
        setTitle(title);
        setStyleName("Default.Dialog");
        setTitleBackground(Color.RED);
        setClosable(false);
        setModal(true);
        setMovable(true);
        setResizable(false);
        setHeight(new Extent(200));


        // create split pane to separate button row from content
        bottomSplitPane = new SplitPane(SplitPane.ORIENTATION_VERTICAL_BOTTOM_TOP, new Extent(37));

        // create user button row
        userButtonRow = new Row();
        userButtonRow.setStyleName("Button.Row");
        Button okButton = new Button("OK");
        okButton.setStyleName("Button.Dialog");
        RowLayoutData layout = new RowLayoutData();
        layout.setAlignment(Alignment.ALIGN_CENTER);
        okButton.setLayoutData(layout);
        okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    userClose();
                }
            });
        
        userButtonRow.add(okButton);

        // add button row to split pane
        bottomSplitPane.add(userButtonRow);

        // create main column with message
        mainColumn = new Column();
        mainColumn.setInsets(new Insets(4));
        add(mainColumn);
        Label messageLabel = new Label(message);
        messageLabel.setLineWrap(true);
        mainColumn.add(messageLabel);

        // add main column to split pane
        bottomSplitPane.add(mainColumn);

        add(bottomSplitPane);
    }
}