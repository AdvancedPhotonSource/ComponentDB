/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament.objects;

import gov.anl.aps.cdb.common.objects.CdbObject;

/**
 *
 * @author djarosz
 */
public class DocDetail extends CdbObject {
    
    private String collectionId;
    private String collectionLabel;
    private String collectionOwner; 
    private String docNum; 
    private String docTypeDesc; 
    private String documentDesc;    
    private String documentId; 
    private String documentOwner; 
    private String documentTitle; 
    private String documentCreateDate;
    private String documentUpdateDate;
    private String fileName;
    private String infoLink; 
    private String link; 
    private String locationDesc; 
    private String projectDesc; 
    private String repository; 
    private String systemDesc; 
    private String state; 
    private String version; 

    public String getCollectionId() {
        return collectionId;
    }

    public String getCollectionLabel() {
        return collectionLabel;
    }

    public String getCollectionOwner() {
        return collectionOwner;
    }

    public String getDocNum() {
        return docNum;
    }

    public String getDocTypeDesc() {
        return docTypeDesc;
    }

    public String getDocumentDesc() {
        return documentDesc;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getDocumentOwner() {
        return documentOwner;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public String getDocumentCreateDate() {
        return documentCreateDate;
    }

    public String getDocumentUpdateDate() {
        return documentUpdateDate;
    }

    public String getFileName() {
        return fileName;
    }

    public String getInfoLink() {
        return infoLink;
    }

    public String getLink() {
        return link;
    }

    public String getLocationDesc() {
        return locationDesc;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public String getRepository() {
        return repository;
    }

    public String getSystemDesc() {
        return systemDesc;
    }

    public String getState() {
        return state;
    }

    public String getVersion() {
        return version;
    }      
    
}
