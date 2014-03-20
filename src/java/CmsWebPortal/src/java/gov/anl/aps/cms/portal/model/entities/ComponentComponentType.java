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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "component_component_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentComponentType.findAll", query = "SELECT c FROM ComponentComponentType c"),
    @NamedQuery(name = "ComponentComponentType.findById", query = "SELECT c FROM ComponentComponentType c WHERE c.id = :id")})
public class ComponentComponentType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "component_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentType componentTypeId;
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component componentId;

    public ComponentComponentType() {
    }

    public ComponentComponentType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ComponentType getComponentTypeId() {
        return componentTypeId;
    }

    public void setComponentTypeId(ComponentType componentTypeId) {
        this.componentTypeId = componentTypeId;
    }

    public Component getComponentId() {
        return componentId;
    }

    public void setComponentId(Component componentId) {
        this.componentId = componentId;
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
        if (!(object instanceof ComponentComponentType)) {
            return false;
        }
        ComponentComponentType other = (ComponentComponentType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.portal.model.entities.ComponentComponentType[ id=" + id + " ]";
    }
    
}
