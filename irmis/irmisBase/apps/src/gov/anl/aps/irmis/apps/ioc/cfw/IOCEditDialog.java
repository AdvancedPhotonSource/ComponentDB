/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.ioc.cfw;

import gov.anl.aps.irmis.login.LoginUtil;
import gov.anl.aps.irmis.persistence.pv.IOC;
import gov.anl.aps.irmis.persistence.pv.IOCStatus;

import java.util.List;
import java.util.Iterator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class IOCEditDialog extends JDialog
                        implements ActionListener {
    private static IOCEditDialog dialog;
    private static IOC ioc = null;
    private static List statusList;
    JTextField iocNameTextField;
    JTextField iocSystemTextField;
    JComboBox activeComboBox;
    JComboBox statusComboBox;

    /**
     * Puts up a ioc edit dialog box, returning a possibly modified IOC object.
     *
     * @param frameComp component within whose frame this dialog will be rendered
     * @param locationComp component that login dialog should show up near (can be null)
     * @param ioc ioc object that is the subject of editing
     * @param iocStatusList list of possible <code>IOCStatus</code> for an ioc
     * @return possibly modified IOC object
     */
    public static IOC showDialog(Component frameComp,
                                 Component locationComp, IOC ioc,
                                 List iocStatusList, boolean newIoc) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        String dialogTitle = null;
        IOCEditDialog.statusList = iocStatusList;
        if (newIoc) {
            IOCEditDialog.ioc = new IOC();
            dialogTitle = "Add New IOC Dialog";
        } else {
            IOCEditDialog.ioc = ioc;
            dialogTitle = "Edit IOC Dialog";
        }
        
        dialog = new IOCEditDialog(frame, locationComp, dialogTitle);
        dialog.setVisible(true);
        return IOCEditDialog.ioc;
    }

    private IOCEditDialog(Frame frame,
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
        IOC ioc = IOCEditDialog.ioc;
        iocNameTextField = new JTextField();
        iocNameTextField.setText(ioc.getIocName());

        // ioc system text field
        iocSystemTextField = new JTextField();
        iocSystemTextField.setText(ioc.getSystem());

        // ioc active/inactive combo box
        activeComboBox = new JComboBox(new String[] {"Active","Inactive"});
        if (ioc.getActive())
            activeComboBox.setSelectedIndex(0);
        else
            activeComboBox.setSelectedIndex(1);

        // ioc status combo box (will replace active/inactive box above over time)
        statusComboBox = new JComboBox();
        Iterator statusIt = statusList.iterator();
        while (statusIt.hasNext()) {
            IOCStatus st = (IOCStatus)statusIt.next();
            statusComboBox.addItem(st);
        }
        if (ioc.getStatus() != null)
            statusComboBox.setSelectedItem(ioc.getStatus());
        else
            statusComboBox.setSelectedIndex(1);

        JPanel textInputPane = new JPanel();
        textInputPane.setLayout(new BoxLayout(textInputPane, BoxLayout.PAGE_AXIS));
        JLabel uLabel = new JLabel("IOC Name:",SwingConstants.LEFT);
        textInputPane.add(uLabel);
        textInputPane.add(iocNameTextField);
        JLabel sLabel = new JLabel("System:",SwingConstants.LEFT);
        textInputPane.add(sLabel);
        textInputPane.add(iocSystemTextField);
        JLabel aLabel = new JLabel("In Use Status:",SwingConstants.LEFT);
        textInputPane.add(aLabel);
        textInputPane.add(activeComboBox);
        textInputPane.add(statusComboBox);
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
            // copy widget data to IOCEditDialog.ioc object
            IOC ioc = IOCEditDialog.ioc;
            ioc.setModifiedBy(LoginUtil.getUsername());
            ioc.setIocName(iocNameTextField.getText());
            ioc.setSystem(iocSystemTextField.getText());
            String choice = (String)activeComboBox.getSelectedItem();
            if (choice.equals("Active"))
                ioc.setActive(true);
            else
                ioc.setActive(false);
            ioc.setStatus((IOCStatus)statusComboBox.getSelectedItem());

        } else if ("Cancel".equals(e.getActionCommand())) {
            // ditch any possible new IOC object
            IOCEditDialog.ioc = null;
        }
        IOCEditDialog.dialog.setVisible(false);
    }
}
