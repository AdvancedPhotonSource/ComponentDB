/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw.wizard;

import net.javaprog.ui.wizard.AbstractStep;
import net.javaprog.ui.wizard.Step;
import net.javaprog.ui.wizard.StepModelCustomizer;

import java.util.Iterator;
import java.util.EventObject;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.*;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import gov.anl.aps.irmis.apps.AppsUtil;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentRelationshipType;
import gov.anl.aps.irmis.persistence.login.GroupName;

// IRMIS service layer
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.component.ComponentService;

/**
 * Step01 prompts the user to select the component type for their instance.
 * 
 */
public class NewComponentWizardStep01 extends AbstractStep implements StepModelCustomizer {

    private NewComponentWizardModel dataModel;
    private JPanel stepComponent;
    private JTable typeList;
    private JTextField compTypeFilterTextField;
    private ComponentType selectedComponentType;
    private int selectedRow;

    public NewComponentWizardStep01(NewComponentWizardModel dataModel) {
        super("Component Type Select","desc");
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
            new JLabel("Select component type:",
                       SwingConstants.LEFT);
        JPanel nameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameLabelPanel.setBackground(Color.white);
        nameLabelPanel.add(nameLabel);
        stepComponent.add(nameLabelPanel);

        // component types table label
        JLabel typeLabel = 
            new JLabel("Component Types:",SwingConstants.LEFT);
        JPanel typeLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typeLabelPanel.setBackground(Color.white);
        typeLabelPanel.add(typeLabel);
        stepComponent.add(typeLabelPanel);

        // comp type name filter
        JPanel compTypeFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel compTypeFilterLabel = 
            new JLabel("Find:",SwingConstants.LEFT);        
        compTypeFilterPanel.add(compTypeFilterLabel);
        compTypeFilterTextField = new JTextField(15);
        compTypeFilterTextField.setToolTipText("Enter a wildcard string to filter component type list.");
        compTypeFilterTextField.setPreferredSize(new Dimension(350,20));
        compTypeFilterTextField.setMaximumSize(new Dimension(350,20));
        //typeListPanel.add(compTypeFilterTextField);
        compTypeFilterTextField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                        String filterText = compTypeFilterTextField.getText();
                        // uppercase it for case insensitive match
                        filterText = filterText.toUpperCase();                        
                        // change any * to reg-ex pattern
                        filterText = filterText.replaceAll("\\*",".*");
                        Pattern filterRegEx = Pattern.compile("^.*"+filterText+".*$");
                        Iterator compTypeIt = dataModel.getComponentTypes().iterator();
                        dataModel.setFilteredComponentTypes(new ArrayList());
                        while (compTypeIt.hasNext()) {
                            ComponentType ct = (ComponentType)compTypeIt.next();
                            Matcher matcher1 = 
                                filterRegEx.matcher(ct.getComponentTypeName().toUpperCase());
                            Matcher matcher2 = 
                                filterRegEx.matcher(ct.getDescription().toUpperCase());
                            if (matcher1.matches() || matcher2.matches())
                                dataModel.getFilteredComponentTypes().add(ct);
                        }
                        ComponentTypeTableModel tableModel = 
                            (ComponentTypeTableModel)typeList.getModel();
                        // request that comp type table update itself
                        tableModel.fireTableDataChanged();
                }
            });
        compTypeFilterPanel.add(compTypeFilterTextField);
        stepComponent.add(compTypeFilterPanel);

        // component types table
        typeList = new JTable(new ComponentTypeTableModel());
        typeList.setShowHorizontalLines(true);
        typeList.setRowSelectionAllowed(true);
        typeList.getColumnModel().getColumn(0).setMinWidth(20);
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
                            (ComponentType)dataModel.getFilteredComponentTypes().get(row);
                    }
                }
            });

    	JScrollPane typeListScroller = new JScrollPane(typeList, vsbp, hsbp);
        stepComponent.add(typeListScroller);

        prepareRendering();

        return stepComponent;
    }

    public void prepareRendering() {

    }

    public Step[] getPendingSteps() {

        // return the next step that should follow
        if (selectedComponentType == null) {
            return new Step[] {new NewComponentWizardStep01(dataModel)};
        } else {
            // create new component based on type
            Component newC = new Component(selectedComponentType);
            dataModel.setComponent(newC);

            // need to find out if we can do auto-placement in control and power hierarchies
            Component parent = dataModel.getParentComponent();
            int hierarchy = dataModel.getHierarchy();
            if (parent != null && hierarchy == ComponentRelationshipType.HOUSING) {  
                boolean canAddToControl = 
                    ComponentService.isValidForAutomaticPlacement(newC, ComponentRelationshipType.CONTROL);
                boolean canAddToPower =
                    ComponentService.isValidForAutomaticPlacement(newC, ComponentRelationshipType.POWER);
                dataModel.setCanAddToControl(canAddToControl);
                dataModel.setCanAddToPower(canAddToPower);
            }
            return new Step[] {new NewComponentWizardStep02(dataModel)};
        }
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
            if (dataModel.getFilteredComponentTypes() != null)
                size = dataModel.getFilteredComponentTypes().size();
            return size;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {

            if (dataModel.getFilteredComponentTypes() != null) {
                ComponentType type = 
                    (ComponentType)dataModel.getFilteredComponentTypes().get(row);
                if (col == 0) {
                    return type.getComponentTypeName();
                } else {
                    if (type.getDescription() == null)
                        return " ";
                    else 
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
