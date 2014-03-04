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
import javax.persistence.JoinColumn;
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
@Table(name = "cable")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cable.findAll", query = "SELECT c FROM Cable c"),
    @NamedQuery(name = "Cable.findByCableId", query = "SELECT c FROM Cable c WHERE c.cableId = :cableId"),
    @NamedQuery(name = "Cable.findByColor", query = "SELECT c FROM Cable c WHERE c.color = :color"),
    @NamedQuery(name = "Cable.findByLabel", query = "SELECT c FROM Cable c WHERE c.label = :label"),
    @NamedQuery(name = "Cable.findByPinDetail", query = "SELECT c FROM Cable c WHERE c.pinDetail = :pinDetail"),
    @NamedQuery(name = "Cable.findByVirtual", query = "SELECT c FROM Cable c WHERE c.virtual = :virtual"),
    @NamedQuery(name = "Cable.findByDestDesc", query = "SELECT c FROM Cable c WHERE c.destDesc = :destDesc"),
    @NamedQuery(name = "Cable.findByMarkForDelete", query = "SELECT c FROM Cable c WHERE c.markForDelete = :markForDelete"),
    @NamedQuery(name = "Cable.findByVersion", query = "SELECT c FROM Cable c WHERE c.version = :version"),
    @NamedQuery(name = "Cable.findByCableTypeId", query = "SELECT c FROM Cable c WHERE c.cableTypeId = :cableTypeId")})
public class Cable implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "cable_id")
    private Integer cableId;
    @Size(max = 60)
    @Column(name = "color")
    private String color;
    @Size(max = 60)
    @Column(name = "label")
    private String label;
    @Column(name = "pin_detail")
    private Boolean pinDetail;
    @Column(name = "virtual")
    private Boolean virtual;
    @Size(max = 60)
    @Column(name = "dest_desc")
    private String destDesc;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @Column(name = "cable_type_id")
    private Integer cableTypeId;
    @OneToMany(mappedBy = "cableId")
    private List<Conductor> conductorList;
    @JoinColumn(name = "component_port_b_id", referencedColumnName = "component_port_id")
    @ManyToOne
    private ComponentPort componentPortBId;
    @JoinColumn(name = "component_port_a_id", referencedColumnName = "component_port_id")
    @ManyToOne
    private ComponentPort componentPortAId;

    public Cable() {
    }

    public Cable(Integer cableId) {
        this.cableId = cableId;
    }

    public Integer getCableId() {
        return cableId;
    }

    public void setCableId(Integer cableId) {
        this.cableId = cableId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getPinDetail() {
        return pinDetail;
    }

    public void setPinDetail(Boolean pinDetail) {
        this.pinDetail = pinDetail;
    }

    public Boolean getVirtual() {
        return virtual;
    }

    public void setVirtual(Boolean virtual) {
        this.virtual = virtual;
    }

    public String getDestDesc() {
        return destDesc;
    }

    public void setDestDesc(String destDesc) {
        this.destDesc = destDesc;
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

    public Integer getCableTypeId() {
        return cableTypeId;
    }

    public void setCableTypeId(Integer cableTypeId) {
        this.cableTypeId = cableTypeId;
    }

    @XmlTransient
    public List<Conductor> getConductorList() {
        return conductorList;
    }

    public void setConductorList(List<Conductor> conductorList) {
        this.conductorList = conductorList;
    }

    public ComponentPort getComponentPortBId() {
        return componentPortBId;
    }

    public void setComponentPortBId(ComponentPort componentPortBId) {
        this.componentPortBId = componentPortBId;
    }

    public ComponentPort getComponentPortAId() {
        return componentPortAId;
    }

    public void setComponentPortAId(ComponentPort componentPortAId) {
        this.componentPortAId = componentPortAId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cableId != null ? cableId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cable)) {
            return false;
        }
        Cable other = (Cable) object;
        if ((this.cableId == null && other.cableId != null) || (this.cableId != null && !this.cableId.equals(other.cableId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.Cable[ cableId=" + cableId + " ]";
    }
    
}
