/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/

package gov.anl.aps.irmis.apps.componenthistory.echo2;

// Echo2 
import nextapp.echo2.app.ContentPane;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.Extent;

// EchoPointNG
import echopointng.ContainerEx;

/**
 * IRMIS Component History Tab Abstract Base Class. Extend this to develop the contents
 * of one of the component history tabs.
 */
public abstract class ComponentHistoryTab extends Column {

    // the top level MVC (model, view, controller) objects
    private ComponentHistoryController controller;
    private ComponentHistoryModel model;

    private ContentPane mainWindowPane;
    private ContainerEx tableContainerEx;
    private Row buttonRow;

    public ComponentHistoryTab(ContentPane mainWindowPane, ComponentHistoryController controller) {

        this.mainWindowPane = mainWindowPane;
        this.controller = controller;
        model = controller.getModel();
        model.addComponentHistoryModelListener(new ComponentHistoryModelListener() {
                public void modified(ComponentHistoryModelEvent e) {
                    updateView(e);
                }
            });

        setStyleName("Tab.Column");

        // Build container of fixed size (used to contain table added by subclass)
        tableContainerEx = new ContainerEx();
        tableContainerEx.setStyleName("Default.ContainerEx");
        tableContainerEx.setHeight(new Extent(155));

        add(tableContainerEx);
        
        // Build button row that subclass will fill
        buttonRow = new Row();
        buttonRow.setStyleName("Button.Row");
        add(buttonRow);
        
    }

    protected void addToTableContainer(Component c) {
        tableContainerEx.add(c);
    }

    protected void addToButtonRow(Component c) {
        buttonRow.add(c);
    }

    protected ContentPane getMainWindowPane() {
        return mainWindowPane;
    }

    protected ComponentHistoryController getController() {
        return controller;
    }

    protected ComponentHistoryModel getModel() {
        return model;
    }

    /**
     * Invoke subclass implementation to update it's particular view.
     */
    public abstract void updateView(ComponentHistoryModelEvent event);

}

