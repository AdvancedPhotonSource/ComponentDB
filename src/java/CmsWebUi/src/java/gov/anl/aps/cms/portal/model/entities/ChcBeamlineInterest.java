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
@Table(name = "chc_beamline_interest")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ChcBeamlineInterest.findAll", query = "SELECT c FROM ChcBeamlineInterest c"),
    @NamedQuery(name = "ChcBeamlineInterest.findByChcBeamlineInterestId", query = "SELECT c FROM ChcBeamlineInterest c WHERE c.chcBeamlineInterestId = :chcBeamlineInterestId"),
    @NamedQuery(name = "ChcBeamlineInterest.findByInterest", query = "SELECT c FROM ChcBeamlineInterest c WHERE c.interest = :interest")})
public class ChcBeamlineInterest implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "chc_beamline_interest_id")
    private Integer chcBeamlineInterestId;
    @Size(max = 30)
    @Column(name = "interest")
    private String interest;
    @OneToMany(mappedBy = "beamlineInterest")
    private List<ComponentType> componentTypeList;

    public ChcBeamlineInterest() {
    }

    public ChcBeamlineInterest(Integer chcBeamlineInterestId) {
        this.chcBeamlineInterestId = chcBeamlineInterestId;
    }

    public Integer getChcBeamlineInterestId() {
        return chcBeamlineInterestId;
    }

    public void setChcBeamlineInterestId(Integer chcBeamlineInterestId) {
        this.chcBeamlineInterestId = chcBeamlineInterestId;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    @XmlTransient
    public List<ComponentType> getComponentTypeList() {
        return componentTypeList;
    }

    public void setComponentTypeList(List<ComponentType> componentTypeList) {
        this.componentTypeList = componentTypeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (chcBeamlineInterestId != null ? chcBeamlineInterestId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ChcBeamlineInterest)) {
            return false;
        }
        ChcBeamlineInterest other = (ChcBeamlineInterest) object;
        if ((this.chcBeamlineInterestId == null && other.chcBeamlineInterestId != null) || (this.chcBeamlineInterestId != null && !this.chcBeamlineInterestId.equals(other.chcBeamlineInterestId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ChcBeamlineInterest[ chcBeamlineInterestId=" + chcBeamlineInterestId + " ]";
    }
    
}
