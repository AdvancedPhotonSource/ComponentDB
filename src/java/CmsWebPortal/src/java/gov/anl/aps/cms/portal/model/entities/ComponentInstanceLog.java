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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "component_instance_log")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComponentInstanceLog.findAll", query = "SELECT c FROM ComponentInstanceLog c"),
    @NamedQuery(name = "ComponentInstanceLog.findById", query = "SELECT c FROM ComponentInstanceLog c WHERE c.id = :id")})
public class ComponentInstanceLog implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "log_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Log logId;
    @JoinColumn(name = "component_instance_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentInstance componentInstanceId;

    public ComponentInstanceLog() {
    }

    public ComponentInstanceLog(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Log getLogId() {
        return logId;
    }

    public void setLogId(Log logId) {
        this.logId = logId;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComponentInstanceLog)) {
            return false;
        }
        ComponentInstanceLog other = (ComponentInstanceLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.portal.model.entities.ComponentInstanceLog[ id=" + id + " ]";
    }
    
}
