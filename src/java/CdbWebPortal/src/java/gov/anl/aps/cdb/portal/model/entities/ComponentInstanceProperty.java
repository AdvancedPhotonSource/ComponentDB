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
@Table(name = "component_instance_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentInstanceProperty.findAll", query = "SELECT c FROM ComponentInstanceProperty c"),
    @NamedQuery(name = "ComponentInstanceProperty.findByComponentInstanceId", query = "SELECT c FROM ComponentInstanceProperty c WHERE c.componentInstancePropertyPK.componentInstanceId = :componentInstanceId"),
    @NamedQuery(name = "ComponentInstanceProperty.findByPropertyTypeId", query = "SELECT c FROM ComponentInstanceProperty c WHERE c.componentInstancePropertyPK.propertyTypeId = :propertyTypeId"),
    @NamedQuery(name = "ComponentInstanceProperty.findByPropertyValueId", query = "SELECT c FROM ComponentInstanceProperty c WHERE c.componentInstancePropertyPK.propertyValueId = :propertyValueId")})
public class ComponentInstanceProperty implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ComponentInstancePropertyPK componentInstancePropertyPK;
    @JoinColumn(name = "property_value_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private PropertyValue propertyValue;
    @JoinColumn(name = "property_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private PropertyType propertyType;
    @JoinColumn(name = "component_instance_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ComponentInstance componentInstance;

    public ComponentInstanceProperty() {
    }

    public ComponentInstanceProperty(ComponentInstancePropertyPK componentInstancePropertyPK) {
        this.componentInstancePropertyPK = componentInstancePropertyPK;
    }

    public ComponentInstanceProperty(int componentInstanceId, int propertyTypeId, int propertyValueId) {
        this.componentInstancePropertyPK = new ComponentInstancePropertyPK(componentInstanceId, propertyTypeId, propertyValueId);
    }

    public ComponentInstancePropertyPK getComponentInstancePropertyPK() {
        return componentInstancePropertyPK;
    }

    public void setComponentInstancePropertyPK(ComponentInstancePropertyPK componentInstancePropertyPK) {
        this.componentInstancePropertyPK = componentInstancePropertyPK;
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

    public ComponentInstance getComponentInstance() {
        return componentInstance;
    }

    public void setComponentInstance(ComponentInstance componentInstance) {
        this.componentInstance = componentInstance;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentInstancePropertyPK != null ? componentInstancePropertyPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentInstanceProperty)) {
            return false;
        }
        ComponentInstanceProperty other = (ComponentInstanceProperty) object;
        if ((this.componentInstancePropertyPK == null && other.componentInstancePropertyPK != null) || (this.componentInstancePropertyPK != null && !this.componentInstancePropertyPK.equals(other.componentInstancePropertyPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.entities.ComponentInstanceProperty[ componentInstancePropertyPK=" + componentInstancePropertyPK + " ]";
    }
    
}
