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

// IRMIS service layer
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.IRMISException;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentPortTemplate;
import gov.anl.aps.irmis.persistence.component.ComponentPortType;
import gov.anl.aps.irmis.persistence.component.PortPinDesignator;

/**
 * Step08 collects info to define a new component port type.
 */
public class NewTypeWizardStep08 extends AbstractStep implements StepModelCustomizer {

    private NewTypeWizardModel dataModel;
    private JPanel stepComponent;
    private JTable designatorList;
    private JComboBox cpCb;
    private PortPinDesignator selectedDesignator;
    private JLabel nameErrorLabel;
    private JTextField portNameField;
    
    public NewTypeWizardStep08(NewTypeWizardModel dataModel) {
        super("Add Port Type","desc");
        this.dataModel = dataModel;
    }

    protected JComponent createComponent() {
        // scrollbar policy
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        stepComponent = new JPanel();
        stepComponent.setLayout(new BoxLayout(stepComponent,BoxLayout.PAGE_AXIS));
        stepComponent.setBackground(Color.white);

        // if data model indicates error
        nameErrorLabel = new JLabel();
        nameErrorLabel.setForeground(Color.red);
        JPanel nameErrorLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameErrorLabelPanel.setBackground(Color.white);
        nameErrorLabelPanel.add(nameErrorLabel);
        stepComponent.add(nameErrorLabelPanel);

        // port type name
        JLabel portNameLabel = 
            new JLabel("Port Type Name:",SwingConstants.LEFT);
        JPanel portNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portNamePanel.setBackground(Color.white);
        portNamePanel.add(portNameLabel);
        portNameField = new JTextField(15);
        portNamePanel.add(portNameField);
        stepComponent.add(portNamePanel);

        // port type group pulldown
        JLabel cpLabel = new JLabel("Port Type Group:",SwingConstants.LEFT);
        JPanel cpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cpPanel.setBackground(Color.white);
        cpPanel.add(cpLabel);
        cpCb = new JComboBox();
        cpCb.removeAllItems();
        cpCb.setEditable(true);        
        Iterator ptgIt = dataModel.getPortTypeGroups().iterator();
        while (ptgIt.hasNext()) {
            String groupName = (String)ptgIt.next();
            cpCb.addItem(groupName);
        }
        cpPanel.add(cpCb);
        stepComponent.add(cpPanel);

        // variable number of designators
        JLabel designatorLabel = 
            new JLabel("Port Pin Designators:",SwingConstants.LEFT);
        JPanel designatorLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        designatorLabelPanel.setBackground(Color.white);
        designatorLabelPanel.add(designatorLabel);
        stepComponent.add(designatorLabelPanel);

        // designators table
        designatorList = new JTable(new DesignatorTableModel());
        designatorList.setShowHorizontalLines(true);
        designatorList.setRowSelectionAllowed(true);
        designatorList.setPreferredScrollableViewportSize(new Dimension(100,45));
        designatorList.getColumnModel().getColumn(0).setMaxWidth(40);
        designatorList.getColumnModel().getColumn(0).setPreferredWidth(40);        
        designatorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        designatorList.getColumnModel().getColumn(1).setCellEditor(new DesignatorTableCellEditor());
        
        // handle row selection in designators table
        designatorList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int selectedDesignatorRow = lsm.getMinSelectionIndex();
                        List modelDesignatorList = 
                            dataModel.getSelectedPort().getComponentPortType().getPortPinDesignators();
                        selectedDesignator = 
                            (PortPinDesignator)modelDesignatorList.get(selectedDesignatorRow);
                    }
                }
            });

    	JScrollPane designatorListScroller = new JScrollPane(designatorList, vsbp, hsbp);
        stepComponent.add(designatorListScroller);

        // designator table action button bar
        JPanel designatorButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton designatorAddButton = new JButton("Add");
        designatorAddButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (designatorList.getCellEditor() != null)
                        designatorList.getCellEditor().stopCellEditing();
                    List modelDesignatorList = 
                        dataModel.getSelectedPort().getComponentPortType().getPortPinDesignators();
                    int row = modelDesignatorList.size();

                    // create new designator row in the table
                    PortPinDesignator newDesignator = new PortPinDesignator();
                    newDesignator.setDesignatorOrder(row);
                    newDesignator.setDesignator("");
                    selectedDesignator = newDesignator;
                    ComponentPortType cpt = dataModel.getSelectedPort().getComponentPortType();
                    cpt.addPortPinDesignator(newDesignator);
                    cpt.setComponentPortPinCount(row+1);

                    DesignatorTableModel designatorTableModel = 
                        (DesignatorTableModel)designatorList.getModel();
                    designatorTableModel.fireTableRowsInserted(row, row);
                    designatorList.editCellAt(row,0);
                }
            });

        JButton designatorRemoveButton = new JButton("Remove");
        designatorRemoveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (selectedDesignator != null) {
                        if (designatorList.getCellEditor() != null)
                            designatorList.getCellEditor().stopCellEditing();
                        ComponentPortType cpt = dataModel.getSelectedPort().getComponentPortType();
                        List modelDesignatorList = cpt.getPortPinDesignators();
                        modelDesignatorList.remove(selectedDesignator);
                        cpt.setComponentPortPinCount(modelDesignatorList.size());
                        // renumber order of remaining designators
                        for (int i = selectedDesignator.getDesignatorOrder() ;
                             i < modelDesignatorList.size() ; i++) {
                            PortPinDesignator ppd = (PortPinDesignator)modelDesignatorList.get(i);
                            ppd.setDesignatorOrder(i);
                        }
                        selectedDesignator = null;
                        DesignatorTableModel designatorTableModel = 
                            (DesignatorTableModel)designatorList.getModel();
                        designatorTableModel.fireTableDataChanged();
                    }
                }
            });

        designatorButtons.add(designatorAddButton);
        designatorButtons.add(designatorRemoveButton);
        stepComponent.add(designatorButtons);

        prepareRendering();

        return stepComponent;
    }

    public void prepareRendering() {
        // initialize a new ComponentPortType
        if (dataModel.getSelectedPort().getComponentPortType() == null)
            dataModel.getSelectedPort().setComponentPortType(new ComponentPortType());

        // pre-load components with any choices that have been made already
        if (stepComponent != null) {
            String err = dataModel.getComponentPortTypeNameError();
            nameErrorLabel.setText(err);
        }
    }

    public Step[] getPendingSteps() {

        // cleanly stop any editing that was in progress
        if (designatorList.getCellEditor() != null)
            designatorList.getCellEditor().stopCellEditing();

        // get newly defined port type and add it to the database
        ComponentPortType cpt = dataModel.getSelectedPort().getComponentPortType();

        // copy widget data to data model object
        cpt.setComponentPortType(portNameField.getText());
        cpt.setComponentPortGroup((String)cpCb.getSelectedItem());

        // first verify that a unique name for the new type has been chosen
        if (cpt.getComponentPortType().length() == 0) {
            dataModel.setComponentPortTypeNameError("You must give it a name");
            return null;

        } else {
            try {
                // check validity of name first
                if (ComponentTypeService.isComponentPortTypeNameValid(cpt.getComponentPortType())) {
                    dataModel.setComponentPortTypeNameError(null);

                    // proceed with database insert
                    ComponentTypeService.saveComponentPortType(cpt);

                    // add it to the master list
                    List portTypes = dataModel.getPortTypes();
                    portTypes.add(0,cpt);
                    
                } else {
                    dataModel.setComponentPortTypeNameError("This port type name already exists!");
                    return null;
                }                
            } catch (IRMISException ie) {
                dataModel.setComponentPortTypeNameError("Unable to access database. Try again later.");
                return null;                
            }            
        }

        // return the next step that should follow
        return new Step[] {new NewTypeWizardStep06(dataModel)};
    }

    /**
     * Model for display/editing of component type document table.
     */
    class DesignatorTableModel extends AbstractTableModel {
        private String[] columnNames = {"Order","Designator"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            ComponentPortType selectedPortType = dataModel.getSelectedPort().getComponentPortType();
            if (selectedPortType != null)
                size = selectedPortType.getPortPinDesignators().size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {

            ComponentPortType selectedPortType = dataModel.getSelectedPort().getComponentPortType();
            if (selectedPortType != null) {
                List designators = selectedPortType.getPortPinDesignators();
                PortPinDesignator ppd = (PortPinDesignator)designators.get(row);
                if (col == 0)
                    return Integer.toString(ppd.getDesignatorOrder());
                else
                    return ppd.getDesignator();
            } else {
                return " ";
            }

        }

        // called after edit of attribute cell is complete
        public void setValueAt(Object value, int row, int col) {
            String strValue = (String)value;
            if (selectedDesignator != null) {
                if (col == 0) {  // document type
                    selectedDesignator.setDesignatorOrder(row);

                } else if (col == 1) {  // document uri
                    selectedDesignator.setDesignator(strValue);
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
     * Cell editor for designator column.
     */
    class DesignatorTableCellEditor extends AbstractCellEditor 
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
