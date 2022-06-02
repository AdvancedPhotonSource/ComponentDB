/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.controllers.utilities.ItemElementRelationshipControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
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
                    + "WHERE (i.firstItemElement.id = :itemElementId) "
                    + "AND i.relationshipType.name = :relationshipTypeName"),
    @NamedQuery(name = "ItemElementRelationship.findRelationshipsByTypeAndItemDomain",
            query = "SELECT i FROM ItemElementRelationship i "
                    + "JOIN i.firstItemElement ie "
                    + "JOIN ie.parentItem it "
                    + "JOIN it.domain d "
                    + "WHERE ie.derivedFromItemElement IS NULL "
                    + "AND ie.name IS NULL AND d.name = :domainName "
                    + "AND i.relationshipType.name = :relationshipTypeName")
})
public class ItemElementRelationship extends CdbEntity implements Serializable {

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
    @ManyToMany(cascade = CascadeType.ALL)
    private List<PropertyValue> propertyValueList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "itemElementRelationship")
    private List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList;
    @JoinColumn(name = "first_item_element_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ItemElement firstItemElement;
    @JoinColumn(name = "first_item_connector_id", referencedColumnName = "id")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private ItemConnector firstItemConnector;
    @Column(name = "first_sort_order")
    private Float firstSortOrder;
    @JoinColumn(name = "second_item_element_id", referencedColumnName = "id")
    @ManyToOne
    private ItemElement secondItemElement;
    @JoinColumn(name = "second_item_connector_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL)
    private ItemConnector secondItemConnector;
    @Column(name = "second_sort_order")
    private Float secondSortOrder;
    @JoinColumn(name = "relationship_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private RelationshipType relationshipType;
    @JoinColumn(name = "link_item_element_id", referencedColumnName = "id")
    @ManyToOne
    private ItemElement linkItemElement;
    @JoinColumn(name = "resource_type_id", referencedColumnName = "id")
    @ManyToOne
    private ResourceType resourceType;
    
    public static String VALUE_LABEL_PRIMARY_CABLE_CONN = "PRI";
    public static String VALUE_LABEL_DETAIL_CABLE_CONN = "DET";
    private static String PRIMARY_CABLE_CONN_INDICATOR = "yes";
    
    private transient Item importFirstItem = null;
    private transient Item importSecondItem = null;
    private transient String importFirstItemConnectorName;
    private transient String importSecondItemConnectorName;
    private transient Boolean importPrimaryCableConnection;

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

    @Override
    public void addPropertyValueToPropertyValueList(PropertyValue propertyValue) {
        if (propertyValueList == null) {
            propertyValueList = new ArrayList<>();
        }
        propertyValueList.add(0, propertyValue);
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
    
    public Item getFirstItem() {
        if (firstItemElement != null) {
            return firstItemElement.getParentItem();
        } else {
            return null;
        }
    }

    public ItemConnector getFirstItemConnector() {
        return firstItemConnector;
    }

    public void setFirstItemConnector(ItemConnector firstItemConnector) {
        this.firstItemConnector = firstItemConnector;
    }
    
    public String getFirstItemConnectorName() {
        if (firstItemConnector != null) {
            return firstItemConnector.getConnectorName();
        } else {
            return null;
        }
    }

    public Float getFirstSortOrder() {
        return firstSortOrder;
    }

    public void setFirstSortOrder(Float firstSortOrder) {
        this.firstSortOrder = firstSortOrder;
    }

    public ItemElement getSecondItemElement() {
        return secondItemElement;
    }

    public void setSecondItemElement(ItemElement secondItemElement) {
        this.secondItemElement = secondItemElement;
    }

    public Item getSecondItem() {
        if (secondItemElement != null) {
            return secondItemElement.getParentItem();
        } else {
            return null;
        }
    }

    public ItemConnector getSecondItemConnector() {
        return secondItemConnector;
    }

    public void setSecondItemConnector(ItemConnector secondItemConnector) {
        this.secondItemConnector = secondItemConnector;
    }

    public String getSecondItemConnectorName() {
        if (secondItemConnector != null) {
            return secondItemConnector.getConnectorName();
        } else {
            return null;
        }
    }

    public Float getSecondSortOrder() {
        return secondSortOrder;
    }

    public void setSecondSortOrder(Float secondSortOrder) {
        this.secondSortOrder = secondSortOrder;
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

        if (!(object instanceof ItemElementRelationship)) {
            return false;
        }
        
        ItemElementRelationship other = (ItemElementRelationship) object;
        
        if (other == this) {
            return true;
        }
        
        if (other.getId() != null) {
            return (other.getId().equals(id));
        } else {
            return false;
        }

// craig commented this out, I think it's up to the domain to determine if two IERs are equal,
// e.g., for cable connections, the key attributes can be the same
//        // check attributes if id is null
//        return (ObjectUtility.equals(other.getRelationshipType(), this.getRelationshipType())
//                && ObjectUtility.equals(other.getFirstItemElement(), this.getFirstItemElement())
//                && ObjectUtility.equals(other.getSecondItemElement(), this.getSecondItemElement())
//                && ObjectUtility.equals(other.getDescription(), this.getDescription())
//                && ObjectUtility.equals(other.getFirstItemConnector(), this.getFirstItemConnector())
//                && ObjectUtility.equals(other.getFirstSortOrder(), this.getFirstSortOrder())
//                && ObjectUtility.equals(other.getLabel(), this.getLabel())
//                && ObjectUtility.equals(other.getLinkItemElement(), this.getLinkItemElement())
//                && ObjectUtility.equals(other.getRelationshipDetails(), this.getRelationshipDetails())
//                && ObjectUtility.equals(other.getResourceType(), this.getResourceType())
//                && ObjectUtility.equals(other.getSecondItemConnector(), this.getSecondItemConnector())
//                && ObjectUtility.equals(other.getSecondSortOrder(), this.getSecondSortOrder()));
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship[ id=" + id + " ]";
    }
    
    @Override
    public ItemElementRelationshipControllerUtility getControllerUtility() {
        return new ItemElementRelationshipControllerUtility(); 
    }
    
    @Override
    public ValidInfo isDeleteAllowed() {

        boolean isValid = true;
        String validStr = "";

        // don't allow deleting primary connection
        if (isPrimaryCableConnection()) {
            isValid = false;
            validStr = "Deleting primary cable connection is not allowed.";
        }

        return new ValidInfo(isValid, validStr);
    }

    // convenience method for cable relationship type
    public boolean isPrimaryCableConnection() {
        return VALUE_LABEL_PRIMARY_CABLE_CONN.equals(getLabel());
    }
    
    public String getCableEndPrimaryIndicator() {
        if (isPrimaryCableConnection()) {
            return PRIMARY_CABLE_CONN_INDICATOR;
        } else {
            return "";
        }
    }
    
    public String getCableEndPrimarySortValue() {
        if (isPrimaryCableConnection()) {
            return "1";
        } else {
            return "2";
        }
    }

    // <editor-fold defaultstate="collapsed" desc="import/export support">   
    
    public Boolean getImportPrimaryCableConnection() {
        if (importPrimaryCableConnection == null) {
            importPrimaryCableConnection = isPrimaryCableConnection();
        }
        return importPrimaryCableConnection;
    }
    
    public void setImportPrimaryCableConnection(Boolean isPrimary) {
        importPrimaryCableConnection = isPrimary;
    }
    
    public Item getImportFirstItem() {
        if (importFirstItem == null) {
            importFirstItem = getFirstItem();
        }
        return importFirstItem;
    }

    public void setImportFirstItem(Item importFirstItem) {
        this.importFirstItem = importFirstItem;
    }

    public Item getImportSecondItem() {
        if (importSecondItem == null) {
            importSecondItem = getSecondItem();
        }
        return importSecondItem;
    }

    public void setImportSecondItem(Item importSecondItem) {
        this.importSecondItem = importSecondItem;
    }

    public String getImportFirstItemConnectorName() {
        if (importFirstItemConnectorName == null) {
            importFirstItemConnectorName = getFirstItemConnectorName();
        }
        return importFirstItemConnectorName;
    }

    public void setImportFirstItemConnectorName(String importFirstItemConnectorName) {
        this.importFirstItemConnectorName = importFirstItemConnectorName;
    }

    public String getImportSecondItemConnectorName() {
        if (importSecondItemConnectorName == null) {
            importSecondItemConnectorName = getSecondItemConnectorName();
        }
        return importSecondItemConnectorName;
    }

    public void setImportSecondItemConnectorName(String importSecondItemConnectorName) {
        this.importSecondItemConnectorName = importSecondItemConnectorName;
    }
    
    // </editor-fold>   
}
