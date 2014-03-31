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
@Table(name = "property_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PropertyType.findAll", query = "SELECT p FROM PropertyType p"),
    @NamedQuery(name = "PropertyType.findById", query = "SELECT p FROM PropertyType p WHERE p.id = :id"),
    @NamedQuery(name = "PropertyType.findByName", query = "SELECT p FROM PropertyType p WHERE p.name = :name"),
    @NamedQuery(name = "PropertyType.findByDescription", query = "SELECT p FROM PropertyType p WHERE p.description = :description")})
public class PropertyType implements Serializable
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
    @Column(name = "name")
    private String name;
    @Size(max = 256)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyTypeId")
    private List<ConnectorTypeProperty> connectorTypePropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyTypeId")
    private List<ComponentProperty> componentPropertyList;
    @JoinColumn(name = "property_category_id", referencedColumnName = "id")
    @ManyToOne
    private PropertyCategory propertyCategory;

    public PropertyType() {
    }

    public PropertyType(Integer id) {
        this.id = id;
    }

    public PropertyType(Integer id, String name) {
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
    public List<ConnectorTypeProperty> getConnectorTypePropertyList() {
        return connectorTypePropertyList;
    }

    public void setConnectorTypePropertyList(List<ConnectorTypeProperty> connectorTypePropertyList) {
        this.connectorTypePropertyList = connectorTypePropertyList;
    }

    @XmlTransient
    public List<ComponentProperty> getComponentPropertyList() {
        return componentPropertyList;
    }

    public void setComponentPropertyList(List<ComponentProperty> componentPropertyList) {
        this.componentPropertyList = componentPropertyList;
    }

    public PropertyCategory getPropertyCategory() {
        return propertyCategory;
    }

    public void setPropertyCategory(PropertyCategory propertyCategory) {
        this.propertyCategory = propertyCategory;
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
        if (!(object instanceof PropertyType)) {
            return false;
        }
        PropertyType other = (PropertyType) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        String result = name;
        if (propertyCategory != null) {
            result += "/" + propertyCategory.getName();
        }
        return result;
    }
    
}
