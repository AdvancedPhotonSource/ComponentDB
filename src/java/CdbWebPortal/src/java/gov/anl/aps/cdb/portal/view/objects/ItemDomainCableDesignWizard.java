/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import java.io.Serializable;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.TreeNode;

/**
 * This class is intended to be used by another controller class, e.g.,
 * ItemDomainMachineDesignController, and is utilized when adding a new cable
 * design item.
 * 
 * I have not added annotations for the named bean or scope since it's not
 * envisioned to be used that way, and I'm not sure if they'd cause problems by
 * being present.
 * 
 * @author cmcchesney
 */
public class ItemDomainCableDesignWizard implements Serializable {

    private TreeNode endpoint1 = null;
    private TreeNode endpoint2 = null;
    private String cableType = null;

    public TreeNode getEndpoint1() {
        return endpoint1;
    }

    public void setEndpoint1(TreeNode endpoint1) {
        this.endpoint1 = endpoint1;
    }

    public TreeNode getEndpoint2() {
        return endpoint2;
    }

    public void setEndpoint2(TreeNode endpoint2) {
        this.endpoint2 = endpoint2;
    }

    public String getCableType() {
        return cableType;
    }

    public void setCableType(String cableType) {
        this.cableType = cableType;
    }
    
    public String onFlowProcess(FlowEvent event) {
        return event.getNewStep();
    }

}
