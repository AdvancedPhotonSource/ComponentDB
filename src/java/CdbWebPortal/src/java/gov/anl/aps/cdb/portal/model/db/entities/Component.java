/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.utilities.CollectionUtility;
import gov.anl.aps.cdb.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OrderBy;
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
@Table(name = "component")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Component.findAll", query = "SELECT c FROM Component c ORDER BY c.name"),
    @NamedQuery(name = "Component.findById", query = "SELECT c FROM Component c WHERE c.id = :id"),
    @NamedQuery(name = "Component.findByName", query = "SELECT c FROM Component c WHERE c.name = :name"),
    @NamedQuery(name = "Component.findByDescription", query = "SELECT c FROM Component c WHERE c.description = :description")})
public class Component extends CdbEntity {

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
    @JoinTable(name = "component_log", joinColumns = {
        @JoinColumn(name = "component_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")})
    @OrderBy("id DESC")
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Log> logList;
    @JoinTable(name = "component_property", joinColumns = {
        @JoinColumn(name = "component_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "property_value_id", referencedColumnName = "id")})
    @ManyToMany(cascade = CascadeType.ALL)
    private List<PropertyValue> propertyValueList;
    @OneToMany(mappedBy = "linkComponentId")
    private List<DesignElementConnection> designElementConnectionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "component")
    private List<ComponentConnector> componentConnectorList;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private EntityInfo entityInfo;
    @JoinColumn(name = "component_type_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private ComponentType componentType;
    @OrderBy("qrId ASC")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "component")
    private List<ComponentInstance> componentInstanceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "component")
    private List<AssemblyComponent> assemblyList;
    @OrderBy("sortOrder ASC")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "assembly")
    private List<AssemblyComponent> assemblyComponentList;
    @OneToMany(mappedBy = "component")
    private List<DesignElement> designElementList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "component")
    private List<ComponentSource> componentSourceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<ComponentResource> componentResourceList;

    private transient boolean isCloned = false;
    private transient List<PropertyValue> imagePropertyList = null;

    private transient final HashMap<Integer, String> propertyValueCacheMap = new HashMap<>();

    // Used to map property type id to property value number
    private static transient HashMap<Integer, Integer> propertyTypeIdIndexMap = new HashMap<>();

    public static void setPropertyTypeIdIndex(Integer index, Integer propertyTypeId) {
        if (propertyTypeId != null) {
            propertyTypeIdIndexMap.put(index, propertyTypeId);
        }
    }

    public Component() {
    }

    public Component(Integer id) {
        this.id = id;
    }

    public Component(Integer id, String name) {
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
    public List<DesignElementConnection> getDesignElementConnectionList() {
        return designElementConnectionList;
    }

    public void setDesignElementConnectionList(List<DesignElementConnection> designElementConnectionList) {
        this.designElementConnectionList = designElementConnectionList;
    }

    @XmlTransient
    public List<ComponentConnector> getComponentConnectorList() {
        return componentConnectorList;
    }

    public void setComponentConnectorList(List<ComponentConnector> componentConnectorList) {
        this.componentConnectorList = componentConnectorList;
    }

    @Override
    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    @XmlTransient
    public List<ComponentInstance> getComponentInstanceList() {
        return componentInstanceList;
    }

    public void setComponentInstanceList(List<ComponentInstance> componentInstanceList) {
        this.componentInstanceList = componentInstanceList;
    }

    @XmlTransient
    public List<AssemblyComponent> getAssemblyList() {
        return assemblyList;
    }

    public void setAssemblyList(List<AssemblyComponent> assemblyList) {
        this.assemblyList = assemblyList;
    }

    @XmlTransient
    public List<AssemblyComponent> getAssemblyComponentList() {
        return assemblyComponentList;
    }

    public void setAssemblyComponentList(List<AssemblyComponent> assemblyComponentList) {
        this.assemblyComponentList = assemblyComponentList;
    }

    @XmlTransient
    public List<DesignElement> getDesignElementList() {
        return designElementList;
    }

    public void setDesignElementList(List<DesignElement> designElementList) {
        this.designElementList = designElementList;
    }

    @XmlTransient
    public List<ComponentSource> getComponentSourceList() {
        return componentSourceList;
    }

    public void setComponentSourceList(List<ComponentSource> componentSourceList) {
        this.componentSourceList = componentSourceList;
    }

    @XmlTransient
    public List<ComponentResource> getComponentResourceList() {
        return componentResourceList;
    }

    public void setComponentResourceList(List<ComponentResource> componentResourceList) {
        this.componentResourceList = componentResourceList;
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

    public boolean equalsByName(Component other) {
        if (other != null) {
            return ObjectUtility.equals(this.name, other.name);
        }
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Component)) {
            return false;
        }
        Component other = (Component) object;
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

    public void addComponentTypeProperties() {
        if (isCloned) {
            // this component has been cloned, do nothing
            return;
        }
        propertyValueList = new ArrayList<>();
        for (PropertyType propertyType : componentType.getPropertyTypeList()) {
            PropertyValue propertyValue = new PropertyValue();
            propertyValue.setPropertyType(propertyType);
            propertyValue.setValue(propertyType.getDefaultValue());
            propertyValue.setUnits(propertyType.getDefaultUnits());
            propertyValue.setEnteredByUser(entityInfo.getCreatedByUser());
            propertyValue.setEnteredOnDateTime(entityInfo.getCreatedOnDateTime());
            propertyValueList.add(propertyValue);
        }
    }

    @Override
    public Component clone() throws CloneNotSupportedException {
        Component cloned = (Component) super.clone();
        cloned.id = null;
        cloned.name = "Cloned name: " + cloned.name;
        if (description != null && !description.isEmpty()) {
            cloned.description = "Cloned description: " + description;
        }
        cloned.componentConnectorList = null;
        cloned.componentInstanceList = null;
        cloned.assemblyList = null;
        cloned.assemblyComponentList = null;
        cloned.designElementList = null;
        cloned.componentResourceList = null;
        cloned.logList = null;
        cloned.propertyValueList = null;
        cloned.componentSourceList = null;
        cloned.entityInfo = null;
        cloned.isCloned = true;
        return cloned;
    }

    public Component copyAndSetEntityInfo(EntityInfo entityInfo) {
        Component copied = null;
        try {
            copied = clone();
            copied.entityInfo = entityInfo;
            copied.propertyValueList = new ArrayList<>();
            for (PropertyValue propertyValue : propertyValueList) {
                PropertyValue propertyValue2 = propertyValue.copyAndSetUserInfoAndDate(entityInfo.getLastModifiedByUser(), entityInfo.getLastModifiedOnDateTime());
                copied.propertyValueList.add(propertyValue2);
            }
            copied.componentSourceList = new ArrayList<>();
            for (ComponentSource componentSource : componentSourceList) {
                ComponentSource componentSource2 = componentSource.copyAndSetComponent(copied);
                copied.componentSourceList.add(componentSource2);
            }
            copied.assemblyComponentList = new ArrayList<>();
            for (AssemblyComponent assemblyComponent : assemblyComponentList) {
                AssemblyComponent assemblyComponent2 = assemblyComponent.copyAndSetAssembly(copied);
                copied.assemblyComponentList.add(assemblyComponent2);
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
        for (Log logEntry : logList) {
            String logEntryKey = "log/text/id:" + logEntry.getId();
            searchResult.doesValueContainPattern(logEntryKey, logEntry.getText(), searchPattern);
        }
        for (PropertyValue propertyValue : propertyValueList) {
            String propertyValueKey = "propertyValue/value/id:" + propertyValue.getId();
            searchResult.doesValueContainPattern(propertyValueKey, propertyValue.getValue(), searchPattern);
            propertyValueKey = "propertyValue/description/id:" + propertyValue.getId();
            searchResult.doesValueContainPattern(propertyValueKey, propertyValue.getDescription(), searchPattern);
        }
        return searchResult;
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

    public String getDisplayNameWithTypeAndCategory() {
        String result = name + " [" + componentType.getNameWithCategory() + "]";
        return result;
    }

    public String getComponentSources() {
        if (componentSourceList == null) {
            return "";
        }
        return CollectionUtility.displayItemListWithoutOutsideDelimiters(componentSourceList, ";");
    }
    
    public Boolean getIsAssembly() {
        return (assemblyComponentList != null && !assemblyComponentList.isEmpty());
    }
}
