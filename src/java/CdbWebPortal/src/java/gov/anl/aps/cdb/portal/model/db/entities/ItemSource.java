/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.utilities.HttpLinkUtility;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author djarosz
 */
@Entity
@Cacheable(true)
@Table(name = "item_source")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemSource.findAll", query = "SELECT i FROM ItemSource i"),
    @NamedQuery(name = "ItemSource.findById", query = "SELECT i FROM ItemSource i WHERE i.id = :id"),
    @NamedQuery(name = "ItemSource.findByPartNumber", query = "SELECT i FROM ItemSource i WHERE i.partNumber = :partNumber"),
    @NamedQuery(name = "ItemSource.findByCost", query = "SELECT i FROM ItemSource i WHERE i.cost = :cost"),
    @NamedQuery(name = "ItemSource.findByDescription", query = "SELECT i FROM ItemSource i WHERE i.description = :description"),
    @NamedQuery(name = "ItemSource.findByIsVendor", query = "SELECT i FROM ItemSource i WHERE i.isVendor = :isVendor"),
    @NamedQuery(name = "ItemSource.findByIsManufacturer", query = "SELECT i FROM ItemSource i WHERE i.isManufacturer = :isManufacturer"),
    @NamedQuery(name = "ItemSource.findByContactInfo", query = "SELECT i FROM ItemSource i WHERE i.contactInfo = :contactInfo"),
    @NamedQuery(name = "ItemSource.findByUrl", query = "SELECT i FROM ItemSource i WHERE i.url = :url")})
public class ItemSource extends CdbEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Size(max = 64)
    @Column(name = "part_number")
    private String partNumber;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    private Float cost;    
    @Size(max = 256)
    private String description;    
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_vendor")
    private boolean isVendor;    
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_manufacturer")    
    private boolean isManufacturer;
    @Size(max = 64)
    @Column(name = "contact_info")    
    private String contactInfo;
    @Size(max = 256)    
    private String url;
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    @ManyToOne(optional = false)        
    private Item item;
    @JoinColumn(name = "source_id", referencedColumnName = "id")
    @ManyToOne(optional = false)   
    private Source source;
        
    private transient String targetUrl;    
    private transient String displayUrl;

    public ItemSource() {
    }

    public ItemSource(Integer id) {
        this.id = id;
    }

    public ItemSource(Integer id, boolean isVendor, boolean isManufacturer) {
        this.id = id;
        this.isVendor = isVendor;
        this.isManufacturer = isManufacturer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsVendor() {
        return isVendor;
    }

    public void setIsVendor(boolean isVendor) {
        this.isVendor = isVendor;
    }
    
    public String getIsVendorString() {
        return String.valueOf(isVendor); 
    }    

    public boolean getIsManufacturer() {
        return isManufacturer;
    }

    public void setIsManufacturer(boolean isManufacturer) {
        this.isManufacturer = isManufacturer;
    }
    
    public String getIsManufacturerString() {
        return String.valueOf(isManufacturer); 
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    @JsonIgnore
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
    
    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
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
        if (!(object instanceof ItemSource)) {
            return false;
        }
        ItemSource other = (ItemSource) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.ItemSource[ id=" + id + " ]";
    }

}
