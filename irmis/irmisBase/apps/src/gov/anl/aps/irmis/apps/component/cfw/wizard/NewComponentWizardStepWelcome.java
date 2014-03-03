/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw.wizard;

import net.javaprog.ui.wizard.AbstractStep;
import net.javaprog.ui.wizard.Step;
import net.javaprog.ui.wizard.StepModelCustomizer;

import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class NewComponentWizardStepWelcome extends AbstractStep implements StepModelCustomizer {

    private NewComponentWizardModel dataModel;
    private JPanel stepComponent;

    public NewComponentWizardStepWelcome(NewComponentWizardModel dataModel) {
        super("Welcome","desc");
        this.dataModel = dataModel;
    }

    protected JComponent createComponent() {
        // Provide introductory explanation of this wizard
        stepComponent = new JPanel();
        stepComponent.setLayout(new BoxLayout(stepComponent,BoxLayout.PAGE_AXIS));
        stepComponent.setBackground(Color.white);
        
        JTextArea introText = new JTextArea(10,30);
        introText.setEditable(false);
        introText.setLineWrap(true);
        introText.setWrapStyleWord(true);
        introText.append("You are about to create a new component instance. You will ");
        introText.append("first pick a component type, and then enter a component name, ");
        introText.append("serial number, etc...");
        introText.append("\n\n");
        introText.append("Your new component will be placed in the housing hierarchy. ");
        introText.append("If possible, we will attempt to automatically place it in the ");
        introText.append("control and power hierarchies for you. If we are unable to, ");
        introText.append("you should manually add your new component to the control and ");
        introText.append("power hierarchies.");
 
        introText.append("\n\nClick Next> to continue...");

        stepComponent.add(introText);

        prepareRendering();
        return stepComponent;
    }

    public void prepareRendering() {
    }

    public Step[] getPendingSteps() {
        return new Step[] {new NewComponentWizardStep01(dataModel)};
    }

}
