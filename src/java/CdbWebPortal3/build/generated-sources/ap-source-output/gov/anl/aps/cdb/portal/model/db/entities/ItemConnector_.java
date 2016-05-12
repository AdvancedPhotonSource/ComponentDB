package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationshipHistory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemResource;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(ItemConnector.class)
public class ItemConnector_ { 

    public static volatile SingularAttribute<ItemConnector, Item> item;
    public static volatile ListAttribute<ItemConnector, ItemElementRelationshipHistory> itemElementRelationshipHistoryList1;
    public static volatile SingularAttribute<ItemConnector, Integer> quantity;
    public static volatile SingularAttribute<ItemConnector, Connector> connector;
    public static volatile ListAttribute<ItemConnector, ItemElementRelationship> itemElementRelationshipList1;
    public static volatile ListAttribute<ItemConnector, ItemResource> itemResourceList;
    public static volatile ListAttribute<ItemConnector, PropertyValue> propertyValueList;
    public static volatile SingularAttribute<ItemConnector, Integer> id;
    public static volatile SingularAttribute<ItemConnector, String> label;
    public static volatile ListAttribute<ItemConnector, ItemElementRelationshipHistory> itemElementRelationshipHistoryList;
    public static volatile ListAttribute<ItemConnector, ItemElementRelationship> itemElementRelationshipList;

}