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
import javax.persistence.Lob;
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
@Table(name = "aoi")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Aoi.findAll", query = "SELECT a FROM Aoi a"),
    @NamedQuery(name = "Aoi.findByAoiId", query = "SELECT a FROM Aoi a WHERE a.aoiId = :aoiId"),
    @NamedQuery(name = "Aoi.findByAoiName", query = "SELECT a FROM Aoi a WHERE a.aoiName = :aoiName"),
    @NamedQuery(name = "Aoi.findByAoiKeyword", query = "SELECT a FROM Aoi a WHERE a.aoiKeyword = :aoiKeyword")})
public class Aoi implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aoi_id")
    private Integer aoiId;
    @Size(max = 60)
    @Column(name = "aoi_name")
    private String aoiName;
    @Lob
    @Size(max = 16777215)
    @Column(name = "aoi_description")
    private String aoiDescription;
    @Lob
    @Size(max = 16777215)
    @Column(name = "aoi_worklog")
    private String aoiWorklog;
    @Size(max = 255)
    @Column(name = "aoi_keyword")
    private String aoiKeyword;
    @JoinColumn(name = "aoi_customer_group_id", referencedColumnName = "group_name_id")
    @ManyToOne
    private GroupName aoiCustomerGroupId;
    @JoinColumn(name = "aoi_status_id", referencedColumnName = "aoi_status_id")
    @ManyToOne
    private AoiStatus aoiStatusId;
    @JoinColumn(name = "aoi_customer_contact_id", referencedColumnName = "person_id")
    @ManyToOne
    private Person aoiCustomerContactId;
    @JoinColumn(name = "aoi_cognizant2_id", referencedColumnName = "person_id")
    @ManyToOne
    private Person aoiCognizant2Id;
    @JoinColumn(name = "aoi_cognizant1_id", referencedColumnName = "person_id")
    @ManyToOne
    private Person aoiCognizant1Id;
    @OneToMany(mappedBy = "aoiId")
    private List<AoiMachine> aoiMachineList;
    @OneToMany(mappedBy = "aoiId")
    private List<AoiDocument> aoiDocumentList;
    @OneToMany(mappedBy = "aoiId")
    private List<AoiTechsys> aoiTechsysList;
    @OneToMany(mappedBy = "aoiId")
    private List<AoiTopdisplay> aoiTopdisplayList;
    @OneToMany(mappedBy = "aoiId")
    private List<AoiCriticality> aoiCriticalityList;
    @OneToMany(mappedBy = "aoiId")
    private List<AoiPlcStcmdLine> aoiPlcStcmdLineList;
    @OneToMany(mappedBy = "aoiId")
    private List<AoiNote> aoiNoteList;
    @OneToMany(mappedBy = "aoi2Id")
    private List<AoiRelation> aoiRelationList;
    @OneToMany(mappedBy = "aoi1Id")
    private List<AoiRelation> aoiRelationList1;

    public Aoi() {
    }

    public Aoi(Integer aoiId) {
        this.aoiId = aoiId;
    }

    public Integer getAoiId() {
        return aoiId;
    }

    public void setAoiId(Integer aoiId) {
        this.aoiId = aoiId;
    }

    public String getAoiName() {
        return aoiName;
    }

    public void setAoiName(String aoiName) {
        this.aoiName = aoiName;
    }

    public String getAoiDescription() {
        return aoiDescription;
    }

    public void setAoiDescription(String aoiDescription) {
        this.aoiDescription = aoiDescription;
    }

    public String getAoiWorklog() {
        return aoiWorklog;
    }

    public void setAoiWorklog(String aoiWorklog) {
        this.aoiWorklog = aoiWorklog;
    }

    public String getAoiKeyword() {
        return aoiKeyword;
    }

    public void setAoiKeyword(String aoiKeyword) {
        this.aoiKeyword = aoiKeyword;
    }

    public GroupName getAoiCustomerGroupId() {
        return aoiCustomerGroupId;
    }

    public void setAoiCustomerGroupId(GroupName aoiCustomerGroupId) {
        this.aoiCustomerGroupId = aoiCustomerGroupId;
    }

    public AoiStatus getAoiStatusId() {
        return aoiStatusId;
    }

    public void setAoiStatusId(AoiStatus aoiStatusId) {
        this.aoiStatusId = aoiStatusId;
    }

    public Person getAoiCustomerContactId() {
        return aoiCustomerContactId;
    }

    public void setAoiCustomerContactId(Person aoiCustomerContactId) {
        this.aoiCustomerContactId = aoiCustomerContactId;
    }

    public Person getAoiCognizant2Id() {
        return aoiCognizant2Id;
    }

    public void setAoiCognizant2Id(Person aoiCognizant2Id) {
        this.aoiCognizant2Id = aoiCognizant2Id;
    }

    public Person getAoiCognizant1Id() {
        return aoiCognizant1Id;
    }

    public void setAoiCognizant1Id(Person aoiCognizant1Id) {
        this.aoiCognizant1Id = aoiCognizant1Id;
    }

    @XmlTransient
    public List<AoiMachine> getAoiMachineList() {
        return aoiMachineList;
    }

    public void setAoiMachineList(List<AoiMachine> aoiMachineList) {
        this.aoiMachineList = aoiMachineList;
    }

    @XmlTransient
    public List<AoiDocument> getAoiDocumentList() {
        return aoiDocumentList;
    }

    public void setAoiDocumentList(List<AoiDocument> aoiDocumentList) {
        this.aoiDocumentList = aoiDocumentList;
    }

    @XmlTransient
    public List<AoiTechsys> getAoiTechsysList() {
        return aoiTechsysList;
    }

    public void setAoiTechsysList(List<AoiTechsys> aoiTechsysList) {
        this.aoiTechsysList = aoiTechsysList;
    }

    @XmlTransient
    public List<AoiTopdisplay> getAoiTopdisplayList() {
        return aoiTopdisplayList;
    }

    public void setAoiTopdisplayList(List<AoiTopdisplay> aoiTopdisplayList) {
        this.aoiTopdisplayList = aoiTopdisplayList;
    }

    @XmlTransient
    public List<AoiCriticality> getAoiCriticalityList() {
        return aoiCriticalityList;
    }

    public void setAoiCriticalityList(List<AoiCriticality> aoiCriticalityList) {
        this.aoiCriticalityList = aoiCriticalityList;
    }

    @XmlTransient
    public List<AoiPlcStcmdLine> getAoiPlcStcmdLineList() {
        return aoiPlcStcmdLineList;
    }

    public void setAoiPlcStcmdLineList(List<AoiPlcStcmdLine> aoiPlcStcmdLineList) {
        this.aoiPlcStcmdLineList = aoiPlcStcmdLineList;
    }

    @XmlTransient
    public List<AoiNote> getAoiNoteList() {
        return aoiNoteList;
    }

    public void setAoiNoteList(List<AoiNote> aoiNoteList) {
        this.aoiNoteList = aoiNoteList;
    }

    @XmlTransient
    public List<AoiRelation> getAoiRelationList() {
        return aoiRelationList;
    }

    public void setAoiRelationList(List<AoiRelation> aoiRelationList) {
        this.aoiRelationList = aoiRelationList;
    }

    @XmlTransient
    public List<AoiRelation> getAoiRelationList1() {
        return aoiRelationList1;
    }

    public void setAoiRelationList1(List<AoiRelation> aoiRelationList1) {
        this.aoiRelationList1 = aoiRelationList1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aoiId != null ? aoiId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Aoi)) {
            return false;
        }
        Aoi other = (Aoi) object;
        if ((this.aoiId == null && other.aoiId != null) || (this.aoiId != null && !this.aoiId.equals(other.aoiId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.Aoi[ aoiId=" + aoiId + " ]";
    }
    
}
