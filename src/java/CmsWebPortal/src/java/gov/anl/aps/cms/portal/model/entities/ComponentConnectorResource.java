/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

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
@Table(name = "component_connector_resource")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentConnectorResource.findAll", query = "SELECT c FROM ComponentConnectorResource c"),
    @NamedQuery(name = "ComponentConnectorResource.findById", query = "SELECT c FROM ComponentConnectorResource c WHERE c.id = :id"),
    @NamedQuery(name = "ComponentConnectorResource.findByValue", query = "SELECT c FROM ComponentConnectorResource c WHERE c.value = :value"),
    @NamedQuery(name = "ComponentConnectorResource.findByQuantity", query = "SELECT c FROM ComponentConnectorResource c WHERE c.quantity = :quantity"),
    @NamedQuery(name = "ComponentConnectorResource.findByIsProvided", query = "SELECT c FROM ComponentConnectorResource c WHERE c.isProvided = :isProvided"),
    @NamedQuery(name = "ComponentConnectorResource.findByIsUsedRequired", query = "SELECT c FROM ComponentConnectorResource c WHERE c.isUsedRequired = :isUsedRequired"),
    @NamedQuery(name = "ComponentConnectorResource.findByIsUsedOptional", query = "SELECT c FROM ComponentConnectorResource c WHERE c.isUsedOptional = :isUsedOptional")})
public class ComponentConnectorResource implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "value")
    private String value;
    @Column(name = "quantity")
    private Integer quantity;
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
    @JoinColumn(name = "resource_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ResourceType resourceTypeId;
    @JoinColumn(name = "component_connector_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentConnector componentConnectorId;

    public ComponentConnectorResource() {
    }

    public ComponentConnectorResource(Integer id) {
        this.id = id;
    }

    public ComponentConnectorResource(Integer id, String value, boolean isProvided, boolean isUsedRequired, boolean isUsedOptional) {
        this.id = id;
        this.value = value;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public ResourceType getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(ResourceType resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public ComponentConnector getComponentConnectorId() {
        return componentConnectorId;
    }

    public void setComponentConnectorId(ComponentConnector componentConnectorId) {
        this.componentConnectorId = componentConnectorId;
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
        if (!(object instanceof ComponentConnectorResource)) {
            return false;
        }
        ComponentConnectorResource other = (ComponentConnectorResource) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.portal.model.entities.ComponentConnectorResource[ id=" + id + " ]";
    }
    
}
