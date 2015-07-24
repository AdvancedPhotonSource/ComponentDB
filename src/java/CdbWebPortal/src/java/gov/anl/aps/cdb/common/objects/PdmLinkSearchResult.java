/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.common.objects;

/**
 *
 * @author djarosz
 */
public class PdmLinkSearchResult extends CdbObject {
    
    private String ufid;
    private String oid;
    private String modifyStamp; 

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
    
    
}
