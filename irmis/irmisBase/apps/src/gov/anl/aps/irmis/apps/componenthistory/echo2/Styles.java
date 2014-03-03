/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.componenthistory.echo2;

import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.app.StyleSheet;
import nextapp.echo2.app.componentxml.ComponentXmlException;
import nextapp.echo2.app.componentxml.StyleSheetLoader;


/**
 * Standard Look-and-feel information for component history application.
 */
public class Styles {
    
    public static final String IMAGE_PATH = "gov/anl/aps/irmis/apps/componenthistory/echo2/resource/image/";
    public static final String STYLE_PATH = "gov/anl/aps/irmis/apps/componenthistory/echo2/resource/style/";
    
    /**
     * Default application style sheet.
     */
    public static final StyleSheet DEFAULT_STYLE_SHEET;
    static {
        try {
            DEFAULT_STYLE_SHEET = StyleSheetLoader.load(STYLE_PATH + "Default.stylesheet", 
                                                        Thread.currentThread().getContextClassLoader());

        } catch (ComponentXmlException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Images
    public static final ImageReference IRMIS_IMAGE = new ResourceImageReference(IMAGE_PATH+"irmis2LogoSmall.png");
    public static final ImageReference MAGNIFIER = new ResourceImageReference(IMAGE_PATH+"Find16.gif");

}
