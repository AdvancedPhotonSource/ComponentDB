/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.utilities.ObjectUtility;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.regex.Pattern;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "property_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PropertyType.findAll", query = "SELECT p FROM PropertyType p ORDER BY p.name"),
    @NamedQuery(name = "PropertyType.findById", query = "SELECT p FROM PropertyType p WHERE p.id = :id"),
    @NamedQuery(name = "PropertyType.findByName", query = "SELECT p FROM PropertyType p WHERE p.name = :name"),
    @NamedQuery(name = "PropertyType.findByDescription", query = "SELECT p FROM PropertyType p WHERE p.description = :description"),
    @NamedQuery(name = "PropertyType.findByDefaultValue", query = "SELECT p FROM PropertyType p WHERE p.defaultValue = :defaultValue"),
    @NamedQuery(name = "PropertyType.findByDefaultUnits", query = "SELECT p FROM PropertyType p WHERE p.defaultUnits = :defaultUnits")})
public class PropertyType extends CdbEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(max = 64)
    private String name;
    @Size(max = 256)
    private String description;
    @Size(max = 64)
    @Column(name = "default_value")
    private String defaultValue;
    @Size(max = 16)
    @Column(name = "default_units")
    private String defaultUnits;
    @ManyToMany(mappedBy = "propertyTypeList")
    private List<ConnectorType> connectorTypeList;
    @ManyToMany(mappedBy = "propertyTypeList")
    private List<ComponentType> componentTypeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyType")
    private List<AllowedPropertyValue> allowedPropertyValueList;
    @JoinColumn(name = "property_type_handler_id", referencedColumnName = "id")
    @ManyToOne
    private PropertyTypeHandler propertyTypeHandler;
    @JoinColumn(name = "property_type_category_id", referencedColumnName = "id")
    @ManyToOne
    private PropertyTypeCategory propertyTypeCategory;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyType")
    private List<PropertyValue> propertyValueList;

    private transient DisplayType displayType = null;

    public PropertyType() {
    }

    public PropertyType(Integer id) {
        this.id = id;
    }

    public PropertyType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
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
    public List<AllowedPropertyValue> getAllowedPropertyValueList() {
        return allowedPropertyValueList;
    }

    public void setAllowedPropertyValueList(List<AllowedPropertyValue> allowedPropertyValueList) {
        this.allowedPropertyValueList = allowedPropertyValueList;
    }

    public PropertyTypeHandler getPropertyTypeHandler() {
        return propertyTypeHandler;
    }

    public void setPropertyTypeHandler(PropertyTypeHandler propertyTypeHandler) {
        this.propertyTypeHandler = propertyTypeHandler;
    }

    public PropertyTypeCategory getPropertyTypeCategory() {
        return propertyTypeCategory;
    }

    public void setPropertyTypeCategory(PropertyTypeCategory propertyTypeCategory) {
        this.propertyTypeCategory = propertyTypeCategory;
    }

    @XmlTransient
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(DisplayType displayType) {
        this.displayType = displayType;
    }

    public boolean hasAllowedPropertyValues() {
        return !allowedPropertyValueList.isEmpty();
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

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(id, name);
        searchResult.doesValueContainPattern("name", name, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        return searchResult;
    }
}
