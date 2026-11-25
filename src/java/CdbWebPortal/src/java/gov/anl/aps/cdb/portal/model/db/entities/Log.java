/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEnteredOnDateTime() {
        return enteredOnDateTime;
    }

    public void setEnteredOnDateTime(Date enteredOnDateTime) {
        this.enteredOnDateTime = enteredOnDateTime;
    }
    
    @JsonIgnore
    public UserInfo getEnteredByUser() {
        return this.enteredByUser; 
    }
        
    public void setEnteredByUser(UserInfo userInfo) {
        this.enteredByUser = userInfo; 
    }
    
    public String getEnteredByUsername() {
        return this.enteredByUser.getUsername(); 
    }

    @JsonIgnore
    public LogTopic getLogTopic() {
        return logTopic;
    }

    public void setLogTopic(LogTopic logTopic) {
        this.logTopic = logTopic;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEffectiveFromDateTime() {
        return effectiveFromDateTime;
    }

    public void setEffectiveFromDateTime(Date effectiveFromDateTime) {
        this.effectiveFromDateTime = effectiveFromDateTime;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEffectiveToDateTime() {
        return effectiveToDateTime;
    }

    public void setEffectiveToDateTime(Date effectiveToDateTime) {
        this.effectiveToDateTime = effectiveToDateTime;
    }

    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    @XmlTransient
    @JsonIgnore
    public List<LogLevel> getLogLevelList() {
        return logLevelList;
    }

    public void setLogLevelList(List<LogLevel> logLevelList) {
        this.logLevelList = logLevelList;
    }

    @XmlTransient
    @JsonIgnore
    public List<ItemElement> getItemElementList() {
        return itemElementList;
    }

    public void setItemElementList(List<ItemElement> itemElementList) {
        this.itemElementList = itemElementList;
    }
    
    @JsonIgnore
    public UserInfo getEnteredByUserId() {
        return enteredByUser;
    }

    public void setEnteredByUserId(UserInfo enteredByUserId) {
        this.enteredByUser = enteredByUserId;
    }

    @JsonIgnore
    public LogTopic getLogTopicId() {
        return logTopic;
    }

    public void setLogTopicId(LogTopic logTopicId) {
        this.logTopic = logTopicId;
    }
    
    @JsonIgnore
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
