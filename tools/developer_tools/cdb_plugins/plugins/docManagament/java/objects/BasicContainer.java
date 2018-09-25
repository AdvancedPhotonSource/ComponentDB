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
public class BasicContainer extends CdbObject {
    
    private Integer dossierId;
    private String dossierName;   
    private String owner; 
    private String groupBy;
    private String groupTemp;     

    public Integer getDossierId() {
        return dossierId;
    }

    public String getDossierName() {
        return dossierName;
    }

    public String getOwner() {
        return owner;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public String getGroupTemp() {
        return groupTemp;
    }

}
