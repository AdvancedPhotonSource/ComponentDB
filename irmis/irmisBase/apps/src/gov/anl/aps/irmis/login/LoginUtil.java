/*
  Copyright (c) 2004-2005 The University of Chicago, as Operator
  of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.login;

import java.awt.Component;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import java.security.Principal;
import javax.security.auth.login.*;
import javax.security.auth.*;
import javax.security.auth.callback.*;

/**
 * Provides static methods for login/logout, registering Swing components
 * that are to be enabled/disabled by login/logout, as well as a method
 * to check if an arbitrary action should be permitted. Given array
 * of Principals is compared against the set of principals of the logged
 * in subject.
 */
public class LoginUtil {

    static Subject subject = null;
    static List protectedComponents = new ArrayList();
    static LoginContext lc = null;

    static {
        // make sure JAAS can find our config file in jar file
        //ClassLoader tClassLoader = LoginUtil.class.getClassLoader();
        //String tAuthPath = tClassLoader.getResource("irmis_jaas.config").toString();
	String tAuthPath = "jar:http://ctlappsirmis.aps.anl.gov/irmis2/idt/irmis.jar!/irmis_jaas.config";
        System.setProperty ("java.security.auth.login.config", tAuthPath);
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
     * @param f application frame in which to pop up login dialog
     * @return true if user authenticated successfully, false otherwise
     *
     */
    static public boolean login(Frame f, boolean usernameOnly) {

        boolean authenticated = false;
        try {
            SimpleCallbackHandler ch = 
                LoginDialog.showDialog(f, null, usernameOnly);
            lc = new LoginContext("IRMIS", ch);
            lc.login();
            authenticated = true;
            subject = lc.getSubject();

            // iterate through registered components, enabling if permitted
            Iterator pcIt = protectedComponents.iterator();
            while (pcIt.hasNext()) {
                ProtectedComponent pc = (ProtectedComponent)pcIt.next();
                if (isPermitted(pc.getPrincipalArray()))
                    pc.getComponent().setEnabled(pc.getInitialEnabledState());
            }

        } catch (LoginException le) {
        } catch (SecurityException se) {
        }
        return authenticated;
    }


    /**
     * Log user out via JAAS LoginModule implementation, dropping their
     * authorization and disabling any registered Swing components.
     */
    static public void logout() {
        try {
            subject = null;
            if (lc != null) {

                // iterate through registered components, disabling
                Iterator pcIt = protectedComponents.iterator();
                while (pcIt.hasNext()) {
                    ProtectedComponent pc = (ProtectedComponent)pcIt.next();
                    pc.getComponent().setEnabled(false);
                }
                
                lc.logout();
            }
        } catch (LoginException le) {
        } catch (SecurityException se) {
        }
    }

    /**
     * Register Swing components here before performing any login/logout.
     * These components will be enabled on login if user isPermitted,
     * and disabled on logout.
     */
    static public void registerProtectedComponent(Component c, Principal[] parr) {
        ProtectedComponent pc = new ProtectedComponent(c, parr);
        protectedComponents.add(pc);
        if (isPermitted(pc.getPrincipalArray()))
            pc.getComponent().setEnabled(true);
        else
            pc.getComponent().setEnabled(false);
    }

    /**
     * Register Swing components here before performing any login/logout.
     * These components will be enabled/disabled (depending on given initialEnabledState)
     * on login if user isPermitted, and disabled on logout.
     */
    static public void registerProtectedComponent(Component c, boolean initialEnabledState,
                                                  Principal[] parr) {
        ProtectedComponent pc = new ProtectedComponent(c, initialEnabledState, parr);
        protectedComponents.add(pc);
        if (isPermitted(pc.getPrincipalArray()))
            pc.getComponent().setEnabled(initialEnabledState);
        else
            pc.getComponent().setEnabled(false);
    }

    /**
     * Compares given arrray of Principal with those of the logged in subject.
     * Returns true if subject has a Principal for every one in parr. In short,
     * returns true if user is authorized.
     *
     * @return true if subject has all Principal in parr, false otherwise
     */
    static public boolean isPermitted(Principal[] parr) {
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
    static public String getUsername() {
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
    static public String getLoginModuleName() {
        Configuration c = Configuration.getConfiguration();
        AppConfigurationEntry ace = c.getAppConfigurationEntry("IRMIS")[0];
        return ace.getLoginModuleName();
    }

    /**
     * Inner class to hold component paired with the array of Principal
     * required by the Subject in order to enable the component.
     */
    static private class ProtectedComponent {
        private Component c;
        private boolean initialEnabledState;
        private Principal[] parr;

        public ProtectedComponent(Component c, Principal[] parr) {
            this(c, true, parr);
        }

        public ProtectedComponent(Component c, boolean initialEnabledState,
                                  Principal[] parr) {
            this.c = c;
            this.initialEnabledState = initialEnabledState;
            this.parr = parr;
        }
        
        public Component getComponent() {
            return this.c;
        }
        public boolean getInitialEnabledState() {
            return this.initialEnabledState;
        }
        public Principal[] getPrincipalArray() {
            return this.parr;
        }
    }
}
