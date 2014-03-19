/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "rec_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RecType.findAll", query = "SELECT r FROM RecType r"),
    @NamedQuery(name = "RecType.findByRecTypeId", query = "SELECT r FROM RecType r WHERE r.recTypeId = :recTypeId"),
    @NamedQuery(name = "RecType.findByRecType", query = "SELECT r FROM RecType r WHERE r.recType = :recType")})
public class RecType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rec_type_id")
    private Integer recTypeId;
    @Size(max = 24)
    @Column(name = "rec_type")
    private String recType;
    @OneToMany(mappedBy = "recTypeId")
    private List<RecTypeDevSup> recTypeDevSupList;
    @OneToMany(mappedBy = "recTypeId")
    private List<FldType> fldTypeList;
    @JoinColumn(name = "ioc_resource_id", referencedColumnName = "ioc_resource_id")
    @ManyToOne
    private IocResource iocResourceId;
    @JoinColumn(name = "ioc_boot_id", referencedColumnName = "ioc_boot_id")
    @ManyToOne
    private IocBoot iocBootId;
    @OneToMany(mappedBy = "recTypeId")
    private List<Rec> recList;

    public RecType() {
    }

    public RecType(Integer recTypeId) {
        this.recTypeId = recTypeId;
    }

    public Integer getRecTypeId() {
        return recTypeId;
    }

    public void setRecTypeId(Integer recTypeId) {
        this.recTypeId = recTypeId;
    }

    public String getRecType() {
        return recType;
    }

    public void setRecType(String recType) {
        this.recType = recType;
    }

    @XmlTransient
    public List<RecTypeDevSup> getRecTypeDevSupList() {
        return recTypeDevSupList;
    }

    public void setRecTypeDevSupList(List<RecTypeDevSup> recTypeDevSupList) {
        this.recTypeDevSupList = recTypeDevSupList;
    }

    @XmlTransient
    public List<FldType> getFldTypeList() {
        return fldTypeList;
    }

    public void setFldTypeList(List<FldType> fldTypeList) {
        this.fldTypeList = fldTypeList;
    }

    public IocResource getIocResourceId() {
        return iocResourceId;
    }

    public void setIocResourceId(IocResource iocResourceId) {
        this.iocResourceId = iocResourceId;
    }

    public IocBoot getIocBootId() {
        return iocBootId;
    }

    public void setIocBootId(IocBoot iocBootId) {
        this.iocBootId = iocBootId;
    }

    @XmlTransient
    public List<Rec> getRecList() {
        return recList;
    }

    public void setRecList(List<Rec> recList) {
        this.recList = recList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recTypeId != null ? recTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RecType)) {
            return false;
        }
        RecType other = (RecType) object;
        if ((this.recTypeId == null && other.recTypeId != null) || (this.recTypeId != null && !this.recTypeId.equals(other.recTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.RecType[ recTypeId=" + recTypeId + " ]";
    }
    
}
