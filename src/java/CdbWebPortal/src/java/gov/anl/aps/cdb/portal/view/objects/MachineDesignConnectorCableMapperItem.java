/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.primefaces.event.diagram.ConnectEvent;
import org.primefaces.event.diagram.ConnectionChangeEvent;
import org.primefaces.event.diagram.DisconnectEvent;
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.StraightConnector;
import org.primefaces.model.diagram.endpoint.DotEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.endpoint.RectangleEndPoint;
import org.primefaces.model.diagram.overlay.ArrowOverlay;

/**
 *
 * @author djarosz
 */
public class MachineDesignConnectorCableMapperItem implements Serializable {

    private DefaultDiagramModel model;

    private boolean suspendEvent;
    private int connectorLocation = 0;
    private String connectorVLocation;
    private int locationIncr = 10;

    public MachineDesignConnectorCableMapperItem(List<MachineDesignConnectorListObject> mdConnectorListObject) {
        model = new DefaultDiagramModel();
        model.setMaxConnections(2);

        model.getDefaultConnectionOverlays().add(new ArrowOverlay(20, 20, 1, 1));
        StraightConnector connector = new StraightConnector();
        model.setDefaultConnector(connector);

        int relationshipLocation = 2;
        String relationshipVLocation = "0em";
        connectorLocation = 0;
        connectorVLocation = "15em";

        for (MachineDesignConnectorListObject mdConn : mdConnectorListObject) {
            ItemElementRelationship relationship = mdConn.getCableRelationship();
            ItemConnector itemConnector = mdConn.getItemConnector();
            Element cableElement = null;
            if (relationship != null) {
                cableElement = new Element(new CableRelationshipWiringElement(relationship), relationshipLocation + "em", relationshipVLocation);
                EndPoint endPointCA = createRectangleEndPoint(EndPointAnchor.BOTTOM);
                endPointCA.setSource(true);
                cableElement.addEndPoint(endPointCA);

                model.addElement(cableElement);

                relationshipLocation += locationIncr;
            }

            if (itemConnector != null) {
                Element connectorElement = new Element(new ItemConnectorWiringElement(itemConnector), connectorLocation + "em", connectorVLocation);
                EndPoint endPointSA = createDotEndPoint(EndPointAnchor.AUTO_DEFAULT);
                endPointSA.setMaxConnections(1);
                endPointSA.setTarget(true);
                connectorElement.addEndPoint(endPointSA);

                model.addElement(connectorElement);
                connectorLocation += locationIncr;                

                if (cableElement != null) {
                    Connection conn = new Connection(cableElement.getEndPoints().get(0), connectorElement.getEndPoints().get(0));
                    model.connect(conn);
                }
            }
        }
    }

    public DiagramModel getModel() {
        return model;
    }

    public void onConnect(ConnectEvent event) {
        Element source = event.getSourceElement();
        Element target = event.getTargetElement();

        changeConnection(source, target, true);
    }

    public void onDisconnect(DisconnectEvent event) {
        Element source = event.getSourceElement();
        Element target = event.getTargetElement();

        changeConnection(source, target, false);
    }

    /**
     * Function will perform necessary model changes to connect and disconnect
     * items
     *
     * @param source
     * @param targetElement
     * @param connected Connect items when true and disconnect when false
     */
    private void changeConnection(Element source, Element target, boolean connected) {
        CableRelationshipWiringElement cableElement = (CableRelationshipWiringElement) source.getData();
        ItemConnectorWiringElement connectorWiringElement = (ItemConnectorWiringElement) target.getData();

        ItemElementRelationship relationship = cableElement.getRelationship();
        ItemConnector itemConnector = connectorWiringElement.getItemConnector();

        if (connected) {
            relationship.setFirstItemConnector(itemConnector);
        } else {
            relationship.setFirstItemConnector(null);
        }
    }

    public void onConnectionChange(ConnectionChangeEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Connection Changed",
                "Original Source:" + event.getOriginalSourceElement().getData()
                + ", New Source: " + event.getNewSourceElement().getData()
                + ", Original Target: " + event.getOriginalTargetElement().getData()
                + ", New Target: " + event.getNewTargetElement().getData());

        FacesContext.getCurrentInstance().addMessage(null, msg);

        PrimeFaces.current().ajax().update("form:msgs");
        suspendEvent = true;
    }

    private EndPoint createDotEndPoint(EndPointAnchor anchor) {
        DotEndPoint endPoint = new DotEndPoint(anchor);
        endPoint.setScope("network");
        endPoint.setTarget(true);
        endPoint.setStyle("{fillStyle:'#98AFC7'}");
        endPoint.setHoverStyle("{fillStyle:'#5C738B'}");

        return endPoint;
    }

    private EndPoint createRectangleEndPoint(EndPointAnchor anchor) {
        RectangleEndPoint endPoint = new RectangleEndPoint(anchor);
        endPoint.setScope("network");
        endPoint.setSource(true);
        endPoint.setStyle("{fillStyle:'#98AFC7'}");
        endPoint.setHoverStyle("{fillStyle:'#5C738B'}");

        return endPoint;
    }

    public class CableRelationshipWiringElement extends WiringElement {

        ItemElementRelationship relationship;

        public CableRelationshipWiringElement(ItemElementRelationship cableRelationship) {
            ItemDomainCableDesign cd = (ItemDomainCableDesign) cableRelationship.getSecondItemElement().getParentItem();
            String name = cd.getName();
            super.name = name;
            relationship = cableRelationship;
        }

        @Override
        public String getIcon() {
            return "cable.svg";
        }

        public ItemElementRelationship getRelationship() {
            return relationship;
        }

    }

    public class ItemConnectorWiringElement extends WiringElement {

        ItemConnector itemConnector;

        public ItemConnectorWiringElement(ItemConnector ic) {
            itemConnector = ic;
            String type = itemConnector.getConnector().getConnectorType().getName();
            String name = itemConnector.getConnector().getName();

            String label = name + "(" + type + ")";
            super.name = label;
        }

        public ItemConnector getItemConnector() {
            return itemConnector;
        }

        public void setItemConnector(ItemConnector itemConnector) {
            this.itemConnector = itemConnector;
        }

        @Override
        public String getIcon() {
            return "item-connector.svg";
        }

    }

    public abstract class WiringElement implements Serializable {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public abstract String getIcon();

        @Override
        public String toString() {
            return name;
        }

    }
}
