/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.rest.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.view.objects.CableDesignConnectionListObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author djarosz
 */
public class CableDesignConnectionListSummaryObject extends CableDesignConnectionListObject {

    public CableDesignConnectionListSummaryObject(ItemDomainCableDesign cableDesign) {
        super(cableDesign);
    }

    public Integer getMdItemId() {
        return getMdItem().getId();
    }

    public Integer getCableRelationshipId() {
        return getCableRelationship().getId();
    }

    @JsonIgnore
    @Override
    public ItemDomainCableDesign getCableDesign() {
        return super.getCableDesign();
    }

    @JsonIgnore
    @Override
    public ItemConnector getItemConnector() {
        return super.getItemConnector();
    }

    @JsonIgnore
    @Override
    public ItemElementRelationship getCableRelationship() {
        return super.getCableRelationship();
    }

    @JsonIgnore
    @Override
    public ItemConnector getMdConnector() {
        return super.getMdConnector();
    }

    @JsonIgnore
    @Override
    public ItemDomainMachineDesign getMdItem() {
        return super.getMdItem(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    public static List<CableDesignConnectionListSummaryObject> getConnectionSummaryList(ItemDomainCableDesign item) {
        List<CableDesignConnectionListSummaryObject> connList = new ArrayList<>();

        // add entries for each cable relationship (connection to MD item)
        List<ItemElementRelationship> cableRelationshipList = getCableRelationshipList(item);

        for (ItemElementRelationship cableRelationship : cableRelationshipList) {
            CableDesignConnectionListSummaryObject connection = new CableDesignConnectionListSummaryObject(item);
            connection.setCableRelationship(cableRelationship, item);
            connList.add(connection);
        }

        return sortConnectionList(connList);
    }

}
