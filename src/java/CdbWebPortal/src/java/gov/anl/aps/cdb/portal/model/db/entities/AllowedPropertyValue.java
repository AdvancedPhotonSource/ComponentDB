package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.common.utilities.ObjectUtility;
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
 *
 * @author sveseli
 */
@Entity
@Table(name = "allowed_property_value")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AllowedPropertyValue.findAll", query = "SELECT a FROM AllowedPropertyValue a"),
    @NamedQuery(name = "AllowedPropertyValue.findById", query = "SELECT a FROM AllowedPropertyValue a WHERE a.id = :id"),
    @NamedQuery(name = "AllowedPropertyValue.findByValue", query = "SELECT a FROM AllowedPropertyValue a WHERE a.value = :value"),
    @NamedQuery(name = "AllowedPropertyValue.findByUnits", query = "SELECT a FROM AllowedPropertyValue a WHERE a.units = :units"),
    @NamedQuery(name = "AllowedPropertyValue.findByDescription", query = "SELECT a FROM AllowedPropertyValue a WHERE a.description = :description"),
    @NamedQuery(name = "AllowedPropertyValue.findBySortOrder", query = "SELECT a FROM AllowedPropertyValue a WHERE a.sortOrder = :sortOrder"),
    @NamedQuery(name = "AllowedPropertyValue.findAllByPropertyTypeId", query = "SELECT a FROM AllowedPropertyValue a WHERE a.propertyType.id = :propertyTypeId")})
public class AllowedPropertyValue extends CdbEntity
{
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
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "sort_order")
    private Float sortOrder;
    @JoinColumn(name = "property_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private PropertyType propertyType;

    public AllowedPropertyValue() {
    }

    public AllowedPropertyValue(Integer id) {
        this.id = id;
    }

    @Override
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

    public Float getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Float sortOrder) {
        this.sortOrder = sortOrder;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByPropertyTypeAndValue(AllowedPropertyValue other) {
        if (other == null) {
            return false;
        }
        
        if (!ObjectUtility.equals(this.propertyType, other.propertyType)) {
            return false;
        }
        
        return ObjectUtility.equals(this.value, other.value);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AllowedPropertyValue)) {
            return false;
        }
        AllowedPropertyValue other = (AllowedPropertyValue) object;
        if (this.id == null && other.id == null) {
            return equalsByPropertyTypeAndValue(other);
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
        return value;        
    }
    
}
