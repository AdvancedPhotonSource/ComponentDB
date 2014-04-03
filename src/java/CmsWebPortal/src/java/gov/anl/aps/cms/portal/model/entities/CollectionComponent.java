/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
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
public class CollectionComponent implements Serializable
{

    private static final long serialVersionUID = 1L;

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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CollectionComponent)) {
            return false;
        }
        CollectionComponent other = (CollectionComponent) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.test.entities.CollectionComponent[ id=" + id + " ]";
    }

}
