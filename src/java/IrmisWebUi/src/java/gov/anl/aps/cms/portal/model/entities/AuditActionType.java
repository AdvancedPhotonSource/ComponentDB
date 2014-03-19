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
@Table(name = "audit_action_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AuditActionType.findAll", query = "SELECT a FROM AuditActionType a"),
    @NamedQuery(name = "AuditActionType.findByAuditActionTypeId", query = "SELECT a FROM AuditActionType a WHERE a.auditActionTypeId = :auditActionTypeId"),
    @NamedQuery(name = "AuditActionType.findByActionName", query = "SELECT a FROM AuditActionType a WHERE a.actionName = :actionName")})
public class AuditActionType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "audit_action_type_id")
    private Integer auditActionTypeId;
    @Size(max = 60)
    @Column(name = "action_name")
    private String actionName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "auditActionTypeId")
    private List<AuditAction> auditActionList;

    public AuditActionType() {
    }

    public AuditActionType(Integer auditActionTypeId) {
        this.auditActionTypeId = auditActionTypeId;
    }

    public Integer getAuditActionTypeId() {
        return auditActionTypeId;
    }

    public void setAuditActionTypeId(Integer auditActionTypeId) {
        this.auditActionTypeId = auditActionTypeId;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    @XmlTransient
    public List<AuditAction> getAuditActionList() {
        return auditActionList;
    }

    public void setAuditActionList(List<AuditAction> auditActionList) {
        this.auditActionList = auditActionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (auditActionTypeId != null ? auditActionTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AuditActionType)) {
            return false;
        }
        AuditActionType other = (AuditActionType) object;
        if ((this.auditActionTypeId == null && other.auditActionTypeId != null) || (this.auditActionTypeId != null && !this.auditActionTypeId.equals(other.auditActionTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AuditActionType[ auditActionTypeId=" + auditActionTypeId + " ]";
    }
    
}
