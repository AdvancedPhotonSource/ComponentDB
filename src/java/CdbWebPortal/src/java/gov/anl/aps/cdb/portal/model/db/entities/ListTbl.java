/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "list")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "List.findAll", query = "SELECT l FROM ListTbl l"),
    @NamedQuery(name = "List.findById", query = "SELECT l FROM ListTbl l WHERE l.id = :id"),
    @NamedQuery(name = "List.findByName", query = "SELECT l FROM ListTbl l WHERE l.name = :name"),
    @NamedQuery(name = "List.findByDescription", query = "SELECT l FROM ListTbl l WHERE l.description = :description")})
public class ListTbl implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    private String name;
    @Size(max = 256)
    private String description;
    @JoinTable(name = "item_element_list", joinColumns = {
        @JoinColumn(name = "list_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "item_element_id", referencedColumnName = "id")})
    @ManyToMany
    private java.util.List<ItemElement> itemElementList;
    @JoinTable(name = "user_list", joinColumns = {
        @JoinColumn(name = "list_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @ManyToMany
    private java.util.List<UserInfo> userInfoList;
    @JoinTable(name = "user_group_list", joinColumns = {
        @JoinColumn(name = "list_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_group_id", referencedColumnName = "id")})
    @ManyToMany
    private java.util.List<UserGroup> userGroupList;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private EntityInfo entityInfo;

    public ListTbl() {
    }

    public void init(String name) {
        EntityInfo newEntityInfo = EntityInfoUtility.createEntityInfo();
        this.setEntityInfo(newEntityInfo);
        this.name = name;
    }

    public void init(String name, SettingEntity settingEntity) {
        init(name);
        if (settingEntity instanceof UserInfo) {
            userInfoList = new ArrayList<>();
            userInfoList.add((UserInfo) settingEntity);
        } else if (settingEntity instanceof UserGroup) {
            userGroupList = new ArrayList<>();
            userGroupList.add((UserGroup) settingEntity);
        }
    }

    public ListTbl(Integer id) {
        this.id = id;
    }

    public ListTbl(Integer id, String name) {
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

    @XmlTransient
    public java.util.List<ItemElement> getItemElementList() {
        return itemElementList;
    }

    public void setItemElementList(java.util.List<ItemElement> itemElementList) {
        this.itemElementList = itemElementList;
    }

    @XmlTransient
    public java.util.List<UserInfo> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(java.util.List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }

    @XmlTransient
    public java.util.List<UserGroup> getUserGroupList() {
        return userGroupList;
    }

    public void setUserGroupList(java.util.List<UserGroup> userGroupList) {
        this.userGroupList = userGroupList;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
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
        if (!(object instanceof ListTbl)) {
            return false;
        }
        ListTbl other = (ListTbl) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.List[ id=" + id + " ]";
    }

}
