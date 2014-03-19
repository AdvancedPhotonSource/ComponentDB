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
@Table(name = "mfg")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Mfg.findAll", query = "SELECT m FROM Mfg m"),
    @NamedQuery(name = "Mfg.findByMfgId", query = "SELECT m FROM Mfg m WHERE m.mfgId = :mfgId"),
    @NamedQuery(name = "Mfg.findByMfgName", query = "SELECT m FROM Mfg m WHERE m.mfgName = :mfgName"),
    @NamedQuery(name = "Mfg.findByVersion", query = "SELECT m FROM Mfg m WHERE m.version = :version"),
    @NamedQuery(name = "Mfg.findByMarkForDelete", query = "SELECT m FROM Mfg m WHERE m.markForDelete = :markForDelete")})
public class Mfg implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "mfg_id")
    private Integer mfgId;
    @Size(max = 100)
    @Column(name = "mfg_name")
    private String mfgName;
    @Column(name = "version")
    private Integer version;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @OneToMany(mappedBy = "mfg")
    private List<ComponentType> componentTypeList;

    public Mfg() {
    }

    public Mfg(Integer mfgId) {
        this.mfgId = mfgId;
    }

    public Integer getMfgId() {
        return mfgId;
    }

    public void setMfgId(Integer mfgId) {
        this.mfgId = mfgId;
    }

    public String getMfgName() {
        return mfgName;
    }

    public void setMfgName(String mfgName) {
        this.mfgName = mfgName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getMarkForDelete() {
        return markForDelete;
    }

    public void setMarkForDelete(Boolean markForDelete) {
        this.markForDelete = markForDelete;
    }

    @XmlTransient
    public List<ComponentType> getComponentTypeList() {
        return componentTypeList;
    }

    public void setComponentTypeList(List<ComponentType> componentTypeList) {
        this.componentTypeList = componentTypeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mfgId != null ? mfgId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Mfg)) {
            return false;
        }
        Mfg other = (Mfg) object;
        if ((this.mfgId == null && other.mfgId != null) || (this.mfgId != null && !this.mfgId.equals(other.mfgId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.Mfg[ mfgId=" + mfgId + " ]";
    }
    
}
