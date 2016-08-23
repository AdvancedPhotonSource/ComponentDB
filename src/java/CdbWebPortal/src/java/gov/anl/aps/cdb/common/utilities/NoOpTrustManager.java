/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/common/utilities/NoOpTrustManager.java $
 *   $Date: 2015-04-16 10:32:53 -0500 (Thu, 16 Apr 2015) $
 *   $Revision: 589 $
 *   $Author: sveseli $
 */
package gov.anl.aps.cdb.common.utilities;

import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

/**
 * Dummy trust manager class.
 *
 * A trivial implementation of <code>X509TrustManager</code> that doesn't
 * actually check the validity of a certificate. This allows us to make SSL
 * connections to internal servers without requiring the installation and
 * maintenance of certificates in the client keystore.
 *
 * @see NoServerVerificationSSLSocketFactory
 */
public class NoOpTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] cert, String authType) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] cert, String authType) {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
