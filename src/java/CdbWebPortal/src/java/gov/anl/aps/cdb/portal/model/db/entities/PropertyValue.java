/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.OneToMany;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "property_value")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PropertyValue.findAll", query = "SELECT p FROM PropertyValue p"),
    @NamedQuery(name = "PropertyValue.findById", query = "SELECT p FROM PropertyValue p WHERE p.id = :id"),
    @NamedQuery(name = "PropertyValue.findByTag", query = "SELECT p FROM PropertyValue p WHERE p.tag = :tag"),
    @NamedQuery(name = "PropertyValue.findByValue", query = "SELECT p FROM PropertyValue p WHERE p.value = :value"),
    @NamedQuery(name = "PropertyValue.findByUnits", query = "SELECT p FROM PropertyValue p WHERE p.units = :units"),
    @NamedQuery(name = "PropertyValue.findByDescription", query = "SELECT p FROM PropertyValue p WHERE p.description = :description"),
    @NamedQuery(name = "PropertyValue.findByEnteredOnDateTime", query = "SELECT p FROM PropertyValue p WHERE p.enteredOnDateTime = :enteredOnDateTime"),
    @NamedQuery(name = "PropertyValue.findByIsUserWriteable", query = "SELECT p FROM PropertyValue p WHERE p.isUserWriteable = :isUserWriteable"),
    @NamedQuery(name = "PropertyValue.findByIsDynamic", query = "SELECT p FROM PropertyValue p WHERE p.isDynamic = :isDynamic"),
    @NamedQuery(name = "PropertyValue.findByDisplayValue", query = "SELECT p FROM PropertyValue p WHERE p.displayValue = :displayValue"),
    @NamedQuery(name = "PropertyValue.findByTargetValue", query = "SELECT p FROM PropertyValue p WHERE p.targetValue = :targetValue"),
    @NamedQuery(name = "PropertyValue.findByValueAndTypeId", query = "SELECT p FROM PropertyValue p WHERE p.value = :value and p.propertyType.id = :propertyTypeId")})
@NamedStoredProcedureQueries({
@NamedStoredProcedureQuery(
            name = "propertyValue.fetchRelationshipParentPropertyValues",
            procedureName = "fetch_relationship_parent_property_values",
            resultClasses = PropertyValue.class,
            parameters = {
                @StoredProcedureParameter(
                        name = "item_id",
                        mode = ParameterMode.IN,
                        type = Integer.class
                ),
                @StoredProcedureParameter(
                        name = "parent_item_id",
                        mode = ParameterMode.IN,
                        type = Integer.class
                ),
                @StoredProcedureParameter(
                        name = "relationship_type_id",
                        mode = ParameterMode.IN,
                        type = Integer.class
                )
            }
    ),
})
public class PropertyValue extends PropertyValueBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 64)
    private String tag;
    @Size(max = 512)
    private String value;
    @Size(max = 16)
    private String units;
    @Size(max = 256)
    private String description;
    @Basic(optional = false)
    @Column(name = "entered_on_date_time")
    @Temporal(TemporalType.TIMESTAMP)    
    private Date enteredOnDateTime;
    @Basic(optional = false)    
    @Column(name = "is_user_writeable")
    private boolean isUserWriteable;
    @Column(name = "effective_from_date_time")    
    private Date effectiveFromDateTime;
    @Column(name = "effective_to_date_time")    
    private Date effectiveToDateTime;
    @Basic(optional = false)    
    @Column(name = "is_dynamic")
    private boolean isDynamic;
    @Size(max = 512)
    @Column(name = "display_value")
    private String displayValue;
    @Size(max = 512)
    @Column(name = "target_value")
    private String targetValue;
    @ManyToMany(mappedBy = "propertyValueList")
    private List<ItemConnector> itemConnectorList;
    @ManyToMany(mappedBy = "propertyValueList")
    private List<Connector> connectorList;
    @ManyToMany(mappedBy = "propertyValueList")
    private List<ItemElement> itemElementList;
    @ManyToMany(mappedBy = "propertyValueList")
    private List<ItemElementRelationship> itemElementRelationshipList;
    @JoinColumn(name = "property_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private PropertyType propertyType;
    @JoinColumn(name = "entered_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserInfo enteredByUser;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyValue")
    private List<PropertyValueHistory> propertyValueHistoryList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyValue")    
    private List<PropertyMetadata> propertyMetadataList;

    public static final transient SimpleDateFormat InputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
    
    @JsonIgnore
    private transient Boolean booleanValue;
    @JsonIgnore
    private transient Date dateValue;

    @JsonIgnore
    private transient String infoActionCommand;
    @JsonIgnore
    private transient boolean handlerInfoSet;

    @JsonIgnore
    private transient List<PropertyValueMetadata> propertyValueMetadataList;    
    @JsonIgnore
    private transient Boolean isHasPropertyMetadata = null;
    
    @JsonIgnore
    private transient AllowedPropertyValue selectedAllowedPropertyValue = null; 
    
    private transient List<Item> itemList = null; 

    public PropertyValue() {
    }

    public PropertyValue(Integer id) {
        this.id = id;
    }

    public PropertyValue(Integer id, Date enteredOnDateTime, boolean isUserWriteable, boolean isDynamic) {
        this.id = id;
        this.enteredOnDateTime = enteredOnDateTime;
        this.isUserWriteable = isUserWriteable;
        this.isDynamic = isDynamic;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.booleanValue = null;
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

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public UserInfo getEnteredByUser() {
        return enteredByUser;
    }

    public void setEnteredByUser(UserInfo enteredByUser) {
        this.enteredByUser = enteredByUser;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEnteredOnDateTime() {
        return enteredOnDateTime;
    }

    public void setEnteredOnDateTime(Date enteredOnDateTime) {
        this.enteredOnDateTime = enteredOnDateTime;
    }
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEffectiveFromDateTime() {
        if (effectiveFromDateTime == null) {
            return enteredOnDateTime;
        }
        return effectiveFromDateTime;
    }

    public void setEffectiveFromDateTime(Date effectiveFromDateTime) {
        this.effectiveFromDateTime = effectiveFromDateTime;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEffectiveToDateTime() {
        return effectiveToDateTime;
    }

    public void setEffectiveToDateTime(Date effectiveToDateTime) {
        this.effectiveToDateTime = effectiveToDateTime;
    }

    public boolean getIsUserWriteable() {
        return isUserWriteable;
    }

    public void setIsUserWriteable(boolean isUserWriteable) {
        this.isUserWriteable = isUserWriteable;
    }

    @JsonIgnore
    public String getIsUserWriteableString() {
        return String.valueOf(isUserWriteable);
    }

    public boolean getIsDynamic() {
        return isDynamic;
    }

    public void setIsDynamic(boolean isDynamic) {
        this.isDynamic = isDynamic;
    }

    @JsonIgnore
    public String getIsDynamicString() {
        return String.valueOf(isDynamic);
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    @XmlTransient
    public List<ItemConnector> getItemConnectorList() {
        return itemConnectorList;
    }

    public void setItemConnectorList(List<ItemConnector> itemConnectorList) {
        this.itemConnectorList = itemConnectorList;
    }

    @XmlTransient
    public List<Connector> getConnectorList() {
        return connectorList;
    }

    public void setConnectorList(List<Connector> connectorList) {
        this.connectorList = connectorList;
    }

    public void addItemElementToItemElementList(ItemElement itemElement) {
        if (itemElementList == null) {
            itemElementList = new ArrayList<>();
        }
        itemElementList.add(itemElement);
    }

    // Item list returned with the property value route API calls.
    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @XmlTransient
    public List<ItemElement> getItemElementList() {
        return itemElementList;
    }

    public void setItemElementList(List<ItemElement> itemElementList) {
        this.itemElementList = itemElementList;
    }

    @XmlTransient
    public List<ItemElementRelationship> getItemElementRelationshipList() {
        return itemElementRelationshipList;
    }

    public void setItemElementRelationshipList(List<ItemElementRelationship> itemElementRelationshipList) {
        this.itemElementRelationshipList = itemElementRelationshipList;
    }

    @XmlTransient
    @JsonIgnore
    public List<PropertyValueHistory> getPropertyValueHistoryList() {
        return propertyValueHistoryList;
    }

    public void setPropertyValueHistoryList(List<PropertyValueHistory> propertyValueHistoryList) {
        this.propertyValueHistoryList = propertyValueHistoryList;
    }

    @XmlTransient
    @JsonIgnore
    public List<PropertyMetadata> getPropertyMetadataList() {
        return propertyMetadataList;
    }
    
    @Override
    @JsonIgnore
    public List<PropertyMetadataBase> getPropertyMetadataBaseList() {
        return (List<PropertyMetadataBase>) (List<?>) getPropertyMetadataList();
    }

    public void setPropertyMetadataList(List<PropertyMetadata> propertyMetadataList) {
        this.propertyMetadataList = propertyMetadataList;
    }

    public boolean equalsByValueAndUnits(PropertyValue other) {
        if (other != null) {
            return (ObjectUtility.equals(this.value, other.value)
                    && ObjectUtility.equals(this.units, other.units));
        }
        return false;
    }

    public boolean equalsByValueAndUnitsAndDescription(PropertyValue other) {
        if (other != null) {
            return (ObjectUtility.equals(this.value, other.value)
                    && ObjectUtility.equals(this.units, other.units)
                    && ObjectUtility.equals(this.description, other.description));
        }
        return false;
    }

    public boolean equalsByTagAndValueAndUnitsAndDescriptionAndMetadata(PropertyValue other) {
        if (other != null) {
            boolean equal = (ObjectUtility.equals(this.tag, other.tag)
                    && ObjectUtility.equals(this.value, other.value)
                    && ObjectUtility.equals(this.units, other.units)
                    && ObjectUtility.equals(this.description, other.description));
            
            if (equal) {
                if (this.getIsHasPropertyMetadata()) {
                    if (other.getIsHasPropertyMetadata()) {
                        for (PropertyMetadata pm : this.getPropertyMetadataList()) {
                            String metadataKey = pm.getMetadataKey();
                            PropertyMetadata otherPm = other.getPropertyMetadataForKey(metadataKey); 
                            if ((otherPm == null) || (ObjectUtility.equals(pm.getMetadataValue(), otherPm.getMetadataValue()) == false)) {
                                equal = false; 
                                break; 
                            }
                        }
                    } else {
                        equal = false; 
                    }                 
                } else if (other.getIsHasPropertyMetadata()) {
                    equal = false; 
                }
            }
            
            return equal;
        }
        return false;
    }
    
    public Boolean getBooleanValue() {
        if (booleanValue == null) {
            if (value == null || value.isEmpty()) {
                booleanValue = false;
            } else {
                booleanValue = Boolean.parseBoolean(this.value);
            }
        }

        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
        if (booleanValue != null) {
            this.value = booleanValue.toString();
        }
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonIgnore
    public Date getDateValue() {
        if (dateValue == null && value != null && !value.isEmpty()) {
            try {
                dateValue = InputDateFormat.parse(value);
            } catch (ParseException ex) {
                // should not happen
            }
        }
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
        if (dateValue != null) {
            this.value = dateValue.toString();
        }
    }

    public String getInfoActionCommand() {
        return infoActionCommand;
    }

    public void setInfoActionCommand(String infoActionCommand) {
        this.infoActionCommand = infoActionCommand;
    }

    public boolean isHandlerInfoSet() {
        return handlerInfoSet;
    }

    public void setHandlerInfoSet(boolean handlerInfoSet) {
        this.handlerInfoSet = handlerInfoSet;
    }

    public void setDisplayValueToValue() {
        this.displayValue = value;
    }

    public void setTargetValueToValue() {
        this.targetValue = value;
    }

    public List<PropertyValueMetadata> getPropertyValueMetadataList() {
        if (propertyValueMetadataList == null) {
            if (propertyType != null) {
                List<PropertyTypeMetadata> propertyTypeMetadataList = propertyType.getPropertyTypeMetadataList();
                if (propertyTypeMetadataList == null) {
                    return propertyValueMetadataList; 
                }
                
                propertyValueMetadataList = new ArrayList<>();

                for (PropertyTypeMetadata ptm : propertyTypeMetadataList) {
                    PropertyValueMetadata valueMetadata;
                    valueMetadata = new PropertyValueMetadata(this, ptm);
                    propertyValueMetadataList.add(valueMetadata);
                }

                // Show depreciated metadata values 
                if (propertyMetadataList != null) {
                    if (propertyMetadataList.size() > propertyValueMetadataList.size()) {
                        for (PropertyMetadata propertyMetadata : propertyMetadataList) {
                            String metadataKey = propertyMetadata.getMetadataKey();
                            boolean skip = false;
                            for (PropertyValueMetadata pvm : propertyValueMetadataList) {
                                if (pvm.getPropertyMetadata().getMetadataKey().equals(metadataKey)) {
                                    skip = true;
                                    break;
                                }
                            }
                            if (skip) {
                                continue;
                            }

                            propertyValueMetadataList.add(new PropertyValueMetadata(this, propertyMetadata));
                        }
                    }
                }

            }
        }
        return propertyValueMetadataList;
    }

    public Boolean getIsHasPropertyMetadata() {
        if (isHasPropertyMetadata == null) {
            if (propertyType != null) {
                if (propertyType.getPropertyTypeMetadataList().size() > 0) {
                    isHasPropertyMetadata = true;
                } else if (propertyMetadataList != null) {
                    isHasPropertyMetadata = propertyMetadataList.size() > 0;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return isHasPropertyMetadata;
    }

    public void setPropertyMetadataValue(String key, String value) {
        if (propertyMetadataList == null) {
            propertyMetadataList = new ArrayList<>();
        }

        PropertyMetadata propertyMetadata = getPropertyMetadataForKey(key);

        if (propertyMetadata != null) {
            propertyMetadata.setMetadataValue(value);
        } else {
            // Not found; needs to be created. 
            propertyMetadata = new PropertyMetadata();
            propertyMetadata.setMetadataKey(key);
            propertyMetadata.setMetadataValue(value);
            propertyMetadata.setPropertyValue(this);
            propertyMetadataList.add(propertyMetadata);
        }
    }

    public String getPropertyMetadataValueForKey(String key) {
        PropertyMetadata propertyMetadata = getPropertyMetadataForKey(key);
        if (propertyMetadata != null) {
            return propertyMetadata.getMetadataValue();
        }
        return null;
    }

    public void removePropertyMetadataKey(String key) {
        PropertyMetadata propertyMetadata = getPropertyMetadataForKey(key);
        if (propertyMetadata != null) {
            propertyMetadataList.remove(propertyMetadata);
            isHasPropertyMetadata = null;
        }
    }

    public void removePropertyMetadataKey(PropertyValueMetadata propertyValueMetadata) {
        String key = propertyValueMetadata.propertyMetadata.getMetadataKey();
        propertyValueMetadataList.remove(propertyValueMetadata);
        removePropertyMetadataKey(key);
    }

    public PropertyMetadata getPropertyMetadataForKey(String key) {
        if (propertyMetadataList != null) {
            for (PropertyMetadata propertyMetadata : propertyMetadataList) {
                if (propertyMetadata.getMetadataKey().equals(key)) {
                    return propertyMetadata;
                }
            }
        }
        return null;
    }

    @Override
    public PropertyValue clone() throws CloneNotSupportedException {
        PropertyValue cloned = (PropertyValue) super.clone();
        cloned.id = null;
        cloned.enteredByUser = null;
        cloned.enteredOnDateTime = null;
        cloned.itemElementList = null;
        cloned.itemElementRelationshipList = null;
        cloned.itemConnectorList = null;
        cloned.connectorList = null;
        cloned.propertyValueHistoryList = null;
        
        if (cloned.propertyMetadataList != null) {
            List<PropertyMetadata> pmd = cloned.propertyMetadataList;
            cloned.propertyMetadataList = new ArrayList<>();
            for (PropertyMetadata propertyMetadata : pmd) {
                PropertyMetadata pmetadata = new PropertyMetadata();
                pmetadata.setPropertyValue(cloned);
                pmetadata.setMetadataKey(propertyMetadata.getMetadataKey());
                pmetadata.setMetadataValue(propertyMetadata.getMetadataValue());
                cloned.propertyMetadataList.add(pmetadata); 
            }
        }
        cloned.tag = tag;
        cloned.description = description;
        return cloned;
    }

    public PropertyValue copyAndSetUserInfoAndDate(UserInfo enteredByUser, Date enteredOnDateTime) {
        PropertyValue copied = null;
        try {
            copied = clone();
            copied.enteredByUser = enteredByUser;
            copied.enteredOnDateTime = enteredOnDateTime;
        } catch (CloneNotSupportedException ex) {
            // will not happen 
        }
        return copied;
    }

    public AllowedPropertyValue getSelectedAllowedPropertyValue() {
        if (value != null) {
            for (AllowedPropertyValue apv : getPropertyType().getAllowedPropertyValueList()) {
                String value = apv.getValue();
                if (value.equals(this.value)) {
                    selectedAllowedPropertyValue = apv;
                    break; 
                }
            }
        }
        return selectedAllowedPropertyValue;
    }

    public void setSelectedAllowedPropertyValue(AllowedPropertyValue selectedAllowedPropertyValue) {
        this.selectedAllowedPropertyValue = selectedAllowedPropertyValue;
        if (selectedAllowedPropertyValue != null) {
            this.value = selectedAllowedPropertyValue.getValue(); 
        }
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
        if (!(object instanceof PropertyValue)) {
            return false;
        }
        PropertyValue other = (PropertyValue) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.PropertyValue[ id=" + id + " ]";
    }

    public class PropertyValueMetadata {

        PropertyTypeMetadata propertyTypeMetadata;
        PropertyMetadata propertyMetadata;
        @JsonIgnore
        PropertyValue propertyValue;

        public PropertyValueMetadata(PropertyValue propertyValue, PropertyTypeMetadata propertyTypeMetadata) {
            this.propertyTypeMetadata = propertyTypeMetadata;
            this.propertyValue = propertyValue;

            this.propertyMetadata = propertyValue.getPropertyMetadataForKey(propertyTypeMetadata.getMetadataKey());

            if (this.propertyMetadata == null) {
                String defaultValue = "";
                if (this.propertyTypeMetadata.getIsHaveAllowedValues()) {
                    defaultValue = this.propertyTypeMetadata.getAllowedPropertyMetadataValueList().get(0).getMetadataValue();
                }
                propertyValue.setPropertyMetadataValue(this.propertyTypeMetadata.getMetadataKey(), defaultValue);
                this.propertyMetadata = propertyValue.getPropertyMetadataForKey(propertyTypeMetadata.getMetadataKey());
            }

        }

        public PropertyValueMetadata(PropertyValue propertyValue, PropertyMetadata propertyMetadata) {
            this.propertyMetadata = propertyMetadata;
            this.propertyValue = propertyValue;
        }

        public PropertyTypeMetadata getPropertyTypeMetadata() {
            return propertyTypeMetadata;
        }

        public PropertyMetadata getPropertyMetadata() {
            return propertyMetadata;
        }

        public PropertyValue getPropertyValue() {
            return propertyValue;
        }

        public boolean getIsTrashFunctionalityAvaiable() {
            return propertyTypeMetadata == null;
        }

    }

}
