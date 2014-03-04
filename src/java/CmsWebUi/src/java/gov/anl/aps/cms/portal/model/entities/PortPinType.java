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
 * @author sveseli
 */
@Entity
@Table(name = "port_pin_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PortPinType.findAll", query = "SELECT p FROM PortPinType p"),
    @NamedQuery(name = "PortPinType.findByPortPinTypeId", query = "SELECT p FROM PortPinType p WHERE p.portPinTypeId = :portPinTypeId"),
    @NamedQuery(name = "PortPinType.findByPortPinType", query = "SELECT p FROM PortPinType p WHERE p.portPinType = :portPinType")})
public class PortPinType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "port_pin_type_id")
    private Integer portPinTypeId;
    @Size(max = 12)
    @Column(name = "port_pin_type")
    private String portPinType;
    @OneToMany(mappedBy = "portPinTypeId")
    private List<PortPin> portPinList;
    @OneToMany(mappedBy = "portPinTypeId")
    private List<PortPinTemplate> portPinTemplateList;

    public PortPinType() {
    }

    public PortPinType(Integer portPinTypeId) {
        this.portPinTypeId = portPinTypeId;
    }

    public Integer getPortPinTypeId() {
        return portPinTypeId;
    }

    public void setPortPinTypeId(Integer portPinTypeId) {
        this.portPinTypeId = portPinTypeId;
    }

    public String getPortPinType() {
        return portPinType;
    }

    public void setPortPinType(String portPinType) {
        this.portPinType = portPinType;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (portPinTypeId != null ? portPinTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PortPinType)) {
            return false;
        }
        PortPinType other = (PortPinType) object;
        if ((this.portPinTypeId == null && other.portPinTypeId != null) || (this.portPinTypeId != null && !this.portPinTypeId.equals(other.portPinTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.PortPinType[ portPinTypeId=" + portPinTypeId + " ]";
    }
    
}
