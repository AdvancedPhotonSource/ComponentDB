/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
 * Log entity class.
 */
@Entity
@Table(name = "log")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Log.findAll", query = "SELECT l FROM Log l"),
    @NamedQuery(name = "Log.findById", query = "SELECT l FROM Log l WHERE l.id = :id"),
    @NamedQuery(name = "Log.findByEnteredOnDateTime", query = "SELECT l FROM Log l WHERE l.enteredOnDateTime = :enteredOnDateTime")})
public class Log extends CdbEntity {

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
    @ManyToMany(mappedBy = "logList")
    private List<ComponentInstance> componentInstanceList;
    @ManyToMany(mappedBy = "logList")
    private List<Component> componentList;
    @ManyToMany(mappedBy = "logList")
    private List<DesignElement> designElementList;
    @ManyToMany(mappedBy = "logList")
    private List<Design> designList;
    @JoinColumn(name = "entered_by_user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserInfo enteredByUser;
    @JoinTable(name = "log_attachment", joinColumns = {
        @JoinColumn(name = "log_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "attachment_id", referencedColumnName = "id")})
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Attachment> attachmentList;
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
    }

    @Override
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

    @XmlTransient
    public List<ComponentInstance> getComponentInstanceList() {
        return componentInstanceList;
    }

    public void setComponentInstanceList(List<ComponentInstance> componentInstanceList) {
        this.componentInstanceList = componentInstanceList;
    }

    @XmlTransient
    public List<Component> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<Component> componentList) {
        this.componentList = componentList;
    }

    @XmlTransient
    public List<DesignElement> getDesignElementList() {
        return designElementList;
    }

    public void setDesignElementList(List<DesignElement> designElementList) {
        this.designElementList = designElementList;
    }

    @XmlTransient
    public List<Design> getDesignList() {
        return designList;
    }

    public void setDesignList(List<Design> designList) {
        this.designList = designList;
    }

    public UserInfo getEnteredByUser() {
        return enteredByUser;
    }

    public void setEnteredByUser(UserInfo enteredByUser) {
        this.enteredByUser = enteredByUser;
    }

    @XmlTransient
    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public LogTopic getLogTopic() {
        return logTopic;
    }

    public void setLogTopic(LogTopic logTopic) {
        this.logTopic = logTopic;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public boolean equalsByText(Log other) {
        if (other != null) {
            return ObjectUtility.equals(this.text, other.text);
        }
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Log)) {
            return false;
        }
        Log other = (Log) object;
        if (this.id == null && other.id == null) {
            return equalsByText(other);
        }

        if (this.id == null || other.id == null) {
            return false;
        }
        return this.id.equals(other.id);
    }

    public String getShortDisplayEnteredOnDateTime() {
        if (enteredOnDateTime == null) {
            return null;
        }
        return shortDisplayDateFormat.format(enteredOnDateTime);

    }

    @Override
    public String toString() {
        return text;
    }

}
