/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "item_element_relationship_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemElementRelationshipHistory.findAll", query = "SELECT i FROM ItemElementRelationshipHistory i"),
    @NamedQuery(name = "ItemElementRelationshipHistory.findById", query = "SELECT i FROM ItemElementRelationshipHistory i WHERE i.id = :id"),
    @NamedQuery(name = "ItemElementRelationshipHistory.findByRelationshipDetails", query = "SELECT i FROM ItemElementRelationshipHistory i WHERE i.relationshipDetails = :relationshipDetails"),
    @NamedQuery(name = "ItemElementRelationshipHistory.findByLabel", query = "SELECT i FROM ItemElementRelationshipHistory i WHERE i.label = :label"),
    @NamedQuery(name = "ItemElementRelationshipHistory.findByDescription", query = "SELECT i FROM ItemElementRelationshipHistory i WHERE i.description = :description"),
    @NamedQuery(name = "ItemElementRelationshipHistory.findByEnteredOnDateTime", query = "SELECT i FROM ItemElementRelationshipHistory i WHERE i.enteredOnDateTime = :enteredOnDateTime")})
public class ItemElementRelationshipHistory implements Serializable {

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
    @Basic(optional = false)
    @NotNull
    @Column(name = "entered_on_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enteredOnDateTime;
    @JoinColumn(name = "item_element_relationship_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ItemElementRelationship itemElementRelationship;
    @JoinColumn(name = "first_item_element_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ItemElement firstItemElement;
    @JoinColumn(name = "first_item_connector_id", referencedColumnName = "id")
    @ManyToOne
    private ItemConnector firstItemConnector;
    @Column(name = "first_sort_order")
    private Float firstSortOrder;
    @JoinColumn(name = "second_item_element_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ItemElement secondItemElement;
    @JoinColumn(name = "second_item_connector_id", referencedColumnName = "id")
    @ManyToOne
    private ItemConnector secondItemConnector;
    @Column(name = "second_sort_order")
    private Float secondSortOrder;
    @JoinColumn(name = "relationship_id_for_parent", referencedColumnName = "id")
    @ManyToOne
    private ItemElementRelationship relationshipForParent;
    @JoinColumn(name = "link_item_element_id", referencedColumnName = "id")
    @ManyToOne
    private ItemElement linkItemElement;
    @JoinColumn(name = "resource_type_id", referencedColumnName = "id")
    @ManyToOne
    private ResourceType resourceType;
    @JoinColumn(name = "entered_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserInfo enteredByUser;

    public ItemElementRelationshipHistory() {
    }

    public ItemElementRelationshipHistory(Integer id) {
        this.id = id;
    }

    public ItemElementRelationshipHistory(Integer id, Date enteredOnDateTime) {
        this.id = id;
        this.enteredOnDateTime = enteredOnDateTime;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEnteredOnDateTime() {
        return enteredOnDateTime;
    }

    public void setEnteredOnDateTime(Date enteredOnDateTime) {
        this.enteredOnDateTime = enteredOnDateTime;
    }

    public ItemElementRelationship getItemElementRelationship() {
        return itemElementRelationship;
    }

    public void setItemElementRelationship(ItemElementRelationship itemElementRelationship) {
        this.itemElementRelationship = itemElementRelationship;
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

    public ItemConnector getSecondItemConnector() {
        return secondItemConnector;
    }

    public void setSecondItemConnector(ItemConnector secondItemConnector) {
        this.secondItemConnector = secondItemConnector;
    }

    public Float getSecondSortOrder() {
        return secondSortOrder;
    }

    public void setSecondSortOrder(Float secondSortOrder) {
        this.secondSortOrder = secondSortOrder;
    }

    public ItemElementRelationship getRelationshipForParent() {
        return relationshipForParent;
    }

    public void setRelationshipForParent(ItemElementRelationship relationshipForParent) {
        this.relationshipForParent = relationshipForParent;
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

    public UserInfo getEnteredByUser() {
        return enteredByUser;
    }

    public void setEnteredByUser(UserInfo enteredByUser) {
        this.enteredByUser = enteredByUser;
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
        if (!(object instanceof ItemElementRelationshipHistory)) {
            return false;
        }
        ItemElementRelationshipHistory other = (ItemElementRelationshipHistory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationshipHistory[ id=" + id + " ]";
    }
    
}
