/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
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
@Cacheable(true)
@Table(name = "user_setting")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserSetting.findAll", query = "SELECT u FROM UserSetting u"),
    @NamedQuery(name = "UserSetting.findById", query = "SELECT u FROM UserSetting u WHERE u.id = :id"),
    @NamedQuery(name = "UserSetting.findByValue", query = "SELECT u FROM UserSetting u WHERE u.value = :value")})
@JsonIgnoreProperties({
    "user"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSetting extends EntitySetting implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 64)
    private String value;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserInfo user;
    @JoinColumn(name = "setting_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private SettingType settingType;

    public UserSetting() {
    }

    public UserSetting(Integer id) {
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

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
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
        if (!(object instanceof UserSetting)) {
            return false;
        }
        UserSetting other = (UserSetting) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.UserSetting[ id=" + id + " ]";
    }

    @Override
    public void setSettingEntity(SettingEntity settingEntity) {
        UserInfo settingEntityUser = (UserInfo) settingEntity; 
        setUser(settingEntityUser);
    }
    
}
