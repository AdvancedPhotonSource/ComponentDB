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
import java.util.HashSet;
import java.util.logging.*;

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

// general app utilities
import gov.anl.aps.irmis.login.LoginUtil;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentTypeFunction;
import gov.anl.aps.irmis.persistence.component.ComponentTypePerson;
import gov.anl.aps.irmis.persistence.component.ComponentTypeDocument;
import gov.anl.aps.irmis.persistence.component.ComponentTypeInterface;
import gov.anl.aps.irmis.persistence.component.ComponentTypeStatus;
import gov.anl.aps.irmis.persistence.component.ComponentPortTemplate;
import gov.anl.aps.irmis.persistence.audit.AuditAction;
import gov.anl.aps.irmis.persistence.audit.AuditActionType;
import gov.anl.aps.irmis.persistence.pv.URI;

// IRMIS service layer
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.shared.AuditService;

/**
 * Step10 summarizes the new component type defined thusfar. Clicking finish
 * here commits the new component type to the database.
 */
public class NewTypeWizardStep10 extends AbstractStep {
    //implements StepModelCustomizer {

    private NewTypeWizardModel dataModel;
    private JPanel stepComponent;
    private JTable attrList;

    public NewTypeWizardStep10(NewTypeWizardModel dataModel) {
        super("Commit","desc");
        this.dataModel = dataModel;
        setCanFinish(true);
        setCanGoNext(false);
    }

    protected JComponent createComponent() {
        // scrollbar policy
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        stepComponent = new JPanel();
        stepComponent.setLayout(new BoxLayout(stepComponent,BoxLayout.PAGE_AXIS));
        stepComponent.setBackground(Color.white);

        // component type summary table
        attrList = new JTable(new CompTypeAttrTableModel());
        attrList.setTableHeader(null);
        attrList.setShowHorizontalLines(true);
        attrList.setRowSelectionAllowed(true);
        JScrollPane attrListScroller = new JScrollPane(attrList, vsbp, hsbp);
        attrList.getColumnModel().getColumn(0).setMinWidth(80);
        //attrList.getColumnModel().getColumn(1).setPreferredWidth(300);
        attrList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stepComponent.add(attrListScroller);

        // commit text
        JTextArea commitText = new JTextArea(4,35);
        commitText.setEditable(false);
        commitText.setLineWrap(true);
        commitText.setWrapStyleWord(true);
        commitText.append("Above is a summary of your new component type. ");
        commitText.append("Click Finish to commit your definition...");
        commitText.setMaximumSize(commitText.getPreferredSize());
        stepComponent.add(commitText);

        return stepComponent;
    }

    public void prepareRendering() {
    }

    /**
     * Component type attribute table model. Displays the myriad of 
     * different scalar info about a component type.
     */
    class CompTypeAttrTableModel extends AbstractTableModel {
        private String[] columnNames = {"Name","Value"};
        public int getColumnCount() {
            return columnNames.length;
        }
        public int getRowCount() {
            int fixedSize = 7;
            int numDocs = 0;
            return fixedSize + numDocs;
        }
        public String getColumnName(int col) {
            return columnNames[col];
        }
        public Object getValueAt(int row, int col) {
            ComponentType ct = dataModel.getComponentType();
            switch (row) {
            case 0: {
                if (col == 0) 
                    return "Component Type";
                else
                    return ct.getComponentTypeName();
            }
            case 1: {
                if (col == 0)
                    return "Description";
                else
                    return ct.getDescription();
            }
            case 2: {
                if (col == 0)
                    return "Manufacturer";
                else {
                    if (ct.getManufacturer() == null)
                        return "none given";
                    else
                        return ct.getManufacturer().getManufacturerName();
                }
            }
            case 3: {
                if (col == 0)
                    return "Form Factor";
                else {
                    if (ct.getFormFactor() == null)
                        return "none given";
                    else
                        return ct.getFormFactor().getFormFactor();
                }
            }
            case 4: {
                if (col == 0)
                    return "Function(s)";
                else {
                    if (ct.getComponentTypeFunctions() == null ||
                        ct.getComponentTypeFunctions().size() == 0) {
                        return "none";
                    } else {
                        Iterator it = ct.getComponentTypeFunctions().iterator();
                        StringBuffer sb = new StringBuffer();
                        while (it.hasNext()) {
                            ComponentTypeFunction ctf = (ComponentTypeFunction)it.next();
                            sb.append(ctf.getFunction().getFunctionName() + ",");
                        }
                        sb.deleteCharAt(sb.length()-1);
                        return sb.toString();
                    }
                }
            }
            case 5: {
                if (col == 0)
                    return "Logical Interfaces";
                else
                    return Integer.toString(ct.getComponentTypeInterfaces().size());
            }
            case 6: {
                if (col == 0)
                    return "Physical Ports";
                else
                    return Integer.toString(ct.getComponentPortTemplates().size());
            }
            default: {
                return " ";
            }
            }
            
        }
        
        public Class getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
    }    
}
