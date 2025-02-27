/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;

/**
 *
 * @author djarosz
 */
public class ItemDomainCableDesignMetadata {

    private Integer cableDesignId;

    private String externalCableName;
    private String importCableId;
    private String alternateCableId;
    private String laying;
    private String voltage;
    private String routedLength;
    private String route;
    private String totalReqLength;
    private String notes;

    private String endpoint1Description;
    private String endpoint1Route;
    private String endpoint1EndLength;
    private String endpoint1Termination;
    private String endpoint1Pinlist;
    private String endpoint1Notes;
    private String endpoint1Drawing;

    private String endpoint2Description;
    private String endpoint2Route;
    private String endpoint2EndLength;
    private String endpoint2Termination;
    private String endpoint2Pinlist;
    private String endpoint2Notes;
    private String endpoint2Drawing;

    public ItemDomainCableDesignMetadata() {
    }

    // Constructor to initialize all fields
    public ItemDomainCableDesignMetadata(ItemDomainCableDesign cableDesignItem) throws CdbException {
        this.cableDesignId = cableDesignItem.getId();
        this.externalCableName = cableDesignItem.getExternalCableName();
        this.importCableId = cableDesignItem.getImportCableId();
        this.alternateCableId = cableDesignItem.getAlternateCableId();
        this.laying = cableDesignItem.getLaying();
        this.voltage = cableDesignItem.getVoltage();
        this.routedLength = cableDesignItem.getRoutedLength();
        this.route = cableDesignItem.getRoute();
        this.totalReqLength = cableDesignItem.getTotalReqLength();
        this.notes = cableDesignItem.getNotes();

        this.endpoint1Description = cableDesignItem.getEndpoint1Description();
        this.endpoint1Route = cableDesignItem.getEndpoint1Route();
        this.endpoint1EndLength = cableDesignItem.getEndpoint1EndLength();
        this.endpoint1Termination = cableDesignItem.getEndpoint1Termination();
        this.endpoint1Pinlist = cableDesignItem.getEndpoint1Pinlist();
        this.endpoint1Notes = cableDesignItem.getEndpoint1Notes();
        this.endpoint1Drawing = cableDesignItem.getEndpoint1Drawing();

        this.endpoint2Description = cableDesignItem.getEndpoint2Description();
        this.endpoint2Route = cableDesignItem.getEndpoint2Route();
        this.endpoint2EndLength = cableDesignItem.getEndpoint2EndLength();
        this.endpoint2Termination = cableDesignItem.getEndpoint2Termination();
        this.endpoint2Pinlist = cableDesignItem.getEndpoint2Pinlist();
        this.endpoint2Notes = cableDesignItem.getEndpoint2Notes();
        this.endpoint2Drawing = cableDesignItem.getEndpoint2Drawing();
    }

    public Integer getCableDesignId() {
        return cableDesignId;
    }

    public void setCableDesignId(Integer cableDesignId) {
        this.cableDesignId = cableDesignId;
    }

    public String getExternalCableName() {
        return externalCableName;
    }

    public void setExternalCableName(String externalCableName) {
        this.externalCableName = externalCableName;
    }

    public String getImportCableId() {
        return importCableId;
    }

    public void setImportCableId(String importCableId) {
        this.importCableId = importCableId;
    }

    public String getAlternateCableId() {
        return alternateCableId;
    }

    public void setAlternateCableId(String alternateCableId) {
        this.alternateCableId = alternateCableId;
    }

    public String getLaying() {
        return laying;
    }

    public void setLaying(String laying) {
        this.laying = laying;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getRoutedLength() {
        return routedLength;
    }

    public void setRoutedLength(String routedLength) {
        this.routedLength = routedLength;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTotalReqLength() {
        return totalReqLength;
    }

    public void setTotalReqLength(String totalReqLength) {
        this.totalReqLength = totalReqLength;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getEndpoint1Description() {
        return endpoint1Description;
    }

    public void setEndpoint1Description(String endpoint1Description) {
        this.endpoint1Description = endpoint1Description;
    }

    public String getEndpoint1Route() {
        return endpoint1Route;
    }

    public void setEndpoint1Route(String endpoint1Route) {
        this.endpoint1Route = endpoint1Route;
    }

    public String getEndpoint1EndLength() {
        return endpoint1EndLength;
    }

    public void setEndpoint1EndLength(String endpoint1EndLength) {
        this.endpoint1EndLength = endpoint1EndLength;
    }

    public String getEndpoint1Termination() {
        return endpoint1Termination;
    }

    public void setEndpoint1Termination(String endpoint1Termination) {
        this.endpoint1Termination = endpoint1Termination;
    }

    public String getEndpoint1Pinlist() {
        return endpoint1Pinlist;
    }

    public void setEndpoint1Pinlist(String endpoint1Pinlist) {
        this.endpoint1Pinlist = endpoint1Pinlist;
    }

    public String getEndpoint1Notes() {
        return endpoint1Notes;
    }

    public void setEndpoint1Notes(String endpoint1Notes) {
        this.endpoint1Notes = endpoint1Notes;
    }

    public String getEndpoint1Drawing() {
        return endpoint1Drawing;
    }

    public void setEndpoint1Drawing(String endpoint1Drawing) {
        this.endpoint1Drawing = endpoint1Drawing;
    }

    public String getEndpoint2Description() {
        return endpoint2Description;
    }

    public void setEndpoint2Description(String endpoint2Description) {
        this.endpoint2Description = endpoint2Description;
    }

    public String getEndpoint2Route() {
        return endpoint2Route;
    }

    public void setEndpoint2Route(String endpoint2Route) {
        this.endpoint2Route = endpoint2Route;
    }

    public String getEndpoint2EndLength() {
        return endpoint2EndLength;
    }

    public void setEndpoint2EndLength(String endpoint2EndLength) {
        this.endpoint2EndLength = endpoint2EndLength;
    }

    public String getEndpoint2Termination() {
        return endpoint2Termination;
    }

    public void setEndpoint2Termination(String endpoint2Termination) {
        this.endpoint2Termination = endpoint2Termination;
    }

    public String getEndpoint2Pinlist() {
        return endpoint2Pinlist;
    }

    public void setEndpoint2Pinlist(String endpoint2Pinlist) {
        this.endpoint2Pinlist = endpoint2Pinlist;
    }

    public String getEndpoint2Notes() {
        return endpoint2Notes;
    }

    public void setEndpoint2Notes(String endpoint2Notes) {
        this.endpoint2Notes = endpoint2Notes;
    }

    public String getEndpoint2Drawing() {
        return endpoint2Drawing;
    }

    public void setEndpoint2Drawing(String endpoint2Drawing) {
        this.endpoint2Drawing = endpoint2Drawing;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return externalCableName == null
                && importCableId == null
                && alternateCableId == null
                && laying == null
                && voltage == null
                && routedLength == null
                && route == null
                && totalReqLength == null
                && notes == null
                && endpoint1Description == null
                && endpoint1Route == null
                && endpoint1EndLength == null
                && endpoint1Termination == null
                && endpoint1Pinlist == null
                && endpoint1Notes == null
                && endpoint1Drawing == null
                && endpoint2Description == null
                && endpoint2Route == null
                && endpoint2EndLength == null
                && endpoint2Termination == null
                && endpoint2Pinlist == null
                && endpoint2Notes == null
                && endpoint2Drawing == null;

    }

}
