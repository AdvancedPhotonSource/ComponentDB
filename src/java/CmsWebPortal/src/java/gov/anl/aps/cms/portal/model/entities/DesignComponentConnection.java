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
@Table(name = "design_component_connection")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DesignComponentConnection.findAll", query = "SELECT d FROM DesignComponentConnection d"),
    @NamedQuery(name = "DesignComponentConnection.findById", query = "SELECT d FROM DesignComponentConnection d WHERE d.id = :id"),
    @NamedQuery(name = "DesignComponentConnection.findByLinkDesignComponentQuantity", query = "SELECT d FROM DesignComponentConnection d WHERE d.linkDesignComponentQuantity = :linkDesignComponentQuantity"),
    @NamedQuery(name = "DesignComponentConnection.findByLabel", query = "SELECT d FROM DesignComponentConnection d WHERE d.label = :label"),
    @NamedQuery(name = "DesignComponentConnection.findByDescription", query = "SELECT d FROM DesignComponentConnection d WHERE d.description = :description")})
public class DesignComponentConnection implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "link_design_component_quantity")
    private Integer linkDesignComponentQuantity;
    @Size(max = 64)
    @Column(name = "label")
    private String label;
    @Size(max = 256)
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "parentDesignComponentConnectionId")
    private List<DesignComponentConnection> designComponentConnectionList;
    @JoinColumn(name = "parent_design_component_connection_id", referencedColumnName = "id")
    @ManyToOne
    private DesignComponentConnection parentDesignComponentConnectionId;
    @JoinColumn(name = "link_design_component_id", referencedColumnName = "id")
    @ManyToOne
    private DesignComponent linkDesignComponentId;
    @JoinColumn(name = "second_component_connector_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentConnector secondComponentConnectorId;
    @JoinColumn(name = "second_design_component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private DesignComponent secondDesignComponentId;
    @JoinColumn(name = "first_component_connector_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private ComponentConnector firstComponentConnectorId;
    @JoinColumn(name = "first_design_component_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private DesignComponent firstDesignComponentId;

    public DesignComponentConnection() {
    }

    public DesignComponentConnection(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLinkDesignComponentQuantity() {
        return linkDesignComponentQuantity;
    }

    public void setLinkDesignComponentQuantity(Integer linkDesignComponentQuantity) {
        this.linkDesignComponentQuantity = linkDesignComponentQuantity;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<DesignComponentConnection> getDesignComponentConnectionList() {
        return designComponentConnectionList;
    }

    public void setDesignComponentConnectionList(List<DesignComponentConnection> designComponentConnectionList) {
        this.designComponentConnectionList = designComponentConnectionList;
    }

    public DesignComponentConnection getParentDesignComponentConnectionId() {
        return parentDesignComponentConnectionId;
    }

    public void setParentDesignComponentConnectionId(DesignComponentConnection parentDesignComponentConnectionId) {
        this.parentDesignComponentConnectionId = parentDesignComponentConnectionId;
    }

    public DesignComponent getLinkDesignComponentId() {
        return linkDesignComponentId;
    }

    public void setLinkDesignComponentId(DesignComponent linkDesignComponentId) {
        this.linkDesignComponentId = linkDesignComponentId;
    }

    public ComponentConnector getSecondComponentConnectorId() {
        return secondComponentConnectorId;
    }

    public void setSecondComponentConnectorId(ComponentConnector secondComponentConnectorId) {
        this.secondComponentConnectorId = secondComponentConnectorId;
    }

    public DesignComponent getSecondDesignComponentId() {
        return secondDesignComponentId;
    }

    public void setSecondDesignComponentId(DesignComponent secondDesignComponentId) {
        this.secondDesignComponentId = secondDesignComponentId;
    }

    public ComponentConnector getFirstComponentConnectorId() {
        return firstComponentConnectorId;
    }

    public void setFirstComponentConnectorId(ComponentConnector firstComponentConnectorId) {
        this.firstComponentConnectorId = firstComponentConnectorId;
    }

    public DesignComponent getFirstDesignComponentId() {
        return firstDesignComponentId;
    }

    public void setFirstDesignComponentId(DesignComponent firstDesignComponentId) {
        this.firstDesignComponentId = firstDesignComponentId;
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
        if (!(object instanceof DesignComponentConnection)) {
            return false;
        }
        DesignComponentConnection other = (DesignComponentConnection) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.test.entities.DesignComponentConnection[ id=" + id + " ]";
    }
    
}
