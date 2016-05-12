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
import javax.persistence.OneToOne;
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
@Table(name = "item")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Item.findAll", 
            query = "SELECT i FROM Item i"),
    @NamedQuery(name = "Item.findById", 
            query = "SELECT i FROM Item i WHERE i.id = :id"),
    @NamedQuery(name = "Item.findByName", 
            query = "SELECT i FROM Item i WHERE i.name = :name"),
    @NamedQuery(name = "Item.findByItemIdentifier1", 
            query = "SELECT i FROM Item i WHERE i.itemIdentifier1 = :itemIdentifier1"),
    @NamedQuery(name = "Item.findByItemIdentifier2", 
            query = "SELECT i FROM Item i WHERE i.itemIdentifier2 = :itemIdentifier2"),
    @NamedQuery(name = "Item.findByQrId", 
            query = "SELECT i FROM Item i WHERE i.qrId = :qrId"),
    @NamedQuery(name = "Item.findByDescription", 
            query = "SELECT i FROM Item i WHERE i.description = :description"),
    @NamedQuery(name = "Item.findByDomainName", 
            query = "SELECT DISTINCT(i) FROM Item i JOIN i.entityTypeList etl WHERE i.domain.name = :domainName and etl.name = :entityTypeName")
})
public class Item extends CdbEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    private String name;
    @Size(max = 32)
    @Column(name = "item_identifier1")
    private String itemIdentifier1;
    @Size(max = 32)
    @Column(name = "item_identifier2")
    private String itemIdentifier2;
    @Column(name = "qr_id")
    private Integer qrId;
    @Size(max = 256)
    private String description;
    @JoinTable(name = "item_log", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Log> logList;
    @JoinTable(name = "item_entity_type", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "entity_type_id", referencedColumnName = "id")})
    @ManyToMany
    private List<EntityType> entityTypeList;
    @JoinTable(name = "item_item_category", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_category_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ItemCategory> itemCategoryList;
    @JoinTable(name = "item_item_type", joinColumns = {
        @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_type_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ItemType> itemTypeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentItem")
    private List<ItemElement> itemElementList;
    @OneToMany(mappedBy = "containedItem")
    private List<ItemElement> itemElementList1;
    @JoinColumn(name = "domain_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Domain domain;
    @OneToMany(mappedBy = "derivedFromItem")
    private List<Item> itemList;
    @JoinColumn(name = "derived_from_item_id", referencedColumnName = "id")
    @ManyToOne
    private Item derivedFromItem;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @OneToOne(optional = false)
    private EntityInfo entityInfo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<ItemConnector> itemConnectorList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<ItemSource> itemSourceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private List<ItemResource> itemResourceList;
    
    private transient ItemElement selfItemElement = null; 
    private transient String itemTypeString = null; 
    private transient String itemCategoryString = null; 

    public Item() {
    }

    public Item(Integer id) {
        this.id = id;
    }

    public Item(Integer id, String name) {
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

    public String getItemIdentifier1() {
        return itemIdentifier1;
    }

    public void setItemIdentifier1(String itemIdentifier1) {
        this.itemIdentifier1 = itemIdentifier1;
    }

    public String getItemIdentifier2() {
        return itemIdentifier2;
    }

    public void setItemIdentifier2(String itemIdentifier2) {
        this.itemIdentifier2 = itemIdentifier2;
    }

    public Integer getQrId() {
        return qrId;
    }

    public void setQrId(Integer qrId) {
        this.qrId = qrId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    @XmlTransient
    public List<EntityType> getEntityTypeList() {
        return entityTypeList;
    }

    public void setEntityTypeList(List<EntityType> entityTypeList) {
        this.entityTypeList = entityTypeList;
    }

    @XmlTransient
    public List<ItemCategory> getItemCategoryList() {
        return itemCategoryList;
    }
    
    public String getItemCategoryString() {
        if (itemCategoryString == null) {
            itemCategoryString = ""; 
            itemCategoryList.stream().forEach((itemCategory) -> {
                itemCategoryString += itemCategory.getName();
            });
        }
        
        return itemCategoryString;
    }

    public void setItemCategoryList(List<ItemCategory> itemCategoryList) {
        this.itemCategoryList = itemCategoryList;
    }

    @XmlTransient
    public List<ItemType> getItemTypeList() {
        return itemTypeList;
    }

    public void setItemTypeList(List<ItemType> itemTypeList) {
        this.itemTypeList = itemTypeList;
    }

    public String getItemTypeString() {
        if (itemTypeString == null) {
            itemTypeString = ""; 
            itemTypeList.stream().forEach((itemType) -> {
                itemTypeString += itemType.getName();
            });
        }
        
        return itemTypeString;
    }

    @XmlTransient
    public List<ItemElement> getItemElementList() {
        return itemElementList;
    }

    public void setItemElementList(List<ItemElement> itemElementList) {
        this.itemElementList = itemElementList;
    }

    @XmlTransient
    public List<ItemElement> getItemElementList1() {
        return itemElementList1;
    }

    public void setItemElementList1(List<ItemElement> itemElementList1) {
        this.itemElementList1 = itemElementList1;
    }
    
    @XmlTransient
    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Item getDerivedFromItem() {
        return derivedFromItem;
    }

    public void setDerivedFromItem(Item derivedFromItem) {
        this.derivedFromItem = derivedFromItem;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    @XmlTransient
    public List<ItemConnector> getItemConnectorList() {
        return itemConnectorList;
    }

    public void setItemConnectorList(List<ItemConnector> itemConnectorList) {
        this.itemConnectorList = itemConnectorList;
    }

    @XmlTransient
    public List<ItemSource> getItemSourceList() {
        return itemSourceList;
    }

    public void setItemSourceList(List<ItemSource> itemSourceList) {
        this.itemSourceList = itemSourceList;
    }

    @XmlTransient
    public List<ItemResource> getItemResourceList() {
        return itemResourceList;
    }

    public void setItemResourceList(List<ItemResource> itemResourceList) {
        this.itemResourceList = itemResourceList;
    }
    
    public ItemElement getSelfElement() {
        if (selfItemElement == null) {
            for (ItemElement ie : this.itemElementList) {
                if (ie.getName() == null) {
                    selfItemElement = ie; 
                    break; 
                }
            }
        }
        
        return selfItemElement; 
    }
    
    public List<PropertyValue> getPropertyValueList() {
        return this.getSelfElement().getPropertyValueList();
    }
    
    public void setImagePropertyList(List<PropertyValue> imageList) {
        this.getSelfElement().setImagePropertyList(imageList);
    }
    
    public List<PropertyValue> getImagePropertyList() {
        return this.getSelfElement().getImagePropertyList(); 
    }
    
    public CdbDomainEntity.PropertyValueInformation getPropertyValueInformation(int propertyTypeId) {
        return this.getSelfElement().getPropertyValueInformation(propertyTypeId); 
    }
    
    public String getPropertyValue1() {
        return getSelfElement().getPropertyValue1();
    }

    public void setPropertyValue1(String propertyValue1) {
        getSelfElement().setPropertyValueByIndex(1, propertyValue1);
    }

    public String getPropertyValue2() {
        return getSelfElement().getPropertyValue2();
    }

    public void setPropertyValue2(String propertyValue2) {
        getSelfElement().setPropertyValueByIndex(2, propertyValue2);
    }

    public String getPropertyValue3() {
        return getSelfElement().getPropertyValue3();
    }

    public void setPropertyValue3(String propertyValue3) {
        getSelfElement().setPropertyValueByIndex(3, propertyValue3);
    }

    public String getPropertyValue4() {
        return getSelfElement().getPropertyValue4();
    }

    public void setPropertyValue4(String propertyValue4) {
        getSelfElement().setPropertyValueByIndex(4, propertyValue4);
    }

    public String getPropertyValue5() {
        return getSelfElement().getPropertyValue5();
    }

    public void setPropertyValue5(String propertyValue5) {
        getSelfElement().setPropertyValueByIndex(5, propertyValue5);
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
        if (!(object instanceof Item)) {
            return false;
        }
        Item other = (Item) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.Item[ id=" + id + " ]";
    }
    
}
