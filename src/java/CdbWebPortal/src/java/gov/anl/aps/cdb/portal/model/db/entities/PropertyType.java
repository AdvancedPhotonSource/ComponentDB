/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import gov.anl.aps.cdb.common.utilities.StringUtility;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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
@Cacheable(true)
@Table(name = "property_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PropertyType.findAll", query = "SELECT p FROM PropertyType p ORDER BY p.name"),
    @NamedQuery(name = "PropertyType.findById", query = "SELECT p FROM PropertyType p WHERE p.id = :id"),
    @NamedQuery(name = "PropertyType.findByName", query = "SELECT p FROM PropertyType p WHERE p.name = :name"),
    @NamedQuery(name = "PropertyType.findByInternalStatus", query = "SELECT p FROM PropertyType p WHERE p.isInternal = :isInternal ORDER BY p.name"),
    @NamedQuery(name = "PropertyType.findByDescription", query = "SELECT p FROM PropertyType p WHERE p.description = :description"),
    @NamedQuery(name = "PropertyType.findByPropertyTypeHandler", query = "SELECT p FROM PropertyType p WHERE p.propertyTypeHandler = :propertyTypeHandler"),
    @NamedQuery(name = "PropertyType.findByPropertyTypeCategory", query = "SELECT P FROM PropertyType p INNER JOIN p.propertyTypeCategory ptc WHERE ptc = :propertyTypeCategory"),
    @NamedQuery(name = "PropertyType.findByDefaultValue", query = "SELECT p FROM PropertyType p WHERE p.defaultValue = :defaultValue"),
    @NamedQuery(name = "PropertyType.findByDefaultUnits", query = "SELECT p FROM PropertyType p WHERE p.defaultUnits = :defaultUnits"),
    @NamedQuery(name = "PropertyType.findByIsUserWriteable", query = "SELECT p FROM PropertyType p WHERE p.isUserWriteable = :isUserWriteable"),
    @NamedQuery(name = "PropertyType.findByIsDynamic", query = "SELECT p FROM PropertyType p WHERE p.isDynamic = :isDynamic"),
    @NamedQuery(name = "PropertyType.findByIsInternal", query = "SELECT p FROM PropertyType p WHERE p.isInternal = :isInternal"),
    @NamedQuery(name = "PropertyType.findByIsActive", query = "SELECT p FROM PropertyType p WHERE p.isActive = :isActive")})
@JsonIgnoreProperties({
    "allowedDomainList",
    "entityTypeList",
    "propertyValueList",
    "propertyTypeCategory",
    "propertyTypeHandler",
    "propertyTypeMetadataList",
    "allowedPropertyValueList",
        
    //Transient Variables 
    "displayType",    
    "allowedDomainString"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyType extends CdbEntity implements Serializable {

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
    @Size(max = 256)
    @Column(name = "prompt_description")
    private String promptDescription;
    @Size(max = 64)
    @Column(name = "default_value")
    private String defaultValue;
    @Size(max = 16)
    @Column(name = "default_units")
    private String defaultUnits;
    @Column(name = "is_user_writeable")
    private Boolean isUserWriteable;
    @Column(name = "is_dynamic")
    private Boolean isDynamic;
    @Column(name = "is_internal")
    private Boolean isInternal;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "is_metadata_dynamic")
    private Boolean isMetadataDynamic;
    @JoinTable(name = "allowed_property_domain", joinColumns = {
        @JoinColumn(name = "property_type_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "domain_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Domain> allowedDomainList;
    @JoinTable(name = "allowed_entity_type", joinColumns = {
        @JoinColumn(name = "property_type_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "entity_type_id", referencedColumnName = "id")})
    @ManyToMany    
    private List<EntityType> entityTypeList;
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "propertyType")    
    private List<PropertyValue> propertyValueList;
    @JoinColumn(name = "property_type_category_id", referencedColumnName = "id")
    @ManyToOne    
    private PropertyTypeCategory propertyTypeCategory;
    @JoinColumn(name = "property_type_handler_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private PropertyTypeHandler propertyTypeHandler;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyType")    
    private List<PropertyTypeMetadata> propertyTypeMetadataList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyType")
    @OrderBy("sortOrder ASC")    
    private List<AllowedPropertyValue> allowedPropertyValueList;

    private transient List<AllowedPropertyValue> sortedAllowedPropertyValueList;

    private transient DisplayType displayType = null;

    private transient String allowedDomainString = null;

    public void resetCachedVales() {
        sortedAllowedPropertyValueList = null;
        allowedDomainString = null;
    }

    public PropertyType() {
    }

    public PropertyType(Integer id) {
        this.id = id;
    }

    public PropertyType(Integer id, String name) {
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

    public String getPromptDescription() {
        return promptDescription;
    }

    public void setPromptDescription(String promptDescription) {
        this.promptDescription = promptDescription;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultUnits() {
        return defaultUnits;
    }

    public void setDefaultUnits(String defaultUnits) {
        this.defaultUnits = defaultUnits;
    }

    public Boolean getIsUserWriteable() {
        return isUserWriteable;
    }

    public void setIsUserWriteable(Boolean isUserWriteable) {
        this.isUserWriteable = isUserWriteable;
    }

    public Boolean getIsDynamic() {
        return isDynamic;
    }

    public void setIsDynamic(Boolean isDynamic) {
        this.isDynamic = isDynamic;
    }

    public Boolean getIsMetadataDynamic() {
        return isMetadataDynamic;
    }

    public void setIsMetadataDynamic(Boolean isMetadataDynamic) {
        this.isMetadataDynamic = isMetadataDynamic;
    }

    public Boolean getIsInternal() {
        return isInternal;
    }

    public void setIsInternal(Boolean isInternal) {
        this.isInternal = isInternal;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @XmlTransient
    public List<Domain> getAllowedDomainList() {
        return allowedDomainList;
    }

    public void setAllowedDomainList(List<Domain> allowedDomainList) {
        allowedDomainString = null;
        this.allowedDomainList = allowedDomainList;
    }

    public String getAllowedDomainString() {
        if (allowedDomainString == null) {
            allowedDomainString = StringUtility.getStringifyCdbList(allowedDomainList);
        }
        return allowedDomainString;
    }

    @XmlTransient
    public List<EntityType> getEntityTypeList() {
        return entityTypeList;
    }

    public void setEntityTypeList(List<EntityType> entityTypeList) {
        this.entityTypeList = entityTypeList;
    }

    @XmlTransient
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    public PropertyTypeCategory getPropertyTypeCategory() {
        return propertyTypeCategory;
    }

    public void setPropertyTypeCategory(PropertyTypeCategory propertyTypeCategory) {
        this.propertyTypeCategory = propertyTypeCategory;
    }

    public PropertyTypeHandler getPropertyTypeHandler() {
        return propertyTypeHandler;
    }

    public void setPropertyTypeHandler(PropertyTypeHandler propertyTypeHandler) {
        this.propertyTypeHandler = propertyTypeHandler;
    }

    @XmlTransient
    public List<PropertyTypeMetadata> getPropertyTypeMetadataList() {
        return propertyTypeMetadataList;
    }
    
    @XmlTransient
    public PropertyTypeMetadata getPropertyTypeMetadataForKey(String key) {
        if (propertyTypeMetadataList != null) {
            for (PropertyTypeMetadata propertyTypeMetadata : propertyTypeMetadataList) {
                if (propertyTypeMetadata.getMetadataKey().equals(key)) {
                    return propertyTypeMetadata;
                }
            }
        }
        return null;
    }

    public void setPropertyTypeMetadataList(List<PropertyTypeMetadata> propertyTypeMetadataList) {
        this.propertyTypeMetadataList = propertyTypeMetadataList;
    }

    @XmlTransient
    public List<AllowedPropertyValue> getAllowedPropertyValueList() {
        return allowedPropertyValueList;
    }

    public List<AllowedPropertyValue> getSortedAllowedPropertyValueList() {
        if (sortedAllowedPropertyValueList == null) {
            sortedAllowedPropertyValueList = new ArrayList<>();
            sortedAllowedPropertyValueList.addAll(allowedPropertyValueList);

            Collections.sort(sortedAllowedPropertyValueList, new Comparator<AllowedPropertyValue>() {
                @Override
                public int compare(AllowedPropertyValue o1, AllowedPropertyValue o2) {
                    return o1.getSortOrder().compareTo(o2.getSortOrder());
                }
            });

            if (defaultValue != null && !defaultValue.equals("")) {
                for (AllowedPropertyValue apv : allowedPropertyValueList) {
                    if (apv.getValue().equals(defaultValue)) {
                        sortedAllowedPropertyValueList.remove(apv);
                        sortedAllowedPropertyValueList.add(0, apv);
                        break;
                    }
                }
            }
        }
        return sortedAllowedPropertyValueList;
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(DisplayType displayType) {
        this.displayType = displayType;
    }

    public void setAllowedPropertyValueList(List<AllowedPropertyValue> allowedPropertyValueList) {
        this.allowedPropertyValueList = allowedPropertyValueList;
    }

    public boolean hasAllowedPropertyValues() {
        return !allowedPropertyValueList.isEmpty();
    }

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(this, id, name);
        searchResult.doesValueContainPattern("name", name, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        return searchResult;
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
        if (!(object instanceof PropertyType)) {
            return false;
        }
        PropertyType other = (PropertyType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.PropertyType[ id=" + id + " ]";
    }

}
