/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.ConnectorType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.StoredProcedureQuery;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainInventoryFacade extends ItemFacadeBase<ItemDomainInventory> {

    @Override
    public String getDomainName() {
        return ItemDomainName.inventory.getValue();
    }
    
    public ItemDomainInventoryFacade() {
        super(ItemDomainInventory.class);
    }

    public List<ItemDomainInventory> getInventoryItemsWithAvailableConnectorType(ConnectorType connectorType, Boolean isMale) {
        try {
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("item.inventoryItemsWithConnectorType");
            query.setParameter("connector_type_id", connectorType.getId());
            query.setParameter("is_male", isMale);
            List<ItemDomainInventory> itemList = query.getResultList();

            return itemList;
        } catch (NoResultException ex) {
        }
        return null;
    }
    
    public static ItemDomainInventoryFacade getInstance() {
        return (ItemDomainInventoryFacade) SessionUtility.findFacade(ItemDomainInventoryFacade.class.getSimpleName()); 
    }

}
