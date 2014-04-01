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
@Table(name = "component_source")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentSource.findAll", query = "SELECT c FROM ComponentSource c"),
    @NamedQuery(name = "ComponentSource.findById", query = "SELECT c FROM ComponentSource c WHERE c.id = :id"),
    @NamedQuery(name = "ComponentSource.findByPartNumber", query = "SELECT c FROM ComponentSource c WHERE c.partNumber = :partNumber"),
    @NamedQuery(name = "ComponentSource.findByCost", query = "SELECT c FROM ComponentSource c WHERE c.cost = :cost"),
    @NamedQuery(name = "ComponentSource.findAllByComponentId", query = "SELECT c FROM ComponentSource c WHERE c.component.id = :componentId")})
public class ComponentSource implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 64)
    @Column(name = "part_number")
    private String partNumber;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "cost")
    private Float cost;
    @JoinColumn(name = "source_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Source source;
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component component;

    public ComponentSource() {
    }

    public ComponentSource(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
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
        if (!(object instanceof ComponentSource)) {
            return false;
        }
        ComponentSource other = (ComponentSource) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.test.entities.ComponentSource[ id=" + id + " ]";
    }
    
}
