/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@JsonIgnoreProperties(value = {
    "itemElement",
    "list",
    "ownerUser",
    "ownerUserGroup",
    "createdByUser",
    "obsoletedByUser",
    "lastModifiedByUser",
    
    "ownerUserDisplayName",
    "createdByDisplayName",
    "lastModifiedByDisplayName",
    "obsoletedByDisplayName",
    "ownerGroupDisplayName"
})
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
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "entityInfo", fetch = FetchType.LAZY)
    private ItemElement itemElement;
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
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "entityInfo", fetch = FetchType.LAZY)
    private ListTbl list;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getCreatedOnDateTime() {
        return createdOnDateTime;
    }

    public void setCreatedOnDateTime(Date createdOnDateTime) {
        this.createdOnDateTime = createdOnDateTime;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getLastModifiedOnDateTime() {
        return lastModifiedOnDateTime;
    }

    public void setLastModifiedOnDateTime(Date lastModifiedOnDateTime) {
        this.lastModifiedOnDateTime = lastModifiedOnDateTime;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
    
    public ListTbl getList() {
        return list;
    }

    public void setList(ListTbl list) {
        this.list = list;
    }
    
    public String getOwnerUsername() {
        if (ownerUser != null) {
            return ownerUser.getUsername();
        }
        return null; 
    }
    
    public String getOwnerUserGroupName() {
        if (ownerUserGroup != null) {
            return ownerUserGroup.getName();
        }
        return null; 
    }
    
    public String getCreatedByUsername() {
        if (createdByUser != null) {
            return createdByUser.getUsername();
        }
        return null; 
    }
    
    public String getObsoletedByUsername() {
        if (obsoletedByUser != null) {
            return obsoletedByUser.getUsername();
        }
        return null;
    }
    
    public String getLastModifiedByUsername() {
        if (lastModifiedByUser != null) {
            return lastModifiedByUser.getUsername();
        }
        return null; 
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
