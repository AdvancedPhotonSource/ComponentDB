/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "item_connector")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemConnector.findAll", query = "SELECT i FROM ItemConnector i"),
    @NamedQuery(name = "ItemConnector.findById", query = "SELECT i FROM ItemConnector i WHERE i.id = :id"),
    @NamedQuery(name = "ItemConnector.findByLabel", query = "SELECT i FROM ItemConnector i WHERE i.label = :label"),
    @NamedQuery(name = "ItemConnector.findByQuantity", query = "SELECT i FROM ItemConnector i WHERE i.quantity = :quantity")})
public class ItemConnector extends CdbEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 64)
    private String label;
    private Integer quantity;
    @JoinTable(name = "item_connector_property", joinColumns = {
        @JoinColumn(name = "item_connector_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "property_value_id", referencedColumnName = "id")})
    @ManyToMany
    private List<PropertyValue> propertyValueList;
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Item item;
    @JoinColumn(name = "connector_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Connector connector;
    @OneToMany(mappedBy = "firstItemConnector")
    private List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList;
    @OneToMany(mappedBy = "secondItemConnector")
    private List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList1;
    @OneToMany(mappedBy = "itemConnector")
    private List<ItemResource> itemResourceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firstItemConnector")
    private List<ItemElementRelationship> itemElementRelationshipList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "secondItemConnector")
    private List<ItemElementRelationship> itemElementRelationshipList1;
    
    private transient ItemConnector itemConnectorOfItemConnectedTo; 
    private transient Item itemConnectedVia; 
        
    public ItemConnector() {
    }

    public ItemConnector(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @XmlTransient
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Connector getConnector() {
        return connector;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    @XmlTransient
    public List<ItemElementRelationshipHistory> getItemElementRelationshipHistoryList() {
        return itemElementRelationshipHistoryList;
    }

    public void setItemElementRelationshipHistoryList(List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList) {
        this.itemElementRelationshipHistoryList = itemElementRelationshipHistoryList;
    }

    @XmlTransient
    public List<ItemElementRelationshipHistory> getItemElementRelationshipHistoryList1() {
        return itemElementRelationshipHistoryList1;
    }

    public void setItemElementRelationshipHistoryList1(List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList1) {
        this.itemElementRelationshipHistoryList1 = itemElementRelationshipHistoryList1;
    }

    @XmlTransient
    public List<ItemResource> getItemResourceList() {
        return itemResourceList;
    }

    public void setItemResourceList(List<ItemResource> itemResourceList) {
        this.itemResourceList = itemResourceList;
    }

    @XmlTransient
    public List<ItemElementRelationship> getItemElementRelationshipList() {
        return itemElementRelationshipList;
    }

    public void setItemElementRelationshipList(List<ItemElementRelationship> itemElementRelationshipList) {
        this.itemElementRelationshipList = itemElementRelationshipList;
    }

    @XmlTransient
    public List<ItemElementRelationship> getItemElementRelationshipList1() {
        return itemElementRelationshipList1;
    }

    public void setItemElementRelationshipList1(List<ItemElementRelationship> itemElementRelationshipList1) {
        this.itemElementRelationshipList1 = itemElementRelationshipList1;
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
        if (!(object instanceof ItemConnector)) {
            return false;
        }
        ItemConnector other = (ItemConnector) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        } else if (this.id == null && other.id == null) {
            // New item check if connector is the same. 
            return ObjectUtility.equals(this.connector, other.connector);             
        }
        return true;
    }

    public ItemConnector getItemConnectorOfItemConnectedTo() {
        return itemConnectorOfItemConnectedTo;
    }

    public void setItemConnectorOfItemConnectedTo(ItemConnector itemConnectorOfItemConnectedTo) {
        this.itemConnectorOfItemConnectedTo = itemConnectorOfItemConnectedTo;
    }

    public Item getItemConnectedVia() {
        return itemConnectedVia;
    }

    public void setItemConnectedVia(Item itemConnectedVia) {
        this.itemConnectedVia = itemConnectedVia;
    }
    
    @Override
    public String toString() {
        if (item != null) {
            if (label != null) {
                return label + " Connector for item: " + item; 
            }
            return "Connector for item: " + item; 
        }
        
        return "gov.anl.aps.cdb.portal.model.db.entities.ItemConnector[ id=" + id + " ]";
    }
    
    public boolean isConnected() {
        List<ItemElementRelationship> relationshipList = getItemElementRelationshipList();
        if ((relationshipList != null) && (!relationshipList.isEmpty())) {
            return true;
        }
        relationshipList = getItemElementRelationshipList1();
        if ((relationshipList != null) && (!relationshipList.isEmpty())) {
            return true;
        }
        return false;
    }
    
}
