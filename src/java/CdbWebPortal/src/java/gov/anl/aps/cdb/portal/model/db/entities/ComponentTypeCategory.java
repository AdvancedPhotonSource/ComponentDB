/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
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
 * @author sveseli
 */
@Entity
@Table(name = "component_type_category")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentTypeCategory.findAll", query = "SELECT c FROM ComponentTypeCategory c"),
    @NamedQuery(name = "ComponentTypeCategory.findById", query = "SELECT c FROM ComponentTypeCategory c WHERE c.id = :id"),
    @NamedQuery(name = "ComponentTypeCategory.findByName", query = "SELECT c FROM ComponentTypeCategory c WHERE c.name = :name"),
    @NamedQuery(name = "ComponentTypeCategory.findByDescription", query = "SELECT c FROM ComponentTypeCategory c WHERE c.description = :description")})
public class ComponentTypeCategory extends CdbEntity
{
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
    @OneToMany(mappedBy = "componentTypeCategory")
    private List<ComponentType> componentTypeList;

    public ComponentTypeCategory() {
    }

    public ComponentTypeCategory(Integer id) {
        this.id = id;
    }

    public ComponentTypeCategory(Integer id, String name) {
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
    public List<ComponentType> getComponentTypeList() {
        return componentTypeList;
    }

    public void setComponentTypeList(List<ComponentType> componentTypeList) {
        this.componentTypeList = componentTypeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByName(ComponentTypeCategory other) {
        if (other == null) {
            return false;
        }
        return ObjectUtility.equals(this.name, other.name);
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ComponentTypeCategory)) {
            return false;
        }
        ComponentTypeCategory other = (ComponentTypeCategory) object;
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

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(id, name);
        searchResult.doesValueContainPattern("name", name, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        return searchResult;
    }      
    
    
}
