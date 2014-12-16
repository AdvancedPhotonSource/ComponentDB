/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "location")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Location.findAll", query = "SELECT l FROM Location l"),
    @NamedQuery(name = "Location.findById", query = "SELECT l FROM Location l WHERE l.id = :id"),
    @NamedQuery(name = "Location.findByName", query = "SELECT l FROM Location l WHERE l.name = :name"),
    @NamedQuery(name = "Location.findByDescription", query = "SELECT l FROM Location l WHERE l.description = :description")})
public class Location extends CloneableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(max = 64)
    private String name;
    @Size(max = 256)
    private String description;
    @OneToMany(mappedBy = "parentLocation")
    private List<Location> locationList;
    @JoinColumn(name = "parent_location_id", referencedColumnName = "id")
    @ManyToOne
    private Location parentLocation;
    @JoinColumn(name = "location_type_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private LocationType locationType;
    @OneToMany(mappedBy = "location")
    private List<ComponentInstance> componentInstanceList;
    @OneToMany(mappedBy = "location")
    private List<DesignElement> designElementList;

    public Location() {
    }

    public Location(Integer id) {
        this.id = id;
    }

    public Location(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
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

    @XmlTransient
    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    public Location getParentLocation() {
        return parentLocation;
    }

    public void setParentLocation(Location parentLocation) {
        this.parentLocation = parentLocation;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    @XmlTransient
    public List<ComponentInstance> getComponentInstanceList() {
        return componentInstanceList;
    }

    public void setComponentInstanceList(List<ComponentInstance> componentInstanceList) {
        this.componentInstanceList = componentInstanceList;
    }

    @XmlTransient
    public List<DesignElement> getDesignElementList() {
        return designElementList;
    }

    public void setDesignElementList(List<DesignElement> designElementList) {
        this.designElementList = designElementList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByName(Location other) {
        if (other != null) {
            return ObjectUtility.equals(this.name, other.name);
        }
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Location)) {
            return false;
        }
        Location other = (Location) object;
        if (this.id == null && other.id == null) {
            return equalsByName(other);
        }

        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(id, name);
        searchResult.doesValueContainPattern("name", name, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        return searchResult;
    }
}
