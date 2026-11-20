/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;
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
@Table(name = "property_type_category")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PropertyTypeCategory.findAll", query = "SELECT p FROM PropertyTypeCategory p ORDER BY p.name ASC"),
    @NamedQuery(name = "PropertyTypeCategory.findById", query = "SELECT p FROM PropertyTypeCategory p WHERE p.id = :id"),
    @NamedQuery(name = "PropertyTypeCategory.findByIds", query = "SELECT p FROM PropertyTypeCategory p WHERE p.id in :ids"),
    @NamedQuery(name = "PropertyTypeCategory.findByName", query = "SELECT p FROM PropertyTypeCategory p WHERE p.name = :name"),
    @NamedQuery(name = "PropertyTypeCategory.findByDescription", query = "SELECT DISTINCT p FROM PropertyTypeCategory p WHERE p.description = :description"),
    @NamedQuery(name = "PropertyTypeCategory.findCategoryIdsForRelevantDomain", query = "Select DISTINCT p.id FROM PropertyTypeCategory p JOIN p.propertyTypeList AS pt JOIN pt.allowedDomainList AS ad WHERE ad.name = :domainName")})
public class PropertyTypeCategory extends CdbEntity implements Serializable {

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
    @OneToMany(mappedBy = "propertyTypeCategory")
    private List<PropertyType> propertyTypeList;

    public PropertyTypeCategory() {
    }

    public PropertyTypeCategory(Integer id) {
        this.id = id;
    }

    public PropertyTypeCategory(Integer id, String name) {
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
    public List<PropertyType> getPropertyTypeList() {
        return propertyTypeList;
    }

    public void setPropertyTypeList(List<PropertyType> propertyTypeList) {
        this.propertyTypeList = propertyTypeList;
    }
    
    @Override
    public SearchResult createSearchResultInfo(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(this, id, name);
        searchResult.doesValueContainPattern("name", name, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        return searchResult;
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
        if (!(object instanceof PropertyTypeCategory)) {
            return false;
        }
        PropertyTypeCategory other = (PropertyTypeCategory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
