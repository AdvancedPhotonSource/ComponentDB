/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "collection")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Collection.findAll", query = "SELECT c FROM Collection c"),
    @NamedQuery(name = "Collection.findById", query = "SELECT c FROM Collection c WHERE c.id = :id"),
    @NamedQuery(name = "Collection.findByName", query = "SELECT c FROM Collection c WHERE c.name = :name"),
    @NamedQuery(name = "Collection.findByDescription", query = "SELECT c FROM Collection c WHERE c.description = :description")})
public class Collection implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Size(max = 256)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "collectionId")
    private List<CollectionLog> collectionLogList;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EntityInfo entityInfoId;
    @OneToMany(mappedBy = "parentCollectionId")
    private List<Collection> collectionList;
    @JoinColumn(name = "parent_collection_id", referencedColumnName = "id")
    @ManyToOne
    private Collection parentCollectionId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "collectionId")
    private List<CollectionComponent> collectionComponentList;

    public Collection() {
    }

    public Collection(Integer id) {
        this.id = id;
    }

    public Collection(Integer id, String name) {
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
    public List<CollectionLog> getCollectionLogList() {
        return collectionLogList;
    }

    public void setCollectionLogList(List<CollectionLog> collectionLogList) {
        this.collectionLogList = collectionLogList;
    }

    public EntityInfo getEntityInfoId() {
        return entityInfoId;
    }

    public void setEntityInfoId(EntityInfo entityInfoId) {
        this.entityInfoId = entityInfoId;
    }

    @XmlTransient
    public List<Collection> getCollectionList() {
        return collectionList;
    }

    public void setCollectionList(List<Collection> collectionList) {
        this.collectionList = collectionList;
    }

    public Collection getParentCollectionId() {
        return parentCollectionId;
    }

    public void setParentCollectionId(Collection parentCollectionId) {
        this.parentCollectionId = parentCollectionId;
    }

    @XmlTransient
    public List<CollectionComponent> getCollectionComponentList() {
        return collectionComponentList;
    }

    public void setCollectionComponentList(List<CollectionComponent> collectionComponentList) {
        this.collectionComponentList = collectionComponentList;
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
        if (!(object instanceof Collection)) {
            return false;
        }
        Collection other = (Collection) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.portal.model.entities.Collection[ id=" + id + " ]";
    }
    
}
