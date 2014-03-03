/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.pv.cfw;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;


public class ResultsColumnsDialog extends JDialog
                        implements ActionListener {
    private static ResultsColumnsDialog dialog;
    private static ColumnChoices columnChoices = null;
    private static boolean saved = false;

    private JCheckBox recordTypeCheckbox;
    private JCheckBox iocCheckbox;
    private JCheckBox systemCheckbox;
    private JTextField fieldColumn1;
    private JTextField fieldColumn2;
    private JTextField fieldColumn3;

    /**
     * Puts up a pv results column choice dialog box, returning a possibly 
     * modified ColumnChoices object.
     *
     * @param frameComp component within whose frame this dialog will be rendered
     * @param locationComp component that login dialog should show up near (can be null)
     * @param columnChoices current set of columns chosen for display
     * @return true if choices were saved
     */
    public static boolean showDialog(Component frameComp,
                                 Component locationComp, ColumnChoices columnChoices) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        String dialogTitle = null;
        ResultsColumnsDialog.columnChoices = columnChoices;
        dialogTitle = "Adjust Display Columns";
        
        dialog = new ResultsColumnsDialog(frame, locationComp, dialogTitle);
        dialog.setVisible(true);
        return ResultsColumnsDialog.saved;
    }

    private ResultsColumnsDialog(Frame frame,
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

        JLabel instructionLabel = new JLabel("Choose columns to display:");

        ColumnChoices columnChoices = ResultsColumnsDialog.columnChoices;

        recordTypeCheckbox = new JCheckBox("record type");
        recordTypeCheckbox.setSelected(columnChoices.getRecordType());
        iocCheckbox = new JCheckBox("ioc");
        iocCheckbox.setSelected(columnChoices.getIoc());
        systemCheckbox = new JCheckBox("system");
        systemCheckbox.setSelected(columnChoices.getSystem());

        // panel for pv field entry
        JPanel pvFieldPanel = new JPanel(new BorderLayout());
        TitledBorder pvFieldPanelTitle = BorderFactory.createTitledBorder("PV Fields");
        pvFieldPanel.setBorder(pvFieldPanelTitle);
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.PAGE_AXIS));
        pvFieldPanel.add(checkboxPanel);
        
        fieldColumn1 = new JTextField();
        fieldColumn1.setText(columnChoices.getField1());
        fieldColumn2 = new JTextField();
        fieldColumn2.setText(columnChoices.getField2());
        fieldColumn3 = new JTextField();
        fieldColumn3.setText(columnChoices.getField3());
        
        checkboxPanel.add(fieldColumn1);
        checkboxPanel.add(fieldColumn2);
        checkboxPanel.add(fieldColumn3);

        // build main panel for dialog
        JPanel textInputPane = new JPanel();
        textInputPane.setLayout(new BoxLayout(textInputPane, BoxLayout.PAGE_AXIS));
        textInputPane.add(instructionLabel);
        textInputPane.add(recordTypeCheckbox);
        textInputPane.add(iocCheckbox);
        textInputPane.add(systemCheckbox);
        textInputPane.add(pvFieldPanel);
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
            // copy widget data to ResultsColumnsDialog.columnChoices object
            ColumnChoices choices = ResultsColumnsDialog.columnChoices;
            choices.setRecordType(recordTypeCheckbox.isSelected());
            choices.setIoc(iocCheckbox.isSelected());
            choices.setSystem(systemCheckbox.isSelected());
            choices.setField1(fieldColumn1.getText());
            choices.setField2(fieldColumn2.getText());
            choices.setField3(fieldColumn3.getText());
            ResultsColumnsDialog.saved = true;

        } else if ("Cancel".equals(e.getActionCommand())) {
            // indicate cancellation
            ResultsColumnsDialog.saved = false;
        }
        ResultsColumnsDialog.dialog.setVisible(false);
    }
}
