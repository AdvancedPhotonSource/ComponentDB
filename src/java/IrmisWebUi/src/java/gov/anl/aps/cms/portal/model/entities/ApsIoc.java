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
import javax.persistence.Lob;
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
@Table(name = "aps_ioc")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ApsIoc.findAll", query = "SELECT a FROM ApsIoc a"),
    @NamedQuery(name = "ApsIoc.findByApsIocId", query = "SELECT a FROM ApsIoc a WHERE a.apsIocId = :apsIocId"),
    @NamedQuery(name = "ApsIoc.findByLocation", query = "SELECT a FROM ApsIoc a WHERE a.location = :location"),
    @NamedQuery(name = "ApsIoc.findByTermServRackNo", query = "SELECT a FROM ApsIoc a WHERE a.termServRackNo = :termServRackNo"),
    @NamedQuery(name = "ApsIoc.findByTermServName", query = "SELECT a FROM ApsIoc a WHERE a.termServName = :termServName"),
    @NamedQuery(name = "ApsIoc.findByTermServPort", query = "SELECT a FROM ApsIoc a WHERE a.termServPort = :termServPort"),
    @NamedQuery(name = "ApsIoc.findByTermServFiberConvCh", query = "SELECT a FROM ApsIoc a WHERE a.termServFiberConvCh = :termServFiberConvCh"),
    @NamedQuery(name = "ApsIoc.findByTermServFiberConvPort", query = "SELECT a FROM ApsIoc a WHERE a.termServFiberConvPort = :termServFiberConvPort"),
    @NamedQuery(name = "ApsIoc.findByPrimEnetSwRackNo", query = "SELECT a FROM ApsIoc a WHERE a.primEnetSwRackNo = :primEnetSwRackNo"),
    @NamedQuery(name = "ApsIoc.findByPrimEnetSwitch", query = "SELECT a FROM ApsIoc a WHERE a.primEnetSwitch = :primEnetSwitch"),
    @NamedQuery(name = "ApsIoc.findByPrimEnetBlade", query = "SELECT a FROM ApsIoc a WHERE a.primEnetBlade = :primEnetBlade"),
    @NamedQuery(name = "ApsIoc.findByPrimEnetMedConvCh", query = "SELECT a FROM ApsIoc a WHERE a.primEnetMedConvCh = :primEnetMedConvCh"),
    @NamedQuery(name = "ApsIoc.findByPrimEnetPort", query = "SELECT a FROM ApsIoc a WHERE a.primEnetPort = :primEnetPort"),
    @NamedQuery(name = "ApsIoc.findByPrimMediaConvPort", query = "SELECT a FROM ApsIoc a WHERE a.primMediaConvPort = :primMediaConvPort"),
    @NamedQuery(name = "ApsIoc.findBySecEnetSwRackNo", query = "SELECT a FROM ApsIoc a WHERE a.secEnetSwRackNo = :secEnetSwRackNo"),
    @NamedQuery(name = "ApsIoc.findBySecEnetSwitch", query = "SELECT a FROM ApsIoc a WHERE a.secEnetSwitch = :secEnetSwitch"),
    @NamedQuery(name = "ApsIoc.findBySecEnetBlade", query = "SELECT a FROM ApsIoc a WHERE a.secEnetBlade = :secEnetBlade"),
    @NamedQuery(name = "ApsIoc.findBySecEnetPort", query = "SELECT a FROM ApsIoc a WHERE a.secEnetPort = :secEnetPort"),
    @NamedQuery(name = "ApsIoc.findBySecEnetMedConvCh", query = "SELECT a FROM ApsIoc a WHERE a.secEnetMedConvCh = :secEnetMedConvCh"),
    @NamedQuery(name = "ApsIoc.findBySecMedConvPort", query = "SELECT a FROM ApsIoc a WHERE a.secMedConvPort = :secMedConvPort"),
    @NamedQuery(name = "ApsIoc.findBySysresetReqd", query = "SELECT a FROM ApsIoc a WHERE a.sysresetReqd = :sysresetReqd"),
    @NamedQuery(name = "ApsIoc.findByInhibitAutoReboot", query = "SELECT a FROM ApsIoc a WHERE a.inhibitAutoReboot = :inhibitAutoReboot")})
public class ApsIoc implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "aps_ioc_id")
    private Integer apsIocId;
    @Size(max = 60)
    @Column(name = "location")
    private String location;
    @Size(max = 60)
    @Column(name = "TermServRackNo")
    private String termServRackNo;
    @Size(max = 60)
    @Column(name = "TermServName")
    private String termServName;
    @Column(name = "TermServPort")
    private Integer termServPort;
    @Size(max = 60)
    @Column(name = "TermServFiberConvCh")
    private String termServFiberConvCh;
    @Column(name = "TermServFiberConvPort")
    private Integer termServFiberConvPort;
    @Size(max = 60)
    @Column(name = "PrimEnetSwRackNo")
    private String primEnetSwRackNo;
    @Size(max = 60)
    @Column(name = "PrimEnetSwitch")
    private String primEnetSwitch;
    @Size(max = 60)
    @Column(name = "PrimEnetBlade")
    private String primEnetBlade;
    @Size(max = 60)
    @Column(name = "PrimEnetMedConvCh")
    private String primEnetMedConvCh;
    @Column(name = "PrimEnetPort")
    private Integer primEnetPort;
    @Column(name = "PrimMediaConvPort")
    private Integer primMediaConvPort;
    @Size(max = 60)
    @Column(name = "SecEnetSwRackNo")
    private String secEnetSwRackNo;
    @Size(max = 60)
    @Column(name = "SecEnetSwitch")
    private String secEnetSwitch;
    @Size(max = 60)
    @Column(name = "SecEnetBlade")
    private String secEnetBlade;
    @Column(name = "SecEnetPort")
    private Integer secEnetPort;
    @Size(max = 60)
    @Column(name = "SecEnetMedConvCh")
    private String secEnetMedConvCh;
    @Column(name = "SecMedConvPort")
    private Integer secMedConvPort;
    @Lob
    @Size(max = 65535)
    @Column(name = "general_functions")
    private String generalFunctions;
    @Lob
    @Size(max = 65535)
    @Column(name = "pre_boot_instr")
    private String preBootInstr;
    @Lob
    @Size(max = 65535)
    @Column(name = "post_boot_instr")
    private String postBootInstr;
    @Lob
    @Size(max = 65535)
    @Column(name = "power_cycle_caution")
    private String powerCycleCaution;
    @Column(name = "sysreset_reqd")
    private Boolean sysresetReqd;
    @Column(name = "inhibit_auto_reboot")
    private Boolean inhibitAutoReboot;
    @JoinColumn(name = "cog_technician_id", referencedColumnName = "person_id")
    @ManyToOne
    private Person cogTechnicianId;
    @JoinColumn(name = "cog_developer_id", referencedColumnName = "person_id")
    @ManyToOne
    private Person cogDeveloperId;
    @JoinColumn(name = "ioc_id", referencedColumnName = "ioc_id")
    @ManyToOne(optional = false)
    private Ioc iocId;

    public ApsIoc() {
    }

    public ApsIoc(Integer apsIocId) {
        this.apsIocId = apsIocId;
    }

    public Integer getApsIocId() {
        return apsIocId;
    }

    public void setApsIocId(Integer apsIocId) {
        this.apsIocId = apsIocId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTermServRackNo() {
        return termServRackNo;
    }

    public void setTermServRackNo(String termServRackNo) {
        this.termServRackNo = termServRackNo;
    }

    public String getTermServName() {
        return termServName;
    }

    public void setTermServName(String termServName) {
        this.termServName = termServName;
    }

    public Integer getTermServPort() {
        return termServPort;
    }

    public void setTermServPort(Integer termServPort) {
        this.termServPort = termServPort;
    }

    public String getTermServFiberConvCh() {
        return termServFiberConvCh;
    }

    public void setTermServFiberConvCh(String termServFiberConvCh) {
        this.termServFiberConvCh = termServFiberConvCh;
    }

    public Integer getTermServFiberConvPort() {
        return termServFiberConvPort;
    }

    public void setTermServFiberConvPort(Integer termServFiberConvPort) {
        this.termServFiberConvPort = termServFiberConvPort;
    }

    public String getPrimEnetSwRackNo() {
        return primEnetSwRackNo;
    }

    public void setPrimEnetSwRackNo(String primEnetSwRackNo) {
        this.primEnetSwRackNo = primEnetSwRackNo;
    }

    public String getPrimEnetSwitch() {
        return primEnetSwitch;
    }

    public void setPrimEnetSwitch(String primEnetSwitch) {
        this.primEnetSwitch = primEnetSwitch;
    }

    public String getPrimEnetBlade() {
        return primEnetBlade;
    }

    public void setPrimEnetBlade(String primEnetBlade) {
        this.primEnetBlade = primEnetBlade;
    }

    public String getPrimEnetMedConvCh() {
        return primEnetMedConvCh;
    }

    public void setPrimEnetMedConvCh(String primEnetMedConvCh) {
        this.primEnetMedConvCh = primEnetMedConvCh;
    }

    public Integer getPrimEnetPort() {
        return primEnetPort;
    }

    public void setPrimEnetPort(Integer primEnetPort) {
        this.primEnetPort = primEnetPort;
    }

    public Integer getPrimMediaConvPort() {
        return primMediaConvPort;
    }

    public void setPrimMediaConvPort(Integer primMediaConvPort) {
        this.primMediaConvPort = primMediaConvPort;
    }

    public String getSecEnetSwRackNo() {
        return secEnetSwRackNo;
    }

    public void setSecEnetSwRackNo(String secEnetSwRackNo) {
        this.secEnetSwRackNo = secEnetSwRackNo;
    }

    public String getSecEnetSwitch() {
        return secEnetSwitch;
    }

    public void setSecEnetSwitch(String secEnetSwitch) {
        this.secEnetSwitch = secEnetSwitch;
    }

    public String getSecEnetBlade() {
        return secEnetBlade;
    }

    public void setSecEnetBlade(String secEnetBlade) {
        this.secEnetBlade = secEnetBlade;
    }

    public Integer getSecEnetPort() {
        return secEnetPort;
    }

    public void setSecEnetPort(Integer secEnetPort) {
        this.secEnetPort = secEnetPort;
    }

    public String getSecEnetMedConvCh() {
        return secEnetMedConvCh;
    }

    public void setSecEnetMedConvCh(String secEnetMedConvCh) {
        this.secEnetMedConvCh = secEnetMedConvCh;
    }

    public Integer getSecMedConvPort() {
        return secMedConvPort;
    }

    public void setSecMedConvPort(Integer secMedConvPort) {
        this.secMedConvPort = secMedConvPort;
    }

    public String getGeneralFunctions() {
        return generalFunctions;
    }

    public void setGeneralFunctions(String generalFunctions) {
        this.generalFunctions = generalFunctions;
    }

    public String getPreBootInstr() {
        return preBootInstr;
    }

    public void setPreBootInstr(String preBootInstr) {
        this.preBootInstr = preBootInstr;
    }

    public String getPostBootInstr() {
        return postBootInstr;
    }

    public void setPostBootInstr(String postBootInstr) {
        this.postBootInstr = postBootInstr;
    }

    public String getPowerCycleCaution() {
        return powerCycleCaution;
    }

    public void setPowerCycleCaution(String powerCycleCaution) {
        this.powerCycleCaution = powerCycleCaution;
    }

    public Boolean getSysresetReqd() {
        return sysresetReqd;
    }

    public void setSysresetReqd(Boolean sysresetReqd) {
        this.sysresetReqd = sysresetReqd;
    }

    public Boolean getInhibitAutoReboot() {
        return inhibitAutoReboot;
    }

    public void setInhibitAutoReboot(Boolean inhibitAutoReboot) {
        this.inhibitAutoReboot = inhibitAutoReboot;
    }

    public Person getCogTechnicianId() {
        return cogTechnicianId;
    }

    public void setCogTechnicianId(Person cogTechnicianId) {
        this.cogTechnicianId = cogTechnicianId;
    }

    public Person getCogDeveloperId() {
        return cogDeveloperId;
    }

    public void setCogDeveloperId(Person cogDeveloperId) {
        this.cogDeveloperId = cogDeveloperId;
    }

    public Ioc getIocId() {
        return iocId;
    }

    public void setIocId(Ioc iocId) {
        this.iocId = iocId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (apsIocId != null ? apsIocId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ApsIoc)) {
            return false;
        }
        ApsIoc other = (ApsIoc) object;
        if ((this.apsIocId == null && other.apsIocId != null) || (this.apsIocId != null && !this.apsIocId.equals(other.apsIocId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.ApsIoc[ apsIocId=" + apsIocId + " ]";
    }
    
}
