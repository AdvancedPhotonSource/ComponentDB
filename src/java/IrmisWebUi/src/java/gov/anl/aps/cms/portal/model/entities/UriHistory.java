/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "uri_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UriHistory.findAll", query = "SELECT u FROM UriHistory u"),
    @NamedQuery(name = "UriHistory.findByUriHistoryId", query = "SELECT u FROM UriHistory u WHERE u.uriHistoryId = :uriHistoryId"),
    @NamedQuery(name = "UriHistory.findByUri", query = "SELECT u FROM UriHistory u WHERE u.uri = :uri"),
    @NamedQuery(name = "UriHistory.findByUriModifiedDate", query = "SELECT u FROM UriHistory u WHERE u.uriModifiedDate = :uriModifiedDate"),
    @NamedQuery(name = "UriHistory.findByModifiedDate", query = "SELECT u FROM UriHistory u WHERE u.modifiedDate = :modifiedDate"),
    @NamedQuery(name = "UriHistory.findByModifiedBy", query = "SELECT u FROM UriHistory u WHERE u.modifiedBy = :modifiedBy")})
public class UriHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "uri_history_id")
    private Integer uriHistoryId;
    @Size(max = 255)
    @Column(name = "uri")
    private String uri;
    @Basic(optional = false)
    @NotNull
    @Column(name = "uri_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uriModifiedDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    @Size(max = 60)
    @Column(name = "modified_by")
    private String modifiedBy;

    public UriHistory() {
    }

    public UriHistory(Integer uriHistoryId) {
        this.uriHistoryId = uriHistoryId;
    }

    public UriHistory(Integer uriHistoryId, Date uriModifiedDate, Date modifiedDate) {
        this.uriHistoryId = uriHistoryId;
        this.uriModifiedDate = uriModifiedDate;
        this.modifiedDate = modifiedDate;
    }

    public Integer getUriHistoryId() {
        return uriHistoryId;
    }

    public void setUriHistoryId(Integer uriHistoryId) {
        this.uriHistoryId = uriHistoryId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Date getUriModifiedDate() {
        return uriModifiedDate;
    }

    public void setUriModifiedDate(Date uriModifiedDate) {
        this.uriModifiedDate = uriModifiedDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uriHistoryId != null ? uriHistoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UriHistory)) {
            return false;
        }
        UriHistory other = (UriHistory) object;
        if ((this.uriHistoryId == null && other.uriHistoryId != null) || (this.uriHistoryId != null && !this.uriHistoryId.equals(other.uriHistoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.UriHistory[ uriHistoryId=" + uriHistoryId + " ]";
    }
    
}
