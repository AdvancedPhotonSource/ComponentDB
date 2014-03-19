/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cms.portal.model.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sveseli
 */
@Entity
@Table(name = "ioc_boot")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "IocBoot.findAll", query = "SELECT i FROM IocBoot i"),
    @NamedQuery(name = "IocBoot.findByIocBootId", query = "SELECT i FROM IocBoot i WHERE i.iocBootId = :iocBootId"),
    @NamedQuery(name = "IocBoot.findByIocId", query = "SELECT i FROM IocBoot i WHERE i.iocId = :iocId"),
    @NamedQuery(name = "IocBoot.findBySysBootLine", query = "SELECT i FROM IocBoot i WHERE i.sysBootLine = :sysBootLine"),
    @NamedQuery(name = "IocBoot.findByIocBootDate", query = "SELECT i FROM IocBoot i WHERE i.iocBootDate = :iocBootDate"),
    @NamedQuery(name = "IocBoot.findByCurrentLoad", query = "SELECT i FROM IocBoot i WHERE i.currentLoad = :currentLoad"),
    @NamedQuery(name = "IocBoot.findByCurrentBoot", query = "SELECT i FROM IocBoot i WHERE i.currentBoot = :currentBoot"),
    @NamedQuery(name = "IocBoot.findByModifiedDate", query = "SELECT i FROM IocBoot i WHERE i.modifiedDate = :modifiedDate"),
    @NamedQuery(name = "IocBoot.findByModifiedBy", query = "SELECT i FROM IocBoot i WHERE i.modifiedBy = :modifiedBy"),
    @NamedQuery(name = "IocBoot.findByBootDevice", query = "SELECT i FROM IocBoot i WHERE i.bootDevice = :bootDevice"),
    @NamedQuery(name = "IocBoot.findByBootParamsVersion", query = "SELECT i FROM IocBoot i WHERE i.bootParamsVersion = :bootParamsVersion"),
    @NamedQuery(name = "IocBoot.findByConsoleConnection", query = "SELECT i FROM IocBoot i WHERE i.consoleConnection = :consoleConnection"),
    @NamedQuery(name = "IocBoot.findByHostInetAddress", query = "SELECT i FROM IocBoot i WHERE i.hostInetAddress = :hostInetAddress"),
    @NamedQuery(name = "IocBoot.findByHostName", query = "SELECT i FROM IocBoot i WHERE i.hostName = :hostName"),
    @NamedQuery(name = "IocBoot.findByIocInetAddress", query = "SELECT i FROM IocBoot i WHERE i.iocInetAddress = :iocInetAddress"),
    @NamedQuery(name = "IocBoot.findByIocPid", query = "SELECT i FROM IocBoot i WHERE i.iocPid = :iocPid"),
    @NamedQuery(name = "IocBoot.findByLaunchScript", query = "SELECT i FROM IocBoot i WHERE i.launchScript = :launchScript"),
    @NamedQuery(name = "IocBoot.findByLaunchScriptPid", query = "SELECT i FROM IocBoot i WHERE i.launchScriptPid = :launchScriptPid"),
    @NamedQuery(name = "IocBoot.findByOsFileName", query = "SELECT i FROM IocBoot i WHERE i.osFileName = :osFileName"),
    @NamedQuery(name = "IocBoot.findByProcessorNumber", query = "SELECT i FROM IocBoot i WHERE i.processorNumber = :processorNumber"),
    @NamedQuery(name = "IocBoot.findByTargetArchitecture", query = "SELECT i FROM IocBoot i WHERE i.targetArchitecture = :targetArchitecture")})
public class IocBoot implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ioc_boot_id")
    private Integer iocBootId;
    @Column(name = "ioc_id")
    private Integer iocId;
    @Size(max = 127)
    @Column(name = "sys_boot_line")
    private String sysBootLine;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ioc_boot_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date iocBootDate;
    @Column(name = "current_load")
    private Boolean currentLoad;
    @Column(name = "current_boot")
    private Boolean currentBoot;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    @Size(max = 10)
    @Column(name = "modified_by")
    private String modifiedBy;
    @Size(max = 127)
    @Column(name = "boot_device")
    private String bootDevice;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "boot_params_version")
    private Float bootParamsVersion;
    @Size(max = 127)
    @Column(name = "console_connection")
    private String consoleConnection;
    @Size(max = 127)
    @Column(name = "host_inet_address")
    private String hostInetAddress;
    @Size(max = 127)
    @Column(name = "host_name")
    private String hostName;
    @Size(max = 127)
    @Column(name = "ioc_inet_address")
    private String iocInetAddress;
    @Column(name = "ioc_pid")
    private Integer iocPid;
    @Size(max = 127)
    @Column(name = "launch_script")
    private String launchScript;
    @Column(name = "launch_script_pid")
    private Integer launchScriptPid;
    @Size(max = 127)
    @Column(name = "os_file_name")
    private String osFileName;
    @Column(name = "processor_number")
    private Integer processorNumber;
    @Size(max = 127)
    @Column(name = "target_architecture")
    private String targetArchitecture;
    @OneToMany(mappedBy = "iocBootId")
    private List<AoiCrawler> aoiCrawlerList;
    @OneToMany(mappedBy = "iocBootId")
    private List<IocResource> iocResourceList;
    @OneToMany(mappedBy = "iocBootId")
    private List<RecHistory> recHistoryList;
    @OneToMany(mappedBy = "iocBootId")
    private List<RecType> recTypeList;
    @OneToMany(mappedBy = "iocBootId")
    private List<IocError> iocErrorList;
    @OneToMany(mappedBy = "iocBootId")
    private List<RecTypeHistory> recTypeHistoryList;
    @OneToMany(mappedBy = "iocBootId")
    private List<Rec> recList;

    public IocBoot() {
    }

    public IocBoot(Integer iocBootId) {
        this.iocBootId = iocBootId;
    }

    public IocBoot(Integer iocBootId, Date iocBootDate, Date modifiedDate) {
        this.iocBootId = iocBootId;
        this.iocBootDate = iocBootDate;
        this.modifiedDate = modifiedDate;
    }

    public Integer getIocBootId() {
        return iocBootId;
    }

    public void setIocBootId(Integer iocBootId) {
        this.iocBootId = iocBootId;
    }

    public Integer getIocId() {
        return iocId;
    }

    public void setIocId(Integer iocId) {
        this.iocId = iocId;
    }

    public String getSysBootLine() {
        return sysBootLine;
    }

    public void setSysBootLine(String sysBootLine) {
        this.sysBootLine = sysBootLine;
    }

    public Date getIocBootDate() {
        return iocBootDate;
    }

    public void setIocBootDate(Date iocBootDate) {
        this.iocBootDate = iocBootDate;
    }

    public Boolean getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(Boolean currentLoad) {
        this.currentLoad = currentLoad;
    }

    public Boolean getCurrentBoot() {
        return currentBoot;
    }

    public void setCurrentBoot(Boolean currentBoot) {
        this.currentBoot = currentBoot;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getBootDevice() {
        return bootDevice;
    }

    public void setBootDevice(String bootDevice) {
        this.bootDevice = bootDevice;
    }

    public Float getBootParamsVersion() {
        return bootParamsVersion;
    }

    public void setBootParamsVersion(Float bootParamsVersion) {
        this.bootParamsVersion = bootParamsVersion;
    }

    public String getConsoleConnection() {
        return consoleConnection;
    }

    public void setConsoleConnection(String consoleConnection) {
        this.consoleConnection = consoleConnection;
    }

    public String getHostInetAddress() {
        return hostInetAddress;
    }

    public void setHostInetAddress(String hostInetAddress) {
        this.hostInetAddress = hostInetAddress;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIocInetAddress() {
        return iocInetAddress;
    }

    public void setIocInetAddress(String iocInetAddress) {
        this.iocInetAddress = iocInetAddress;
    }

    public Integer getIocPid() {
        return iocPid;
    }

    public void setIocPid(Integer iocPid) {
        this.iocPid = iocPid;
    }

    public String getLaunchScript() {
        return launchScript;
    }

    public void setLaunchScript(String launchScript) {
        this.launchScript = launchScript;
    }

    public Integer getLaunchScriptPid() {
        return launchScriptPid;
    }

    public void setLaunchScriptPid(Integer launchScriptPid) {
        this.launchScriptPid = launchScriptPid;
    }

    public String getOsFileName() {
        return osFileName;
    }

    public void setOsFileName(String osFileName) {
        this.osFileName = osFileName;
    }

    public Integer getProcessorNumber() {
        return processorNumber;
    }

    public void setProcessorNumber(Integer processorNumber) {
        this.processorNumber = processorNumber;
    }

    public String getTargetArchitecture() {
        return targetArchitecture;
    }

    public void setTargetArchitecture(String targetArchitecture) {
        this.targetArchitecture = targetArchitecture;
    }

    @XmlTransient
    public List<AoiCrawler> getAoiCrawlerList() {
        return aoiCrawlerList;
    }

    public void setAoiCrawlerList(List<AoiCrawler> aoiCrawlerList) {
        this.aoiCrawlerList = aoiCrawlerList;
    }

    @XmlTransient
    public List<IocResource> getIocResourceList() {
        return iocResourceList;
    }

    public void setIocResourceList(List<IocResource> iocResourceList) {
        this.iocResourceList = iocResourceList;
    }

    @XmlTransient
    public List<RecHistory> getRecHistoryList() {
        return recHistoryList;
    }

    public void setRecHistoryList(List<RecHistory> recHistoryList) {
        this.recHistoryList = recHistoryList;
    }

    @XmlTransient
    public List<RecType> getRecTypeList() {
        return recTypeList;
    }

    public void setRecTypeList(List<RecType> recTypeList) {
        this.recTypeList = recTypeList;
    }

    @XmlTransient
    public List<IocError> getIocErrorList() {
        return iocErrorList;
    }

    public void setIocErrorList(List<IocError> iocErrorList) {
        this.iocErrorList = iocErrorList;
    }

    @XmlTransient
    public List<RecTypeHistory> getRecTypeHistoryList() {
        return recTypeHistoryList;
    }

    public void setRecTypeHistoryList(List<RecTypeHistory> recTypeHistoryList) {
        this.recTypeHistoryList = recTypeHistoryList;
    }

    @XmlTransient
    public List<Rec> getRecList() {
        return recList;
    }

    public void setRecList(List<Rec> recList) {
        this.recList = recList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iocBootId != null ? iocBootId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof IocBoot)) {
            return false;
        }
        IocBoot other = (IocBoot) object;
        if ((this.iocBootId == null && other.iocBootId != null) || (this.iocBootId != null && !this.iocBootId.equals(other.iocBootId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cms.model.entities.IocBoot[ iocBootId=" + iocBootId + " ]";
    }
    
}
