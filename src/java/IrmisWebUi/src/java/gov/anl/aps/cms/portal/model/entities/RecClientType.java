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
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "rec_client_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RecClientType.findAll", query = "SELECT r FROM RecClientType r"),
    @NamedQuery(name = "RecClientType.findByRecClientTypeId", query = "SELECT r FROM RecClientType r WHERE r.recClientTypeId = :recClientTypeId"),
    @NamedQuery(name = "RecClientType.findByDescription", query = "SELECT r FROM RecClientType r WHERE r.description = :description")})
public class RecClientType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "rec_client_type_id")
    private Integer recClientTypeId;
    @Size(max = 100)
    @Column(name = "description")
    private String description;

    public RecClientType() {
    }

    public RecClientType(Integer recClientTypeId) {
        this.recClientTypeId = recClientTypeId;
    }

    public Integer getRecClientTypeId() {
        return recClientTypeId;
    }

    public void setRecClientTypeId(Integer recClientTypeId) {
        this.recClientTypeId = recClientTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recClientTypeId != null ? recClientTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RecClientType)) {
            return false;
        }
        RecClientType other = (RecClientType) object;
        if ((this.recClientTypeId == null && other.recClientTypeId != null) || (this.recClientTypeId != null && !this.recClientTypeId.equals(other.recClientTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.RecClientType[ recClientTypeId=" + recClientTypeId + " ]";
    }
    
}
