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
import javax.persistence.Lob;
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
@Table(name = "vuri_rel")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VuriRel.findAll", query = "SELECT v FROM VuriRel v"),
    @NamedQuery(name = "VuriRel.findByVuriRelId", query = "SELECT v FROM VuriRel v WHERE v.vuriRelId = :vuriRelId"),
    @NamedQuery(name = "VuriRel.findByParentVuriId", query = "SELECT v FROM VuriRel v WHERE v.parentVuriId = :parentVuriId"),
    @NamedQuery(name = "VuriRel.findByChildVuriId", query = "SELECT v FROM VuriRel v WHERE v.childVuriId = :childVuriId")})
public class VuriRel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "vuri_rel_id")
    private Integer vuriRelId;
    @Column(name = "parent_vuri_id")
    private Integer parentVuriId;
    @Column(name = "child_vuri_id")
    private Integer childVuriId;
    @Lob
    @Size(max = 65535)
    @Column(name = "rel_info")
    private String relInfo;

    public VuriRel() {
    }

    public VuriRel(Integer vuriRelId) {
        this.vuriRelId = vuriRelId;
    }

    public Integer getVuriRelId() {
        return vuriRelId;
    }

    public void setVuriRelId(Integer vuriRelId) {
        this.vuriRelId = vuriRelId;
    }

    public Integer getParentVuriId() {
        return parentVuriId;
    }

    public void setParentVuriId(Integer parentVuriId) {
        this.parentVuriId = parentVuriId;
    }

    public Integer getChildVuriId() {
        return childVuriId;
    }

    public void setChildVuriId(Integer childVuriId) {
        this.childVuriId = childVuriId;
    }

    public String getRelInfo() {
        return relInfo;
    }

    public void setRelInfo(String relInfo) {
        this.relInfo = relInfo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vuriRelId != null ? vuriRelId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VuriRel)) {
            return false;
        }
        VuriRel other = (VuriRel) object;
        if ((this.vuriRelId == null && other.vuriRelId != null) || (this.vuriRelId != null && !this.vuriRelId.equals(other.vuriRelId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.VuriRel[ vuriRelId=" + vuriRelId + " ]";
    }
    
}
