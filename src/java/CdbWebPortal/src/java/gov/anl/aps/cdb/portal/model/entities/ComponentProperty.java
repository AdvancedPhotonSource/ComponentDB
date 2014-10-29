/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.entities;

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
@Table(name = "component_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentProperty.findAll", query = "SELECT c FROM ComponentProperty c"),
    @NamedQuery(name = "ComponentProperty.findByComponentId", query = "SELECT c FROM ComponentProperty c WHERE c.componentPropertyPK.componentId = :componentId"),
    @NamedQuery(name = "ComponentProperty.findByPropertyTypeId", query = "SELECT c FROM ComponentProperty c WHERE c.componentPropertyPK.propertyTypeId = :propertyTypeId"),
    @NamedQuery(name = "ComponentProperty.findByPropertyValueId", query = "SELECT c FROM ComponentProperty c WHERE c.componentPropertyPK.propertyValueId = :propertyValueId")})
public class ComponentProperty extends CloneableEntity
{
    @EmbeddedId
    protected ComponentPropertyPK componentPropertyPK;
    @JoinColumn(name = "property_value_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private PropertyValue propertyValue;
    @JoinColumn(name = "property_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private PropertyType propertyType;
    @JoinColumn(name = "component_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Component component;

    public ComponentProperty() {
    }

    public ComponentProperty(ComponentPropertyPK componentPropertyPK) {
        this.componentPropertyPK = componentPropertyPK;
    }

    public ComponentProperty(int componentId, int propertyTypeId, int propertyValueId) {
        this.componentPropertyPK = new ComponentPropertyPK(componentId, propertyTypeId, propertyValueId);
    }

    public ComponentPropertyPK getComponentPropertyPK() {
        return componentPropertyPK;
    }

    public void setComponentPropertyPK(ComponentPropertyPK componentPropertyPK) {
        this.componentPropertyPK = componentPropertyPK;
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

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentPropertyPK != null ? componentPropertyPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentProperty)) {
            return false;
        }
        ComponentProperty other = (ComponentProperty) object;
        if ((this.componentPropertyPK == null && other.componentPropertyPK != null) || (this.componentPropertyPK != null && !this.componentPropertyPK.equals(other.componentPropertyPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.entities.ComponentProperty[ componentPropertyPK=" + componentPropertyPK + " ]";
    }
    
}
