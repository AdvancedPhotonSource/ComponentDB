package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ResourceType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(ItemElementRelationshipHistory.class)
public class ItemElementRelationshipHistory_ { 

    public static volatile SingularAttribute<ItemElementRelationshipHistory, ItemElement> linkItemElement;
    public static volatile SingularAttribute<ItemElementRelationshipHistory, UserInfo> enteredByUser;
    public static volatile SingularAttribute<ItemElementRelationshipHistory, String> description;
    public static volatile SingularAttribute<ItemElementRelationshipHistory, String> label;
    public static volatile SingularAttribute<ItemElementRelationshipHistory, ItemElement> firstItemElement;
    public static volatile SingularAttribute<ItemElementRelationshipHistory, ItemConnector> firstItemConnector;
    public static volatile SingularAttribute<ItemElementRelationshipHistory, String> relationshipDetails;
    public static volatile SingularAttribute<ItemElementRelationshipHistory, ItemElementRelationship> itemElementRelationship;
    public static volatile SingularAttribute<ItemElementRelationshipHistory, Integer> id;
    public static volatile SingularAttribute<ItemElementRelationshipHistory, Date> enteredOnDateTime;
    public static volatile SingularAttribute<ItemElementRelationshipHistory, ItemConnector> secondItemConnector;
    public static volatile SingularAttribute<ItemElementRelationshipHistory, ItemElement> secondItemElement;
    public static volatile SingularAttribute<ItemElementRelationshipHistory, ResourceType> resourceType;

}