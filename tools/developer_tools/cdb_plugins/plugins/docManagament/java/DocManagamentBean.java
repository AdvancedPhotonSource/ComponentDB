/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.LoginController;
import gov.anl.aps.cdb.portal.controllers.PropertyValueController;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.api.DocumentManagamentApi;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.BasicContainer;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.Collection;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.CollectionSearchResult;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.DocDetail;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.Document;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@SessionScoped
@Named("docManagamentBean")
public class DocManagamentBean implements Serializable {

    private DocumentManagamentApi documentManagamentApi;

    private List<BasicContainer> containerList = null;
    private BasicContainer selectedContainer = null;

    private String dmsDocumentSearchString = null;
    private List<Collection> quickSearchDocumentResults = null;
    private Collection selectedDocument = null;
    private Document selectedDocumentObject = null;

    private static final String COLLECTION_NUM_GROUP_NAME = "CollectionNum";
    private static final Pattern collectionNumPatter
            = Pattern.compile("(\\s+|^)(?<" + COLLECTION_NUM_GROUP_NAME + ">[a-zA-Z][0-9]{1,3})(?=\\s+|$)");
    private String dmsCollectionSearchString = null;
    private List<Collection> dmsCollectionSearchResults = null;
    private Collection selectedCollection = null;

    private LoginController loginController;

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DocManagamentBean.class.getName());

    @PostConstruct
    public void init() {
        documentManagamentApi = DocManagerPlugin.createNewDocumentManagamentApi();
    }

    public String getDocManagamentSystemUrl() {
        return DocManagerPlugin.getContextRootUrlProperty();
    }

    public static DocManagamentBean getInstance() {
        return (DocManagamentBean) SessionUtility.findBean("docManagamentBean");
    }

    public void performQuickSearch() {
        if (dmsDocumentSearchString != null && !dmsDocumentSearchString.equals("")) {
            try {
                Collection[] results = documentManagamentApi.quickSearchCollection(dmsDocumentSearchString, true, 0);
                quickSearchDocumentResults = Arrays.asList(results);
            } catch (CdbException ex) {
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            }
        } else {
            SessionUtility.addErrorMessage("No Search String", "Please enter a search text before searching.");
        }
    }

    public void performCollectionSearch() {
        if (dmsCollectionSearchString != null && !dmsCollectionSearchString.equals("")) {
            Matcher matcher = collectionNumPatter.matcher(dmsCollectionSearchString);

            String collectionLabel = dmsCollectionSearchString;
            String collectionId = null;
            if (matcher.find()) {
                collectionId = matcher.group(COLLECTION_NUM_GROUP_NAME);
                collectionLabel = collectionLabel.replace(collectionId, "");
            }

            CollectionSearchResult searchCollections;
            try {
                searchCollections = documentManagamentApi.searchCollections(collectionId, collectionLabel);
            } catch (CdbException ex) {
                logger.debug(ex);
                SessionUtility.addErrorMessage("Error", ex.getMessage());
                return;
            }

            dmsCollectionSearchResults = DocumentManagamentApi.groupCollectionsAndDocuments(searchCollections);
        } else {
            SessionUtility.addErrorMessage("No Search String", "Please enter a search text before searching.");
        }

    }

    public void performCollectionSelection(String onSuccessRemoteCommand) {
        if (this.selectedCollection != null) {
            updateCurrentPropertyValue(this.selectedCollection.getCollectionId(), onSuccessRemoteCommand);
            // No need to render results anymore. Causes issues with overriding variables. 
            dmsCollectionSearchResults = null; 
        } else {
            SessionUtility.addWarningMessage("No Selection", "Please select a container item.");
        }
    }

    public void performContainerSelection(String onSuccessRemoteCommand) {
        if (this.selectedContainer != null) {
            updateCurrentPropertyValue(this.selectedContainer.getDossierId() + "", onSuccessRemoteCommand);
        } else {
            SessionUtility.addWarningMessage("No Selection", "Please select a container item.");
        }
    }

    public void performDocumentSelection(String onSuccessRemotecommand) {
        if (this.selectedDocument != null) {
            updateCurrentPropertyValue(selectedDocument.getDocNumId() + "", onSuccessRemotecommand);
        } else {
            SessionUtility.addWarningMessage("No Selection", "Please select a container item.");
        }
    }

    public void resetCollectionSearchVariables() {
        dmsCollectionSearchString = null;
        dmsCollectionSearchResults = null;
        selectedCollection = null;
    }

    public void resetContainerSearchVariables() {
        selectedContainer = null;
    }

    public void resetDocumentSearchVariables() {
        dmsDocumentSearchString = null;
        quickSearchDocumentResults = null;
        selectedDocument = null;
    }

    protected boolean loadSelectedCollectionByPropertyValue(PropertyValue propertyValue) {
        selectedCollection = null;
        if (propertyValue.getValue() != null && !propertyValue.getValue().equals("")) {
            try {
                String collectionId = propertyValue.getValue();
                CollectionSearchResult searchCollections = documentManagamentApi.searchCollections(collectionId, null);
                List<Collection> groupCollectionsAndDocuments = DocumentManagamentApi.groupCollectionsAndDocuments(searchCollections);
                if (groupCollectionsAndDocuments.size() == 1) {
                    selectedCollection = groupCollectionsAndDocuments.get(0);
                    return true;
                } else {
                    SessionUtility.addErrorMessage("Error Fetching Collection", "Got unexpected results from DMS server");
                    return false;
                }
            } catch (CdbException ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error Fetching Collection", ex.getMessage());
                return false;
            }
        } else {
            SessionUtility.addWarningMessage("No Value", "Specify a value and try again.");
            return false;
        }

    }

    public void loadDocumentInformation(Integer documentId) {
        if (loadSelectedDocumentByDocumentId(documentId)) {
            String showInfo = DocManagamentDocumentPropertyTypeHandler.INFO_ACTION_COMMAND;
            SessionUtility.executeRemoteCommand(showInfo);
        }
    }

    private boolean loadSelectedDocumentByDocumentId(int documentId) {
        try {

            selectedDocumentObject = documentManagamentApi.getDocumentById(documentId);

            // Ensure login controller loaded
            getLoginController();

            if (loginController.isLoggedIn()) {
                String username = loginController.getUsername();
                String docNumCode = selectedDocumentObject.getDocNumCode();
                DocDetail[] pdmlinkDocDetails = documentManagamentApi.getPdmlinkDocDetails(docNumCode, username);
                DocDetail[] icmsDocDetails = documentManagamentApi.getIcmsDocDetails(docNumCode, username);

                selectedDocumentObject.setPdmLinkDocDetail(Arrays.asList(pdmlinkDocDetails));
                selectedDocumentObject.setIcmsDocDetail(Arrays.asList(icmsDocDetails));
            }

            return true;
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error Fetching Doc", ex.getMessage());
            return false;
        } catch (Exception ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error Fetching Doc", ex.getMessage());
            return false;
        }
    }

    protected boolean loadSelectedDocumentObjectByPropertyValue(PropertyValue propertyValue) {
        selectedDocumentObject = null;
        if (propertyValue.getValue() != null && !propertyValue.getValue().equals("")) {
            int documentId = Integer.parseInt(propertyValue.getValue());
            return loadSelectedDocumentByDocumentId(documentId);
        } else {
            SessionUtility.addWarningMessage("No Value", "Specify a value and try again.");
            return false;
        }
    }

    private void updateCurrentPropertyValue(String value, String onSuccessRemotecommand) {
        PropertyValueController instance = PropertyValueController.getInstance();
        PropertyValue current = instance.getCurrent();
        current.setValue(value);
        current.setDisplayValue(null);
        PropertyTypeHandlerInterface handler = PropertyTypeHandlerFactory.getHandler(current);
        handler.setDisplayValue(current);

        SessionUtility.executeRemoteCommand(onSuccessRemotecommand);
    }

    public List<BasicContainer> getContainerList() {
        if (containerList == null) {
            try {
                BasicContainer[] allPublicContainers = documentManagamentApi.getAllPublicContainers();
                containerList = Arrays.asList(allPublicContainers);
            } catch (CdbException ex) {
                Logger.getLogger(DocManagamentBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return containerList;
    }

    private LoginController getLoginController() {
        if (loginController == null) {
            loginController = LoginController.getInstance();
        }
        return loginController;
    }

    public BasicContainer getSelectedContainer() {
        return selectedContainer;
    }

    public void setSelectedContainer(BasicContainer selectedContainer) {
        this.selectedContainer = selectedContainer;
    }

    public String getDmsDocumentSearchString() {
        return dmsDocumentSearchString;
    }

    public void setDmsDocumentSearchString(String dmsDocumentSearchString) {
        this.dmsDocumentSearchString = dmsDocumentSearchString;
    }

    public List<Collection> getQuickSearchDocumentResults() {
        return quickSearchDocumentResults;
    }

    public Document getSelectedDocumentObject() {
        return selectedDocumentObject;
    }

    public Collection getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument(Collection selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public String getDmsCollectionSearchString() {
        return dmsCollectionSearchString;
    }

    public void setDmsCollectionSearchString(String dmsCollectionSearchString) {
        this.dmsCollectionSearchString = dmsCollectionSearchString;
    }

    public List<Collection> getDmsCollectionSearchResults() {
        return dmsCollectionSearchResults;
    }

    public Collection getSelectedCollection() {
        return selectedCollection;
    }

    public void setSelectedCollection(Collection selectedCollection) {
        this.selectedCollection = selectedCollection;
    }

    public static void printCollectionNumberInString(String input) {
        Matcher matcher = DocManagamentBean.collectionNumPatter.matcher(input);

        boolean result = matcher.find();

        if (result) {
            System.out.println(matcher.group(DocManagamentBean.COLLECTION_NUM_GROUP_NAME));
        }
    }

    public static void main(String[] args) {
        printCollectionNumberInString("Hello World A123");
        printCollectionNumberInString("A234");
        printCollectionNumberInString("A345 something");
        printCollectionNumberInString("something A456 something");
        printCollectionNumberInString("something A567 something B678");
    }

}
