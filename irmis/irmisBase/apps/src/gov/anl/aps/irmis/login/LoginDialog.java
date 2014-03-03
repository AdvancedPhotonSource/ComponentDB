/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Standard login dialog designed to return a JAAS-compliant CallbackHandler prepopulated
 * with the login credentials.
 *
 * @see LoginUtil
 */
public class LoginDialog extends JDialog
                        implements ActionListener {
    private static LoginDialog dialog;
    private static SimpleCallbackHandler ch = null;
    private JTextField usernameInput;
    private JPasswordField passwordInput;
    private boolean usernameOnly;

    /**
     * Puts up a login dialog box, and after user has input login credentials
     * we return an instance of CallbackHandler suitable for use with JAAS
     * LoginModule. We return null if user cancel's out of login dialog.
     *
     * @param frameComp component within whose frame this dialog will be rendered
     * @param locationComp component that login dialog should show up near (can be null)
     * @param usernameOnly if true, only ask for username in the dialog
     * @return SimpleCallbackHandler containing login credentials, or null
     */
    public static SimpleCallbackHandler showDialog(Component frameComp,
                                                   Component locationComp,
                                                   boolean usernameOnly) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new LoginDialog(frame, locationComp, usernameOnly);
        dialog.setVisible(true);
        return ch;
    }

    private LoginDialog(Frame frame,
                        Component locationComp,
                        boolean usernameOnly) {
        super(frame, "IRMIS Login", true);

        this.usernameOnly = usernameOnly;

        //Create and initialize the buttons.
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);
        //
        final JButton loginButton = new JButton("Login");
        loginButton.setActionCommand("Login");
        loginButton.addActionListener(this);
        getRootPane().setDefaultButton(loginButton);

        //main part of the dialog
        usernameInput = new JTextField();
        passwordInput = new JPasswordField();

        JPanel textInputPane = new JPanel();
        textInputPane.setLayout(new BoxLayout(textInputPane, BoxLayout.PAGE_AXIS));
        JLabel uLabel = new JLabel("Username:",SwingConstants.LEFT);
        textInputPane.add(uLabel);
        textInputPane.add(usernameInput);
        if (!usernameOnly) {
            JLabel pLabel = new JLabel("Password:",SwingConstants.LEFT);
            textInputPane.add(pLabel);
            textInputPane.add(passwordInput);
        }
        textInputPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(loginButton);

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(textInputPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        //Initialize values.
        pack();
        setLocationRelativeTo(locationComp);
    }

    //Handle clicks on the Set and Cancel buttons.
    public void actionPerformed(ActionEvent e) {
        if ("Login".equals(e.getActionCommand())) {
            if (this.usernameOnly)
                LoginDialog.ch = new SimpleCallbackHandler(usernameInput.getText(), null);
            else
                LoginDialog.ch = new SimpleCallbackHandler(usernameInput.getText(),
                                                           passwordInput.getPassword());

        } else if ("Cancel".equals(e.getActionCommand())) {
            LoginDialog.ch = null;
        }
        LoginDialog.dialog.setVisible(false);
    }
}
