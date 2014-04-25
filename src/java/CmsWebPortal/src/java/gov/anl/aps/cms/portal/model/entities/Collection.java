package gov.anl.aps.cms.portal.model.entities;

import java.util.List;
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
public class Collection extends CloneableEntity
{
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
    
    @JoinTable(name = "collection_log", joinColumns = {
        @JoinColumn(name = "collection_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Log> logList;
  
    @JoinTable(name = "collection_link", joinColumns = {
        @JoinColumn(name = "parent_collection_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "child_collection_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Collection> childCollectionList;
    
    @ManyToMany(mappedBy = "childCollectionList")
    private List<Collection> parentCollectionList;
    
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private EntityInfo entityInfo;
    
    @JoinColumn(name = "parent_collection_id", referencedColumnName = "id")
    @ManyToOne
    private Collection parentCollection;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "collection")
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
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    @XmlTransient
    public List<Collection> getChildCollectionList() {
        return childCollectionList;
    }

    public void setChildCollectionList(List<Collection> childCollectionList) {
        this.childCollectionList = childCollectionList;
    }

    @XmlTransient
    public List<Collection> getParentCollectionList() {
        return parentCollectionList;
    }

    public void setParentCollectionList(List<Collection> parentCollectionList) {
        this.parentCollectionList = parentCollectionList;
    }
    
    public Collection getParentCollection() {
        return parentCollection;
    }

    public void setParentCollection(Collection parentCollection) {
        this.parentCollection = parentCollection;
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
        return "gov.anl.aps.cms.test.entities.Collection[ id=" + id + " ]";
    }

    @Override
    public Collection clone() throws CloneNotSupportedException {
        Collection cloned = (Collection) super.clone();
        cloned.id = null;
        cloned.name = "Copy Of " + cloned.name;
        cloned.childCollectionList = null;
        cloned.parentCollectionList = null;
        for (CollectionComponent collectionComponent : cloned.collectionComponentList) {
            collectionComponent.setId(null);
            collectionComponent.setCollection(cloned);
        }
        cloned.entityInfo = null;
        return cloned;
    }    
}
