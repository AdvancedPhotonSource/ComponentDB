/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "connector_type_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ConnectorTypeProperty.findAll", query = "SELECT c FROM ConnectorTypeProperty c"),
    @NamedQuery(name = "ConnectorTypeProperty.findById", query = "SELECT c FROM ConnectorTypeProperty c WHERE c.id = :id"),
    @NamedQuery(name = "ConnectorTypeProperty.findByValue", query = "SELECT c FROM ConnectorTypeProperty c WHERE c.value = :value")})
public class ConnectorTypeProperty implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "value")
    private String value;
    @JoinColumn(name = "property_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private PropertyType propertyTypeId;
    @JoinColumn(name = "connector_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ConnectorType connectorTypeId;

    public ConnectorTypeProperty() {
    }

    public ConnectorTypeProperty(Integer id) {
        this.id = id;
    }

    public ConnectorTypeProperty(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public PropertyType getPropertyTypeId() {
        return propertyTypeId;
    }

    public void setPropertyTypeId(PropertyType propertyTypeId) {
        this.propertyTypeId = propertyTypeId;
    }

    public ConnectorType getConnectorTypeId() {
        return connectorTypeId;
    }

    public void setConnectorTypeId(ConnectorType connectorTypeId) {
        this.connectorTypeId = connectorTypeId;
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
        if (!(object instanceof ConnectorTypeProperty)) {
            return false;
        }
        ConnectorTypeProperty other = (ConnectorTypeProperty) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.test.entities.ConnectorTypeProperty[ id=" + id + " ]";
    }
    
}
