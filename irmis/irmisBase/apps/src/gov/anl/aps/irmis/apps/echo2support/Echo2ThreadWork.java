/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.echo2support;

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.TaskQueueHandle;


/**
 * This is a base class that can be used to define work that is to be
 * run in a background thread. This Echo2ThreadWork is identical in
 * design to the well-known Java SwingThreadWork technique. The idea
 * is to create an instance of this class, overriding the doNonUILogic
 * and doUIUpdateLogic methods. The former should contain your long
 * running logic (ie. a database query), and the latter will contain
 * code for updating the Echo2 user interface via the Echo2 TaskQueue
 * technique. This is complex, so I suggest just copying an example 
 * from an existing Echo2 application in this IRMIS codebase.
 *
 * @author Claude Saunders
 *
 */
public abstract class Echo2ThreadWork implements Runnable {
    
    private TaskQueueHandle taskQueueHandle = null;
    private ApplicationInstance appInstance = null;
    
    /**
     * Initialize with given Echo2 task queue handle and application instance. 
     * These must be created by invoking Echo2 ApplicationInstance and passed 
     * in here.
     */
    public Echo2ThreadWork(TaskQueueHandle tqh, ApplicationInstance ai) {
        taskQueueHandle = tqh;
        appInstance = ai;
    }

    /**
     * Standard Runnable run method.
     */
    public void run() {

        // First do work in user's doNonUILogic() method.
        try {
            doNonUILogic();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        // Then, queue up the work defined in doUIUpdateLogic() in 
        //   Echo2 task queue, which is for async. UI updates.
        Runnable task = new Runnable() {
                public void run() {
                    finished();
                }
            };
        appInstance.enqueueTask(taskQueueHandle, task);
    }


    /**
     * This method will be implemented by the inner class of Echo2ThreadWork.
     * It should only consist of the logic that's unrelated to UI
     *
     * @throws java.lang.RuntimeException thrown if there are any errors in the non-ui logic
     */
    protected abstract void doNonUILogic() throws RuntimeException;

    /**
     * This method will be implemented by the inner class of Echo2ThreadWork.
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
            doUIUpdateLogic();
        }
        catch (RuntimeException e) {
            // Do nothing, simply cleanup below
            System.out.println("Echo2Worker error " + e);
            e.printStackTrace(System.out);
        }
    }

}
