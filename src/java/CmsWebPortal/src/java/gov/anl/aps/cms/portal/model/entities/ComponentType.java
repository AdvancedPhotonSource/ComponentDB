/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import gov.anl.aps.cms.portal.utilities.ObjectUtility;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "component_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentType.findAll", query = "SELECT c FROM ComponentType c"),
    @NamedQuery(name = "ComponentType.findById", query = "SELECT c FROM ComponentType c WHERE c.id = :id"),
    @NamedQuery(name = "ComponentType.findByName", query = "SELECT c FROM ComponentType c WHERE c.name = :name"),
    @NamedQuery(name = "ComponentType.findByDescription", query = "SELECT c FROM ComponentType c WHERE c.description = :description")})
public class ComponentType extends CloneableEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Size(max = 256)
    @Column(name = "description")
    private String description;
    @ManyToMany(mappedBy = "componentTypeList")
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
    
}
