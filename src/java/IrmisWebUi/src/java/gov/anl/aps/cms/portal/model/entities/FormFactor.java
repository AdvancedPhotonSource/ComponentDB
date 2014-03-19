/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "form_factor")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FormFactor.findAll", query = "SELECT f FROM FormFactor f"),
    @NamedQuery(name = "FormFactor.findByFormFactorId", query = "SELECT f FROM FormFactor f WHERE f.formFactorId = :formFactorId"),
    @NamedQuery(name = "FormFactor.findByFormFactor", query = "SELECT f FROM FormFactor f WHERE f.formFactor = :formFactor"),
    @NamedQuery(name = "FormFactor.findByMarkForDelete", query = "SELECT f FROM FormFactor f WHERE f.markForDelete = :markForDelete"),
    @NamedQuery(name = "FormFactor.findByVersion", query = "SELECT f FROM FormFactor f WHERE f.version = :version")})
public class FormFactor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "form_factor_id")
    private Integer formFactorId;
    @Size(max = 100)
    @Column(name = "form_factor")
    private String formFactor;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @OneToMany(mappedBy = "formFactor")
    private List<ComponentType> componentTypeList;

    public FormFactor() {
    }

    public FormFactor(Integer formFactorId) {
        this.formFactorId = formFactorId;
    }

    public Integer getFormFactorId() {
        return formFactorId;
    }

    public void setFormFactorId(Integer formFactorId) {
        this.formFactorId = formFactorId;
    }

    public String getFormFactor() {
        return formFactor;
    }

    public void setFormFactor(String formFactor) {
        this.formFactor = formFactor;
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

    @XmlTransient
    public List<ComponentType> getComponentTypeList() {
        return componentTypeList;
    }

    public void setComponentTypeList(List<ComponentType> componentTypeList) {
        this.componentTypeList = componentTypeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (formFactorId != null ? formFactorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FormFactor)) {
            return false;
        }
        FormFactor other = (FormFactor) object;
        if ((this.formFactorId == null && other.formFactorId != null) || (this.formFactorId != null && !this.formFactorId.equals(other.formFactorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.FormFactor[ formFactorId=" + formFactorId + " ]";
    }
    
}
