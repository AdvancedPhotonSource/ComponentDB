/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.entities;

import java.io.Serializable;
import javax.persistence.Basic;
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
@Table(name = "design_link")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DesignLink.findAll", query = "SELECT d FROM DesignLink d"),
    @NamedQuery(name = "DesignLink.findById", query = "SELECT d FROM DesignLink d WHERE d.id = :id"),
    @NamedQuery(name = "DesignLink.findByLabel", query = "SELECT d FROM DesignLink d WHERE d.label = :label"),
    @NamedQuery(name = "DesignLink.findByDescription", query = "SELECT d FROM DesignLink d WHERE d.description = :description")})
public class DesignLink implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 64)
    private String label;
    @Size(max = 256)
    private String description;
    @JoinColumn(name = "child_design_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Design childDesignId;
    @JoinColumn(name = "parent_design_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Design parentDesignId;

    public DesignLink() {
    }

    public DesignLink(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Design getChildDesignId() {
        return childDesignId;
    }

    public void setChildDesignId(Design childDesignId) {
        this.childDesignId = childDesignId;
    }

    public Design getParentDesignId() {
        return parentDesignId;
    }

    public void setParentDesignId(Design parentDesignId) {
        this.parentDesignId = parentDesignId;
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
        if (!(object instanceof DesignLink)) {
            return false;
        }
        DesignLink other = (DesignLink) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.entities.DesignLink[ id=" + id + " ]";
    }
    
}
