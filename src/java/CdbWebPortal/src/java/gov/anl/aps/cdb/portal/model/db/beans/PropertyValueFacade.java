/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

/**
 *
 * @author djarosz
 */
@Stateless
public class PropertyValueFacade extends CdbEntityFacade<PropertyValue> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    private static final Integer SEARCH_RESULT_LIMIT = 1000;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PropertyValueFacade() {
        super(PropertyValue.class);
    }
    
    public static PropertyValueFacade getInstance() {
        return (PropertyValueFacade) SessionUtility.findFacade(PropertyValueFacade.class.getSimpleName()); 
    }
    
    public List<PropertyValue> getPropertyValueListByTypeIdAndValue(String propertyValue, Integer propertyTypeId) {
        try {
            
            return (List<PropertyValue> ) em.createNamedQuery("PropertyValue.findByValueAndTypeId")
                    .setParameter("value", propertyValue)
                    .setParameter("propertyTypeId", propertyTypeId)
                    .getResultList();                        
        } catch (NoResultException ex) {
        }
        return null;
    }
    
    public List<PropertyValue> fetchRelationshipParentPropertyValues(Integer itemId, Integer parentItemId, Integer relationshipTypeId) {        
        try {
            StoredProcedureQuery query = em.createNamedStoredProcedureQuery("propertyValue.fetchRelationshipParentPropertyValues");
            query.setParameter("item_id", itemId);
            query.setParameter("parent_item_id", parentItemId);
            query.setParameter("relationship_type_id", relationshipTypeId);            

            List<PropertyValue> resultList = query.getResultList();

            return resultList;
        } catch (NoResultException ex) {
        }
        return null;
    }

    /**
     * Searches item-attached property values, matching each whitespace-delimited
     * word of the search string independently (word-order independent). A property
     * value matches when every word appears in at least one of its value, tag, or
     * text fields.
     *
     * The owning item is fetched in the same query (the join to
     * itemElementList is already present) and attached to each property value via
     * its transient searchResultParentItem field, so search results can show the
     * owning item without a per-row lookup.
     *
     * @param searchString whitespace-delimited search words
     * @return matching property values, limited to SEARCH_RESULT_LIMIT
     */
    public List<PropertyValue> searchPropertyValues(String searchString) {
        String[] tokens = searchString.trim().split("\\s+");

        StringBuilder jpql = new StringBuilder(
                "SELECT DISTINCT pv, ie.parentItem FROM PropertyValue pv JOIN pv.itemElementList ie WHERE ");
        for (int i = 0; i < tokens.length; i++) {
            if (i > 0) {
                jpql.append(" AND ");
            }
            jpql.append("(pv.value LIKE :t").append(i)
                    .append(" OR pv.tag LIKE :t").append(i)
                    .append(" OR pv.text LIKE :t").append(i).append(")");
        }

        Query query = em.createQuery(jpql.toString());
        for (int i = 0; i < tokens.length; i++) {
            query.setParameter("t" + i, "%" + convertWildcards(tokens[i]) + "%");
        }
        query.setMaxResults(SEARCH_RESULT_LIMIT);

        // A property value attached to multiple item elements yields multiple rows;
        // dedup by property value id (first owning item wins) to match the prior
        // DISTINCT pv behavior.
        List<Object[]> rows = (List<Object[]>) query.getResultList();
        List<PropertyValue> resultList = new ArrayList<>();
        Set<Integer> seenIds = new HashSet<>();
        for (Object[] row : rows) {
            PropertyValue propertyValue = (PropertyValue) row[0];
            if (seenIds.add(propertyValue.getId())) {
                propertyValue.setSearchResultParentItem((Item) row[1]);
                resultList.add(propertyValue);
            }
        }
        return resultList;
    }

    /**
     * Resolves the owning item of a property value by walking
     * property value -> item element -> parent item. Performed at redirect time
     * (rather than per search result row) to keep the search results page light.
     *
     * @param propertyValueId id of the property value
     * @return the first parent item found, or null if none
     */
    public Item getParentItemForPropertyValue(Integer propertyValueId) {
        try {
            List<Item> resultList = (List<Item>) em.createQuery(
                    "SELECT ie.parentItem FROM PropertyValue pv JOIN pv.itemElementList ie WHERE pv.id = :id")
                    .setParameter("id", propertyValueId)
                    .setMaxResults(1)
                    .getResultList();
            if (!resultList.isEmpty()) {
                return resultList.get(0);
            }
        } catch (NoResultException ex) {
        }
        return null;
    }

}
