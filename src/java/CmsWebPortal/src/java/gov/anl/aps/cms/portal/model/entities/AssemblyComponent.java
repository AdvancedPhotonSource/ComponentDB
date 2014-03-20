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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "assembly_component")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AssemblyComponent.findAll", query = "SELECT a FROM AssemblyComponent a"),
    @NamedQuery(name = "AssemblyComponent.findById", query = "SELECT a FROM AssemblyComponent a WHERE a.id = :id"),
    @NamedQuery(name = "AssemblyComponent.findByQuantity", query = "SELECT a FROM AssemblyComponent a WHERE a.quantity = :quantity")})
public class AssemblyComponent implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "quantity")
    private Integer quantity;
    @OneToMany(mappedBy = "linkAssemblyComponentId")
    private List<AssemblyComponentConnection> assemblyComponentConnectionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "secondAssemblyComponentId")
    private List<AssemblyComponentConnection> assemblyComponentConnectionList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firstAssemblyComponentId")
    private List<AssemblyComponentConnection> assemblyComponentConnectionList2;
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component componentId;
    @JoinColumn(name = "assembly_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component assemblyId;

    public AssemblyComponent() {
    }

    public AssemblyComponent(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @XmlTransient
    public List<AssemblyComponentConnection> getAssemblyComponentConnectionList() {
        return assemblyComponentConnectionList;
    }

    public void setAssemblyComponentConnectionList(List<AssemblyComponentConnection> assemblyComponentConnectionList) {
        this.assemblyComponentConnectionList = assemblyComponentConnectionList;
    }

    @XmlTransient
    public List<AssemblyComponentConnection> getAssemblyComponentConnectionList1() {
        return assemblyComponentConnectionList1;
    }

    public void setAssemblyComponentConnectionList1(List<AssemblyComponentConnection> assemblyComponentConnectionList1) {
        this.assemblyComponentConnectionList1 = assemblyComponentConnectionList1;
    }

    @XmlTransient
    public List<AssemblyComponentConnection> getAssemblyComponentConnectionList2() {
        return assemblyComponentConnectionList2;
    }

    public void setAssemblyComponentConnectionList2(List<AssemblyComponentConnection> assemblyComponentConnectionList2) {
        this.assemblyComponentConnectionList2 = assemblyComponentConnectionList2;
    }

    public Component getComponentId() {
        return componentId;
    }

    public void setComponentId(Component componentId) {
        this.componentId = componentId;
    }

    public Component getAssemblyId() {
        return assemblyId;
    }

    public void setAssemblyId(Component assemblyId) {
        this.assemblyId = assemblyId;
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
        if (!(object instanceof AssemblyComponent)) {
            return false;
        }
        AssemblyComponent other = (AssemblyComponent) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.portal.model.entities.AssemblyComponent[ id=" + id + " ]";
    }
    
}
