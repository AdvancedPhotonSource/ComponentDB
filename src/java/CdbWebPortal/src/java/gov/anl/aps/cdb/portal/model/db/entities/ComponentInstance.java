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
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Component instance entity class.
 */
@Entity
@Table(name = "component_instance")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentInstance.findAll", query = "SELECT c FROM ComponentInstance c"),
    @NamedQuery(name = "ComponentInstance.findAllOrderByQrId", query = "SELECT c FROM ComponentInstance c ORDER BY c.qrId"),
    @NamedQuery(name = "ComponentInstance.findById", query = "SELECT c FROM ComponentInstance c WHERE c.id = :id"),
    @NamedQuery(name = "ComponentInstance.findBySerialNumber", query = "SELECT c FROM ComponentInstance c WHERE c.serialNumber = :serialNumber"),
    @NamedQuery(name = "ComponentInstance.findByQrId", query = "SELECT c FROM ComponentInstance c WHERE c.qrId = :qrId"),
    @NamedQuery(name = "ComponentInstance.findByTag", query = "SELECT c FROM ComponentInstance c WHERE c.tag = :tag"),
    @NamedQuery(name = "ComponentInstance.findByLocationDetails", query = "SELECT c FROM ComponentInstance c WHERE c.locationDetails = :locationDetails"),
    @NamedQuery(name = "ComponentInstance.findByDescription", query = "SELECT c FROM ComponentInstance c WHERE c.description = :description")})
public class ComponentInstance extends CdbEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 64)
    private String tag;
    @Size(max = 32)
    @Column(name = "serial_number")
    private String serialNumber;
    @Column(name = "qr_id")
    private Integer qrId;
    @Size(max = 256)
    @Column(name = "location_details")
    private String locationDetails;
    @Size(max = 256)
    private String description;
    @JoinTable(name = "component_instance_log", joinColumns = {
        @JoinColumn(name = "component_instance_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")})
    @OrderBy("id DESC")
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Log> logList;
    @JoinTable(name = "component_instance_property", joinColumns = {
        @JoinColumn(name = "component_instance_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "property_value_id", referencedColumnName = "id")})
    @ManyToMany(cascade = CascadeType.ALL)
    private List<PropertyValue> propertyValueList;
    @ManyToMany(mappedBy = "componentInstanceList")
    private List<DesignElement> designElementList;
    @OneToMany(mappedBy = "location")
    private List<ComponentInstanceLocationHistory> componentInstanceLocationHistoryList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentInstance")
    private List<ComponentInstanceLocationHistory> componentInstanceLocationHistoryList1;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private EntityInfo entityInfo;
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @ManyToOne
    private Location location;
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component component;

    private transient List<PropertyValue> imagePropertyList = null;

    private transient final HashMap<Integer, String> propertyValueCacheMap = new HashMap<>();

    // Used to map property type id to property value number
    private static transient HashMap<Integer, Integer> propertyTypeIdIndexMap = new HashMap<>();

    private transient String qrIdDisplay;
    private transient boolean isCloned = false;

    public static void setPropertyTypeIdIndex(Integer index, Integer propertyTypeId) {
        if (propertyTypeId != null) {
            propertyTypeIdIndexMap.put(index, propertyTypeId);
        }
    }

    private transient String selectedLocationName = null;

    public ComponentInstance() {
    }

    public ComponentInstance(Integer id) {
        this.id = id;
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

    @XmlTransient
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    @XmlTransient
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    @XmlTransient
    public List<DesignElement> getDesignElementList() {
        return designElementList;
    }

    public void setDesignElementList(List<DesignElement> designElementList) {
        this.designElementList = designElementList;
    }

    @XmlTransient
    public List<ComponentInstanceLocationHistory> getComponentInstanceLocationHistoryList() {
        return componentInstanceLocationHistoryList;
    }

    public void setComponentInstanceLocationHistoryList(List<ComponentInstanceLocationHistory> componentInstanceLocationHistoryList) {
        this.componentInstanceLocationHistoryList = componentInstanceLocationHistoryList;
    }

    @XmlTransient
    public List<ComponentInstanceLocationHistory> getComponentInstanceLocationHistoryList1() {
        return componentInstanceLocationHistoryList1;
    }

    public void setComponentInstanceLocationHistoryList1(List<ComponentInstanceLocationHistory> componentInstanceLocationHistoryList1) {
        this.componentInstanceLocationHistoryList1 = componentInstanceLocationHistoryList1;
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getQrId() {
        return qrId;
    }

    public void setQrId(Integer qrId) {
        this.qrId = qrId;
        qrIdDisplay = null;
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

    public String getPropertyValueByIndex(Integer index) {
        Integer propertyTypeId = propertyTypeIdIndexMap.get(index);
        if (propertyTypeId != null) {
            return propertyValueCacheMap.get(propertyTypeId);
        }
        return null;
    }

    public void setPropertyValueByIndex(Integer index, String propertyValue) {
        if (index == null) {
            return;
        }
        Integer propertyTypeId = propertyTypeIdIndexMap.get(index);
        if (propertyTypeId != null) {
            propertyValueCacheMap.put(propertyTypeId, propertyValue);
        }
    }

    public String getPropertyValue1() {
        return getPropertyValueByIndex(1);
    }

    public void setPropertyValue1(String propertyValue1) {
        setPropertyValueByIndex(1, propertyValue1);
    }

    public String getPropertyValue2() {
        return getPropertyValueByIndex(2);
    }

    public void setPropertyValue2(String propertyValue2) {
        setPropertyValueByIndex(2, propertyValue2);
    }

    public String getPropertyValue3() {
        return getPropertyValueByIndex(3);
    }

    public void setPropertyValue3(String propertyValue3) {
        setPropertyValueByIndex(3, propertyValue3);
    }

    public String getPropertyValue4() {
        return getPropertyValueByIndex(4);
    }

    public void setPropertyValue4(String propertyValue4) {
        setPropertyValueByIndex(4, propertyValue4);
    }

    public String getPropertyValue5() {
        return getPropertyValueByIndex(5);
    }

    public void setPropertyValue5(String propertyValue5) {
        setPropertyValueByIndex(5, propertyValue5);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByLocationAndTag(ComponentInstance other) {
        if (other != null) {
            return (ObjectUtility.equals(this.location, other.location)
                    && ObjectUtility.equals(this.tag, other.tag));
        }
        return false;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentInstance)) {
            return false;
        }
        ComponentInstance other = (ComponentInstance) object;
        if (this.id == null && other.id == null) {
            return equalsByLocationAndTag(other);
        }

        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return "ComponentInstance[ id=" + id + " ]";
    }

    public void clearPropertyValueCache() {
        propertyValueCacheMap.clear();
    }

    public String getPropertyValue(Integer propertyTypeId) {
        if (propertyTypeId == null) {
            return null;
        }
        String cachedValue = propertyValueCacheMap.get(propertyTypeId);
        if (cachedValue == null) {
            String delimiter = "";
            cachedValue = "";
            for (PropertyValue propertyValue : propertyValueList) {
                if (propertyValue.getPropertyType().getId().equals(propertyTypeId)) {
                    String value = propertyValue.getValue();
                    if (value != null && !value.isEmpty()) {
                        cachedValue += delimiter + value;
                        delimiter = "|";
                    }
                }
            }
            propertyValueCacheMap.put(propertyTypeId, cachedValue);
        }
        return cachedValue;
    }

    public String getSelectedLocationName() {
        return selectedLocationName;
    }

    public void setSelectedLocationName(String selectedLocationName) {
        this.selectedLocationName = selectedLocationName;
    }

    public void resetAttributesToNullIfEmpty() {
        if (tag != null && tag.isEmpty()) {
            tag = null;
        }
        if (serialNumber != null && serialNumber.isEmpty()) {
            serialNumber = null;
        }
    }

    public void updateDynamicProperties(UserInfo enteredByUser, Date enteredOnDateTime) {
        if (isCloned) {
            // Only update properties for non-cloned instances
            return;
        }
        List<PropertyValue> componentInstancePropertyList = getPropertyValueList();
        if (componentInstancePropertyList == null) {
            componentInstancePropertyList = new ArrayList<>();
        }
        List<PropertyValue> componentPropertyList = component.getPropertyValueList();
        for (PropertyValue propertyValue : componentPropertyList) {
            if (propertyValue.getIsDynamic()) {
                PropertyValue propertyValue2 = propertyValue.copyAndSetUserInfoAndDate(enteredByUser, enteredOnDateTime);
                componentInstancePropertyList.add(propertyValue2);
            }
        }
        setPropertyValueList(componentInstancePropertyList);
    }

    public static String formatQrIdDisplay(Integer qrId) {
        String qrIdDisplay = null;
        if (qrId != null) {
            qrIdDisplay = String.format("%09d", qrId);
            qrIdDisplay = qrIdDisplay.substring(0, 3) + " " + qrIdDisplay.substring(3, 6) + " " + qrIdDisplay.substring(6, 9);
        }
        return qrIdDisplay;
    }

    public String getQrIdDisplay() {
        if (qrId != null && qrIdDisplay == null) {
            qrIdDisplay = formatQrIdDisplay(qrId);
        }
        return qrIdDisplay;
    }

    @Override
    public ComponentInstance clone() throws CloneNotSupportedException {
        ComponentInstance cloned = (ComponentInstance) super.clone();
        cloned.id = null;
        cloned.qrId = null;
        cloned.serialNumber = null;
        cloned.tag = tag;
        cloned.description = description;
        cloned.designElementList = null;
        cloned.logList = null;
        cloned.componentInstanceLocationHistoryList = null;
        cloned.componentInstanceLocationHistoryList1 = null;
        cloned.imagePropertyList = null;
        cloned.propertyValueList = null;
        cloned.entityInfo = null;
        cloned.isCloned = true;
        return cloned;
    }

    public ComponentInstance copyAndSetEntityInfo(EntityInfo entityInfo) {
        ComponentInstance copied = null;
        try {
            copied = clone();
            copied.entityInfo = entityInfo;
            copied.propertyValueList = new ArrayList<>();
            for (PropertyValue propertyValue : propertyValueList) {
                PropertyValue propertyValue2 = propertyValue.copyAndSetUserInfoAndDate(entityInfo.getLastModifiedByUser(), entityInfo.getLastModifiedOnDateTime());
                copied.propertyValueList.add(propertyValue2);
            }
        } catch (CloneNotSupportedException ex) {
            // will not happen
        }
        return copied;
    }

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(id, id.toString());
        searchResult.doesValueContainPattern("tag", tag, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        searchResult.doesValueContainPattern("qrId", qrId, searchPattern);
        searchResult.doesValueContainPattern("serialNumber", serialNumber, searchPattern);
        LogUtility.searchLogList(logList, searchPattern, searchResult);
        PropertyValueUtility.searchPropertyValueList(propertyValueList, searchPattern, searchResult);
        EntityInfoUtility.searchEntityInfo(entityInfo, searchPattern, searchResult);
        return searchResult;
    }
}
