/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "component_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentType.findAll", query = "SELECT c FROM ComponentType c"),
    @NamedQuery(name = "ComponentType.findByComponentTypeId", query = "SELECT c FROM ComponentType c WHERE c.componentTypeId = :componentTypeId"),
    @NamedQuery(name = "ComponentType.findByComponentTypeName", query = "SELECT c FROM ComponentType c WHERE c.componentTypeName = :componentTypeName"),
    @NamedQuery(name = "ComponentType.findByDescription", query = "SELECT c FROM ComponentType c WHERE c.description = :description"),
    @NamedQuery(name = "ComponentType.findByMarkForDelete", query = "SELECT c FROM ComponentType c WHERE c.markForDelete = :markForDelete"),
    @NamedQuery(name = "ComponentType.findByVersion", query = "SELECT c FROM ComponentType c WHERE c.version = :version")})
public class ComponentType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_type_id")
    private Integer componentTypeId;
    @Size(max = 60)
    @Column(name = "component_type_name")
    private String componentTypeName;
    @Size(max = 100)
    @Column(name = "description")
    private String description;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @Lob
    @Size(max = 65535)
    @Column(name = "vdescription")
    private String vdescription;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentTypeId")
    private List<Component> componentList;
    @OneToMany(mappedBy = "componentTypeId")
    private List<ComponentTypeDocument> componentTypeDocumentList;
    @JoinColumn(name = "chc_contact_id", referencedColumnName = "person_id")
    @ManyToOne
    private Person contact;
    @JoinColumn(name = "chc_beamline_interest_id", referencedColumnName = "chc_beamline_interest_id")
    @ManyToOne
    private ChcBeamlineInterest beamlineInterest;
    @JoinColumn(name = "mfg_id", referencedColumnName = "mfg_id")
    @ManyToOne
    private Mfg mfg;
    @JoinColumn(name = "form_factor_id", referencedColumnName = "form_factor_id")
    @ManyToOne
    private FormFactor formFactor;
    @OneToMany(mappedBy = "componentTypeId")
    private List<ComponentTypeIf> componentTypeIfList;
    @OneToMany(mappedBy = "componentTypeId")
    private List<ComponentTypeStatus> componentTypeStatusList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "componentTypeId")
    private List<ComponentInstance> componentInstanceList;
    @OneToMany(mappedBy = "componentTypeId")
    private List<ComponentTypePerson> componentTypePersonList;
    @OneToMany(mappedBy = "componentTypeId")
    private List<ComponentTypeFunction> componentTypeFunctionList;
    @OneToMany(mappedBy = "componentTypeId")
    private List<ComponentPortTemplate> componentPortTemplateList;

    public ComponentType() {
    }

    public ComponentType(Integer componentTypeId) {
        this.componentTypeId = componentTypeId;
    }

    public Integer getComponentTypeId() {
        return componentTypeId;
    }

    public void setComponentTypeId(Integer componentTypeId) {
        this.componentTypeId = componentTypeId;
    }

    public String getComponentTypeName() {
        return componentTypeName;
    }

    public void setComponentTypeName(String componentTypeName) {
        this.componentTypeName = componentTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getMarkForDelete() {
        return markForDelete;
    }

    public void setMarkForDelete(Boolean markForDelete) {
        this.markForDelete = markForDelete;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getVdescription() {
        return vdescription;
    }

    public void setVdescription(String vdescription) {
        this.vdescription = vdescription;
    }

    @XmlTransient
    public List<Component> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<Component> componentList) {
        this.componentList = componentList;
    }

    @XmlTransient
    public List<ComponentTypeDocument> getComponentTypeDocumentList() {
        return componentTypeDocumentList;
    }

    public void setComponentTypeDocumentList(List<ComponentTypeDocument> componentTypeDocumentList) {
        this.componentTypeDocumentList = componentTypeDocumentList;
    }

    public Person getContact() {
        return contact;
    }

    public void setContact(Person contact) {
        this.contact = contact;
    }

    public ChcBeamlineInterest getBeamlineInterest() {
        return beamlineInterest;
    }

    public void setBeamlineInterest(ChcBeamlineInterest beamlineInterest) {
        this.beamlineInterest = beamlineInterest;
    }

    public Mfg getMfg() {
        return mfg;
    }

    public void setMfg(Mfg mfg) {
        this.mfg = mfg;
    }

    public FormFactor getFormFactor() {
        return formFactor;
    }

    public void setFormFactor(FormFactor formFactor) {
        this.formFactor = formFactor;
    }

    @XmlTransient
    public List<ComponentTypeIf> getComponentTypeIfList() {
        return componentTypeIfList;
    }

    public void setComponentTypeIfList(List<ComponentTypeIf> componentTypeIfList) {
        this.componentTypeIfList = componentTypeIfList;
    }

    @XmlTransient
    public List<ComponentTypeStatus> getComponentTypeStatusList() {
        return componentTypeStatusList;
    }

    public void setComponentTypeStatusList(List<ComponentTypeStatus> componentTypeStatusList) {
        this.componentTypeStatusList = componentTypeStatusList;
    }

    @XmlTransient
    public List<ComponentInstance> getComponentInstanceList() {
        return componentInstanceList;
    }

    public void setComponentInstanceList(List<ComponentInstance> componentInstanceList) {
        this.componentInstanceList = componentInstanceList;
    }

    @XmlTransient
    public List<ComponentTypePerson> getComponentTypePersonList() {
        return componentTypePersonList;
    }

    public void setComponentTypePersonList(List<ComponentTypePerson> componentTypePersonList) {
        this.componentTypePersonList = componentTypePersonList;
    }

    @XmlTransient
    public List<ComponentTypeFunction> getComponentTypeFunctionList() {
        return componentTypeFunctionList;
    }

    public void setComponentTypeFunctionList(List<ComponentTypeFunction> componentTypeFunctionList) {
        this.componentTypeFunctionList = componentTypeFunctionList;
    }

    @XmlTransient
    public List<ComponentPortTemplate> getComponentPortTemplateList() {
        return componentPortTemplateList;
    }

    public void setComponentPortTemplateList(List<ComponentPortTemplate> componentPortTemplateList) {
        this.componentPortTemplateList = componentPortTemplateList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentTypeId != null ? componentTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentType)) {
            return false;
        }
        ComponentType other = (ComponentType) object;
        if ((this.componentTypeId == null && other.componentTypeId != null) || (this.componentTypeId != null && !this.componentTypeId.equals(other.componentTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentType[ componentTypeId=" + componentTypeId + " ]";
    }
    
}
