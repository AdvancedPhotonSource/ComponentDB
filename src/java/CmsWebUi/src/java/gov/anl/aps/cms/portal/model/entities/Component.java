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
    @NamedQuery(name = "Component.findByComponentId", query = "SELECT c FROM Component c WHERE c.componentId = :componentId"),
    @NamedQuery(name = "Component.findByComponentInstanceName", query = "SELECT c FROM Component c WHERE c.componentInstanceName = :componentInstanceName"),
    @NamedQuery(name = "Component.findByMarkForDelete", query = "SELECT c FROM Component c WHERE c.markForDelete = :markForDelete"),
    @NamedQuery(name = "Component.findByVersion", query = "SELECT c FROM Component c WHERE c.version = :version"),
    @NamedQuery(name = "Component.findByImageUri", query = "SELECT c FROM Component c WHERE c.imageUri = :imageUri")})
public class Component implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_id")
    private Integer componentId;
    @Size(max = 60)
    @Column(name = "component_instance_name")
    private String componentInstanceName;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @Size(max = 255)
    @Column(name = "image_uri")
    private String imageUri;
    @JoinColumn(name = "component_type_id", referencedColumnName = "component_type_id")
    @ManyToOne(optional = false)
    private ComponentType componentTypeId;
    @OneToMany(mappedBy = "childComponentId")
    private List<ComponentRel> componentRelList;
    @OneToMany(mappedBy = "parentComponentId")
    private List<ComponentRel> componentRelList1;
    @OneToMany(mappedBy = "componentId")
    private List<ComponentPort> componentPortList;
    @OneToMany(mappedBy = "componentId")
    private List<ComponentInstance> componentInstanceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<ApsComponent> apsComponentList;
    @OneToMany(mappedBy = "componentId")
    private List<Server> serverList;

    public Component() {
    }

    public Component(Integer componentId) {
        this.componentId = componentId;
    }

    public Integer getComponentId() {
        return componentId;
    }

    public void setComponentId(Integer componentId) {
        this.componentId = componentId;
    }

    public String getComponentInstanceName() {
        return componentInstanceName;
    }

    public void setComponentInstanceName(String componentInstanceName) {
        this.componentInstanceName = componentInstanceName;
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

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public ComponentType getComponentTypeId() {
        return componentTypeId;
    }

    public void setComponentTypeId(ComponentType componentTypeId) {
        this.componentTypeId = componentTypeId;
    }

    @XmlTransient
    public List<ComponentRel> getComponentRelList() {
        return componentRelList;
    }

    public void setComponentRelList(List<ComponentRel> componentRelList) {
        this.componentRelList = componentRelList;
    }

    @XmlTransient
    public List<ComponentRel> getComponentRelList1() {
        return componentRelList1;
    }

    public void setComponentRelList1(List<ComponentRel> componentRelList1) {
        this.componentRelList1 = componentRelList1;
    }

    @XmlTransient
    public List<ComponentPort> getComponentPortList() {
        return componentPortList;
    }

    public void setComponentPortList(List<ComponentPort> componentPortList) {
        this.componentPortList = componentPortList;
    }

    @XmlTransient
    public List<ComponentInstance> getComponentInstanceList() {
        return componentInstanceList;
    }

    public void setComponentInstanceList(List<ComponentInstance> componentInstanceList) {
        this.componentInstanceList = componentInstanceList;
    }

    @XmlTransient
    public List<ApsComponent> getApsComponentList() {
        return apsComponentList;
    }

    public void setApsComponentList(List<ApsComponent> apsComponentList) {
        this.apsComponentList = apsComponentList;
    }

    @XmlTransient
    public List<Server> getServerList() {
        return serverList;
    }

    public void setServerList(List<Server> serverList) {
        this.serverList = serverList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentId != null ? componentId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Component)) {
            return false;
        }
        Component other = (Component) object;
        if ((this.componentId == null && other.componentId != null) || (this.componentId != null && !this.componentId.equals(other.componentId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.Component[ componentId=" + componentId + " ]";
    }
    
}
