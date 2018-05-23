/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author djarosz
 */
@Entity
@Table(name = "log")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Log.findAll", query = "SELECT l FROM Log l"),
    @NamedQuery(name = "Log.findById", query = "SELECT l FROM Log l WHERE l.id = :id"),
    @NamedQuery(name = "Log.findByEnteredOnDateTime", query = "SELECT l FROM Log l WHERE l.enteredOnDateTime = :enteredOnDateTime"),
    @NamedQuery(name = "Log.findByEffectiveFromDateTime", query = "SELECT l FROM Log l WHERE l.effectiveFromDateTime = :effectiveFromDateTime"),
    @NamedQuery(name = "Log.findByEffectiveToDateTime", query = "SELECT l FROM Log l WHERE l.effectiveToDateTime = :effectiveToDateTime")})
public class Log extends CdbEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(max = 65535)
    private String text;
    @Basic(optional = false)
    @NotNull
    @Column(name = "entered_on_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enteredOnDateTime;
    @Column(name = "effective_from_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveFromDateTime;
    @Column(name = "effective_to_date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveToDateTime;
    @JoinTable(name = "log_attachment", joinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "attachment_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Attachment> attachmentList;
    @JoinTable(name = "system_log", joinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "log_level_id", referencedColumnName = "id")})
    @ManyToMany
    private List<LogLevel> logLevelList;
    @ManyToMany(mappedBy = "logList")
    private List<ItemElement> itemElementList;
    @JoinColumn(name = "entered_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserInfo enteredByUser;
    @JoinColumn(name = "log_topic_id", referencedColumnName = "id")
    @ManyToOne
    private LogTopic logTopic;
    
    private static transient SimpleDateFormat shortDisplayDateFormat = new SimpleDateFormat("MM/dd/yy HH:mm");

    public Log() {
    }

    public Log(Integer id) {        
        this.id = id;
    }

    public Log(Integer id, String text, Date enteredOnDateTime) {
        this.id = id;
        this.text = text;
        this.enteredOnDateTime = enteredOnDateTime;
        this.effectiveFromDateTime = enteredOnDateTime; 
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getEnteredOnDateTime() {
        return enteredOnDateTime;
    }

    public void setEnteredOnDateTime(Date enteredOnDateTime) {
        this.enteredOnDateTime = enteredOnDateTime;
    }
    
    public UserInfo getEnteredByUser() {
        return this.enteredByUser; 
    }
    
    public void setEnteredByUser(UserInfo userInfo) {
        this.enteredByUser = userInfo; 
    }

    public LogTopic getLogTopic() {
        return logTopic;
    }

    public void setLogTopic(LogTopic logTopic) {
        this.logTopic = logTopic;
    }

    public Date getEffectiveFromDateTime() {
        return effectiveFromDateTime;
    }

    public void setEffectiveFromDateTime(Date effectiveFromDateTime) {
        this.effectiveFromDateTime = effectiveFromDateTime;
    }

    public Date getEffectiveToDateTime() {
        return effectiveToDateTime;
    }

    public void setEffectiveToDateTime(Date effectiveToDateTime) {
        this.effectiveToDateTime = effectiveToDateTime;
    }

    @XmlTransient
    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    @XmlTransient
    public List<LogLevel> getLogLevelList() {
        return logLevelList;
    }

    public void setLogLevelList(List<LogLevel> logLevelList) {
        this.logLevelList = logLevelList;
    }

    @XmlTransient
    public List<ItemElement> getItemElementList() {
        return itemElementList;
    }

    public void setItemElementList(List<ItemElement> itemElementList) {
        this.itemElementList = itemElementList;
    }

    public UserInfo getEnteredByUserId() {
        return enteredByUser;
    }

    public void setEnteredByUserId(UserInfo enteredByUserId) {
        this.enteredByUser = enteredByUserId;
    }

    public LogTopic getLogTopicId() {
        return logTopic;
    }

    public void setLogTopicId(LogTopic logTopicId) {
        this.logTopic = logTopicId;
    }
    
    public String getShortDisplayEnteredOnDateTime() {
        if (enteredOnDateTime == null) {
            return null;
        }
        return shortDisplayDateFormat.format(enteredOnDateTime);

    }
    
    public void addLogLevel(LogLevel logLevel) {
        if (logLevelList == null) {
            logLevelList = new ArrayList<>(); 
        }
        logLevelList.add(logLevel);
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
        if (!(object instanceof Log)) {
            return false;
        }
        Log other = (Log) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.anl.aps.cdb.portal.model.db.entities.Log[ id=" + id + " ]";
    }
    
}
