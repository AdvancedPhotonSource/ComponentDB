/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw.wizard;

import net.javaprog.ui.wizard.Wizard;
import net.javaprog.ui.wizard.DefaultWizardModel;
import net.javaprog.ui.wizard.WizardModel;
import net.javaprog.ui.wizard.WizardModelEvent;
import net.javaprog.ui.wizard.WizardModelListener;
import net.javaprog.ui.wizard.Step;

import java.util.List;
import java.awt.Frame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.component.ComponentType;
import gov.anl.aps.irmis.persistence.component.Component;

/**
 * Wizard for entering a new IRMIS Component.
 */
public class NewComponentWizard extends Wizard {

    private static NewComponentWizard wizard;
    private static NewComponentWizardModel dataModel;

    /**
     * Puts up the wizard dialog, which remains the focus until finshed or cancelled.
     * This wizard is for creating a new component of a chosen type.
     *
     * @param componentTypes list of possible component types
     * @param frameComp swing component that wizard window is to be a child of, null ok
     * @param parentComponent intended parent of this new component we are forming
     * @param hierarchy which hierarchy we are being placed in
     * @return the wizard data model, filled out or with cancelled boolean set
     */
    public static NewComponentWizardModel showWizard(java.awt.Component frameComp, 
                                                     List componentTypes, 
                                                     Component parentComponent,
                                                     int hierarchy) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dataModel = new NewComponentWizardModel(componentTypes, parentComponent, hierarchy);

        // define the initial steps, although sequence may change dynamically
        WizardModel wizardModel = new DefaultWizardModel(new Step[] {
            new NewComponentWizardStepWelcome(dataModel),
            new NewComponentWizardStep01(dataModel)
        });

        NewComponentWizardModelListener wml = new NewComponentWizardModelListener(dataModel);
        wizardModel.addWizardModelListener(wml);
        
        String title = "New IRMIS Component Wizard";
        wizard = new NewComponentWizard(frame, wizardModel, title);
        wizard.pack();
        wizard.setLocationRelativeTo(null);
        wizard.setVisible(true);
        return dataModel;
    }

    private NewComponentWizard(Frame frame, WizardModel wizardModel, String title) {
        super(frame, wizardModel, title);
    }    

    private static class NewComponentWizardModelListener implements WizardModelListener {

        private NewComponentWizardModel dataModel;
        public NewComponentWizardModelListener(NewComponentWizardModel m) {
            dataModel = m;
        }

        public void stepShown(WizardModelEvent e) {};

        public void wizardCanceled(WizardModelEvent e) {
            dataModel.setCancelled(true);
        }
        
        public void wizardFinished(WizardModelEvent e) {
            dataModel.setCancelled(false);
        }

        public void wizardModelChanged(WizardModelEvent e) {};
    }

}
