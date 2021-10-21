/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.utilities.CollectionUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
@Table(name = "user_group")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserGroup.findAll", query = "SELECT u FROM UserGroup u ORDER BY u.name ASC"),
    @NamedQuery(name = "UserGroup.findById", query = "SELECT u FROM UserGroup u WHERE u.id = :id"),
    @NamedQuery(name = "UserGroup.findByName", query = "SELECT u FROM UserGroup u WHERE u.name = :name"),
    @NamedQuery(name = "UserGroup.findByDescription", query = "SELECT u FROM UserGroup u WHERE u.description = :description"),
    @NamedQuery(name = "UserGroup.findGroupsWithSettings", query = "SELECT u FROM UserGroup u WHERE u.userGroupSettingList IS NOT EMPTY")
})
public class UserGroup extends SettingEntity implements Serializable {

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
    @JoinTable(name = "user_user_group", joinColumns = {
        @JoinColumn(name = "user_group_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @ManyToMany
    @OrderBy("lastName ASC")
    private List<UserInfo> userInfoList;
    @JoinTable(name = "user_group_list", joinColumns = {
        @JoinColumn(name = "user_group_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "list_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ListTbl> listList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userGroup")
    private List<UserRole> userRoleList;
    @OneToMany(mappedBy = "ownerUserGroup")
    private List<EntityInfo> entityInfoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userGroup")
    private List<UserGroupSetting> userGroupSettingList;           

    public UserGroup() {
    }

    public UserGroup(Integer id) {
        this.id = id;
    }

    public UserGroup(Integer id, String name) {
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
    @JsonIgnore
    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }
    
    @JsonIgnore
    public String getUserInfoListString() {                
        if (userInfoList == null || userInfoList.isEmpty()) {
            return "No users assigned"; 
        } 
        return CollectionUtility.displayItemListWithoutOutsideDelimiters(userInfoList, ",");
    }

    @XmlTransient
    @JsonIgnore
    public List<gov.anl.aps.cdb.portal.model.db.entities.ListTbl> getListList() {
        return listList;
    }

    public void setListList(List<gov.anl.aps.cdb.portal.model.db.entities.ListTbl> listList) {
        this.listList = listList;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserRole> getUserRoleList() {
        return userRoleList;
    }

    public void setUserRoleList(List<UserRole> userRoleList) {
        this.userRoleList = userRoleList;
    }

    @XmlTransient
    @JsonIgnore
    public List<EntityInfo> getEntityInfoList() {
        return entityInfoList;
    }

    public void setEntityInfoList(List<EntityInfo> entityInfoList) {
        this.entityInfoList = entityInfoList;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserGroupSetting> getUserGroupSettingList() {
        return userGroupSettingList;
    }

    public void setUserGroupSettingList(List<UserGroupSetting> userGroupSettingList) {
        this.userGroupSettingList = userGroupSettingList;
    }
    
    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(this, id, name);
        searchResult.doesValueContainPattern("name", name, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        return searchResult;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (id != null) {
            hash += id.hashCode();
        } else if (getName() != null) {
            hash += getName().hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof UserGroup)) {
            return false;
        }
        
        UserGroup other = (UserGroup) object;
        
        // special case for new items uses name
        if ((this.id == null) && (other.id == null)) {
            
            if ((this.getName() == null) && (other.getName() == null)) {
                // both names null
                return true;
            } else if (((this.getName() == null) && other.getName() != null) || (this.getName() != null && !this.getName().equals(other.getName()))) {
                // names are not equal
                return false;
            } else {
                // names are equal
                return true;
            }
            
        // check for existing items uses id
        } else if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            // at least one of the items exists and ids are not equal
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    @JsonIgnore
    public List<EntitySetting> getSettingList() {
        return (List<EntitySetting>)(List<?>) getUserGroupSettingList(); 
    }

    @Override
    @JsonIgnore
    public List<ListTbl> getItemElementLists() {
        return getListList(); 
    }

    @Override
    public void setSettingList(List<EntitySetting> entitySettingList) {
        setUserGroupSettingList((List<UserGroupSetting>)(List<?>)entitySettingList);
    }

    @Override
    public EntitySetting createNewEntitySetting() {
        return new UserGroupSetting(); 
    }
    
}
