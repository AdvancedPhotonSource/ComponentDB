/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package CFWDemo;

import net.javaprog.ui.wizard.AbstractStep;
import net.javaprog.ui.wizard.Step;
import net.javaprog.ui.wizard.StepModelCustomizer;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComponent;

public class DemoWizardWelcomeStep extends AbstractStep {

    public DemoWizardWelcomeStep() {
        super("Welcome","desc");
    }

    protected JComponent createComponent() {
        // return component to be shown to the user
        JPanel stepComponent = new JPanel();
        stepComponent.add(new JLabel("This is the start of the demo wizard. Please click Next>"));
        return stepComponent;
    }

    public void prepareRendering() {
    }

}
