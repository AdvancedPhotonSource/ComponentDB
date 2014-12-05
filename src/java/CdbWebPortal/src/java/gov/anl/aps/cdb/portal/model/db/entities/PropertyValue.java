/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.utilities.ObjectUtility;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
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
    @NamedQuery(name = "PropertyValue.findByEnteredOnDateTime", query = "SELECT p FROM PropertyValue p WHERE p.enteredOnDateTime = :enteredOnDateTime")})
public class PropertyValue extends CloneableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 64)
    private String tag;
    @Size(max = 64)
    private String value;
    @Size(max = 16)
    private String units;
    @Size(max = 256)
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "entered_on_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enteredOnDateTime;
    @ManyToMany(mappedBy = "propertyValueList")
    private List<Design> designList;
    @ManyToMany(mappedBy = "propertyValueList")
    private List<ComponentInstance> componentInstanceList;
    @ManyToMany(mappedBy = "propertyValueList")
    private List<ComponentConnector> componentConnectorList;
    @ManyToMany(mappedBy = "propertyValueList")
    private List<Component> componentList;
    @ManyToMany(mappedBy = "propertyValueList")
    private List<DesignElement> designElementList;
    @JoinColumn(name = "entered_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserInfo enteredByUser;
    @JoinColumn(name = "property_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private PropertyType propertyType;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "propertyValue")
    private List<PropertyValueHistory> propertyValueHistoryList;

    private transient String displayValue = null;
    private transient String targetValue = null;
    
    public PropertyValue() {
    }

    public PropertyValue(Integer id) {
        this.id = id;
    }

    public PropertyValue(Integer id, Date enteredOnDateTime) {
        this.id = id;
        this.enteredOnDateTime = enteredOnDateTime;
    }

    @Override
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

    public Date getEnteredOnDateTime() {
        return enteredOnDateTime;
    }

    public void setEnteredOnDateTime(Date enteredOnDateTime) {
        this.enteredOnDateTime = enteredOnDateTime;
    }

    @XmlTransient
    public List<Design> getDesignList() {
        return designList;
    }

    public void setDesignList(List<Design> designList) {
        this.designList = designList;
    }

    @XmlTransient
    public List<ComponentInstance> getComponentInstanceList() {
        return componentInstanceList;
    }

    public void setComponentInstanceList(List<ComponentInstance> componentInstanceList) {
        this.componentInstanceList = componentInstanceList;
    }

    @XmlTransient
    public List<ComponentConnector> getComponentConnectorList() {
        return componentConnectorList;
    }

    public void setComponentConnectorList(List<ComponentConnector> componentConnectorList) {
        this.componentConnectorList = componentConnectorList;
    }

    @XmlTransient
    public List<Component> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<Component> componentList) {
        this.componentList = componentList;
    }

    @XmlTransient
    public List<DesignElement> getDesignElementList() {
        return designElementList;
    }

    public void setDesignElementList(List<DesignElement> designElementList) {
        this.designElementList = designElementList;
    }

    public UserInfo getEnteredByUser() {
        return enteredByUser;
    }

    public void setEnteredByUser(UserInfo enteredByUser) {
        this.enteredByUser = enteredByUser;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    @XmlTransient
    public List<PropertyValueHistory> getPropertyValueHistoryList() {
        return propertyValueHistoryList;
    }

    public void setPropertyValueHistoryList(List<PropertyValueHistory> propertyValueHistoryList) {
        this.propertyValueHistoryList = propertyValueHistoryList;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public void setDisplayValueToValue() {
        displayValue = value;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }
    
    public void setTargetValueToValue() {
        targetValue = value;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

   public boolean equalsByValueAndUnits(PropertyValue other) {
        if (other != null) {
            return ( ObjectUtility.equals(this.value, other.value) 
                    && ObjectUtility.equals(this.units, other.units) );
        }
        return false;
    }
   
    public boolean equalsByValueAndUnitsAndDescription(PropertyValue other) {
        if (other != null) {
            return ( ObjectUtility.equals(this.value, other.value) 
                    && ObjectUtility.equals(this.units, other.units) 
                    && ObjectUtility.equals(this.description, other.description) );
        }
        return false;
    }

    public boolean equalsByTagAndValueAndUnitsAndDescription(PropertyValue other) {
        if (other != null) {
            return ( ObjectUtility.equals(this.tag, other.tag) 
                    && ObjectUtility.equals(this.value, other.value) 
                    && ObjectUtility.equals(this.units, other.units) 
                    && ObjectUtility.equals(this.description, other.description) );
        }
        return false;
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PropertyValue)) {
            return false;
        }
        PropertyValue other = (PropertyValue) object;
        if (this.id == null && other.id == null) {
            return equalsByValueAndUnitsAndDescription(other);
        }

        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        if (units != null && !units.isEmpty()) {
            return value + " [" + units + "]";
        }
        else {
            return value;
        }
    }
    
}

