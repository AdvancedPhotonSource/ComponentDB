/*
  Copyright (c) 2004-2005 The University of Chicago, as Operator
  of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.login.echo2support;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import java.security.Principal;
import javax.security.auth.login.*;
import javax.security.auth.*;
import javax.security.auth.callback.*;

// Echo2
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Color;

import gov.anl.aps.irmis.login.SimpleCallbackHandler;
import gov.anl.aps.irmis.login.UserPrincipal;

/**
 * Provides methods for login/logout, registering Echo2 components
 * that are to be enabled/disabled by login/logout, as well as a method
 * to check if an arbitrary action should be permitted. Given array
 * of Principals is compared against the set of principals of the logged
 * in subject.
 */
public class LoginUtil {

    Subject subject = null;
    List protectedComponents = new ArrayList();
    LoginContext lc = null;
    static Color disabledBackgroundColor = new Color(221, 221, 221); // light grey
    static Color disabledForegroundColor = new Color(153, 153, 153); // medium grey

    static {
        // make sure JAAS can find our config file in jar file
        ClassLoader tClassLoader = LoginUtil.class.getClassLoader();
        String tAuthPath = tClassLoader.getResource("irmis_jaas.config").toString();
        System.setProperty ("java.security.auth.login.config", tAuthPath);
    }

    public LoginUtil() {
        protectedComponents = new ArrayList();
    }

    /**
     * Pop up login dialog (in frame f's window), and pass login credentials
     * to the configured JAAS LoginModule implementation for authentication
     * and authorization. Iterate over any registered Swing components
     * (registerProtectedComponent()) enabling them if isPermitted() returns 
     * true. Using the usernameOnly flag, you can instruct the login dialog
     * to only ask for username. Note that the configured JAAS LoginModule
     * you are using must be able to work with only a username.
     *
     * @param ch callback handler from login dialog
     * @param l label which displays name of user logged in
     * @param usernameOnly should be true when no authentication is to be used
     *
     */
    public void login(SimpleCallbackHandler ch) {
        
        try {
            lc = new LoginContext("IRMIS", ch);
            lc.login();
            subject = lc.getSubject();
        } catch (LoginException le) {
        } catch (SecurityException se) {
        }
        
    }


    /**
     * Log user out via JAAS LoginModule implementation, dropping their
     * authorization and disabling any registered Swing components.
     */
    public void logout() {
        try {
            subject = null;
            if (lc != null) {
                lc.logout();
            }
        } catch (LoginException le) {
        } catch (SecurityException se) {
        }
    }

    /**
     * Scans through list of GUI components that have been registered with LoginUtil. 
     * Enables components if authorization permits, otherwise disables them.
     */
    public void updateProtectedComponents() {

        // iterate through registered components, enabling if permitted, disabling otherwise
        Iterator pcIt = protectedComponents.iterator();
        while (pcIt.hasNext()) {
            ProtectedComponent pc = (ProtectedComponent)pcIt.next();
            if (isPermitted(pc.getPrincipalArray())) {
                pc.getComponent().setEnabled(pc.getInitialEnabledState());
                pc.getComponent().setBackground(pc.getOriginalBackground());
                pc.getComponent().setForeground(pc.getOriginalForeground());
            } else {
                pc.getComponent().setEnabled(false);
                pc.getComponent().setBackground(disabledBackgroundColor);
                pc.getComponent().setForeground(disabledForegroundColor);
            }
        }
    }

    /**
     * Register Swing components here before performing any login/logout.
     * These components will be enabled on login if user isPermitted,
     * and disabled on logout.
     */
    public void registerProtectedComponent(Component c, Principal[] parr) {
        registerProtectedComponent(c, true, parr);
    }

    /**
     * Register Swing components here before performing any login/logout.
     * These components will be enabled/disabled (depending on given initialEnabledState)
     * on login if user isPermitted, and disabled on logout.
     */
    public void registerProtectedComponent(Component c, boolean initialEnabledState,
                                                  Principal[] parr) {
        ProtectedComponent pc = new ProtectedComponent(c, initialEnabledState, parr);
        protectedComponents.add(pc);
        if (isPermitted(pc.getPrincipalArray())) {
            pc.getComponent().setEnabled(initialEnabledState);
            pc.getComponent().setBackground(pc.getOriginalBackground());
            pc.getComponent().setForeground(pc.getOriginalForeground());
        } else {
            pc.getComponent().setEnabled(false);
            pc.getComponent().setBackground(disabledBackgroundColor);
            pc.getComponent().setForeground(disabledForegroundColor);
        }
    }

    /**
     * Compares given arrray of Principal with those of the logged in subject.
     * Returns true if subject has a Principal for every one in parr. In short,
     * returns true if user is authorized.
     *
     * @return true if subject has all Principal in parr, false otherwise
     */
    public boolean isPermitted(Principal[] parr) {
        if (subject == null)
            return false;

        Set subjectPrincipals = subject.getPrincipals();
        if (subjectPrincipals == null)
            return false;

        boolean isPermitted = true;
        for (int i=0 ; i < parr.length ; i++) {
            Principal requiredPrincipal = parr[i];
            if (!subjectPrincipals.contains(requiredPrincipal)) {
                isPermitted = false;
                break;
            }
        }
        return isPermitted;
    }

    /**
     * Returns the username string from the UserPrincipal of the
     * authenticated Subject. Null if not yet authenticated.
     */
    public String getUsername() {
        if (subject == null)
            return null;

        Set subjectPrincipals = subject.getPrincipals();
        if (subjectPrincipals == null)
            return null;

        String username = null;
        Iterator pIt = subjectPrincipals.iterator();
        while (pIt.hasNext()) {
            Principal p = (Principal)pIt.next();
            if (p instanceof UserPrincipal) {
                username = ((UserPrincipal)p).getName();
                break;
            }
        }
        return username;
    }

    /**
     * Returns the login module class name in use.
     */
    public String getLoginModuleName() {
        Configuration c = Configuration.getConfiguration();
        AppConfigurationEntry ace = c.getAppConfigurationEntry("IRMIS")[0];
        return ace.getLoginModuleName();
    }

    /**
     * Inner class to hold component paired with the array of Principal
     * required by the Subject in order to enable the component.
     */
    private class ProtectedComponent {
        private Component c;
        private boolean initialEnabledState;
        private Principal[] parr;
        private Color originalBackground;
        private Color originalForeground;

        public ProtectedComponent(Component c, Principal[] parr) {
            this(c, true, parr);
        }

        public ProtectedComponent(Component c, boolean initialEnabledState,
                                  Principal[] parr) {
            this.c = c;
            this.initialEnabledState = initialEnabledState;
            this.parr = parr;
            this.originalBackground = c.getBackground();
            this.originalForeground = c.getForeground();
        }
        
        public Component getComponent() {
            return this.c;
        }

        public Color getOriginalBackground() {
            return this.originalBackground;
        }
        public Color getOriginalForeground() {
            return this.originalForeground;
        }

        public boolean getInitialEnabledState() {
            return this.initialEnabledState;
        }
        public Principal[] getPrincipalArray() {
            return this.parr;
        }
    }
}
