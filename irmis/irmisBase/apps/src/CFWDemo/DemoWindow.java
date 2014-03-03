/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package CFWDemo;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.HashSet;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

// XAL/CFW framework
import gov.sns.application.*;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.pv.IOC;

/**
 * Primary GUI for Demo application. All window layout, and Swing event
 * listeners are done here. Business logic work is delegated to <code>DemoDocument</code>,
 * which in turn requests that the DemoModel notify us here of changes to the data. In short,
 * we listen for <code>DemoModelEvent</code> here.
 */
public class DemoWindow extends XalInternalWindow {

	// The main model for the document
	final protected DemoModel _model;

    // The main document (controller)
    final protected DemoDocument document;

	// Swing GUI components
    private JPanel topPanel;
    private JPanel iocListPanel;

    // list box to show ioc's
    private JList iocList;
    // Swing data model for list box
    private DefaultListModel iocListModel;

    // button to kick off search of ioc's
    private JButton searchButton;

    // button to show demo wizard
    private JButton wizardButton;
	
	/** 
	 * Creates a new instance of DemoWindow
	 * @param aDocument The document for this window
	 */
    public DemoWindow(final XalInternalDocument aDocument) {
        super(aDocument);

        // save a reference to the document cast as a DemoDocument
        document = (DemoDocument)aDocument;
        
        // Grab a reference to the data model
		_model = document.getModel();

        // make DemoWindow a listener for changes in DemoModel
		_model.addDemoModelListener( new DemoModelListener() {
                public void modified(DemoModelEvent e) {
                    // when we get an data model event, call our updateView method
                    updateView(e);
                }
            });

        // initial application window size
        setSize(400, 400);

        // build contents of window
		makeContents();
    }


    /**
     * Top-level method to build up Swing GUI components.
     */
    public void makeContents() {

        // scrollbar policy
        int hsbp = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
        int vsbp = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

        // topmost panel
        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.PAGE_AXIS));

        // create ioc list panel with scrollbar
        iocListPanel = new JPanel(new BorderLayout());
        TitledBorder iocListTitle = BorderFactory.createTitledBorder("IOCs");
        iocListPanel.setBorder(iocListTitle);
        iocListModel = new DefaultListModel();
    	iocList = new JList(iocListModel);
    	iocList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    	JScrollPane iocListScroller = new JScrollPane(iocList, vsbp, hsbp);
        iocListPanel.add(iocListScroller);

        // create search button
        searchButton = new JButton("Search");

        // invoke DemoDocument.actionIOCSearch() when button is pressed
        searchButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // kick off the search for ioc's
                    document.actionIOCSearch();
                }
            });

        // create wizard button
        wizardButton = new JButton("Wizard...");

        wizardButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // this demonstrates how to invoke a wizard and get
                    //  the data from it afterwards
                    DemoWizardModel dataModel = DemoWizard.showWizard();
                    System.out.println("You entered:"+dataModel.getJunk()+" in the wizard");
                }
            });

        // pack in ioc list panel and search button
        topPanel.add(iocListPanel);
        topPanel.add(searchButton);
        topPanel.add(wizardButton);

        // add our topmost panel to the desktop window
    	getContentPane().add(topPanel);
    	
    }


    /**
     * Update graphical view of DemoModel data based on event type.
     *
     * @param event type of data model change
     */
    public void updateView(DemoModelEvent event) {

        switch(event.getType()) {

        case DemoModelEvent.NEW_IOC_LIST: {
            List iocList = _model.getIocList();
            if (iocList != null) {
                Iterator iocIt = iocList.iterator();

                // iterate over IOC objects 
                while (iocIt.hasNext()) {
                    IOC ioc = (IOC)iocIt.next();

                    // explicitly add ioc names to selection list
                    iocListModel.addElement(ioc.getIocName());
                }
            }
            break;
        }

        default: {}
        }
            
    }


    /********************************************************************
     * Inner classes supporting Swing components
     ********************************************************************/
    
    // nothing here, but lots in PV Viewer application

}
