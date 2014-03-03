package gov.anl.aps.irmis.login;

import java.io.*;
import java.util.*;
import javax.security.auth.login.*;
import javax.security.auth.*;
import javax.security.auth.callback.*;

/**
 *
 */
public class SimpleCallbackHandler implements CallbackHandler {

    private String username = null;
    private char[] password = null;

    public SimpleCallbackHandler(String username, char[] password) {
        this.username = username;
        this.password = password;
    }
    
    public void handle(Callback[] callbacks)
        throws IOException, UnsupportedCallbackException {
        
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof TextOutputCallback) {
                
                // nothing, we shouldn't get one of these
                
            } else if (callbacks[i] instanceof NameCallback) {
                
                // prompt the user for a username
                NameCallback nc = (NameCallback)callbacks[i];
                
                nc.setName(this.username);
                           
                
            } else if (callbacks[i] instanceof PasswordCallback) {
                
                // prompt the user for sensitive information
                PasswordCallback pc = (PasswordCallback)callbacks[i];
                pc.setPassword(this.password);
                if (this.password != null) {
                    for (int j=0; j < this.password.length; j++)
                        this.password[j] = ' ';
                    this.password = null;
                }

                
            } else {
                throw new UnsupportedCallbackException
                    (callbacks[i], "Unrecognized Callback");
            }
        }
    }
}
