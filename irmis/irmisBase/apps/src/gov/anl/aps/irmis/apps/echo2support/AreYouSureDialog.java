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
 * General application "are you sure? yes/no" dialog. 
 * The yes/no choice is available via isYesResponse() method.
 */
public class AreYouSureDialog extends WindowPane {

    private SplitPane bottomSplitPane;
    private Column mainColumn;
    private Column userColumn;
    private Row userButtonRow;
    private boolean yesResponse = false;

    /**
     * Constructor builds basic "are you sure?" dialog.
     */
    public AreYouSureDialog() {
        super();
        setTitle("Confirmation Dialog");
        setStyleName("Default.Dialog");
        setTitleBackground(Color.RED);
        setClosable(false);
        setModal(true);
        setMovable(true);
        setResizable(false);
        setWidth(new Extent(160));
        setHeight(new Extent(110));


        // create split pane to separate button row from content
        bottomSplitPane = new SplitPane(SplitPane.ORIENTATION_VERTICAL_BOTTOM_TOP, new Extent(37));

        // create user button row
        userButtonRow = new Row();
        userButtonRow.setStyleName("Button.Row");
        Button yesButton = new Button("Yes");
        yesButton.setStyleName("Button.Dialog");
        Button noButton = new Button("No");
        noButton.setStyleName("Button.Dialog");

        yesButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    yesResponse = true;
                    userClose();
                }
            });

        noButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    yesResponse = false;
                    userClose();
                }
            });
        
        userButtonRow.add(yesButton);
        userButtonRow.add(noButton);

        // add button row to split pane
        bottomSplitPane.add(userButtonRow);

        // create main column with message
        mainColumn = new Column();
        mainColumn.setInsets(new Insets(4));
        add(mainColumn);
        Label messageLabel = new Label("Are you sure?");
        messageLabel.setLineWrap(false);
        mainColumn.add(messageLabel);

        // add main column to split pane
        bottomSplitPane.add(mainColumn);

        add(bottomSplitPane);
    }

    public boolean isYesResponse() {
        return yesResponse;
    }

}