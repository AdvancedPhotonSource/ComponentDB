/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "port_pin_template")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PortPinTemplate.findAll", query = "SELECT p FROM PortPinTemplate p"),
    @NamedQuery(name = "PortPinTemplate.findByPortPinTemplateId", query = "SELECT p FROM PortPinTemplate p WHERE p.portPinTemplateId = :portPinTemplateId"),
    @NamedQuery(name = "PortPinTemplate.findByPortPinUsage", query = "SELECT p FROM PortPinTemplate p WHERE p.portPinUsage = :portPinUsage"),
    @NamedQuery(name = "PortPinTemplate.findByMarkForDelete", query = "SELECT p FROM PortPinTemplate p WHERE p.markForDelete = :markForDelete"),
    @NamedQuery(name = "PortPinTemplate.findByVersion", query = "SELECT p FROM PortPinTemplate p WHERE p.version = :version")})
public class PortPinTemplate implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "port_pin_template_id")
    private Integer portPinTemplateId;
    @Size(max = 60)
    @Column(name = "port_pin_usage")
    private String portPinUsage;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @JoinColumn(name = "port_pin_designator_id", referencedColumnName = "port_pin_designator_id")
    @ManyToOne
    private PortPinDesignator portPinDesignatorId;
    @JoinColumn(name = "port_pin_type_id", referencedColumnName = "port_pin_type_id")
    @ManyToOne
    private PortPinType portPinTypeId;
    @JoinColumn(name = "component_port_template_id", referencedColumnName = "component_port_template_id")
    @ManyToOne
    private ComponentPortTemplate componentPortTemplateId;

    public PortPinTemplate() {
    }

    public PortPinTemplate(Integer portPinTemplateId) {
        this.portPinTemplateId = portPinTemplateId;
    }

    public Integer getPortPinTemplateId() {
        return portPinTemplateId;
    }

    public void setPortPinTemplateId(Integer portPinTemplateId) {
        this.portPinTemplateId = portPinTemplateId;
    }

    public String getPortPinUsage() {
        return portPinUsage;
    }

    public void setPortPinUsage(String portPinUsage) {
        this.portPinUsage = portPinUsage;
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

    public PortPinDesignator getPortPinDesignatorId() {
        return portPinDesignatorId;
    }

    public void setPortPinDesignatorId(PortPinDesignator portPinDesignatorId) {
        this.portPinDesignatorId = portPinDesignatorId;
    }

    public PortPinType getPortPinTypeId() {
        return portPinTypeId;
    }

    public void setPortPinTypeId(PortPinType portPinTypeId) {
        this.portPinTypeId = portPinTypeId;
    }

    public ComponentPortTemplate getComponentPortTemplateId() {
        return componentPortTemplateId;
    }

    public void setComponentPortTemplateId(ComponentPortTemplate componentPortTemplateId) {
        this.componentPortTemplateId = componentPortTemplateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (portPinTemplateId != null ? portPinTemplateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PortPinTemplate)) {
            return false;
        }
        PortPinTemplate other = (PortPinTemplate) object;
        if ((this.portPinTemplateId == null && other.portPinTemplateId != null) || (this.portPinTemplateId != null && !this.portPinTemplateId.equals(other.portPinTemplateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.PortPinTemplate[ portPinTemplateId=" + portPinTemplateId + " ]";
    }
    
}
