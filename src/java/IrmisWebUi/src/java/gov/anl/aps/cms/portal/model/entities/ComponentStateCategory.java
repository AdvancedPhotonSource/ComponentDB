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
@Table(name = "component_state_category")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentStateCategory.findAll", query = "SELECT c FROM ComponentStateCategory c"),
    @NamedQuery(name = "ComponentStateCategory.findByComponentStateCategoryId", query = "SELECT c FROM ComponentStateCategory c WHERE c.componentStateCategoryId = :componentStateCategoryId"),
    @NamedQuery(name = "ComponentStateCategory.findByCategory", query = "SELECT c FROM ComponentStateCategory c WHERE c.category = :category")})
public class ComponentStateCategory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_state_category_id")
    private Integer componentStateCategoryId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "category")
    private String category;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentStateCategoryId")
    private List<ComponentState> componentStateList;

    public ComponentStateCategory() {
    }

    public ComponentStateCategory(Integer componentStateCategoryId) {
        this.componentStateCategoryId = componentStateCategoryId;
    }

    public ComponentStateCategory(Integer componentStateCategoryId, String category) {
        this.componentStateCategoryId = componentStateCategoryId;
        this.category = category;
    }

    public Integer getComponentStateCategoryId() {
        return componentStateCategoryId;
    }

    public void setComponentStateCategoryId(Integer componentStateCategoryId) {
        this.componentStateCategoryId = componentStateCategoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @XmlTransient
    public List<ComponentState> getComponentStateList() {
        return componentStateList;
    }

    public void setComponentStateList(List<ComponentState> componentStateList) {
        this.componentStateList = componentStateList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentStateCategoryId != null ? componentStateCategoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentStateCategory)) {
            return false;
        }
        ComponentStateCategory other = (ComponentStateCategory) object;
        if ((this.componentStateCategoryId == null && other.componentStateCategoryId != null) || (this.componentStateCategoryId != null && !this.componentStateCategoryId.equals(other.componentStateCategoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentStateCategory[ componentStateCategoryId=" + componentStateCategoryId + " ]";
    }
    
}
