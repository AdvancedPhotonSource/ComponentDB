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
 * Design entity class.
 */
@Entity
@Table(name = "design")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Design.findAll", query = "SELECT d FROM Design d ORDER BY d.name"),
    @NamedQuery(name = "Design.findById", query = "SELECT d FROM Design d WHERE d.id = :id"),
    @NamedQuery(name = "Design.findByName", query = "SELECT d FROM Design d WHERE d.name = :name"),
    @NamedQuery(name = "Design.findByDescription", query = "SELECT d FROM Design d WHERE d.description = :description")})
public class Design extends CdbDomainEntity {

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
    @JoinTable(name = "design_property", joinColumns = {
        @JoinColumn(name = "design_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "property_value_id", referencedColumnName = "id")})
    @ManyToMany(cascade = CascadeType.ALL)
    private List<PropertyValue> propertyValueList;
    @JoinTable(name = "design_log", joinColumns = {
        @JoinColumn(name = "design_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")})
    @OrderBy("id DESC")
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Log> logList;
    @OrderBy("sortOrder ASC")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "parentDesign")
    private List<DesignElement> designElementList;
    @JoinColumn(name = "entity_info_id", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private EntityInfo entityInfo;

    private transient boolean isCloned = false;

    public Design() {
    }

    public Design(Integer id) {
        this.id = id;
    }

    public Design(Integer id, String name) {
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
    @Override
    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    @XmlTransient
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    @XmlTransient
    public List<DesignElement> getDesignElementList() {
        if (designElementList == null) {
            designElementList = new ArrayList<>();
        }
        return designElementList;
    }

    public void setDesignElementList(List<DesignElement> designElementList) {
        this.designElementList = designElementList;
    }

    @Override
    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    @XmlTransient
    public List<DesignElement> getComponentNodes() {
        ArrayList<DesignElement> componentNodes = new ArrayList<>();
        for (DesignElement designElement : designElementList) {
            if (designElement.getComponent() != null) {
                componentNodes.add(designElement);
            }
        }
        return componentNodes;
    }

    @XmlTransient
    public List<DesignElement> getChildDesignNodes() {
        ArrayList<DesignElement> childDesignNodes = new ArrayList<>();
        for (DesignElement designElement : designElementList) {
            if (designElement.getChildDesign() != null) {
                childDesignNodes.add(designElement);
            }
        }
        return childDesignNodes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByName(Design other) {
        if (other != null) {
            return ObjectUtility.equals(this.name, other.name);
        }
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Design)) {
            return false;
        }
        Design other = (Design) object;
        if (this.id == null && other.id == null) {
            return equalsByName(other);
        }

        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public Design clone() throws CloneNotSupportedException {
        Design cloned = (Design) super.clone();
        cloned.id = null;
        cloned.name = "Cloned from: " + cloned.name;
        cloned.description = description;
        cloned.designElementList = null;
        cloned.logList = null;
        cloned.propertyValueList = null;
        cloned.entityInfo = null;
        cloned.isCloned = true;
        return cloned;
    }

    public Design copyAndSetEntityInfo(EntityInfo entityInfo) {
        Design copied = null;
        try {
            copied = clone();
            copied.entityInfo = entityInfo;
            copied.propertyValueList = new ArrayList<>();
            for (PropertyValue propertyValue : propertyValueList) {
                PropertyValue propertyValue2 = propertyValue.copyAndSetUserInfoAndDate(entityInfo.getLastModifiedByUser(), entityInfo.getLastModifiedOnDateTime());
                copied.propertyValueList.add(propertyValue2);
            }
            copied.designElementList = new ArrayList<>();
            for (DesignElement designElement : designElementList) {
                DesignElement designElement2 = designElement.copyAndSetParentDesign(copied);
                copied.designElementList.add(designElement2);
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
        EntityInfoUtility.searchEntityInfo(entityInfo, searchPattern, searchResult);
        String baseKey;
        for (DesignElement designElement : designElementList) {
            baseKey = "designElement/id:" + designElement.getId();
            String designElementKey = baseKey + "/name";
            searchResult.doesValueContainPattern(designElementKey, designElement.getName(), searchPattern);
            designElementKey = baseKey + "/description";
            searchResult.doesValueContainPattern(designElementKey, designElement.getDescription(), searchPattern);
            Component component = designElement.getComponent();
            if (component != null) {
                designElementKey = baseKey + "/component/name";
                searchResult.doesValueContainPattern(designElementKey, component.getName(), searchPattern);
            }
            Design childDesign = designElement.getChildDesign();
            if (childDesign != null) {
                designElementKey = baseKey + "/childDesign/name";
                searchResult.doesValueContainPattern(designElementKey, childDesign.getName(), searchPattern);
            }
        }
        return searchResult;
    }
}
