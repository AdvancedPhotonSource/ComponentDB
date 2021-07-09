/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.controllers.utilities.ConnectorControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import java.io.Serializable;
import java.util.ArrayList;
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
 * @author djarosz
 */
@Entity
@Table(name = "connector")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Connector.findAll", query = "SELECT c FROM Connector c"),
    @NamedQuery(name = "Connector.findById", query = "SELECT c FROM Connector c WHERE c.id = :id"),
    @NamedQuery(name = "Connector.findByName", query = "SELECT c FROM Connector c WHERE c.name = :name"),
    @NamedQuery(name = "Connector.findByDescription", query = "SELECT c FROM Connector c WHERE c.description = :description")})


public class Connector extends CdbEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = true)
    @Size(min = 0, max = 64)
    private String name;
    @Size(max = 256)
    private String description;
    @JoinTable(name = "connector_property", joinColumns = {
        @JoinColumn(name = "connector_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "property_value_id", referencedColumnName = "id")})
    @ManyToMany(cascade = CascadeType.ALL)
    private List<PropertyValue> propertyValueList;
    @OneToMany(mappedBy = "connector")
    private List<ItemConnector> itemConnectorList; // removed CascadeType.ALL since this circular relationship was causing duplicate ItemConnectors to be created (since create operation cascaded)
    @JoinColumn(name = "connector_type_id", referencedColumnName = "id")
    @ManyToOne
    private ConnectorType connectorType;
    @JoinColumn(name = "resource_type_id", referencedColumnName = "id")
    @ManyToOne
    private ResourceType resourceType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_male")
    private boolean isMale;

    public static final String CABLE_END_DESIGNATION_PROPERTY_TYPE = "cable_end_designation_property_type";
    public static final String CABLE_END_DESIGNATION_PROPERTY_DESCRIPTION = "cable end designation";
    private transient String cableEndDesignation = null;
    private transient PropertyValue cableEndDesignationPropertyValue = null;
    private static PropertyType cableEndDesignationPropertyType = null;
    public static final String DEFAULT_CABLE_END_DESIGNATION = "1";
    
    public Connector() {
    }

    public Connector(Integer id) {
        this.id = id;
    }

    public Connector(Integer id, String name) {
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

    public boolean getIsMale() {
        return isMale;
    }

    public void setIsMale(boolean isMale) {
        this.isMale = isMale;
    }

    @XmlTransient
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    @XmlTransient
    public List<ItemConnector> getItemConnectorList() {
        return itemConnectorList;
    }

    public void setItemConnectorList(List<ItemConnector> itemConnectorList) {
        this.itemConnectorList = itemConnectorList;
    }

    public ConnectorType getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(ConnectorType connectorType) {
        this.connectorType = connectorType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Connector)) {
            return false;
        }
        
        Connector other = (Connector) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        
        if (this.id == null && other.id == null) {
            if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public String toString() {
        String result = "";
        if (name != null) {
            result += name;
            if (connectorType != null) {
                result += " - " + connectorType.getName();
            }
        } else if (connectorType != null) {
            result += connectorType.getName();
        }

        return result;

    }
    
    public ConnectorControllerUtility getControllerUtility() {
        return new ConnectorControllerUtility(); 
    }
    
    public void addPropertyValueToPropertyValueList(PropertyValue propertyValue) {
        if (propertyValueList == null) {
            propertyValueList = new ArrayList<>();
        }
        propertyValueList.add(0, propertyValue);
    }
    
    public PropertyType getCableEndDesignationPropertyType() {
        
        if (cableEndDesignationPropertyType == null) {
            
            cableEndDesignationPropertyType =
                    PropertyTypeFacade.getInstance().findByName(CABLE_END_DESIGNATION_PROPERTY_TYPE);
            
            if (cableEndDesignationPropertyType == null) {
                cableEndDesignationPropertyType = getControllerUtility().prepareCableEndDesignationPropertyType();
            }
        }
        return cableEndDesignationPropertyType;
    }
    
    public PropertyValue prepareCableEndDesignationPropertyValue() {
        PropertyType propertyType = getCableEndDesignationPropertyType();
        return getControllerUtility().preparePropertyTypeValueAdd(
                this, propertyType, propertyType.getDefaultValue(), null);
    }
    
    public PropertyValue getCableEndDesignationPropertyValue() {

        if (cableEndDesignationPropertyValue == null) {
            List<PropertyValue> propertyValueList = getPropertyValueList();
            if (propertyValueList != null) {
                for (PropertyValue propertyValue : propertyValueList) {
                    if (propertyValue.getPropertyType().getName().equals(CABLE_END_DESIGNATION_PROPERTY_TYPE)) {
                        cableEndDesignationPropertyValue = propertyValue;
                    }
                }
            }
        }
        
        if (cableEndDesignationPropertyValue == null) {
            cableEndDesignationPropertyValue = prepareCableEndDesignationPropertyValue();
        }

        return cableEndDesignationPropertyValue;
    }

    public void setCableEndDesignation(String endDesignation) {
        PropertyValue propertyValue = getCableEndDesignationPropertyValue();
        if (propertyValue != null) {
            cableEndDesignation = endDesignation;
            propertyValue.setValue(endDesignation);
        }
    }
    
    @JsonIgnore
    public String getCableEndDesignation() {        
        if (cableEndDesignation == null) {
            cableEndDesignation = getCableEndDesignationPropertyValue().getValue();
        }
        return cableEndDesignation;
    }

}
