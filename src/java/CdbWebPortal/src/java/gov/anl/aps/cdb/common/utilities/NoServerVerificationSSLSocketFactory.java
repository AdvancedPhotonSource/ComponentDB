/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.utilities;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * SSL socket factory that does not verify server credentials.
 *
 * A minor extension of <code>SSLSocketFactory</code> that installs a dummy
 * trust manager. This allows creation of SSL sockets that don't verify the
 * server certificates.
 *
 * @see NoOpTrustManager
 */
public class NoServerVerificationSSLSocketFactory extends SSLSocketFactory {

    private static final Logger logger = LogManager.getLogger(NoServerVerificationSSLSocketFactory.class.getName());

    private SSLSocketFactory factory;

    /**
     * Default constructor.
     */
    public NoServerVerificationSSLSocketFactory() {
        try {
            TrustManager tm = new NoOpTrustManager();
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, // No KeyManager required
                    new TrustManager[]{tm},
                    new java.security.SecureRandom());

            factory = (SSLSocketFactory) sslcontext.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            logger.error(ex);
        }
    }

    /**
     * Get default (no server verification) socket factory.
     *
     * @return socket factory
     */
    public static SocketFactory getDefault() {
        return new NoServerVerificationSSLSocketFactory();
    }

    /**
     * Create SSL socket layered over an existing socket connected to the named
     * host, at a given port.
     *
     * @param socket existing socket
     * @param host
     * @param port
     * @param autoClose
     * @return created socket
     * @throws IOException in case of IO errors
     */
    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws IOException {
        return factory.createSocket(socket, host, port, autoClose);
    }

    /**
     * Create a socket and connect it to the specified remote address/port, and
     * bind it to the specified local address/port.
     *
     * @param address server network address
     * @param port server port
     * @param localAddress client network address
     * @param localPort client port
     * @return created socket
     * @throws IOException in case of IO errors
     */
    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
            throws IOException {
        return factory.createSocket(address, port, localAddress, localPort);
    }

    /**
     * Create a socket and connect it to the specified remote address/port.
     *
     * @param address server network address
     * @param port server port
     * @return created socket
     * @throws IOException in case of IO errors
     */
    @Override
    public Socket createSocket(InetAddress address, int port) throws IOException {
        return factory.createSocket(address, port);
    }

    /**
     * Create a socket and connect it to the specified remote host/port, and
     * bind it to the specified local address/port.
     *
     * @param host server host
     * @param port server port
     * @param localAddress client network address
     * @param localPort client port
     * @return created socket
     * @throws IOException in case of IO errors
     */
    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort)
            throws IOException {
        return factory.createSocket(host, port, localAddress, localPort);
    }

    /**
     * Create a socket and connect it to the specified remote host/port, and
     * bind it to the specified local address/port.
     *
     * @param host server host
     * @param port server port
     * @return created socket
     * @throws IOException in case of IO errors
     */
    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return factory.createSocket(host, port);
    }

    /**
     * Get default cipher suites from socket factory.
     *
     * @return list of default ciphers
     */
    @Override
    public String[] getDefaultCipherSuites() {
        return factory.getSupportedCipherSuites();
    }

    /**
     * Get supported cipher suites from socket factory.
     *
     * @return list of supported ciphers
     */
    @Override
    public String[] getSupportedCipherSuites() {
        return factory.getSupportedCipherSuites();
    }

}
