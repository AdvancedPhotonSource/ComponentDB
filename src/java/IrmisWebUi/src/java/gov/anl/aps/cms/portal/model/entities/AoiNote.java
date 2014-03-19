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
@Table(name = "aoi_note")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AoiNote.findAll", query = "SELECT a FROM AoiNote a"),
    @NamedQuery(name = "AoiNote.findByAoiNoteId", query = "SELECT a FROM AoiNote a WHERE a.aoiNoteId = :aoiNoteId"),
    @NamedQuery(name = "AoiNote.findByAoiNoteDate", query = "SELECT a FROM AoiNote a WHERE a.aoiNoteDate = :aoiNoteDate"),
    @NamedQuery(name = "AoiNote.findByAoiNote", query = "SELECT a FROM AoiNote a WHERE a.aoiNote = :aoiNote")})
public class AoiNote implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aoi_note_id")
    private Integer aoiNoteId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "aoi_note_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date aoiNoteDate;
    @Size(max = 255)
    @Column(name = "aoi_note")
    private String aoiNote;
    @JoinColumn(name = "aoi_id", referencedColumnName = "aoi_id")
    @ManyToOne
    private Aoi aoiId;

    public AoiNote() {
    }

    public AoiNote(Integer aoiNoteId) {
        this.aoiNoteId = aoiNoteId;
    }

    public AoiNote(Integer aoiNoteId, Date aoiNoteDate) {
        this.aoiNoteId = aoiNoteId;
        this.aoiNoteDate = aoiNoteDate;
    }

    public Integer getAoiNoteId() {
        return aoiNoteId;
    }

    public void setAoiNoteId(Integer aoiNoteId) {
        this.aoiNoteId = aoiNoteId;
    }

    public Date getAoiNoteDate() {
        return aoiNoteDate;
    }

    public void setAoiNoteDate(Date aoiNoteDate) {
        this.aoiNoteDate = aoiNoteDate;
    }

    public String getAoiNote() {
        return aoiNote;
    }

    public void setAoiNote(String aoiNote) {
        this.aoiNote = aoiNote;
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
        hash += (aoiNoteId != null ? aoiNoteId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AoiNote)) {
            return false;
        }
        AoiNote other = (AoiNote) object;
        if ((this.aoiNoteId == null && other.aoiNoteId != null) || (this.aoiNoteId != null && !this.aoiNoteId.equals(other.aoiNoteId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.AoiNote[ aoiNoteId=" + aoiNoteId + " ]";
    }
    
}
