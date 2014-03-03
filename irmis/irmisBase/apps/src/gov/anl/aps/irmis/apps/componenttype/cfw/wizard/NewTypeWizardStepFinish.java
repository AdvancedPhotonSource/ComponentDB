/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenttype.cfw.wizard;

import net.javaprog.ui.wizard.AbstractStep;
import net.javaprog.ui.wizard.Step;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComponent;

public class NewTypeWizardStepFinish extends AbstractStep {

    private NewTypeWizardModel dataModel;

    public NewTypeWizardStepFinish(NewTypeWizardModel dm) {
        super("Finish","You are done.");
        dataModel = dm;
        setCanFinish(true);
    }

    protected JComponent createComponent() {
        // return component to be shown to the user
        JPanel stepComponent = new JPanel();
        stepComponent.add(new JLabel("You're done. Click Finish> to close."));
        return stepComponent;
    }

    public void prepareRendering() {
        dataModel.setCancelled(false);
    }

}
