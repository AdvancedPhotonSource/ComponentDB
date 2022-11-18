/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignRelationshipTreeNode.MachineTreeRelationshipConfiguration;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
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
            ItemDomainMachineDesignFacade facade, ItemElementRelationshipTypeNames relationshipToLoad, EntityTypeName entity) {
        MachineTreeRelationshipConfiguration config = new MachineTreeRelationshipConfiguration(relationshipToLoad, entity);
        initFirstLevel(items, domain, facade, config);
    }

    public ItemDomainMachineDesignRelationshipTreeNode(ItemDomainMachineDesign item, Domain domain,
            ItemDomainMachineDesignFacade facade, 
            ItemElementRelationshipTypeNames relationshipToLoad, EntityTypeName entity,
            boolean setTypes, boolean loadAllChildren) {
        List<ItemDomainMachineDesign> items = new ArrayList<>();
        items.add(item);
        MachineTreeRelationshipConfiguration config = new MachineTreeRelationshipConfiguration(relationshipToLoad, entity);
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
    protected <T extends ItemDomainMachineDesignBaseTreeNode> T createSearchResultChildNode(ItemDomainMachineDesignBaseTreeNode parentNode, ItemElement ie, boolean childrenLoaded) {
        Item item = ie.getContainedItem();
        EntityTypeName defaultEntityTypeName = config.getDefaultEntityTypeName();
        if (ItemDomainMachineDesign.isItemEntityType(item, defaultEntityTypeName.getValue())) {
            return super.createSearchResultChildNode(parentNode, ie, childrenLoaded); 
        }
        
        ItemDomainMachineDesignBaseTreeNode newNode = parentNode.createChildNode(ie, childrenLoaded, false);
        String type = MachineTreeRelationshipConfiguration.RELATIONSHIP_CHILD_NODE_TYPE;
        newNode.setType(type);
        return (T) newNode;         
    }

    @Override
    protected List<ItemDomainMachineDesign> getParentItems(ItemDomainMachineDesign item) {
        ItemDomainMachineDesignFacade designFacade = config.getFacade();
        ItemElementRelationshipTypeNames relationshipToLoad = config.getRelationshipToLoad();
        
        Integer currentItemId = item.getId();
        Integer controlChildItemId = item.getControlChildItemId();
        List<ItemDomainMachineDesign> relationshiParentItems = designFacade.fetchRelationshipParentItems(currentItemId, relationshipToLoad.getDbId(), controlChildItemId);
        
        // Set control child for referencing in next itteration. 
        for (ItemDomainMachineDesign parent : relationshiParentItems) {
            parent.setControlChildItemId(currentItemId);
        }
        
        return relationshiParentItems; 
    }

    @Override
    protected ItemElement getParentItemElement(ItemDomainMachineDesign item) {
        EntityTypeName defaultEntityTypeName = config.getDefaultEntityTypeName();
        if (ItemDomainMachineDesign.isItemEntityType(item, defaultEntityTypeName.getValue())) {
            return super.getParentItemElement(item);             
        } 
        
        return createMockItemElement(item);
    }

    @Override
    protected List<ItemDomainMachineDesign> fetchMachineItemsByNameFilter(String nameFilter) {
        ItemDomainMachineDesignFacade designFacade = config.getFacade();
        int relationshipId = config.getRelationshipToLoad().getDbId();
        int entityTypeId = config.defaultEntityTypeName.getDbId();
        return designFacade.fetchNameFilterForRelationshipHierarchy(domain.getId(), entityTypeId, relationshipId, nameFilter); 
    }

    @Override
    protected void loadAdditionalChildren() {        
        ItemElementRelationshipTypeNames relationshipToLoad = getConfig().getRelationshipToLoad();
        String type = MachineTreeRelationshipConfiguration.RELATIONSHIP_CHILD_NODE_TYPE;
        loadRelationshipsFromRelationshipList(false, relationshipToLoad, type);
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
        
        public static final String RELATIONSHIP_CHILD_NODE_TYPE = "machineDesignRelationshipNode"; 
        
        ItemElementRelationshipTypeNames relationshipToLoad;
        EntityTypeName defaultEntityTypeName; 

        public MachineTreeRelationshipConfiguration() {
        }       

        public MachineTreeRelationshipConfiguration(ItemElementRelationshipTypeNames relationshipToLoad, EntityTypeName defaultEntityTypeName) {
            this.relationshipToLoad = relationshipToLoad;
            this.defaultEntityTypeName = defaultEntityTypeName;
        }

        public ItemElementRelationshipTypeNames getRelationshipToLoad() {
            return relationshipToLoad;
        }

        public EntityTypeName getDefaultEntityTypeName() {
            return defaultEntityTypeName;
        }
        
    }

}
