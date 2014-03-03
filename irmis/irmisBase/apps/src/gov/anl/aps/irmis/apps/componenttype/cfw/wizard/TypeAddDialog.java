/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenttype.cfw.wizard;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class TypeAddDialog extends JDialog
                        implements ActionListener {
    private static TypeAddDialog dialog;
    private static String typeName = null;
    JTextField typeNameTextField;

    /**
     * Puts up a dialog box for entering a single new type value.
     *
     * @param frameComp component within whose frame this dialog will be rendered
     * @param locationComp component that login dialog should show up near (can be null)
     * @return string entered by user in dialog
     */
    public static String showDialog(Component frameComp,
                                 Component locationComp,
                                 String dialogTitle) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        
        dialog = new TypeAddDialog(frame, locationComp, dialogTitle);
        dialog.setVisible(true);
        return TypeAddDialog.typeName;
    }

    private TypeAddDialog(Frame frame,
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

        // type name text field
        typeNameTextField = new JTextField(20);

        JPanel textInputPane = new JPanel();
        textInputPane.setLayout(new BoxLayout(textInputPane, BoxLayout.PAGE_AXIS));
        JLabel uLabel = new JLabel("Name:",SwingConstants.LEFT);
        textInputPane.add(uLabel);
        textInputPane.add(typeNameTextField);
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
            // copy widget data to TypeAddDialog.typeName object
            TypeAddDialog.typeName = typeNameTextField.getText();

        } else if ("Cancel".equals(e.getActionCommand())) {
            // ditch any possible new string
            TypeAddDialog.typeName = null;
        }
        TypeAddDialog.dialog.setVisible(false);
    }
}
