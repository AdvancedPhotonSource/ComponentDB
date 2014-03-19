package gov.anl.aps.cms.portal.utility;

import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

/**
 * A trivial implementation of <code>X509TrustManager</code> that doesn't
 * actually check the validity of a certificate. This allows us to make
 * SSL connections to internal servers without requiring the installation
 * and maintenance of certificates in the client keystore.
 *
 * @see NoServerVerificationSSLSocketFactory
 */
public class NoOpTrustManager implements X509TrustManager 
{

    @Override
    public void checkClientTrusted(X509Certificate[] cert, String authType) 
    {
    }
    
    @Override
    public void checkServerTrusted(X509Certificate[] cert, String authType) 
    {
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() 
    {
        return new X509Certificate[0];
    }
}
