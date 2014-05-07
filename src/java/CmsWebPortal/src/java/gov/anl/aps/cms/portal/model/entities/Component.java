/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cms.portal.model.entities;

import gov.anl.aps.cms.portal.utilities.CollectionUtility;
import gov.anl.aps.cms.portal.utilities.ObjectUtility;
import gov.anl.aps.cms.portal.utilities.SearchResult;
import java.text.DecimalFormat;
import java.util.LinkedList;
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
 *
 * @author sveseli
 */
@Entity
@Table(name = "component")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Component.findAll", query = "SELECT c FROM Component c"),
    @NamedQuery(name = "Component.findById", query = "SELECT c FROM Component c WHERE c.id = :id"),
    @NamedQuery(name = "Component.findByName", query = "SELECT c FROM Component c WHERE c.name = :name"),
    @NamedQuery(name = "Component.findByDescription", query = "SELECT c FROM Component c WHERE c.description = :description"),
    @NamedQuery(name = "Component.findByDocumentationUri", query = "SELECT c FROM Component c WHERE c.documentationUri = :documentationUri"),
    @NamedQuery(name = "Component.findByEstimatedCost", query = "SELECT c FROM Component c WHERE c.estimatedCost = :estimatedCost")})
public class Component extends CloneableEntity
{

    private static final DecimalFormat EstimatedCostDecimalFormat = new DecimalFormat(".00");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;

    @Size(max = 256)
    @Column(name = "description")
    private String description;
    @Size(max = 256)
    @Column(name = "documentation_uri")
    private String documentationUri;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "estimated_cost")
    private Float estimatedCost;

    @JoinTable(name = "component_log", joinColumns = {
        @JoinColumn(name = "component_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")})
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Log> logList;

    @JoinTable(name = "component_component_type", joinColumns = {
        @JoinColumn(name = "component_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "component_type_id", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.EAGER)
    private List<ComponentType> componentTypeList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "component")
    private List<ComponentConnector> componentConnectorList;

    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private EntityInfo entityInfo;

    @JoinColumn(name = "component_state_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentState componentState;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<DesignComponent> designComponentList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "designId")
    private List<DesignComponent> designComponentList1;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "component")
    private List<CollectionComponent> collectionComponentList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<ComponentInstance> componentInstanceList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentId")
    private List<AssemblyComponent> assemblyComponentList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assemblyId")
    private List<AssemblyComponent> assemblyComponentList1;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "component")
    private List<ComponentSource> componentSourceList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "component")
    private List<ComponentProperty> componentPropertyList;

    private transient List<ComponentTypeCategory> componentTypeCategoryList = null;

    public Component() {
    }

    public Component(Integer id) {
        this.id = id;
    }

    public Component(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public String getDocumentationUri() {
        return documentationUri;
    }

    public String getDocumentationUriShortDisplay() {
        if (documentationUri == null) {
            return null;
        }

        // Use first three letters of URL host
        String display = documentationUri;
        String urlDelimiter = "//";
        int pos = documentationUri.indexOf(urlDelimiter);
        if (pos >= 0) {
            pos += urlDelimiter.length();
            display = display.substring(pos, pos + 3);
        }
        return display.toUpperCase();
    }

    public void setDocumentationUri(String documentationUri) {
        this.documentationUri = documentationUri;
    }

    public Float getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Float estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getDisplayEstimatedCost() {
        if (estimatedCost == null) {
            return null;
        }
        return "$ " + EstimatedCostDecimalFormat.format(estimatedCost);
    }

    @XmlTransient
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    @XmlTransient
    public List<ComponentType> getComponentTypeList() {
        return componentTypeList;
    }

    public void setComponentTypeList(List<ComponentType> componentTypeList) {
        CollectionUtility.removeNullReferencesFromList(componentTypeList);
        this.componentTypeList = componentTypeList;
        updateComponentTypeCategoryList();
    }

    private void updateComponentTypeCategoryList() {
        componentTypeCategoryList = new LinkedList<>();
        for (ComponentType componentType : getComponentTypeList()) {
            ComponentTypeCategory componentTypeCategory = componentType.getComponentTypeCategory();
            if (componentTypeCategory != null && !componentTypeCategoryList.contains(componentTypeCategory)) {
                componentTypeCategoryList.add(componentTypeCategory);
            }
        }
    }

    @XmlTransient
    public List<ComponentTypeCategory> getComponentTypeCategoryList() {
        if (componentTypeCategoryList == null) {
            updateComponentTypeCategoryList();
        }
        return componentTypeCategoryList;
    }

    @XmlTransient
    public List<ComponentConnector> getComponentConnectorList() {
        return componentConnectorList;
    }

    public void setComponentConnectorList(List<ComponentConnector> componentConnectorList) {
        this.componentConnectorList = componentConnectorList;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public ComponentState getComponentState() {
        return componentState;
    }

    public void setComponentState(ComponentState componentState) {
        this.componentState = componentState;
    }

    @XmlTransient
    public List<DesignComponent> getDesignComponentList() {
        return designComponentList;
    }

    public void setDesignComponentList(List<DesignComponent> designComponentList) {
        this.designComponentList = designComponentList;
    }

    @XmlTransient
    public List<DesignComponent> getDesignComponentList1() {
        return designComponentList1;
    }

    public void setDesignComponentList1(List<DesignComponent> designComponentList1) {
        this.designComponentList1 = designComponentList1;
    }

    @XmlTransient
    public List<CollectionComponent> getCollectionComponentList() {
        return collectionComponentList;
    }

    public void setCollectionComponentList(List<CollectionComponent> collectionComponentList) {
        this.collectionComponentList = collectionComponentList;
    }

    @XmlTransient
    public List<ComponentInstance> getComponentInstanceList() {
        return componentInstanceList;
    }

    public void setComponentInstanceList(List<ComponentInstance> componentInstanceList) {
        this.componentInstanceList = componentInstanceList;
    }

    @XmlTransient
    public List<AssemblyComponent> getAssemblyComponentList() {
        return assemblyComponentList;
    }

    public void setAssemblyComponentList(List<AssemblyComponent> assemblyComponentList) {
        this.assemblyComponentList = assemblyComponentList;
    }

    @XmlTransient
    public List<AssemblyComponent> getAssemblyComponentList1() {
        return assemblyComponentList1;
    }

    public void setAssemblyComponentList1(List<AssemblyComponent> assemblyComponentList1) {
        this.assemblyComponentList1 = assemblyComponentList1;
    }

    @XmlTransient
    public List<ComponentSource> getComponentSourceList() {
        return componentSourceList;
    }

    public void setComponentSourceList(List<ComponentSource> componentSourceList) {
        this.componentSourceList = componentSourceList;
    }

    @XmlTransient
    public List<ComponentProperty> getComponentPropertyList() {
        return componentPropertyList;
    }

    public void setComponentPropertyList(List<ComponentProperty> componentPropertyList) {
        this.componentPropertyList = componentPropertyList;
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

    @Override
    public Component clone() throws CloneNotSupportedException {
        Component cloned = (Component) super.clone();
        cloned.id = null;
        cloned.name = "Copy Of " + cloned.name;
        //cloned.componentTypeList = null;
        //cloned.componentTypeCategoryList = null;
        //cloned.componentPropertyList = null;
        //cloned.componentSourceList = null;
        cloned.componentConnectorList = null;
        cloned.collectionComponentList = null;
        cloned.componentInstanceList = null;
        cloned.assemblyComponentList = null;
        cloned.assemblyComponentList1 = null;
        cloned.designComponentList = null;
        cloned.designComponentList1 = null;
        for (ComponentProperty componentProperty : cloned.componentPropertyList) {
            componentProperty.setId(null);
            componentProperty.setComponent(cloned);
        }
        for (ComponentSource componentSource : cloned.componentSourceList) {
            componentSource.setId(null);
            componentSource.setComponent(cloned);
        }
//        for (ComponentConnector componentConnector : cloned.componentConnectorList) {
//            componentConnector.setId(null);
//            componentConnector.setComponent(cloned);
//        }
        cloned.entityInfo = null;
        return cloned;
    }

    public String getDisplayComponentTypeAndCategoryList() {
        if (componentTypeList == null || componentTypeList.isEmpty()) {
            return "";
        }
        
        String display = "";
        String delimiter = "";
        for (ComponentType componentType : componentTypeList) {
            display += delimiter + componentType.getNameWithCategory();
            delimiter = ", ";
        }
        return display;
    }

    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(id, name);
        searchResult.doesValueContainPattern("name", name, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        for (Log logEntry : logList) {
            String logEntryKey = "logEntryId:" + logEntry.getId();
            searchResult.doesValueContainPattern(logEntryKey, logEntry.getText(), searchPattern);
        }
        return searchResult;
    }
}
