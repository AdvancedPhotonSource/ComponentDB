/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.db.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "component_resource")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentResource.findAll", query = "SELECT c FROM ComponentResource c"),
    @NamedQuery(name = "ComponentResource.findById", query = "SELECT c FROM ComponentResource c WHERE c.id = :id"),
    @NamedQuery(name = "ComponentResource.findByValue", query = "SELECT c FROM ComponentResource c WHERE c.value = :value"),
    @NamedQuery(name = "ComponentResource.findByUnits", query = "SELECT c FROM ComponentResource c WHERE c.units = :units"),
    @NamedQuery(name = "ComponentResource.findByDescription", query = "SELECT c FROM ComponentResource c WHERE c.description = :description"),
    @NamedQuery(name = "ComponentResource.findByIsProvided", query = "SELECT c FROM ComponentResource c WHERE c.isProvided = :isProvided"),
    @NamedQuery(name = "ComponentResource.findByIsUsedRequired", query = "SELECT c FROM ComponentResource c WHERE c.isUsedRequired = :isUsedRequired"),
    @NamedQuery(name = "ComponentResource.findByIsUsedOptional", query = "SELECT c FROM ComponentResource c WHERE c.isUsedOptional = :isUsedOptional")})
public class ComponentResource implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 64)
    private String value;
    @Size(max = 16)
    private String units;
    @Size(max = 256)
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_provided")
    private boolean isProvided;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_used_required")
    private boolean isUsedRequired;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_used_optional")
    private boolean isUsedOptional;
    @JoinColumn(name = "component_connector_id", referencedColumnName = "id")
    @ManyToOne
    private ComponentConnector componentConnectorId;
    @JoinColumn(name = "resource_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ResourceType resourceTypeId;
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component componentId;

    public ComponentResource() {
    }

    public ComponentResource(Integer id) {
        this.id = id;
    }

    public ComponentResource(Integer id, boolean isProvided, boolean isUsedRequired, boolean isUsedOptional) {
        this.id = id;
        this.isProvided = isProvided;
        this.isUsedRequired = isUsedRequired;
        this.isUsedOptional = isUsedOptional;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsProvided() {
        return isProvided;
    }

    public void setIsProvided(boolean isProvided) {
        this.isProvided = isProvided;
    }

    public boolean getIsUsedRequired() {
        return isUsedRequired;
    }

    public void setIsUsedRequired(boolean isUsedRequired) {
        this.isUsedRequired = isUsedRequired;
    }

    public boolean getIsUsedOptional() {
        return isUsedOptional;
    }

    public void setIsUsedOptional(boolean isUsedOptional) {
        this.isUsedOptional = isUsedOptional;
    }

    public ComponentConnector getComponentConnectorId() {
        return componentConnectorId;
    }

    public void setComponentConnectorId(ComponentConnector componentConnectorId) {
        this.componentConnectorId = componentConnectorId;
    }

    public ResourceType getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(ResourceType resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public Component getComponentId() {
        return componentId;
    }

    public void setComponentId(Component componentId) {
        this.componentId = componentId;
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
        if (!(object instanceof ComponentResource)) {
            return false;
        }
        ComponentResource other = (ComponentResource) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.entities.ComponentResource[ id=" + id + " ]";
    }
    
}
