/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "design_element_property")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DesignElementProperty.findAll", query = "SELECT d FROM DesignElementProperty d"),
    @NamedQuery(name = "DesignElementProperty.findById", query = "SELECT d FROM DesignElementProperty d WHERE d.id = :id")})
public class DesignElementProperty implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @JoinColumn(name = "property_value_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private PropertyValue propertyValueId;
    @JoinColumn(name = "property_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private PropertyType propertyTypeId;
    @JoinColumn(name = "design_element_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private DesignElement designElementId;

    public DesignElementProperty() {
    }

    public DesignElementProperty(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PropertyValue getPropertyValueId() {
        return propertyValueId;
    }

    public void setPropertyValueId(PropertyValue propertyValueId) {
        this.propertyValueId = propertyValueId;
    }

    public PropertyType getPropertyTypeId() {
        return propertyTypeId;
    }

    public void setPropertyTypeId(PropertyType propertyTypeId) {
        this.propertyTypeId = propertyTypeId;
    }

    public DesignElement getDesignElementId() {
        return designElementId;
    }

    public void setDesignElementId(DesignElement designElementId) {
        this.designElementId = designElementId;
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
        if (!(object instanceof DesignElementProperty)) {
            return false;
        }
        DesignElementProperty other = (DesignElementProperty) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.entities.DesignElementProperty[ id=" + id + " ]";
    }
    
}
