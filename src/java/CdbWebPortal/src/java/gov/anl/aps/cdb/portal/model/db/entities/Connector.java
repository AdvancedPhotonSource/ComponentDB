/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.io.Serializable;
import java.util.List;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Connector.findAll", query = "SELECT c FROM Connector c"),
    @NamedQuery(name = "Connector.findById", query = "SELECT c FROM Connector c WHERE c.id = :id"),
    @NamedQuery(name = "Connector.findByName", query = "SELECT c FROM Connector c WHERE c.name = :name"),
    @NamedQuery(name = "Connector.findByDescription", query = "SELECT c FROM Connector c WHERE c.description = :description")})
public class Connector implements Serializable {

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
    @JoinTable(name = "connector_property", joinColumns = {
        @JoinColumn(name = "connector_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "property_value_id", referencedColumnName = "id")})
    @ManyToMany
    private List<PropertyValue> propertyValueList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "connector")
    private List<ItemConnector> itemConnectorList;
    @JoinColumn(name = "connector_type_id", referencedColumnName = "id")
    @ManyToOne
    private ConnectorType connectorType;
    @JoinColumn(name = "resource_type_id", referencedColumnName = "id")
    @ManyToOne
    private ResourceType resourceType;

    public Connector() {
    }

    public Connector(Integer id) {
        this.id = id;
    }

    public Connector(Integer id, String name) {
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
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    @XmlTransient
    public List<ItemConnector> getItemConnectorList() {
        return itemConnectorList;
    }

    public void setItemConnectorList(List<ItemConnector> itemConnectorList) {
        this.itemConnectorList = itemConnectorList;
    }

    public ConnectorType getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(ConnectorType connectorType) {
        this.connectorType = connectorType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
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
        if (!(object instanceof Connector)) {
            return false;
        }
        Connector other = (Connector) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.Connector[ id=" + id + " ]";
    }
    
}
