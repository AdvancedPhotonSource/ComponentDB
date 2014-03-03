/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps;


// work queue support from concurrent.jar
import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;

/** 
 * Utility class containing singleton background work queue we use to handle 
 * lengthy logic (ie. sql queries). This is a work queue backed by a single 
 * common background thread. It's important to conduct most if not all sql 
 * activity using this thread, as the Hibernate session is maintained in 
 * ThreadLocal storage, and therefore should only exist on one thread to 
 * take advantage of Hibernate caching. Also, this single work queue serves
 * to serialize many activities that cause GUI updates, reducing possibility
 * of complex race conditions.
 *
 * The QueuedExecutor class comes from the public domain concurrent.jar library, 
 * most of which went into Sun's JDK 1.5 java.util.concurrent package. 
 * We're still on JDK1.4.
 */
public class QueuedExecutorUtil {

    private static QueuedExecutor queuedExecutor = new QueuedExecutor();

    public static void execute(Runnable runnable) {
        try {
            queuedExecutor.execute(runnable);
        } catch (InterruptedException ie) {}
    }

}
