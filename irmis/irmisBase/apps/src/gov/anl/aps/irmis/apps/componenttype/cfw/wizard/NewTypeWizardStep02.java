/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenttype.cfw.wizard;

import net.javaprog.ui.wizard.AbstractStep;
import net.javaprog.ui.wizard.Step;
import net.javaprog.ui.wizard.StepModelCustomizer;

import java.util.Iterator;
import java.util.List;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.*;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentTypeFunction;
import gov.anl.aps.irmis.persistence.component.ComponentTypeStatus;
import gov.anl.aps.irmis.persistence.component.Manufacturer;
import gov.anl.aps.irmis.persistence.component.FormFactor;
import gov.anl.aps.irmis.persistence.component.Function;

// IRMIS service layer
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Step02 gets a variety of general info about the new component type, such
 * as description, manufacturer, form factor, function.
 */
public class NewTypeWizardStep02 extends AbstractStep implements StepModelCustomizer {

    private NewTypeWizardModel dataModel;
    private JPanel stepComponent;
    private JTextArea descTextArea;
    private JComboBox mfgCb;
    private JComboBox ffCb;
    private JComboBox functionCb;
    private JList functionList;
    private DefaultListModel functionListModel;

    public NewTypeWizardStep02(NewTypeWizardModel dataModel) {
        super("General Info 1","desc");
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

        // description text area (editable)
        JLabel descLabel = new JLabel("Description:",SwingConstants.LEFT);
        JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        descPanel.setBackground(Color.white);
        descPanel.add(descLabel);
        descTextArea = new JTextArea(2,22);
        if (dataModel.getComponentType().getIsBaseType())
            descTextArea.setEnabled(false);
        descTextArea.setBorder(BorderFactory.createLoweredBevelBorder());
        descPanel.add(descTextArea);
        stepComponent.add(descPanel);

        // manufacturer pulldown (editable)
        JLabel mfgLabel = new JLabel("Manufacturer:",SwingConstants.LEFT);
        JPanel mfgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mfgPanel.setBackground(Color.white);
        mfgPanel.add(mfgLabel);
        mfgCb = new JComboBox();
        if (dataModel.getComponentType().getIsBaseType()) {
            mfgCb.setEnabled(false);
        }
        mfgCb.addItem("New Manufacturer...");
        Iterator mfgIt = dataModel.getManufacturers().iterator();
        while (mfgIt.hasNext()) {
            Manufacturer mfg = (Manufacturer)mfgIt.next();
            mfgCb.addItem(mfg);
        }
        mfgCb.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        if (e.getItem() instanceof String) {  // New Manufacturer...
                            String newType = 
                                TypeAddDialog.showDialog(stepComponent,null,"Add New Manufacturer");
                            if (newType != null) {
                                Manufacturer mfg = null;
                                mfg = dataModel.findManufacturer(newType);
                                if (mfg == null) {
                                    mfg = new Manufacturer();
                                    mfg.setManufacturerName(newType);
                                    // save it to db
                                    try {
                                        ComponentTypeService.saveManufacturer(mfg);
                                        mfgCb.addItem(mfg);
                                        mfgCb.setSelectedItem(mfg);
                                    } catch (IRMISException ie) {
                                        ie.printStackTrace(System.out);
                                    }
                                }
                            } else {
                                mfgCb.setSelectedIndex(1);
                            }

                        } else {
                            ComponentType ct = dataModel.getComponentType();
                            Manufacturer mfg = (Manufacturer)e.getItem();
                            ct.setManufacturer(mfg);
                        }
                    }
                }
            });        
        mfgPanel.add(mfgCb);
        stepComponent.add(mfgPanel);

        // form-factor pulldown (editable)
        JLabel ffLabel = new JLabel("Form Factor:",SwingConstants.LEFT);
        JPanel ffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ffPanel.setBackground(Color.white);
        ffPanel.add(ffLabel);
        ffCb = new JComboBox();
        if (dataModel.getComponentType().getIsBaseType())
            ffCb.setEnabled(false);
        ffCb.addItem("New Form Factor...");
        Iterator ffIt = dataModel.getFormFactors().iterator();
        while (ffIt.hasNext()) {
            FormFactor ff = (FormFactor)ffIt.next();
            ffCb.addItem(ff);
        }
        ffCb.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        if (e.getItem() instanceof String) {  // New Form Factor...
                            String newType = 
                                TypeAddDialog.showDialog(stepComponent,null,"Add New Form Factor");
                            if (newType != null) {
                                FormFactor ff = null;
                                ff = dataModel.findFormFactor(newType);
                                if (ff == null) {
                                    ff = new FormFactor();
                                    ff.setFormFactor(newType);
                                    // save it to db
                                    try {
                                        ComponentTypeService.saveFormFactor(ff);
                                        ffCb.addItem(ff);
                                        ffCb.setSelectedItem(ff);
                                    } catch (IRMISException ie) {
                                        ie.printStackTrace(System.out);
                                    }
                                }
                            } else {
                                ffCb.setSelectedIndex(1);
                            }

                        } else {
                            ComponentType ct = dataModel.getComponentType();
                            FormFactor ff = (FormFactor)e.getItem();
                            ct.setFormFactor(ff);
                        }
                    }
                }
            });        
        ffPanel.add(ffCb);
        stepComponent.add(ffPanel);

        // function list 
        JLabel functionLabel = new JLabel("Function (multi-select):",SwingConstants.LEFT);
        JPanel functionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        functionPanel.setBackground(Color.white);
        functionPanel.add(functionLabel);

        functionListModel = new DefaultListModel();
        functionListModel.addElement("New Function...");
        Iterator fIt = dataModel.getFunctions().iterator();
        while (fIt.hasNext()) {
            Function f = (Function)fIt.next();
            functionListModel.addElement(f);
        }
    	functionList = new JList(functionListModel);
        functionList.setVisibleRowCount(5);
    	functionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // handle function list selection events
        functionList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting() == false) {
                        // just detect and process "New Function..." selection
                        int[] indices = functionList.getSelectedIndices();
                        for (int i=0 ; i < indices.length ; i++) {
                            if (indices[i] == 0) {
                                Function f = null;
                                ComponentType ct = dataModel.getComponentType();
                                String newType = 
                                    TypeAddDialog.showDialog(stepComponent,null,"Add New Function");
                                if (newType != null) {
                                    f = dataModel.findFunction(newType);
                                    if (f == null) {
                                        f = new Function();
                                        f.setFunctionName(newType);
                                        // save it to db
                                        try {
                                            ComponentTypeService.saveFunction(f);
                                            functionListModel.add(1,f);
                                            functionList.removeSelectionInterval(0,0);
                                            //functionCb.setSelectedItem(f);
                                            
                                        } catch (IRMISException ie) {
                                            ie.printStackTrace(System.out);
                                        }
                                    }
                                }                                
                            }
                        }
                    }
                }
            });
    	JScrollPane functionListScroller = new JScrollPane(functionList, vsbp, hsbp);
        functionPanel.add(functionListScroller);
        stepComponent.add(functionPanel);

        prepareRendering();

        return stepComponent;
    }

    public void prepareRendering() {
        // pre-load components with any choices that have been made already
        if (stepComponent != null) {
            ComponentType ct = dataModel.getComponentType();

            descTextArea.setText(ct.getDescription());
            if (ct.getManufacturer() == null) {
                if (mfgCb.getItemCount() > 1)
                    mfgCb.setSelectedIndex(1);
                else
                    mfgCb.setSelectedIndex(-1);
            } else {
                mfgCb.setSelectedItem(ct.getManufacturer());
            }

            if (ct.getFormFactor() == null) {
                if (ffCb.getItemCount() > 1)
                    ffCb.setSelectedIndex(1);
                else
                    ffCb.setSelectedIndex(-1);
            } else {
                ffCb.setSelectedItem(ct.getFormFactor());
            }

            functionList.clearSelection();
            List functions = dataModel.getFunctions();
            int firstIndex = -1;
            if (ct.getComponentTypeFunctions() != null) {
                Iterator it = ct.getComponentTypeFunctions().iterator();
                while (it.hasNext()) {
                    ComponentTypeFunction ctf = (ComponentTypeFunction)it.next();
                    int index = functions.indexOf(ctf.getFunction()) + 1;
                    if (firstIndex == -1)
                        firstIndex = index;
                    functionList.addSelectionInterval(index,index);
                }
                functionList.ensureIndexIsVisible(firstIndex);
            }
        }
    }

    public Step[] getPendingSteps() {

        // transfer data from widgets to dataModel
        ComponentType ct = dataModel.getComponentType();

        // only take 1st 100 chars of description
        String descText = descTextArea.getText();
        if (descText.length() > 100)
            descText = descText.substring(0,99);
        ct.setDescription(descText);

        // get function selections
        ct.clearComponentTypeFunctions();
        int[] indices = functionList.getSelectedIndices();
        for (int i=0 ; i < indices.length ; i++) {
            if (indices[i] != 0) {  // don't process "New Function..."
                Function f = (Function)functionListModel.elementAt(indices[i]);
                // get selection into ct
                ComponentTypeFunction ctf = new ComponentTypeFunction();
                ctf.setFunction(f);
                ct.addComponentTypeFunction(ctf);                
            }
        }

        // decide which step should follow

        // go to next step
        return new Step[] {new NewTypeWizardStep02a(dataModel)};
    }

}
