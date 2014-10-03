/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author sveseli
 */
@Embeddable
public class ComponentConnectorPropertyPK implements Serializable
{
    @Basic(optional = false)
    @NotNull
    @Column(name = "component_connector_id")
    private int componentConnectorId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "property_type_id")
    private int propertyTypeId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "property_value_id")
    private int propertyValueId;

    public ComponentConnectorPropertyPK() {
    }

    public ComponentConnectorPropertyPK(int componentConnectorId, int propertyTypeId, int propertyValueId) {
        this.componentConnectorId = componentConnectorId;
        this.propertyTypeId = propertyTypeId;
        this.propertyValueId = propertyValueId;
    }

    public int getComponentConnectorId() {
        return componentConnectorId;
    }

    public void setComponentConnectorId(int componentConnectorId) {
        this.componentConnectorId = componentConnectorId;
    }

    public int getPropertyTypeId() {
        return propertyTypeId;
    }

    public void setPropertyTypeId(int propertyTypeId) {
        this.propertyTypeId = propertyTypeId;
    }

    public int getPropertyValueId() {
        return propertyValueId;
    }

    public void setPropertyValueId(int propertyValueId) {
        this.propertyValueId = propertyValueId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) componentConnectorId;
        hash += (int) propertyTypeId;
        hash += (int) propertyValueId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentConnectorPropertyPK)) {
            return false;
        }
        ComponentConnectorPropertyPK other = (ComponentConnectorPropertyPK) object;
        if (this.componentConnectorId != other.componentConnectorId) {
            return false;
        }
        if (this.propertyTypeId != other.propertyTypeId) {
            return false;
        }
        if (this.propertyValueId != other.propertyValueId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.entities.ComponentConnectorPropertyPK[ componentConnectorId=" + componentConnectorId + ", propertyTypeId=" + propertyTypeId + ", propertyValueId=" + propertyValueId + " ]";
    }
    
}
