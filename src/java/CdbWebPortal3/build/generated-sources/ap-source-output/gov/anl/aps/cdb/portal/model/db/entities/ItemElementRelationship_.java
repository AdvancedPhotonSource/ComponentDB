package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationshipHistory;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.model.db.entities.ResourceType;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(ItemElementRelationship.class)
public class ItemElementRelationship_ { 

    public static volatile SingularAttribute<ItemElementRelationship, ItemElement> linkItemElement;
    public static volatile SingularAttribute<ItemElementRelationship, RelationshipType> relationshipType;
    public static volatile SingularAttribute<ItemElementRelationship, String> description;
    public static volatile SingularAttribute<ItemElementRelationship, String> label;
    public static volatile ListAttribute<ItemElementRelationship, ItemElementRelationshipHistory> itemElementRelationshipHistoryList;
    public static volatile SingularAttribute<ItemElementRelationship, ItemElement> firstItemElement;
    public static volatile SingularAttribute<ItemElementRelationship, ItemConnector> firstItemConnector;
    public static volatile SingularAttribute<ItemElementRelationship, String> relationshipDetails;
    public static volatile ListAttribute<ItemElementRelationship, PropertyValue> propertyValueList;
    public static volatile SingularAttribute<ItemElementRelationship, Integer> id;
    public static volatile SingularAttribute<ItemElementRelationship, ItemConnector> secondItemConnector;
    public static volatile SingularAttribute<ItemElementRelationship, ItemElement> secondItemElement;
    public static volatile SingularAttribute<ItemElementRelationship, ResourceType> resourceType;

}