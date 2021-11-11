/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
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
 *
 * @author djarosz
 */
@Entity
@Cacheable(true)
@Table(name = "connector_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ConnectorType.findAll", query = "SELECT c FROM ConnectorType c"),
    @NamedQuery(name = "ConnectorType.findById", query = "SELECT c FROM ConnectorType c WHERE c.id = :id"),
    @NamedQuery(name = "ConnectorType.findByName", query = "SELECT c FROM ConnectorType c WHERE c.name = :name"),
    @NamedQuery(name = "ConnectorType.findByDescription", query = "SELECT c FROM ConnectorType c WHERE c.description = :description")})
public class ConnectorType extends CdbEntity implements Serializable {

    private static final long serialVersionUID = 1L;
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
    @OneToMany(mappedBy = "connectorType")
    private List<Connector> connectorList;

    public ConnectorType() {
    }

    public ConnectorType(Integer id) {
        this.id = id;
    }

    public ConnectorType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

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
    public List<Connector> getConnectorList() {
        return connectorList;
    }

    public void setConnectorList(List<Connector> connectorList) {
        this.connectorList = connectorList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (id != null) {
            hash += id.hashCode();
        } else if (getName() != null) {
            hash += getName().hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof ConnectorType)) {
            return false;
        }
        
        ConnectorType other = (ConnectorType) object;
        
        // special case for new items uses name
        if ((this.id == null) && (other.id == null)) {
            
            if ((this.getName() == null) && (other.getName() == null)) {
                // both names null
                return true;
            } else if (((this.getName() == null) && other.getName() != null) || (this.getName() != null && !this.getName().equals(other.getName()))) {
                // names are not equal
                return false;
            } else {
                // names are equal
                return true;
            }
            
        // check for existing items uses id
        } else if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            // at least one of the items exists and ids are not equal
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return name; 
    }
    
}
