/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.entities;

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
 * Component instance location history entity class.
 */
@Entity
@Table(name = "component_instance_location_history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentInstanceLocationHistory.findAll", query = "SELECT c FROM ComponentInstanceLocationHistory c"),
    @NamedQuery(name = "ComponentInstanceLocationHistory.findById", query = "SELECT c FROM ComponentInstanceLocationHistory c WHERE c.id = :id"),
    @NamedQuery(name = "ComponentInstanceLocationHistory.findByLocationDetails", query = "SELECT c FROM ComponentInstanceLocationHistory c WHERE c.locationDetails = :locationDetails"),
    @NamedQuery(name = "ComponentInstanceLocationHistory.findByEnteredOnDateTime", query = "SELECT c FROM ComponentInstanceLocationHistory c WHERE c.enteredOnDateTime = :enteredOnDateTime")})
public class ComponentInstanceLocationHistory extends CdbEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 256)
    @Column(name = "location_details")
    private String locationDetails;
    @Basic(optional = false)
    @NotNull
    @Column(name = "entered_on_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enteredOnDateTime;
    @JoinColumn(name = "entered_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserInfo enteredByUser;
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @ManyToOne
    private Location location;
    @JoinColumn(name = "component_instance_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentInstance componentInstance;

    public ComponentInstanceLocationHistory() {
    }

    public ComponentInstanceLocationHistory(Integer id) {
        this.id = id;
    }

    public ComponentInstanceLocationHistory(Integer id, Date enteredOnDateTime) {
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

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public Date getEnteredOnDateTime() {
        return enteredOnDateTime;
    }

    public void setEnteredOnDateTime(Date enteredOnDateTime) {
        this.enteredOnDateTime = enteredOnDateTime;
    }

    public UserInfo getEnteredByUser() {
        return enteredByUser;
    }

    public void setEnteredByUser(UserInfo enteredByUser) {
        this.enteredByUser = enteredByUser;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ComponentInstance getComponentInstance() {
        return componentInstance;
    }

    public void setComponentInstance(ComponentInstance componentInstance) {
        this.componentInstance = componentInstance;
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
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "ComponentInstanceLocationHistory[ id=" + id + " ]";
    }
    
    public void updateFromComponentInstance(ComponentInstance componentInstance) {
        this.componentInstance = componentInstance; 
        this.location = componentInstance.getLocation();
        this.locationDetails = componentInstance.getLocationDetails(); 
        this.enteredByUser = componentInstance.getEntityInfo().getLastModifiedByUser(); 
        this.enteredOnDateTime = componentInstance.getEntityInfo().getLastModifiedOnDateTime(); 
    }
    
}
