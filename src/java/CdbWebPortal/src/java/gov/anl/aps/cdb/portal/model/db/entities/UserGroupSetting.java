/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "user_group_setting")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserGroupSetting.findAll", query = "SELECT u FROM UserGroupSetting u"),
    @NamedQuery(name = "UserGroupSetting.findById", query = "SELECT u FROM UserGroupSetting u WHERE u.id = :id"),
    @NamedQuery(name = "UserGroupSetting.findByValue", query = "SELECT u FROM UserGroupSetting u WHERE u.value = :value")})
public class UserGroupSetting extends EntitySetting implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 64)
    private String value;
    @JoinColumn(name = "user_group_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserGroup userGroup;
    @JoinColumn(name = "setting_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private SettingType settingType;

    public UserGroupSetting() {
    }

    public UserGroupSetting(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    @Override
    public SettingType getSettingType() {
        return settingType;
    }

    @Override
    public void setSettingType(SettingType settingType) {
        this.settingType = settingType;
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
        if (!(object instanceof UserGroupSetting)) {
            return false;
        }
        UserGroupSetting other = (UserGroupSetting) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.UserGroupSetting[ id=" + id + " ]";
    }

    @Override
    public void setSettingEntity(SettingEntity settingEntity) {
        UserGroup setttingEntityUserGroup = (UserGroup) settingEntity;
        setUserGroup(setttingEntityUserGroup);
    }
    
}
