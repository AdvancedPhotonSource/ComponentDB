/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "component_state")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentState.findAll", query = "SELECT c FROM ComponentState c"),
    @NamedQuery(name = "ComponentState.findByComponentStateId", query = "SELECT c FROM ComponentState c WHERE c.componentStateId = :componentStateId"),
    @NamedQuery(name = "ComponentState.findByState", query = "SELECT c FROM ComponentState c WHERE c.state = :state")})
public class ComponentState implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_state_id")
    private Integer componentStateId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "state")
    private String state;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentStateId")
    private List<ComponentInstanceState> componentInstanceStateList;
    @JoinColumn(name = "component_state_category_id", referencedColumnName = "component_state_category_id")
    @ManyToOne(optional = false)
    private ComponentStateCategory componentStateCategoryId;

    public ComponentState() {
    }

    public ComponentState(Integer componentStateId) {
        this.componentStateId = componentStateId;
    }

    public ComponentState(Integer componentStateId, String state) {
        this.componentStateId = componentStateId;
        this.state = state;
    }

    public Integer getComponentStateId() {
        return componentStateId;
    }

    public void setComponentStateId(Integer componentStateId) {
        this.componentStateId = componentStateId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @XmlTransient
    public List<ComponentInstanceState> getComponentInstanceStateList() {
        return componentInstanceStateList;
    }

    public void setComponentInstanceStateList(List<ComponentInstanceState> componentInstanceStateList) {
        this.componentInstanceStateList = componentInstanceStateList;
    }

    public ComponentStateCategory getComponentStateCategoryId() {
        return componentStateCategoryId;
    }

    public void setComponentStateCategoryId(ComponentStateCategory componentStateCategoryId) {
        this.componentStateCategoryId = componentStateCategoryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentStateId != null ? componentStateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentState)) {
            return false;
        }
        ComponentState other = (ComponentState) object;
        if ((this.componentStateId == null && other.componentStateId != null) || (this.componentStateId != null && !this.componentStateId.equals(other.componentStateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentState[ componentStateId=" + componentStateId + " ]";
    }
    
}
