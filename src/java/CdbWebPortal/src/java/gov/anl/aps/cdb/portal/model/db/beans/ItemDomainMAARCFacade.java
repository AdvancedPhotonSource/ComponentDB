/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMAARC;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainMAARCFacade extends ItemFacadeBase<ItemDomainMAARC> {
    
    @Override
    public ItemDomainName getDomain() {
        return ItemDomainName.maarc;
    }
    
    public ItemDomainMAARCFacade() {
        super(ItemDomainMAARC.class);
    }
    
    public static ItemDomainMAARCFacade getInstance() {
        return (ItemDomainMAARCFacade) SessionUtility.findFacade(ItemDomainMAARCFacade.class.getSimpleName()); 
    }
    
    public List<ItemDomainMAARC> findByName(String name) {
        return findByDomainAndName(getDomainName(), name);
    }  
    
    public List<ItemDomainMAARC> findByUniqueFields(String name, String experimentName, String experimentFilePath) {
        return findByDomainNameNameItemIdentifier1AndItemIdentifier2(getDomainName(), name, experimentName, experimentFilePath); 
    }
}
