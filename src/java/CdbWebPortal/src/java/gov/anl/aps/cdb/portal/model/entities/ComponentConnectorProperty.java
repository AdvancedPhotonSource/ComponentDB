/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.entities;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "component_connector_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentConnectorProperty.findAll", query = "SELECT c FROM ComponentConnectorProperty c"),
    @NamedQuery(name = "ComponentConnectorProperty.findByComponentConnectorId", query = "SELECT c FROM ComponentConnectorProperty c WHERE c.componentConnectorPropertyPK.componentConnectorId = :componentConnectorId"),
    @NamedQuery(name = "ComponentConnectorProperty.findByPropertyTypeId", query = "SELECT c FROM ComponentConnectorProperty c WHERE c.componentConnectorPropertyPK.propertyTypeId = :propertyTypeId"),
    @NamedQuery(name = "ComponentConnectorProperty.findByPropertyValueId", query = "SELECT c FROM ComponentConnectorProperty c WHERE c.componentConnectorPropertyPK.propertyValueId = :propertyValueId")})
public class ComponentConnectorProperty implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ComponentConnectorPropertyPK componentConnectorPropertyPK;
    @JoinColumn(name = "property_value_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private PropertyValue propertyValue;
    @JoinColumn(name = "property_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private PropertyType propertyType;
    @JoinColumn(name = "component_connector_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ComponentConnector componentConnector;

    public ComponentConnectorProperty() {
    }

    public ComponentConnectorProperty(ComponentConnectorPropertyPK componentConnectorPropertyPK) {
        this.componentConnectorPropertyPK = componentConnectorPropertyPK;
    }

    public ComponentConnectorProperty(int componentConnectorId, int propertyTypeId, int propertyValueId) {
        this.componentConnectorPropertyPK = new ComponentConnectorPropertyPK(componentConnectorId, propertyTypeId, propertyValueId);
    }

    public ComponentConnectorPropertyPK getComponentConnectorPropertyPK() {
        return componentConnectorPropertyPK;
    }

    public void setComponentConnectorPropertyPK(ComponentConnectorPropertyPK componentConnectorPropertyPK) {
        this.componentConnectorPropertyPK = componentConnectorPropertyPK;
    }

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(PropertyValue propertyValue) {
        this.propertyValue = propertyValue;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public ComponentConnector getComponentConnector() {
        return componentConnector;
    }

    public void setComponentConnector(ComponentConnector componentConnector) {
        this.componentConnector = componentConnector;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentConnectorPropertyPK != null ? componentConnectorPropertyPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentConnectorProperty)) {
            return false;
        }
        ComponentConnectorProperty other = (ComponentConnectorProperty) object;
        if ((this.componentConnectorPropertyPK == null && other.componentConnectorPropertyPK != null) || (this.componentConnectorPropertyPK != null && !this.componentConnectorPropertyPK.equals(other.componentConnectorPropertyPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.entities.ComponentConnectorProperty[ componentConnectorPropertyPK=" + componentConnectorPropertyPK + " ]";
    }
    
}
