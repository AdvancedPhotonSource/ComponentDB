/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.utilities.HttpLinkUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
 * @author djarosz
 */
@Entity
@Cacheable(true)
@Table(name = "source")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Source.findAll", query = "SELECT s FROM Source s"),
    @NamedQuery(name = "Source.findById", query = "SELECT s FROM Source s WHERE s.id = :id"),
    @NamedQuery(name = "Source.findByName", query = "SELECT s FROM Source s WHERE s.name = :name"),
    @NamedQuery(name = "Source.findByDescription", query = "SELECT s FROM Source s WHERE s.description = :description"),
    @NamedQuery(name = "Source.findByContactInfo", query = "SELECT s FROM Source s WHERE s.contactInfo = :contactInfo"),
    @NamedQuery(name = "Source.findByUrl", query = "SELECT s FROM Source s WHERE s.url = :url")})
public class Source extends CdbEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    private String name;
    @Size(max = 256)
    private String description;
    @Size(max = 64)
    @Column(name = "contact_info")
    private String contactInfo;
    @Size(max = 256)
    private String url;
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "source")
    private List<ItemSource> itemSourceList;

    private transient String targetUrl;
    private transient String displayUrl;
    
    private transient String importItemName;

    public Source() {
    }

    public Source(Integer id) {
        this.id = id;
    }

    public Source(Integer id, String name) {
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

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.targetUrl = HttpLinkUtility.prepareHttpLinkTargetValue(url);
        this.displayUrl = HttpLinkUtility.prepareHttpLinkDisplayValue(url);
    }

    @XmlTransient
    public List<ItemSource> getItemSourceList() {
        return itemSourceList;
    }

    public void setItemSourceList(List<ItemSource> itemSourceList) {
        this.itemSourceList = itemSourceList;
    }

    public String getTargetUrl() {
        if (targetUrl == null && url != null) {
            targetUrl = HttpLinkUtility.prepareHttpLinkTargetValue(url);
        }
        return targetUrl;
    }

    public String getDisplayUrl() {
        if (displayUrl == null && url != null) {
            displayUrl = HttpLinkUtility.prepareHttpLinkDisplayValue(url);
        }
        return displayUrl;
    }
    
    @Override
    public SearchResult search(Pattern searchPattern) {
        SearchResult searchResult = new SearchResult(this, id, name);
        searchResult.doesValueContainPattern("name", name, searchPattern);
        searchResult.doesValueContainPattern("description", description, searchPattern);
        return searchResult;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Source)) {
            return false;
        }
        Source other = (Source) object;
        
        if ((this.id == null) && (other.id == null)) {
            // neither object has an id, so compare by names
            if ((this.name == null) && (other.name == null)) {
                return true;
            } else if ((this.name == null) ^ (other.name == null)) {
                return false;
            } else {
                return this.name.equals(other.name);
            }
        }
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            // at least on object has a non-null id, so compare by id
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (getName() != null && getName().isEmpty() == false) {
            return getName();
        } else {
            return "gov.anl.aps.cdb.portal.model.db.entities.Source[ id=" + id + " ]";
        }
    }

    @JsonIgnore
    public String getImportItemName() {
        return importItemName;
    }

    public void setImportItemName(String importItemName) {
        this.importItemName = importItemName;
    }

}
