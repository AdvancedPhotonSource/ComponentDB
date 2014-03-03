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
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentType;

// IRMIS service layer
import gov.anl.aps.irmis.service.component.ComponentTypeService;
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Begins by asking for the component type name.
 */
public class NewTypeWizardStep01 extends AbstractStep implements StepModelCustomizer {

    private NewTypeWizardModel dataModel;
    private JPanel stepComponent;
    private JTextField textField;
    private JLabel nameErrorLabel;
    private JRadioButton copyRB = null;

    public NewTypeWizardStep01(NewTypeWizardModel dataModel) {
        super("Name","desc");
        this.dataModel = dataModel;
    }

    protected JComponent createComponent() {
        stepComponent = new JPanel();
        stepComponent.setLayout(new BoxLayout(stepComponent,BoxLayout.PAGE_AXIS));
        stepComponent.setBackground(Color.white);
        
        // intro text
        JTextArea introText = new JTextArea(4,35);
        introText.setEditable(false);
        introText.setLineWrap(true);
        introText.setWrapStyleWord(true);
        if (dataModel.getEditMode()) {
            introText.append("You can edit the component type name if you want, although ");
            introText.append("this is not common. You will be notified if your choice ");
            introText.append("is not unique or too long.\n");
        } else {
            introText.append("Give a unique name for the new component type. This is ");
            introText.append("typically the vendor specific model name/number. You will ");
            introText.append("be notified if your choice is not unique or too long. ");
            introText.append("You can create it from scratch, or modify a copy of an ");
            introText.append("existing component type.\n");
        }

        introText.setMaximumSize(introText.getPreferredSize());
        stepComponent.add(introText);
        
        // component type name
        JLabel nameLabel = new JLabel("Component Type Name:",SwingConstants.LEFT);
        JPanel nameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameLabelPanel.setBackground(Color.white);
        nameLabelPanel.add(nameLabel);
        stepComponent.add(nameLabelPanel);
        
        // if data model indicates error
        nameErrorLabel = new JLabel();
        nameErrorLabel.setForeground(Color.red);
        JPanel nameErrorLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameErrorLabelPanel.setBackground(Color.white);
        nameErrorLabelPanel.add(nameErrorLabel);
        stepComponent.add(nameErrorLabelPanel);
        
        textField = new JTextField(30);
        JPanel textFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textFieldPanel.setBackground(Color.white);
        textFieldPanel.add(textField);
        stepComponent.add(textFieldPanel);

        
        if (!dataModel.getEditMode()) {
            // radio buttons to allow creating from scratch or copying existing defn.
            JPanel scratchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            scratchPanel.setBackground(Color.white);
            JRadioButton scratchRB = new JRadioButton("create from scratch");
            scratchRB.setBackground(Color.white);
            scratchRB.setSelected(true);
            scratchPanel.add(scratchRB);
            
            JPanel copyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            copyPanel.setBackground(Color.white);
            copyRB = new JRadioButton("copy existing type");
            copyRB.setBackground(Color.white);
            copyPanel.add(copyRB);
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(scratchRB);
            buttonGroup.add(copyRB);
            
            stepComponent.add(scratchPanel);
            stepComponent.add(copyPanel);
        }
        
        // add filler
        stepComponent.add(Box.createVerticalStrut(300));

        prepareRendering();

        return stepComponent;
    }

    public void prepareRendering() {
        if (stepComponent != null) {
            ComponentType ct = dataModel.getComponentType();
            if (textField.getText() == null || textField.getText().length() == 0)
                textField.setText(ct.getComponentTypeName());

            String err = dataModel.getComponentTypeNameError();
            nameErrorLabel.setText(err);
        }
    }

    public Step[] getPendingSteps() {
        // get component type name
        String componentTypeName = textField.getText();
        
        if (componentTypeName.length() == 0) {
            dataModel.setComponentTypeNameError("You must give it a name");
            return null;

        } else {
            try {
                if (ComponentTypeService.isComponentTypeNameValid(componentTypeName) ||
                    (dataModel.getEditMode() && 
                     dataModel.getOriginalComponentTypeName().equals(componentTypeName))) {
                    dataModel.getComponentType().setComponentTypeName(componentTypeName);
                    dataModel.setComponentTypeNameError(null);
                    if (copyRB != null && copyRB.isSelected())
                        return new Step[] {new NewTypeWizardStep01a(this.dataModel)};
                    else
                        return new Step[] {new NewTypeWizardStep02(this.dataModel)};
                } else {
                    dataModel.setComponentTypeNameError("This component type already exists!");
                    return null;
                }                
            } catch (IRMISException ie) {
                dataModel.setComponentTypeNameError("Unable to access database. Try again later.");
                return null;                
            }
        }
    }

}
