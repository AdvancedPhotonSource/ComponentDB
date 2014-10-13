/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.entities;

import gov.anl.aps.cdb.portal.utilities.ObjectUtility;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
    @NamedQuery(name = "PropertyType.findByDescription", query = "SELECT p FROM PropertyType p WHERE p.description = :description"),
    @NamedQuery(name = "PropertyType.findByHandlerName", query = "SELECT p FROM PropertyType p WHERE p.handlerName = :handlerName"),
    @NamedQuery(name = "PropertyType.findByDefaultValue", query = "SELECT p FROM PropertyType p WHERE p.defaultValue = :defaultValue"),
    @NamedQuery(name = "PropertyType.findByDefaultUnits", query = "SELECT p FROM PropertyType p WHERE p.defaultUnits = :defaultUnits"),
    @NamedQuery(name = "PropertyType.findByIsUserWriteable", query = "SELECT p FROM PropertyType p WHERE p.isUserWriteable = :isUserWriteable"),
    @NamedQuery(name = "PropertyType.findByIsDynamic", query = "SELECT p FROM PropertyType p WHERE p.isDynamic = :isDynamic")})
public class PropertyType extends CloneableEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    private String name;
    @Size(max = 256)
    private String description;
    @Size(max = 256)
    @Column(name = "handler_name")
    private String handlerName;
    @Size(max = 64)
    @Column(name = "default_value")
    private String defaultValue;
    @Size(max = 16)
    @Column(name = "default_units")
    private String defaultUnits;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_user_writeable")
    private boolean isUserWriteable;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_dynamic")
    private boolean isDynamic;
    @JoinTable(name = "connector_type_property_type", joinColumns = {
        @JoinColumn(name = "property_type_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "connector_type_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ConnectorType> connectorTypeList;
    @JoinTable(name = "component_type_property_type", joinColumns = {
        @JoinColumn(name = "property_type_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "component_type_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ComponentType> componentTypeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyTypeId")
    private List<DesignProperty> designPropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyType")
    private List<ComponentInstanceProperty> componentInstancePropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyTypeId")
    private List<AllowedPropertyValue> allowedPropertyValueList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyType")
    private List<ComponentConnectorProperty> componentConnectorPropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyType")
    private List<ComponentProperty> componentPropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyTypeId")
    private List<DesignElementProperty> designElementPropertyList;
    @JoinColumn(name = "property_type_category_id", referencedColumnName = "id")
    @ManyToOne
    private PropertyTypeCategory propertyTypeCategory;

    public PropertyType() {
    }

    public PropertyType(Integer id) {
        this.id = id;
    }

    public PropertyType(Integer id, String name, boolean isUserWriteable, boolean isDynamic) {
        this.id = id;
        this.name = name;
        this.isUserWriteable = isUserWriteable;
        this.isDynamic = isDynamic;
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

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultUnits() {
        return defaultUnits;
    }

    public void setDefaultUnits(String defaultUnits) {
        this.defaultUnits = defaultUnits;
    }

    public boolean getIsUserWriteable() {
        return isUserWriteable;
    }

    public void setIsUserWriteable(boolean isUserWriteable) {
        this.isUserWriteable = isUserWriteable;
    }

    public boolean getIsDynamic() {
        return isDynamic;
    }

    public void setIsDynamic(boolean isDynamic) {
        this.isDynamic = isDynamic;
    }

    @XmlTransient
    public List<ConnectorType> getConnectorTypeList() {
        return connectorTypeList;
    }

    public void setConnectorTypeList(List<ConnectorType> connectorTypeList) {
        this.connectorTypeList = connectorTypeList;
    }

    @XmlTransient
    public List<ComponentType> getComponentTypeList() {
        return componentTypeList;
    }

    public void setComponentTypeList(List<ComponentType> componentTypeList) {
        this.componentTypeList = componentTypeList;
    }

    @XmlTransient
    public List<DesignProperty> getDesignPropertyList() {
        return designPropertyList;
    }

    public void setDesignPropertyList(List<DesignProperty> designPropertyList) {
        this.designPropertyList = designPropertyList;
    }

    @XmlTransient
    public List<ComponentInstanceProperty> getComponentInstancePropertyList() {
        return componentInstancePropertyList;
    }

    public void setComponentInstancePropertyList(List<ComponentInstanceProperty> componentInstancePropertyList) {
        this.componentInstancePropertyList = componentInstancePropertyList;
    }

    @XmlTransient
    public List<AllowedPropertyValue> getAllowedPropertyValueList() {
        return allowedPropertyValueList;
    }

    public void setAllowedPropertyValueList(List<AllowedPropertyValue> allowedPropertyValueList) {
        this.allowedPropertyValueList = allowedPropertyValueList;
    }

    @XmlTransient
    public List<ComponentConnectorProperty> getComponentConnectorPropertyList() {
        return componentConnectorPropertyList;
    }

    public void setComponentConnectorPropertyList(List<ComponentConnectorProperty> componentConnectorPropertyList) {
        this.componentConnectorPropertyList = componentConnectorPropertyList;
    }

    @XmlTransient
    public List<ComponentProperty> getComponentPropertyList() {
        return componentPropertyList;
    }

    public void setComponentPropertyList(List<ComponentProperty> componentPropertyList) {
        this.componentPropertyList = componentPropertyList;
    }

    @XmlTransient
    public List<DesignElementProperty> getDesignElementPropertyList() {
        return designElementPropertyList;
    }

    public void setDesignElementPropertyList(List<DesignElementProperty> designElementPropertyList) {
        this.designElementPropertyList = designElementPropertyList;
    }

    public PropertyTypeCategory getPropertyTypeCategory() {
        return propertyTypeCategory;
    }

    public void setPropertyTypeCategory(PropertyTypeCategory propertyTypeCategory) {
        this.propertyTypeCategory = propertyTypeCategory;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByName(PropertyType other) {
        if (other == null) {
            return false;
        }

        return ObjectUtility.equals(this.name, other.name);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PropertyType)) {
            return false;
        }
        PropertyType other = (PropertyType) object;
        if (this.id == null && other.id == null) {
            return equalsByName(other);
        }

        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return name;
    }

    
}
