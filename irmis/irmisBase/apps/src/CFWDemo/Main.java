/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package CFWDemo;

import java.net.URL;
import gov.sns.application.*;

/**
 * Entry point for CFW application. Defines basic information
 * for CFW framework and kicks off actual application. The methods
 * below are the bare minimum required to have a valid CFW application.
 * There are other methods that can be overridden to get other stuff
 * done.
 */
public class Main extends DesktopApplicationAdaptor {
	
    // --------- application document handling ------------------------------

    /**
     * Returns the text file suffixes of files this application can open.
     * @return Suffixes of readable files
     */
    public String[] readableDocumentTypes() {
        // Note that this demo does not demonstrate reading/writing files,
        //   so this doesn't matter much, except that it's required by
        //   cfw.
        return new String[] {};
    }
    
    
    /**
     * Returns the text file suffixes of files this application can write.
     * @return Suffixes of writable files
     */
    public String[] writableDocumentTypes() {
        return new String[] {};
    }
    
    
    /**
     * Implement this method to return an instance of my custom document.
     * DemoDocument is the initial document in this multi-document application.
     * Note that we don't show how to handle more than one document type yet.
     * That will be coming soon...
     *
     * @return An instance of my custom document.
     */
    public XalInternalDocument newEmptyDocument() {
        return new DemoDocument();
    }
    
    
    /**
     * Implement this method to return an instance of my custom document
     * corresponding to the specified URL. 
     *
     * @param url The URL of the file to open.
     * @return An instance of my custom document.
     */
    public XalInternalDocument newDocument(final URL url) {
        return new DemoDocument(url);
    }
    
    
    // --------- Global application management ---------------------------------
    
    
    /**
     * Specifies the name of my application.
     * @return Name of my application.
     */
    public String applicationName() {
        return "CFW Demo";
    }
    
    
    
    // --------- Application events --------------------------------------------
    
    /** 
     * Capture the application launched event and print it.  This is an optional
     * hook that can be used to do something useful at the end of the application launch.
     */
    public void applicationFinishedLaunching() {
        System.out.println("Demo running...");
    }
    
    
    /**
     * Do-nothing Constructor
     */
    public Main() {
    }
    
    
    /**
     * Application entry point. Begin with <code>Application.launch()</code> 
     * to start the desktop application, then use produceDocument() to pop
     * up the initial window in the desktop. 
     */
	public static void main(String[] args) {
        try {
            System.out.println("Starting Demo...");
			Application.launch( new Main());
            Application app = Application.getApp();
            app.produceDocument(new DemoDocument());
        }
        catch(Exception exception) {
            System.err.println( exception.getMessage() );
            exception.printStackTrace();
			Application.displayApplicationError("Launch Exception", "Launch Exception", exception);
			System.exit(-1);
        }
    }
}
