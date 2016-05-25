/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "entity_info")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EntityInfo.findAll", query = "SELECT e FROM EntityInfo e"),
    @NamedQuery(name = "EntityInfo.findById", query = "SELECT e FROM EntityInfo e WHERE e.id = :id"),
    @NamedQuery(name = "EntityInfo.findByIsGroupWriteable", query = "SELECT e FROM EntityInfo e WHERE e.isGroupWriteable = :isGroupWriteable"),
    @NamedQuery(name = "EntityInfo.findByCreatedOnDateTime", query = "SELECT e FROM EntityInfo e WHERE e.createdOnDateTime = :createdOnDateTime"),
    @NamedQuery(name = "EntityInfo.findByLastModifiedOnDateTime", query = "SELECT e FROM EntityInfo e WHERE e.lastModifiedOnDateTime = :lastModifiedOnDateTime"),
    @NamedQuery(name = "EntityInfo.findByObsoletedOnDateTime", query = "SELECT e FROM EntityInfo e WHERE e.obsoletedOnDateTime = :obsoletedOnDateTime")})
public class EntityInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Column(name = "is_group_writeable")
    private Boolean isGroupWriteable;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_on_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOnDateTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "last_modified_on_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedOnDateTime;
    @Column(name = "obsoleted_on_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date obsoletedOnDateTime;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "entityInfo")
    private ItemElement itemElement;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "entityInfo")
    private Item item;
    @JoinColumn(name = "owner_user_id", referencedColumnName = "id")
    @ManyToOne
    private UserInfo ownerUser;
    @JoinColumn(name = "owner_user_group_id", referencedColumnName = "id")
    @ManyToOne
    private UserGroup ownerUserGroup;
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserInfo createdByUser;
    @JoinColumn(name = "last_modified_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserInfo lastModifiedByUser;
    @JoinColumn(name = "obsoleted_by_user_id", referencedColumnName = "id")
    @ManyToOne
    private UserInfo obsoletedByUser;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "entityInfo")
    private List list;

    public EntityInfo() {
    }

    public EntityInfo(Integer id) {
        this.id = id;
    }

    public EntityInfo(Integer id, Date createdOnDateTime, Date lastModifiedOnDateTime) {
        this.id = id;
        this.createdOnDateTime = createdOnDateTime;
        this.lastModifiedOnDateTime = lastModifiedOnDateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIsGroupWriteable() {
        return isGroupWriteable;
    }

    public void setIsGroupWriteable(Boolean isGroupWriteable) {
        this.isGroupWriteable = isGroupWriteable;
    }

    public Date getCreatedOnDateTime() {
        return createdOnDateTime;
    }

    public void setCreatedOnDateTime(Date createdOnDateTime) {
        this.createdOnDateTime = createdOnDateTime;
    }

    public Date getLastModifiedOnDateTime() {
        return lastModifiedOnDateTime;
    }

    public void setLastModifiedOnDateTime(Date lastModifiedOnDateTime) {
        this.lastModifiedOnDateTime = lastModifiedOnDateTime;
    }

    public Date getObsoletedOnDateTime() {
        return obsoletedOnDateTime;
    }

    public void setObsoletedOnDateTime(Date obsoletedOnDateTime) {
        this.obsoletedOnDateTime = obsoletedOnDateTime;
    }

    public ItemElement getItemElement() {
        return itemElement;
    }

    public void setItemElement(ItemElement itemElement) {
        this.itemElement = itemElement;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public UserInfo getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(UserInfo ownerUser) {
        this.ownerUser = ownerUser;
    }
    
    public String getUserInfoDisplayName(UserInfo userInfo) {
        if (userInfo != null) {
            return "(" + userInfo.getUsername() + ") " + userInfo.getFullNameForSelection();
        }
        return ""; 
    }
    
    public String getOwnerUserDisplayName() {
        if (ownerUser == null) {
            return "-";
        }
        return getUserInfoDisplayName(ownerUser); 
    }
    
    public String getCreatedByDisplayName() {
        return getUserInfoDisplayName(createdByUser); 
    }

    public String getLastModifiedByDisplayName() {
        return getUserInfoDisplayName(lastModifiedByUser); 
    }
    
    public String getObsoletedByDisplayName() {
        return getUserInfoDisplayName(obsoletedByUser); 
    }
    
    public String getOwnerGroupDisplayName() {
        if (ownerUserGroup == null) {
            return "-";
        }
        return ownerUserGroup.getName(); 
    }

    public UserGroup getOwnerUserGroup() {
        return ownerUserGroup;
    }

    public void setOwnerUserGroup(UserGroup ownerUserGroupId) {
        this.ownerUserGroup = ownerUserGroupId;
    }

    public UserInfo getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(UserInfo createdByUser) {
        this.createdByUser = createdByUser;
    }

    public UserInfo getLastModifiedByUser() {
        return lastModifiedByUser;
    }

    public void setLastModifiedByUser(UserInfo lastModifiedByUser) {
        this.lastModifiedByUser = lastModifiedByUser;
    }

    public UserInfo getObsoletedByUser() {
        return obsoletedByUser;
    }

    public void setObsoletedByUser(UserInfo obsoletedByUser) {
        this.obsoletedByUser = obsoletedByUser;
    }
    
    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
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
        if (!(object instanceof EntityInfo)) {
            return false;
        }
        EntityInfo other = (EntityInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.EntityInfo[ id=" + id + " ]";
    }
    
}
