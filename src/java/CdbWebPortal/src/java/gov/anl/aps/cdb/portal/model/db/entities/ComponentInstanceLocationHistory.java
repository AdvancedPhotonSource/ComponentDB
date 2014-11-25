/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.model.db.entities;

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
@Table(name = "component_instance_location_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentInstanceLocationHistory.findAll", query = "SELECT c FROM ComponentInstanceLocationHistory c"),
    @NamedQuery(name = "ComponentInstanceLocationHistory.findById", query = "SELECT c FROM ComponentInstanceLocationHistory c WHERE c.id = :id"),
    @NamedQuery(name = "ComponentInstanceLocationHistory.findByLocationDetails", query = "SELECT c FROM ComponentInstanceLocationHistory c WHERE c.locationDetails = :locationDetails"),
    @NamedQuery(name = "ComponentInstanceLocationHistory.findByDescription", query = "SELECT c FROM ComponentInstanceLocationHistory c WHERE c.description = :description"),
    @NamedQuery(name = "ComponentInstanceLocationHistory.findByEnteredOnDateTime", query = "SELECT c FROM ComponentInstanceLocationHistory c WHERE c.enteredOnDateTime = :enteredOnDateTime")})
public class ComponentInstanceLocationHistory implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 256)
    @Column(name = "location_details")
    private String locationDetails;
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
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @ManyToOne
    private ComponentInstance locationId;
    @JoinColumn(name = "component_instance_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentInstance componentInstanceId;

    public ComponentInstanceLocationHistory() {
    }

    public ComponentInstanceLocationHistory(Integer id) {
        this.id = id;
    }

    public ComponentInstanceLocationHistory(Integer id, Date enteredOnDateTime) {
        this.id = id;
        this.enteredOnDateTime = enteredOnDateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
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

    public ComponentInstance getLocationId() {
        return locationId;
    }

    public void setLocationId(ComponentInstance locationId) {
        this.locationId = locationId;
    }

    public ComponentInstance getComponentInstanceId() {
        return componentInstanceId;
    }

    public void setComponentInstanceId(ComponentInstance componentInstanceId) {
        this.componentInstanceId = componentInstanceId;
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
        if (!(object instanceof ComponentInstanceLocationHistory)) {
            return false;
        }
        ComponentInstanceLocationHistory other = (ComponentInstanceLocationHistory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.entities.ComponentInstanceLocationHistory[ id=" + id + " ]";
    }
    
}
