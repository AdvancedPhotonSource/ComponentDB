/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dialog to query user if they wish to delete component from current
 * tree, or delete component entirely. 
 */
public class DeleteDialog extends JDialog
                        implements ActionListener {
    private static DeleteDialog dialog;
    private static String typeName = null;

    JRadioButton treeOnlyButton;
    JRadioButton allButton;
    ButtonGroup buttonGroup;

    /**
     * Puts up a dialog box for confirming type of delete operation desired.
     *
     * @param frameComp component within whose frame this dialog will be rendered
     * @param locationComp component that delete dialog should show up near (can be null)
     * @return type of delete desired as a string
     */
    public static String showDialog(Component frameComp,
                                    Component locationComp,
                                    String dialogTitle) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        
        dialog = new DeleteDialog(frame, locationComp, dialogTitle);
        dialog.setVisible(true);
        return DeleteDialog.typeName;
    }

    private DeleteDialog(Frame frame,
                        Component locationComp, String title) {
        super(frame, title, true);

        //Create and initialize the buttons.
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);
        //
        final JButton deleteButton = new JButton("Delete");
        deleteButton.setActionCommand("Delete");
        deleteButton.addActionListener(this);
        getRootPane().setDefaultButton(deleteButton);

        //main part of the dialog

        // type name text field
        treeOnlyButton = new JRadioButton("delete from this tree only");
        allButton = new JRadioButton("delete completely");
        buttonGroup = new ButtonGroup();
        buttonGroup.add(treeOnlyButton);
        buttonGroup.add(allButton);
        treeOnlyButton.setSelected(true);

        JPanel radioButtonPane = new JPanel();
        radioButtonPane.setLayout(new BoxLayout(radioButtonPane, BoxLayout.PAGE_AXIS));
        JLabel descLabel = new JLabel("Delete Component:",SwingConstants.LEFT);
        radioButtonPane.add(descLabel);
        radioButtonPane.add(treeOnlyButton);
        radioButtonPane.add(allButton);        
        radioButtonPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(deleteButton);

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(radioButtonPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        //Initialize values.
        pack();
        setLocationRelativeTo(locationComp);
    }

    //Handle clicks on the Set and Cancel buttons.
    public void actionPerformed(ActionEvent e) {
        if ("Delete".equals(e.getActionCommand())) {
            // copy widget data to DeleteDialog.typeName object
            // check radio buttons, and set typeName appropriately
            if (treeOnlyButton.isSelected())
                DeleteDialog.typeName = "treeOnly";
            else if (allButton.isSelected())
                DeleteDialog.typeName = "all";
            else
                DeleteDialog.typeName = null;

        } else if ("Cancel".equals(e.getActionCommand())) {
            // ditch any possible new string
            DeleteDialog.typeName = null;
        }
        DeleteDialog.dialog.setVisible(false);
    }
}
