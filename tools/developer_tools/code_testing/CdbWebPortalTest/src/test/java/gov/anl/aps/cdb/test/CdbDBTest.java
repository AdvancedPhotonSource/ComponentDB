/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.test;

import gov.anl.aps.cdb.portal.model.db.beans.*;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CdbDBTest {

    @Deployment
    public static Archive<?> createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackage(ItemFacade.class.getPackage())
                .addPackage(Item.class.getPackage())                
                .addAsResource("persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        return war;
    }

    @EJB
    AllowedPropertyValueFacade allowedPropertyValueFacade;

    @EJB
    AttachmentFacade attachmentFacade;

    @EJB
    ConnectorFacade connectorFacade;

    @EJB
    ConnectorTypeFacade connectorTypeFacade;

    @EJB
    DomainFacade domainFacade;

    @EJB
    EntityInfoFacade entityInfoFacade;

    @EJB
    EntityTypeFacade entityTypeFacade;
    
    @EJB
    ItemCategoryFacade itemCategoryFacade;
    
    @EJB
    ItemConnectorFacade itemConnectorFacade;
    
    @EJB
    ItemElementFacade itemElementFacade;
    
    @EJB
    ItemElementRelationshipFacade itemElementRelationshipFacade;
    
    @EJB
    ItemElementRelationshipHistoryFacade itemElementRelationshipHistoryFacade;
    
    @EJB
    ItemFacade itemFacade; 
    
    @EJB
    ItemProjectFacade itemProjectFacade;
    
    @EJB
    ItemResourceFacade itemResourceFacade; 
    
    @EJB
    ItemSourceFacade itemSourceFacade;
    
    @EJB
    ItemTypeFacade itemTypeFacade; 
    
    @EJB
    ListFacade listFacade;
    
    @EJB
    LogFacade logFacade;
    
    @EJB
    LogLevelFacade logLevelFacade;
    
    @EJB
    LogTopicFacade logTopicFacade;
    
    @EJB
    PropertyMetadataFacade propertyMetadataFacade; 
    
    @EJB
    PropertyTypeCategoryFacade propertyTypeCategoryFacade; 
    
    @EJB
    PropertyTypeFacade propertyTypeFacade;
    
    @EJB
    PropertyTypeHandlerFacade propertyTypeHandlerFacade; 
    
    @EJB
    PropertyValueFacade propertyValueFacade;
    
    @EJB
    PropertyValueHistoryFacade propertyValueHistoryFacade;
    
    @EJB
    RelationshipTypeFacade relationshipTypeFacade; 
    
    @EJB
    RelationshipTypeHandlerFacade relationshipTypeHandlerFacade;
    
    @EJB
    ResourceTypeCategoryFacade resourceTypeCategoryFacade;
    
    @EJB
    ResourceTypeFacade resourceTypeFacade; 
    
    @EJB
    RoleTypeFacade roleTypeFacade; 
    
    @EJB
    SettingTypeFacade settingTypeFacade; 
    
    @EJB
    SourceFacade sourceFacade;
    
    @EJB
    UserGroupFacade userGroupFacade; 
    
    @EJB
    UserGroupSettingFacade userGroupSettingFacade; 
    
    @EJB
    UserInfoFacade userInfoFacade;
    
    @EJB
    UserRoleFacade userRoleFacade;
    
    @EJB
    UserSettingFacade userSettingFacade; 
    
    @Test
    public void checkSomeEJBIsSet() {
        Assert.assertNotNull(allowedPropertyValueFacade);
    }

    public AllowedPropertyValueFacade getAllowedPropertyValueFacade() {
        return allowedPropertyValueFacade;
    }

    public AttachmentFacade getAttachmentFacade() {
        return attachmentFacade;
    }

    public ConnectorFacade getConnectorFacade() {
        return connectorFacade;
    }

    public ConnectorTypeFacade getConnectorTypeFacade() {
        return connectorTypeFacade;
    }

    public DomainFacade getDomainFacade() {
        return domainFacade;
    }

    public EntityInfoFacade getEntityInfoFacade() {
        return entityInfoFacade;
    }

    public EntityTypeFacade getEntityTypeFacade() {
        return entityTypeFacade;
    }

    public ItemCategoryFacade getItemCategoryFacade() {
        return itemCategoryFacade;
    }

    public ItemConnectorFacade getItemConnectorFacade() {
        return itemConnectorFacade;
    }

    public ItemElementFacade getItemElementFacade() {
        return itemElementFacade;
    }

    public ItemElementRelationshipFacade getItemElementRelationshipFacade() {
        return itemElementRelationshipFacade;
    }

    public ItemElementRelationshipHistoryFacade getItemElementRelationshipHistoryFacade() {
        return itemElementRelationshipHistoryFacade;
    }

    public ItemFacade getItemFacade() {
        return itemFacade;
    }

    public ItemProjectFacade getItemProjectFacade() {
        return itemProjectFacade;
    }

    public ItemResourceFacade getItemResourceFacade() {
        return itemResourceFacade;
    }

    public ItemSourceFacade getItemSourceFacade() {
        return itemSourceFacade;
    }

    public ItemTypeFacade getItemTypeFacade() {
        return itemTypeFacade;
    }

    public ListFacade getListFacade() {
        return listFacade;
    }

    public LogFacade getLogFacade() {
        return logFacade;
    }

    public LogLevelFacade getLogLevelFacade() {
        return logLevelFacade;
    }

    public LogTopicFacade getLogTopicFacade() {
        return logTopicFacade;
    }

    public PropertyMetadataFacade getPropertyMetadataFacade() {
        return propertyMetadataFacade;
    }

    public PropertyTypeCategoryFacade getPropertyTypeCategoryFacade() {
        return propertyTypeCategoryFacade;
    }

    public PropertyTypeFacade getPropertyTypeFacade() {
        return propertyTypeFacade;
    }

    public PropertyTypeHandlerFacade getPropertyTypeHandlerFacade() {
        return propertyTypeHandlerFacade;
    }

    public PropertyValueFacade getPropertyValueFacade() {
        return propertyValueFacade;
    }

    public PropertyValueHistoryFacade getPropertyValueHistoryFacade() {
        return propertyValueHistoryFacade;
    }

    public RelationshipTypeFacade getRelationshipTypeFacade() {
        return relationshipTypeFacade;
    }

    public RelationshipTypeHandlerFacade getRelationshipTypeHandlerFacade() {
        return relationshipTypeHandlerFacade;
    }

    public ResourceTypeCategoryFacade getResourceTypeCategoryFacade() {
        return resourceTypeCategoryFacade;
    }

    public ResourceTypeFacade getResourceTypeFacade() {
        return resourceTypeFacade;
    }

    public RoleTypeFacade getRoleTypeFacade() {
        return roleTypeFacade;
    }

    public SettingTypeFacade getSettingTypeFacade() {
        return settingTypeFacade;
    }

    public SourceFacade getSourceFacade() {
        return sourceFacade;
    }

    public UserGroupFacade getUserGroupFacade() {
        return userGroupFacade;
    }

    public UserGroupSettingFacade getUserGroupSettingFacade() {
        return userGroupSettingFacade;
    }

    public UserInfoFacade getUserInfoFacade() {
        return userInfoFacade;
    }

    public UserRoleFacade getUserRoleFacade() {
        return userRoleFacade;
    }

    public UserSettingFacade getUserSettingFacade() {
        return userSettingFacade;
    }
}
