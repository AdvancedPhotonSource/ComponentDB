/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "log")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Log.findAll", query = "SELECT l FROM Log l"),
    @NamedQuery(name = "Log.findById", query = "SELECT l FROM Log l WHERE l.id = :id"),
    @NamedQuery(name = "Log.findByCreatedOnDateTime", query = "SELECT l FROM Log l WHERE l.createdOnDateTime = :createdOnDateTime")})
public class Log implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "text")
    private String text;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_on_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOnDateTime;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logId")
    private List<CollectionLog> collectionLogList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logId")
    private List<ComponentInstanceLog> componentInstanceLogList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logId")
    private List<DesignComponentLog> designComponentLogList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logId")
    private List<ComponentLog> componentLogList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "logId")
    private List<DesignLog> designLogList;
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User createdByUserId;

    public Log() {
    }

    public Log(Integer id) {
        this.id = id;
    }

    public Log(Integer id, String text, Date createdOnDateTime) {
        this.id = id;
        this.text = text;
        this.createdOnDateTime = createdOnDateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedOnDateTime() {
        return createdOnDateTime;
    }

    public void setCreatedOnDateTime(Date createdOnDateTime) {
        this.createdOnDateTime = createdOnDateTime;
    }

    @XmlTransient
    public List<CollectionLog> getCollectionLogList() {
        return collectionLogList;
    }

    public void setCollectionLogList(List<CollectionLog> collectionLogList) {
        this.collectionLogList = collectionLogList;
    }

    @XmlTransient
    public List<ComponentInstanceLog> getComponentInstanceLogList() {
        return componentInstanceLogList;
    }

    public void setComponentInstanceLogList(List<ComponentInstanceLog> componentInstanceLogList) {
        this.componentInstanceLogList = componentInstanceLogList;
    }

    @XmlTransient
    public List<DesignComponentLog> getDesignComponentLogList() {
        return designComponentLogList;
    }

    public void setDesignComponentLogList(List<DesignComponentLog> designComponentLogList) {
        this.designComponentLogList = designComponentLogList;
    }

    @XmlTransient
    public List<ComponentLog> getComponentLogList() {
        return componentLogList;
    }

    public void setComponentLogList(List<ComponentLog> componentLogList) {
        this.componentLogList = componentLogList;
    }

    @XmlTransient
    public List<DesignLog> getDesignLogList() {
        return designLogList;
    }

    public void setDesignLogList(List<DesignLog> designLogList) {
        this.designLogList = designLogList;
    }

    public User getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(User createdByUserId) {
        this.createdByUserId = createdByUserId;
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
        if (!(object instanceof Log)) {
            return false;
        }
        Log other = (Log) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.portal.model.entities.Log[ id=" + id + " ]";
    }
    
}
