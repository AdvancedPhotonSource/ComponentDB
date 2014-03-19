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
@Table(name = "function")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Function1.findAll", query = "SELECT f FROM Function1 f"),
    @NamedQuery(name = "Function1.findByFunctionId", query = "SELECT f FROM Function1 f WHERE f.functionId = :functionId"),
    @NamedQuery(name = "Function1.findByFunction", query = "SELECT f FROM Function1 f WHERE f.function = :function"),
    @NamedQuery(name = "Function1.findByVersion", query = "SELECT f FROM Function1 f WHERE f.version = :version"),
    @NamedQuery(name = "Function1.findByMarkForDelete", query = "SELECT f FROM Function1 f WHERE f.markForDelete = :markForDelete")})
public class Function1 implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "function_id")
    private Integer functionId;
    @Size(max = 100)
    @Column(name = "function")
    private String function;
    @Column(name = "version")
    private Integer version;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @OneToMany(mappedBy = "functionId")
    private List<ComponentTypeFunction> componentTypeFunctionList;

    public Function1() {
    }

    public Function1(Integer functionId) {
        this.functionId = functionId;
    }

    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getMarkForDelete() {
        return markForDelete;
    }

    public void setMarkForDelete(Boolean markForDelete) {
        this.markForDelete = markForDelete;
    }

    @XmlTransient
    public List<ComponentTypeFunction> getComponentTypeFunctionList() {
        return componentTypeFunctionList;
    }

    public void setComponentTypeFunctionList(List<ComponentTypeFunction> componentTypeFunctionList) {
        this.componentTypeFunctionList = componentTypeFunctionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (functionId != null ? functionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Function1)) {
            return false;
        }
        Function1 other = (Function1) object;
        if ((this.functionId == null && other.functionId != null) || (this.functionId != null && !this.functionId.equals(other.functionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.Function1[ functionId=" + functionId + " ]";
    }
    
}
