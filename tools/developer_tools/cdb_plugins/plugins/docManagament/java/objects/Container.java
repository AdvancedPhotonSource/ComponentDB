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
public class Container extends CdbObject {
    
    private Integer dossierId;
    private String dossierName;
    private String dossierDesc;
    private String dossierScope;
    private String dossierJson;
    private UserInfo owner; 
    private String updatedBy;
    private String updateDate; 
    private String createdBy;
    private String createDate; 
    private String dossierActive;
    private String dossierOwner; 

    public Integer getDossierId() {
        return dossierId;
    }

    public String getDossierName() {
        return dossierName;
    }

    public String getDossierDesc() {
        return dossierDesc;
    }

    public String getDossierScope() {
        return dossierScope;
    }

    public String getDossierJson() {
        return dossierJson;
    }

    public UserInfo getOwner() {
        return owner;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getDossierActive() {
        return dossierActive;
    }

    public String getDossierOwner() {
        return dossierOwner;
    }
    
}
