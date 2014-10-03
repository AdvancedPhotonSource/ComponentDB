/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.entities;

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
    @NamedQuery(name = "ResourceType.findByDescription", query = "SELECT r FROM ResourceType r WHERE r.description = :description"),
    @NamedQuery(name = "ResourceType.findByHandlerName", query = "SELECT r FROM ResourceType r WHERE r.handlerName = :handlerName"),
    @NamedQuery(name = "ResourceType.findByDefaultValue", query = "SELECT r FROM ResourceType r WHERE r.defaultValue = :defaultValue"),
    @NamedQuery(name = "ResourceType.findByDefaultUnits", query = "SELECT r FROM ResourceType r WHERE r.defaultUnits = :defaultUnits")})
public class ResourceType implements Serializable
{
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
    @Size(max = 64)
    @Column(name = "handler_name")
    private String handlerName;
    @Size(max = 64)
    @Column(name = "default_value")
    private String defaultValue;
    @Size(max = 16)
    @Column(name = "default_units")
    private String defaultUnits;
    @JoinTable(name = "component_type_resource_type", joinColumns = {
        @JoinColumn(name = "resource_type_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "component_type_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ComponentType> componentTypeList;
    @OneToMany(mappedBy = "resourceTypeId")
    private List<DesignElementConnection> designElementConnectionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "resourceTypeId")
    private List<ComponentResource> componentResourceList;
    @JoinColumn(name = "resource_type_category_id", referencedColumnName = "id")
    @ManyToOne
    private ResourceTypeCategory resourceTypeCategoryId;
    @OneToMany(mappedBy = "resourceTypeId")
    private List<ConnectorType> connectorTypeList;

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

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultUnits() {
        return defaultUnits;
    }

    public void setDefaultUnits(String defaultUnits) {
        this.defaultUnits = defaultUnits;
    }

    @XmlTransient
    public List<ComponentType> getComponentTypeList() {
        return componentTypeList;
    }

    public void setComponentTypeList(List<ComponentType> componentTypeList) {
        this.componentTypeList = componentTypeList;
    }

    @XmlTransient
    public List<DesignElementConnection> getDesignElementConnectionList() {
        return designElementConnectionList;
    }

    public void setDesignElementConnectionList(List<DesignElementConnection> designElementConnectionList) {
        this.designElementConnectionList = designElementConnectionList;
    }

    @XmlTransient
    public List<ComponentResource> getComponentResourceList() {
        return componentResourceList;
    }

    public void setComponentResourceList(List<ComponentResource> componentResourceList) {
        this.componentResourceList = componentResourceList;
    }

    public ResourceTypeCategory getResourceTypeCategoryId() {
        return resourceTypeCategoryId;
    }

    public void setResourceTypeCategoryId(ResourceTypeCategory resourceTypeCategoryId) {
        this.resourceTypeCategoryId = resourceTypeCategoryId;
    }

    @XmlTransient
    public List<ConnectorType> getConnectorTypeList() {
        return connectorTypeList;
    }

    public void setConnectorTypeList(List<ConnectorType> connectorTypeList) {
        this.connectorTypeList = connectorTypeList;
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
        return "gov.anl.aps.cdb.portal.model.entities.ResourceType[ id=" + id + " ]";
    }
    
}
