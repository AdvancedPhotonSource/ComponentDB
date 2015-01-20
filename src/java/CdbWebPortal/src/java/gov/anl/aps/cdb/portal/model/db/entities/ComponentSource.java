/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.utilities.ObjectUtility;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "component_source")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentSource.findAll", query = "SELECT c FROM ComponentSource c"),
    @NamedQuery(name = "ComponentSource.findById", query = "SELECT c FROM ComponentSource c WHERE c.id = :id"),
    @NamedQuery(name = "ComponentSource.findByPartNumber", query = "SELECT c FROM ComponentSource c WHERE c.partNumber = :partNumber"),
    @NamedQuery(name = "ComponentSource.findByCost", query = "SELECT c FROM ComponentSource c WHERE c.cost = :cost"),
    @NamedQuery(name = "ComponentSource.findByDescription", query = "SELECT c FROM ComponentSource c WHERE c.description = :description"),
    @NamedQuery(name = "ComponentSource.findAllByComponentId", query = "SELECT c FROM ComponentSource c WHERE c.component.id = :componentId")})
public class ComponentSource extends CloneableEntity {

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
    @JoinColumn(name = "source_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Source source;
    @JoinColumn(name = "component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Component component;

    public ComponentSource() {
    }

    public ComponentSource(Integer id) {
        this.id = id;
    }

    @Override
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

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
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

    public boolean getIsManufacturer() {
        return isManufacturer;
    }

    public void setIsManufacturer(boolean isManufacturer) {
        this.isManufacturer = isManufacturer;
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
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsBySource(ComponentSource other) {
        if (other != null) {
            return ObjectUtility.equals(this.source, other.source);
        }
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ComponentSource)) {
            return false;
        }
        ComponentSource other = (ComponentSource) object;
        if (this.id == null && other.id == null) {
            return equalsBySource(other);
        }

        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return source.getName();
    }

}
