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
@Table(name = "connector_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ConnectorType.findAll", query = "SELECT c FROM ConnectorType c"),
    @NamedQuery(name = "ConnectorType.findById", query = "SELECT c FROM ConnectorType c WHERE c.id = :id"),
    @NamedQuery(name = "ConnectorType.findByName", query = "SELECT c FROM ConnectorType c WHERE c.name = :name"),
    @NamedQuery(name = "ConnectorType.findByDescription", query = "SELECT c FROM ConnectorType c WHERE c.description = :description")})
public class ConnectorType implements Serializable {
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "connectorTypeId")
    private List<ComponentConnector> componentConnectorList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "connectorTypeId")
    private List<ConnectorTypeProperty> connectorTypePropertyList;
    @JoinColumn(name = "connector_category_id", referencedColumnName = "id")
    @ManyToOne
    private ConnectorCategory connectorCategoryId;

    public ConnectorType() {
    }

    public ConnectorType(Integer id) {
        this.id = id;
    }

    public ConnectorType(Integer id, String name) {
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
    public List<ComponentConnector> getComponentConnectorList() {
        return componentConnectorList;
    }

    public void setComponentConnectorList(List<ComponentConnector> componentConnectorList) {
        this.componentConnectorList = componentConnectorList;
    }

    @XmlTransient
    public List<ConnectorTypeProperty> getConnectorTypePropertyList() {
        return connectorTypePropertyList;
    }

    public void setConnectorTypePropertyList(List<ConnectorTypeProperty> connectorTypePropertyList) {
        this.connectorTypePropertyList = connectorTypePropertyList;
    }

    public ConnectorCategory getConnectorCategoryId() {
        return connectorCategoryId;
    }

    public void setConnectorCategoryId(ConnectorCategory connectorCategoryId) {
        this.connectorCategoryId = connectorCategoryId;
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
        if (!(object instanceof ConnectorType)) {
            return false;
        }
        ConnectorType other = (ConnectorType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.portal.model.entities.ConnectorType[ id=" + id + " ]";
    }
    
}
