/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@Cacheable(true)
@Table(name = "setting_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SettingType.findAll", query = "SELECT s FROM SettingType s"),
    @NamedQuery(name = "SettingType.findById", query = "SELECT s FROM SettingType s WHERE s.id = :id"),
    @NamedQuery(name = "SettingType.findByName", query = "SELECT s FROM SettingType s WHERE s.name = :name"),
    @NamedQuery(name = "SettingType.findByDescription", query = "SELECT s FROM SettingType s WHERE s.description = :description"),
    @NamedQuery(name = "SettingType.findByDefaultValue", query = "SELECT s FROM SettingType s WHERE s.defaultValue = :defaultValue")})
public class SettingType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    private String name;
    @Size(max = 256)
    private String description;
    @Size(max = 64)
    @Column(name = "default_value")
    private String defaultValue;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "settingType")
    private List<UserSetting> userSettingList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "settingType")
    private List<UserGroupSetting> userGroupSettingList;

    public SettingType() {
    }

    public SettingType(Integer id) {
        this.id = id;
    }

    public SettingType(Integer id, String name) {
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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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
    public List<UserGroupSetting> getUserGroupSettingList() {
        return userGroupSettingList;
    }

    public void setUserGroupSettingList(List<UserGroupSetting> userGroupSettingList) {
        this.userGroupSettingList = userGroupSettingList;
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
        if (!(object instanceof SettingType)) {
            return false;
        }
        SettingType other = (SettingType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.SettingType[ id=" + id + " ]";
    }
    
}
