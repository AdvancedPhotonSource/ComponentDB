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
import java.util.ArrayList;

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
import gov.anl.aps.irmis.persistence.component.ComponentTypeInterface;
import gov.anl.aps.irmis.persistence.component.ComponentTypeInterfaceType;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;

// IRMIS service layer
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Step04 begins the process of adding potentially several logical interfaces
 * of different types (control, housing, power, required, presented).
 */
public class NewTypeWizardStep04 extends AbstractStep implements StepModelCustomizer {

    private NewTypeWizardModel dataModel;
    private JPanel stepComponent;
    private JTable ifList;
    private List interfaces;
    private ComponentTypeInterface selectedInterface;

    public NewTypeWizardStep04(NewTypeWizardModel dataModel) {
        super("Logical Interfaces","desc");
        interfaces = new ArrayList();
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

        // variable number of interfaces
        JLabel ifLabel = 
            new JLabel("Logical Interfaces:",SwingConstants.LEFT);
        JPanel ifLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ifLabelPanel.setBackground(Color.white);
        ifLabelPanel.add(ifLabel);
        stepComponent.add(ifLabelPanel);

        // interfaces table
        ifList = new JTable(new InterfaceTableModel());
        ifList.setShowHorizontalLines(true);
        ifList.setRowSelectionAllowed(true);
        ifList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ifList.getColumnModel().getColumn(0).setMaxWidth(80);
        ifList.getColumnModel().getColumn(0).setPreferredWidth(80);        
        ifList.getColumnModel().getColumn(1).setMaxWidth(70);
        ifList.getColumnModel().getColumn(1).setPreferredWidth(70);        
        ifList.getColumnModel().getColumn(0).setCellEditor(new RelationshipTableCellEditor());
        ifList.getColumnModel().getColumn(1).setCellEditor(new DirectionTableCellEditor());
        ifList.getColumnModel().getColumn(2).setCellEditor(new TypeTableCellEditor());

        // handle row selection in interfaces table
        ifList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int selectedInterfaceRow = lsm.getMinSelectionIndex();
                        selectedInterface = 
                            (ComponentTypeInterface)interfaces.get(selectedInterfaceRow);
                    }
                }
            });

    	JScrollPane ifListScroller = new JScrollPane(ifList, vsbp, hsbp);
        stepComponent.add(ifListScroller);

        // interface table action button bar
        JPanel ifButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton ifAddButton = new JButton("Add");
        ifAddButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // stop any editing in progress
                    if (ifList.getCellEditor() != null)
                        ifList.getCellEditor().stopCellEditing();

                    // create new interface row in the table
                    ComponentTypeInterface newInterface = new ComponentTypeInterface();
                    // initialize with something so pulldowns show value
                    newInterface.setRequired(true);
                    ComponentRelationshipType crt = 
                        (ComponentRelationshipType)dataModel.findRelationship("control");
                    newInterface.setRelationshipType(crt);
                    newInterface.setInterfaceType((ComponentTypeInterfaceType)dataModel.getControlInterfaceTypes().get(0));
                    selectedInterface = newInterface;
                    interfaces.add(newInterface);

                    int row = interfaces.size() - 1;
                    InterfaceTableModel interfaceTableModel = 
                        (InterfaceTableModel)ifList.getModel();
                    interfaceTableModel.fireTableRowsInserted(row, row);
                    ifList.editCellAt(row,0);
                }
            });

        JButton ifRemoveButton = new JButton("Remove");
        // don't allow removal when in edit mode
        if (dataModel.getEditMode() && 
            dataModel.getComponentType().getComponentTypeStatus().getInstantiated())
            ifRemoveButton.setEnabled(false);

        ifRemoveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (selectedInterface != null) {
                        // stop any editing in progress
                        if (ifList.getCellEditor() != null)
                            ifList.getCellEditor().stopCellEditing();

                        interfaces.remove(selectedInterface);
                        InterfaceTableModel interfaceTableModel = 
                            (InterfaceTableModel)ifList.getModel();
                        interfaceTableModel.fireTableDataChanged();
                    }
                }
            });

        ifButtons.add(ifAddButton);
        ifButtons.add(ifRemoveButton);
        stepComponent.add(ifButtons);

        prepareRendering();

        return stepComponent;
    }

    public void prepareRendering() {
        if (stepComponent != null) {

            // create list from set for temporary use by gui
            interfaces = 
                new ArrayList(dataModel.getComponentType().getComponentTypeInterfaces());

        }
    }

    public Step[] getPendingSteps() {
        ComponentType ct = dataModel.getComponentType();

        // clear interface set and re-fill from interfaces list
        ct.clearComponentTypeInterfaces();
        Iterator it = interfaces.iterator();
        while (it.hasNext()) {
            ComponentTypeInterface cti = (ComponentTypeInterface)it.next();
            ct.addComponentTypeInterface(cti);
        }

        // return the next step that should follow
        return new Step[] {new NewTypeWizardStep05(dataModel)};
    }


    /**
     * Model for display/editing of component type interface table.
     */
    class InterfaceTableModel extends AbstractTableModel {
        private String[] columnNames = {"Relationship","Direction","Type"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int size = 0;
            if (interfaces != null)
                size = interfaces.size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {

            if (interfaces != null) {
                ComponentTypeInterface ife = 
                    (ComponentTypeInterface)interfaces.get(row);
                if (col == 0) {
                    return ife.getRelationshipType();
                } else if (col == 1) {
                    if (ife.getRequired())
                        return "required";
                    else 
                        return "presented";
                } else {
                    return ife.getInterfaceType();
                }
            } else {
                return " ";
            }

        }

        // called after edit of interface cell is complete
        public void setValueAt(Object value, int row, int col) {

            if (interfaces != null) {
                ComponentTypeInterface ife = (ComponentTypeInterface)interfaces.get(row);
                if (col == 0) {  
                    ComponentRelationshipType crt = (ComponentRelationshipType)value;
                    ife.setRelationshipType(crt);

                    // reset the interface type after relationship type selection
                    String relName = crt.getRelationshipType();
                    ComponentTypeInterfaceType ctit = null;
                    if (relName.equals("control")) {
                        ctit = (ComponentTypeInterfaceType)dataModel
                            .getControlInterfaceTypes().get(0);
                        
                    } else if (relName.equals("housing")) {
                        ctit = (ComponentTypeInterfaceType)dataModel
                            .getHousingInterfaceTypes().get(0);
                        
                    } else if (relName.equals("power")) {
                        ctit = (ComponentTypeInterfaceType)dataModel
                            .getPowerInterfaceTypes().get(0);
                        
                    }
                    ife.setInterfaceType(ctit);
                    
                } else if (col == 1) {
                    String strValue = (String)value;
                    if (strValue.equalsIgnoreCase("required")) {
                        ife.setRequired(true);
                        ife.setPresented(false);
                    } else {
                        ife.setRequired(false);
                        ife.setPresented(true);
                    }

                } else if (col == 2) {
                    ife.setInterfaceType((ComponentTypeInterfaceType)value);
                }
            }

        } 


        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return true;
        }
    }

    /**
     * Cell editor for interface relationship column.
     */
    class RelationshipTableCellEditor extends AbstractCellEditor 
        implements TableCellEditor {

        JComboBox cb = null;

        // called when cell value edited by user
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, 
                                                     boolean isSelected,
                                                     int row, int col) {

            if (cb == null) {
                cb = new JComboBox();
                cb.setEditable(false);
                
                Iterator rtIt = dataModel.getRelationships().iterator();
                while (rtIt.hasNext()) {
                    ComponentRelationshipType crt = 
                        (ComponentRelationshipType)rtIt.next();
                    cb.addItem(crt);
                }
                cb.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                stopCellEditing();
                                InterfaceTableModel interfaceTableModel = 
                                    (InterfaceTableModel)ifList.getModel();
                                interfaceTableModel.fireTableDataChanged();
                            }
                        }
                    });        
            }
                
            cb.setSelectedItem((ComponentRelationshipType)value);

            // autosize the row height to fit combo box better
            int desiredHeight = (int) cb.getPreferredSize().getHeight();
            if (desiredHeight > table.getRowHeight(row))
                table.setRowHeight(row,desiredHeight);
            
            return cb;
        }

        // called when editing is complete. must return new value
        // to be stored in the cell.
        public Object getCellEditorValue() {
            return cb.getSelectedItem();
        }
        
        public boolean isCellEditable(EventObject e) {
            return true;
        }

    }

    /**
     * Cell editor for interface direction column.
     */
    class DirectionTableCellEditor extends AbstractCellEditor 
        implements TableCellEditor {

        JComboBox cb = null;

        // called when cell value edited by user
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, 
                                                     boolean isSelected,
                                                     int row, int col) {

            String valueStr = (String)value;
            if (cb == null) {
                cb = new JComboBox();
                cb.setEditable(false);
                cb.addItem("presented");
                cb.addItem("required");
                
                cb.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                stopCellEditing();
                            }
                        }
                    });        
            }

            cb.setSelectedItem((String)value);

            // autosize the row height to fit combo box better
            int desiredHeight = (int) cb.getPreferredSize().getHeight();
            if (desiredHeight > table.getRowHeight(row))
                table.setRowHeight(row,desiredHeight);
            
            return cb;
        }

        // called when editing is complete. must return new value
        // to be stored in the cell.
        public Object getCellEditorValue() {
            return cb.getSelectedItem();
        }
        
        public boolean isCellEditable(EventObject e) {
            return true;
        }

    }

    /**
     * Cell editor for interface type column.
     */
    class TypeTableCellEditor extends AbstractCellEditor 
        implements TableCellEditor {

        // we need 3 combo boxes, one for each relationship type
        JComboBox controlCb = null;
        JComboBox housingCb = null;
        JComboBox powerCb = null;

        // this will reference the correct one
        JComboBox cb = null;
        ComponentRelationshipType crt = null;

        // called when cell value edited by user
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, 
                                                     boolean isSelected,
                                                     int row, int col) {

            // find out which rel type is selected
            crt = (ComponentRelationshipType)table.getValueAt(row,0);
            String relName = crt.getRelationshipType();

            // process the cb for that rel type
            if (relName.equals("control"))
                cb = controlCb;
            else if (relName.equals("housing"))
                cb = housingCb;
            else 
                cb = powerCb;
            
            if (cb == null) {
                cb = new JComboBox();                
                cb.setEditable(false);
                cb.addItem("New Type...");

                // fill combo box with choices for the given rel type
                Iterator itIt = null;
                if (relName.equals("control"))
                    itIt = dataModel.getControlInterfaceTypes().iterator();
                else if (relName.equals("housing"))
                    itIt = dataModel.getHousingInterfaceTypes().iterator();
                else 
                    itIt = dataModel.getPowerInterfaceTypes().iterator();

                while (itIt.hasNext()) {
                    ComponentTypeInterfaceType ctit = 
                        (ComponentTypeInterfaceType)itIt.next();
                    cb.addItem(ctit);
                }

                cb.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                if (e.getItem() instanceof String &&
                                    ((String)e.getItem()).equals("New Type...")) {  // New Type...
                                                                   String newType = 
                                        TypeAddDialog.showDialog(stepComponent,null,
                                                                 "Add New Interface Type");
                                    if (newType != null) {
                                        ComponentTypeInterfaceType ctit = 
                                            dataModel.findInterfaceType(crt, newType);
                                        if (ctit == null) {
                                            ctit = new ComponentTypeInterfaceType();
                                            ctit.setRelationshipType(crt);
                                            ctit.setInterfaceType(newType);
                                            // save it to db
                                            try {
                                                ComponentTypeService.saveInterfaceType(ctit);
                                                cb.addItem(ctit);
                                                cb.setSelectedItem(ctit);
                                            } catch (IRMISException ie) {
                                                ie.printStackTrace(System.out);
                                            }
                                        }
                                        
                                    } else {
                                        cb.setSelectedIndex(1);
                                    }
                                }  // end if instanceof String
                                stopCellEditing();
                            } // end if SELECTED
                        }
                    }); 
            }  // end if (cb == null)

            cb.setSelectedItem((ComponentTypeInterfaceType)value);

            // autosize the row height to fit combo box better
            int desiredHeight = (int) cb.getPreferredSize().getHeight();
            if (desiredHeight > table.getRowHeight(row))
                table.setRowHeight(row,desiredHeight);
            
            return cb;
        }

        // called when editing is complete. must return new value
        // to be stored in the cell.
        public Object getCellEditorValue() {
            // will be either a String or a ComponentTypeInterfaceType 
            return cb.getSelectedItem();  
        }
        
        public boolean isCellEditable(EventObject e) {
            return true;
        }

    }


}
