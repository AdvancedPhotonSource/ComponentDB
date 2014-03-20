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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "collection_log")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CollectionLog.findAll", query = "SELECT c FROM CollectionLog c"),
    @NamedQuery(name = "CollectionLog.findById", query = "SELECT c FROM CollectionLog c WHERE c.id = :id")})
public class CollectionLog implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "log_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Log logId;
    @JoinColumn(name = "collection_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Collection collectionId;

    public CollectionLog() {
    }

    public CollectionLog(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Log getLogId() {
        return logId;
    }

    public void setLogId(Log logId) {
        this.logId = logId;
    }

    public Collection getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Collection collectionId) {
        this.collectionId = collectionId;
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
        if (!(object instanceof CollectionLog)) {
            return false;
        }
        CollectionLog other = (CollectionLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.portal.model.entities.CollectionLog[ id=" + id + " ]";
    }
    
}
