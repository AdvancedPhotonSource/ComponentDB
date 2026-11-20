/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@Cacheable(true)
@Table(name = "relationship_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RelationshipType.findAll", query = "SELECT r FROM RelationshipType r"),
    @NamedQuery(name = "RelationshipType.findById", query = "SELECT r FROM RelationshipType r WHERE r.id = :id"),
    @NamedQuery(name = "RelationshipType.findByName", query = "SELECT r FROM RelationshipType r WHERE r.name = :name"),
    @NamedQuery(name = "RelationshipType.findByDescription", query = "SELECT r FROM RelationshipType r WHERE r.description = :description")})
public class RelationshipType extends CdbEntity implements Serializable {

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
    @JoinColumn(name = "relationship_type_handler_id", referencedColumnName = "id")
    @ManyToOne
    private RelationshipTypeHandler relationshipTypeHandler;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "relationshipType")
    private List<ItemElementRelationship> itemElementRelationshipList;

    public RelationshipType() {
    }

    public RelationshipType(Integer id) {
        this.id = id;
    }

    public RelationshipType(Integer id, String name) {
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

    public RelationshipTypeHandler getRelationshipTypeHandler() {
        return relationshipTypeHandler;
    }

    public void setRelationshipTypeHandler(RelationshipTypeHandler relationshipTypeHandler) {
        this.relationshipTypeHandler = relationshipTypeHandler;
    }

    @XmlTransient
    @JsonIgnore
    public List<ItemElementRelationship> getItemElementRelationshipList() {
        return itemElementRelationshipList;
    }

    public void setItemElementRelationshipList(List<ItemElementRelationship> itemElementRelationshipList) {
        this.itemElementRelationshipList = itemElementRelationshipList;
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
        if (!(object instanceof RelationshipType)) {
            return false;
        }
        RelationshipType other = (RelationshipType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.RelationshipType[ id=" + id + " ]";
    }
    
}
