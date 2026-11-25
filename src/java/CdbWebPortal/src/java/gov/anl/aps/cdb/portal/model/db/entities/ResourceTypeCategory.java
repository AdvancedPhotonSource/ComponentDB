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
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "resource_type_category")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ResourceTypeCategory.findAll", query = "SELECT r FROM ResourceTypeCategory r"),
    @NamedQuery(name = "ResourceTypeCategory.findById", query = "SELECT r FROM ResourceTypeCategory r WHERE r.id = :id"),
    @NamedQuery(name = "ResourceTypeCategory.findByName", query = "SELECT r FROM ResourceTypeCategory r WHERE r.name = :name"),
    @NamedQuery(name = "ResourceTypeCategory.findByDescription", query = "SELECT r FROM ResourceTypeCategory r WHERE r.description = :description")})
public class ResourceTypeCategory implements Serializable {

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
    @OneToMany(mappedBy = "resourceTypeCategory")
    private List<ResourceType> resourceTypeList;

    public ResourceTypeCategory() {
    }

    public ResourceTypeCategory(Integer id) {
        this.id = id;
    }

    public ResourceTypeCategory(Integer id, String name) {
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
    @JsonIgnore
    public List<ResourceType> getResourceTypeList() {
        return resourceTypeList;
    }

    public void setResourceTypeList(List<ResourceType> resourceTypeList) {
        this.resourceTypeList = resourceTypeList;
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
        if (!(object instanceof ResourceTypeCategory)) {
            return false;
        }
        ResourceTypeCategory other = (ResourceTypeCategory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.ResourceTypeCategory[ id=" + id + " ]";
    }
    
}
