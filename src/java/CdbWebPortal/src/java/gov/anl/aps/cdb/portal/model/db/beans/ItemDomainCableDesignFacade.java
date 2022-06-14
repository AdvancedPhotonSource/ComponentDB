/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.AdvancedFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainCableDesignFacade extends ItemFacadeBase<ItemDomainCableDesign> {
    
    public final static String FILTER_NAME_ANCESTOR_ANY = "Endpoint Ancestor Name";
    public final static String FILTER_NAME_ANCESTOR_BY_END = "End1/2 Endpoint Ancestor Name";
    public final static String FILTER_PARAM_ANCESTOR_NAME = "Ancestor Name Contains";
    public final static String FILTER_PARAM_ANCESTOR_NAME_END1 = "End1 Ancestor Name Contains";
    public final static String FILTER_PARAM_ANCESTOR_NAME_END2 = "End2 Ancestor Name Contains";
    
    @Override
    public ItemDomainName getDomain() {
        return ItemDomainName.cableDesign;
    }
    
    public ItemDomainCableDesignFacade() {
        super(ItemDomainCableDesign.class);
    }
    
    public static ItemDomainCableDesignFacade getInstance() {
        return (ItemDomainCableDesignFacade) 
                SessionUtility.findFacade(ItemDomainCableDesignFacade.class.getSimpleName()); 
    }
    
    @Override
    public List<AdvancedFilter> initializeAdvancedFilterInfo(ItemController controller) {
        List<AdvancedFilter> filters = new ArrayList<>();
        
        AdvancedFilter filter = new AdvancedFilter(
                FILTER_NAME_ANCESTOR_ANY, 
                "Machine design parents of cable endpoints",
                controller);
        filter.addParameter(
                FILTER_PARAM_ANCESTOR_NAME, 
                "Substring to match in machine hierarchy for endpoint devices");
        filters.add(filter);
        
        filter = new AdvancedFilter(
                FILTER_NAME_ANCESTOR_BY_END, 
                "Machine design parents of cable endpoints constrained to cable end",
                controller);
        filter.addParameter(
                FILTER_PARAM_ANCESTOR_NAME_END1, 
                "Substring to match in machine hierarchy for endpoint devices on end 1");
        filter.addParameter(
                FILTER_PARAM_ANCESTOR_NAME_END2, 
                "Substring to match in machine hierarchy for endpoint devices on end 2");
        filters.add(filter);
        
        return filters;
    }
    
    public List<ItemDomainCableDesign> processAdvancedFilter(String name, Map<String, String> parameterValueMap) {
        
        if (name == null || name.isEmpty()) {
            return null;
        }
        
        List<ItemDomainCableDesign> result = null;
        switch (name) {
            
            case FILTER_NAME_ANCESTOR_ANY:
                result = processAdvancedFilterAncestorAny(parameterValueMap.get(FILTER_PARAM_ANCESTOR_NAME));
                break;
                
            case FILTER_NAME_ANCESTOR_BY_END:
                result = processAdvancedFilterAncestorByEnd(
                        parameterValueMap.get(FILTER_PARAM_ANCESTOR_NAME_END1),
                        parameterValueMap.get(FILTER_PARAM_ANCESTOR_NAME_END2));
                break;
                
        }
        
        return result;
    }
    
    public List<ItemDomainCableDesign> processAdvancedFilterAncestorAny(String ancestorName) {
        try {
            return (List<ItemDomainCableDesign>) em.createNamedQuery("ItemDomainCableDesign.filterAncestorAny")
                    .setParameter("domainName", getDomainName())
                    .setParameter("nameFilterValue", ancestorName)
                    .getResultList();
        } catch (NoResultException ex) {
        }
        return null;
    }
    
    public List<ItemDomainCableDesign> processAdvancedFilterAncestorByEnd(
            String end1AncestorName, String end2AncestorName) {
        
        try {
            return (List<ItemDomainCableDesign>) em.createNamedQuery("ItemDomainCableDesign.filterAncestorByEnd")
                    .setParameter("domainName", getDomainName())
                    .setParameter("end1Value", end1AncestorName)
                    .setParameter("end2Value", end2AncestorName)
                    .getResultList();
        } catch (NoResultException ex) {
        }
        return null;
    }

    /**
     * Creates new cable design item.  Overridden here so that we can manually create
     * new ItemConnectors since there is no PERSIST cascade on IER.firstItemConnector
     */
    @Override
    public void create(ItemDomainCableDesign entity) {     
        
        for (ItemConnector itemConnector : entity.getNewItemConnectorList()) {
            if (itemConnector != null) {
                ItemConnectorFacade.getInstance().create(itemConnector);
            }
        }
        entity.clearNewItemConnectorList();

        super.create(entity);
    }

    /**
     * Updates cable design item.  Overridden here because, if we edit a cable
     * to remove cable relationship objects they are not removed from the database
     * by updating the database for that cable.  Thus they are removed explicitly
     * here.
     */
    @Override
    public ItemDomainCableDesign edit(ItemDomainCableDesign entity) {
        
        ItemDomainCableDesign result = super.edit(entity);
        
        for (ItemElementRelationship ier : entity.getDeletedIerList()) {
            ItemElementRelationshipFacade.getInstance().remove(ier);
        }
        entity.clearDeletedIerList();
        
        return result;
    } 

    @Override
    public List<ItemDomainCableDesign> searchEntities(String searchString) {
        try {            
            searchString = convertWildcards(searchString); 
            return (List<ItemDomainCableDesign>) em.createNamedStoredProcedureQuery("itemCableDesign.searchItems")                    
                    .setParameter("search_string", searchString)
                    .setParameter("limit_row", SEARCH_RESULT_LIMIT)
                    .getResultList();
        } catch (NoResultException ex) {

        }
        return null;
    }

    public List<ItemDomainCableDesign> findByName(String name) {
        return findByDomainAndName(getDomainName(), name);
    }  
}
