/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.apps.irmis.cfw;

import gov.anl.aps.irmis.login.LoginUtil;
import gov.anl.aps.irmis.apps.pv.cfw.PVDocument;
import gov.anl.aps.irmis.apps.component.cfw.ComponentDocument;
import gov.anl.aps.irmis.apps.cable.cfw.CableDocument;
import gov.anl.aps.irmis.apps.componenttype.cfw.ComponentTypeDocument;
import gov.anl.aps.irmis.apps.ioc.cfw.IOCDocument;
import gov.anl.aps.irmis.apps.admin.cfw.AdminDocument;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

import gov.sns.application.*;

// general app utilities
import gov.anl.aps.irmis.login.LoginUtil;
import gov.anl.aps.irmis.apps.AppsUtil;
import gov.anl.aps.irmis.apps.SwingThreadWork;
import gov.anl.aps.irmis.apps.QueuedExecutorUtil;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.audit.AuditAction;
import gov.anl.aps.irmis.persistence.audit.AuditActionType;

// IRMIS service layer
import gov.anl.aps.irmis.service.IRMISException;
import gov.anl.aps.irmis.service.shared.AuditService;
import gov.anl.aps.irmis.service.IRMISService;

/**
 * Entry point for IRMIS application. Defines basic information
 * for XAL framework and kicks off actual application.
 */
public class Main extends DesktopApplicationAdaptor {

    // custom actions for login/logout menu add-ons
	private Action _loginAction;
	private Action _logoutAction;
	private Action _refreshAction;
    private String titlePrefix = "";
    static private JLabel l;
    static private int iconWidth;
    static private int iconHeight;
    static private TimerTask tt;  // timer to periodically reload data
    static private Timer t;
    static private long completionTime;
    static private boolean testMode = false;


    /**
     * Returns the text file suffixes of files this application can open.
     * @return Suffixes of readable files
     */
    public String[] readableDocumentTypes() {
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
     * PVDocument is the initial document in this multi-document application.
     *
     * @return An instance of my custom document.
     */
    public XalInternalDocument newEmptyDocument() {
        return new PVDocument();
    }

    /**
     * Implement this method to return an instance of the appropriate 
     * document based on the string argument.
     *
     * @param type string name for desired document type (comes from new menu)
     * @return An instance of my custom document.
     */
    public XalInternalDocument newEmptyDocument(String type) {
        if (type == null)
            return new PVDocument();

        if (type.equals("idt::pv")) {
            return new PVDocument();
        } else if (type.equals("idt::ioc")) {
            return new IOCDocument();
        } else if (type.equals("idt::component")) {
            return new ComponentDocument();
        } else if (type.equals("idt::component-type")) {
            return new ComponentTypeDocument();
        } else if (type.equals("idt::cable")) {
            return new CableDocument();
        } else if (type.equals("idt::admin")) {
            return new AdminDocument();
        } else {
            return new PVDocument();
        }
    }
    
    
    /**
     * Implement this method to return an instance of my custom document
     * corresponding to the specified URL. 
     * @param url The URL of the file to open.
     * @return An instance of my custom document.
     */
    public XalInternalDocument newDocument(final URL url) {
        return new PVDocument(url);
    }
    
    
    // --------- Global application management ---------------------------------
    
    
    /**
     * Specifies the name of my application.
     * @return Name of my application.
     */
    public String applicationName() {
        return titlePrefix + "IRMIS Desktop (not logged in)";
    }
    
    
    
    // --------- Application events --------------------------------------------
    
    /** 
     * Capture the application launched event and print it.  This is an optional
     * hook that can be used to do something useful at the end of the application launch.
     */
    public void applicationFinishedLaunching() {
        Logger.getLogger("global").log(Level.INFO, "IRMIS running...");
    }

    /**
     * Register some new menu actions. See menudef.properties to see what actual
     * menu these are used in.
     */
    protected void customizeCommands(Commander commander) {
        _loginAction = new AbstractAction("login") {
                public void actionPerformed(ActionEvent event) {
                    // find out which login module we're using
                    String module = LoginUtil.getLoginModuleName();

                    // Use this to decide whether to show login dialog with password field.
                    // NOTE: should probably find some other way than hardcoding class here
                    boolean usernameOnly = false;
                    if (module.equals("gov.anl.aps.irmis.login.PersonRoleLoginModule") ||
                        module.equals("gov.anl.aps.irmis.login.DoNothingLoginModule"))
                        usernameOnly = true;

                    // supply our desktop frame so login knows where to pop up login dialog
                    JFrame appFrame = ((DesktopApplication)Application.getApp()).getDesktopFrame();
                    int actionType;
                    String desc;
                    if (LoginUtil.login(appFrame, usernameOnly)) {
                        appFrame.setTitle(titlePrefix+"IRMIS Desktop (logged in as " + LoginUtil.getUsername() + ")");
                        actionType = AuditActionType.LOGIN_SUCCESSFUL;
                        desc = "login successful";
                    } else {
                        appFrame.setTitle(titlePrefix+"IRMIS Desktop (login failed)");
                        actionType = AuditActionType.LOGIN_FAILED;
                        desc = "login failed";
                    }
                    // log action to audit table
                    try {
                        AuditAction auditAction = 
                            AuditService.createAuditAction(actionType, desc,
                                                           LoginUtil.getUsername(), null);
                        AuditService.saveAuditAction(auditAction);
                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        Logger.getLogger("global").log(Level.WARNING, "IRMIS Service Error.", ie);
                    }
                }
            };
        commander.registerAction(_loginAction);

        _logoutAction = new AbstractAction("logout") {
                public void actionPerformed(ActionEvent event) {
                    String userName = LoginUtil.getUsername();
                    LoginUtil.logout();
                    JFrame appFrame = ((DesktopApplication)Application.getApp()).getDesktopFrame();
                    appFrame.setTitle(titlePrefix+"IRMIS Desktop (not logged in)");
                    // log action to audit table
                    try {
                        AuditAction auditAction = 
                            AuditService.createAuditAction(AuditActionType.LOGOUT, "logout",
                                                           userName, null);
                        AuditService.saveAuditAction(auditAction);
                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                        Logger.getLogger("global").log(Level.WARNING, "IRMIS Service Error.", ie);
                    }
                }
            };
        commander.registerAction(_logoutAction);

        _refreshAction = new AbstractAction("refresh") {
                public void actionPerformed(ActionEvent event) {
                    requestResetOfDocuments();
                }
            };
        commander.registerAction(_refreshAction);

    }
    
    
    /**
     * Constructor
     */
    public Main() {

        if (System.getProperty("irmis.test") != null) {
            String prop = System.getProperty("irmis.test");
            if (prop.equalsIgnoreCase("true"))
                testMode = true;
            else
                testMode = false;
        }

        if (testMode)
            titlePrefix = "Test ";
        else
            titlePrefix = "";
            
    }
    
    
    /**
     * Application entry point. Begin with <code>Application.launch()</code> 
     * to start the initial PVDocument. Then bring up the other documents
     * of this multi-document application ComponentDocument, CableDocument, etc...
     */
	public static void main(String[] args) {
        try {
            // shut off most of hibernate logging
            Logger.getLogger("org.hibernate")
                .setLevel(Level.WARNING);
            //Logger.getLogger("org.hibernate.engine.Cascades").setLevel(Level.FINEST);

            // don't allow any hibernate connection info to get out, period!
            Logger.getLogger("org.hibernate.connection.DriverManagerConnectionProvider")
                .setLevel(Level.OFF);

            Logger.getLogger("global").log(Level.INFO,"Starting IRMIS...");

            Main main = new Main();
			Application.launch(main);
            DesktopApplication app = (DesktopApplication)Application.getApp();

            // install the desktop background image that moves with resizes
            JDesktopPane desktopPane = (JDesktopPane)app.getDesktopFrame().getContentPane();
            URL image = AppsUtil.getImageURL("irmis2Logo.png");
            ImageIcon icon = new ImageIcon(image);
            if (testMode)
                l = new JLabel("TEST VERSION");
            else
                l = new JLabel(icon,JLabel.CENTER);
            
            iconWidth = icon.getIconWidth();
            iconHeight = icon.getIconHeight();
            Rectangle b = desktopPane.getBounds();
            double width = b.getWidth();
            double height = b.getHeight();
            int x = new Double(width / 2).intValue() - iconWidth/2;
            int y = new Double(height / 2).intValue() - iconHeight/2;
            l.setBounds(x,y,iconWidth,iconHeight);
            // note: stage only
            if (testMode)
                desktopPane.setBackground(Color.yellow.darker());
            desktopPane.add(l, JLayeredPane.FRAME_CONTENT_LAYER);
            desktopPane.addComponentListener(new ComponentAdapter() {
                    public void componentResized(ComponentEvent e) {
                        Rectangle b = e.getComponent().getBounds();
                        double width = b.getWidth();
                        double height = b.getHeight();
                        int x = new Double(width / 2).intValue() - iconWidth/2;
                        int y = new Double(height / 2).intValue() - iconHeight/2;
                        l.setBounds(x,y,iconWidth,iconHeight);
                    }
                });

            // figure out which app should pop up initially
            String defaultApp = System.getProperty("default.application");
            app.produceDocument(main.newEmptyDocument(defaultApp));

        }
        catch(Exception exception) {
            Logger.getLogger("global").log(Level.SEVERE, "Error starting app.", exception);
            System.err.println( exception.getMessage() );
            exception.printStackTrace();
			Application.displayApplicationError("Launch Exception", "Launch Exception", exception);
			System.exit(-1);
        }
    }

    /**
     * Clears object cache through IRMISService, then goes through
     * IRMIS desktop list of documents and does an actionReset()
     * on each. This effectively reloads the data for each, although the
     * actual implementation of actionReset() is left to the document.
     */
    public static void requestResetOfDocuments() {

        // go through documents on desktop, resetting the data model of each
        Application app = Application.getApp();
        List docs = app.getDocuments();
        Iterator docIt = docs.iterator();
        while (docIt.hasNext()) {
            XalInternalDocument doc = (XalInternalDocument)docIt.next();

            if (doc.getTitle().equals("idt::component")) {
                ComponentDocument cDoc = (ComponentDocument)doc;
                cDoc.actionResetModel();

            } else if (doc.getTitle().equals("idt::cable")) { 
                CableDocument cDoc = (CableDocument)doc;
                cDoc.actionResetModel();

            } else if (doc.getTitle().equals("idt::component-type")) { 
                ComponentTypeDocument cDoc = (ComponentTypeDocument)doc;
                cDoc.actionResetModel();

            } else if (doc.getTitle().equals("idt::pv")) {
                PVDocument cDoc = (PVDocument)doc;
                cDoc.actionResetModel();

            } else if (doc.getTitle().equals("idt::ioc")) {
                IOCDocument cDoc = (IOCDocument)doc;
                cDoc.actionResetModel();

            } else if (doc.getTitle().equals("idt::admin")) {
                AdminDocument cDoc = (AdminDocument)doc;
                cDoc.actionResetModel();
            }
        }

        // queue it up to make sure it happens in sequence
        QueuedExecutorUtil.execute(new SwingThreadWork() {
                public void doNonUILogic() {
                    try {
                        IRMISService.clearCache();
                    } catch (IRMISException ie) {
                        ie.printStackTrace();
                    }
                }
                public void doUIUpdateLogic() {
                }
            });

        // go through documents again, requesting full reload of data
        docIt = docs.iterator();
        while (docIt.hasNext()) {
            XalInternalDocument doc = (XalInternalDocument)docIt.next();

            if (doc.getTitle().equals("idt::component")) {
                ComponentDocument cDoc = (ComponentDocument)doc;
                cDoc.actionReload();

            } else if (doc.getTitle().equals("idt::cable")) { 
                CableDocument cDoc = (CableDocument)doc;
                cDoc.actionReload();

            } else if (doc.getTitle().equals("idt::component-type")) { 
                ComponentTypeDocument cDoc = (ComponentTypeDocument)doc;
                cDoc.actionReload();

            } else if (doc.getTitle().equals("idt::pv")) {
                PVDocument cDoc = (PVDocument)doc;
                cDoc.actionReload();

            } else if (doc.getTitle().equals("idt::ioc")) {
                IOCDocument cDoc = (IOCDocument)doc;
                cDoc.actionReload();

            } else if (doc.getTitle().equals("idt::admin")) {
                AdminDocument cDoc = (AdminDocument)doc;
                cDoc.actionReload();
            }
        }
        // go through documents again, requesting full GUI redraw
        docIt = docs.iterator();
        while (docIt.hasNext()) {
            XalInternalDocument doc = (XalInternalDocument)docIt.next();

            if (doc.getTitle().equals("idt::component")) {
                ComponentDocument cDoc = (ComponentDocument)doc;
                cDoc.actionRedraw();

            } else if (doc.getTitle().equals("idt::cable")) { 
                CableDocument cDoc = (CableDocument)doc;
                cDoc.actionRedraw();

            } else if (doc.getTitle().equals("idt::component-type")) { 
                ComponentTypeDocument cDoc = (ComponentTypeDocument)doc;
                cDoc.actionRedraw();

            } else if (doc.getTitle().equals("idt::pv")) {
                PVDocument cDoc = (PVDocument)doc;
                cDoc.actionRedraw();

            } else if (doc.getTitle().equals("idt::ioc")) {
                IOCDocument cDoc = (IOCDocument)doc;
                cDoc.actionRedraw();

            } else if (doc.getTitle().equals("idt::admin")) {
                AdminDocument cDoc = (AdminDocument)doc;
                cDoc.actionRedraw();
            }
        }
    }
}
