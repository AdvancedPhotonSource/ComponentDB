/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * @author djarosz
 */
@Entity
@Table(name = "property_metadata")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PropertyMetadata.findAll", query = "SELECT p FROM PropertyMetadata p"),
    @NamedQuery(name = "PropertyMetadata.findById", query = "SELECT p FROM PropertyMetadata p WHERE p.id = :id"),
    @NamedQuery(name = "PropertyMetadata.findByMetadataKey", query = "SELECT p FROM PropertyMetadata p WHERE p.metadataKey = :metadataKey"),
    @NamedQuery(name = "PropertyMetadata.findByMetadataValue", query = "SELECT p FROM PropertyMetadata p WHERE p.metadataValue = :metadataValue")})
public class PropertyMetadata extends PropertyMetadataBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "metadata_key")
    private String metadataKey;
    @Basic(optional = false)
    @NotNull
    @Size(min = 0, max = 256, message = "Metadata value cannot be more than 256 characters long.")
    @Column(name = "metadata_value")
    private String metadataValue;
    @JoinColumn(name = "property_value_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @JsonIgnore
    private PropertyValue propertyValue;

    public PropertyMetadata() {
    }

    public PropertyMetadata(Integer id) {
        this.id = id;
    }

    public PropertyMetadata(Integer id, String metadataKey, String metadataValue) {
        this.id = id;
        this.metadataKey = metadataKey;
        this.metadataValue = metadataValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getMetadataKey() {
        return metadataKey;
    }

    public void setMetadataKey(String metadataKey) {
        this.metadataKey = metadataKey;
    }

    @Override
    public String getMetadataValue() {
        return metadataValue;
    }

    public void setMetadataValue(String metadataValue) {
        this.metadataValue = metadataValue;
    }

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(PropertyValue propertyValue) {
        this.propertyValue = propertyValue;
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
        if (!(object instanceof PropertyMetadata)) {
            return false;
        }
        
        PropertyMetadata other = (PropertyMetadata) object;
        
        if (this.propertyValue == null) {
            // Comparison cannot be made. 
            return false; 
        }
               
        if (this.propertyValue.equals(other.getPropertyValue())) {
            if (this.metadataKey.equals(other.metadataKey)) {
                return true; 
            }
        }
        
        return false;
    }

    @Override
    public String toString() {
        if (metadataValue != null && metadataKey != null) {
            return "Key: " + metadataKey + " - Value: " + metadataValue;
        }
        return "gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata[ id=" + id + " ]";
    }
    
}
