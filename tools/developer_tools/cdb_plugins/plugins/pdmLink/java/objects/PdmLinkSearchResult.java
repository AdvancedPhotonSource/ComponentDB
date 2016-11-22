/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.pdmLink.objects;

import gov.anl.aps.cdb.common.objects.CdbObject;

/**
 *
 * @author djarosz
 */
public class PdmLinkSearchResult extends CdbObject {
    
    private String ufid;
    private String oid;
    private String modifyStamp; 
    private String number; 

    public String getUfid() {
        return ufid;
    }

    public String getOid() {
        return oid;
    }

    public String getModifyStamp() {
        return modifyStamp;
    }

    public void setUfid(String ufid) {
        this.ufid = ufid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public void setModifyStamp(String modifyStamp) {
        this.modifyStamp = modifyStamp;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
