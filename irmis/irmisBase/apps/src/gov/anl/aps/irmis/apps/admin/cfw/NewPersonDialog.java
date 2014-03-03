/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.admin.cfw;

import gov.anl.aps.irmis.login.LoginUtil;
import gov.anl.aps.irmis.persistence.login.Person;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class NewPersonDialog extends JDialog
    implements ActionListener {
    private static NewPersonDialog dialog;
    private static Person person = null;
    JTextField firstNameTextField;
    JTextField middleNameTextField;
    JTextField lastNameTextField;
    JTextField useridTextField;
    
    /**
     * Puts up a "new" person dialog box, returning a new Person object.
     *
     * @param frameComp component within whose frame this dialog will be rendered
     * @param locationComp component that dialog should show up near (can be null)
     * @return new Person object, or null if dialog cancelled
     */
    public static Person showDialog(Component frameComp,
                                    Component locationComp) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        String dialogTitle = "New Person Dialog";
        
        dialog = new NewPersonDialog(frame, locationComp, dialogTitle);
        dialog.setVisible(true);
        return NewPersonDialog.person;
    }

    private NewPersonDialog(Frame frame,
                            Component locationComp, String title) {
        super(frame, title, true);

        //Create and initialize the buttons.
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);
        //
        final JButton saveButton = new JButton("Save");
        saveButton.setActionCommand("Save");
        saveButton.addActionListener(this);
        getRootPane().setDefaultButton(saveButton);

        //main part of the dialog

        // ioc name text field
        firstNameTextField = new JTextField(10);
        middleNameTextField = new JTextField(5);
        lastNameTextField = new JTextField(10);
        useridTextField = new JTextField(10);

        JPanel textInputPane = new JPanel();
        textInputPane.setLayout(new BoxLayout(textInputPane, BoxLayout.PAGE_AXIS));
        JLabel fnLabel = new JLabel("First Name:",SwingConstants.LEFT);
        textInputPane.add(fnLabel);
        textInputPane.add(firstNameTextField);

        JLabel mnLabel = new JLabel("Middle Name:",SwingConstants.LEFT);
        textInputPane.add(mnLabel);
        textInputPane.add(middleNameTextField);

        JLabel lnLabel = new JLabel("Last Name:",SwingConstants.LEFT);
        textInputPane.add(lnLabel);
        textInputPane.add(lastNameTextField);

        JLabel uiLabel = new JLabel("Unix Userid:",SwingConstants.LEFT);
        textInputPane.add(uiLabel);
        textInputPane.add(useridTextField);

        textInputPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(saveButton);

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
        if ("Save".equals(e.getActionCommand())) {
            // copy widget data to NewPersonDialog.person object
            Person person = new Person();
            NewPersonDialog.person = person;

            person.setFirstName(firstNameTextField.getText());
            person.setMiddleName(middleNameTextField.getText());
            person.setLastName(lastNameTextField.getText());
            person.setUserid(useridTextField.getText());

        } else if ("Cancel".equals(e.getActionCommand())) {
            // ditch any possible new Person object
            NewPersonDialog.person = null;
        }
        NewPersonDialog.dialog.setVisible(false);
    }
}
