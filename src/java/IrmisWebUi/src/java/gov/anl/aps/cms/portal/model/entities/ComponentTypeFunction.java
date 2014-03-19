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
@Table(name = "component_type_function")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentTypeFunction.findAll", query = "SELECT c FROM ComponentTypeFunction c"),
    @NamedQuery(name = "ComponentTypeFunction.findByComponentTypeFunctionId", query = "SELECT c FROM ComponentTypeFunction c WHERE c.componentTypeFunctionId = :componentTypeFunctionId"),
    @NamedQuery(name = "ComponentTypeFunction.findByMarkForDelete", query = "SELECT c FROM ComponentTypeFunction c WHERE c.markForDelete = :markForDelete"),
    @NamedQuery(name = "ComponentTypeFunction.findByVersion", query = "SELECT c FROM ComponentTypeFunction c WHERE c.version = :version")})
public class ComponentTypeFunction implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_type_function_id")
    private Integer componentTypeFunctionId;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @JoinColumn(name = "function_id", referencedColumnName = "function_id")
    @ManyToOne
    private Function1 functionId;
    @JoinColumn(name = "component_type_id", referencedColumnName = "component_type_id")
    @ManyToOne
    private ComponentType componentTypeId;

    public ComponentTypeFunction() {
    }

    public ComponentTypeFunction(Integer componentTypeFunctionId) {
        this.componentTypeFunctionId = componentTypeFunctionId;
    }

    public Integer getComponentTypeFunctionId() {
        return componentTypeFunctionId;
    }

    public void setComponentTypeFunctionId(Integer componentTypeFunctionId) {
        this.componentTypeFunctionId = componentTypeFunctionId;
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

    public Function1 getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Function1 functionId) {
        this.functionId = functionId;
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
        hash += (componentTypeFunctionId != null ? componentTypeFunctionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentTypeFunction)) {
            return false;
        }
        ComponentTypeFunction other = (ComponentTypeFunction) object;
        if ((this.componentTypeFunctionId == null && other.componentTypeFunctionId != null) || (this.componentTypeFunctionId != null && !this.componentTypeFunctionId.equals(other.componentTypeFunctionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentTypeFunction[ componentTypeFunctionId=" + componentTypeFunctionId + " ]";
    }
    
}
