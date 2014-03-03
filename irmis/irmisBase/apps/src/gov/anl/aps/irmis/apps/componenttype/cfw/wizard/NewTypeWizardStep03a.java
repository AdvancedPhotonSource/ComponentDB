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
import java.util.Collections;

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

import gov.sns.application.Application;

import gov.anl.aps.irmis.apps.AppsUtil;
import gov.anl.aps.irmis.apps.admin.cfw.NewPersonDialog;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.pv.URI;
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.ComponentTypeStatus;
import gov.anl.aps.irmis.persistence.component.BeamlineInterest;

// IRMIS service layer
import gov.anl.aps.irmis.service.shared.PersonService;
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Step03a gets CHC specific general info about the new component type, such
 * as verbose description, beamline interest, and chc_contact person.
 */
public class NewTypeWizardStep03a extends AbstractStep implements StepModelCustomizer {

    private NewTypeWizardModel dataModel;
    private JPanel stepComponent;
    private JTextArea descTextArea;
    private JComboBox cpCb;  // contact person
    private JComboBox biCb;  // beamline interest
    private JComboBox nsCb;
    private JComboBox naCb;

    public NewTypeWizardStep03a(NewTypeWizardModel dataModel) {
        super("General Info 3","desc");
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

        // verbose description text area (editable)
        JLabel descLabel = new JLabel("Verbose Description:",SwingConstants.LEFT);
        JPanel descPanel = new JPanel();
        descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.PAGE_AXIS));
        descPanel.setBackground(Color.white);
        descPanel.add(descLabel);
        descTextArea = new JTextArea(6,20);
        descTextArea.setLineWrap(true);
        if (dataModel.getComponentType().getIsBaseType())
            descTextArea.setEnabled(false);
        descTextArea.setBorder(BorderFactory.createLoweredBevelBorder());
        //descPanel.add(descTextArea);
        JScrollPane descScrollPane = new JScrollPane(descTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                     ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        descPanel.add(descScrollPane);
        stepComponent.add(descPanel);

        // Beamline interest pulldown
        JLabel biLabel = new JLabel("Beamline Interest:",SwingConstants.LEFT);
        JPanel biPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        biPanel.setBackground(Color.white);
        biPanel.add(biLabel);
        biCb = new JComboBox();
        biCb.removeAllItems();
        Iterator biIt = dataModel.getBeamlineInterestList().iterator();
        while (biIt.hasNext()) {
            BeamlineInterest bi= (BeamlineInterest)biIt.next();
            biCb.addItem(bi);
        }
        biCb.setSelectedIndex(0);
        biPanel.add(biCb);
        stepComponent.add(biPanel);

        // CHC contact person pulldown
        JLabel cpLabel = new JLabel("CHC Contact:",SwingConstants.LEFT);
        JPanel cpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cpPanel.setBackground(Color.white);
        cpPanel.add(cpLabel);
        cpCb = new JComboBox();
        cpCb.removeAllItems();
        cpCb.addItem("New Person...");
        Iterator pIt = dataModel.getPersons().iterator();
        while (pIt.hasNext()) {
            Person p = (Person)pIt.next();
            cpCb.addItem(p);
        }
        cpCb.setSelectedIndex(0);
        cpPanel.add(cpCb);
        stepComponent.add(cpPanel);

        // handle new person selection in pulldown
        cpCb.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        if (e.getItem() instanceof String) {
                            Person person = NewPersonDialog.showDialog(stepComponent, null);
                            if (person != null) {
                                List personList = dataModel.getPersons();
                                if (personList.contains(person)) {
                                    Application.displayWarning("Warning","Person with this userid already exists.");
                                } else {
                                    try {
                                        PersonService.savePerson(person);
                                        personList.add(person);
                                        Collections.sort(personList);
                                        cpCb.addItem(person);
                                        cpCb.setSelectedItem(person);
                                    } catch (IRMISException ie) {
                                        ie.printStackTrace();
                                    }
                                }
                            }
                        } 
                    }
                }
            });        
        cpCb.setSelectedIndex(1);

        // NRTL Status pulldown
        JLabel nsLabel = new JLabel("NRTL Status:",SwingConstants.LEFT);
        JPanel nsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nsPanel.setBackground(Color.white);
        nsPanel.add(nsLabel);
        nsCb = new JComboBox();
        naCb = new JComboBox(); // init here in case event fired below
        // handle nrtl status selection in pulldown
        nsCb.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        String item = (String)e.getItem();
                        if (item.equalsIgnoreCase("NRTL Approved")) {
                            naCb.setEnabled(true);
                        } else {
                            naCb.setEnabled(false);
                            naCb.setSelectedItem(null);
                        }
                    }
                }
            });        
        nsCb.removeAllItems();
        nsCb.addItem("Not Applicable");  // NA in database
        nsCb.addItem("TBD");             // TBD in database
        nsCb.addItem("ANL Inspection Required");             // ANL in database
        nsCb.addItem("NRTL Approved");   // NRTL in database
        nsPanel.add(nsCb);
        stepComponent.add(nsPanel);

        // NRTL Agency text input
        JLabel naLabel = new JLabel("NRTL Agency:",SwingConstants.LEFT);
        JPanel naPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        naPanel.setBackground(Color.white);
        naPanel.add(naLabel);
        naCb.setEditable(true);
        naCb.removeAllItems();
        naCb.addItem("Reputable Manufacturer");
        naCb.addItem("UL");
        naPanel.add(naCb);
        stepComponent.add(naPanel);

        prepareRendering();

        return stepComponent;
    }

    public void prepareRendering() {
        // pre-load components with any choices that have been made already
        if (stepComponent != null) {
            ComponentType ct = dataModel.getComponentType();
            descTextArea.setText(ct.getVerboseDescription());
            cpCb.setSelectedItem(ct.getChcContact());
            if (ct.getBeamlineInterest() != null)
                biCb.setSelectedItem(ct.getBeamlineInterest());

            ComponentTypeStatus cts = ct.getComponentTypeStatus();
            String nrtlStatus = cts.getNrtlStatus();
            if (nrtlStatus != null) {
                // map from database strings to pulldown strings
                if (nrtlStatus.equalsIgnoreCase("NA") || nrtlStatus.equalsIgnoreCase("NH")) {
                    nsCb.setSelectedItem("Not Applicable");
                } else if (nrtlStatus.equalsIgnoreCase("TBD")) {
                    nsCb.setSelectedItem("TBD");
                } else if (nrtlStatus.equalsIgnoreCase("ANL")) {
                    nsCb.setSelectedItem("ANL Inspection Required");
                } else if (nrtlStatus.equalsIgnoreCase("NRTL")) {
                    nsCb.setSelectedItem("NRTL Approved");
                } else {
                    nsCb.setSelectedItem("Not Applicable");
                }
                if (nrtlStatus.equalsIgnoreCase("NRTL")) {
                    naCb.setEnabled(true);
                } else {
                    naCb.setEnabled(false);
                    naCb.setSelectedItem(null);
                }
                
            }
            if (cts.getNrtlAgency() != null) {
                naCb.setSelectedItem(cts.getNrtlAgency());
            } else {
                naCb.setSelectedItem(null);
            }
        }
    }

    public Step[] getPendingSteps() {
        ComponentType ct = dataModel.getComponentType();
        ComponentTypeStatus cts = ct.getComponentTypeStatus();

        // transfer data from widgets to dataModel
        ct.setVerboseDescription(descTextArea.getText());
        ct.setChcContact((Person)cpCb.getSelectedItem());
        ct.setBeamlineInterest((BeamlineInterest)biCb.getSelectedItem());
        String nrtlStatus = (String)nsCb.getSelectedItem();

        // map from pulldown strings to database strings
        if (nrtlStatus.equalsIgnoreCase("Not Applicable")) {
            cts.setNrtlStatus("NA");
        } else if (nrtlStatus.equalsIgnoreCase("TBD")) {
            cts.setNrtlStatus("TBD");
        } else if (nrtlStatus.equalsIgnoreCase("ANL Inspection Required")) {
            cts.setNrtlStatus("ANL");
        } else if (nrtlStatus.equalsIgnoreCase("NRTL Approved")) {
            cts.setNrtlStatus("NRTL");
        } 
        
        String naText = (String)naCb.getSelectedItem();
        cts.setNrtlAgency(naText);
        

        // return the next step that should follow
        if (dataModel.getComponentType().getIsBaseType()) {
            return new Step[] {new NewTypeWizardStep10(dataModel)};
        } else {
            return new Step[] {new NewTypeWizardStep04(dataModel)};
        }
    }

}
