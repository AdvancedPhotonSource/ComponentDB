/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
    @NamedQuery(name = "List.findAll", query = "SELECT l FROM List l"),
    @NamedQuery(name = "List.findById", query = "SELECT l FROM List l WHERE l.id = :id"),
    @NamedQuery(name = "List.findByName", query = "SELECT l FROM List l WHERE l.name = :name"),
    @NamedQuery(name = "List.findByDescription", query = "SELECT l FROM List l WHERE l.description = :description")})
public class List implements Serializable {

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
    @ManyToMany(mappedBy = "listList")
    private java.util.List<ItemElement> itemElementList;
    @ManyToMany(mappedBy = "listList")
    private java.util.List<UserInfo> userInfoList;
    @ManyToMany(mappedBy = "listList")
    private java.util.List<UserGroup> userGroupList;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @OneToOne(optional = false)
    private EntityInfo entityInfo;

    public List() {
    }

    public List(Integer id) {
        this.id = id;
    }

    public List(Integer id, String name) {
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
        if (!(object instanceof List)) {
            return false;
        }
        List other = (List) object;
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
