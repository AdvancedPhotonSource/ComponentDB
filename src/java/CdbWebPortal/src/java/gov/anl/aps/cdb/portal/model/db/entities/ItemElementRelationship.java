/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "item_element_relationship")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemElementRelationship.findAll", 
            query = "SELECT i FROM ItemElementRelationship i"),
    @NamedQuery(name = "ItemElementRelationship.findById", 
            query = "SELECT i FROM ItemElementRelationship i WHERE i.id = :id"),
    @NamedQuery(name = "ItemElementRelationship.findByRelationshipDetails", 
            query = "SELECT i FROM ItemElementRelationship i WHERE i.relationshipDetails = :relationshipDetails"),
    @NamedQuery(name = "ItemElementRelationship.findByLabel", 
            query = "SELECT i FROM ItemElementRelationship i WHERE i.label = :label"),
    @NamedQuery(name = "ItemElementRelationship.findByDescription", 
            query = "SELECT i FROM ItemElementRelationship i WHERE i.description = :description"),
    @NamedQuery(name = "ItemElementRelationship.findByRelationshipTypeNameAndFirstElementId", 
            query = "SELECT i FROM ItemElementRelationship i "
                    + "WHERE (i.firstItemElement.id = :itemElementId OR i.secondItemElement.id = :itemElementId) "
                    + "AND i.relationshipType.name = :relationshipTypeName")
})
public class ItemElementRelationship implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 64)
    @Column(name = "relationship_details")
    private String relationshipDetails;
    @Size(max = 64)
    private String label;
    @Size(max = 256)
    private String description;
    @JoinTable(name = "item_element_relationship_property", joinColumns = {
        @JoinColumn(name = "item_element_relationship_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "property_value_id", referencedColumnName = "id")})
    @ManyToMany
    private List<PropertyValue> propertyValueList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "itemElementRelationship")
    private List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList;
    @JoinColumn(name = "first_item_element_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ItemElement firstItemElement;
    @JoinColumn(name = "first_item_connector_id", referencedColumnName = "id")
    @ManyToOne
    private ItemConnector firstItemConnector;
    @JoinColumn(name = "second_item_element_id", referencedColumnName = "id")
    @ManyToOne
    private ItemElement secondItemElement;
    @JoinColumn(name = "second_item_connector_id", referencedColumnName = "id")
    @ManyToOne
    private ItemConnector secondItemConnector;
    @JoinColumn(name = "relationship_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private RelationshipType relationshipType;
    @JoinColumn(name = "link_item_element_id", referencedColumnName = "id")
    @ManyToOne
    private ItemElement linkItemElement;
    @JoinColumn(name = "resource_type_id", referencedColumnName = "id")
    @ManyToOne
    private ResourceType resourceType;

    public ItemElementRelationship() {
    }

    public ItemElementRelationship(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRelationshipDetails() {
        return relationshipDetails;
    }

    public void setRelationshipDetails(String relationshipDetails) {
        this.relationshipDetails = relationshipDetails;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    @XmlTransient
    public List<ItemElementRelationshipHistory> getItemElementRelationshipHistoryList() {
        return itemElementRelationshipHistoryList;
    }

    public void setItemElementRelationshipHistoryList(List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList) {
        this.itemElementRelationshipHistoryList = itemElementRelationshipHistoryList;
    }

    public ItemElement getFirstItemElement() {
        return firstItemElement;
    }

    public void setFirstItemElement(ItemElement firstItemElement) {
        this.firstItemElement = firstItemElement;
    }

    public ItemConnector getFirstItemConnector() {
        return firstItemConnector;
    }

    public void setFirstItemConnector(ItemConnector firstItemConnector) {
        this.firstItemConnector = firstItemConnector;
    }

    public ItemElement getSecondItemElement() {
        return secondItemElement;
    }

    public void setSecondItemElement(ItemElement secondItemElement) {
        this.secondItemElement = secondItemElement;
    }

    public ItemConnector getSecondItemConnector() {
        return secondItemConnector;
    }

    public void setSecondItemConnector(ItemConnector secondItemConnector) {
        this.secondItemConnector = secondItemConnector;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    public ItemElement getLinkItemElement() {
        return linkItemElement;
    }

    public void setLinkItemElement(ItemElement linkItemElement) {
        this.linkItemElement = linkItemElement;
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemElementRelationship)) {
            return false;
        }
        ItemElementRelationship other = (ItemElementRelationship) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship[ id=" + id + " ]";
    }
    
}
