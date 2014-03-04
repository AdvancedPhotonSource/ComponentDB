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
@Table(name = "component_instance_state")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentInstanceState.findAll", query = "SELECT c FROM ComponentInstanceState c"),
    @NamedQuery(name = "ComponentInstanceState.findByComponentInstanceStateId", query = "SELECT c FROM ComponentInstanceState c WHERE c.componentInstanceStateId = :componentInstanceStateId"),
    @NamedQuery(name = "ComponentInstanceState.findByPersonId", query = "SELECT c FROM ComponentInstanceState c WHERE c.personId = :personId"),
    @NamedQuery(name = "ComponentInstanceState.findByEnteredDate", query = "SELECT c FROM ComponentInstanceState c WHERE c.enteredDate = :enteredDate"),
    @NamedQuery(name = "ComponentInstanceState.findByComment", query = "SELECT c FROM ComponentInstanceState c WHERE c.comment = :comment"),
    @NamedQuery(name = "ComponentInstanceState.findByReferenceData1", query = "SELECT c FROM ComponentInstanceState c WHERE c.referenceData1 = :referenceData1"),
    @NamedQuery(name = "ComponentInstanceState.findByReferenceData2", query = "SELECT c FROM ComponentInstanceState c WHERE c.referenceData2 = :referenceData2"),
    @NamedQuery(name = "ComponentInstanceState.findByVersion", query = "SELECT c FROM ComponentInstanceState c WHERE c.version = :version")})
public class ComponentInstanceState implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "component_instance_state_id")
    private Integer componentInstanceStateId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "person_id")
    private int personId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "entered_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enteredDate;
    @Size(max = 255)
    @Column(name = "comment")
    private String comment;
    @Size(max = 255)
    @Column(name = "reference_data_1")
    private String referenceData1;
    @Column(name = "reference_data_2")
    @Temporal(TemporalType.TIMESTAMP)
    private Date referenceData2;
    @Column(name = "version")
    private Integer version;
    @JoinColumn(name = "component_state_id", referencedColumnName = "component_state_id")
    @ManyToOne(optional = false)
    private ComponentState componentStateId;
    @JoinColumn(name = "component_instance_id", referencedColumnName = "component_instance_id")
    @ManyToOne(optional = false)
    private ComponentInstance componentInstanceId;

    public ComponentInstanceState() {
    }

    public ComponentInstanceState(Integer componentInstanceStateId) {
        this.componentInstanceStateId = componentInstanceStateId;
    }

    public ComponentInstanceState(Integer componentInstanceStateId, int personId, Date enteredDate) {
        this.componentInstanceStateId = componentInstanceStateId;
        this.personId = personId;
        this.enteredDate = enteredDate;
    }

    public Integer getComponentInstanceStateId() {
        return componentInstanceStateId;
    }

    public void setComponentInstanceStateId(Integer componentInstanceStateId) {
        this.componentInstanceStateId = componentInstanceStateId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public Date getEnteredDate() {
        return enteredDate;
    }

    public void setEnteredDate(Date enteredDate) {
        this.enteredDate = enteredDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReferenceData1() {
        return referenceData1;
    }

    public void setReferenceData1(String referenceData1) {
        this.referenceData1 = referenceData1;
    }

    public Date getReferenceData2() {
        return referenceData2;
    }

    public void setReferenceData2(Date referenceData2) {
        this.referenceData2 = referenceData2;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public ComponentState getComponentStateId() {
        return componentStateId;
    }

    public void setComponentStateId(ComponentState componentStateId) {
        this.componentStateId = componentStateId;
    }

    public ComponentInstance getComponentInstanceId() {
        return componentInstanceId;
    }

    public void setComponentInstanceId(ComponentInstance componentInstanceId) {
        this.componentInstanceId = componentInstanceId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (componentInstanceStateId != null ? componentInstanceStateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentInstanceState)) {
            return false;
        }
        ComponentInstanceState other = (ComponentInstanceState) object;
        if ((this.componentInstanceStateId == null && other.componentInstanceStateId != null) || (this.componentInstanceStateId != null && !this.componentInstanceStateId.equals(other.componentInstanceStateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ComponentInstanceState[ componentInstanceStateId=" + componentInstanceStateId + " ]";
    }
    
}
