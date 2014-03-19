/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "rec_client")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RecClient.findAll", query = "SELECT r FROM RecClient r"),
    @NamedQuery(name = "RecClient.findByRecClientId", query = "SELECT r FROM RecClient r WHERE r.recClientId = :recClientId"),
    @NamedQuery(name = "RecClient.findByRecClientTypeId", query = "SELECT r FROM RecClient r WHERE r.recClientTypeId = :recClientTypeId"),
    @NamedQuery(name = "RecClient.findByRecNm", query = "SELECT r FROM RecClient r WHERE r.recNm = :recNm"),
    @NamedQuery(name = "RecClient.findByFldType", query = "SELECT r FROM RecClient r WHERE r.fldType = :fldType"),
    @NamedQuery(name = "RecClient.findByVuriId", query = "SELECT r FROM RecClient r WHERE r.vuriId = :vuriId"),
    @NamedQuery(name = "RecClient.findByCurrentLoad", query = "SELECT r FROM RecClient r WHERE r.currentLoad = :currentLoad")})
public class RecClient implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rec_client_id")
    private Integer recClientId;
    @Column(name = "rec_client_type_id")
    private Integer recClientTypeId;
    @Size(max = 128)
    @Column(name = "rec_nm")
    private String recNm;
    @Size(max = 24)
    @Column(name = "fld_type")
    private String fldType;
    @Column(name = "vuri_id")
    private Integer vuriId;
    @Column(name = "current_load")
    private Boolean currentLoad;

    public RecClient() {
    }

    public RecClient(Integer recClientId) {
        this.recClientId = recClientId;
    }

    public Integer getRecClientId() {
        return recClientId;
    }

    public void setRecClientId(Integer recClientId) {
        this.recClientId = recClientId;
    }

    public Integer getRecClientTypeId() {
        return recClientTypeId;
    }

    public void setRecClientTypeId(Integer recClientTypeId) {
        this.recClientTypeId = recClientTypeId;
    }

    public String getRecNm() {
        return recNm;
    }

    public void setRecNm(String recNm) {
        this.recNm = recNm;
    }

    public String getFldType() {
        return fldType;
    }

    public void setFldType(String fldType) {
        this.fldType = fldType;
    }

    public Integer getVuriId() {
        return vuriId;
    }

    public void setVuriId(Integer vuriId) {
        this.vuriId = vuriId;
    }

    public Boolean getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(Boolean currentLoad) {
        this.currentLoad = currentLoad;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recClientId != null ? recClientId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RecClient)) {
            return false;
        }
        RecClient other = (RecClient) object;
        if ((this.recClientId == null && other.recClientId != null) || (this.recClientId != null && !this.recClientId.equals(other.recClientId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.RecClient[ recClientId=" + recClientId + " ]";
    }
    
}
