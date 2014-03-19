/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "doc_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DocType.findAll", query = "SELECT d FROM DocType d"),
    @NamedQuery(name = "DocType.findByDocTypeId", query = "SELECT d FROM DocType d WHERE d.docTypeId = :docTypeId"),
    @NamedQuery(name = "DocType.findByDocType", query = "SELECT d FROM DocType d WHERE d.docType = :docType")})
public class DocType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "doc_type_id")
    private Integer docTypeId;
    @Size(max = 40)
    @Column(name = "doc_type")
    private String docType;
    @OneToMany(mappedBy = "docTypeId")
    private List<AoiDocument> aoiDocumentList;

    public DocType() {
    }

    public DocType(Integer docTypeId) {
        this.docTypeId = docTypeId;
    }

    public Integer getDocTypeId() {
        return docTypeId;
    }

    public void setDocTypeId(Integer docTypeId) {
        this.docTypeId = docTypeId;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    @XmlTransient
    public List<AoiDocument> getAoiDocumentList() {
        return aoiDocumentList;
    }

    public void setAoiDocumentList(List<AoiDocument> aoiDocumentList) {
        this.aoiDocumentList = aoiDocumentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (docTypeId != null ? docTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DocType)) {
            return false;
        }
        DocType other = (DocType) object;
        if ((this.docTypeId == null && other.docTypeId != null) || (this.docTypeId != null && !this.docTypeId.equals(other.docTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.DocType[ docTypeId=" + docTypeId + " ]";
    }
    
}
