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
 * Step02a gets info regarding the standing inventory (spares, stock).
 * 
 */
public class NewTypeWizardStep02a extends AbstractStep implements StepModelCustomizer {

    private NewTypeWizardModel dataModel;
    private JPanel stepComponent;
    private JSpinner stockSpinner;
    private JSpinner spareSpinner;
    private JTextArea spareLocationTextArea;

    public NewTypeWizardStep02a(NewTypeWizardModel dataModel) {
        super("Inventory Info","desc");
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

        // status (stock and spare quantity)
        JLabel stockLabel = new JLabel("In Stock:",SwingConstants.LEFT);
        JLabel spareLabel = new JLabel("   Spares:",SwingConstants.LEFT);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.white);
        stockSpinner = 
            new JSpinner(new SpinnerNumberModel(0,0,100,1));
        spareSpinner = 
            new JSpinner(new SpinnerNumberModel(0,0,100,1));
        statusPanel.add(stockLabel);
        statusPanel.add(stockSpinner);
        statusPanel.add(spareLabel);
        statusPanel.add(spareSpinner);
        stepComponent.add(statusPanel);

        // spares location text area (editable)
        JLabel spareLocLabel = new JLabel("Spares Location:",SwingConstants.LEFT);
        JPanel spareLocPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        spareLocPanel.setBackground(Color.white);
        spareLocPanel.add(spareLocLabel);
        spareLocationTextArea = new JTextArea(2,20);
        if (dataModel.getComponentType().getIsBaseType())
            spareLocationTextArea.setEnabled(false);
        spareLocationTextArea.setBorder(BorderFactory.createLoweredBevelBorder());
        spareLocPanel.add(spareLocationTextArea);
        stepComponent.add(spareLocPanel);

        prepareRendering();

        return stepComponent;
    }

    public void prepareRendering() {
        // pre-load components with any choices that have been made already
        if (stepComponent != null) {
            ComponentType ct = dataModel.getComponentType();

            ComponentTypeStatus cts = ct.getComponentTypeStatus();
            if (cts != null) {
                stockSpinner.getModel().setValue(new Integer(cts.getStockQuantity()));
                spareSpinner.getModel().setValue(new Integer(cts.getSpareQuantity()));
                spareLocationTextArea.setText(cts.getSpareLocation());
            }
        }
    }

    public Step[] getPendingSteps() {

        // transfer data from widgets to dataModel
        ComponentType ct = dataModel.getComponentType();

        ct.getComponentTypeStatus()
            .setStockQuantity(((Integer)stockSpinner.getModel().getValue()).intValue());
        ct.getComponentTypeStatus()
            .setSpareQuantity(((Integer)spareSpinner.getModel().getValue()).intValue());

        String spareLocText = spareLocationTextArea.getText();
        ct.getComponentTypeStatus().setSpareLocation(spareLocText);

        // decide which step should follow

        // go to next step
        return new Step[] {new NewTypeWizardStep03(dataModel)};
    }

}
