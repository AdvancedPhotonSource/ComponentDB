/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package CFWDemo;

import net.javaprog.ui.wizard.AbstractStep;
import net.javaprog.ui.wizard.Step;
import net.javaprog.ui.wizard.StepModelCustomizer;

import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JTextField;

public class DemoWizardDataInput1Step extends AbstractStep implements StepModelCustomizer {

    private DemoWizardModel dataModel;
    private JTextField textField;

    public DemoWizardDataInput1Step(DemoWizardModel dataModel) {
        super("Data Input Step 1","Please enter the requested data.");
        this.dataModel = dataModel;
    }

    protected JComponent createComponent() {
        // return component to be shown to the user
        JPanel stepComponent = new JPanel(new FlowLayout());
        stepComponent.add(new JLabel("Enter a string:"));
        textField = new JTextField(20);
        stepComponent.add(textField);
        return stepComponent;
    }

    public void prepareRendering() {
    }

    public Step[] getPendingSteps() {
        // get data from components and put in dataModel
        dataModel.setJunk(textField.getText());

        // decide which step should follow

        // return the next step that should follow
        return new Step[] {new DemoWizardFinishStep()};
    }

}
