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
public class ComponentConnector implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 64)
    @Column(name = "label")
    private String label;
    @Column(name = "quantity")
    private Integer quantity;
    @JoinColumn(name = "connector_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ConnectorType connectorTypeId;
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component componentId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentConnectorId")
    private List<ComponentConnectorResource> componentConnectorResourceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "secondComponentConnectorId")
    private List<DesignComponentConnection> designComponentConnectionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firstComponentConnectorId")
    private List<DesignComponentConnection> designComponentConnectionList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "secondComponentConnectorId")
    private List<AssemblyComponentConnection> assemblyComponentConnectionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firstComponentConnectorId")
    private List<AssemblyComponentConnection> assemblyComponentConnectionList1;

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

    public ConnectorType getConnectorTypeId() {
        return connectorTypeId;
    }

    public void setConnectorTypeId(ConnectorType connectorTypeId) {
        this.connectorTypeId = connectorTypeId;
    }

    public Component getComponentId() {
        return componentId;
    }

    public void setComponentId(Component componentId) {
        this.componentId = componentId;
    }

    @XmlTransient
    public List<ComponentConnectorResource> getComponentConnectorResourceList() {
        return componentConnectorResourceList;
    }

    public void setComponentConnectorResourceList(List<ComponentConnectorResource> componentConnectorResourceList) {
        this.componentConnectorResourceList = componentConnectorResourceList;
    }

    @XmlTransient
    public List<DesignComponentConnection> getDesignComponentConnectionList() {
        return designComponentConnectionList;
    }

    public void setDesignComponentConnectionList(List<DesignComponentConnection> designComponentConnectionList) {
        this.designComponentConnectionList = designComponentConnectionList;
    }

    @XmlTransient
    public List<DesignComponentConnection> getDesignComponentConnectionList1() {
        return designComponentConnectionList1;
    }

    public void setDesignComponentConnectionList1(List<DesignComponentConnection> designComponentConnectionList1) {
        this.designComponentConnectionList1 = designComponentConnectionList1;
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
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.test.entities.ComponentConnector[ id=" + id + " ]";
    }
    
}
