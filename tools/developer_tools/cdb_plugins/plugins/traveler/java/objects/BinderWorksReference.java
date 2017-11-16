/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler.objects;

/**
 *
 * @author djarosz
 */
public class BinderWorksReference extends TravelerObject {
    private String alias; 
    private String refType;
    private String addedOn;
    private String addedBy;
    private String color; 
    
    private Double status;
    private Double inProgress;
    
    private Integer value;
    private Integer sequence;
    private Integer priority;    
    private Integer finished; 

    public String getAlias() {
        return alias;
    }

    public String getRefType() {
        return refType;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public String getColor() {
        return color;
    }

    public Double getStatus() {
        return status;
    }

    public Integer getValue() {
        return value;
    }

    public Integer getSequence() {
        return sequence;
    }

    public Integer getPriority() {
        return priority;
    }

    public Double getInProgress() {
        return inProgress;
    }

    public Integer getFinished() {
        return finished;
    }
    
}
