/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.controllers.utilities.ConnectorControllerUtility;
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

    @JsonIgnore
    @XmlTransient
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    @JsonIgnore
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

    @Override
    public ConnectorControllerUtility getControllerUtility() {
        return new ConnectorControllerUtility();
    }

    public void addPropertyValueToPropertyValueList(PropertyValue propertyValue) {
        if (propertyValueList == null) {
            propertyValueList = new ArrayList<>();
        }
        propertyValueList.add(0, propertyValue);
    }

    public List<Item> otherItemsUsingConnector(Item item) {
        List<Item> result = new ArrayList<>();
        List<ItemConnector> itemConnectorList = getItemConnectorList();
        if (itemConnectorList != null && (!itemConnectorList.isEmpty())) {
            for (ItemConnector itemConnector : itemConnectorList) {
                if (item == null
                        || (itemConnector.getItem() != null && !itemConnector.getItem().getId().equals(item.getId()))) {
                    result.add(itemConnector.getItem());
                }
            }
        }
        return result;
    }

}
