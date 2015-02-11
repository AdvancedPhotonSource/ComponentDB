/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;
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
import javax.validation.constraints.Size;
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
    @NamedQuery(name = "AssemblyComponent.findByDescription", query = "SELECT a FROM AssemblyComponent a WHERE a.description = :description"),
    @NamedQuery(name = "AssemblyComponent.findBySortOrder", query = "SELECT a FROM AssemblyComponent a WHERE a.sortOrder = :sortOrder")})
public class AssemblyComponent extends CdbEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 256)
    private String description;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "sort_order")
    private Float sortOrder;
    @OneToMany(mappedBy = "linkAssemblyComponentId")
    private List<AssemblyComponentConnection> assemblyComponentConnectionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "secondAssemblyComponentId")
    private List<AssemblyComponentConnection> assemblyComponentConnectionList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firstAssemblyComponentId")
    private List<AssemblyComponentConnection> assemblyComponentConnectionList2;
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component component;
    @JoinColumn(name = "assembly_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component assembly;

    public AssemblyComponent() {
    }

    public AssemblyComponent(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Float sortOrder) {
        this.sortOrder = sortOrder;
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

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Component getAssembly() {
        return assembly;
    }

    public void setAssembly(Component assembly) {
        this.assembly = assembly;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByAssemblyAndComponent(AssemblyComponent other) {
        if (other != null) {
            return (ObjectUtility.equals(this.assembly, other.assembly)
                    && ObjectUtility.equals(this.component, other.component));
        }
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AssemblyComponent)) {
            return false;
        }
        AssemblyComponent other = (AssemblyComponent) object;
        if (this.id == null && other.id == null) {
            return equalsByAssemblyAndComponent(other);
        }

        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }    

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.entities.AssemblyComponent[ id=" + id + " ]";
    }

    
    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(id, component.getName());
        searchResult.doesValueContainPattern("description", description, searchPattern);
        return searchResult;
    }
}
