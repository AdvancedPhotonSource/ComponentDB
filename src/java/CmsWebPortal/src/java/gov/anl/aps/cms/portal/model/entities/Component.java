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
@Table(name = "component")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Component.findAll", query = "SELECT c FROM Component c"),
    @NamedQuery(name = "Component.findById", query = "SELECT c FROM Component c WHERE c.id = :id"),
    @NamedQuery(name = "Component.findByName", query = "SELECT c FROM Component c WHERE c.name = :name"),
    @NamedQuery(name = "Component.findByDescription", query = "SELECT c FROM Component c WHERE c.description = :description"),
    @NamedQuery(name = "Component.findByDocumentationUri", query = "SELECT c FROM Component c WHERE c.documentationUri = :documentationUri"),
    @NamedQuery(name = "Component.findByEstimatedCost", query = "SELECT c FROM Component c WHERE c.estimatedCost = :estimatedCost")})
public class Component implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Size(max = 256)
    @Column(name = "description")
    private String description;
    @Size(max = 256)
    @Column(name = "documentation_uri")
    private String documentationUri;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "estimated_cost")
    private Float estimatedCost;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<ComponentConnector> componentConnectorList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<ComponentLog> componentLogList;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EntityInfo entityInfo;
    @JoinColumn(name = "component_state_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentState componentState;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<DesignComponent> designComponentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "designId")
    private List<DesignComponent> designComponentList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<CollectionComponent> collectionComponentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<ComponentInstance> componentInstanceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<AssemblyComponent> assemblyComponentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assemblyId")
    private List<AssemblyComponent> assemblyComponentList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<ComponentSource> componentSourceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<ComponentProperty> componentPropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<ComponentComponentType> componentComponentTypeList;

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

    public String getDocumentationUri() {
        return documentationUri;
    }

    public void setDocumentationUri(String documentationUri) {
        this.documentationUri = documentationUri;
    }

    public Float getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Float estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    @XmlTransient
    public List<ComponentConnector> getComponentConnectorList() {
        return componentConnectorList;
    }

    public void setComponentConnectorList(List<ComponentConnector> componentConnectorList) {
        this.componentConnectorList = componentConnectorList;
    }

    @XmlTransient
    public List<ComponentLog> getComponentLogList() {
        return componentLogList;
    }

    public void setComponentLogList(List<ComponentLog> componentLogList) {
        this.componentLogList = componentLogList;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public ComponentState getComponentState() {
        return componentState;
    }

    public void setComponentState(ComponentState componentState) {
        this.componentState = componentState;
    }

    @XmlTransient
    public List<DesignComponent> getDesignComponentList() {
        return designComponentList;
    }

    public void setDesignComponentList(List<DesignComponent> designComponentList) {
        this.designComponentList = designComponentList;
    }

    @XmlTransient
    public List<DesignComponent> getDesignComponentList1() {
        return designComponentList1;
    }

    public void setDesignComponentList1(List<DesignComponent> designComponentList1) {
        this.designComponentList1 = designComponentList1;
    }

    @XmlTransient
    public List<CollectionComponent> getCollectionComponentList() {
        return collectionComponentList;
    }

    public void setCollectionComponentList(List<CollectionComponent> collectionComponentList) {
        this.collectionComponentList = collectionComponentList;
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
    public List<ComponentComponentType> getComponentComponentTypeList() {
        return componentComponentTypeList;
    }

    public void setComponentComponentTypeList(List<ComponentComponentType> componentComponentTypeList) {
        this.componentComponentTypeList = componentComponentTypeList;
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
        return "gov.anl.aps.cms.portal.model.entities.Component[ id=" + id + " ]";
    }
    
}
