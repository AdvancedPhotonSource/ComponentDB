/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.ioc.cfw.plugins;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

import gov.anl.aps.irmis.persistence.pv.IOC;

/**
 * Interface for a plug-in class that returns a Swing component
 * for displaying/editing site-specific ioc information. This allows
 * different sites to implement a custom database table of ioc
 * related info, yet render the viewer/editor in the idtioc application
 * distributed with irmisBase.
 */
public interface IOCExtendedInfoPlugin {

    /**
     * Returns a Swing JPanel which contains widgets for viewing/editing
     * any site-specific ioc information. A list of <code>IOC</code>
     * objects is supplied to allow lookup of the extended info using
     * the existing ioc id's. 
     *
     * @param iocList - list of <code>IOC</code> objects
     *
     * @return Swing JPanel ready for display
     */
    public JPanel createIOCInfoPanel(List iocList);

    /**
     * Externally select a given ioc. The created component (JPanel) should
     * display the extended info pertaining to this ioc.
     *
     * @param ioc - a <code>IOC</code> object
     */
    public void selectIOC(IOC ioc);

    /**
     * A new object representing extended info for a new generic IOC
     * should be created and persisted.
     */
    public void addNewExtendedIOC(IOC ioc);
    


}
