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
@Table(name = "property_metadata_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PropertyMetadataHistory.findAll", query = "SELECT p FROM PropertyMetadataHistory p"),
    @NamedQuery(name = "PropertyMetadataHistory.findById", query = "SELECT p FROM PropertyMetadataHistory p WHERE p.id = :id"),
    @NamedQuery(name = "PropertyMetadataHistory.findByMetadataKey", query = "SELECT p FROM PropertyMetadataHistory p WHERE p.metadataKey = :metadataKey"),
    @NamedQuery(name = "PropertyMetadataHistory.findByMetadataValue", query = "SELECT p FROM PropertyMetadataHistory p WHERE p.metadataValue = :metadataValue")})
public class PropertyMetadataHistory extends PropertyMetadataBase implements Serializable {

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
    @Size(min = 0, max = 64)
    @Column(name = "metadata_value")
    private String metadataValue;
    @JoinColumn(name = "property_value_history_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @JsonIgnore
    private PropertyValueHistory propertyValueHistory;

    public PropertyMetadataHistory() {
    }

    public PropertyMetadataHistory(Integer id) {
        this.id = id;
    }

    public PropertyMetadataHistory(Integer id, String metadataKey, String metadataValue) {
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

    public PropertyValueHistory getPropertyValueHistory() {
        return propertyValueHistory;
    }

    public void setPropertyValueHistory(PropertyValueHistory propertyValueHistory) {
        this.propertyValueHistory = propertyValueHistory;
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
        if (!(object instanceof PropertyMetadataHistory)) {
            return false;
        }
        
        PropertyMetadataHistory other = (PropertyMetadataHistory) object;
        
        if (this.propertyValueHistory == null) {
            // Comparison cannot be made. 
            return false; 
        }
               
        if (this.propertyValueHistory.equals(other.getPropertyValueHistory())) {
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
        return "gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadataHistory[ id=" + id + " ]";
    }
    
}
