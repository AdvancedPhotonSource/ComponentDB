/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
 *
 * @author sveseli
 */
@Entity
@Table(name = "resource_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ResourceType.findAll", query = "SELECT r FROM ResourceType r"),
    @NamedQuery(name = "ResourceType.findById", query = "SELECT r FROM ResourceType r WHERE r.id = :id"),
    @NamedQuery(name = "ResourceType.findByName", query = "SELECT r FROM ResourceType r WHERE r.name = :name"),
    @NamedQuery(name = "ResourceType.findByDescription", query = "SELECT r FROM ResourceType r WHERE r.description = :description")})
public class ResourceType implements Serializable {
    private static final long serialVersionUID = 1L;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resourceTypeId")
    private List<ComponentConnectorResource> componentConnectorResourceList;
    @JoinColumn(name = "resource_category_id", referencedColumnName = "id")
    @ManyToOne
    private ResourceCategory resourceCategoryId;

    public ResourceType() {
    }

    public ResourceType(Integer id) {
        this.id = id;
    }

    public ResourceType(Integer id, String name) {
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
    public List<ComponentConnectorResource> getComponentConnectorResourceList() {
        return componentConnectorResourceList;
    }

    public void setComponentConnectorResourceList(List<ComponentConnectorResource> componentConnectorResourceList) {
        this.componentConnectorResourceList = componentConnectorResourceList;
    }

    public ResourceCategory getResourceCategoryId() {
        return resourceCategoryId;
    }

    public void setResourceCategoryId(ResourceCategory resourceCategoryId) {
        this.resourceCategoryId = resourceCategoryId;
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
        if (!(object instanceof ResourceType)) {
            return false;
        }
        ResourceType other = (ResourceType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.portal.model.entities.ResourceType[ id=" + id + " ]";
    }
    
}
