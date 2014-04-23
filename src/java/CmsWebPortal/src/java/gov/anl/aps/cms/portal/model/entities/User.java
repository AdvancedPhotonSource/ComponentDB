package gov.anl.aps.cms.portal.model.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
 * @author sveseli
 */
@Entity
@Table(name = "user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = "User.findByFirstName", query = "SELECT u FROM User u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "User.findByLastName", query = "SELECT u FROM User u WHERE u.lastName = :lastName"),
    @NamedQuery(name = "User.findByMiddleName", query = "SELECT u FROM User u WHERE u.middleName = :middleName"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
    @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password")})
public class User extends CloneableEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "username")
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
    @Size(max = 16)
    @Column(name = "email")
    private String email;

    @Size(max = 16)
    @Column(name = "password")
    private String password;

    @JoinTable(name = "user_user_group", joinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_group_id", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.EAGER)
    private List<UserGroup> userGroupList;

    @OneToMany(mappedBy = "obsoletedByUser")
    private List<EntityInfo> entityInfoList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lastModifiedByUser")
    private List<EntityInfo> entityInfoList1;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "createdByUser")
    private List<EntityInfo> entityInfoList2;

    @OneToMany(mappedBy = "ownerUser")
    private List<EntityInfo> entityInfoList3;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "user")
    private List<UserSetting> userSettingList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "createdByUser")
    private List<Log> logList;

    private transient HashMap<String, UserSetting> userSettingMap = null;
    private transient Date userSettingsModificationDate = null;

    public User() {
    }

    public User(Integer id) {
        this.id = id;
    }

    public User(Integer id, String username, String firstName, String lastName) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        }
        else {
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

    @XmlTransient
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
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
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return username;
    }

}
