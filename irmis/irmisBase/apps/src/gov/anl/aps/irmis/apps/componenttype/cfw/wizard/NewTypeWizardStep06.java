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
import java.util.Set;
import java.util.HashSet;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentPortTemplate;
import gov.anl.aps.irmis.persistence.component.ComponentPortType;
import gov.anl.aps.irmis.persistence.component.PortPinTemplate;
import gov.anl.aps.irmis.persistence.component.PortPinDesignator;

/**
 * Step06 begins the process of adding a single port to a component type.
 * This step collects basic scalar info about the port, then proceeds to
 * another step to build up the pins.
 */
public class NewTypeWizardStep06 extends AbstractStep 
    implements StepModelCustomizer {

    private NewTypeWizardModel dataModel;
    private JPanel stepComponent;
    private JLabel portGroupPropertyLabel;
    private JLabel portPinCountPropertyLabel;
    private JTextField portNameField;
    private JComboBox ptCb;

    public NewTypeWizardStep06(NewTypeWizardModel dataModel) {
        super("Add Port","desc");
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
            new JLabel(dataModel.getComponentType().getComponentTypeName() + ": Add Port",
                       SwingConstants.LEFT);
        JPanel nameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameLabelPanel.setBackground(Color.white);
        nameLabelPanel.add(nameLabel);
        stepComponent.add(nameLabelPanel);

        // port name
        JLabel portNameLabel = 
            new JLabel("Port Name:",SwingConstants.LEFT);
        JPanel portNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portNamePanel.setBackground(Color.white);
        portNamePanel.add(portNameLabel);
        portNameField = new JTextField(15);
        portNamePanel.add(portNameField);
        stepComponent.add(portNamePanel);

        // port type

        // port type properties (based on selected port type)
        // define here 'cause we need them in the listener below
        portGroupPropertyLabel = 
            new JLabel("",SwingConstants.LEFT);
        portPinCountPropertyLabel = 
            new JLabel("",SwingConstants.LEFT);        

        JLabel portTypeLabel = 
            new JLabel("Port Type:",SwingConstants.LEFT);
        JPanel portTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portTypePanel.setBackground(Color.white);
        portTypePanel.add(portTypeLabel);
        ptCb = new JComboBox();
        ptCb.removeAllItems();
        ptCb.setEditable(false); 
        ptCb.addItem("New Type...");
        Iterator ptIt = dataModel.getPortTypes().iterator();
        while (ptIt.hasNext()) {
            ComponentPortType pt = (ComponentPortType)ptIt.next();
            ptCb.addItem(pt);
        }

        // handle port type selections
        ptCb.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    ComponentPortTemplate cp = dataModel.getSelectedPort();
                    ComponentPortType cpt = null;
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        if (e.getItem() instanceof String) {
                            portGroupPropertyLabel.setText("");
                            portPinCountPropertyLabel.setText("");
                            cp.setComponentPortType(null);
                        } else {
                            cpt = (ComponentPortType)e.getItem();
                            portGroupPropertyLabel.setText("Group: "+cpt.getComponentPortGroup());
                            portPinCountPropertyLabel.setText("Pin Count: "+cpt.getComponentPortPinCount());
                            cp.setComponentPortType(cpt);
                        }
                    }
                }
            });        
        ptCb.setSelectedIndex(1);

        portTypePanel.add(ptCb);
        stepComponent.add(portTypePanel);

        JPanel portTypePropertyPanel = new JPanel(new BorderLayout());
        TitledBorder portTypePropertyTitle = BorderFactory.createTitledBorder("Port Type Properties");
        portTypePropertyPanel.setBorder(portTypePropertyTitle);
        portTypePropertyPanel.setBackground(Color.white);
        portTypePropertyPanel.add(portGroupPropertyLabel, BorderLayout.NORTH);
        portTypePropertyPanel.add(portPinCountPropertyLabel, BorderLayout.CENTER);
        stepComponent.add(portTypePropertyPanel);

        prepareRendering();

        return stepComponent;
    }

    public void prepareRendering() {
        ComponentPortTemplate cp = dataModel.getSelectedPort();

        // pre-load components with any choices that have been made already
        if (stepComponent != null) {
            portNameField.setText(cp.getComponentPortName());
            if (cp.getComponentPortType() != null)
                ptCb.setSelectedItem(cp.getComponentPortType());
        }
    }

    public Step[] getPendingSteps() {

        // copy data from widgets to data model
        ComponentPortTemplate cp = dataModel.getSelectedPort();
        cp.setComponentPortName(portNameField.getText());

        if (cp.getComponentPortType() == null) {
            // go to step for adding a new port type
            return new Step[] {new NewTypeWizardStep08(dataModel)};

        } else {
            ComponentPortType spt = cp.getComponentPortType();

            // prepare the pins list based on selected port type
            Set pins = new HashSet();
            Iterator it = spt.getPortPinDesignators().iterator();
            while (it.hasNext()) {
                PortPinDesignator ppd = (PortPinDesignator)it.next();
                PortPinTemplate newPin = new PortPinTemplate();
                newPin.setPortPinUsage("");
                newPin.setPortPinDesignator(ppd);
                cp.addPortPinTemplate(newPin);
            }
            
            // proceed to step for editing pins
            return new Step[] {new NewTypeWizardStep07(dataModel)};            
        }

    }
}
