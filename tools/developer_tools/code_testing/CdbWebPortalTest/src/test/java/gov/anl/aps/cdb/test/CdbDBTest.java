/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.test;

import gov.anl.aps.cdb.portal.model.db.beans.*;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CdbDBTest {
    
    private final String DBUNIT_RESTORE_FILENAME = "src/test/resources/dbUnitDBRestore.xml";
    
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
    
    @PersistenceContext
    EntityManager em;
    
    @Inject
    UserTransaction utx;

    @Deployment
    public static Archive<?> createDeployment() {
        
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackage(ItemFacade.class.getPackage())
                .addClass(SessionUtility.class)
                .addPackage(Item.class.getPackage())                
                .addAsResource("cdb.portal.properties", "cdb.portal.properties") 
                .addAsResource("resources.properties", "resources.properties") 
                .addAsResource("persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");        
        return war;
    }
    
    @Before
    public void prepareDBTest() throws Exception {
        populateCleanDatabase(); 
    }
    
    private void populateCleanDatabase() throws Exception {
        utx.begin();
        em.joinTransaction();
        // Get sql connection object from the entity manager 
        Connection connection = em.unwrap(Connection.class);
        //Read in the restore of the db. 
        InputStream inputStream = new FileInputStream(DBUNIT_RESTORE_FILENAME);
        IDatabaseConnection dbunitConnection = new DatabaseConnection(connection); 
        FlatXmlDataSetBuilder flatXmlDataSetBuilder = new FlatXmlDataSetBuilder();
        flatXmlDataSetBuilder.setColumnSensing(true);
        IDataSet dataset = flatXmlDataSetBuilder.build(inputStream);
        
        // Allow empty fields during the import of database data. 
        DatabaseConfig config = dbunitConnection.getConfig(); 
        config.setProperty("http://www.dbunit.org/features/allowEmptyFields", true);
        // Perform a clean insert of the dataset. 
        DatabaseOperation.CLEAN_INSERT.execute(dbunitConnection, dataset);
        
        //Commit the changes made in preperation for next test. 
        utx.commit();        
    }
    
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
