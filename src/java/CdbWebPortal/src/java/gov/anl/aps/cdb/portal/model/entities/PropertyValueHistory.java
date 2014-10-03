/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "property_value_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PropertyValueHistory.findAll", query = "SELECT p FROM PropertyValueHistory p"),
    @NamedQuery(name = "PropertyValueHistory.findById", query = "SELECT p FROM PropertyValueHistory p WHERE p.id = :id"),
    @NamedQuery(name = "PropertyValueHistory.findByValue", query = "SELECT p FROM PropertyValueHistory p WHERE p.value = :value"),
    @NamedQuery(name = "PropertyValueHistory.findByUnits", query = "SELECT p FROM PropertyValueHistory p WHERE p.units = :units"),
    @NamedQuery(name = "PropertyValueHistory.findByDescription", query = "SELECT p FROM PropertyValueHistory p WHERE p.description = :description"),
    @NamedQuery(name = "PropertyValueHistory.findByEnteredOnDateTime", query = "SELECT p FROM PropertyValueHistory p WHERE p.enteredOnDateTime = :enteredOnDateTime")})
public class PropertyValueHistory implements Serializable
{
    private static final long serialVersionUID = 1L;
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
    @Basic(optional = false)
    @NotNull
    @Column(name = "entered_on_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enteredOnDateTime;
    @JoinColumn(name = "entered_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserInfo enteredByUserId;
    @JoinColumn(name = "property_value_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private PropertyValue propertyValueId;

    public PropertyValueHistory() {
    }

    public PropertyValueHistory(Integer id) {
        this.id = id;
    }

    public PropertyValueHistory(Integer id, Date enteredOnDateTime) {
        this.id = id;
        this.enteredOnDateTime = enteredOnDateTime;
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

    public UserInfo getEnteredByUserId() {
        return enteredByUserId;
    }

    public void setEnteredByUserId(UserInfo enteredByUserId) {
        this.enteredByUserId = enteredByUserId;
    }

    public PropertyValue getPropertyValueId() {
        return propertyValueId;
    }

    public void setPropertyValueId(PropertyValue propertyValueId) {
        this.propertyValueId = propertyValueId;
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
        if (!(object instanceof PropertyValueHistory)) {
            return false;
        }
        PropertyValueHistory other = (PropertyValueHistory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.entities.PropertyValueHistory[ id=" + id + " ]";
    }
    
}
