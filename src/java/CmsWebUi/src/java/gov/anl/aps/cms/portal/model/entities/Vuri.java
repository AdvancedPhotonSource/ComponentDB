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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "vuri")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Vuri.findAll", query = "SELECT v FROM Vuri v"),
    @NamedQuery(name = "Vuri.findByVuriId", query = "SELECT v FROM Vuri v WHERE v.vuriId = :vuriId"),
    @NamedQuery(name = "Vuri.findByUriId", query = "SELECT v FROM Vuri v WHERE v.uriId = :uriId")})
public class Vuri implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "vuri_id")
    private Integer vuriId;
    @Column(name = "uri_id")
    private Integer uriId;

    public Vuri() {
    }

    public Vuri(Integer vuriId) {
        this.vuriId = vuriId;
    }

    public Integer getVuriId() {
        return vuriId;
    }

    public void setVuriId(Integer vuriId) {
        this.vuriId = vuriId;
    }

    public Integer getUriId() {
        return uriId;
    }

    public void setUriId(Integer uriId) {
        this.uriId = uriId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vuriId != null ? vuriId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Vuri)) {
            return false;
        }
        Vuri other = (Vuri) object;
        if ((this.vuriId == null && other.vuriId != null) || (this.vuriId != null && !this.vuriId.equals(other.vuriId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.Vuri[ vuriId=" + vuriId + " ]";
    }
    
}
