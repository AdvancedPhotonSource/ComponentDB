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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "aoi_relation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AoiRelation.findAll", query = "SELECT a FROM AoiRelation a"),
    @NamedQuery(name = "AoiRelation.findByAoiRelationId", query = "SELECT a FROM AoiRelation a WHERE a.aoiRelationId = :aoiRelationId")})
public class AoiRelation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aoi_relation_id")
    private Integer aoiRelationId;
    @JoinColumn(name = "aoi2_relation_type_id", referencedColumnName = "aoi_relation_type_id")
    @ManyToOne
    private AoiRelationType aoi2RelationTypeId;
    @JoinColumn(name = "aoi1_relation_type_id", referencedColumnName = "aoi_relation_type_id")
    @ManyToOne
    private AoiRelationType aoi1RelationTypeId;
    @JoinColumn(name = "aoi2_id", referencedColumnName = "aoi_id")
    @ManyToOne
    private Aoi aoi2Id;
    @JoinColumn(name = "aoi1_id", referencedColumnName = "aoi_id")
    @ManyToOne
    private Aoi aoi1Id;

    public AoiRelation() {
    }

    public AoiRelation(Integer aoiRelationId) {
        this.aoiRelationId = aoiRelationId;
    }

    public Integer getAoiRelationId() {
        return aoiRelationId;
    }

    public void setAoiRelationId(Integer aoiRelationId) {
        this.aoiRelationId = aoiRelationId;
    }

    public AoiRelationType getAoi2RelationTypeId() {
        return aoi2RelationTypeId;
    }

    public void setAoi2RelationTypeId(AoiRelationType aoi2RelationTypeId) {
        this.aoi2RelationTypeId = aoi2RelationTypeId;
    }

    public AoiRelationType getAoi1RelationTypeId() {
        return aoi1RelationTypeId;
    }

    public void setAoi1RelationTypeId(AoiRelationType aoi1RelationTypeId) {
        this.aoi1RelationTypeId = aoi1RelationTypeId;
    }

    public Aoi getAoi2Id() {
        return aoi2Id;
    }

    public void setAoi2Id(Aoi aoi2Id) {
        this.aoi2Id = aoi2Id;
    }

    public Aoi getAoi1Id() {
        return aoi1Id;
    }

    public void setAoi1Id(Aoi aoi1Id) {
        this.aoi1Id = aoi1Id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aoiRelationId != null ? aoiRelationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AoiRelation)) {
            return false;
        }
        AoiRelation other = (AoiRelation) object;
        if ((this.aoiRelationId == null && other.aoiRelationId != null) || (this.aoiRelationId != null && !this.aoiRelationId.equals(other.aoiRelationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AoiRelation[ aoiRelationId=" + aoiRelationId + " ]";
    }
    
}
