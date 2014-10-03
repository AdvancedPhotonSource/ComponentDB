/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Component.findAll", query = "SELECT c FROM Component c"),
    @NamedQuery(name = "Component.findById", query = "SELECT c FROM Component c WHERE c.id = :id"),
    @NamedQuery(name = "Component.findByName", query = "SELECT c FROM Component c WHERE c.name = :name"),
    @NamedQuery(name = "Component.findByDescription", query = "SELECT c FROM Component c WHERE c.description = :description")})
public class Component implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    private String name;
    @Size(max = 256)
    private String description;
    @JoinTable(name = "component_log", joinColumns = {
        @JoinColumn(name = "component_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Log> logList;
    @OneToMany(mappedBy = "linkComponentId")
    private List<DesignElementConnection> designElementConnectionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<ComponentConnector> componentConnectorList;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EntityInfo entityInfoId;
    @JoinColumn(name = "component_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentType componentTypeId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<ComponentInstance> componentInstanceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<AssemblyComponent> assemblyComponentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assemblyId")
    private List<AssemblyComponent> assemblyComponentList1;
    @OneToMany(mappedBy = "componentId")
    private List<DesignElement> designElementList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<ComponentSource> componentSourceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "component")
    private List<ComponentProperty> componentPropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<ComponentResource> componentResourceList;

    public Component() {
    }

    public Component(Integer id) {
        this.id = id;
    }

    public Component(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    @XmlTransient
    public List<DesignElementConnection> getDesignElementConnectionList() {
        return designElementConnectionList;
    }

    public void setDesignElementConnectionList(List<DesignElementConnection> designElementConnectionList) {
        this.designElementConnectionList = designElementConnectionList;
    }

    @XmlTransient
    public List<ComponentConnector> getComponentConnectorList() {
        return componentConnectorList;
    }

    public void setComponentConnectorList(List<ComponentConnector> componentConnectorList) {
        this.componentConnectorList = componentConnectorList;
    }

    public EntityInfo getEntityInfoId() {
        return entityInfoId;
    }

    public void setEntityInfoId(EntityInfo entityInfoId) {
        this.entityInfoId = entityInfoId;
    }

    public ComponentType getComponentTypeId() {
        return componentTypeId;
    }

    public void setComponentTypeId(ComponentType componentTypeId) {
        this.componentTypeId = componentTypeId;
    }

    @XmlTransient
    public List<ComponentInstance> getComponentInstanceList() {
        return componentInstanceList;
    }

    public void setComponentInstanceList(List<ComponentInstance> componentInstanceList) {
        this.componentInstanceList = componentInstanceList;
    }

    @XmlTransient
    public List<AssemblyComponent> getAssemblyComponentList() {
        return assemblyComponentList;
    }

    public void setAssemblyComponentList(List<AssemblyComponent> assemblyComponentList) {
        this.assemblyComponentList = assemblyComponentList;
    }

    @XmlTransient
    public List<AssemblyComponent> getAssemblyComponentList1() {
        return assemblyComponentList1;
    }

    public void setAssemblyComponentList1(List<AssemblyComponent> assemblyComponentList1) {
        this.assemblyComponentList1 = assemblyComponentList1;
    }

    @XmlTransient
    public List<DesignElement> getDesignElementList() {
        return designElementList;
    }

    public void setDesignElementList(List<DesignElement> designElementList) {
        this.designElementList = designElementList;
    }

    @XmlTransient
    public List<ComponentSource> getComponentSourceList() {
        return componentSourceList;
    }

    public void setComponentSourceList(List<ComponentSource> componentSourceList) {
        this.componentSourceList = componentSourceList;
    }

    @XmlTransient
    public List<ComponentProperty> getComponentPropertyList() {
        return componentPropertyList;
    }

    public void setComponentPropertyList(List<ComponentProperty> componentPropertyList) {
        this.componentPropertyList = componentPropertyList;
    }

    @XmlTransient
    public List<ComponentResource> getComponentResourceList() {
        return componentResourceList;
    }

    public void setComponentResourceList(List<ComponentResource> componentResourceList) {
        this.componentResourceList = componentResourceList;
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
        if (!(object instanceof Component)) {
            return false;
        }
        Component other = (Component) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.entities.Component[ id=" + id + " ]";
    }
    
}
