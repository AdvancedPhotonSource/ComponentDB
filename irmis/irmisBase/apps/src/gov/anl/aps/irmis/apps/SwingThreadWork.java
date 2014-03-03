package gov.anl.aps.irmis.apps;

import java.awt.*;
import javax.swing.*;

/**
 * This is a variation of Yexin Chen's original code which runs a lengthy task
 * in a separate thread, but disables the UI during this time (basically, his
 * extension of SwingWorker). I've modified it further to work with the QueuedExecutor
 * from the util.concurrent library. This avoids the repeated thread creation of
 * the former implementation.
 *
 * @author Yexin Chen
 * @author Claude Saunders
 *
 */
public abstract class SwingThreadWork implements Runnable {
    
	private GlassPane glassPane;
	private RootPaneContainer aComponent = null;
    
    /**
     * Create a block of work to be performed in a background thread. The
     * work in doNonUILogic will be queued up on a single pre-created background
     * thread. When this work is done, the swing component will eat all
     * UI events, and the cursor will become a busy hourglass for the duration
     * of the work. Afterwards, the work in doUIUpdateLogic will be queued up
     * on the standard Swing event queue.
     *
     * @param aComponent a reference to the UI component that's directly using
     * SwingThreadWork
     */
    public SwingThreadWork(RootPaneContainer aComponent) {
        setAComponent(aComponent);
    }

    /**
     * Alternate constructor if there is no associated UI component.
     */
    public SwingThreadWork() {
    }

    /**
     * Standard Runnable run method.
     */
    public void run() {
        activateGlassPane();
        try {
            doNonUILogic();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        deactivateGlassPane();
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    finished();
                }
            });
    }

    /**
     * Activate the capabilities of glasspane
     * 
     */
    private void activateGlassPane() {
        if (getAComponent() != null) {
            // Mount the glasspane on the component window
            GlassPane aPane = GlassPane.mount(getAComponent(), true);
            // keep track of the glasspane as an instance variable
            setGlassPane(aPane);
            
            if (getGlassPane() != null) {
                // Start interception UI interactions
                getGlassPane().setVisible(true);
            }
        }
    }

    /**
     * Deactivate the glasspane
     * 
     */
    private void deactivateGlassPane() {
        if (getAComponent() != null) {
            if (getGlassPane() != null) {
                // Stop UI interception
                getGlassPane().setVisible(false);
            }
        }
    }

    /**
     * This method will be implemented by the inner class of SwingThreadWork.
     * It should only consist of the logic that's unrelated to UI
     *
     * @throws java.lang.RuntimeException thrown if there are any errors in the non-ui logic
     */
    protected abstract void doNonUILogic() throws RuntimeException;

    /**
     * This method will be implemented by the inner class of SwingThreadWork.
     * It should only consist of the logic that's related to UI updating, after
     * the doNonUILogic() method is done. 
     *
     * @throws java.lang.RuntimeException thrown if there are any problems in the ui update logic
     */
    protected abstract void doUIUpdateLogic() throws RuntimeException;


    /**
     * This logic is done on the Swing event thread after background thread is done.
     */
    protected void finished() {
        try {
            // deactivateGlassPane();
            doUIUpdateLogic();
        }
        catch (RuntimeException e) {
            // Do nothing, simply cleanup below
            System.out.println("SwingWorker error " + e);
            e.printStackTrace(System.out);
        }
        finally {
            // Allow original component to get the focus
            //if (getAComponent() != null) {
            //getAComponent().requestFocus();
            //}
        }
    }

    /**
     * Getter method
     * 
     * @return RootPaneContainer
     */
    protected RootPaneContainer getAComponent() {
        return aComponent;
    }

    /**
     * Getter method
     * 
     * @return GlassPane
     */
    protected GlassPane getGlassPane() {
        return glassPane;
    }

    /**
     * Setter method
     * 
     * @param newAComponent a component that implements RootPaneContainer
     */
    protected void setAComponent(RootPaneContainer newAComponent) {
        aComponent = newAComponent;
    }

    /**
     * Setter method
     * 
     * @param newGlassPane GlassPane
     */
    protected void setGlassPane(GlassPane newGlassPane) {
        glassPane = newGlassPane;
    }
}
