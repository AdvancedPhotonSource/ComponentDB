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
@Table(name = "component_type_document")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentTypeDocument.findAll", query = "SELECT c FROM ComponentTypeDocument c"),
    @NamedQuery(name = "ComponentTypeDocument.findByComponentTypeDocumentId", query = "SELECT c FROM ComponentTypeDocument c WHERE c.componentTypeDocumentId = :componentTypeDocumentId"),
    @NamedQuery(name = "ComponentTypeDocument.findByDocumentType", query = "SELECT c FROM ComponentTypeDocument c WHERE c.documentType = :documentType"),
    @NamedQuery(name = "ComponentTypeDocument.findByMarkForDelete", query = "SELECT c FROM ComponentTypeDocument c WHERE c.markForDelete = :markForDelete"),
    @NamedQuery(name = "ComponentTypeDocument.findByVersion", query = "SELECT c FROM ComponentTypeDocument c WHERE c.version = :version")})
public class ComponentTypeDocument implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_type_document_id")
    private Integer componentTypeDocumentId;
    @Size(max = 100)
    @Column(name = "document_type")
    private String documentType;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @JoinColumn(name = "uri_id", referencedColumnName = "uri_id")
    @ManyToOne
    private Uri uriId;
    @JoinColumn(name = "component_type_id", referencedColumnName = "component_type_id")
    @ManyToOne
    private ComponentType componentTypeId;

    public ComponentTypeDocument() {
    }

    public ComponentTypeDocument(Integer componentTypeDocumentId) {
        this.componentTypeDocumentId = componentTypeDocumentId;
    }

    public Integer getComponentTypeDocumentId() {
        return componentTypeDocumentId;
    }

    public void setComponentTypeDocumentId(Integer componentTypeDocumentId) {
        this.componentTypeDocumentId = componentTypeDocumentId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Boolean getMarkForDelete() {
        return markForDelete;
    }

    public void setMarkForDelete(Boolean markForDelete) {
        this.markForDelete = markForDelete;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Uri getUriId() {
        return uriId;
    }

    public void setUriId(Uri uriId) {
        this.uriId = uriId;
    }

    public ComponentType getComponentTypeId() {
        return componentTypeId;
    }

    public void setComponentTypeId(ComponentType componentTypeId) {
        this.componentTypeId = componentTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentTypeDocumentId != null ? componentTypeDocumentId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentTypeDocument)) {
            return false;
        }
        ComponentTypeDocument other = (ComponentTypeDocument) object;
        if ((this.componentTypeDocumentId == null && other.componentTypeDocumentId != null) || (this.componentTypeDocumentId != null && !this.componentTypeDocumentId.equals(other.componentTypeDocumentId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentTypeDocument[ componentTypeDocumentId=" + componentTypeDocumentId + " ]";
    }
    
}
