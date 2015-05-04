/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.api;

import gov.anl.aps.cdb.common.constants.CdbHttpHeader;
import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.common.constants.CdbRole;
import gov.anl.aps.cdb.common.constants.CdbServiceProtocol;
import gov.anl.aps.cdb.common.exceptions.AuthorizationError;
import gov.anl.aps.cdb.common.exceptions.CommunicationError;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.InvalidSession;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.CdbExceptionFactory;
import gov.anl.aps.cdb.common.utilities.NoServerVerificationSSLSocketFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.apache.log4j.Logger;

/**
 * CDB REST Web Service API class.
 *
 * This class serves as superclass for all CDB web service interface classes. It
 * handles basic communication with web service (establishing sessions, sending
 * requests, receiving responses, generating exceptions, etc.).
 */
public class CdbRestApi {

    /**
     * Relative path for login requests.
     */
    public static final String LOGIN_REQUEST_URL = "/login";

    private static final String DefaultSessionId = "defaultSession";

    private static final boolean httpsInitialized = initializeHttpsConnection();    
    private static final Logger logger = Logger.getLogger(CdbRestApi.class.getName());

    private static boolean initializeHttpsConnection() {
        HttpsURLConnection.setDefaultSSLSocketFactory(new NoServerVerificationSSLSocketFactory());
        return true;
    }

    private URL serviceUrl;
    private CdbSession session = new CdbSession();

    /**
     * Constructor.
     *
     * Initializes web service URL from system properties.
     *
     * @throws ConfigurationError if web service URL property is malformed or
     * null
     */
    public CdbRestApi() throws ConfigurationError {
        configureFromProperties();
    }

    /**
     * Constructor.
     *
     * @param webServiceUrl web service URL
     * @throws ConfigurationError if web service URL is malformed or null
     */
    public CdbRestApi(String webServiceUrl) throws ConfigurationError {
        configureFromString(webServiceUrl);
    }

    /**
     * Configure web service URL from Java VM properties.
     *
     * @throws ConfigurationError if web service URL property is malformed or
     * null
     */
    public final void configureFromProperties() throws ConfigurationError {
        String webServiceUrl = System.getProperty(CdbProperty.WEB_SERVICE_URL_PROPERTY_NAME);
        configureFromString(webServiceUrl);
    }

    /**
     * Configure web service URL from string.
     *
     * @param webServiceUrl web service URL
     * @throws ConfigurationError if web service URL property is malformed or
     * null
     */
    public final void configureFromString(String webServiceUrl) throws ConfigurationError {
        if (webServiceUrl == null) {
            throw new ConfigurationError("CDB web service url is not specified.");
        }
        try {
            serviceUrl = new URL(webServiceUrl);
        } catch (MalformedURLException ex) {
            throw new ConfigurationError("Malformed CDB web service url: " + webServiceUrl);
        }

        CdbServiceProtocol protocol = CdbServiceProtocol.fromString(serviceUrl.getProtocol());
        if (protocol == null) {
            throw new ConfigurationError("Unsupported service protocol specified in " + webServiceUrl);
        }
    }

    public URL getServiceUrl() {
        return serviceUrl;
    }

    public CdbSession getSession() {
        return session;
    }

    public void setSession(CdbSession session) {
        this.session = session;
    }

    /**
     * Check HTTP response for exceptions.
     *
     * @param connection HTTP connection
     * @throws CdbException when CDB error is detected
     */
    public static void checkHttpResponseForCdbException(HttpURLConnection connection) throws CdbException {
        String exceptionType = connection.getHeaderField(CdbHttpHeader.CDB_EXCEPTION_TYPE_HEADER);
        if (exceptionType != null) {
            String statusMessage = connection.getHeaderField(CdbHttpHeader.CDB_STATUS_MESSAGE_HEADER);
            String statusCode = connection.getHeaderField(CdbHttpHeader.CDB_STATUS_CODE_HEADER);
            int code = Integer.parseInt(statusCode);
            CdbExceptionFactory.throwCdbException(exceptionType, code, statusMessage);
        }
    }

    /**
     * Convert HTTP error for exceptions.
     *
     * @param httpError HTTP error
     * @param connection HTTP connection
     * @return generated CDB exception
     */
    public static CdbException convertHttpErrorToCdbException(Exception httpError, HttpURLConnection connection) {
        String exceptionType = connection.getHeaderField(CdbHttpHeader.CDB_EXCEPTION_TYPE_HEADER);
        if (exceptionType != null) {
            String statusMessage = connection.getHeaderField(CdbHttpHeader.CDB_STATUS_MESSAGE_HEADER);
            String statusCode = connection.getHeaderField(CdbHttpHeader.CDB_STATUS_CODE_HEADER);
            int code = Integer.parseInt(statusCode);
            return CdbExceptionFactory.generateCdbException(exceptionType, code, statusMessage);
        } else {
            return new CdbException(httpError);
        }
    }

    /**
     * Get full request URL.
     *
     * @param requestUrl relative request path, e.g. /object
     * @return full request URL string, e.g. http://localhost:17524/cdb/object
     */
    public String getFullRequestUrl(String requestUrl) {
        String url = serviceUrl + requestUrl;
        return url;
    }

    /**
     * Verify session cookie.
     *
     * @return session cookie
     * @throws InvalidSession if session cookie is expired or null
     */
    public String verifySessionCookie() throws InvalidSession {
        return session.verifyCookie();
    }

    /*
     * Get all response headers in a single string.
     *
     * @param connection HTTP connection 
     * @return string containing response headers
     */
    private static String getResponseHeaders(HttpURLConnection connection) {
        String headerString = "";
        Map<String, List<String>> headerMap = connection.getHeaderFields();
        for (String key : headerMap.keySet()) {
            List<String> values = headerMap.get(key);
            headerString += key + ": " + values + "\n";
        }
        return headerString;
    }

    /**
     * Prepare post data.
     *
     * @param data key/value data map
     * @return string suitable for HTTP post request
     * @throws InvalidArgument in case of invalid input data
     */
    public static String preparePostData(Map<String, String> data) throws InvalidArgument {
        try {
            String postData = "";
            String separator = "";
            for (String key : data.keySet()) {
                postData += separator + key + "=" + URLEncoder.encode(data.get(key), "UTF8");
                separator = "&";
            }
            return postData;
        } catch (UnsupportedEncodingException ex) {
            logger.error("Invalid argument: " + ex);
            throw new InvalidArgument(ex);
        }
    }

    /**
     * Update session cookie from connection's HTTP headers.
     *
     * @param connection HTTP connection
     */
    private void updateSessionCookie(HttpURLConnection connection) {
        String cookie = connection.getHeaderField(CdbHttpHeader.CDB_SET_COOKIE_HEADER);
        if (cookie != null) {
            session.setCookie(cookie);
            logger.debug("Updated session cookie: " + cookie);
        }
        String sessionRole = connection.getHeaderField(CdbHttpHeader.CDB_SESSION_ROLE_HEADER);
        if (sessionRole != null) {
            session.setRole(CdbRole.fromString(sessionRole));
            logger.debug("Updated session role: " + sessionRole);
        }
    }

    /**
     * Send post data.
     *
     * @param data key/value data map
     * @param connection HTTP connection
     * @throws InvalidArgument in case there is a problem with post data
     * @throws CdbException in case of any other error
     */
    private static void sendPostData(Map<String, String> data, HttpURLConnection connection) throws InvalidArgument, CdbException {
        String postData = preparePostData(data);
        try (DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
            dos.writeBytes(postData);
            dos.flush();
        } catch (IOException ex) {
            logger.error(ex);
            throw new CdbException(ex);
        }
    }

    /**
     * Read HTTP response.
     *
     * @param connection HTTP connection
     * @return HTTP response as a string
     * @throws CdbException in case of any errors
     */
    private static String readHttpResponse(HttpURLConnection connection) throws CdbException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
                sb.append('\n');
            }
            return sb.toString();
        } catch (IOException ex) {
            logger.error(ex);
            throw new CdbException(ex);
        }
    }

    /**
     * Set session cookie in the request headers.
     *
     * @param connection HTTP connection
     * @param sessionCookie session cookie (may be null)
     */
    private static void setCookieRequestHeader(HttpURLConnection connection, String sessionCookie) {
        if (sessionCookie != null) {
            connection.setRequestProperty("Cookie", sessionCookie);
            logger.debug("Setting session cookie to: " + sessionCookie);
        }
    }

    /**
     * Set common POST request headers plus session cookie.
     *
     * @param connection HTTP connection
     * @param sessionCookie session cookie (may be null)
     * @throws CdbException in case of any errors
     */
    private static void setPostRequestHeaders(HttpURLConnection connection, String sessionCookie) throws CdbException {
        setPostRequestHeaders(connection);
        setCookieRequestHeader(connection, sessionCookie);
    }

    /**
     * Set common POST request headers.
     *
     * @param connection HTTP connection
     * @throws CdbException in case of any errors
     */
    private static void setPostRequestHeaders(HttpURLConnection connection) throws CdbException {
        try {
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        } catch (ProtocolException ex) {
            logger.error(ex);
            throw new CdbException(ex);
        }
    }

    /**
     * Set common GET request headers plus session cookie.
     *
     * @param connection HTTP connection
     * @param sessionCookie session cookie (may be null)
     * @throws CdbException in case of any errors
     */
    private static void setGetRequestHeaders(HttpURLConnection connection, String sessionCookie) throws CdbException {
        setGetRequestHeaders(connection);
        setCookieRequestHeader(connection, sessionCookie);
    }

    /**
     * Set common GET request headers.
     *
     * @param connection HTTP connection
     * @throws CdbException in case of any errors
     */
    private static void setGetRequestHeaders(HttpURLConnection connection) throws CdbException {
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException ex) {
            logger.error(ex);
            throw new CdbException(ex);
        }
    }

    /**
     * Set common PUT request headers plus session cookie.
     *
     * @param connection HTTP connection
     * @param sessionCookie session cookie (may be null)
     * @throws CdbException in case of any errors
     */
    private static void setPutRequestHeaders(HttpURLConnection connection, String sessionCookie) throws CdbException {
        setPutRequestHeaders(connection);
        setCookieRequestHeader(connection, sessionCookie);
    }

    /**
     * Set common PUT request headers.
     *
     * @param connection HTTP connection
     * @throws CdbException in case of any errors
     */
    private static void setPutRequestHeaders(HttpURLConnection connection) throws CdbException {
        try {
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        } catch (ProtocolException ex) {
            logger.error(ex);
            throw new CdbException(ex);
        }
    }

    /**
     * Set common DELETE request headers plus session cookie.
     *
     * @param connection HTTP connection
     * @param sessionCookie session cookie (may be null)
     * @throws CdbException in case of any errors
     */
    private static void setDeleteRequestHeaders(HttpURLConnection connection, String sessionCookie) throws CdbException {
        setDeleteRequestHeaders(connection);
        setCookieRequestHeader(connection, sessionCookie);
    }

    /**
     * Set common DELETE request headers.
     *
     * @param connection HTTP connection
     * @throws CdbException in case of any errors
     */
    private static void setDeleteRequestHeaders(HttpURLConnection connection) throws CdbException {
        try {
            connection.setRequestMethod("DELETE");
        } catch (ProtocolException ex) {
            logger.error(ex);
            throw new CdbException(ex);
        }
    }

    /**
     * Login with a given username and password, and with specified session id.
     *
     * @param username username
     * @param password password
     * @param sessionId session id, can be null
     * @throws AuthorizationError in case of incorrect username or password
     * @throws CommunicationError in case service cannot be contacted
     * @throws CdbException in case of any other errors
     */
    public void login(String username, String password, String sessionId) throws CdbException {
        HttpURLConnection connection = null;
        try {
            String urlString = getFullRequestUrl(LOGIN_REQUEST_URL);
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            setPostRequestHeaders(connection);
            HashMap<String, String> loginData = new HashMap<>();
            loginData.put("username", username);
            loginData.put("password", password);

            logger.debug("Establishing session for user: " + username);
            logger.debug("Service URL: " + serviceUrl);
            sendPostData(loginData, connection);
            checkHttpResponseForCdbException(connection);
            session.setUsername(username);
            session.setId(sessionId);
            updateSessionCookie(connection);
        } catch (ConnectException ex) {
            String errorMsg = "Cannot connect to " + getServiceUrl();
            logger.error(errorMsg);
            throw new CommunicationError(errorMsg, ex);
        } catch (CdbException ex) {
            logger.error(ex);
            throw ex;
        } catch (IOException ex) {
            logger.error(ex);
            throw new CdbException(ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    /**
     * Login with a given username and password.
     *
     * @param username username
     * @param password password
     * @throws AuthorizationError in case of incorrect username or password
     * @throws CommunicationError in case service cannot be contacted
     * @throws CdbException in case of any other errors
     */
    public void login(String username, String password) throws CdbException {
        login(username, password, DefaultSessionId);
    }

    /**
     * Invoke GET request.
     *
     * @param requestUrl relative request path, e.g. /object
     * @return service response string
     * @throws CdbException in case of any errors
     */
    public String invokeSessionGetRequest(String requestUrl) throws CdbException {
        String urlString = getFullRequestUrl(requestUrl);
        HttpURLConnection connection = null;
        try {
            logger.debug("Invoking session get request for URL: " + requestUrl);
            String sessionCookie = session.verifyCookie();
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            setGetRequestHeaders(connection, sessionCookie);
            updateSessionCookie(connection);
            checkHttpResponseForCdbException(connection);
            logger.debug("Response message:\n" + connection.getResponseMessage());
            return readHttpResponse(connection);
        } catch (CdbException ex) {
            throw ex;
        } catch (ConnectException ex) {
            String errorMsg = "Cannot connect to " + getServiceUrl();
            logger.error(errorMsg);
            throw new CommunicationError(errorMsg, ex);
        } catch (IOException ex) {
            CdbException cdbException = convertHttpErrorToCdbException(ex, connection);
            logger.error(ex.getMessage());
            throw cdbException;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Invoke GET request.
     *
     * @param requestUrl relative request path, e.g. /object
     * @return service response string
     * @throws CdbException in case of any errors
     */
    public String invokeGetRequest(String requestUrl) throws CdbException {
        String urlString = getFullRequestUrl(requestUrl);
        HttpURLConnection connection = null;
        try {
            logger.debug("Invoking get request for URL: " + requestUrl);
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            updateSessionCookie(connection);
            checkHttpResponseForCdbException(connection);
            logger.debug("Response message:\n" + connection.getResponseMessage());
            return readHttpResponse(connection);
        } catch (CdbException ex) {
            throw ex;
        } catch (ConnectException ex) {
            String errorMsg = "Cannot connect to " + getServiceUrl();
            logger.error(errorMsg);
            throw new CommunicationError(errorMsg, ex);
        } catch (IOException ex) {
            CdbException cdbException = convertHttpErrorToCdbException(ex, connection);
            logger.error(ex.getMessage());
            throw cdbException;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Invoke POST request.
     *
     * @param requestUrl relative request path, e.g. /object
     * @param data request data
     * @return service response string
     * @throws CdbException in case of any errors
     */
    public String invokeSessionPostRequest(String requestUrl, Map<String, String> data) throws CdbException {
        String urlString = getFullRequestUrl(requestUrl);
        HttpURLConnection connection = null;
        try {
            logger.debug("Invoking session post request for URL: " + requestUrl);
            String sessionCookie = session.verifyCookie();
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            setPostRequestHeaders(connection, sessionCookie);
            sendPostData(data, connection);
            updateSessionCookie(connection);
            checkHttpResponseForCdbException(connection);
            logger.debug("Response message:\n" + connection.getResponseMessage());
            return readHttpResponse(connection);
        } catch (CdbException ex) {
            throw ex;
        } catch (ConnectException ex) {
            String errorMsg = "Cannot connect to " + getServiceUrl();
            logger.error(errorMsg);
            throw new CommunicationError(errorMsg, ex);
        } catch (IOException ex) {
            CdbException cdbException = convertHttpErrorToCdbException(ex, connection);
            logger.error(ex.getMessage());
            throw cdbException;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Invoke PUT request.
     *
     * @param requestUrl relative request path, e.g. /object
     * @param data request data
     * @return service response string
     * @throws CdbException in case of any errors
     */
    public String invokeSessionPutRequest(String requestUrl, Map<String, String> data) throws CdbException {
        String urlString = getFullRequestUrl(requestUrl);
        HttpURLConnection connection = null;
        try {
            logger.debug("Invoking session put request for URL: " + requestUrl);
            String sessionCookie = session.verifyCookie();
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            setPutRequestHeaders(connection, sessionCookie);
            sendPostData(data, connection);
            updateSessionCookie(connection);
            checkHttpResponseForCdbException(connection);
            logger.debug("Response message:\n" + connection.getResponseMessage());
            return readHttpResponse(connection);
        } catch (CdbException ex) {
            throw ex;
        } catch (ConnectException ex) {
            String errorMsg = "Cannot connect to " + getServiceUrl();
            logger.error(errorMsg);
            throw new CommunicationError(errorMsg, ex);
        } catch (IOException ex) {
            CdbException cdbException = convertHttpErrorToCdbException(ex, connection);
            logger.error(ex.getMessage());
            throw cdbException;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Invoke DELETE request.
     *
     * @param requestUrl relative request path, e.g. /cdb/object
     * @return service response string
     * @throws CdbException in case of any errors
     */
    public String invokeSessionDeleteRequest(String requestUrl) throws CdbException {
        String urlString = getFullRequestUrl(requestUrl);
        HttpURLConnection connection = null;
        try {
            logger.debug("Invoking session post request for URL: " + requestUrl);
            String sessionCookie = session.verifyCookie();
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            setDeleteRequestHeaders(connection, sessionCookie);
            updateSessionCookie(connection);
            checkHttpResponseForCdbException(connection);
            logger.debug("Response message:\n" + connection.getResponseMessage());
            return readHttpResponse(connection);
        } catch (CdbException ex) {
            throw ex;
        } catch (ConnectException ex) {
            String errorMsg = "Cannot connect to " + getServiceUrl();
            logger.error(errorMsg);
            throw new CommunicationError(errorMsg, ex);
        } catch (IOException ex) {
            CdbException cdbException = convertHttpErrorToCdbException(ex, connection);
            logger.error(ex.getMessage());
            throw cdbException;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    /*
     * Main method, used for simple testing.
     *
     * @param args main arguments
     */
    public static void main(String[] args) {
        try {
            CdbRestApi client = new CdbRestApi("http://zagreb.svdev.net:10232/cdb");
            //client.login("sveseli", "sveseli");
            HashMap<String, String> data = new HashMap<>();
            //data.put("parentDirectory", "/");
            String drawing = client.invokeGetRequest("/pdmLink/drawings/D14100201-113160.asm");
            System.out.println("Drawing: \n" + drawing);
        } catch (CdbException ex) {
            System.out.println("Sorry: " + ex);
        }
    }
}
