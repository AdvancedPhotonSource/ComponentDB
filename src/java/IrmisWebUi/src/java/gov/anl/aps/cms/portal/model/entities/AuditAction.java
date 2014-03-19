/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "audit_action")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AuditAction.findAll", query = "SELECT a FROM AuditAction a"),
    @NamedQuery(name = "AuditAction.findByAuditActionId", query = "SELECT a FROM AuditAction a WHERE a.auditActionId = :auditActionId"),
    @NamedQuery(name = "AuditAction.findByActionKey", query = "SELECT a FROM AuditAction a WHERE a.actionKey = :actionKey"),
    @NamedQuery(name = "AuditAction.findByActionDesc", query = "SELECT a FROM AuditAction a WHERE a.actionDesc = :actionDesc"),
    @NamedQuery(name = "AuditAction.findByActionDate", query = "SELECT a FROM AuditAction a WHERE a.actionDate = :actionDate")})
public class AuditAction implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "audit_action_id")
    private Integer auditActionId;
    @Column(name = "action_key")
    private Integer actionKey;
    @Size(max = 100)
    @Column(name = "action_desc")
    private String actionDesc;
    @Basic(optional = false)
    @NotNull
    @Column(name = "action_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actionDate;
    @JoinColumn(name = "audit_action_type_id", referencedColumnName = "audit_action_type_id")
    @ManyToOne(optional = false)
    private AuditActionType auditActionTypeId;
    @JoinColumn(name = "person_id", referencedColumnName = "person_id")
    @ManyToOne
    private Person personId;

    public AuditAction() {
    }

    public AuditAction(Integer auditActionId) {
        this.auditActionId = auditActionId;
    }

    public AuditAction(Integer auditActionId, Date actionDate) {
        this.auditActionId = auditActionId;
        this.actionDate = actionDate;
    }

    public Integer getAuditActionId() {
        return auditActionId;
    }

    public void setAuditActionId(Integer auditActionId) {
        this.auditActionId = auditActionId;
    }

    public Integer getActionKey() {
        return actionKey;
    }

    public void setActionKey(Integer actionKey) {
        this.actionKey = actionKey;
    }

    public String getActionDesc() {
        return actionDesc;
    }

    public void setActionDesc(String actionDesc) {
        this.actionDesc = actionDesc;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public AuditActionType getAuditActionTypeId() {
        return auditActionTypeId;
    }

    public void setAuditActionTypeId(AuditActionType auditActionTypeId) {
        this.auditActionTypeId = auditActionTypeId;
    }

    public Person getPersonId() {
        return personId;
    }

    public void setPersonId(Person personId) {
        this.personId = personId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (auditActionId != null ? auditActionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AuditAction)) {
            return false;
        }
        AuditAction other = (AuditAction) object;
        if ((this.auditActionId == null && other.auditActionId != null) || (this.auditActionId != null && !this.auditActionId.equals(other.auditActionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AuditAction[ auditActionId=" + auditActionId + " ]";
    }
    
}
