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
@Table(name = "aoi_relation_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AoiRelationType.findAll", query = "SELECT a FROM AoiRelationType a"),
    @NamedQuery(name = "AoiRelationType.findByAoiRelationTypeId", query = "SELECT a FROM AoiRelationType a WHERE a.aoiRelationTypeId = :aoiRelationTypeId"),
    @NamedQuery(name = "AoiRelationType.findByAoiRelation", query = "SELECT a FROM AoiRelationType a WHERE a.aoiRelation = :aoiRelation")})
public class AoiRelationType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aoi_relation_type_id")
    private Integer aoiRelationTypeId;
    @Size(max = 40)
    @Column(name = "aoi_relation")
    private String aoiRelation;
    @OneToMany(mappedBy = "aoi2RelationTypeId")
    private List<AoiRelation> aoiRelationList;
    @OneToMany(mappedBy = "aoi1RelationTypeId")
    private List<AoiRelation> aoiRelationList1;

    public AoiRelationType() {
    }

    public AoiRelationType(Integer aoiRelationTypeId) {
        this.aoiRelationTypeId = aoiRelationTypeId;
    }

    public Integer getAoiRelationTypeId() {
        return aoiRelationTypeId;
    }

    public void setAoiRelationTypeId(Integer aoiRelationTypeId) {
        this.aoiRelationTypeId = aoiRelationTypeId;
    }

    public String getAoiRelation() {
        return aoiRelation;
    }

    public void setAoiRelation(String aoiRelation) {
        this.aoiRelation = aoiRelation;
    }

    @XmlTransient
    public List<AoiRelation> getAoiRelationList() {
        return aoiRelationList;
    }

    public void setAoiRelationList(List<AoiRelation> aoiRelationList) {
        this.aoiRelationList = aoiRelationList;
    }

    @XmlTransient
    public List<AoiRelation> getAoiRelationList1() {
        return aoiRelationList1;
    }

    public void setAoiRelationList1(List<AoiRelation> aoiRelationList1) {
        this.aoiRelationList1 = aoiRelationList1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aoiRelationTypeId != null ? aoiRelationTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AoiRelationType)) {
            return false;
        }
        AoiRelationType other = (AoiRelationType) object;
        if ((this.aoiRelationTypeId == null && other.aoiRelationTypeId != null) || (this.aoiRelationTypeId != null && !this.aoiRelationTypeId.equals(other.aoiRelationTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AoiRelationType[ aoiRelationTypeId=" + aoiRelationTypeId + " ]";
    }
    
}
