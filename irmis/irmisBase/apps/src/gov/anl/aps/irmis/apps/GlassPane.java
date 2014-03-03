package gov.anl.aps.irmis.apps;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

/**
 * This is the glass pane class that intercepts screen interactions during system busy states.
 * Modified a fair bit from Y. Chen's original implementation.
 *
 * @author Yexin Chen
 * @author Claude Saunders
 */
public class GlassPane extends JComponent implements AWTEventListener {

    private JRootPane theRootPane;

    /**
     * GlassPane constructor comment.
     * @param Container a 
     */
    protected GlassPane(JRootPane theRootPane) {

        // add adapters that do nothing for keyboard and mouse actions
        addMouseListener(new MouseAdapter() {
            });
	
        addKeyListener(new KeyAdapter() {
            });
	
        this.theRootPane = theRootPane;
    }

    /**
     * Receives all key events in the AWT and processes the ones that originated from the
     * current window with the glass pane.
     *
     * @param event the AWTEvent that was fired
     */
    public void eventDispatched(AWTEvent event) {

        Object source = event.getSource();
        
        // discard the event if its source is not from the correct type
        boolean sourceIsComponent = (event.getSource() instanceof Component);

        if ((event instanceof KeyEvent) && sourceIsComponent) {

            // If the event originated from the window w/glass pane, consume the event
            if ((SwingUtilities.getRootPane((Component)source) == theRootPane)) {
                
                ((KeyEvent) event).consume();
            }
        }
    }
    
    /**
     * Finds the glass pane that is related to the specified component.
     * 
     * @param startComponent the component used to start the search for the glass pane
     * @param create a flag whether to create a glass pane if one does not exist
     * @return GlassPane
     */
    public synchronized static GlassPane mount(RootPaneContainer startComponent, boolean create) {

        JRootPane aJRootPane = null;
        
        aJRootPane = startComponent.getRootPane();
        
        if (aJRootPane != null) {
            
            // Retrieve an existing GlassPane if old one already exist or create a new one, otherwise return null
            if ((aJRootPane.getGlassPane() != null) && 
                (aJRootPane.getGlassPane() instanceof GlassPane)) {
                
                // Sets the mouse cursor to hourglass mode
                aJRootPane.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                return (GlassPane) aJRootPane.getGlassPane();
                
            } else if (create) {
                
                GlassPane aGlassPane = new GlassPane(aJRootPane);
                aJRootPane.setGlassPane(aGlassPane);
                // Sets the mouse cursor to hourglass mode
                aGlassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                return aGlassPane;
                
            } else {
                
                return null;
            }
            
        } else {
            return null;
        }
    }


    /**
     * Sets the glass pane as visible or invisible. The mouse cursor will be set accordingly.
     */
    public void setVisible(boolean value) {
        if (value) {
            // Start receiving all events and consume them if necessary
            Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);

            this.requestFocus();

            // Activate the glass pane capabilities
            super.setVisible(value);

        } else {
            // Stop receiving all events
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
            
            // Deactivate the glass pane capabilities
            super.setVisible(value);
            
            theRootPane.setCursor(null);
        }
    }
}
