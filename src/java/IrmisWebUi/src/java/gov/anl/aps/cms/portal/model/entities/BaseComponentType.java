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
@Table(name = "base_component_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "BaseComponentType.findAll", query = "SELECT b FROM BaseComponentType b"),
    @NamedQuery(name = "BaseComponentType.findByBaseComponentTypeId", query = "SELECT b FROM BaseComponentType b WHERE b.baseComponentTypeId = :baseComponentTypeId"),
    @NamedQuery(name = "BaseComponentType.findByComponentTypeName", query = "SELECT b FROM BaseComponentType b WHERE b.componentTypeName = :componentTypeName")})
public class BaseComponentType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "base_component_type_id")
    private Integer baseComponentTypeId;
    @Size(max = 60)
    @Column(name = "component_type_name")
    private String componentTypeName;

    public BaseComponentType() {
    }

    public BaseComponentType(Integer baseComponentTypeId) {
        this.baseComponentTypeId = baseComponentTypeId;
    }

    public Integer getBaseComponentTypeId() {
        return baseComponentTypeId;
    }

    public void setBaseComponentTypeId(Integer baseComponentTypeId) {
        this.baseComponentTypeId = baseComponentTypeId;
    }

    public String getComponentTypeName() {
        return componentTypeName;
    }

    public void setComponentTypeName(String componentTypeName) {
        this.componentTypeName = componentTypeName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (baseComponentTypeId != null ? baseComponentTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BaseComponentType)) {
            return false;
        }
        BaseComponentType other = (BaseComponentType) object;
        if ((this.baseComponentTypeId == null && other.baseComponentTypeId != null) || (this.baseComponentTypeId != null && !this.baseComponentTypeId.equals(other.baseComponentTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.BaseComponentType[ baseComponentTypeId=" + baseComponentTypeId + " ]";
    }
    
}
