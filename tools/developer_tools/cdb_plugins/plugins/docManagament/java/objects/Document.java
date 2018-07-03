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
public class Document extends CdbObject{
    
    private String collectionId; 
    private Collection collection; 
    private char approvalReq;
    private Long createDate;
    private Integer createdBy; 
    private String docNumCode;
    private Integer docNumId;
    private Integer docTypeAttrId;
    private String docTypeId;
    private char documentActive;
    private String documentDesc;
    private String documentId;
    private String documentOwner;
    private String documentTitle;
    private String locationId;
    private UserInfo owner;     
    private Object projectAttrId; 
    private String projectId;
    private Integer repositoryAttrId;
    private Object systemAttrId;
    private String systemId;
    private Long updateDate;
    private String updatedBy; 

    public String getCollectionId() {
        return collectionId;
    }

    public Collection getCollection() {
        return collection;
    }

    public char getApprovalReq() {
        return approvalReq;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public String getDocNumCode() {
        return docNumCode;
    }

    public Integer getDocNumId() {
        return docNumId;
    }

    public Integer getDocTypeAttrId() {
        return docTypeAttrId;
    }

    public String getDocTypeId() {
        return docTypeId;
    }

    public char getDocumentActive() {
        return documentActive;
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

    public String getLocationId() {
        return locationId;
    }

    public UserInfo getOwner() {
        return owner;
    }

    public Object getProjectAttrId() {
        return projectAttrId;
    }

    public String getProjectId() {
        return projectId;
    }

    public Integer getRepositoryAttrId() {
        return repositoryAttrId;
    }

    public Object getSystemAttrId() {
        return systemAttrId;
    }

    public String getSystemId() {
        return systemId;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }
    
    
}
