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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "technical_system")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TechnicalSystem.findAll", query = "SELECT t FROM TechnicalSystem t"),
    @NamedQuery(name = "TechnicalSystem.findByTechnicalSystemId", query = "SELECT t FROM TechnicalSystem t WHERE t.technicalSystemId = :technicalSystemId"),
    @NamedQuery(name = "TechnicalSystem.findByTechnicalSystem", query = "SELECT t FROM TechnicalSystem t WHERE t.technicalSystem = :technicalSystem"),
    @NamedQuery(name = "TechnicalSystem.findByDescription", query = "SELECT t FROM TechnicalSystem t WHERE t.description = :description")})
public class TechnicalSystem implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "technical_system_id")
    private Integer technicalSystemId;
    @Size(max = 60)
    @Column(name = "technical_system")
    private String technicalSystem;
    @Size(max = 255)
    @Column(name = "description")
    private String description;

    public TechnicalSystem() {
    }

    public TechnicalSystem(Integer technicalSystemId) {
        this.technicalSystemId = technicalSystemId;
    }

    public Integer getTechnicalSystemId() {
        return technicalSystemId;
    }

    public void setTechnicalSystemId(Integer technicalSystemId) {
        this.technicalSystemId = technicalSystemId;
    }

    public String getTechnicalSystem() {
        return technicalSystem;
    }

    public void setTechnicalSystem(String technicalSystem) {
        this.technicalSystem = technicalSystem;
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
        hash += (technicalSystemId != null ? technicalSystemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TechnicalSystem)) {
            return false;
        }
        TechnicalSystem other = (TechnicalSystem) object;
        if ((this.technicalSystemId == null && other.technicalSystemId != null) || (this.technicalSystemId != null && !this.technicalSystemId.equals(other.technicalSystemId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.TechnicalSystem[ technicalSystemId=" + technicalSystemId + " ]";
    }
    
}
