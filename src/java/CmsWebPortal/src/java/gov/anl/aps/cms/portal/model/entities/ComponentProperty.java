/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import gov.anl.aps.cms.portal.utilities.ObjectUtility;
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
@Table(name = "component_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentProperty.findAll", query = "SELECT c FROM ComponentProperty c"),
    @NamedQuery(name = "ComponentProperty.findById", query = "SELECT c FROM ComponentProperty c WHERE c.id = :id"),
    @NamedQuery(name = "ComponentProperty.findByValue", query = "SELECT c FROM ComponentProperty c WHERE c.value = :value"),
    @NamedQuery(name = "ComponentProperty.findAllByComponentId", query = "SELECT c FROM ComponentProperty c WHERE c.component.id = :componentId")})

public class ComponentProperty extends CloneableEntity
{
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
    private PropertyType propertyType;
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component component;

    public ComponentProperty() {
    }

    public ComponentProperty(Integer id) {
        this.id = id;
    }

    public ComponentProperty(Integer id, String value) {
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByComponentAndPropertyTypeAndValue(ComponentProperty other) {
        if (other == null) {
            return false;
        }
        
        if (!ObjectUtility.equals(this.component, other.component)) {
            return false;
        }

        if (!ObjectUtility.equals(this.propertyType, other.propertyType)) {
            return false;
        }
        
        return ObjectUtility.equals(this.value, other.value);
    }
    
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ComponentProperty)) {
            return false;
        }
        ComponentProperty other = (ComponentProperty) object;
        if (this.id == null && other.id == null) {
            return equalsByComponentAndPropertyTypeAndValue(other);
        }
        
        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);        
    }    
   

    @Override
    public String toString() {
        return "gov.anl.aps.cms.test.entities.ComponentProperty[ id=" + id + " ]";
    }
    
}
