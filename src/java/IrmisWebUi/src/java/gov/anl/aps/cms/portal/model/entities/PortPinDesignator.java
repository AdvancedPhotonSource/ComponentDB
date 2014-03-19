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
@Table(name = "port_pin_designator")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PortPinDesignator.findAll", query = "SELECT p FROM PortPinDesignator p"),
    @NamedQuery(name = "PortPinDesignator.findByPortPinDesignatorId", query = "SELECT p FROM PortPinDesignator p WHERE p.portPinDesignatorId = :portPinDesignatorId"),
    @NamedQuery(name = "PortPinDesignator.findByDesignatorOrder", query = "SELECT p FROM PortPinDesignator p WHERE p.designatorOrder = :designatorOrder"),
    @NamedQuery(name = "PortPinDesignator.findByDesignator", query = "SELECT p FROM PortPinDesignator p WHERE p.designator = :designator"),
    @NamedQuery(name = "PortPinDesignator.findByMarkForDelete", query = "SELECT p FROM PortPinDesignator p WHERE p.markForDelete = :markForDelete"),
    @NamedQuery(name = "PortPinDesignator.findByVersion", query = "SELECT p FROM PortPinDesignator p WHERE p.version = :version")})
public class PortPinDesignator implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "port_pin_designator_id")
    private Integer portPinDesignatorId;
    @Column(name = "designator_order")
    private Integer designatorOrder;
    @Size(max = 60)
    @Column(name = "designator")
    private String designator;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @OneToMany(mappedBy = "portPinDesignatorId")
    private List<PortPin> portPinList;
    @OneToMany(mappedBy = "portPinDesignatorId")
    private List<PortPinTemplate> portPinTemplateList;
    @JoinColumn(name = "component_port_type_id", referencedColumnName = "component_port_type_id")
    @ManyToOne
    private ComponentPortType componentPortTypeId;

    public PortPinDesignator() {
    }

    public PortPinDesignator(Integer portPinDesignatorId) {
        this.portPinDesignatorId = portPinDesignatorId;
    }

    public Integer getPortPinDesignatorId() {
        return portPinDesignatorId;
    }

    public void setPortPinDesignatorId(Integer portPinDesignatorId) {
        this.portPinDesignatorId = portPinDesignatorId;
    }

    public Integer getDesignatorOrder() {
        return designatorOrder;
    }

    public void setDesignatorOrder(Integer designatorOrder) {
        this.designatorOrder = designatorOrder;
    }

    public String getDesignator() {
        return designator;
    }

    public void setDesignator(String designator) {
        this.designator = designator;
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
    public List<PortPin> getPortPinList() {
        return portPinList;
    }

    public void setPortPinList(List<PortPin> portPinList) {
        this.portPinList = portPinList;
    }

    @XmlTransient
    public List<PortPinTemplate> getPortPinTemplateList() {
        return portPinTemplateList;
    }

    public void setPortPinTemplateList(List<PortPinTemplate> portPinTemplateList) {
        this.portPinTemplateList = portPinTemplateList;
    }

    public ComponentPortType getComponentPortTypeId() {
        return componentPortTypeId;
    }

    public void setComponentPortTypeId(ComponentPortType componentPortTypeId) {
        this.componentPortTypeId = componentPortTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (portPinDesignatorId != null ? portPinDesignatorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PortPinDesignator)) {
            return false;
        }
        PortPinDesignator other = (PortPinDesignator) object;
        if ((this.portPinDesignatorId == null && other.portPinDesignatorId != null) || (this.portPinDesignatorId != null && !this.portPinDesignatorId.equals(other.portPinDesignatorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.PortPinDesignator[ portPinDesignatorId=" + portPinDesignatorId + " ]";
    }
    
}
