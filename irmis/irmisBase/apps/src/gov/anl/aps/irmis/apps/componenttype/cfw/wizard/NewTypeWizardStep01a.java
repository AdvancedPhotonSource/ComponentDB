/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenttype.cfw.wizard;

import net.javaprog.ui.wizard.AbstractStep;
import net.javaprog.ui.wizard.Step;
import net.javaprog.ui.wizard.StepModelCustomizer;

import java.util.Iterator;

import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentType;

// IRMIS service layer
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Begins by asking for the component type name.
 */
public class NewTypeWizardStep01a extends AbstractStep implements StepModelCustomizer {

    private NewTypeWizardModel dataModel;
    private JPanel stepComponent;
    private JTable typeList;
    private ComponentType selectedComponentType;
    private int selectedRow;

    public NewTypeWizardStep01a(NewTypeWizardModel dataModel) {
        super("Copy","desc");
        this.dataModel = dataModel;
    }

    protected JComponent createComponent() {
        // scrollbar policy
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        stepComponent = new JPanel();
        stepComponent.setLayout(new BoxLayout(stepComponent,BoxLayout.PAGE_AXIS));
        stepComponent.setBackground(Color.white);
        
        // help text
        JTextArea introText = new JTextArea(4,35);
        introText.setEditable(false);
        introText.setLineWrap(true);
        introText.setWrapStyleWord(true);
        introText.append("Pick a component type to base your new type on.\n");
        introText.setMaximumSize(introText.getPreferredSize());
        stepComponent.add(introText);

        // component types table
        typeList = new JTable(new ComponentTypeTableModel());
        typeList.setShowHorizontalLines(true);
        typeList.setRowSelectionAllowed(true);
        typeList.getColumnModel().getColumn(0).setMaxWidth(110);
        typeList.getColumnModel().getColumn(0).setPreferredWidth(110);        
        typeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // handle row selection in component types table
        typeList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) return;
                    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        int row = lsm.getMinSelectionIndex();
                        selectedRow = row;
                        selectedComponentType = 
                            (ComponentType)dataModel.getComponentTypes().get(row);
                    }
                }
            });

    	JScrollPane typeListScroller = new JScrollPane(typeList, vsbp, hsbp);
        stepComponent.add(typeListScroller);        

        prepareRendering();

        return stepComponent;
    }

    public void prepareRendering() {
        if (stepComponent != null) {
        }
    }

    public Step[] getPendingSteps() {

        // create copy of selected component type
        if (selectedComponentType != null) {
            try {
                ComponentType copyCt = (ComponentType)selectedComponentType.clone();
                copyCt.setComponentTypeName(dataModel.getComponentType().getComponentTypeName());
                dataModel.setComponentType(copyCt);
            } catch (CloneNotSupportedException cnse) {
                cnse.printStackTrace();
            }
        }

        return new Step[] {new NewTypeWizardStep02(this.dataModel)};
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
            if (dataModel.getComponentTypes() != null)
                size = dataModel.getComponentTypes().size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {

            if (dataModel.getComponentTypes() != null) {
                ComponentType type = 
                    (ComponentType)dataModel.getComponentTypes().get(row);
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
