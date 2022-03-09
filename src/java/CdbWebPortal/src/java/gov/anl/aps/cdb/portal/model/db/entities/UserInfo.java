/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.controllers.LoginController;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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
@Cacheable(true)
@Table(name = "user_info")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserInfo.findAll", query = "SELECT u FROM UserInfo u ORDER BY u.lastName ASC"),
    @NamedQuery(name = "UserInfo.findById", query = "SELECT u FROM UserInfo u WHERE u.id = :id"),
    @NamedQuery(name = "UserInfo.findByUsername", query = "SELECT u FROM UserInfo u WHERE u.username = :username"),
    @NamedQuery(name = "UserInfo.findByFirstName", query = "SELECT u FROM UserInfo u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "UserInfo.findByLastName", query = "SELECT u FROM UserInfo u WHERE u.lastName = :lastName"),
    @NamedQuery(name = "UserInfo.findByMiddleName", query = "SELECT u FROM UserInfo u WHERE u.middleName = :middleName"),
    @NamedQuery(name = "UserInfo.findByEmail", query = "SELECT u FROM UserInfo u WHERE u.email = :email"),
    @NamedQuery(name = "UserInfo.findByPassword", query = "SELECT u FROM UserInfo u WHERE u.password = :password"),
    @NamedQuery(name = "UserInfo.findByDescription", query = "SELECT u FROM UserInfo u WHERE u.description = :description")})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo extends SettingEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    private String username;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "first_name")
    private String firstName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "last_name")
    private String lastName;
    @Size(max = 16)
    @Column(name = "middle_name")
    private String middleName;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 64)
    private String email;
    @Size(max = 256)
    private String password;
    @Size(max = 256)
    private String description;
    @JoinTable(name = "user_list", joinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "list_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ListTbl> listList;
    @JoinTable(name = "user_user_group", joinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_group_id", referencedColumnName = "id")})
    @ManyToMany
    private List<UserGroup> usersGroupList;
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "enteredByUser")
    private List<Log> logList;
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "enteredByUser")
    private List<PropertyValue> propertyValueList;
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "userInfo")
    private List<UserRole> usersRoleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<UserSetting> userSettingList;
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "enteredByUser")
    private List<PropertyValueHistory> propertyValueHistoryList;
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "ownerUser")
    private List<EntityInfo> entityInfoList;
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "createdByUser")
    private List<EntityInfo> entityInfoList1;
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "lastModifiedByUser")
    private List<EntityInfo> entityInfoList2;
    @OneToMany(mappedBy = "obsoletedByUser")
    private List<EntityInfo> entityInfoList3;
    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "enteredByUser")
    private List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList;
            
    private transient String fullNameForSelection = null;
    private transient String userGroupListString = null; 
    private transient String passwordEntry = null;   
    
    private transient Boolean importSetPassword = null;
    private transient String importPassword = null;
    
    public UserInfo() {
    }

    public UserInfo(Integer id) {
        this.id = id;
    }

    public UserInfo(Integer id, String username, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    @JsonIgnore
    public boolean getHasLocalPassword() {
        return password != null; 
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getPasswordEntry() {
        return passwordEntry;
    }

    public void setPasswordEntry(String passwordEntry) {
        this.passwordEntry = passwordEntry;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    @JsonIgnore
    public Boolean getImportSetPassword() {
        return importSetPassword;
    }
    
    public void setImportSetPassword(Boolean b) {
        importSetPassword = b;
    }
    
    @JsonIgnore
    public String getImportPassword() {
        return importPassword;
    }
    
    public void setImportPassword(String password) {
        importPassword = password;
    }

    @JsonIgnore
    public String getUserGroupListString() {
        if (userGroupListString == null) {            
            String userGroupString = CdbEntityController.displayEntityList(getUserGroupList());
            userGroupListString = userGroupString;
        }
        return userGroupListString;
    }

    public void setUserGroupListString(String userGroupListString) {
        this.userGroupListString = userGroupListString;
    }
    
    @XmlTransient
    @JsonIgnore
    public List<ListTbl> getListList() {
        return listList;
    }

    public void setListList(List<ListTbl> listList) {
        this.listList = listList;
    }
    
    public List<UserGroup> getUserGroupList() {
        return usersGroupList;
    }

    public void setUserGroupList(List<UserGroup> userGroupList) {
        this.usersGroupList = userGroupList;
        userGroupListString = null; // erase cached string rep of list
    }

    @XmlTransient
    @JsonIgnore
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    @XmlTransient
    @JsonIgnore
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }
    
    public List<UserRole> getUserRoleList() {
        return usersRoleList;
    }

    public void setUserRoleList(List<UserRole> userRoleList) {
        this.usersRoleList = userRoleList;
    }

    @XmlTransient
    @JsonIgnore
    public List<UserSetting> getUserSettingList() {
        return userSettingList;
    }

    public void setUserSettingList(List<UserSetting> userSettingList) {
        this.userSettingList = userSettingList;
    }

    @XmlTransient
    @JsonIgnore
    public List<PropertyValueHistory> getPropertyValueHistoryList() {
        return propertyValueHistoryList;
    }

    public void setPropertyValueHistoryList(List<PropertyValueHistory> propertyValueHistoryList) {
        this.propertyValueHistoryList = propertyValueHistoryList;
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
    public List<EntityInfo> getEntityInfoList1() {
        return entityInfoList1;
    }

    public void setEntityInfoList1(List<EntityInfo> entityInfoList1) {
        this.entityInfoList1 = entityInfoList1;
    }

    @XmlTransient
    @JsonIgnore
    public List<EntityInfo> getEntityInfoList2() {
        return entityInfoList2;
    }

    public void setEntityInfoList2(List<EntityInfo> entityInfoList2) {
        this.entityInfoList2 = entityInfoList2;
    }

    @XmlTransient
    @JsonIgnore
    public List<EntityInfo> getEntityInfoList3() {
        return entityInfoList3;
    }

    public void setEntityInfoList3(List<EntityInfo> entityInfoList3) {
        this.entityInfoList3 = entityInfoList3;
    }

    @XmlTransient
    @JsonIgnore
    public List<ItemElementRelationshipHistory> getItemElementRelationshipHistoryList() {
        return itemElementRelationshipHistoryList;
    }

    public void setItemElementRelationshipHistoryList(List<ItemElementRelationshipHistory> itemElementRelationshipHistoryList) {
        this.itemElementRelationshipHistoryList = itemElementRelationshipHistoryList;
    }
    
    @JsonIgnore
    public boolean isUserAdmin() {
        List<String> adminGroupNameList = LoginController.getAdminGroupNameList();
        for (String adminGroupName : adminGroupNameList) {
            if (isUserMemberOfUserGroup(adminGroupName)) {
                return true; 
            }
        }
        return false;        
    }
    
    @JsonIgnore
    public boolean isUserAdvanced() {
        List<String> advancedGroupNameList = LoginController.getAdvancedGroupNameList();
        for (String advancedGroupName : advancedGroupNameList) {
            if (isUserMemberOfUserGroup(advancedGroupName)) {
                return true; 
            }
        }
        return false;        
    }
    
    @JsonIgnore
    public boolean isUserMaintainer() {
        List<String> maintainerGroupNameList = LoginController.getMaintainerGroupNameList();
        for (String maintainerGroupName : maintainerGroupNameList) {
            if (isUserMemberOfUserGroup(maintainerGroupName)) {
                return true; 
            }
        }
        return false;        
    }
    
    public boolean isUserMemberOfUserGroup(String groupName) {
        for (UserGroup userGroup : getUserGroupList()) {
            if (userGroup.getName().equals(groupName)) {
                return true;
            }
        }
        return false;
    }
        
    @JsonIgnore
    public String getFullNameForSelection() {
        if (fullNameForSelection != null) {
            return fullNameForSelection;
        }

        if (lastName == null || lastName.isEmpty()) {
            return username;
        }

        fullNameForSelection = lastName;
        if (firstName != null && !firstName.isEmpty()) {
            fullNameForSelection += ", " + firstName;
        }
        if (middleName != null && !middleName.isEmpty()) {
            fullNameForSelection += " " + middleName;
        }
        return fullNameForSelection;
    }
    
    @JsonIgnore
    public String getDisplayName() {
        return "(" + getUsername() + ") " + getFullNameForSelection();
    }

    @Override
    public SearchResult createSearchResultInfo(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(this, id, username);
        searchResult.doesValueContainPattern("username", username, searchPattern);
        searchResult.doesValueContainPattern("firstName", firstName, searchPattern);
        searchResult.doesValueContainPattern("middleName", middleName, searchPattern);
        searchResult.doesValueContainPattern("lastName", lastName, searchPattern);
        searchResult.doesValueContainPattern("email", email, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        return searchResult;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (id != null) {
            hash += id.hashCode();
        } else if (getUsername() != null) {
            hash += getUsername().hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof UserInfo)) {
            return false;
        }
        
        UserInfo other = (UserInfo) object;
        
        // special case for new items uses name
        if ((this.id == null) && (other.id == null)) {
            
            if ((this.getUsername() == null) && (other.getUsername() == null)) {
                // both names null
                return true;
            } else if (((this.getUsername() == null) && other.getUsername() != null) 
                    || (this.getUsername() != null && !this.getUsername().equals(other.getUsername()))) {
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
        return lastName + ", " + firstName + " (" + username + ")"; 
    }

    @Override
    @JsonIgnore
    public List<EntitySetting> getSettingList() {
        return (List<EntitySetting>)(List<?>) getUserSettingList(); 
    }

    @Override
    @JsonIgnore
    public List<ListTbl> getItemElementLists() {
        return getListList(); 
    }

    @Override
    public void setSettingList(List<EntitySetting> entitySettingList) {
        setUserSettingList((List<UserSetting>)(List<?>)entitySettingList);
    }

    @Override
    public EntitySetting createNewEntitySetting() {
        return new UserSetting();
    }
    
}
