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
@Table(name = "design_component")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DesignComponent.findAll", query = "SELECT d FROM DesignComponent d"),
    @NamedQuery(name = "DesignComponent.findById", query = "SELECT d FROM DesignComponent d WHERE d.id = :id"),
    @NamedQuery(name = "DesignComponent.findByName", query = "SELECT d FROM DesignComponent d WHERE d.name = :name")})
public class DesignComponent implements Serializable {
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "designComponentId")
    private List<DesignComponentLog> designComponentLogList;
    @OneToMany(mappedBy = "linkDesignComponentId")
    private List<DesignComponentConnection> designComponentConnectionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "secondDesignComponentId")
    private List<DesignComponentConnection> designComponentConnectionList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firstDesignComponentId")
    private List<DesignComponentConnection> designComponentConnectionList2;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EntityInfo entityInfoId;
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Location locationId;
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component componentId;
    @JoinColumn(name = "design_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component designId;

    public DesignComponent() {
    }

    public DesignComponent(Integer id) {
        this.id = id;
    }

    public DesignComponent(Integer id, String name) {
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

    @XmlTransient
    public List<DesignComponentLog> getDesignComponentLogList() {
        return designComponentLogList;
    }

    public void setDesignComponentLogList(List<DesignComponentLog> designComponentLogList) {
        this.designComponentLogList = designComponentLogList;
    }

    @XmlTransient
    public List<DesignComponentConnection> getDesignComponentConnectionList() {
        return designComponentConnectionList;
    }

    public void setDesignComponentConnectionList(List<DesignComponentConnection> designComponentConnectionList) {
        this.designComponentConnectionList = designComponentConnectionList;
    }

    @XmlTransient
    public List<DesignComponentConnection> getDesignComponentConnectionList1() {
        return designComponentConnectionList1;
    }

    public void setDesignComponentConnectionList1(List<DesignComponentConnection> designComponentConnectionList1) {
        this.designComponentConnectionList1 = designComponentConnectionList1;
    }

    @XmlTransient
    public List<DesignComponentConnection> getDesignComponentConnectionList2() {
        return designComponentConnectionList2;
    }

    public void setDesignComponentConnectionList2(List<DesignComponentConnection> designComponentConnectionList2) {
        this.designComponentConnectionList2 = designComponentConnectionList2;
    }

    public EntityInfo getEntityInfoId() {
        return entityInfoId;
    }

    public void setEntityInfoId(EntityInfo entityInfoId) {
        this.entityInfoId = entityInfoId;
    }

    public Location getLocationId() {
        return locationId;
    }

    public void setLocationId(Location locationId) {
        this.locationId = locationId;
    }

    public Component getComponentId() {
        return componentId;
    }

    public void setComponentId(Component componentId) {
        this.componentId = componentId;
    }

    public Component getDesignId() {
        return designId;
    }

    public void setDesignId(Component designId) {
        this.designId = designId;
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
        if (!(object instanceof DesignComponent)) {
            return false;
        }
        DesignComponent other = (DesignComponent) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.portal.model.entities.DesignComponent[ id=" + id + " ]";
    }
    
}
