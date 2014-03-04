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
@Table(name = "aoi_document")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AoiDocument.findAll", query = "SELECT a FROM AoiDocument a"),
    @NamedQuery(name = "AoiDocument.findByDocId", query = "SELECT a FROM AoiDocument a WHERE a.docId = :docId"),
    @NamedQuery(name = "AoiDocument.findByUri", query = "SELECT a FROM AoiDocument a WHERE a.uri = :uri")})
public class AoiDocument implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "doc_id")
    private Integer docId;
    @Size(max = 255)
    @Column(name = "uri")
    private String uri;
    @JoinColumn(name = "doc_type_id", referencedColumnName = "doc_type_id")
    @ManyToOne
    private DocType docTypeId;
    @JoinColumn(name = "aoi_id", referencedColumnName = "aoi_id")
    @ManyToOne
    private Aoi aoiId;

    public AoiDocument() {
    }

    public AoiDocument(Integer docId) {
        this.docId = docId;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public DocType getDocTypeId() {
        return docTypeId;
    }

    public void setDocTypeId(DocType docTypeId) {
        this.docTypeId = docTypeId;
    }

    public Aoi getAoiId() {
        return aoiId;
    }

    public void setAoiId(Aoi aoiId) {
        this.aoiId = aoiId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (docId != null ? docId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AoiDocument)) {
            return false;
        }
        AoiDocument other = (AoiDocument) object;
        if ((this.docId == null && other.docId != null) || (this.docId != null && !this.docId.equals(other.docId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AoiDocument[ docId=" + docId + " ]";
    }
    
}
