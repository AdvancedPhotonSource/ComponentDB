/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "property_type_metadata")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PropertyTypeMetadata.findAll", query = "SELECT p FROM PropertyTypeMetadata p"),
    @NamedQuery(name = "PropertyTypeMetadata.findById", query = "SELECT p FROM PropertyTypeMetadata p WHERE p.id = :id")})
public class PropertyTypeMetadata extends CdbEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min=1, max = 32)
    @Column(name = "metadata_key")
    private String metadataKey;
    @Size(max = 256)
    private String description;
    @JoinColumn(name = "property_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private PropertyType propertyType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyTypeMetadata")
    @OrderBy("sortOrder ASC")
    private List<AllowedPropertyMetadataValue> allowedPropertyMetadataValueList; 

    public PropertyTypeMetadata() {
    }

    public PropertyTypeMetadata(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMetadataKey() {
        return metadataKey;
    }

    public void setMetadataKey(String metadataKey) {
        this.metadataKey = metadataKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    @XmlTransient    
    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public List<AllowedPropertyMetadataValue> getAllowedPropertyMetadataValueList() {
        return allowedPropertyMetadataValueList;
    }

    public void setAllowedPropertyMetadataValueList(List<AllowedPropertyMetadataValue> allowedPropertyMetadataValueList) {
        this.allowedPropertyMetadataValueList = allowedPropertyMetadataValueList;
    }
    
    @JsonIgnore
    public boolean getIsHaveAllowedValues() {
        if (allowedPropertyMetadataValueList != null) { 
            return allowedPropertyMetadataValueList.size() > 0; 
        }
        return false;
    }
    
    public boolean hasAllowedPropertyMetadataValue(String value) {
        if (allowedPropertyMetadataValueList != null) {
            for (AllowedPropertyMetadataValue allowedValue : allowedPropertyMetadataValueList) {
                if (allowedValue.getMetadataValue().equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @JsonIgnore
    public boolean getIsHaveDescription() {
        return description != null && !description.equals(""); 
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
        if (!(object instanceof PropertyTypeMetadata)) {
            return false;
        }
        PropertyTypeMetadata other = (PropertyTypeMetadata) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return metadataKey;
    }
    
}
