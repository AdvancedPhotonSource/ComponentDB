/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw;

import gov.anl.aps.irmis.login.LoginUtil;
import gov.anl.aps.irmis.persistence.component.ComponentType;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;


public class ReplaceComponentDialog extends JDialog
    implements ActionListener {
    private static ReplaceComponentDialog dialog;
    private static ComponentType componentType = null;
    JTextField compTypeFilterTextField;
    private List componentTypes;
    private List filteredComponentTypes;
    private JTable typeList;

    /**
     * Puts up a replace component dialog box, returning the selected 
     * replacement <code>ComponentType</code>, or null if cancelled.
     *
     * @param frameComp component within whose frame this dialog will be rendered
     * @param locationComp component that dialog should show up near (can be null)
     * @param ctList list of possible component type replacements
     * @return selected <code>ComponentType</code> or null
     */
    public static ComponentType showDialog(Component frameComp,
                                           Component locationComp, List ctList) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        String dialogTitle = "Select Replacement Component Type Dialog";
        
        dialog = new ReplaceComponentDialog(frame, locationComp, dialogTitle, ctList);
        dialog.setVisible(true);
        return ReplaceComponentDialog.componentType;
    }

    private ReplaceComponentDialog(Frame frame,
                                   Component locationComp, String title,
                                   List ctList) {
        super(frame, title, true);

        componentTypes = ctList;
        filteredComponentTypes = ctList;

        //Create and initialize the buttons.
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(this);
        //
        final JButton saveButton = new JButton("Replace");
        saveButton.setActionCommand("Replace");
        saveButton.addActionListener(this);
        getRootPane().setDefaultButton(saveButton);

        JPanel textInputPane = new JPanel();
        textInputPane.setLayout(new BoxLayout(textInputPane, BoxLayout.PAGE_AXIS));

        // component type selection
        JLabel nameLabel = 
            new JLabel("Select replacement component type:",
                       SwingConstants.LEFT);
        JPanel nameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //nameLabelPanel.setBackground(Color.white);
        nameLabelPanel.add(nameLabel);
        textInputPane.add(nameLabelPanel);

        // comp type name filter
        JPanel compTypeFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel compTypeFilterLabel = 
            new JLabel("Find:",SwingConstants.LEFT);        
        compTypeFilterPanel.add(compTypeFilterLabel);
        compTypeFilterTextField = new JTextField(15);
        compTypeFilterTextField.setToolTipText("Enter a wildcard string to filter component type list.");
        compTypeFilterTextField.setPreferredSize(new Dimension(350,20));
        compTypeFilterTextField.setMaximumSize(new Dimension(350,20));
        compTypeFilterTextField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                        String filterText = compTypeFilterTextField.getText();
                        // uppercase it for case insensitive match
                        filterText = filterText.toUpperCase();                        
                        // change any * to reg-ex pattern
                        filterText = filterText.replaceAll("\\*",".*");
                        Pattern filterRegEx = Pattern.compile("^.*"+filterText+".*$");
                        Iterator compTypeIt = componentTypes.iterator();
                        filteredComponentTypes = new ArrayList();
                        while (compTypeIt.hasNext()) {
                            ComponentType ct = (ComponentType)compTypeIt.next();
                            Matcher matcher1 = 
                                filterRegEx.matcher(ct.getComponentTypeName().toUpperCase());
                            Matcher matcher2 = 
                                filterRegEx.matcher(ct.getDescription().toUpperCase());
                            if (matcher1.matches() || matcher2.matches())
                                filteredComponentTypes.add(ct);
                        }
                        ComponentTypeTableModel tableModel = 
                            (ComponentTypeTableModel)typeList.getModel();
                        // request that comp type table update itself
                        tableModel.fireTableDataChanged();
                }
            });
        compTypeFilterPanel.add(compTypeFilterTextField);
        textInputPane.add(compTypeFilterPanel);

        // component types table

        // scrollbar policy
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        typeList = new JTable(new ComponentTypeTableModel());
        typeList.setShowHorizontalLines(true);
        typeList.setRowSelectionAllowed(true);
        typeList.getColumnModel().getColumn(0).setMinWidth(40);
        typeList.getColumnModel().getColumn(0).setPreferredWidth(110);        
        typeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // handle row selection in component types table
        typeList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int row = lsm.getMinSelectionIndex();
                        ReplaceComponentDialog.componentType = 
                            (ComponentType)filteredComponentTypes.get(row);
                    }
                }
            });

    	JScrollPane typeListScroller = new JScrollPane(typeList, vsbp, hsbp);
        textInputPane.add(typeListScroller);        

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
        if ("Replace".equals(e.getActionCommand())) {
            // do nothing, we're all set

        } else if ("Cancel".equals(e.getActionCommand())) {
            // ditch any selected component type
            ReplaceComponentDialog.componentType = null;
        }
        ReplaceComponentDialog.dialog.setVisible(false);
    }

    /**
     * Model for display of component type table.
     */
    class ComponentTypeTableModel extends AbstractTableModel {
        private String[] columnNames = {"Type","Description"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            if (filteredComponentTypes != null)
                size = filteredComponentTypes.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {

            if (filteredComponentTypes != null) {
                ComponentType type = 
                    (ComponentType)filteredComponentTypes.get(row);
                if (col == 0) {
                    return type.getComponentTypeName();
                } else {
                    return type.getDescription();
                }
            } else {
                return " ";
            }

        }

        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }

    }
}
