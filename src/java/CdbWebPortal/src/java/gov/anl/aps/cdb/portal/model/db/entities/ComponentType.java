/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Component type entity class.
 */
@Entity
@Table(name = "component_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentType.findAll", query = "SELECT c FROM ComponentType c ORDER BY c.name"),
    @NamedQuery(name = "ComponentType.findById", query = "SELECT c FROM ComponentType c WHERE c.id = :id"),
    @NamedQuery(name = "ComponentType.findByName", query = "SELECT c FROM ComponentType c WHERE c.name = :name"),
    @NamedQuery(name = "ComponentType.findByDescription", query = "SELECT c FROM ComponentType c WHERE c.description = :description")})
public class ComponentType extends CdbEntity {

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
    @JoinTable(name = "component_type_resource_type", joinColumns = {
        @JoinColumn(name = "component_type_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "resource_type_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ResourceType> resourceTypeList;
    @JoinTable(name = "component_type_property_type", joinColumns = {
        @JoinColumn(name = "component_type_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "property_type_id", referencedColumnName = "id")})
    @ManyToMany
    private List<PropertyType> propertyTypeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentType")
    private List<Component> componentList;
    @JoinColumn(name = "component_type_category_id", referencedColumnName = "id")
    @ManyToOne
    private ComponentTypeCategory componentTypeCategory;

    public ComponentType() {
    }

    public ComponentType(Integer id) {
        this.id = id;
    }

    public ComponentType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
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
    public List<ResourceType> getResourceTypeList() {
        return resourceTypeList;
    }

    public void setResourceTypeList(List<ResourceType> resourceTypeList) {
        this.resourceTypeList = resourceTypeList;
    }

    @XmlTransient
    public List<PropertyType> getPropertyTypeList() {
        return propertyTypeList;
    }

    public void setPropertyTypeList(List<PropertyType> propertyTypeList) {
        this.propertyTypeList = propertyTypeList;
    }

    @XmlTransient
    public List<Component> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<Component> componentList) {
        this.componentList = componentList;
    }

    public ComponentTypeCategory getComponentTypeCategory() {
        return componentTypeCategory;
    }

    public void setComponentTypeCategory(ComponentTypeCategory componentTypeCategory) {
        this.componentTypeCategory = componentTypeCategory;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByName(ComponentType other) {
        if (other != null) {
            return ObjectUtility.equals(this.name, other.name);
        }
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ComponentType)) {
            return false;
        }
        ComponentType other = (ComponentType) object;
        if (this.id == null && other.id == null) {
            return equalsByName(other);
        }

        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getNameWithCategory() {
        String result = name;
        if (componentTypeCategory != null) {
            result += ":" + componentTypeCategory.getName();
        }
        return result;
    }

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(id, name);
        searchResult.doesValueContainPattern("name", name, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        return searchResult;
    }

}
