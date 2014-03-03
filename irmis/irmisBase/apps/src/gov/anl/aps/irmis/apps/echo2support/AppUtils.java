/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.echo2support;

import javax.servlet.http.HttpSession;

// Echo2 
import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.webcontainer.ContainerContext;
import nextapp.echo2.webcontainer.command.BrowserRedirectCommand;


/**
 * General application modal message box. Construct with title and message.
 * To detect the closure of the message box, you add a WindowPaneListener 
 * to your message box object.
 */
public class AppUtils {

    /**
     * Utility method to exit application and invalidate the HTTP session.
     */
    public static void exitAndKillSession(ApplicationInstance appInstance, String url) {
        appInstance.enqueueCommand(new BrowserRedirectCommand(url));
        ContainerContext containerContext =
            (ContainerContext)appInstance.getContextProperty(ContainerContext.CONTEXT_PROPERTY_NAME);
        final HttpSession session = containerContext.getSession();
        Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.currentThread().sleep(3000);
                        if (session != null)
                            session.invalidate();
                    } catch (Throwable t) {
                    } 
                }
            });
        thread.start();
    }
}