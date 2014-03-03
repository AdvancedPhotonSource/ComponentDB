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

// persistence layer
import gov.anl.aps.irmis.persistence.component.Component;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.login.GroupName;

// service layer
import gov.anl.aps.irmis.service.shared.PersonService;
import gov.anl.aps.irmis.service.IRMISException;

// useful dialog I'm sharing with component type app
import gov.anl.aps.irmis.apps.componenttype.cfw.wizard.TypeAddDialog;

/**
 * Step02 prompts the user to fill out various bits of info we keep
 * about a component, like name, serial number, ownership, etc...
 */
public class NewComponentWizardStep02 extends AbstractStep implements StepModelCustomizer {

    private NewComponentWizardModel dataModel;
    private JPanel stepComponent;
    private JTextField serialNumberTextField;
    private JTextField componentNameTextField;
    private JTextField imageURITextField;
    private JRadioButton falseButton;
    private JRadioButton trueButton;    
    private JComboBox gnCb;

    public NewComponentWizardStep02(NewComponentWizardModel dataModel) {
        super("Component Info","desc");
        this.dataModel = dataModel;
    }

    protected JComponent createComponent() {
        // scrollbar policy
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        stepComponent = new JPanel();
        stepComponent.setLayout(new BoxLayout(stepComponent,BoxLayout.PAGE_AXIS));
        stepComponent.setBackground(Color.white);

        // component name
        JLabel cnNameLabel = new JLabel("Name (optional):",SwingConstants.LEFT);
        JPanel cnNameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cnNameLabelPanel.setBackground(Color.white);
        cnNameLabelPanel.add(cnNameLabel);
        stepComponent.add(cnNameLabelPanel);

        componentNameTextField = new JTextField(20);
        JPanel componentNameTextFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        componentNameTextFieldPanel.setBackground(Color.white);
        componentNameTextFieldPanel.add(componentNameTextField);
        stepComponent.add(componentNameTextFieldPanel);

        JLabel ciNameLabel = new JLabel("Image URL (optional):",SwingConstants.LEFT);
        JPanel ciNameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ciNameLabelPanel.setBackground(Color.white);
        ciNameLabelPanel.add(ciNameLabel);
        stepComponent.add(ciNameLabelPanel);

        imageURITextField = new JTextField(20);
        JPanel imageURITextFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imageURITextFieldPanel.setBackground(Color.white);
        imageURITextFieldPanel.add(imageURITextField);
        stepComponent.add(imageURITextFieldPanel);

        // component serial number
        JLabel snNameLabel = new JLabel("Serial Number (optional):",SwingConstants.LEFT);
        JPanel snNameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        snNameLabelPanel.setBackground(Color.white);
        snNameLabelPanel.add(snNameLabel);
        stepComponent.add(snNameLabelPanel);

        serialNumberTextField = new JTextField(20);
        JPanel serialNumberTextFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        serialNumberTextFieldPanel.setBackground(Color.white);
        serialNumberTextFieldPanel.add(serialNumberTextField);
        stepComponent.add(serialNumberTextFieldPanel);

        // verified boolean
        JLabel verifLabel = new JLabel("Verified:",SwingConstants.LEFT);
        JPanel verifLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        verifLabelPanel.setBackground(Color.white);
        verifLabelPanel.add(verifLabel);
        stepComponent.add(verifLabelPanel);

        falseButton = new JRadioButton("False");
        falseButton.setBackground(Color.white);
        falseButton.setSelected(true);
        trueButton = new JRadioButton("True");
        trueButton.setBackground(Color.white);
        ButtonGroup verifButtonGroup = new ButtonGroup();
        verifButtonGroup.add(falseButton);
        verifButtonGroup.add(trueButton);
        JPanel verifPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        verifPanel.setBackground(Color.white);
        verifPanel.add(falseButton);
        verifPanel.add(trueButton);
        stepComponent.add(verifPanel);

        // group ownership
        JLabel groupLabel = new JLabel("Group Ownership:",SwingConstants.LEFT);
        JPanel groupLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        groupLabelPanel.setBackground(Color.white);
        groupLabelPanel.add(groupLabel);
        stepComponent.add(groupLabelPanel);

        gnCb = new JComboBox();
        gnCb.addItem("New Group Name...");
        Iterator gnIt = dataModel.getGroupNames().iterator();
        while (gnIt.hasNext()) {
            GroupName gn = (GroupName)gnIt.next();
            gnCb.addItem(gn);
        }
        gnCb.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        if (e.getItem() instanceof String) {  // New Group Name...
                            String newName = 
                                TypeAddDialog.showDialog(stepComponent,null,"Add New Group Name");
                            if (newName != null) {
                                GroupName gn = null;
                                gn = dataModel.findGroupName(newName);
                                if (gn == null) {
                                    gn = new GroupName();
                                    gn.setGroupName(newName);
                                    // save it to db
                                    try {
                                        PersonService.saveGroupName(gn);
                                        gnCb.addItem(gn);
                                        gnCb.setSelectedItem(gn);
                                    } catch (IRMISException ie) {
                                        ie.printStackTrace(System.out);
                                    }
                                }
                            } else {
                                gnCb.setSelectedIndex(1);
                            }

                        } else {
                            GroupName gn = (GroupName)e.getItem();
                            dataModel.setSelectedGroupName(gn);
                        }
                    }
                }
            });        
        gnCb.setSelectedIndex(1);
        JPanel gnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gnPanel.setBackground(Color.white);
        gnPanel.add(gnCb);
        stepComponent.add(gnPanel);

        prepareRendering();

        return stepComponent;
    }

    public void prepareRendering() {

    }

    public Step[] getPendingSteps() {

        // get component info
        String componentSerialNumber = serialNumberTextField.getText();
        String componentName = componentNameTextField.getText();
        String imageURI = imageURITextField.getText();
        Component c = dataModel.getComponent();
        c.setComponentName(componentName);
        c.setImageURI(imageURI);
        c.getApsComponent().setSerialNumber(componentSerialNumber);
        if (falseButton.isSelected())
            c.getApsComponent().setVerified(false);
        else
            c.getApsComponent().setVerified(true);

        c.getApsComponent().setGroupName(dataModel.getSelectedGroupName());
        
        return new Step[] {new NewComponentWizardStepFinish(dataModel)};
    }
}
