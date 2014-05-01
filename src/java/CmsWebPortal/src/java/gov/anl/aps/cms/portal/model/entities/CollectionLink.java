/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
@Table(name = "collection_link")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CollectionLink.findAll", query = "SELECT c FROM CollectionLink c"),
    @NamedQuery(name = "CollectionLink.findById", query = "SELECT c FROM CollectionLink c WHERE c.id = :id"),
    @NamedQuery(name = "CollectionLink.findByTag", query = "SELECT c FROM CollectionLink c WHERE c.tag = :tag")})
public class CollectionLink extends CloneableEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Size(max = 256)
    @Column(name = "description")
    private String description;

    @Size(max = 64)
    @Column(name = "tag")
    private String tag;

    @JoinColumn(name = "child_collection_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Collection childCollection;

    @JoinColumn(name = "parent_collection_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Collection parentCollection;

    public CollectionLink() {
    }

    public CollectionLink(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Collection getChildCollection() {
        return childCollection;
    }

    public void setChildCollection(Collection childCollection) {
        this.childCollection = childCollection;
    }

    public Collection getParentCollection() {
        return parentCollection;
    }

    public void setParentCollection(Collection parentCollection) {
        this.parentCollection = parentCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByParentCollectionAndChilCollectionAndTag(CollectionLink other) {
        if (other == null) {
            return false;
        }

        if (!ObjectUtility.equals(this.parentCollection, other.parentCollection)) {
            return false;
        }

        if (!ObjectUtility.equals(this.childCollection, other.childCollection)) {
            return false;
        }

        return ObjectUtility.equals(this.tag, other.tag);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CollectionLink)) {
            return false;
        }
        CollectionLink other = (CollectionLink) object;
        if (this.id == null && other.id == null) {
            return equalsByParentCollectionAndChilCollectionAndTag(other);
        }

        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.test.entities.CollectionLink[ id=" + id + " ]";
    }

}
