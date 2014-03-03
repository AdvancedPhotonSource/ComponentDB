/*
  Copyright (c) 2004-2005 The University of Chicago, as Operator
  of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.login;

import java.util.*;
import java.io.IOException;

import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.*;

import javax.security.auth.*;
import javax.security.auth.callback.*;
import javax.security.auth.login.*;
import javax.security.auth.spi.*;

import gov.anl.aps.irmis.persistence.DAOException;
import gov.anl.aps.irmis.persistence.login.Person;
import gov.anl.aps.irmis.persistence.login.PersonDAO;
import gov.anl.aps.irmis.persistence.login.Role;

/**
 * This LoginModule authenticates user against APS LDAP 
 * server, and retrieves allowed roles (authorization)
 * from the irmis_users table in the relational database.
 * 
 */
public class APSLoginModule implements LoginModule {

    // initial state
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map sharedState;
    private Map options;

    // configurable option
    private boolean debug = false;

    // the authentication status
    private boolean succeeded = false;
    private boolean commitSucceeded = false;

    // username and password
    private String username;
    private char[] password;

    // implementation of Principal
    private UserPrincipal userPrincipal;

    /**
     * Initialize this <code>LoginModule</code>.
     *
     * @param subject the <code>Subject</code> to be authenticated. <p>
     *
     * @param callbackHandler a <code>CallbackHandler</code> for communicating
     *			with the end user (prompting for user names and
     *			passwords, for example). <p>
     *
     * @param sharedState shared <code>LoginModule</code> state. <p>
     *
     * @param options options specified in the login
     *			<code>Configuration</code> for this particular
     *			<code>LoginModule</code>.
     */
    public void initialize(Subject subject, CallbackHandler callbackHandler,
                           Map sharedState, Map options) {
 
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;

        // initialize any configured options
        debug = false;
    }

    /**
     * Authenticate the user by prompting for a user name and password.
     *
     * @return true in all cases since this <code>LoginModule</code>
     *		should not be ignored.
     *
     * @exception FailedLoginException if the authentication fails. <p>
     *
     * @exception LoginException if this <code>LoginModule</code>
     *		is unable to perform the authentication.
     */
    public boolean login() throws LoginException {

        // prompt for a user name and password
        if (callbackHandler == null)
            throw new LoginException("Error: no CallbackHandler given");

        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("username: ");
        callbacks[1] = new PasswordCallback("password: ", false);
 
        try {
            callbackHandler.handle(callbacks);
            username = ((NameCallback)callbacks[0]).getName();
            password = ((PasswordCallback)callbacks[1]).getPassword();
            ((PasswordCallback)callbacks[1]).clearPassword();
 
        } catch (java.io.IOException ioe) {
            throw new LoginException(ioe.toString());
        } catch (UnsupportedCallbackException uce) {
            throw new LoginException("Error: " + uce.getCallback().toString() +
                                     " not available to garner authentication information " +
                                     "from the user");
        }

        // verify the username/password
        boolean usernameCorrect = false;
        boolean passwordCorrect = false;

        // contact LDAP server to validate username/password
        if (ldapValidatesCredentials(username,password)) {
            
            // authentication succeeded!!!
            passwordCorrect = true;
            if (debug)
                System.out.println("\t\t[APSLoginModule] " +
                                   "authentication succeeded");
            succeeded = true;
            for (int i = 0; i < password.length; i++)
                password[i] = ' ';
            password = null;
            return true;
        } else {

            // authentication failed -- clean out state
            if (debug)
                System.out.println("\t\t[APSLoginModule] " +
                                   "authentication failed");
            succeeded = false;
            username = null;
            for (int i = 0; i < password.length; i++)
                password[i] = ' ';
            password = null;
            throw new FailedLoginException("User Name and/or Password Incorrect");

        }
    }

    /**
     * Use username and password to attempt initial connection and bind with
     * APS LDAP server. Successful connection implies that credentials are
     * accepted.
     *
     * @return true if valid, false otherwise
     */
    private boolean ldapValidatesCredentials(String username, char[] password) {

        // dump out immediately if not given password
        if (password.length == 0)
            return false;

        boolean validated = false;
        Hashtable env = new Hashtable();
        String dn = "uid="+username+",ou=people,o=aps.anl.gov,dc=aps,dc=anl,dc=gov";
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL,"ldaps://phoebusldap.aps.anl.gov:636");
        env.put(Context.SECURITY_AUTHENTICATION,"simple");
        env.put(Context.SECURITY_PRINCIPAL,dn);
        env.put(Context.SECURITY_CREDENTIALS,password);
        // the below property allows us to circumvent server certificate checks
        env.put("java.naming.ldap.factory.socket","gov.anl.aps.irmis.login.DummySSLSocketFactory");

        try {
            DirContext ctx = new InitialDirContext(env);
            validated = true;
        } catch (NamingException ne) {
            // DO NOT dump this stack trace
        }
        return validated;
    }

    /**
     * Invoked if authentication succeeds. We load the Subject with
     * initialized principals here.
     *
     * @exception LoginException if the commit fails.
     *
     * @return true if this LoginModule's own login and commit
     *		attempts succeeded, or false otherwise.
     */
    public boolean commit() throws LoginException {
        if (succeeded == false) {
            return false;

        } else {
            // add user principal to the subject
            userPrincipal = new UserPrincipal(username);
            if (!subject.getPrincipals().contains(userPrincipal))
                subject.getPrincipals().add(userPrincipal);

            if (debug) {
                System.out.println("\t\t[APSLoginModule] " +
                                   "added UserPrincipal to Subject");
            }

            // get user's roles from db
            Set roles = null;
            try {
                PersonDAO pDAO = new PersonDAO();
                Person person = pDAO.findPerson(username);
                // do this to make absolutely sure we don't have old cached person
                pDAO.evict(person);
                // get it again
                person = pDAO.findPerson(username);
                if (person != null)
                    roles = person.getRoles();

            } catch (DAOException de) {
                de.printStackTrace();
            }

            // instantiate RolePrincipal from roles and add to Subject
            if (roles != null) {
                Iterator roleIt = roles.iterator();
                while (roleIt.hasNext()) {
                    Role role = (Role)roleIt.next();
                    String roleName = role.getRoleName().getRoleName();
                    RolePrincipal newPrincipal = new RolePrincipal(roleName);
                    subject.getPrincipals().add(newPrincipal);
                    if (debug) {
                        System.out.println("\t\t[APSLoginModule] " +
                                           "added RolePrincipal("+roleName+") to Subject");
                    }
                }
            }

            // in any case, clean out state
            username = null;
            commitSucceeded = true;
            return true;
        }
    }

    /**
     * <p> This method is called if the LoginContext's
     * overall authentication failed.
     * (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL LoginModules
     * did not succeed).
     *
     * <p> If this LoginModule's own authentication attempt
     * succeeded (checked by retrieving the private state saved by the
     * <code>login</code> and <code>commit</code> methods),
     * then this method cleans up any state that was originally saved.
     *
     * <p>
     *
     * @exception LoginException if the abort fails.
     *
     * @return false if this LoginModule's own login and/or commit attempts
     *		failed, and true otherwise.
     */
    public boolean abort() throws LoginException {
        if (succeeded == false) {
            return false;
        } else if (succeeded == true && commitSucceeded == false) {
            // login succeeded but overall authentication failed
            succeeded = false;
            username = null;
            userPrincipal = null;
        } else {
            // overall authentication succeeded and commit succeeded,
            // but someone else's commit failed
            logout();
        }
        return true;
    }

    /**
     * Logout the user.
     *
     * <p> This method removes the <code>SamplePrincipal</code>
     * that was added by the <code>commit</code> method.
     *
     * <p>
     *
     * @exception LoginException if the logout fails.
     *
     * @return true in all cases since this <code>LoginModule</code>
     *          should not be ignored.
     */
    public boolean logout() throws LoginException {

        subject.getPrincipals().clear();
        succeeded = false;
        succeeded = commitSucceeded;
        username = null;
        userPrincipal = null;
        return true;
    }
}
