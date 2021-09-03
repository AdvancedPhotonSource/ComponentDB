/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignRelationshipTreeNode.MachineTreeRelationshipConfiguration;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dariusz
 */
public class ItemDomainMachineDesignRelationshipTreeNode extends ItemDomainMachineDesignBaseTreeNode<MachineTreeRelationshipConfiguration> {   

    public ItemDomainMachineDesignRelationshipTreeNode() {
    }

    public ItemDomainMachineDesignRelationshipTreeNode(List<ItemDomainMachineDesign> items, Domain domain,
            ItemDomainMachineDesignFacade facade, ItemElementRelationshipTypeNames relationshipToLoad) {
        MachineTreeRelationshipConfiguration config = new MachineTreeRelationshipConfiguration(relationshipToLoad);
        initFirstLevel(items, domain, facade, config);
    }

    public ItemDomainMachineDesignRelationshipTreeNode(ItemDomainMachineDesign item, Domain domain,
            ItemDomainMachineDesignFacade facade, ItemElementRelationshipTypeNames relationshipToLoad, 
            boolean setTypes, boolean loadAllChildren) {
        List<ItemDomainMachineDesign> items = new ArrayList<>();
        items.add(item);
        MachineTreeRelationshipConfiguration config = new MachineTreeRelationshipConfiguration(relationshipToLoad);
        config.setLoadAllChildren(loadAllChildren);
        config.setSetMachineTreeNodeType(setTypes);
        initFirstLevel(items, domain, facade, config);
    }   

    protected ItemDomainMachineDesignRelationshipTreeNode(ItemElement element, MachineTreeRelationshipConfiguration config, ItemDomainMachineDesignRelationshipTreeNode parent, boolean setType) {
        super(element, config, parent, setType);        
    }

    @Override
    protected ItemDomainMachineDesignRelationshipTreeNode createTreeNodeObject(ItemElement itemElement) {
        return new ItemDomainMachineDesignRelationshipTreeNode(itemElement, config, this, true);
    }
        
    @Override
    protected ItemDomainMachineDesignRelationshipTreeNode createTreeNodeObject(ItemElement element, MachineTreeRelationshipConfiguration config, ItemDomainMachineDesignBaseTreeNode parent, boolean setType) {
        return new ItemDomainMachineDesignRelationshipTreeNode(element, config, this, setType); 
    }

    @Override
    protected void loadRelationships() {        
        ItemElementRelationshipTypeNames relationshipToLoad = getConfig().getRelationshipToLoad();
        String relationshipTypeName = relationshipToLoad.getValue();
        
        loadRelationshipsFromRelationshipList(false, relationshipTypeName, "machineDesignRelationshipNode");       
    }

    @Override
    protected boolean isLoadElementChildren() {
        ItemElement element = this.getElement();
        Item containedItem = element.getContainedItem();
        if (containedItem instanceof ItemDomainMachineDesign) {
            return ItemDomainMachineDesign.isItemControl(containedItem);
        }

        return true;
    }

    @Override
    public MachineTreeRelationshipConfiguration createTreeNodeConfiguration() {
        return new MachineTreeRelationshipConfiguration(); 
    }   
    
    public class MachineTreeRelationshipConfiguration extends MachineTreeBaseConfiguration {
        
        ItemElementRelationshipTypeNames relationshipToLoad;

        public MachineTreeRelationshipConfiguration() {
        }

        public MachineTreeRelationshipConfiguration(ItemElementRelationshipTypeNames relationshipToLoad) {
            this.relationshipToLoad = relationshipToLoad;
        }

        public ItemElementRelationshipTypeNames getRelationshipToLoad() {
            return relationshipToLoad;
        }
        
    }

}
