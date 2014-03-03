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
import java.util.Set;
import java.util.SortedSet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import gov.anl.aps.irmis.persistence.component.PortPinType;
import gov.anl.aps.irmis.persistence.component.PortPinDesignator;
import gov.anl.aps.irmis.persistence.component.PortPinTemplate;
import gov.anl.aps.irmis.persistence.component.ComponentPortTemplate;
import gov.anl.aps.irmis.persistence.component.ComponentPortType;

/**
 * Step07 continues the process of adding a port. Here we prompt for the
 * entry of the individual pins of the port.
 */
public class NewTypeWizardStep07 extends AbstractStep 
    implements StepModelCustomizer {

    private NewTypeWizardModel dataModel;
    private JPanel stepComponent;
    private JTable pinList;

    public NewTypeWizardStep07(NewTypeWizardModel dataModel) {
        super("Edit Pins","desc");
        this.dataModel = dataModel;
        if (dataModel.getEditPinsMode()) {
            setCanGoNext(false);
            setCanFinish(true);
        }
    }

    protected JComponent createComponent() {
        // scrollbar policy
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        stepComponent = new JPanel();
        stepComponent.setLayout(new BoxLayout(stepComponent,BoxLayout.PAGE_AXIS));
        stepComponent.setBackground(Color.white);


        ComponentPortTemplate cp = dataModel.getSelectedPort();
        ComponentPortType cpt = cp.getComponentPortType();

        // component type name        
        JLabel nameLabel = 
            new JLabel(dataModel.getComponentType().getComponentTypeName() + 
                       "   port:" + cp.getComponentPortName()+
                       " type:"+ cpt.getComponentPortType(),
                       SwingConstants.LEFT);
        JPanel nameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameLabelPanel.setBackground(Color.white);
        nameLabelPanel.add(nameLabel);
        stepComponent.add(nameLabelPanel);

        // variable number of pins
        JLabel pinLabel = 
            new JLabel("Physical Pins:",SwingConstants.LEFT);
        JPanel pinLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pinLabelPanel.setBackground(Color.white);
        pinLabelPanel.add(pinLabel);
        stepComponent.add(pinLabelPanel);

        // pins table
        pinList = new JTable(new PinTableModel());
        pinList.setShowHorizontalLines(true);
        pinList.setRowSelectionAllowed(true);
        pinList.getColumnModel().getColumn(1).setCellEditor(new PinNameTableCellEditor());
        //pinList.getColumnModel().getColumn(2).setCellEditor(new PinTypeTableCellEditor());

    	JScrollPane pinListScroller = new JScrollPane(pinList, vsbp, hsbp);
        pinList.getColumnModel().getColumn(1).setPreferredWidth(130);
        pinList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stepComponent.add(pinListScroller);

        return stepComponent;
    }


    public void prepareRendering() {
    }

    public Step[] getPendingSteps() {
        // cleanly stop any editing that was in progress
        if (pinList.getCellEditor() != null)
            pinList.getCellEditor().stopCellEditing();

        return new Step[] {new NewTypeWizardStep05(dataModel)};            
    }

    /**
     * Model for display/editing of component type pin table.
     */
    class PinTableModel extends AbstractTableModel {
        private String[] columnNames = {"Designator","Usage"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            ComponentPortTemplate cp = dataModel.getSelectedPort();
            Set pins = cp.getPortPinTemplates();
            if (pins != null)
                size = pins.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            SortedSet pins = dataModel.getSelectedPort().getPortPinTemplates();
            if (pins != null) {
                Iterator it = pins.iterator();
                int index = 0;
                PortPinTemplate pin = null;
                while (it.hasNext()) {
                    pin = (PortPinTemplate)it.next();
                    if (index == row)
                        break;
                    index++;
                }
                switch (col) {
                case 0: {
                    return pin.getPortPinDesignator().getDesignator();
                }
                case 1: {
                    return pin.getPortPinUsage();
                }
                default: { return " "; }
                }

            } else {
                return " ";
            }

        }

        // called after edit of attribute cell is complete
        public void setValueAt(Object value, int row, int col) {

            SortedSet pins = dataModel.getSelectedPort().getPortPinTemplates();
            if (pins != null) {
                Iterator it = pins.iterator();
                int index = 0;
                PortPinTemplate pin = null;
                while (it.hasNext()) {
                    pin = (PortPinTemplate)it.next();
                    if (index == row)
                        break;
                    index++;
                }
                if (col == 1) {  // name
                    String strValue = (String)value;
                    pin.setPortPinUsage(strValue);
                    
                } 
            }
        }

        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (col == 0)
                return false;
            else 
                return true;
        }
        
    }

    /**
     * Cell editor for pin name column.
     */
    class PinNameTableCellEditor extends AbstractCellEditor 
        implements TableCellEditor {
        
        JTextField textField = new JTextField();

        // called when cell value edited by user
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, 
                                                     boolean isSelected,
                                                     int row, int col) {

            textField.setEditable(true);
            textField.addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        char keyChar = e.getKeyChar();

                        if (keyChar == '\n') {
                            // not a good way to get rid of newline,
                            // but I can't figure out how to stop
                            // this from getting entered in text area
                            int cp = textField.getCaretPosition();
                            Document doc = textField.getDocument();
                            try {
                                doc.remove(textField.getText().length()-1,1);
                            } catch (BadLocationException ble) {}
                            stopCellEditing();
                        }
                    }
                });
            
            // put the data in the text field
            if (value != null)
                textField.setText(value.toString());
            
            return textField;
        }

        // called when editing is complete. must return new value
        // to be stored in the cell.
        public Object getCellEditorValue() {
            return textField.getText();
        }

        public boolean isCellEditable(EventObject e) {
            return true;
        }

    }    

}
