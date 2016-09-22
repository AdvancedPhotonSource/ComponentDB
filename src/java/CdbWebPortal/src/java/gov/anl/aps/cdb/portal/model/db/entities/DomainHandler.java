/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "domain_handler")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DomainHandler.findAll", query = "SELECT d FROM DomainHandler d"),
    @NamedQuery(name = "DomainHandler.findById", query = "SELECT d FROM DomainHandler d WHERE d.id = :id"),
    @NamedQuery(name = "DomainHandler.findByName", query = "SELECT d FROM DomainHandler d WHERE d.name = :name"),
    @NamedQuery(name = "DomainHandler.findByDescription", query = "SELECT d FROM DomainHandler d WHERE d.description = :description")})
public class DomainHandler extends CdbEntity implements Serializable {

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
    @JoinTable(name = "allowed_domain_handler_entity_type", joinColumns = {
        @JoinColumn(name = "domain_handler_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "entity_type_id", referencedColumnName = "id")})
    private List<EntityType> allowedEntityTypeList; 
    @ManyToMany
    private List<DomainHandler> domainHandlerList;
    @OneToMany(mappedBy = "domainHandler")
    private List<Domain> domainList;    
    @ManyToMany(mappedBy = "domainHandlerList")
    private List<PropertyType> propertyTypeList;

    public DomainHandler() {
    }

    public DomainHandler(Integer id) {
        this.id = id;
    }

    public DomainHandler(Integer id, String name) {
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
    public List<Domain> getDomainList() {
        return domainList;
    }

    public void setDomainList(List<Domain> domainList) {
        this.domainList = domainList;
    }

    @XmlTransient
    public List<PropertyType> getPropertyTypeList() {
        return propertyTypeList;
    }

    public void setPropertyTypeList(List<PropertyType> propertyTypeList) {
        this.propertyTypeList = propertyTypeList;
    }

    @XmlTransient
    public List<EntityType> getAllowedEntityTypeList() {
        return allowedEntityTypeList;
    }

    public void setAllowedEntityTypeList(List<EntityType> allowedEntityTypeList) {
        this.allowedEntityTypeList = allowedEntityTypeList;
    }

    @XmlTransient
    public List<DomainHandler> getDomainHandlerList() {
        return domainHandlerList;
    }

    public void setDomainHandlerList(List<DomainHandler> domainHandlerList) {
        this.domainHandlerList = domainHandlerList;
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
        if (!(object instanceof DomainHandler)) {
            return false;
        }
        DomainHandler other = (DomainHandler) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.DomainHandler[ id=" + id + " ]";
    }
    
}
