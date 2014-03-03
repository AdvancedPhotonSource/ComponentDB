/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps;

import gov.sns.application.Application;

import java.awt.Component;
import java.net.URL;
import java.io.IOException;
import javax.jnlp.*;
import javax.swing.JButton;
import javax.swing.ImageIcon;

/** 
 * Utility class with a variety of methods that are used by the various
 * IRMIS applications.
 */
public class AppsUtil {


    public static void showURL(Component parentComponent, String urlString) {
        try {
            BasicService basicService = (BasicService)
                ServiceManager.lookup("javax.jnlp.BasicService");
            if (!basicService.isWebBrowserSupported()) {
                Application.displayWarning("Browser access problem","Web browser access from java is not supported on your system.");
            } else {
                java.net.URL url = new java.net.URL(urlString);
                if (!basicService.showDocument(url)) {
                    Application.displayWarning("Browser access problem","Please (1) run javaws (Java Web Start) and configure the path to your system's web browser executable. (2) Restart IRMIS.");
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (UnavailableServiceException use) {
            // unable to use jnlp services for whatever reason
            Application.displayWarning("Browser access problem","Web browser access from java is not supported for non Java Web Start launched applications.");
        } 
    }

    public static URL getImageURL(String imageName) {

        // look for the image in a standard place
        String imgLocation = "gov/anl/aps/irmis/apps/resources/"+imageName;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL imageURL = cl.getResource(imgLocation);

        return imageURL;
    }

}
