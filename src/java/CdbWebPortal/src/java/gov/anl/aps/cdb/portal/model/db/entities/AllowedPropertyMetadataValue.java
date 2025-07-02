/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
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
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "allowed_property_metadata_value")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AllowedPropertyMetadataValue.findAll", query = "SELECT p FROM AllowedPropertyMetadataValue p"),
    @NamedQuery(name = "AllowedPropertyMetadataValue.findById", query = "SELECT p FROM AllowedPropertyMetadataValue p WHERE p.id = :id")})
public class AllowedPropertyMetadataValue extends CdbEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min=1, max = 32)
    @Column(name = "metadata_value")
    private String metadataValue;
    @Size(max = 256)
    private String description;
    @JoinColumn(name = "property_type_metadata_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private PropertyTypeMetadata propertyTypeMetadata;
    @Column(name = "sort_order")
    private Float sortOrder;

    public AllowedPropertyMetadataValue() {
    }

    public AllowedPropertyMetadataValue(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }   

    public String getDescription() {
        return description;
    }

    public String getMetadataValue() {
        return metadataValue;
    }

    public void setMetadataValue(String metadataValue) {
        this.metadataValue = metadataValue;
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
    public PropertyTypeMetadata getPropertyTypeMetadata() {
        return propertyTypeMetadata;
    }

    public void setPropertyTypeMetadata(PropertyTypeMetadata propertyTypeMetadata) {
        this.propertyTypeMetadata = propertyTypeMetadata;
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
        if (!(object instanceof AllowedPropertyMetadataValue)) {
            return false;
        }
        AllowedPropertyMetadataValue other = (AllowedPropertyMetadataValue) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return metadataValue;
    }
    
}
