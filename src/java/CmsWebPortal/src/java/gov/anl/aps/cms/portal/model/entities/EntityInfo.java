/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
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
public class EntityInfo implements Serializable
{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "is_group_writeable")
    private Boolean isGroupWriteable = true;

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

    @JoinColumn(name = "obsoleted_by_user_id", referencedColumnName = "id")
    @ManyToOne
    private User obsoletedByUser;

    @JoinColumn(name = "last_modified_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User lastModifiedByUser;

    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User createdByUser;

    @JoinColumn(name = "owner_user_group_id", referencedColumnName = "id")
    @ManyToOne
    private UserGroup ownerUserGroup;

    @JoinColumn(name = "owner_user_id", referencedColumnName = "id")
    @ManyToOne
    private User ownerUser;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entityInfo")
    private List<Collection> collectionList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entityInfo")
    private List<Component> componentList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entityInfoId")
    private List<DesignComponent> designComponentList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entityInfoId")
    private List<ComponentInstance> componentInstanceList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entityInfoId")
    private List<Design> designList;

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

    public User getObsoletedByUser() {
        return obsoletedByUser;
    }

    public void setObsoletedByUser(User obsoletedByUser) {
        this.obsoletedByUser = obsoletedByUser;
    }

    public User getLastModifiedByUser() {
        return lastModifiedByUser;
    }

    public void setLastModifiedByUser(User lastModifiedByUser) {
        this.lastModifiedByUser = lastModifiedByUser;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    public UserGroup getOwnerUserGroup() {
        return ownerUserGroup;
    }

    public void setOwnerUserGroup(UserGroup ownerUserGroup) {
        this.ownerUserGroup = ownerUserGroup;
    }

    public User getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(User ownerUser) {
        this.ownerUser = ownerUser;
    }

    @XmlTransient
    public List<Collection> getCollectionList() {
        return collectionList;
    }

    public void setCollectionList(List<Collection> collectionList) {
        this.collectionList = collectionList;
    }

    @XmlTransient
    public List<Component> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<Component> componentList) {
        this.componentList = componentList;
    }

    @XmlTransient
    public List<DesignComponent> getDesignComponentList() {
        return designComponentList;
    }

    public void setDesignComponentList(List<DesignComponent> designComponentList) {
        this.designComponentList = designComponentList;
    }

    @XmlTransient
    public List<ComponentInstance> getComponentInstanceList() {
        return componentInstanceList;
    }

    public void setComponentInstanceList(List<ComponentInstance> componentInstanceList) {
        this.componentInstanceList = componentInstanceList;
    }

    @XmlTransient
    public List<Design> getDesignList() {
        return designList;
    }

    public void setDesignList(List<Design> designList) {
        this.designList = designList;
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
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.portal.model.entities.EntityInfo[ id=" + id + " ]";
    }

}
