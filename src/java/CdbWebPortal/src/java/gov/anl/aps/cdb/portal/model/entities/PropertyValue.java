/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.entities;

import java.io.Serializable;
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
    @NamedQuery(name = "PropertyValue.findByValue", query = "SELECT p FROM PropertyValue p WHERE p.value = :value"),
    @NamedQuery(name = "PropertyValue.findByUnits", query = "SELECT p FROM PropertyValue p WHERE p.units = :units"),
    @NamedQuery(name = "PropertyValue.findByDescription", query = "SELECT p FROM PropertyValue p WHERE p.description = :description"),
    @NamedQuery(name = "PropertyValue.findByEnteredOnDateTime", query = "SELECT p FROM PropertyValue p WHERE p.enteredOnDateTime = :enteredOnDateTime")})
public class PropertyValue implements Serializable
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyValueId")
    private List<DesignProperty> designPropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyValue")
    private List<ComponentInstanceProperty> componentInstancePropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyValue")
    private List<ComponentConnectorProperty> componentConnectorPropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyValue")
    private List<ComponentProperty> componentPropertyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyValueId")
    private List<DesignElementProperty> designElementPropertyList;
    @JoinColumn(name = "entered_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserInfo enteredByUserId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propertyValueId")
    private List<PropertyValueHistory> propertyValueHistoryList;

    public PropertyValue() {
    }

    public PropertyValue(Integer id) {
        this.id = id;
    }

    public PropertyValue(Integer id, Date enteredOnDateTime) {
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

    @XmlTransient
    public List<DesignProperty> getDesignPropertyList() {
        return designPropertyList;
    }

    public void setDesignPropertyList(List<DesignProperty> designPropertyList) {
        this.designPropertyList = designPropertyList;
    }

    @XmlTransient
    public List<ComponentInstanceProperty> getComponentInstancePropertyList() {
        return componentInstancePropertyList;
    }

    public void setComponentInstancePropertyList(List<ComponentInstanceProperty> componentInstancePropertyList) {
        this.componentInstancePropertyList = componentInstancePropertyList;
    }

    @XmlTransient
    public List<ComponentConnectorProperty> getComponentConnectorPropertyList() {
        return componentConnectorPropertyList;
    }

    public void setComponentConnectorPropertyList(List<ComponentConnectorProperty> componentConnectorPropertyList) {
        this.componentConnectorPropertyList = componentConnectorPropertyList;
    }

    @XmlTransient
    public List<ComponentProperty> getComponentPropertyList() {
        return componentPropertyList;
    }

    public void setComponentPropertyList(List<ComponentProperty> componentPropertyList) {
        this.componentPropertyList = componentPropertyList;
    }

    @XmlTransient
    public List<DesignElementProperty> getDesignElementPropertyList() {
        return designElementPropertyList;
    }

    public void setDesignElementPropertyList(List<DesignElementProperty> designElementPropertyList) {
        this.designElementPropertyList = designElementPropertyList;
    }

    public UserInfo getEnteredByUserId() {
        return enteredByUserId;
    }

    public void setEnteredByUserId(UserInfo enteredByUserId) {
        this.enteredByUserId = enteredByUserId;
    }

    @XmlTransient
    public List<PropertyValueHistory> getPropertyValueHistoryList() {
        return propertyValueHistoryList;
    }

    public void setPropertyValueHistoryList(List<PropertyValueHistory> propertyValueHistoryList) {
        this.propertyValueHistoryList = propertyValueHistoryList;
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
        return "gov.anl.aps.cdb.portal.model.entities.PropertyValue[ id=" + id + " ]";
    }
    
}
