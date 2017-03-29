/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.entities.ListTbl;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author djarosz
 */
public abstract class ItemFacadeBase<ItemDomainEntity extends Item> extends CdbEntityFacade<ItemDomainEntity> {

    @PersistenceContext(unitName = "CdbWebPortalPU")
    private EntityManager em;

    List<ItemDomainEntity> itemsToAdd;

    private final String QUERY_STRING_START = "SELECT i FROM Item i ";

    public ItemFacadeBase(Class<ItemDomainEntity> entityClass) {
        super(entityClass);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void create(ItemDomainEntity item) {
        itemsToAdd = new ArrayList<>();
        this.populateItemsToAdd(item);

        try {
            for (ItemDomainEntity newItem : itemsToAdd) {
                em.persist(newItem);
            }
        } catch (Exception ex) {
            em.clear();
            throw ex;
        }
    }

    @Override
    public ItemDomainEntity edit(ItemDomainEntity item) {
        itemsToAdd = new ArrayList<>();
        this.populateItemsToAdd(item);

        try {
            for (ItemDomainEntity newItem : itemsToAdd) {
                em.persist(newItem);
            }
        } catch (Exception ex) {
            em.clear();
            throw ex;
        }

        return super.edit(item);
    }

    private void populateItemsToAdd(ItemDomainEntity item) {
        if (item != null) {
            item.resetItemElementDisplayList();
            for (ItemElement itemElement : item.getItemElementDisplayList()) {
                ItemDomainEntity containedItem = (ItemDomainEntity) itemElement.getContainedItem();
                if (containedItem != null) {
                    Boolean nonExistantItem = true;
                    if (containedItem.getId() != null) {
                        ItemDomainEntity dbItem = find(containedItem.getId());
                        if (dbItem == null) {
                            // Not yet existend in DB. 
                            populateItemsToAdd(containedItem);
                        }
                    } else {
                        populateItemsToAdd(containedItem);
                    }
                }
            }
            if (item.getId() != null) {
                if (find(item.getId()) != null) {
                    // No need to add new item. 
                    return;
                }
            }
            itemsToAdd.add(item);
        }
    }

    public List<ItemDomainEntity> findByDomainAndEntityType(String domainName, String entityTypeName) {
        try {
            return (List<ItemDomainEntity>) em.createNamedQuery("Item.findByDomainNameAndEntityType")
                    .setParameter("domainName", domainName)
                    .setParameter("entityTypeName", entityTypeName)
                    .getResultList();
        } catch (NoResultException ex) {

        }
        return null;
    }

    public List<ItemDomainEntity> findByDomain(String domainName) {
        try {
            return (List<ItemDomainEntity>) em.createNamedQuery("Item.findByDomainName")
                    .setParameter("domainName", domainName)
                    .getResultList();
        } catch (NoResultException ex) {

        }
        return null;
    }
    
    public List<ItemDomainEntity> findByDomainAndProject(String domainName, String projectName) {
        try {
            return (List<ItemDomainEntity>) em.createNamedQuery("Item.findByDomainNameAndProject")
                    .setParameter("domainName", domainName)
                    .setParameter("projectName", projectName)
                    .getResultList();
        } catch (NoResultException ex) {

        }
        return null;
    }

    public List<ItemDomainEntity> findByDomainWithoutParents(String domainName) {
        try {
            return (List<ItemDomainEntity>) em.createNamedQuery("Item.findByDomainNameWithNoParents")
                    .setParameter("domainName", domainName)
                    .getResultList();
        } catch (NoResultException ex) {

        }
        return null;
    }

    public List<ItemDomainEntity> findByDomainOrderByQrId(String domainName) {
        try {
            return (List<ItemDomainEntity>) em.createNamedQuery("Item.findByDomainNameOrderByQrId")
                    .setParameter("domainName", domainName)
                    .getResultList();
        } catch (NoResultException ex) {

        }
        return null;
    }
    
    public List<ItemDomainEntity> findByDomainAndProjectOrderByQrId(String domainName, String projectName) {
        try {
            return (List<ItemDomainEntity>) em.createNamedQuery("Item.findByDomainNameAndProjectOrderByQrId")
                    .setParameter("domainName", domainName)
                    .setParameter("projectName", projectName)
                    .getResultList();
        } catch (NoResultException ex) {

        }
        return null;
    }

    public List<ItemDomainEntity> findByName(String name) {
        try {
            return (List<ItemDomainEntity>) em.createNamedQuery("Item.findByName")
                    .setParameter("name", name)
                    .getResultList();
        } catch (NoResultException ex) {

        }
        return null;
    }

    public ItemDomainEntity findByUniqueAttributes(Item derivedFromItem, Domain domain,
            String name, String itemIdentifier1, String itemIdentifier2) {
        String queryString = QUERY_STRING_START + "WHERE ";
        if (derivedFromItem == null) {
            queryString += "i.derivedFromItem is null ";
        } else {
            queryString += "i.derivedFromItem.id = " + derivedFromItem.getId() + " ";
        }

        queryString += " AND i.domain.id = " + domain.getId() + " ";

        if (name == null || name.isEmpty()) {
            queryString += "AND (i.name is null OR i.name = '') ";
        } else {
            queryString += "AND i.name = '" + name + "' ";
        }

        if (itemIdentifier1 == null || itemIdentifier1.isEmpty()) {
            queryString += "AND (i.itemIdentifier1 is null OR i.itemIdentifier1 = '')";
        } else {
            queryString += "AND i.itemIdentifier1 = '" + itemIdentifier1 + "' ";
        }

        if (itemIdentifier2 == null || itemIdentifier2.isEmpty()) {
            queryString += "AND (i.itemIdentifier2 is null OR i.itemIdentifier2 = '') ";
        } else {
            queryString += "AND i.itemIdentifier2 = '" + itemIdentifier2 + "' ";
        }

        try {
            return (ItemDomainEntity) em.createQuery(queryString).getSingleResult();
        } catch (NoResultException ex) {
        }

        return null;
    }

    public List<ItemDomainEntity> findByFilterViewCategoryTypeAttributes(ItemProject itemProject,
            List<ItemCategory> itemCategoryList, ItemType itemType, String itemDomainName) {
        return findByFilterViewAttributes(itemProject, itemCategoryList, itemType, itemDomainName, null, null);
    }

    public List<ItemDomainEntity> findByFilterViewOwnerAttributes(ItemProject itemProject,
            List<UserGroup> ownerUserGroupList, UserInfo ownerUserName, String itemDomainName) {
        return findByFilterViewAttributes(itemProject, null, null, itemDomainName, ownerUserGroupList, ownerUserName);
    }
    
    public List<ItemDomainEntity> findByFilterViewItemProjectAttributes(ItemProject itemProject, String itemDomainName) {
        return findByFilterViewAttributes(itemProject, null, null, itemDomainName, null, null);
    }

    private List<ItemDomainEntity> findByFilterViewAttributes(ItemProject itemProject,
            List<ItemCategory> itemCategoryList,
            ItemType itemType,
            String itemDomainName,
            List<UserGroup> ownerUserGroupList,
            UserInfo ownerUserName) {
        String queryString = QUERY_STRING_START;

        List<String> queryParameters = new ArrayList<>();

        if (itemDomainName != null) {
            queryParameters.add("i.domain.name = '" + itemDomainName + "'");
        }

        if (itemProject != null) {
            queryString += "JOIN i.itemProjectList ipl ";
            queryParameters.add("ipl.id = " + itemProject.getId());
        }

        if (itemCategoryList != null) {
            if (!itemCategoryList.isEmpty()) {
                queryString += "JOIN i.itemCategoryList icl ";
                String queryParameter = "(";
                for (ItemCategory itemCategory : itemCategoryList) {
                    if (itemCategoryList.indexOf(itemCategory) != 0) {
                        queryParameter += " OR ";
                    }
                    queryParameter += "icl.id = " + itemCategory.getId();
                }
                queryParameter += ")";
                queryParameters.add(queryParameter);
            }
        }

        if (itemType != null) {
            queryString += " JOIN i.itemTypeList itl ";
            queryParameters.add("itl.id = " + itemType.getId());
        }

        if (ownerUserGroupList != null || ownerUserName != null) {
            queryString += " JOIN i.fullItemElementList fiel ";
            queryParameters.add("fiel.name is NULL and fiel.derivedFromItemElement is null");
            if (ownerUserGroupList != null && !ownerUserGroupList.isEmpty()) {
                String queryParameter = "(";
                for (UserGroup userGroup : ownerUserGroupList) {
                    if (ownerUserGroupList.indexOf(userGroup) != 0) {
                        queryParameter += " OR ";
                    }
                    queryParameter += "fiel.entityInfo.ownerUserGroup.name = '" + userGroup.getName() + "'";
                }
                queryParameter += ")";
                queryParameters.add(queryParameter);
            }

            if (ownerUserName != null) {
                String queryParameter = "fiel.entityInfo.ownerUser.username = '" + ownerUserName.getUsername() + "'";
                queryParameters.add(queryParameter);
            }
        }

        if (!queryParameters.isEmpty()) {

            queryString += "WHERE ";

            for (String queryParameter : queryParameters) {
                if (queryParameters.indexOf(queryParameter) == 0) {
                    queryString += queryParameter + " ";
                } else {
                    queryString += "AND " + queryParameter + " ";
                }
            }

            queryString += "ORDER BY i.name ASC";

            return (List<ItemDomainEntity>) em.createQuery(queryString).getResultList();
        }

        return null;
    }

    public List<ItemDomainEntity> findByDerivedFromItemId(int derivedFromItemId) {
        try {
            return (List<ItemDomainEntity>) em.createNamedQuery("Item.findByDerivedFromItemId")
                    .setParameter("id", derivedFromItemId)
                    .getResultList();
        } catch (NoResultException ex) {

        }
        return null;
    }

    public List<ItemDomainEntity> findByDomainAndEntityTypeOrderByQrId(String domainName, String entityTypeName) {
        try {
            return (List<ItemDomainEntity>) em.createNamedQuery("Item.findByDomainNameOderByQrId")
                    .setParameter("domainName", domainName)
                    .setParameter("entityTypeName", entityTypeName)
                    .getResultList();
        } catch (NoResultException ex) {

        }
        return null;
    }

    public List<ItemDomainEntity> findByDomainAndDerivedEntityTypeOrderByQrId(String domainName, String entityTypeName) {
        try {
            return (List<ItemDomainEntity>) em.createNamedQuery("Item.findByDomainAndDerivedEntityTypeOrderByQrId")
                    .setParameter("domainName", domainName)
                    .setParameter("entityTypeName", entityTypeName)
                    .getResultList();
        } catch (NoResultException ex) {

        }
        return null;
    }

    public List<ItemDomainEntity> findAllWithName() {
        return (List<ItemDomainEntity>) em.createNamedQuery("Item.findAllWithName").getResultList();
    }   

    public ItemDomainEntity findByQrId(Integer qrId) {
        try {
            return (ItemDomainEntity) em.createNamedQuery("Item.findByQrId")
                    .setParameter("qrId", qrId)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public ItemDomainEntity findById(Integer id) {
        try {
            return (ItemDomainEntity) em.createNamedQuery("Item.findById")
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public List<ItemDomainEntity> getItemListWithPropertyType(String domainName, Integer propertyTypeId) {
        try {
            return (List<ItemDomainEntity>) em.createNamedQuery("Item.findItemsWithPropertyType")
                    .setParameter("domainName", domainName)
                    .setParameter("propertyTypeId", propertyTypeId)
                    .getResultList();
        } catch (NoResultException ex) {
        }
        return null;
    }
    
    public List<ItemDomainEntity> getItemsWithPropertyTypeAndProject(String domainName, Integer propertyTypeId, String projectName) {
        try {
            return (List<ItemDomainEntity>) em.createNamedQuery("Item.findItemsWithPropertyTypeAndProject")
                    .setParameter("domainName", domainName)
                    .setParameter("propertyTypeId", propertyTypeId)
                    .setParameter("projectName", projectName)
                    .getResultList();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public List<ItemDomainEntity> getItemListOwnedByUser(String domainName, UserInfo ownerUserInfo) {
        try {
            return (List<ItemDomainEntity>) em.createNamedQuery("Item.findItemsOwnedByUserId")
                    .setParameter("domainName", domainName)
                    .setParameter("ownerUserId", ownerUserInfo.getId())
                    .getResultList();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public List<ItemDomainEntity> getItemListOwnedByUserGroup(String domainName, UserGroup ownerUserGroup) {
        try {
            return (List<ItemDomainEntity>) em.createNamedQuery("Item.findItemsOwnedByUserGroupId")
                    .setParameter("domainName", domainName)
                    .setParameter("ownerUserGroupId", ownerUserGroup.getId())
                    .getResultList();
        } catch (NoResultException ex) {
        }
        return null;
    }

    public List<ItemDomainEntity> getItemListContainedInList(String domainName, ListTbl list) {
        if (list != null) {
            try {
                return (List<ItemDomainEntity>) em.createNamedQuery("Item.findItemsInList")
                        .setParameter("domainName", domainName)
                        .setParameter("list", list)
                        .getResultList();
            } catch (NoResultException ex) {
            }
        }
        return null;
    }

    public List<ItemDomainEntity> getItemListContainedInListOrOwnedByUser(String domainName, ListTbl list, UserInfo ownerUserInfo) {
        if (list != null) {
            try {
                return (List<ItemDomainEntity>) em.createNamedQuery("Item.findItemsOwnedByUserIdOrInList")
                        .setParameter("domainName", domainName)
                        .setParameter("ownerUserId", ownerUserInfo.getId())
                        .setParameter("list", list)
                        .getResultList();
            } catch (NoResultException ex) {
            }
        }
        return null;
    }

    public List<ItemDomainEntity> getItemListContainedInListOrOwnedByGroup(String domainName, ListTbl list, UserGroup ownerUserGroup) {
        if (list != null) {
            try {
                Query namedQuery = em.createNamedQuery("Item.findItemsOwnedByUserGroupIdOrInList")
                        .setParameter("domainName", domainName)
                        .setParameter("ownerUserGroupId", ownerUserGroup.getId())
                        .setParameter("list", list);

                return (List<ItemDomainEntity>) namedQuery.getResultList();
            } catch (NoResultException ex) {
            }
        }
        return null;
    }

}
