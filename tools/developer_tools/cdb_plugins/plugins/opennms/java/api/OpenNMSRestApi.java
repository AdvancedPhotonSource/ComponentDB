/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.opennms.api;

import gov.anl.aps.cdb.portal.plugins.support.traveler.api.*;
import com.google.gson.Gson;
import gov.anl.aps.cdb.api.*;
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
import gov.anl.aps.cdb.portal.plugins.support.opennms.objects.BusinessService;
import gov.anl.aps.cdb.portal.plugins.support.opennms.objects.OpenNMSObjectFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.net.ssl.HttpsURLConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.shaded.json.JSONObject;

/**
 * CDB REST Web Service API class.
 *
 * This class serves as superclass for all CDB web service interface classes. It
 * handles basic communication with web service (establishing sessions, sending
 * requests, receiving responses, generating exceptions, etc.).
 */
public class OpenNMSRestApi {

    private static final boolean httpsInitialized = initializeHttpsConnection();    

    private static final Logger logger = LogManager.getLogger(OpenNMSRestApi.class.getName());

    private static boolean initializeHttpsConnection() {
        HttpsURLConnection.setDefaultSSLSocketFactory(new NoServerVerificationSSLSocketFactory());
        return true;
    }
    
    private URL serviceUrl;        
    private String basicAuthCredentials;        
    
    public OpenNMSRestApi(String webServiceUrl, String basicAuthUser, String basicAuthPass) throws ConfigurationError {        
        String basicAuthUserPass = basicAuthUser + ":" + basicAuthPass; 
        this.basicAuthCredentials = Base64.getEncoder().encodeToString(basicAuthUserPass.getBytes()); 
        
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
            throw new ConfigurationError("Web service url is not specified.");
        }
        try {
            serviceUrl = new URL(webServiceUrl);
        } catch (MalformedURLException ex) {
            throw new ConfigurationError("Malformed web service url: " + webServiceUrl);
        }

        CdbServiceProtocol protocol = CdbServiceProtocol.fromString(serviceUrl.getProtocol());
        if (protocol == null) {
            throw new ConfigurationError("Unsupported service protocol specified in " + webServiceUrl);
        }
    }

    public URL getServiceUrl() {
        return serviceUrl;
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
    
    private HttpURLConnection openConnection(URL url) throws IOException{
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();        
        connection.setRequestProperty("Authorization", "Basic " + this.basicAuthCredentials);
        connection.setRequestProperty("Accept", "application/json");
        return connection;
    }

    /**
     * Invoke GET request.
     *
     * @param requestUrl relative request path, e.g. /object
     * @return service response string
     * @throws CdbException in case of any errors
     */
    public String invokeGetRequest(String requestUrl) throws IOException, CdbException {
        String urlString = getFullRequestUrl(requestUrl);
        HttpURLConnection connection = null;
        try {
            logger.debug("Invoking get request for URL: " + requestUrl);
            URL url = new URL(urlString);
            connection = openConnection(url);             
            logger.debug("Response message:\n" + connection.getResponseMessage());
            return readHttpResponse(connection);   
        } catch (IOException ex) {            
            logger.error(ex.getMessage());
            throw ex;
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
        String webServiceUrl = ""; 
        String basicAuthUser = ""; 
        String basicAuthPass = ""; 
        
        try {
            OpenNMSRestApi api = new OpenNMSRestApi(webServiceUrl, basicAuthUser, basicAuthPass);
            
            String result = api.invokeGetRequest("/api/v2/business-services/111");
            
            BusinessService objectResult = OpenNMSObjectFactory.createObject(result, BusinessService.class);
            
            System.out.println(result);
            
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(OpenNMSRestApi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
