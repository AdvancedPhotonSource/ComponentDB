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
@Table(name = "person")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
    @NamedQuery(name = "Person.findByPersonId", query = "SELECT p FROM Person p WHERE p.personId = :personId"),
    @NamedQuery(name = "Person.findByFirstNm", query = "SELECT p FROM Person p WHERE p.firstNm = :firstNm"),
    @NamedQuery(name = "Person.findByMiddleNm", query = "SELECT p FROM Person p WHERE p.middleNm = :middleNm"),
    @NamedQuery(name = "Person.findByLastNm", query = "SELECT p FROM Person p WHERE p.lastNm = :lastNm"),
    @NamedQuery(name = "Person.findByUserid", query = "SELECT p FROM Person p WHERE p.userid = :userid")})
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "person_id")
    private Integer personId;
    @Size(max = 30)
    @Column(name = "first_nm")
    private String firstNm;
    @Size(max = 30)
    @Column(name = "middle_nm")
    private String middleNm;
    @Size(max = 30)
    @Column(name = "last_nm")
    private String lastNm;
    @Size(max = 30)
    @Column(name = "userid")
    private String userid;
    @OneToMany(mappedBy = "aoiCustomerContactId")
    private List<Aoi> aoiList;
    @OneToMany(mappedBy = "aoiCognizant2Id")
    private List<Aoi> aoiList1;
    @OneToMany(mappedBy = "aoiCognizant1Id")
    private List<Aoi> aoiList2;
    @OneToMany(mappedBy = "contact")
    private List<ComponentType> componentTypeList;
    @OneToMany(mappedBy = "personId")
    private List<PersonGroup> personGroupList;
    @OneToMany(mappedBy = "cogTechnicianId")
    private List<ApsIoc> apsIocList;
    @OneToMany(mappedBy = "cogDeveloperId")
    private List<ApsIoc> apsIocList1;
    @OneToMany(mappedBy = "personId")
    private List<AuditAction> auditActionList;
    @OneToMany(mappedBy = "personId")
    private List<ComponentTypePerson> componentTypePersonList;
    @OneToMany(mappedBy = "cognizantId")
    private List<Server> serverList;

    public Person() {
    }

    public Person(Integer personId) {
        this.personId = personId;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getFirstNm() {
        return firstNm;
    }

    public void setFirstNm(String firstNm) {
        this.firstNm = firstNm;
    }

    public String getMiddleNm() {
        return middleNm;
    }

    public void setMiddleNm(String middleNm) {
        this.middleNm = middleNm;
    }

    public String getLastNm() {
        return lastNm;
    }

    public void setLastNm(String lastNm) {
        this.lastNm = lastNm;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @XmlTransient
    public List<Aoi> getAoiList() {
        return aoiList;
    }

    public void setAoiList(List<Aoi> aoiList) {
        this.aoiList = aoiList;
    }

    @XmlTransient
    public List<Aoi> getAoiList1() {
        return aoiList1;
    }

    public void setAoiList1(List<Aoi> aoiList1) {
        this.aoiList1 = aoiList1;
    }

    @XmlTransient
    public List<Aoi> getAoiList2() {
        return aoiList2;
    }

    public void setAoiList2(List<Aoi> aoiList2) {
        this.aoiList2 = aoiList2;
    }

    @XmlTransient
    public List<ComponentType> getComponentTypeList() {
        return componentTypeList;
    }

    public void setComponentTypeList(List<ComponentType> componentTypeList) {
        this.componentTypeList = componentTypeList;
    }

    @XmlTransient
    public List<PersonGroup> getPersonGroupList() {
        return personGroupList;
    }

    public void setPersonGroupList(List<PersonGroup> personGroupList) {
        this.personGroupList = personGroupList;
    }

    @XmlTransient
    public List<ApsIoc> getApsIocList() {
        return apsIocList;
    }

    public void setApsIocList(List<ApsIoc> apsIocList) {
        this.apsIocList = apsIocList;
    }

    @XmlTransient
    public List<ApsIoc> getApsIocList1() {
        return apsIocList1;
    }

    public void setApsIocList1(List<ApsIoc> apsIocList1) {
        this.apsIocList1 = apsIocList1;
    }

    @XmlTransient
    public List<AuditAction> getAuditActionList() {
        return auditActionList;
    }

    public void setAuditActionList(List<AuditAction> auditActionList) {
        this.auditActionList = auditActionList;
    }

    @XmlTransient
    public List<ComponentTypePerson> getComponentTypePersonList() {
        return componentTypePersonList;
    }

    public void setComponentTypePersonList(List<ComponentTypePerson> componentTypePersonList) {
        this.componentTypePersonList = componentTypePersonList;
    }

    @XmlTransient
    public List<Server> getServerList() {
        return serverList;
    }

    public void setServerList(List<Server> serverList) {
        this.serverList = serverList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (personId != null ? personId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Person)) {
            return false;
        }
        Person other = (Person) object;
        if ((this.personId == null && other.personId != null) || (this.personId != null && !this.personId.equals(other.personId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.Person[ personId=" + personId + " ]";
    }
    
    public String getFullName() {
        String result = "";
        if (firstNm != null) {
            result = firstNm;
        }
        if (middleNm != null) {
            result += " " + middleNm;
        }
        result = result.trim();
        if (lastNm != null) {
            result += " " + lastNm;
        }
        return result.trim();
    }
}
