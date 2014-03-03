/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.login.echo2support;

// Echo2
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.TextField;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.PasswordField;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;

import gov.anl.aps.irmis.apps.echo2support.DialogBox;
import gov.anl.aps.irmis.login.SimpleCallbackHandler;

/**
 * Standard login dialog designed to return a JAAS-compliant CallbackHandler prepopulated
 * with the login credentials. This version is for use in Echo2 AJAX application.
 *
 * @see LoginUtil
 */
public class LoginDialog extends DialogBox {

    private SimpleCallbackHandler ch = null;
    private TextField usernameInput;
    private PasswordField passwordInput;
    private boolean usernameOnly;


    public LoginDialog(boolean usernameOnly, int width, int height) {
        super("IRMIS Login", width, height);

        this.usernameOnly = usernameOnly;

        // user fillable part of the dialog
        Column userColumn = getUserColumn();
        userColumn.setCellSpacing(new Extent(5));
        userColumn.setInsets(new Insets(7));

        usernameInput = new TextField();
        passwordInput = new PasswordField();
        passwordInput.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String username = usernameInput.getText();
                    String pw = passwordInput.getText();
                    if (username.length() > 0 && pw.length() > 0) {
                        ch = new SimpleCallbackHandler(username, pw.toCharArray());
                        userClose();
                    }
                }
            });

        Label uLabel = new Label("Username:");
        userColumn.add(uLabel);
        userColumn.add(usernameInput);

        if (!usernameOnly) {
            Label pLabel = new Label("Password:");
            userColumn.add(pLabel);
            userColumn.add(passwordInput);
        }
    }


    public boolean okAction(ActionEvent e) {
        if (usernameOnly)
            ch = new SimpleCallbackHandler(usernameInput.getText(), null);
        else
            ch = new SimpleCallbackHandler(usernameInput.getText(), 
                                           passwordInput.getText().toCharArray());
        return true;
    }


    public void cancelAction(ActionEvent e) {
        ch = null;
    }

    public SimpleCallbackHandler getCallbackHandler() {
        return ch;
    }

}
