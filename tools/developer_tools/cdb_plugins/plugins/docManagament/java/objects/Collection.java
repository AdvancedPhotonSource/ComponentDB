/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament.objects;

import gov.anl.aps.cdb.common.objects.CdbObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author djarosz
 */
public class Collection extends CdbObject{
    
    private String collectionId; 
    private String collectionLabel;
    private String collectionOwner;
    private Integer docNumId;
    private String docNum;
    private String docTypeAttr;
    private String docTypeDesc; 
    private String documentCreateDate;
    private String documentDesc;
    private String documentId;
    private String documentOwner;
    private String documentTitle;
    private String documentUpdateDate;
    private String locationDesc;
    private String projectDesc; 
    private String repository;
    private String repositoryAttr; 
    private Object systemAttr; 
    private String systemDesc; 
    
    private List<Collection> documents; 

    public Collection(String collectionId, String collectionLabel, String collectionOwner) {
        this.collectionId = collectionId;
        this.collectionLabel = collectionLabel;
        this.collectionOwner = collectionOwner;
        
        documents = new ArrayList<>(); 
    }

    public String getCollectionId() {
        return collectionId;
    }

    public String getCollectionLabel() {
        return collectionLabel;
    }

    public String getCollectionOwner() {
        return collectionOwner;
    }

    public Integer getDocNumId() {
        return docNumId;
    }

    public String getDocNum() {
        return docNum;
    }

    public String getDocTypeAttr() {
        return docTypeAttr;
    }

    public String getDocTypeDesc() {
        return docTypeDesc;
    }

    public String getDocumentCreateDate() {
        return documentCreateDate;
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

    public String getDocumentUpdateDate() {
        return documentUpdateDate;
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

    public String getRepositoryAttr() {
        return repositoryAttr;
    }

    public Object getSystemAttr() {
        return systemAttr;
    }

    public String getSystemDesc() {
        return systemDesc;
    }    

    public List<Collection> getDocuments() {
        return documents;
    }
    
    public int getNumberOfDocuments() {
        if (documents != null) {
            return documents.size(); 
        }
        return 0; 
    }
    
}
