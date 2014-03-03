/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.admin.cfw;

import java.util.List;
import java.util.Iterator;

// persistence layer
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.login.Role;
import gov.anl.aps.irmis.persistence.login.RoleName;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class NewRoleDialog extends JDialog
    implements ActionListener {
    private static NewRoleDialog dialog;
    private static Role role = null;
    private static List allRoleNames = null;
    JComboBox roleNameCb;
    JTextArea roleExplanation;
    
    /**
     * Puts up a add role dialog box, returning a new Role object.
     *
     * @param frameComp component within whose frame this dialog will be rendered
     * @param locationComp component that dialog should show up near (can be null)
     * @return new Role object, or null if dialog cancelled
     */
    public static Role showDialog(Component frameComp,
                                    Component locationComp,
                                    List allRoleNamesList) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        String dialogTitle = "Add Role Dialog";
        allRoleNames = allRoleNamesList;
        
        dialog = new NewRoleDialog(frame, locationComp, dialogTitle);
        dialog.setVisible(true);
        return NewRoleDialog.role;
    }

    private NewRoleDialog(Frame frame,
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

        // role name combo box
        roleNameCb = new JComboBox();
        Iterator roleNameIt = NewRoleDialog.allRoleNames.iterator();
        while (roleNameIt.hasNext()) {
            RoleName roleName = (RoleName)roleNameIt.next();
            roleNameCb.addItem(roleName);
        }

        JPanel textInputPane = new JPanel();
        textInputPane.setLayout(new BoxLayout(textInputPane, BoxLayout.PAGE_AXIS));
        JLabel roleNameLabel = new JLabel("Role Name:",SwingConstants.LEFT);
        textInputPane.add(roleNameLabel);
        textInputPane.add(roleNameCb);

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
            // copy widget data to NewRoleDialog.role object
            Role role = new Role();
            NewRoleDialog.role = role;

            role.setRoleName((RoleName)roleNameCb.getSelectedItem());

        } else if ("Cancel".equals(e.getActionCommand())) {
            // ditch any possible new Role object
            NewRoleDialog.role = null;
        }
        NewRoleDialog.dialog.setVisible(false);
    }
}
