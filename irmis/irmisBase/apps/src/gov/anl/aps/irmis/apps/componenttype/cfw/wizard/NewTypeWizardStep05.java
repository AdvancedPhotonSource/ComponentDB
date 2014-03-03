/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenttype.cfw.wizard;

import net.javaprog.ui.wizard.AbstractStep;
import net.javaprog.ui.wizard.Step;
import net.javaprog.ui.wizard.StepModelCustomizer;

import java.util.Iterator;
import java.util.EventObject;
import java.util.List;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentPortTemplate;

/**
 * Step05 begins the process of adding potentially several physical ports
 * and their underlying pins. The current set is summarized here, with radio
 * buttons to request addition, removal, and indicate completion.
 */
public class NewTypeWizardStep05 extends AbstractStep 
    implements StepModelCustomizer,ActionListener {

    private NewTypeWizardModel dataModel;
    private JPanel stepComponent;
    private JTable portList;
    private JRadioButton removeButton;
    private JRadioButton addButton;
    private JRadioButton editPinsButton;
    private JRadioButton doneButton;

    public NewTypeWizardStep05(NewTypeWizardModel dataModel) {
        super("Physical Interfaces","desc");
        this.dataModel = dataModel;
    }

    protected JComponent createComponent() {
        // scrollbar policy
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        stepComponent = new JPanel();
        stepComponent.setLayout(new BoxLayout(stepComponent,BoxLayout.PAGE_AXIS));
        stepComponent.setBackground(Color.white);

        // component type name
        JLabel nameLabel = 
            new JLabel(dataModel.getComponentType().getComponentTypeName(),
                       SwingConstants.LEFT);
        JPanel nameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameLabelPanel.setBackground(Color.white);
        nameLabelPanel.add(nameLabel);
        stepComponent.add(nameLabelPanel);

        // variable number of ports
        JLabel portLabel = 
            new JLabel("Physical Ports:",SwingConstants.LEFT);
        JPanel portLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portLabelPanel.setBackground(Color.white);
        portLabelPanel.add(portLabel);
        stepComponent.add(portLabelPanel);

        // ports table
        portList = new JTable(new PortTableModel());
        portList.setShowHorizontalLines(true);
        portList.setRowSelectionAllowed(true);
        portList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        portList.getColumnModel().getColumn(0).setMaxWidth(50);
        portList.getColumnModel().getColumn(0).setPreferredWidth(50);        
        portList.getColumnModel().getColumn(0).setCellEditor(new OrderTableCellEditor());

        // handle row selection in ports table
        portList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        editPinsButton.setEnabled(true);
                        // don't allow removing ports when in edit mode
                        if (!dataModel.getEditMode() ||
                            !dataModel.getComponentType().getComponentTypeStatus().getInstantiated())
                            removeButton.setEnabled(true);
                        int row = lsm.getMinSelectionIndex();
                        ComponentType ct = dataModel.getComponentType();
                        List modelPortList = ct.getComponentPortTemplates();
                        dataModel.setSelectedPort((ComponentPortTemplate)modelPortList.get(row));
                    }
                }
            });
        
    	JScrollPane portListScroller = new JScrollPane(portList, vsbp, hsbp);
        stepComponent.add(portListScroller);

        JPanel radioButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioButtonPanel.setBackground(Color.white);

        // port table radio buttons
        addButton = new JRadioButton("Add");
        addButton.setActionCommand("Add");
        addButton.addActionListener(this);
        addButton.setBackground(Color.white);
        radioButtonPanel.add(addButton);

        removeButton = new JRadioButton("Remove");
        removeButton.setActionCommand("Remove");
        removeButton.addActionListener(this);
        removeButton.setBackground(Color.white);
        removeButton.setEnabled(false);
        radioButtonPanel.add(removeButton);

        editPinsButton = new JRadioButton("Edit Pins");
        editPinsButton.setActionCommand("Edit Pins");
        editPinsButton.addActionListener(this);
        editPinsButton.setBackground(Color.white);
        editPinsButton.setEnabled(false);
        radioButtonPanel.add(editPinsButton);

        doneButton = new JRadioButton("Done");
        doneButton.setBackground(Color.white);
        doneButton.setActionCommand("Done");
        doneButton.addActionListener(this);
        radioButtonPanel.add(doneButton);

        ButtonGroup group = new ButtonGroup();
        group.add(addButton);
        group.add(removeButton);
        group.add(editPinsButton);
        group.add(doneButton);
        stepComponent.add(radioButtonPanel);

        prepareRendering();

        return stepComponent;
    }

    public void actionPerformed(ActionEvent e) {
        String cmdString = e.getActionCommand();
        dataModel.setPortEditStatus(cmdString);
    }

    public void prepareRendering() {
        if (stepComponent != null) {
            if (dataModel.getEditMode() && !dataModel.getAddPortMode()) {
                doneButton.setSelected(true);
                dataModel.setPortEditStatus("Done");
            } else {
                addButton.setSelected(true);
                dataModel.setPortEditStatus("Add");
            }
        }
    }

    public Step[] getPendingSteps() {

        ComponentType ct = dataModel.getComponentType();

        // decide which step should follow based on portEditStatus
        String cmdString = dataModel.getPortEditStatus();
        if (cmdString.equals("Add")) {

            // add new port in data model
            List modelPortList = ct.getComponentPortTemplates();
            ComponentPortTemplate newPort = new ComponentPortTemplate();
            newPort.setComponentPortOrder(modelPortList.size());
            ct.addComponentPortTemplate(newPort);
            dataModel.setSelectedPort(newPort);
            
            // go to next step for defining new port properties
            return new Step[] {new NewTypeWizardStep06(dataModel)};

        } else if (cmdString.equals("Edit Pins")) {

            if (dataModel.getSelectedPort() != null) {
                // go to next step for defining new port properties
                return new Step[] {new NewTypeWizardStep07(dataModel)};
                
            } else {
                return null;
            }

        } else if (cmdString.equals("Remove")) {
            // get row out of table and from dataModel, return to this step
            if (dataModel.getSelectedPort() != null) {
                List modelPortList = ct.getComponentPortTemplates();
                modelPortList.remove(dataModel.getSelectedPort().getComponentPortOrder());
                // renumber port order of remaining elements
                for (int i = dataModel.getSelectedPort().getComponentPortOrder() ;
                     i < modelPortList.size() ; i++) {
                    ComponentPortTemplate cpt = (ComponentPortTemplate)modelPortList.get(i);
                    cpt.setComponentPortOrder(i);
                }
                PortTableModel portTableModel = 
                    (PortTableModel)portList.getModel();
                portTableModel.fireTableDataChanged();
            }
            // stay on this step
            return null;

        } else {  // Done

            // go to final confirmation prior to committing 
            
            // NOTE: will eventually forward to Step09 for signals definition
            return new Step[] {new NewTypeWizardStep10(dataModel)};            
        }
    }

    /**
     * Model for display/editing of component type port table.
     */
    class PortTableModel extends AbstractTableModel {
        private String[] columnNames = {"Order","Name","Type","Group","Num Pins"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            ComponentType ct = dataModel.getComponentType();
            List modelPortList = ct.getComponentPortTemplates();
            if (modelPortList != null)
                size = modelPortList.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            ComponentType ct = dataModel.getComponentType();
            List modelPortList = ct.getComponentPortTemplates();
            if (modelPortList != null) {
                ComponentPortTemplate cp = 
                    (ComponentPortTemplate)modelPortList.get(row);
                if (col == 0)
                    return Integer.toString(row);
                else if (col == 1) {
                    if (cp.getComponentPortName() == null)
                        return " ";
                    else
                        return cp.getComponentPortName();
                } else if (col == 2) {
                    if (cp.getComponentPortType() == null)
                        return " ";
                    else
                        return cp.getComponentPortType().getComponentPortType();
                } else if (col == 3) {
                    if (cp.getComponentPortType() == null)
                        return " ";
                    else
                        return cp.getComponentPortType().getComponentPortGroup();
                } else {
                    if (cp.getComponentPortType() == null)
                        return "0";
                    else
                        return Integer.toString(cp.getComponentPortType().getComponentPortPinCount());
                }
            } else {
                return " ";
            }
        }

        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }

    }

    /**
     * Cell editor for port order column.
     */
    class OrderTableCellEditor extends AbstractCellEditor 
        implements TableCellEditor {

        // note: make this a spinner instead
        JComboBox cb = new JComboBox();

        // called when cell value edited by user
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, 
                                                     boolean isSelected,
                                                     int row, int col) {

            String valueStr = (String)value;
            // set up combo box here
            cb.removeAllItems();
            cb.setEditable(false);
            cb.addItem("0");
            cb.addItem("1");
            cb.addItem("2");

            cb.setSelectedItem(valueStr);

            // autosize the row height to fit combo box better
            int desiredHeight = (int) cb.getPreferredSize().getHeight();
            if (desiredHeight > table.getRowHeight(row))
                table.setRowHeight(row,desiredHeight);
            
            return cb;
        }

        // called when editing is complete. must return new value
        // to be stored in the cell.
        public Object getCellEditorValue() {
            String selectedValue = (String)cb.getSelectedItem();
            return selectedValue;
        }
        
        public boolean isCellEditable(EventObject e) {
            return true;
        }

    }

}
