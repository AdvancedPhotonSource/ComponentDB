/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package CFWDemo;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Application data model for Demo application. In a real application, you would
 * put all your application "state" in here. For example, results from database
 * queries would go here, or input from text widgets that you want to store.
 */
public class DemoModel {

    /**
     *  List of others interested in changes to this data model.
     *  This is what separates the "view" from the "model".
     *  For now, the only listener will be the DemoWindow.
     */
    private List demoModelListenersList = new ArrayList();

    // this is your application data
    private StringBuffer textBlock =  new StringBuffer();
    private List iocList = new ArrayList();

	/**
	 * Do nothing constructor
	 */
	public DemoModel() {
	}

    /*****************************************************************/
    /**** Data Model Listener Support ********************************/
    /*****************************************************************/

    /**
     * This allows dynamically adding a class who wants to be notified
     * whenever this data model changes. The class you add must implement
     * DemoModelListener.
     */
    public void addDemoModelListener(DemoModelListener l) {
        demoModelListenersList.add(l);
    }

    /**
     * This requests that all listeners be notified of change. This is a
     * general notification, since the modelEvent is null. Typically you
     * would use the method below instead, and supply a specific event
     * that would tell the listener what changed in the model.
     */
    public void notifyDemoModelListeners() {
        notifyDemoModelListeners(null);
    }

    /**
     * Notify registered listeners of the given <code>DemoModelEvent</code>.
     *
     * @param modelEvent encapsulates one of several possible events
     */
    public void notifyDemoModelListeners(DemoModelEvent modelEvent) {
        Iterator it = demoModelListenersList.iterator();
        while (it.hasNext()) {
            DemoModelListener l = (DemoModelListener)it.next();
            l.modified(modelEvent);
        }
    }

    /*****************************************************************/
    /**** Data Accessors *********************************************/
    /*****************************************************************/

    public StringBuffer getTextBlock() {
        return textBlock;
    }

    public void setTextBlock(StringBuffer value) {
        textBlock = value;
    }

    public List getIocList() {
        return iocList;
    }

    public void setIocList(List list) {
        iocList = list;
    }

}
