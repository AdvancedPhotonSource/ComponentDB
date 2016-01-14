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

import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Location entity class.
 */
@Entity
@Table(name = "location")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Location.findAll", query = "SELECT l FROM Location l ORDER BY l.name"),
    @NamedQuery(name = "Location.findById", query = "SELECT l FROM Location l WHERE l.id = :id"),
    @NamedQuery(name = "Location.findByName", query = "SELECT l FROM Location l WHERE l.name = :name"),
    @NamedQuery(name = "Location.findByDescription", query = "SELECT l FROM Location l WHERE l.description = :description"),
    @NamedQuery(name = "Location.findByIsUserWriteable", query = "SELECT l FROM Location l WHERE l.isUserWriteable = :isUserWriteable"),
    @NamedQuery(name = "Location.findBySortOrder", query = "SELECT l FROM Location l WHERE l.sortOrder = :sortOrder"),
    @NamedQuery(name = "Location.findLocationsWithoutParents", query = "SELECT l FROM Location l WHERE l.id NOT IN (SELECT l2.id FROM Location l2 JOIN l2.parentLocationList cll2) ORDER BY l.name")})
public class Location extends CdbEntity {

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
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_user_writeable")
    private boolean isUserWriteable;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "sort_order")
    private Float sortOrder;
    @JoinTable(name = "location_link",
            joinColumns = {
                @JoinColumn(name = "child_location_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                @JoinColumn(name = "parent_location_id", referencedColumnName = "id")})
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private List<Location> parentLocationList;
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "parentLocationList")
    private List<Location> childLocationList;
    @JoinColumn(name = "location_type_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private LocationType locationType;
    @OneToMany(mappedBy = "location")
    private List<ComponentInstance> componentInstanceList;
    @OneToMany(mappedBy = "location")
    private List<DesignElement> designElementList;
    @OneToMany(mappedBy = "location")
    private List<ComponentInstanceLocationHistory> componentInstanceLocationHistoryList;
    
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

    public boolean getIsUserWriteable() {
        return isUserWriteable;
    }

    public void setIsUserWriteable(boolean isUserWriteable) {
        this.isUserWriteable = isUserWriteable;
    }

    public Float getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Float sortOrder) {
        this.sortOrder = sortOrder;
    }

    @XmlTransient
    public List<Location> getChildLocationList() {
        return childLocationList;
    }

    public void setChildLocationList(List<Location> childLocationList) {
        this.childLocationList = childLocationList;
    }

    @XmlTransient
    public List<Location> getParentLocationList() {
        return parentLocationList;
    }

    public void setParentLocationList(List<Location> parentLocationList) {
        this.parentLocationList = parentLocationList;
    }

    @XmlTransient
    public List<ComponentInstanceLocationHistory> getComponentInstanceLocationHistoryList() {
        return componentInstanceLocationHistoryList;
    }

    public void setComponentInstanceLocationHistoryList(List<ComponentInstanceLocationHistory> componentInstanceLocationHistoryList) {
        this.componentInstanceLocationHistoryList = componentInstanceLocationHistoryList;
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

    public Location getParentLocation() {
        if (parentLocationList == null || parentLocationList.isEmpty()) {
            return null;
        }
        return parentLocationList.get(0);
    }

    public void resetParentLocation() {
        parentLocationList = null;
    }

    public void setParentLocation(Location parentLocation) {
        Location oldParentLocation = getParentLocation();
        if (oldParentLocation != null) {
            if (oldParentLocation.equals(parentLocation)) {
                return;
            }
            List<Location> oldParentLocationChildList = oldParentLocation.getChildLocationList();
            oldParentLocationChildList.remove(this);
        }

        parentLocationList = new ArrayList<>();
        if (parentLocation != null) {
            parentLocationList.add(parentLocation);
            List<Location> newParentLocationChildList = parentLocation.getChildLocationList();
            if (newParentLocationChildList == null) {
                newParentLocationChildList = new ArrayList<>();
                parentLocation.setChildLocationList(newParentLocationChildList);
            }
            newParentLocationChildList.add(this);
        }
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
