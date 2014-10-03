/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.utilities;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.apache.log4j.Logger;

/**
 *
 * @author sveseli
 */
public class LdapUtility
{

    private static final String LdapUrlPropertyName = "cdb.portal.ldapUrl";
    private static final String LdapDnStringPropertyName = "cdb.portal.ldapDnString";
    private static final String ldapUrl = ConfigurationUtility.getPortalProperty(LdapUrlPropertyName);
    private static final String ldapDnString = ConfigurationUtility.getPortalProperty(LdapDnStringPropertyName);

    private static final Logger logger = Logger.getLogger(LdapUtility.class.getName());

    /**
     * Use username and password to attempt initial connection and bind with APS
     * LDAP server. Successful connection implies that credentials are accepted.
     *
     * @param username username
     * @param password password
     *
     * @return true if valid, false otherwise
     */
    public static boolean validateCredentials(String username, String password) {

        // dump out immediately if not given password
        if (password.isEmpty()) {
            return false;
        }

        boolean validated = false;
        Hashtable env = new Hashtable();
        String dn = ldapDnString.replace("USERNAME", username);
        logger.debug("Authenticating: " + dn);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, dn);
        env.put(Context.SECURITY_CREDENTIALS, password);
        // the below property allows us to circumvent server certificate checks
        env.put("java.naming.ldap.factory.socket", "gov.anl.aps.cdb.portal.utilities.NoServerVerificationSSLSocketFactory");

        try {
            DirContext ctx = new InitialDirContext(env);
            validated = true;
        }
        catch (NamingException ex) {
            ex.printStackTrace();
        }
        return validated;
    }

}
