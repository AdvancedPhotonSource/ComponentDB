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
@Table(name = "aoi_topdisplay")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AoiTopdisplay.findAll", query = "SELECT a FROM AoiTopdisplay a"),
    @NamedQuery(name = "AoiTopdisplay.findByAoiTopdisplayId", query = "SELECT a FROM AoiTopdisplay a WHERE a.aoiTopdisplayId = :aoiTopdisplayId"),
    @NamedQuery(name = "AoiTopdisplay.findByUri", query = "SELECT a FROM AoiTopdisplay a WHERE a.uri = :uri")})
public class AoiTopdisplay implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aoi_topdisplay_id")
    private Integer aoiTopdisplayId;
    @Size(max = 255)
    @Column(name = "uri")
    private String uri;
    @JoinColumn(name = "aoi_id", referencedColumnName = "aoi_id")
    @ManyToOne
    private Aoi aoiId;

    public AoiTopdisplay() {
    }

    public AoiTopdisplay(Integer aoiTopdisplayId) {
        this.aoiTopdisplayId = aoiTopdisplayId;
    }

    public Integer getAoiTopdisplayId() {
        return aoiTopdisplayId;
    }

    public void setAoiTopdisplayId(Integer aoiTopdisplayId) {
        this.aoiTopdisplayId = aoiTopdisplayId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Aoi getAoiId() {
        return aoiId;
    }

    public void setAoiId(Aoi aoiId) {
        this.aoiId = aoiId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aoiTopdisplayId != null ? aoiTopdisplayId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AoiTopdisplay)) {
            return false;
        }
        AoiTopdisplay other = (AoiTopdisplay) object;
        if ((this.aoiTopdisplayId == null && other.aoiTopdisplayId != null) || (this.aoiTopdisplayId != null && !this.aoiTopdisplayId.equals(other.aoiTopdisplayId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AoiTopdisplay[ aoiTopdisplayId=" + aoiTopdisplayId + " ]";
    }
    
}
