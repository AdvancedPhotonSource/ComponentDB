/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.utilities;

import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.apache.log4j.Logger;

/**
 * LDAP utility class for verifying user credentials.
 *
 * @see NoServerVerificationSSLSocketFactory
 */
public class LdapUtility {

    private static final String LdapUrlPropertyName = "cdb.portal.ldapUrl";
    private static final String LdapDnStringPropertyName = "cdb.portal.ldapDnString";
    private static final String LdapLookupDnPropertyName = "cdb.portal.ldapLookupDn";
    private static final String LdapLookupDnBindPasswordPropertyName = "cdb.portal.ldapLookupBindDnPassword";
    private static final String LdapLookupDnUsernameFilterPropertyName = "cdb.portal.ldapLookupDnUsernameFilter";

    private static final String ldapUrl = ConfigurationUtility.getPortalProperty(LdapUrlPropertyName);
    private static final String ldapDnString = ConfigurationUtility.getPortalProperty(LdapDnStringPropertyName);
    private static final String ldapLookupDn = ConfigurationUtility.getPortalProperty(LdapLookupDnPropertyName);
    private static final String ldapLookupDnBindPassword = ConfigurationUtility.getPortalProperty(LdapLookupDnBindPasswordPropertyName);
    private static final String ldapLookupDnUsernamefilter = ConfigurationUtility.getPortalProperty(LdapLookupDnUsernameFilterPropertyName);

    private static final Logger logger = Logger.getLogger(LdapUtility.class.getName());

    /**
     * Validate user credentials.
     *
     * Use username and password to attempt initial connection and bind with
     * LDAP server. Successful connection implies that credentials are accepted.
     *
     * @param username username
     * @param password password
     *
     * @return true if credentials are valid, false otherwise
     */
    public static boolean validateCredentials(String username, String password) {

        // dump out immediately if not given password
        if (password.isEmpty()) {
            return false;
        }

        String dn = getDn(username);
        logger.debug("Authenticating: " + dn);

        return bindToLdap(dn, password) != null;
    }

    private static DirContext bindToLdap(String bindDn, String password) {
        DirContext ctx = null;
        if (bindDn != "") {
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapUrl);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, bindDn);
            env.put(Context.SECURITY_CREDENTIALS, password);
            // the below property allows us to circumvent server certificate checks
            env.put("java.naming.ldap.factory.socket", "gov.anl.aps.cdb.common.utilities.NoServerVerificationSSLSocketFactory");
            try {
                ctx = new InitialDirContext(env);
            } catch (NamingException ex) {
                logger.error(ex);                
            }
        }

        return ctx;
    }

    private static String getDn(String username) {
        String dn = "";
        if (ldapLookupDn == null || ldapLookupDn.equals("")) {
            // Basic dn swap 
            dn = ldapDnString.replace("%s", username);
        } else {
            // Lookup dn
            DirContext bind = null;
            if (ldapLookupDnBindPassword == null || ldapLookupDnBindPassword.equals("")) {
                // No bind necessary
                Hashtable env = new Hashtable();
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.PROVIDER_URL, ldapUrl);
                try {
                    bind = new InitialDirContext(env);
                } catch (NamingException ex) {
                    logger.error(ex);                
                }
            } else {
                bind = bindToLdap(ldapLookupDn, ldapLookupDnBindPassword);            
            }

            String filter = ldapLookupDnUsernamefilter.replace("%s", username);

            SearchControls search = new SearchControls();
            search.setSearchScope(SearchControls.SUBTREE_SCOPE);

            try {
                NamingEnumeration result = bind.search(ldapDnString, filter, search);
                if (result.hasMore()) {
                    SearchResult record = (SearchResult) result.next();
                    dn = record.getNameInNamespace();
                    logger.debug("Found a dn for user: " + dn);
                }
            } catch (NamingException ex) {
                logger.error(ex);
            }
        }

        return dn;
    }

}
