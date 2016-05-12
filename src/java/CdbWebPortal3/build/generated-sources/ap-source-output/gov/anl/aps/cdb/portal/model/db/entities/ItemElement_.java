package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationshipHistory;
import gov.anl.aps.cdb.portal.model.db.entities.List;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(ItemElement.class)
public class ItemElement_ { 

    public static volatile SingularAttribute<ItemElement, Boolean> isRequired;
    public static volatile ListAttribute<ItemElement, ItemElementRelationship> itemElementRelationshipList1;
    public static volatile SingularAttribute<ItemElement, EntityInfo> entityInfo;
    public static volatile ListAttribute<ItemElement, ItemElementRelationship> itemElementRelationshipList2;
    public static volatile SingularAttribute<ItemElement, String> description;
    public static volatile ListAttribute<ItemElement, ItemElementRelationshipHistory> itemElementRelationshipHistoryList;
    public static volatile ListAttribute<ItemElement, ItemElementRelationship> itemElementRelationshipList;
    public static volatile ListAttribute<ItemElement, List> listList;
    public static volatile ListAttribute<ItemElement, ItemElementRelationshipHistory> itemElementRelationshipHistoryList1;
    public static volatile ListAttribute<ItemElement, ItemElementRelationshipHistory> itemElementRelationshipHistoryList2;
    public static volatile SingularAttribute<ItemElement, Item> containedItem;
    public static volatile SingularAttribute<ItemElement, Float> sortOrder;
    public static volatile SingularAttribute<ItemElement, String> name;
    public static volatile ListAttribute<ItemElement, Log> logList;
    public static volatile SingularAttribute<ItemElement, Item> parentItem;
    public static volatile ListAttribute<ItemElement, PropertyValue> propertyValueList;
    public static volatile SingularAttribute<ItemElement, Integer> id;

}