/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.db.entities;

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
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "component_connector")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentConnector.findAll", query = "SELECT c FROM ComponentConnector c"),
    @NamedQuery(name = "ComponentConnector.findById", query = "SELECT c FROM ComponentConnector c WHERE c.id = :id"),
    @NamedQuery(name = "ComponentConnector.findByLabel", query = "SELECT c FROM ComponentConnector c WHERE c.label = :label"),
    @NamedQuery(name = "ComponentConnector.findByQuantity", query = "SELECT c FROM ComponentConnector c WHERE c.quantity = :quantity")})
public class ComponentConnector extends CloneableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 64)
    private String label;
    private Integer quantity;
    @JoinTable(name = "component_connector_property", joinColumns = {
        @JoinColumn(name = "component_connector_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "property_value_id", referencedColumnName = "id")})
    @ManyToMany
    private List<PropertyValue> propertyValueList;
    @OneToMany(mappedBy = "secondComponentConnectorId")
    private List<DesignElementConnection> designElementConnectionList;
    @OneToMany(mappedBy = "firstComponentConnectorId")
    private List<DesignElementConnection> designElementConnectionList1;
    @JoinColumn(name = "connector_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ConnectorType connectorType;
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component component;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "secondComponentConnectorId")
    private List<AssemblyComponentConnection> assemblyComponentConnectionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firstComponentConnectorId")
    private List<AssemblyComponentConnection> assemblyComponentConnectionList1;
    @OneToMany(mappedBy = "componentConnectorId")
    private List<ComponentResource> componentResourceList;

    public ComponentConnector() {
    }

    public ComponentConnector(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @XmlTransient
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    @XmlTransient
    public List<DesignElementConnection> getDesignElementConnectionList() {
        return designElementConnectionList;
    }

    public void setDesignElementConnectionList(List<DesignElementConnection> designElementConnectionList) {
        this.designElementConnectionList = designElementConnectionList;
    }

    @XmlTransient
    public List<DesignElementConnection> getDesignElementConnectionList1() {
        return designElementConnectionList1;
    }

    public void setDesignElementConnectionList1(List<DesignElementConnection> designElementConnectionList1) {
        this.designElementConnectionList1 = designElementConnectionList1;
    }

    public ConnectorType getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(ConnectorType connectorType) {
        this.connectorType = connectorType;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    @XmlTransient
    public List<AssemblyComponentConnection> getAssemblyComponentConnectionList() {
        return assemblyComponentConnectionList;
    }

    public void setAssemblyComponentConnectionList(List<AssemblyComponentConnection> assemblyComponentConnectionList) {
        this.assemblyComponentConnectionList = assemblyComponentConnectionList;
    }

    @XmlTransient
    public List<AssemblyComponentConnection> getAssemblyComponentConnectionList1() {
        return assemblyComponentConnectionList1;
    }

    public void setAssemblyComponentConnectionList1(List<AssemblyComponentConnection> assemblyComponentConnectionList1) {
        this.assemblyComponentConnectionList1 = assemblyComponentConnectionList1;
    }

    @XmlTransient
    public List<ComponentResource> getComponentResourceList() {
        return componentResourceList;
    }

    public void setComponentResourceList(List<ComponentResource> componentResourceList) {
        this.componentResourceList = componentResourceList;
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
        if (!(object instanceof ComponentConnector)) {
            return false;
        }
        ComponentConnector other = (ComponentConnector) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.entities.ComponentConnector[ id=" + id + " ]";
    }
    
}
