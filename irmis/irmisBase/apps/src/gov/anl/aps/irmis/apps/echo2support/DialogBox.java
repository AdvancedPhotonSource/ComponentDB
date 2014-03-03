/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.echo2support;

// Echo2 
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.SplitPane;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Button;
import nextapp.echo2.app.WindowPane;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Alignment;

import nextapp.echo2.app.button.*;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.layout.*;


/**
 * Base abstract class for a general application modal dialog box. This should be
 * subclassed, with the subclass providing the okAction and cancelAction methods.
 * The subclass can fill the dialog box with whatever by calling getUserColumn
 * and then adding any gui components to that. To detect the closure of the
 * dialog box, you add a WindowPaneListener to your dialog box object.
 */
public abstract class DialogBox extends WindowPane {

    private SplitPane mainSplitPane;
    private Column userColumn;
    private Row userButtonRow;

    /**
     * Constructor builds basic dialog box with ok and cancel buttons and no interior.
     * Use getUserColumn and add your custom dialog box content to that.
     */
    public DialogBox(String title, int initialWidth, int initialHeight) {
        super(title, new Extent(initialWidth), new Extent(initialHeight));
        setStyleName("Default.Dialog");
        setClosable(false);
        setModal(true);
        setMovable(true);
        setResizable(false);

        // create main split pane (for button row and user column);
        mainSplitPane = new SplitPane(SplitPane.ORIENTATION_VERTICAL_BOTTOM_TOP, new Extent(37));
        mainSplitPane.setStyleName("Default");
        add(mainSplitPane);

        // create user content column
        userColumn = new Column();

        // create user button row
        userButtonRow = new Row();
        userButtonRow.setStyleName("Button.Row");

        Button okButton = new Button("OK");
        okButton.setStyleName("Button.Dialog");
        okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    boolean closeDialog = okAction(e);
                    if (closeDialog)
                        userClose();
                }
            });
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyleName("Button.Dialog");
        cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cancelAction(e);
                    userClose();
                }
            });
        
        userButtonRow.add(okButton);
        userButtonRow.add(cancelButton);
        mainSplitPane.add(userButtonRow);
        mainSplitPane.add(userColumn);
        
    }
    
    /**
     * This method returns an Echo2 Column component into which you may add
     * your dialog box content.
     */
    protected Column getUserColumn() {
        return userColumn;
    }

    /**
     * Invoked when ok button pressed in dialog box. The window pane closing
     * event will follow after this if you return true. You can return false
     * if you want the dialog box to remain up (ie. to display an error message).
     * You must implement this method in your actual dialog box subclass.
     *
     * @return true if dialog should close, false if dialog should stay open
     */
    protected abstract boolean okAction(ActionEvent e);

    /**
     * Invoked when cancel button pressed in dialog box. The window pane closing
     * event will follow after this. You must implement this method in you
     * actual dialog box subclass.
     */
    protected abstract void cancelAction(ActionEvent e);
    
}