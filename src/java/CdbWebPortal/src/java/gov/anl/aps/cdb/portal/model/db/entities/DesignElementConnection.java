/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.entities;

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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Design element connection entity class.
 */
@Entity
@Table(name = "design_element_connection")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DesignElementConnection.findAll", query = "SELECT d FROM DesignElementConnection d"),
    @NamedQuery(name = "DesignElementConnection.findById", query = "SELECT d FROM DesignElementConnection d WHERE d.id = :id"),
    @NamedQuery(name = "DesignElementConnection.findByLinkComponentQuantity", query = "SELECT d FROM DesignElementConnection d WHERE d.linkComponentQuantity = :linkComponentQuantity"),
    @NamedQuery(name = "DesignElementConnection.findByLabel", query = "SELECT d FROM DesignElementConnection d WHERE d.label = :label"),
    @NamedQuery(name = "DesignElementConnection.findByDescription", query = "SELECT d FROM DesignElementConnection d WHERE d.description = :description")})
public class DesignElementConnection extends CdbEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Column(name = "link_component_quantity")
    private Integer linkComponentQuantity;
    @Size(max = 64)
    private String label;
    @Size(max = 256)
    private String description;
    @JoinColumn(name = "resource_type_id", referencedColumnName = "id")
    @ManyToOne
    private ResourceType resourceTypeId;
    @JoinColumn(name = "link_component_id", referencedColumnName = "id")
    @ManyToOne
    private Component linkComponentId;
    @JoinColumn(name = "second_component_connector_id", referencedColumnName = "id")
    @ManyToOne
    private ComponentConnector secondComponentConnectorId;
    @JoinColumn(name = "second_design_element_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private DesignElement secondDesignElementId;
    @JoinColumn(name = "first_component_connector_id", referencedColumnName = "id")
    @ManyToOne
    private ComponentConnector firstComponentConnectorId;
    @JoinColumn(name = "first_design_element_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private DesignElement firstDesignElementId;

    public DesignElementConnection() {
    }

    public DesignElementConnection(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLinkComponentQuantity() {
        return linkComponentQuantity;
    }

    public void setLinkComponentQuantity(Integer linkComponentQuantity) {
        this.linkComponentQuantity = linkComponentQuantity;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResourceType getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(ResourceType resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public Component getLinkComponentId() {
        return linkComponentId;
    }

    public void setLinkComponentId(Component linkComponentId) {
        this.linkComponentId = linkComponentId;
    }

    public ComponentConnector getSecondComponentConnectorId() {
        return secondComponentConnectorId;
    }

    public void setSecondComponentConnectorId(ComponentConnector secondComponentConnectorId) {
        this.secondComponentConnectorId = secondComponentConnectorId;
    }

    public DesignElement getSecondDesignElementId() {
        return secondDesignElementId;
    }

    public void setSecondDesignElementId(DesignElement secondDesignElementId) {
        this.secondDesignElementId = secondDesignElementId;
    }

    public ComponentConnector getFirstComponentConnectorId() {
        return firstComponentConnectorId;
    }

    public void setFirstComponentConnectorId(ComponentConnector firstComponentConnectorId) {
        this.firstComponentConnectorId = firstComponentConnectorId;
    }

    public DesignElement getFirstDesignElementId() {
        return firstDesignElementId;
    }

    public void setFirstDesignElementId(DesignElement firstDesignElementId) {
        this.firstDesignElementId = firstDesignElementId;
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
        if (!(object instanceof DesignElementConnection)) {
            return false;
        }
        DesignElementConnection other = (DesignElementConnection) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "DesignElementConnection[ id=" + id + " ]";
    }

}
