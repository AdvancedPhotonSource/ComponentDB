package gov.anl.aps.cdb.common.utilities;

import gov.anl.aps.cdb.common.utilities.NoOpTrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * A minor extension of <code>SSLSocketFactory</code> that installs
 * a dummy trust manager. This allows creation of SSL sockets that don't
 * verify the server certificates.
 *
 * @see NoOpTrustManager
 */
public class NoServerVerificationSSLSocketFactory extends SSLSocketFactory 
{
    private SSLSocketFactory factory;
    public NoServerVerificationSSLSocketFactory() 
    {
        try {
            TrustManager tm = new NoOpTrustManager();
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init( null, // No KeyManager required
                             new TrustManager[] {tm},
                             new java.security.SecureRandom());
            
            factory = (SSLSocketFactory)sslcontext.getSocketFactory();
            
        } 
        catch(KeyManagementException | NoSuchAlgorithmException ex) { 
            ex.printStackTrace(); 
        }
    }
    
    public static SocketFactory getDefault() {
        return new NoServerVerificationSSLSocketFactory();
    }
    
    @Override
    public Socket createSocket(Socket socket, String s, int i, boolean flag) 
        throws IOException 
    {
        return factory.createSocket( socket, s, i, flag);
    }
    
    @Override
    public Socket createSocket(InetAddress inaddr, int i, InetAddress inaddr1, int j) 
        throws IOException 
    {
        return factory.createSocket(inaddr, i, inaddr1, j);
    }
    
    @Override
    public Socket createSocket(InetAddress inaddr, int i) throws IOException 
    {
        return factory.createSocket(inaddr, i);
    }
    
    @Override
    public Socket createSocket(String s, int i, InetAddress inaddr, int j) 
        throws IOException 
    {
        return factory.createSocket(s, i, inaddr, j);
    }
    
    @Override
    public Socket createSocket(String s, int i) throws IOException 
    {
        return factory.createSocket(s, i);
    }
    
    @Override
    public String[] getDefaultCipherSuites() 
    {
        return factory.getSupportedCipherSuites();
    }
    
    @Override
    public String[] getSupportedCipherSuites() 
    {
        return factory.getSupportedCipherSuites();
    }
    
}
