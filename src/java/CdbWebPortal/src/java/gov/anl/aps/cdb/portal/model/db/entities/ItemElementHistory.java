/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "item_element_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemElementHistory.findAll",
            query = "SELECT i FROM ItemElementHistory i"),
    @NamedQuery(name = "ItemElementHistory.findById",
            query = "SELECT i FROM ItemElementHistory i WHERE i.id = :id"),
    @NamedQuery(name = "ItemElementHistory.findByIsRequired",
            query = "SELECT i FROM ItemElementHistory i WHERE i.isRequired = :isRequired"),
    @NamedQuery(name = "ItemElementHistory.findByDescription",
            query = "SELECT i FROM ItemElementHistory i WHERE i.description = :description"),
    @NamedQuery(name = "ItemElementHistory.findBySortOrder",
            query = "SELECT i FROM ItemElementHistory i WHERE i.sortOrder = :sortOrder"),})
public class ItemElementHistory extends CdbEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @JoinColumn(name = "item_element_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ItemElement itemElement;
    @Size(max = 64)
    @Column(name = "snapshot_element_name")
    private String snapshotElementName;
    @Column(name = "is_required")
    private Boolean isRequired;
    @Column(name = "is_housed")
    private boolean isHoused;
    @Size(max = 256)
    private String description;    
    @Column(name = "sort_order")
    private Float sortOrder;   
    @JoinColumn(name = "derived_from_item_element_id", referencedColumnName = "id")
    @ManyToOne
    private ItemElement derivedFromItemElement;
    @JoinColumn(name = "parent_item_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Item parentItem;
    @Size(max = 256)
    @Column(name = "snapshot_parent_name")
    private String snapshotParentName;
    @JoinColumn(name = "contained_item_id1", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.MERGE)
    private Item containedItem1;
    @Size(max = 256)
    @Column(name = "snapshot_contained_item_1_name")
    private String snapshotContainedItem1Name;
    @JoinColumn(name = "contained_item_id2", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.MERGE)
    private Item containedItem2;
    @Size(max = 256)
    @Column(name = "snapshot_contained_item_2_name")
    private String snapshotContainedItem2Name;
    @JoinColumn(name = "entered_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserInfo enteredByUser;
    @Basic(optional = false)
    @Column(name = "entered_on_date_time")
    @Temporal(TemporalType.TIMESTAMP)    
    private Date enteredOnDateTime;

    public ItemElementHistory() {
    }
    
    public ItemElementHistory(ItemElement element, EntityInfo entityInfo) {
        this.itemElement = element; 
        this.snapshotElementName = element.getName(); 
        Item parentItem = element.getParentItem();
        // No need to track parent item for self element. 
        if (parentItem != null && !parentItem.getSelfElement().equals(element)) {
            this.parentItem = parentItem; 
        }
        this.snapshotParentName = element.getParentItem().toString(); 
        this.containedItem1 = element.getContainedItem();
        if (this.containedItem1 != null) {
            this.snapshotContainedItem1Name = this.containedItem1.toString();
        }
        this.containedItem2 = element.getContainedItem2(); 
        if (this.containedItem2 != null) {
            this.snapshotContainedItem2Name = this.containedItem2.toString();
        }
        this.derivedFromItemElement = element.getDerivedFromItemElement(); 
        this.isRequired = element.getIsRequired(); 
        this.sortOrder = element.getSortOrder(); 
        this.description = element.getDescription(); 
        
        this.isHoused = element.getIsHoused(); 
        
        this.enteredByUser = entityInfo.getLastModifiedByUser();
        this.enteredOnDateTime = entityInfo.getLastModifiedOnDateTime(); 
    }

    public ItemElementHistory(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public boolean getIsHoused() {
        return isHoused;
    }

    public void setIsHoused(boolean isHoused) {
        this.isHoused = isHoused;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Float sortOrder) {
        this.sortOrder = sortOrder;
    }

    public ItemElement getItemElement() {
        return itemElement;
    }

    public void setItemElement(ItemElement itemElement) {
        this.itemElement = itemElement;
    }

    public String getSnapshotElementName() {
        return snapshotElementName;
    }

    public void setSnapshotElementName(String snapshotElementName) {
        this.snapshotElementName = snapshotElementName;
    }
    
    @XmlTransient
    public Item getParentItem() {
        // Handle self element
        if (parentItem == null && itemElement != null) {
            Item parentItem = itemElement.getParentItem();
            if (parentItem != null && parentItem.getSelfElement().equals(itemElement)) {
                return parentItem; 
            }
        }
        return parentItem;
    }

    public void setParentItem(Item parentItem) {
        this.parentItem = parentItem;
    }

    public String getSnapshotParentName() {
        return snapshotParentName;
    }

    public void setSnapshotParentName(String snapshotParentName) {
        this.snapshotParentName = snapshotParentName;
    }
    
    @XmlTransient
    public Item getContainedItem() {
        return containedItem1;
    }

    public void setContainedItem(Item containedItem) {        
        this.containedItem1 = containedItem;
    }

    public String getSnapshotContainedItemName() {
        return snapshotContainedItem1Name;
    }

    public void setSnapshotContainedItemName(String snapshotContainedItem1Name) {
        this.snapshotContainedItem1Name = snapshotContainedItem1Name;
    }
    
    @XmlTransient
    public Item getContainedItem2() {
        return containedItem2;
    }

    public void setContainedItem2(Item containedItem2) {
        this.containedItem2 = containedItem2;
    }

    public String getSnapshotContainedItem2Name() {
        return snapshotContainedItem2Name;
    }

    public void setSnapshotContainedItem2Name(String snapshotContainedItem2Name) {
        this.snapshotContainedItem2Name = snapshotContainedItem2Name;
    }
    
    @XmlTransient
    public ItemElement getDerivedFromItemElement() {
        return derivedFromItemElement;
    }

    public void setDerivedFromItemElement(ItemElement derivedFromItemElement) {
        this.derivedFromItemElement = derivedFromItemElement;
    }

    @XmlTransient
    public UserInfo getEnteredByUser() {
        return enteredByUser;
    }

    public void setEnteredByUser(UserInfo enteredByUser) {
        this.enteredByUser = enteredByUser;
    }

    @XmlTransient
    public Date getEnteredOnDateTime() {
        return enteredOnDateTime;
    }

    public void setEnteredOnDateTime(Date enteredOnDateTime) {
        this.enteredOnDateTime = enteredOnDateTime;
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
        if (!(object instanceof ItemElementHistory)) {
            return false;
        }
        ItemElementHistory other = (ItemElementHistory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (snapshotElementName != null && snapshotElementName.isEmpty() == false) {
            return snapshotElementName;
        }

        return "gov.anl.aps.cdb.portal.model.db.entities.ItemElementHistory[ id=" + id + " ]";
    }

}
