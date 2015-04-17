/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
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
 * User info entity class.
 */
@Entity
@Table(name = "user_info")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserInfo.findAll", query = "SELECT u FROM UserInfo u ORDER BY u.lastName"),
    @NamedQuery(name = "UserInfo.findById", query = "SELECT u FROM UserInfo u WHERE u.id = :id"),
    @NamedQuery(name = "UserInfo.findByUsername", query = "SELECT u FROM UserInfo u WHERE u.username = :username"),
    @NamedQuery(name = "UserInfo.findByFirstName", query = "SELECT u FROM UserInfo u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "UserInfo.findByLastName", query = "SELECT u FROM UserInfo u WHERE u.lastName = :lastName"),
    @NamedQuery(name = "UserInfo.findByMiddleName", query = "SELECT u FROM UserInfo u WHERE u.middleName = :middleName"),
    @NamedQuery(name = "UserInfo.findByEmail", query = "SELECT u FROM UserInfo u WHERE u.email = :email"),
    @NamedQuery(name = "UserInfo.findByPassword", query = "SELECT u FROM UserInfo u WHERE u.password = :password"),
    @NamedQuery(name = "UserInfo.findByDescription", query = "SELECT u FROM UserInfo u WHERE u.description = :description")})
public class UserInfo extends CdbEntity {

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
    @JoinTable(name = "user_user_group", joinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_group_id", referencedColumnName = "id")})
    @OrderBy("id DESC")
    @ManyToMany
    private List<UserGroup> userGroupList;
    @OneToMany(mappedBy = "obsoletedByUser")
    private List<EntityInfo> entityInfoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lastModifiedByUser")
    private List<EntityInfo> entityInfoList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "createdByUser")
    private List<EntityInfo> entityInfoList2;
    @OneToMany(mappedBy = "ownerUser")
    private List<EntityInfo> entityInfoList3;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<UserSetting> userSettingList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "enteredByUser")
    private List<ComponentInstanceLocationHistory> componentInstanceLocationHistoryList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "enteredByUser")
    private List<Log> logList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "enteredByUser")
    private List<PropertyValue> propertyValueList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "enteredByUser")
    private List<PropertyValueHistory> propertyValueHistoryList;

    private transient HashMap<String, UserSetting> userSettingMap = null;
    private transient Date userSettingsModificationDate = null;
    private transient String fullNameForSelection = null;

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

    @Override
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<UserGroup> getUserGroupList() {
        return userGroupList;
    }

    public void setUserGroupList(List<UserGroup> userGroupList) {
        this.userGroupList = userGroupList;
    }

    @XmlTransient
    public List<EntityInfo> getEntityInfoList() {
        return entityInfoList;
    }

    public void setEntityInfoList(List<EntityInfo> entityInfoList) {
        this.entityInfoList = entityInfoList;
    }

    @XmlTransient
    public List<EntityInfo> getEntityInfoList1() {
        return entityInfoList1;
    }

    public void setEntityInfoList1(List<EntityInfo> entityInfoList1) {
        this.entityInfoList1 = entityInfoList1;
    }

    @XmlTransient
    public List<EntityInfo> getEntityInfoList2() {
        return entityInfoList2;
    }

    public void setEntityInfoList2(List<EntityInfo> entityInfoList2) {
        this.entityInfoList2 = entityInfoList2;
    }

    @XmlTransient
    public List<EntityInfo> getEntityInfoList3() {
        return entityInfoList3;
    }

    public void setEntityInfoList3(List<EntityInfo> entityInfoList3) {
        this.entityInfoList3 = entityInfoList3;
    }

    @XmlTransient
    public List<UserSetting> getUserSettingList() {
        return userSettingList;
    }

    public void setUserSettingList(List<UserSetting> userSettingList) {
        this.userSettingList = userSettingList;

        // Store settings into map for easy access
        createUserSettingMap();
    }

    private void createUserSettingMap() {
        userSettingMap = new HashMap<>();
        for (UserSetting setting : userSettingList) {
            userSettingMap.put(setting.getSettingType().getName(), setting);
        }
        updateSettingsModificationDate();
    }

    public UserSetting getUserSetting(String name) {
        if (userSettingMap == null) {
            createUserSettingMap();
        }
        return userSettingMap.get(name);
    }

    public void setUserSetting(String name, UserSetting userSetting) {
        if (userSettingMap == null) {
            createUserSettingMap();
        }
        UserSetting oldUserSetting = userSettingMap.get(name);
        if (oldUserSetting != null) {
            oldUserSetting.setValue(userSetting.getValue());
        } else {
            userSettingMap.put(name, userSetting);
        }
        userSettingsModificationDate = new Date();
    }

    public void setUserSettingValue(String name, Object value) {
        UserSetting userSetting = getUserSetting(name);
        if (userSetting != null && value != null) {
            userSetting.setValue(value.toString());
        }
    }

    public String getUserSettingValueAsString(String name, String defaultValue) {
        UserSetting userSetting = getUserSetting(name);
        if (userSetting == null) {
            return defaultValue;
        }
        return userSetting.getValue();
    }

    public Boolean getUserSettingValueAsBoolean(String name, Boolean defaultValue) {
        UserSetting userSetting = getUserSetting(name);
        if (userSetting == null) {
            return defaultValue;
        }
        String settingValue = userSetting.getValue();
        if (settingValue == null || settingValue.isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(settingValue);
    }

    public Integer getUserSettingValueAsInteger(String name, Integer defaultValue) {
        UserSetting userSetting = getUserSetting(name);
        if (userSetting == null) {
            return defaultValue;
        }
        String settingValue = userSetting.getValue();
        if (settingValue == null || settingValue.isEmpty()) {
            return defaultValue;
        }
        return Integer.parseInt(settingValue);
    }

    public Float getUserSettingValueAsFloat(String name, Float defaultValue) {
        UserSetting userSetting = getUserSetting(name);
        if (userSetting == null) {
            return defaultValue;
        }
        String settingValue = userSetting.getValue();
        if (settingValue == null || settingValue.isEmpty()) {
            return defaultValue;
        }
        return Float.parseFloat(settingValue);
    }

    public void updateSettingsModificationDate() {
        userSettingsModificationDate = new Date();
    }

    public Date getUserSettingsModificationDate() {
        return userSettingsModificationDate;
    }

    public boolean areUserSettingsModifiedAfterDate(Date date) {
        return date == null || userSettingsModificationDate == null || userSettingsModificationDate.after(date);
    }

    public boolean hasUserSettings() {
        return userSettingList != null && !userSettingList.isEmpty();
    }

    public boolean equalsByUsername(UserInfo other) {
        if (other != null) {
            return ObjectUtility.equals(this.username, other.username);
        }
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserInfo)) {
            return false;
        }
        UserInfo other = (UserInfo) object;
        if (this.id == null && other.id == null) {
            return equalsByUsername(other);
        }

        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return username;
    }

    @XmlTransient
    public List<ComponentInstanceLocationHistory> getComponentInstanceLocationHistoryList() {
        return componentInstanceLocationHistoryList;
    }

    public void setComponentInstanceLocationHistoryList(List<ComponentInstanceLocationHistory> componentInstanceLocationHistoryList) {
        this.componentInstanceLocationHistoryList = componentInstanceLocationHistoryList;
    }

    @XmlTransient
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    @XmlTransient
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    @XmlTransient
    public List<PropertyValueHistory> getPropertyValueHistoryList() {
        return propertyValueHistoryList;
    }

    public void setPropertyValueHistoryList(List<PropertyValueHistory> propertyValueHistoryList) {
        this.propertyValueHistoryList = propertyValueHistoryList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(id, username);
        searchResult.doesValueContainPattern("username", username, searchPattern);
        searchResult.doesValueContainPattern("firstName", firstName, searchPattern);
        searchResult.doesValueContainPattern("middleName", middleName, searchPattern);
        searchResult.doesValueContainPattern("lastName", lastName, searchPattern);
        searchResult.doesValueContainPattern("email", email, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        return searchResult;
    }

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

}
