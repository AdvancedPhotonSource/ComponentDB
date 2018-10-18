/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament.api;

import gov.anl.aps.cdb.api.CdbRestApi;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.objects.CdbObjectFactory;
import gov.anl.aps.cdb.common.utilities.ArgumentUtility;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.Collection;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.Container;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gov.anl.aps.cdb.common.exceptions.CommunicationError;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.BasicContainer;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.CollectionSearch;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.CollectionSearchResult;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.DocDetail;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.Document;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author djarosz
 */
public class DocumentManagamentApi extends CdbRestApi {

    private static final Gson gson = new GsonBuilder().create();

    protected static final String REST_GET_CONTAINER_BY_ID_PATH = "/data/getDossier/";
    protected static final String REST_GET_QUICK_SEARCH_PATH = "/data/dns/quickSearch/";
    protected static final String REST_GET_PUBLIC_COLLECTIONS_PATH = "/data/getDossiers/0";
    protected static final String REST_GET_DOCUMENT_DETAILS = "/data/getDocument/";
    protected static final String REST_POST_SEARCH_COLLECTIONS = "/data/dns/documentSearch/0/999999";
    
    protected static final String REST_GET_ICMS_DOCINFO = "/data/icms/docDetail";
    protected static final String REST_GET_PDMLINK_DOCINFO = "/data/pdmlink/docDetail";
            

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DocumentManagamentApi.class.getName());

    /**
     * Constructor.
     *
     * @throws ConfigurationError if web service URL property is malformed or
     * null
     */
    public DocumentManagamentApi() throws ConfigurationError {
        super();
    }

    /**
     * Constructor.
     *
     * @param webServiceUrl web service URL
     * @throws ConfigurationError if web service URL is malformed or null
     */
    public DocumentManagamentApi(String webServiceUrl) throws ConfigurationError {
        super(webServiceUrl);
    }

    public Document getDocumentById(Integer documentId) throws InvalidArgument, CdbException {
        ArgumentUtility.verifyPositiveInteger("document id", documentId);
        String requestPath = REST_GET_DOCUMENT_DETAILS + documentId;
        String jsonString = invokeGetRequest(requestPath);
        Document document = (Document) CdbObjectFactory.createCdbObject(jsonString, Document.class);
        if (document.getDocumentId() == null) {
            throw new CdbException("Cannot find DMS document with id: " + documentId); 
        }
        
        return document;              
    }

    public Container getContainerById(Integer containerId) throws InvalidArgument, CdbException {
        ArgumentUtility.verifyPositiveInteger("collection id", containerId);
        String requestPath = REST_GET_CONTAINER_BY_ID_PATH + containerId;
        String jsonString = invokeGetRequest(requestPath);
        Container container = (Container) CdbObjectFactory.createCdbObject(jsonString, Container.class);
        if (container.getDossierId() == null) {
            throw new CdbException("Cannot find DMS container with id: " + containerId); 
        }
        return container;
    }
    
    public DocDetail[] getIcmsDocDetails(String documentNum, String username) throws InvalidArgument, CdbException {
        return getDocDetail(REST_GET_ICMS_DOCINFO, documentNum, username); 
    }
    
    public DocDetail[] getPdmlinkDocDetails(String documentNum, String username) throws InvalidArgument, CdbException {
        return getDocDetail(REST_GET_PDMLINK_DOCINFO, documentNum, username); 
    }
    
    private DocDetail[] getDocDetail(String path, String documentNum, String username) throws InvalidArgument, CdbException {
        ArgumentUtility.verifyNonEmptyString("documentNum", documentNum);
        ArgumentUtility.verifyNonEmptyString("username", username);
        
        String requestPath = path + "/" + documentNum + "/" + username; 
        
        String jsonString = invokeGetRequest(requestPath);
        return gson.fromJson(jsonString, DocDetail[].class); 
    }

    public BasicContainer[] getAllPublicContainers() throws InvalidArgument, CdbException {
        String requestPath = REST_GET_PUBLIC_COLLECTIONS_PATH;
        String jsonString = invokeGetRequest(requestPath);
        return gson.fromJson(jsonString, BasicContainer[].class);
    }

    public Collection[] quickSearchCollection(String search, boolean hasDns, int userId) throws InvalidArgument, CdbException {
        search = search.replace(" ", "%20");
        String requestPath = REST_GET_QUICK_SEARCH_PATH + search + "/" + Boolean.toString(hasDns) + "/" + userId;
        String jsonString = invokeGetRequest(requestPath);
        return gson.fromJson(jsonString, Collection[].class);
    }

    public CollectionSearchResult searchCollections(String collectionId, String collectionLabel) throws CdbException {
        CollectionSearch collectionSearch = new CollectionSearch(collectionId, collectionLabel);
        String requestData = gson.toJson(collectionSearch);
        String jsonString = invokePOSTRequest(REST_POST_SEARCH_COLLECTIONS, requestData);

        CollectionSearchResult collectionsResult;
        // API inconsistency patch.         
        if (jsonString.contains("\"docs\":{}")) {
            collectionsResult = new CollectionSearchResult(0, new ArrayList<>());
        } else {
            collectionsResult = 
                    (CollectionSearchResult) CdbObjectFactory.createCdbObject(jsonString, CollectionSearchResult.class);
        }
        return collectionsResult;
    }

    public static List<Collection> groupCollectionsAndDocuments(CollectionSearchResult collectionSearchResult) {
        String lastCollection = null;
        List<Collection> collections = new ArrayList<>();
        Collection newCollection = null;
        for (Collection collection : collectionSearchResult.getDocs()) {
            String currentCollectionId = collection.getCollectionId();
            if (lastCollection == null || !lastCollection.equals(currentCollectionId)) {
                lastCollection = currentCollectionId;
                newCollection = new Collection(collection.getCollectionId(),
                        collection.getCollectionLabel(),
                        collection.getCollectionOwner());
                collections.add(newCollection);
            }
            newCollection.getDocuments().add(collection);
        }
        return collections;
    }

    private String invokePOSTRequest(String requestUrl, String data) throws CdbException {
        String urlString = getFullRequestUrl(requestUrl);
        HttpURLConnection connection = null;
        try {
            logger.debug("Invoking get request for URL: " + requestUrl);
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            // Set Headers
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            // Send data 
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.writeBytes(data);
            dos.flush();

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

    public static void main(String[] args) {

        try {
            DocumentManagamentApi api = new DocumentManagamentApi("https://....");

            CollectionSearchResult searchCollections = api.searchCollections("A0", "test");
            List<Collection> groupCollectionsAndDocuments = groupCollectionsAndDocuments(searchCollections);

            Collection[] results = api.quickSearchCollection("Quadrupole", true, 0);
            BasicContainer[] allPublicContainers = api.getAllPublicContainers();
            System.out.println(results);

        } catch (ConfigurationError ex) {
            Logger.getLogger(DocumentManagamentApi.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (InvalidArgument ex) {
            Logger.getLogger(DocumentManagamentApi.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (CdbException ex) {
            Logger.getLogger(DocumentManagamentApi.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

}
