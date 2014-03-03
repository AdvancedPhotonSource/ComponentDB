/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenttype.cfw.wizard;

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
import gov.anl.aps.irmis.persistence.component.ComponentPortTemplate;

/**
 * Wizard for entering a new IRMIS Component Type. Step flow is roughly
 * as follows: <pre>
 *         StepWelcome
 *            |
 *            V
 *         Step01 (name)
 *            |
 *            V
 *         Step02 (general info 1)
 *            |
 *            V
 *         Step02a (inventory info)
 *            |
 *            V
 *         Step03 (general info 2)
 *            |
 *            V
 *         Step03a (general info 3)
 *            |
 *            V
 *         Step04 (logical interfaces)
 *            |
 *            V
 *      |->Step05 (ports) ------------->Step10 (commit)
 *      |     |                           
 *      |     V                          
 *      |  Step06 (add port)         
 *      |     |
 *      |     V
 *      ---Step07 (add pins)
 *
 * </pre>
 */
public class NewTypeWizard extends Wizard {

    private static NewTypeWizard wizard;
    private static NewTypeWizardModel dataModel;

    /**
     * Puts up the wizard dialog, which remains the focus until finshed or cancelled.
     * This version is for creating a new component type.
     *
     * @param compTypeList list of possible component types
     * @param frameComp component that wizard window is to be a child of, null ok
     * @return the wizard data model, filled out or with cancelled boolean set
     */
    public static NewTypeWizardModel showWizard(java.awt.Component frameComp,
                                                List compTypeList) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dataModel = new NewTypeWizardModel(compTypeList);

        // define the initial steps, although sequence may change dynamically
        WizardModel wizardModel = new DefaultWizardModel(new Step[] {
            new NewTypeWizardStepWelcome(dataModel),
            new NewTypeWizardStep01(dataModel)
        });
        NewTypeWizardModelListener wml = new NewTypeWizardModelListener(dataModel);
        wizardModel.addWizardModelListener(wml);
        
        String title = "New IRMIS Component Type Wizard";
        wizard = new NewTypeWizard(frame, wizardModel, title);
        wizard.pack();
        wizard.setLocationRelativeTo(null);
        wizard.setSize(500,400);
        wizard.setVisible(true);
        return dataModel;
    }

    /**
     * Puts up the wizard dialog, which remains the focus until finshed or cancelled.
     * This version is for editing an existing component type.
     *
     */
    public static  NewTypeWizardModel showWizard(java.awt.Component frameComp,
                                                 ComponentType ct) {
        return showWizard(frameComp, ct, 0, null);
    }

    /**
     * Puts up the wizard dialog at a given starting point.
     */
    public static NewTypeWizardModel showWizard(java.awt.Component frameComp,
                                                ComponentType ct, int startPoint,
                                                ComponentPortTemplate cpt) {

        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        if (startPoint == 5)  
            dataModel = new NewTypeWizardModel(ct, true);  // addPortMode true
        else
            dataModel = new NewTypeWizardModel(ct, false);

        WizardModel wizardModel = null;
        if (startPoint == 0) {
            // define the initial steps, although sequence may change dynamically
            wizardModel = new DefaultWizardModel(new Step[] {
                new NewTypeWizardStepWelcome(dataModel),
                new NewTypeWizardStep02(dataModel)
            });
        } else if (startPoint == 5) {
            wizardModel = new DefaultWizardModel(new Step[] {
                new NewTypeWizardStep05(dataModel),
                new NewTypeWizardStep06(dataModel)
            });
        } else if (startPoint == 7) {
            dataModel.setSelectedPort(cpt);
            dataModel.setEditPinsMode(true);
            wizardModel = new DefaultWizardModel(new Step[] {
                new NewTypeWizardStep07(dataModel),
                new NewTypeWizardStep10(dataModel)
            });
        } else {
            wizardModel = new DefaultWizardModel(new Step[] {
                new NewTypeWizardStepWelcome(dataModel),
                new NewTypeWizardStep02(dataModel)
            });
        }

        NewTypeWizardModelListener wml = new NewTypeWizardModelListener(dataModel);
        wizardModel.addWizardModelListener(wml);
        
        String title = "Edit IRMIS Component Type Wizard";
        wizard = new NewTypeWizard(frame, wizardModel, title);
        wizard.pack();
        wizard.setLocationRelativeTo(null);
        wizard.setSize(500,400);
        wizard.setVisible(true);
        return dataModel;
    }

    private NewTypeWizard(Frame frame, WizardModel wizardModel, String title) {
        super(frame, wizardModel, title);
    }    

    private static class NewTypeWizardModelListener implements WizardModelListener {

        private NewTypeWizardModel dataModel;
        public NewTypeWizardModelListener(NewTypeWizardModel m) {
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
