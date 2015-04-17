/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
 * Connector type category entity class.
 */
@Entity
@Table(name = "connector_type_category")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ConnectorTypeCategory.findAll", query = "SELECT c FROM ConnectorTypeCategory c"),
    @NamedQuery(name = "ConnectorTypeCategory.findById", query = "SELECT c FROM ConnectorTypeCategory c WHERE c.id = :id"),
    @NamedQuery(name = "ConnectorTypeCategory.findByName", query = "SELECT c FROM ConnectorTypeCategory c WHERE c.name = :name"),
    @NamedQuery(name = "ConnectorTypeCategory.findByDescription", query = "SELECT c FROM ConnectorTypeCategory c WHERE c.description = :description")})
public class ConnectorTypeCategory extends CdbEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    private String name;
    @Size(max = 256)
    private String description;
    @OneToMany(mappedBy = "connectorTypeCategory")
    private List<ConnectorType> connectorTypeList;

    public ConnectorTypeCategory() {
    }

    public ConnectorTypeCategory(Integer id) {
        this.id = id;
    }

    public ConnectorTypeCategory(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<ConnectorType> getConnectorTypeList() {
        return connectorTypeList;
    }

    public void setConnectorTypeList(List<ConnectorType> connectorTypeList) {
        this.connectorTypeList = connectorTypeList;
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
        if (!(object instanceof ConnectorTypeCategory)) {
            return false;
        }
        ConnectorTypeCategory other = (ConnectorTypeCategory) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "ConnectorTypeCategory[ id=" + id + " ]";
    }

}
