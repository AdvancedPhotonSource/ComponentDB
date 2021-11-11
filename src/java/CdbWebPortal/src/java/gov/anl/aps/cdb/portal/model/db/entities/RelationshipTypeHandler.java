/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@Cacheable(true)
@Table(name = "relationship_type_handler")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RelationshipTypeHandler.findAll", query = "SELECT r FROM RelationshipTypeHandler r"),
    @NamedQuery(name = "RelationshipTypeHandler.findById", query = "SELECT r FROM RelationshipTypeHandler r WHERE r.id = :id"),
    @NamedQuery(name = "RelationshipTypeHandler.findByName", query = "SELECT r FROM RelationshipTypeHandler r WHERE r.name = :name"),
    @NamedQuery(name = "RelationshipTypeHandler.findByDescription", query = "SELECT r FROM RelationshipTypeHandler r WHERE r.description = :description")})
public class RelationshipTypeHandler implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    private String name;
    @Size(max = 256)
    private String description;
    @OneToMany(mappedBy = "relationshipTypeHandler")
    private List<RelationshipType> relationshipTypeList;

    public RelationshipTypeHandler() {
    }

    public RelationshipTypeHandler(Integer id) {
        this.id = id;
    }

    public RelationshipTypeHandler(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<RelationshipType> getRelationshipTypeList() {
        return relationshipTypeList;
    }

    public void setRelationshipTypeList(List<RelationshipType> relationshipTypeList) {
        this.relationshipTypeList = relationshipTypeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RelationshipTypeHandler)) {
            return false;
        }
        RelationshipTypeHandler other = (RelationshipTypeHandler) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.RelationshipTypeHandler[ id=" + id + " ]";
    }
    
}
