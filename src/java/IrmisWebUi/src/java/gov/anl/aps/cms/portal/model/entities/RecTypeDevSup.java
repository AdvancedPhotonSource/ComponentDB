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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "rec_type_dev_sup")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RecTypeDevSup.findAll", query = "SELECT r FROM RecTypeDevSup r"),
    @NamedQuery(name = "RecTypeDevSup.findByRecTypeDevSupId", query = "SELECT r FROM RecTypeDevSup r WHERE r.recTypeDevSupId = :recTypeDevSupId"),
    @NamedQuery(name = "RecTypeDevSup.findByDtypStr", query = "SELECT r FROM RecTypeDevSup r WHERE r.dtypStr = :dtypStr"),
    @NamedQuery(name = "RecTypeDevSup.findByDevSupDset", query = "SELECT r FROM RecTypeDevSup r WHERE r.devSupDset = :devSupDset"),
    @NamedQuery(name = "RecTypeDevSup.findByDevSupIoType", query = "SELECT r FROM RecTypeDevSup r WHERE r.devSupIoType = :devSupIoType")})
public class RecTypeDevSup implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rec_type_dev_sup_id")
    private Integer recTypeDevSupId;
    @Size(max = 50)
    @Column(name = "dtyp_str")
    private String dtypStr;
    @Size(max = 50)
    @Column(name = "dev_sup_dset")
    private String devSupDset;
    @Size(max = 50)
    @Column(name = "dev_sup_io_type")
    private String devSupIoType;
    @JoinColumn(name = "rec_type_id", referencedColumnName = "rec_type_id")
    @ManyToOne
    private RecType recTypeId;

    public RecTypeDevSup() {
    }

    public RecTypeDevSup(Integer recTypeDevSupId) {
        this.recTypeDevSupId = recTypeDevSupId;
    }

    public Integer getRecTypeDevSupId() {
        return recTypeDevSupId;
    }

    public void setRecTypeDevSupId(Integer recTypeDevSupId) {
        this.recTypeDevSupId = recTypeDevSupId;
    }

    public String getDtypStr() {
        return dtypStr;
    }

    public void setDtypStr(String dtypStr) {
        this.dtypStr = dtypStr;
    }

    public String getDevSupDset() {
        return devSupDset;
    }

    public void setDevSupDset(String devSupDset) {
        this.devSupDset = devSupDset;
    }

    public String getDevSupIoType() {
        return devSupIoType;
    }

    public void setDevSupIoType(String devSupIoType) {
        this.devSupIoType = devSupIoType;
    }

    public RecType getRecTypeId() {
        return recTypeId;
    }

    public void setRecTypeId(RecType recTypeId) {
        this.recTypeId = recTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recTypeDevSupId != null ? recTypeDevSupId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RecTypeDevSup)) {
            return false;
        }
        RecTypeDevSup other = (RecTypeDevSup) object;
        if ((this.recTypeDevSupId == null && other.recTypeDevSupId != null) || (this.recTypeDevSupId != null && !this.recTypeDevSupId.equals(other.recTypeDevSupId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.RecTypeDevSup[ recTypeDevSupId=" + recTypeDevSupId + " ]";
    }
    
}
