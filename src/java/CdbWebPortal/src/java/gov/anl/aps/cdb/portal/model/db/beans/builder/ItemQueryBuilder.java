/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans.builder;

import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Dariusz
 */
public abstract class ItemQueryBuilder {
    
    private static final Logger logger = LogManager.getLogger(ItemQueryBuilder.class.getName());

    private static final String QUERY_STRING_START = "SELECT DISTINCT(i) FROM Item i ";
    private static final CharSequence[] ESCAPE_QUERY_CHARACTERS = {"'"};
    private static final String ITEM_ELEMENTS_LIST_JOIN_NAME = "fiel";
    private static final String ITEM_PROJECT_LIST_JOIN_NAME = "ipl";
    private static final String ENTITY_TYPE_LIST_JOIN_NAME = "etl";
    private static final String ITEM_CATEGORY_LIST_JOIN_NAME = "icl"; 
    private static final String ITEM_TYPE_LIST_JOIN_NAME = "itl"; 
    private static final String CORE_METADATA_PROPERTY_JOIN_NAME = "cmp";

    private static final String QUERY_LIKE = "LIKE";
    
    private final String METADATA_FIELD_START = "Metadata-"; 
    private final String PROPERTY_FIELD_START = "propertyColumn"; 

    boolean include_fiel = false;
    boolean include_ipl = false;
    boolean include_etl = false;
    boolean include_icl = false;
    boolean include_itl = false;     
    private Set<String> pvlNames = null;
    private Set<String> coreMetadataNames = null; 

    private Set<String> firstIERNames = null;
    private Set<String> secondIERNames = null; 

    private boolean fiel_included_in_where;

    protected Domain domain;
    protected Map filterMap;
    protected String sortField;
    protected SortOrder sortOrder;

    private String wherePart;
    private String sortPart;
    private String joinPart;
    
    public ItemQueryBuilder(Domain domain, Map filterMap, String sortField, SortOrder sortOrder) {
        this.domain = domain;
        this.filterMap = filterMap;
        this.sortField = sortField;
        this.sortOrder = sortOrder;

        this.pvlNames = new HashSet<>();
        this.coreMetadataNames = new HashSet<>(); 
        this.firstIERNames = new HashSet<>(); 
        this.secondIERNames = new HashSet<>(); 
        
        this.fiel_included_in_where = false;
    }

    public String getQueryForItems() {
        generateWhereString();
        generateSortString();

        generateJoinString();

        String fullQuery = QUERY_STRING_START + joinPart + wherePart + sortPart;

        return fullQuery;
    }

    /**
     * Adds the joins but run generate where and sort first to gather info on
     * which joins are relevant.
     *
     * @return
     */
    private String generateJoinString() {
        joinPart = "";

        if (include_etl) {
            joinPart += " JOIN i.entityTypeList " + ENTITY_TYPE_LIST_JOIN_NAME;
        }
        if (include_fiel) {
            joinPart += " JOIN i.fullItemElementList " + ITEM_ELEMENTS_LIST_JOIN_NAME;
        }
        if (include_ipl) {
            joinPart += " JOIN i.itemProjectList " + ITEM_PROJECT_LIST_JOIN_NAME;
        }
        
        if (include_icl) {
            joinPart += " JOIN i.itemCategoryList " + ITEM_CATEGORY_LIST_JOIN_NAME; 
        }
        
        if (include_itl) {
            joinPart += " JOIN i.itemTypeList " + ITEM_TYPE_LIST_JOIN_NAME; 
        }

        for (String pvlName : pvlNames) {
            joinPart += " JOIN fiel.propertyValueList " + pvlName;
        }

        for (String ierName : firstIERNames) {
            joinPart += " JOIN fiel.itemElementRelationshipList " + ierName;
        }
        
        for (String ierName : secondIERNames) {
            joinPart += " JOIN fiel.itemElementRelationshipList1 " + ierName;
        }
        
        for (String cmName : coreMetadataNames) {
            joinPart += " JOIN " + CORE_METADATA_PROPERTY_JOIN_NAME + ".propertyMetadataList " + cmName;
        }

        return joinPart;

    }

    private void generateWhereString() {
        wherePart = "";
        appendWhere("=", "i.domain.id", domain.getId());

        for (Object key : filterMap.keySet()) {
            Object filterValue = filterMap.get(key);
            if (filterValue instanceof FilterMeta) {                
                filterValue = ((FilterMeta)filterValue).getFilterValue();
            }

            if (filterValue != null) {
                String keyString = key.toString();

                String valueString = filterValue.toString();

                if (keyString.startsWith(PROPERTY_FIELD_START)) {
                    addPropertyWhereByTypeId(keyString, valueString);
                    return;
                } else if (keyString.startsWith(METADATA_FIELD_START)) {
                    addCoreMetadataWhere(keyString, valueString);
                    return;
                }

                QueryTranslator qt = QueryTranslator.getQueryTranslatorByValue(keyString);

                if (qt != null) {
                    appendWhere(QUERY_LIKE, qt.getQueryNameField(), valueString);
                    String parent = qt.getParent();
                    updateIncludes(parent);
                } else {
                    handleUnandeledFieldFilter(keyString, valueString);
                }
            }
        }
    }
    
    private String prepareCoreMetadataQuery(String key) {
        String coreMetadataPropertyName = getCoreMetadataPropertyName();
        if (coreMetadataPropertyName == null) {
            logger.warn("Cannot filter core property not specified in controller");
            return null; 
        }    
        
        preparePropertyQuery(CORE_METADATA_PROPERTY_JOIN_NAME, "name", coreMetadataPropertyName);
                
        String metadataKey = key.split("-")[1];
        
        String filter_key = "cmp"+metadataKey; 
        coreMetadataNames.add(filter_key);
        String keyMatchQuery = filter_key + ".metadataKey"; 
        appendWhere("=", keyMatchQuery, metadataKey);

        return filter_key; 
    }
    
    private void addCoreMetadataWhere(String key, String value) {
        String filter_key = prepareCoreMetadataQuery(key);                
        
        String filterMatchQuery = filter_key + ".metadataValue";
        appendWhere(QUERY_LIKE, filterMatchQuery, value);
    }
    
    /**
     * Override this method in sub-controller to allow filtering of core property. 
     * 
     * @return 
     */
    protected String getCoreMetadataPropertyName() {
        return null; 
    }

    /**
     * Override this method in sub-controller to filter additional attributes.
     *
     * @param key
     * @param value
     */
    protected void handleUnandeledFieldFilter(String key, String value) {

    }

    private void includeFiel() {
        if (fiel_included_in_where == false) {
            include_fiel = true;

            appendWhere("IS", ITEM_ELEMENTS_LIST_JOIN_NAME + ".derivedFromItemElement", null);
            appendWhere("IS", ITEM_ELEMENTS_LIST_JOIN_NAME + ".name", null);

            fiel_included_in_where = true;
        }
    }
    
    protected void addSelfElementWhereByAttribute(String attribute, String value) {        
        String nameField = ITEM_ELEMENTS_LIST_JOIN_NAME + "." + attribute; 
        
        appendWhere(QUERY_LIKE, nameField, value);
        
        includeFiel();        
    }

    protected void addPropertyWhereByTypeId(String fullFieldName, String value) {
        String key = preparePropertyQuery(fullFieldName, "id");

        addPropertyWhere(key, value);
    }

    protected void addPropertyWhereByTypeName(String propertyTypeName, String key, String value) {
        preparePropertyQueryByPropertyTypeName(key, propertyTypeName);

        addPropertyWhere(key, value);
    }

    private void addPropertyWhere(String key, String value) {
        String queryName = key + ".value";
        appendWhere(QUERY_LIKE, queryName, value);
    }

    protected String preparePropertyQueryByPropertyTypeName(String fieldName, String propertyTypeName) {
        String fullFieldName = fieldName + "-" + propertyTypeName;
        return preparePropertyQuery(fullFieldName, "name");
    }

    /**
     * Prepare a property query before a where or sort is performed
     *
     * @param fullFieldName
     * @param byAttribute
     * @return key of the property
     */
    private String preparePropertyQuery(String fullFieldName, String byAttribute) {
        String[] split = fullFieldName.split("-");
        String key = split[0]; 
        String propertyTypeByValue = split[1];
        
        preparePropertyQuery(key, byAttribute, propertyTypeByValue);

        return key;
    }
    
    private void preparePropertyQuery(String key, String byAttribute, String propertyTypeByValue) {
        if (pvlNames.contains(key) == false) {
            String queryName = key + ".propertyType." + byAttribute;
            appendWhere("=", queryName, propertyTypeByValue);

            pvlNames.add(key);
            includeFiel();
        }
    }
    
    protected void addFirstRelationshipDetailsWhere(String key, String relationshipTypeName, String relationshipDetails) {
        addFirstRelationshipWhere(key, relationshipTypeName, "relationshipDetails", relationshipDetails);
    }
    
    protected void addFirstRelationshipParentItemWhere(String key, String relationshipTypeName, String relationshipItemName) {
        addFirstRelationshipWhere(key, relationshipTypeName, "secondItemElement.parentItem.name", relationshipItemName);
    }
    
    private void addFirstRelationshipWhere(String key, String relationshipTypeName, String dbFieldName, String value) {
        prepareFirstRelationshipQueryByRelationshipName(key, relationshipTypeName);
        
        String queryName = key + "." + dbFieldName; 
        appendWhere(QUERY_LIKE, queryName, value);
    }
    
    /**
     * Prepares a relationship entity for filter or sort 
     * 
     * @param key entity reference in query 
     * @param relationshipTypeName name of the relationship type
     */
    protected void prepareFirstRelationshipQueryByRelationshipName(String key, String relationshipTypeName) {
        prepareRelationshipQuery(key, "name", relationshipTypeName);
        firstIERNames.add(key);
    }
    
    protected void addSecondRelationshipDetailsWhere(String key, String relationshipTypeName, String relationshipDetails) {
        addSecondRelationshipWhere(key, relationshipTypeName, "relationshipDetails", relationshipDetails);
    }
    
    protected void addSecondRelationshipParentItemWhere(String key, String relationshipTypeName, String relationshipItemName) {
        addSecondRelationshipWhere(key, relationshipTypeName, "firstItemElement.parentItem.name", relationshipItemName);
    }
    
    private void addSecondRelationshipWhere(String key, String relationshipTypeName, String dbFieldName, String value) {
        prepareSecondRelationshipQueryByRelationshipName(key, relationshipTypeName);
        
        String queryName = key + "." + dbFieldName; 
        appendWhere(QUERY_LIKE, queryName, value);
    }
    
    /**
     * Prepares a relationship entity for filter or sort 
     * 
     * @param key entity reference in query 
     * @param relationshipTypeName name of the relationship type
     */
    protected void prepareSecondRelationshipQueryByRelationshipName(String key, String relationshipTypeName) {
        prepareRelationshipQuery(key, "name", relationshipTypeName);
        secondIERNames.add(key);
    }
    
    /**
     * prepares a relationship entity for filter or sort 
     * 
     * @param key entity reference in query
     * @param byAttribute id/name of attribute 
     * @param relationshipTypeAttribute 
     */
    private void prepareRelationshipQuery(String key, String byAttribute, String relationshipTypeAttribute) {
        String queryName = key + ".relationshipType." + byAttribute;
        appendWhere("=", queryName, relationshipTypeAttribute);
        
        includeFiel();
    }

    private void updateIncludes(String parent) {
        switch (parent) {
            case ITEM_ELEMENTS_LIST_JOIN_NAME:
                includeFiel();
                break;
            case ENTITY_TYPE_LIST_JOIN_NAME:
                include_etl = true;
                break;
            case ITEM_PROJECT_LIST_JOIN_NAME:
                include_ipl = true;
                break;
            case ITEM_CATEGORY_LIST_JOIN_NAME: 
                include_icl = true;
                break;
            case ITEM_TYPE_LIST_JOIN_NAME: 
                include_itl = true; 
                break; 
        }
    }

    private void generateSortString() {
        sortPart = "";
        if (sortOrder != null && sortField != null) {
            String fullSortField = null;

            if (sortField.startsWith(PROPERTY_FIELD_START)) {
                sortField = preparePropertyQuery(sortField, "id");
                fullSortField = sortField + ".value";
            } else if (sortField.startsWith(METADATA_FIELD_START)) {
                sortField = prepareCoreMetadataQuery(sortField); 
                fullSortField = sortField + ".metadataValue"; 
            }else {
                QueryTranslator qt = QueryTranslator.getQueryTranslatorByValue(sortField);
                if (qt != null) {
                    fullSortField = qt.getQueryNameField();
                    String parent = qt.getParent();
                    updateIncludes(parent);
                }
            }

            if (fullSortField == null) {
                fullSortField = handleUnhandeledSortField();
            }

            if (fullSortField != null) {
                sortPart = " ORDER BY " + fullSortField + " ";
                if (sortOrder == SortOrder.ASCENDING) {
                    sortPart += "ASC";
                } else {
                    sortPart += "DESC";
                }
            }
        }
    }

    /**
     * Override to handle a sort field that is specific to domain.
     *
     * @return
     */
    protected String handleUnhandeledSortField() {
        return null;
    }

    public static String findByFilterViewAttributesQuery(ItemProject itemProject,
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
            queryString += " JOIN i.fullItemElementList " + ITEM_ELEMENTS_LIST_JOIN_NAME + " ";
            queryParameters.add(ITEM_ELEMENTS_LIST_JOIN_NAME + ".name is NULL and " + ITEM_ELEMENTS_LIST_JOIN_NAME + ".derivedFromItemElement is null");
            if (ownerUserGroupList != null && !ownerUserGroupList.isEmpty()) {
                String queryParameter = "(";
                for (UserGroup userGroup : ownerUserGroupList) {
                    if (ownerUserGroupList.indexOf(userGroup) != 0) {
                        queryParameter += " OR ";
                    }
                    queryParameter += ITEM_ELEMENTS_LIST_JOIN_NAME + ".entityInfo.ownerUserGroup.name = '" + userGroup.getName() + "'";
                }
                queryParameter += ")";
                queryParameters.add(queryParameter);
            }

            if (ownerUserName != null) {
                String queryParameter = ITEM_ELEMENTS_LIST_JOIN_NAME + ".entityInfo.ownerUser.username = '" + ownerUserName.getUsername() + "'";
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

            return queryString;
        }

        return null;
    }

    public static String findByUniqueAttributesQuery(Item derivedFromItem, Domain domain,
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
            queryString += "AND i.name = '" + escapeCharacters(name) + "' ";
        }

        if (itemIdentifier1 == null || itemIdentifier1.isEmpty()) {
            queryString += "AND (i.itemIdentifier1 is null OR i.itemIdentifier1 = '') ";
        } else {
            queryString += "AND i.itemIdentifier1 = '" + escapeCharacters(itemIdentifier1) + "' ";
        }

        if (itemIdentifier2 == null || itemIdentifier2.isEmpty()) {
            queryString += "AND (i.itemIdentifier2 is null OR i.itemIdentifier2 = '') ";
        } else {
            queryString += "AND i.itemIdentifier2 = '" + escapeCharacters(itemIdentifier2) + "' ";
        }

        return queryString;
    }

    private void appendWhere(String comparator, String key, Object object) {
        if (wherePart.isEmpty()) {
            wherePart += " WHERE ";
        } else {
            wherePart += "AND ";
        }

        String value = "NULL";
        if (object != null) {
            value = object.toString();
        }

        if (object != null && object instanceof String) {
            if (comparator.equalsIgnoreCase(QUERY_LIKE)) {
                value = "%" + value + "%";
            }

            value = "'" + escapeCharacters(value) + "'";
        }

        wherePart += key + " " + comparator + " " + value + " ";
    }

    private static String escapeCharacters(String queryParameter) {
        for (CharSequence cs : ESCAPE_QUERY_CHARACTERS) {
            if (queryParameter.contains(cs)) {
                queryParameter = queryParameter.replace(cs, "'" + cs);
            }
        }
        return queryParameter;
    }

    public enum QueryTranslator {
        item_identifier1("itemIdentifier1", "i"),
        item_identifier2("itemIdentifier2", "i"),
        qrid("qrId", "i"),
        qrIdFilter("qrIdFilter", "i", "qrId"),
        name("name", "i"),
        id("id", "i"),
        derivedFromItemName("derivedFromItem.name", "i"),
        itemProject("itemProjectString", ITEM_PROJECT_LIST_JOIN_NAME, "name"),
        description("description", ITEM_ELEMENTS_LIST_JOIN_NAME),
        ownerUser("ownerUser.username", ITEM_ELEMENTS_LIST_JOIN_NAME, "entityInfo.ownerUser.username"),
        ownerGroup("ownerUserGroup.name", ITEM_ELEMENTS_LIST_JOIN_NAME, "entityInfo.ownerUserGroup.name"),
        createdUser("createdByUser.username", ITEM_ELEMENTS_LIST_JOIN_NAME, "entityInfo.createdByUser.username"),
        createdDate("createdOnDateTime", ITEM_ELEMENTS_LIST_JOIN_NAME, "entityInfo.createdOnDateTime"),
        modifiedUser("lastModifiedByUser.username", ITEM_ELEMENTS_LIST_JOIN_NAME, "entityInfo.lastModifiedByUser.username"),
        modifiedDate("lastModifiedOnDateTime", ITEM_ELEMENTS_LIST_JOIN_NAME, "entityInfo.lastModifiedOnDateTime"),
        entityTypeName("entityTypeString", ENTITY_TYPE_LIST_JOIN_NAME, "name"),
        itemCategoryName("itemCategoryString", ITEM_CATEGORY_LIST_JOIN_NAME, "name"),
        itemTypeName("itemTypeString", ITEM_TYPE_LIST_JOIN_NAME, "name");

        private String value;
        private String customDesignation;
        private String parent;

        private QueryTranslator(String value, String parent) {
            this.value = value;
            this.parent = parent;
        }

        private QueryTranslator(String value, String parent, String customDesignation) {
            this.value = value;
            this.parent = parent;
            this.customDesignation = customDesignation;
        }

        public String getValue() {
            return value;
        }

        public String getParent() {
            return parent;
        }

        public String getQueryNameField() {
            String result = getParent() + ".";
            if (customDesignation != null) {
                result += customDesignation;
            } else {
                result += getValue();
            }
            return result;
        }

        public static QueryTranslator getQueryTranslatorByValue(String value) {
            if (value != null) {
                for (QueryTranslator qt : QueryTranslator.values()) {
                    if (qt.getValue().equals(value)) {
                        return qt;
                    }
                }
            }
            return null;
        }
    };

}
