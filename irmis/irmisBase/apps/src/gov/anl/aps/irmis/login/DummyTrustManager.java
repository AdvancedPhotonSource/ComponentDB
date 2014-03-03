package gov.anl.aps.irmis.login;

import java.security.cert.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;

/**
 * A trivial implementation of <code>X509TrustManager</code> that doesn't
 * actually check the validity of a certificate. This allows us to make
 * SSL connections to internal servers without requiring the installation
 * and maintenance of certificates in the client keystore.
 *
 * @see DummySSLSocketFactory
 */
public class DummyTrustManager implements X509TrustManager {

    public void checkClientTrusted( X509Certificate[] cert, String authType) {
        return;
    }
    
    public void checkServerTrusted( X509Certificate[] cert, String authType) {
        return;
    }
    
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
