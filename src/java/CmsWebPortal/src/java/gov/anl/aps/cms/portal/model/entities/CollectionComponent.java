
package gov.anl.aps.cms.portal.model.entities;

import gov.anl.aps.cms.portal.utilities.ObjectUtility;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "collection_component")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CollectionComponent.findAll", query = "SELECT c FROM CollectionComponent c"),
    @NamedQuery(name = "CollectionComponent.findById", query = "SELECT c FROM CollectionComponent c WHERE c.id = :id"),
    @NamedQuery(name = "CollectionComponent.findByQuantity", query = "SELECT c FROM CollectionComponent c WHERE c.quantity = :quantity"),
    @NamedQuery(name = "CollectionComponent.findByDescription", query = "SELECT c FROM CollectionComponent c WHERE c.description = :description")})
public class CollectionComponent extends CloneableEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "quantity")
    private Integer quantity = 1;

    @Size(max = 256)
    @Column(name = "description")
    private String description;

    @Size(max = 64)
    @Column(name = "tag")
    private String tag;

    @Column(name = "priority")
    private Float priority;

    @JoinColumn(name = "component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component component;

    @JoinColumn(name = "collection_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Collection collection;

    public CollectionComponent() {
    }

    public CollectionComponent(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Float getPriority() {
        return priority;
    }

    public void setPriority(Float priority) {
        this.priority = priority;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByComponentAndTag(CollectionComponent other) {
        if (other == null) {
            return false;
        }
        
        if (!ObjectUtility.equals(this.component, other.component)) {
            return false;
        }
        
        return ObjectUtility.equals(this.tag, other.tag);
    }
        
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CollectionComponent)) {
            return false;
        }
        CollectionComponent other = (CollectionComponent) object;
        if (this.id == null && other.id == null) {
            return equalsByComponentAndTag(other);
        }
        
        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);        
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.test.entities.CollectionComponent[ id=" + id + " ]";
    }

}
