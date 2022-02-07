/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.model.ItemDomainLocationTreeNode.ItemDomainLocationTreeConfiguration;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainLocationFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.List;

/**
 *
 * @author darek
 */
public class ItemDomainLocationTreeNode extends ItemBaseLazyTreeNode<ItemDomainLocation, ItemDomainLocationFacade, ItemDomainLocationTreeConfiguration>{

    public ItemDomainLocationTreeNode(ItemElement element, ItemDomainLocationTreeConfiguration config, ItemBaseLazyTreeNode parent) {
        super(element, config, parent);
    }

    public ItemDomainLocationTreeNode(List<ItemDomainLocation> items, Domain domain, ItemDomainLocationFacade facade) {
        super(items, domain, facade);
    }

    @Override
    protected ItemDomainLocationTreeNode createTreeNodeObject(ItemElement itemElement) {
        return new ItemDomainLocationTreeNode(itemElement, config, this);
    }

    @Override
    protected ItemDomainLocationTreeNode createTreeNodeObject(ItemElement element, ItemDomainLocationTreeConfiguration config, ItemBaseLazyTreeNode parent) {
        return new ItemDomainLocationTreeNode(element, config, parent);
    }

    @Override
    public ItemDomainLocationTreeConfiguration createTreeNodeConfiguration() {
        return new ItemDomainLocationTreeConfiguration();
    }
    
    public class ItemDomainLocationTreeConfiguration extends ItemTreeBaseConfiguration {

        public ItemDomainLocationTreeConfiguration() {
        }
        
    }
    
}
