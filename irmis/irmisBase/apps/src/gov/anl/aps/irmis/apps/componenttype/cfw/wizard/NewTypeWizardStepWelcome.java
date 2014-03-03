/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenttype.cfw.wizard;

import net.javaprog.ui.wizard.AbstractStep;
import net.javaprog.ui.wizard.Step;
import net.javaprog.ui.wizard.StepModelCustomizer;

import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JTextArea;

public class NewTypeWizardStepWelcome extends AbstractStep implements StepModelCustomizer {

    private NewTypeWizardModel dataModel;

    public NewTypeWizardStepWelcome(NewTypeWizardModel dataModel) {
        super("Welcome","desc");
        this.dataModel = dataModel;
    }

    protected JComponent createComponent() {
        // Provide introductory explanation of this wizard
        JPanel stepComponent = new JPanel();
        stepComponent.setBackground(Color.white);
        
        JTextArea introText = new JTextArea(10,30);
        introText.setEditable(false);
        introText.setLineWrap(true);
        introText.setWrapStyleWord(true);
        if (dataModel.getEditMode()) {
            if (dataModel.getComponentType().getIsBaseType()) {
                introText.append("The component type you have selected for edit is ");
                introText.append("considered a base type. You can only edit a few ");
                introText.append("of its properties such as stock and spare quantity. ");

            } else if (dataModel.getComponentType().getComponentTypeStatus().getInstantiated()){
                introText.append("You are about to begin the process of editing an existing ");
                introText.append("component type. You can only edit some of the properties, ");
                introText.append("since others could potentially invalidate any actual ");
                introText.append("components created based on this component type. ");
            } else {
                introText.append("You are about to begin the process of editing an existing ");
                introText.append("component type. You can edit any of the properties, since ");
                introText.append("no actual components have been created yet based on this ");
                introText.append("type. ");
            }

        } else {
            introText.append("You are about to begin the process of adding a new ");
            introText.append("component type. You will begin by naming it and filling ");
            introText.append("out a number of basic properties. Then you will proceed ");
            introText.append("to provide the logical control, housing, and power interfaces ");
            introText.append("that define how this component type can relate to others. ");
            introText.append("Lastly, you will enter the physical ports and pins (if any).");
        }
        introText.append("\n\nClick Next> to begin...");

        stepComponent.add(introText);
        return stepComponent;
    }

    public void prepareRendering() {
    }

    public Step[] getPendingSteps() {

        if (dataModel.getComponentType().getIsBaseType()) {
            return new Step[] {new NewTypeWizardStep02(dataModel)};

        } else {
            return new Step[] {new NewTypeWizardStep01(dataModel)};
        }
    }

}
