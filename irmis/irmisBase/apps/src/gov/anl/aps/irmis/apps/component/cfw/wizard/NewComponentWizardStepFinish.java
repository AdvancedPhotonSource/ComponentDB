/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.component.cfw.wizard;

import net.javaprog.ui.wizard.AbstractStep;
import net.javaprog.ui.wizard.Step;

import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JComponent;

public class NewComponentWizardStepFinish extends AbstractStep {

    private NewComponentWizardModel dataModel;

    public NewComponentWizardStepFinish(NewComponentWizardModel dm) {
        super("Finish","You are done.");
        dataModel = dm;
        setCanFinish(true);
    }

    protected JComponent createComponent() {
        // return component to be shown to the user
        JPanel stepComponent = new JPanel();
        stepComponent.setLayout(new BoxLayout(stepComponent,BoxLayout.PAGE_AXIS));
        stepComponent.setBackground(Color.white);

        JTextArea introText = new JTextArea(10,30);
        introText.setEditable(false);
        introText.setLineWrap(true);
        introText.setWrapStyleWord(true);
        introText.append("I will now add the component for you. You can move the ");
        introText.append("component location up and down with arrow buttons, and  ");
        introText.append("assign it a physical slot, logical card, or outlet ");
        introText.append("using the text box below each tree. ");

        if (dataModel.getCanAddToControl()) {
            introText.append("\n\n");
            introText.append("Note: the control parent is assumed to be the same as ".toUpperCase());
            introText.append("the housing parent. If wrong, please correct manually.".toUpperCase());
        }
        if (dataModel.getCanAddToPower()) {
            introText.append("\n\n");
            introText.append("Note: the power parent is assumed to be the same as ".toUpperCase());
            introText.append("the housing parent. If wrong, please correct manually.".toUpperCase());
        }

        introText.append("\n\nClick Finish> to close...");

        stepComponent.add(introText);

        return stepComponent;
    }

    public void prepareRendering() {
        dataModel.setCancelled(false);
    }

}
