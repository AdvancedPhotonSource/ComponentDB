/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.entities;

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
    @NamedQuery(name = "Design.findAll", query = "SELECT d FROM Design d"),
    @NamedQuery(name = "Design.findById", query = "SELECT d FROM Design d WHERE d.id = :id"),
    @NamedQuery(name = "Design.findByName", query = "SELECT d FROM Design d WHERE d.name = :name"),
    @NamedQuery(name = "Design.findByDescription", query = "SELECT d FROM Design d WHERE d.description = :description")})
public class Design extends CloneableEntity {
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
    @JoinTable(name = "design_property", joinColumns = {
        @JoinColumn(name = "design_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "property_value_id", referencedColumnName = "id")})
    @ManyToMany
    private List<PropertyValue> propertyValueList;
    @JoinTable(name = "design_log", joinColumns = {
        @JoinColumn(name = "design_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Log> logList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "childDesignId")
    private List<DesignLink> designLinkList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentDesignId")
    private List<DesignLink> designLinkList1;
    @OneToMany(mappedBy = "childDesign")
    private List<DesignElement> designElementList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentDesign")
    private List<DesignElement> designElementList1;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EntityInfo entityInfo;

    public Design() {
    }

    public Design(Integer id) {
        this.id = id;
    }

    public Design(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
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
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    @XmlTransient
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    @XmlTransient
    public List<DesignLink> getDesignLinkList() {
        return designLinkList;
    }

    public void setDesignLinkList(List<DesignLink> designLinkList) {
        this.designLinkList = designLinkList;
    }

    @XmlTransient
    public List<DesignLink> getDesignLinkList1() {
        return designLinkList1;
    }

    public void setDesignLinkList1(List<DesignLink> designLinkList1) {
        this.designLinkList1 = designLinkList1;
    }

    @XmlTransient
    public List<DesignElement> getDesignElementList() {
        return designElementList;
    }

    public void setDesignElementList(List<DesignElement> designElementList) {
        this.designElementList = designElementList;
    }

    @XmlTransient
    public List<DesignElement> getDesignElementList1() {
        return designElementList1;
    }

    public void setDesignElementList1(List<DesignElement> designElementList1) {
        this.designElementList1 = designElementList1;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
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
        if (!(object instanceof Design)) {
            return false;
        }
        Design other = (Design) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.entities.Design[ id=" + id + " ]";
    }
    
}
