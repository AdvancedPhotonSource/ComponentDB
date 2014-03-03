/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package CFWDemo;

import net.javaprog.ui.wizard.Wizard;
import net.javaprog.ui.wizard.DefaultWizardModel;
import net.javaprog.ui.wizard.WizardModel;
import net.javaprog.ui.wizard.Step;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComponent;

public class DemoWizard extends Wizard {

    private static DemoWizard wizard;
    private static DemoWizardModel dataModel;

    /**
     * Puts up a demo wizard dialog, which remains the focus until finshed or cancelled.
     *
     */
    public static DemoWizardModel showWizard() {
        dataModel = new DemoWizardModel();

        // define the initial steps, although sequence may change dynamically
        WizardModel wizardModel = new DefaultWizardModel(new Step[] {
            new DemoWizardWelcomeStep(),
            new DemoWizardDataInput1Step(dataModel)
        });
        
        String title = "Demo Wizard";
        wizard = new DemoWizard(wizardModel, title);
        wizard.pack();
        wizard.setLocationRelativeTo(null);
        wizard.setVisible(true);
        return dataModel;
    }

    private DemoWizard(WizardModel wizardModel, String title) {
        super(wizardModel, title);
    }    

}
