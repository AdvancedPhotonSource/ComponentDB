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

import gov.anl.aps.cdb.portal.constants.DesignElementType;
import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
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
 * Design element entity class.
 */
@Entity
@Table(name = "design_element")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DesignElement.findAll", query = "SELECT d FROM DesignElement d"),
    @NamedQuery(name = "DesignElement.findById", query = "SELECT d FROM DesignElement d WHERE d.id = :id"),
    @NamedQuery(name = "DesignElement.findByName", query = "SELECT d FROM DesignElement d WHERE d.name = :name"),
    @NamedQuery(name = "DesignElement.findByNameAndParentDesign", query = "SELECT d FROM DesignElement d WHERE d.name = :name and d.parentDesign = :parentDesign"),
    @NamedQuery(name = "DesignElement.findByDescription", query = "SELECT d FROM DesignElement d WHERE d.description = :description"),
    @NamedQuery(name = "DesignElement.findBySortOrder", query = "SELECT d FROM DesignElement d WHERE d.sortOrder = :sortOrder")})
public class DesignElement extends CdbEntity {

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
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "sort_order")
    private Float sortOrder;
    @JoinTable(name = "design_element_log", joinColumns = {
        @JoinColumn(name = "design_element_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")})
    @OrderBy("id DESC")
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Log> logList;
    @JoinTable(name = "design_element_component_instance", joinColumns = {
        @JoinColumn(name = "design_element_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "component_instance_id", referencedColumnName = "id")})
    @ManyToMany
    private List<ComponentInstance> componentInstanceList;
    @JoinTable(name = "design_element_property", joinColumns = {
        @JoinColumn(name = "design_element_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "property_value_id", referencedColumnName = "id")})
    @ManyToMany(cascade = CascadeType.ALL)
    private List<PropertyValue> propertyValueList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "secondDesignElementId")
    private List<DesignElementConnection> designElementConnectionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "firstDesignElementId")
    private List<DesignElementConnection> designElementConnectionList1;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EntityInfo entityInfo;
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @ManyToOne
    private Location location;
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    @ManyToOne
    private Component component;
    @JoinColumn(name = "child_design_id", referencedColumnName = "id")
    @ManyToOne
    private Design childDesign;
    @JoinColumn(name = "parent_design_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Design parentDesign;

    private transient boolean isCloned = false;
    private transient List<PropertyValue> imagePropertyList = null;

    public DesignElement() {
    }

    public DesignElement(Integer id) {
        this.id = id;
    }

    public DesignElement(Integer id, String name) {
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

    public Float getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Float sortOrder) {
        this.sortOrder = sortOrder;
    }

    @XmlTransient
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    @XmlTransient
    public List<ComponentInstance> getComponentInstanceList() {
        return componentInstanceList;
    }

    public void setComponentInstanceList(List<ComponentInstance> componentInstanceList) {
        this.componentInstanceList = componentInstanceList;
    }

    @XmlTransient
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    @XmlTransient
    public List<DesignElementConnection> getDesignElementConnectionList() {
        return designElementConnectionList;
    }

    public void setDesignElementConnectionList(List<DesignElementConnection> designElementConnectionList) {
        this.designElementConnectionList = designElementConnectionList;
    }

    @XmlTransient
    public List<DesignElementConnection> getDesignElementConnectionList1() {
        return designElementConnectionList1;
    }

    public void setDesignElementConnectionList1(List<DesignElementConnection> designElementConnectionList1) {
        this.designElementConnectionList1 = designElementConnectionList1;
    }

    public List<PropertyValue> getImagePropertyList() {
        return imagePropertyList;
    }

    public void setImagePropertyList(List<PropertyValue> imagePropertyList) {
        this.imagePropertyList = imagePropertyList;
    }

    public void resetImagePropertyList() {
        this.imagePropertyList = null;
    }

    @Override
    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Design getChildDesign() {
        return childDesign;
    }

    public void setChildDesign(Design childDesign) {
        this.childDesign = childDesign;
    }

    public Design getParentDesign() {
        return parentDesign;
    }

    public void setParentDesign(Design parentDesign) {
        this.parentDesign = parentDesign;
    }

    public String getContainedObjectName() {
        if (component != null) {
            return component.getName();
        }
        if (childDesign != null) {
            return childDesign.getName();
        }
        return null;
    }

    public DesignElementType getContainedObjectType() {
        if (component != null) {
            return DesignElementType.COMPONENT;
        }
        if (childDesign != null) {
            return DesignElementType.DESIGN;
        }
        return null;
    }

    public Integer getContainedObjectId() {
        if (component != null) {
            return component.getId();
        }
        if (childDesign != null) {
            return childDesign.getId();
        }
        return null;
    }

    public ComponentType getContainedObjectComponentType() {
        if (component != null) {
            return component.getComponentType();
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByNameAndComponentAndChildDesignAndLocation(DesignElement other) {
        if (other != null) {
            return (ObjectUtility.equals(this.name, other.name)
                    && ObjectUtility.equals(this.component, other.component)
                    && ObjectUtility.equals(this.childDesign, other.childDesign)
                    && ObjectUtility.equals(this.location, other.location));
        }
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DesignElement)) {
            return false;
        }
        DesignElement other = (DesignElement) object;
        if (this.id == null && other.id == null) {
            return equalsByNameAndComponentAndChildDesignAndLocation(other);
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

    public void updateDynamicProperties(UserInfo enteredByUser, Date enteredOnDateTime) {
        if (isCloned) {
            // Only update properties for non-cloned elements
            return;
        }

        List<PropertyValue> inheritedPropertyList = null;
        if (component != null) {
            inheritedPropertyList = component.getPropertyValueList();
        } else if (childDesign != null) {
            inheritedPropertyList = childDesign.getPropertyValueList();
        }

        if (inheritedPropertyList != null) {
            List<PropertyValue> designElementPropertyList = getPropertyValueList();
            if (designElementPropertyList == null) {
                designElementPropertyList = new ArrayList<>();
            }
            for (PropertyValue propertyValue : inheritedPropertyList) {
                if (propertyValue.getIsDynamic()) {
                    PropertyValue propertyValue2 = propertyValue.copyAndSetUserInfoAndDate(enteredByUser, enteredOnDateTime);
                    designElementPropertyList.add(propertyValue2);
                }
            }
            setPropertyValueList(designElementPropertyList);
        }
    }

    @Override
    public DesignElement clone() throws CloneNotSupportedException {
        DesignElement cloned = (DesignElement) super.clone();
        cloned.id = null;
        cloned.name = "Cloned from: " + name;
        cloned.description = description;
        cloned.logList = null;
        cloned.imagePropertyList = null;
        cloned.propertyValueList = null;
        cloned.componentInstanceList = null;
        cloned.designElementConnectionList = null;
        cloned.designElementConnectionList1 = null;
        cloned.entityInfo = null;
        cloned.isCloned = true;
        return cloned;
    }

    public DesignElement copyAndSetParentDesign(Design parentDesign) {
        DesignElement copied = null;
        EntityInfo copiedEntityInfo = parentDesign.getEntityInfo();
        try {
            copied = clone();
            copied.parentDesign = parentDesign;
            copied.entityInfo = copiedEntityInfo;
            copied.propertyValueList = new ArrayList<>();
            for (PropertyValue propertyValue : propertyValueList) {
                PropertyValue propertyValue2 = propertyValue.copyAndSetUserInfoAndDate(copiedEntityInfo.getLastModifiedByUser(), copiedEntityInfo.getLastModifiedOnDateTime());
                copied.propertyValueList.add(propertyValue2);
            }
        } catch (CloneNotSupportedException ex) {
            // will not happen
        }
        return copied;
    }

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(id, name);
        searchResult.doesValueContainPattern("name", name, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        LogUtility.searchLogList(logList, searchPattern, searchResult);
        PropertyValueUtility.searchPropertyValueList(propertyValueList, searchPattern, searchResult);
        if (component != null) {
            String componentKey = "component/name";
            searchResult.doesValueContainPattern(componentKey, component.getName(), searchPattern);
        }
        if (childDesign != null) {
            String childDesignKey = "childDesign/name";
            searchResult.doesValueContainPattern(childDesignKey, childDesign.getName(), searchPattern);

        }
        return searchResult;
    }
}
