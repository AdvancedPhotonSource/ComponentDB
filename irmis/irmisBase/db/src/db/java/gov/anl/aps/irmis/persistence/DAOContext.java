/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.persistence;

// java io for reading and parsing config file
import java.io.InputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import java.util.logging.Logger;
import java.util.logging.Level;

// blowfish encryption stuff
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

// hibernate
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.util.DTDEntityResolver;

/**
 * Encapsulates a session with an IRMIS data store. A user of the IRMIS DAO API must
 * construct, hold, and pass around an DAOContext to keep the state of a potentially 
 * lengthy IRMIS session. This implementation is Hibernate-specific, and is tied to the
 * data access object implementation (BaseDAO). Nonetheless, it prevents Hibernate and/or
 * JDBC code from making its way into user code.
 */
public class DAOContext {

    private static DAOContext singletonContext = null;
    private static final ThreadLocal threadLocalContext = new ThreadLocal();

    private final SessionFactory sessionFactory;
    private Session s;

    /**
     * Construct context, configuring hibernate from hibernate.cfg.xml 
     * file in classpath and creating the hibernate <code>Session</code>.
     *
     * @throws DAOException
     */
    public DAOContext() throws DAOException {
        try {
            // shut off most of hibernate logging
            Logger.getLogger("org.hibernate")
                .setLevel(Level.WARNING);
            Logger.getLogger("com.mchange.v2")
                .setLevel(Level.WARNING);
            //Logger.getLogger("org.hibernate.engine.Cascades").setLevel(Level.FINEST);
            
            // don't allow any hibernate connection info to get out, period!
            Logger.getLogger("org.hibernate.connection.DriverManagerConnectionProvider")
                .setLevel(Level.OFF);


            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream fileInputStream = cl
                .getResourceAsStream(HibernateUtil.getConfigurationFileName());
            InputStream configInputStream = fileInputStream;

            // if key property is not null, then we must decrypt configuration file
            String key = System.getProperty("encryption.key");

            if (key != null && key.getBytes().length == 16) {
                SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "Blowfish");
                Cipher cipher = Cipher.getInstance("Blowfish");
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
                CipherInputStream cis = new CipherInputStream(fileInputStream, cipher);
                configInputStream = cis;
            }

            // Rather complicated way of doing hibernate config, but we need input stream
            // so we can decrypt configuration file if needed.
            Configuration cfg = new Configuration();
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            docBuilder.setEntityResolver(new DTDEntityResolver());
            Document dom = docBuilder.parse(configInputStream);
            cfg.configure(dom);
            sessionFactory = cfg.buildSessionFactory();
            s = sessionFactory.openSession();            

        } catch (HibernateException he) {
            he.printStackTrace(System.out);
            throw new DAOException(he);

        } catch (NoSuchAlgorithmException nae) {
            nae.printStackTrace(System.out);
            throw new DAOException(nae);

        } catch (InvalidKeyException ike) {
            ike.printStackTrace(System.out);
            throw new DAOException(ike);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace(System.out);
            throw new DAOException(pce);

        } catch (SAXException se) {
            se.printStackTrace(System.out);
            throw new DAOException(se);

        } catch (IOException ioe) {
            ioe.printStackTrace(System.out);
            throw new DAOException(ioe);

        } catch (NoSuchPaddingException nspe) {
            nspe.printStackTrace(System.out);
            throw new DAOException(nspe);
        }
    }

    /**
     * Retrieves a single, application-wide common instance of DAOContext. This
     * ensures that all code shares a single session with IRMIS. If needed, DAOContext
     * may be constructed directly, but each will be a new heavyweight session. Note:
     * the system property db.session.storage was added. This can configure an alternate
     * mechanism whereby the DAOContext is stored in ThreadLocal storage instead of as
     * an application wide singleton.
     *
     * @return DAOContext
     */
    public synchronized static DAOContext getInstance() throws DAOException {
        // This will either be "singleton" or "threadlocal"
        String sessionStorageProperty = System.getProperty("db.session.storage");

        if (sessionStorageProperty == null || sessionStorageProperty.equals("singleton")) {
            if (singletonContext == null)
                singletonContext = new DAOContext();
            return singletonContext;

        } else { // assume "threadlocal" in this case
            DAOContext threadContext = (DAOContext)threadLocalContext.get();
            if (threadContext == null) {
                threadContext = new DAOContext();
                attach(threadContext);
            }
            return threadContext;
        }

    }

    /**
     * Detach the current DAOContext from the ThreadLocal storage.
     */
    public static DAOContext detach() {
        DAOContext context = (DAOContext)threadLocalContext.get();        
        threadLocalContext.set(null);
        return context;
    }

    /**
     * Attach the given DAOContext to the ThreadLocal storage.
     */
    public static void attach(DAOContext context) {
        threadLocalContext.set(context);
    }


    /**
     * Return active hibernate session stored in this context. Open a new one
     * if the current session is closed.
     *
     * @return hibernate Session
     */
    public Session getSession() throws DAOException {
        try {
            if (!s.isOpen()) {
                s = sessionFactory.openSession();
            }
        } catch (HibernateException he) {
            throw new DAOException(he);
        }
        return s;
    }

    /**
     * Close the hibernate session.
     */
    public void closeSession() {
        try {
            s.close();
        } catch (HibernateException he) {
            he.printStackTrace();
        } 
    }

}
