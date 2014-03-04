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
@Table(name = "aps_component")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ApsComponent.findAll", query = "SELECT a FROM ApsComponent a"),
    @NamedQuery(name = "ApsComponent.findByApsComponentId", query = "SELECT a FROM ApsComponent a WHERE a.apsComponentId = :apsComponentId"),
    @NamedQuery(name = "ApsComponent.findBySerialNumber", query = "SELECT a FROM ApsComponent a WHERE a.serialNumber = :serialNumber"),
    @NamedQuery(name = "ApsComponent.findByVerified", query = "SELECT a FROM ApsComponent a WHERE a.verified = :verified"),
    @NamedQuery(name = "ApsComponent.findByMarkForDelete", query = "SELECT a FROM ApsComponent a WHERE a.markForDelete = :markForDelete"),
    @NamedQuery(name = "ApsComponent.findByVersion", query = "SELECT a FROM ApsComponent a WHERE a.version = :version")})
public class ApsComponent implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aps_component_id")
    private Integer apsComponentId;
    @Size(max = 60)
    @Column(name = "serial_number")
    private String serialNumber;
    @Column(name = "verified")
    private Boolean verified;
    @Column(name = "mark_for_delete")
    private Boolean markForDelete;
    @Column(name = "version")
    private Integer version;
    @JoinColumn(name = "group_name_id", referencedColumnName = "group_name_id")
    @ManyToOne
    private GroupName groupNameId;
    @JoinColumn(name = "component_id", referencedColumnName = "component_id")
    @ManyToOne(optional = false)
    private Component componentId;

    public ApsComponent() {
    }

    public ApsComponent(Integer apsComponentId) {
        this.apsComponentId = apsComponentId;
    }

    public Integer getApsComponentId() {
        return apsComponentId;
    }

    public void setApsComponentId(Integer apsComponentId) {
        this.apsComponentId = apsComponentId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
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

    public GroupName getGroupNameId() {
        return groupNameId;
    }

    public void setGroupNameId(GroupName groupNameId) {
        this.groupNameId = groupNameId;
    }

    public Component getComponentId() {
        return componentId;
    }

    public void setComponentId(Component componentId) {
        this.componentId = componentId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (apsComponentId != null ? apsComponentId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ApsComponent)) {
            return false;
        }
        ApsComponent other = (ApsComponent) object;
        if ((this.apsComponentId == null && other.apsComponentId != null) || (this.apsComponentId != null && !this.apsComponentId.equals(other.apsComponentId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ApsComponent[ apsComponentId=" + apsComponentId + " ]";
    }
    
}
