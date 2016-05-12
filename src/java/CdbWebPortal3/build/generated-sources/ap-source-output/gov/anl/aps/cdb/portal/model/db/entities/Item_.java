package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemResource;
import gov.anl.aps.cdb.portal.model.db.entities.ItemSource;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(Item.class)
public class Item_ { 

    public static volatile ListAttribute<Item, ItemElement> itemElementList;
    public static volatile SingularAttribute<Item, EntityInfo> entityInfo;
    public static volatile SingularAttribute<Item, Integer> qrId;
    public static volatile SingularAttribute<Item, String> description;
    public static volatile ListAttribute<Item, ItemType> itemTypeList;
    public static volatile SingularAttribute<Item, Item> derivedFromItem;
    public static volatile ListAttribute<Item, ItemElement> itemElementList1;
    public static volatile ListAttribute<Item, ItemCategory> itemCategoryList;
    public static volatile SingularAttribute<Item, Domain> domain;
    public static volatile ListAttribute<Item, ItemSource> itemSourceList;
    public static volatile ListAttribute<Item, ItemResource> itemResourceList;
    public static volatile SingularAttribute<Item, String> name;
    public static volatile ListAttribute<Item, Log> logList;
    public static volatile ListAttribute<Item, Item> itemList;
    public static volatile SingularAttribute<Item, Integer> id;
    public static volatile ListAttribute<Item, EntityType> entityTypeList;
    public static volatile SingularAttribute<Item, String> itemIdentifier1;
    public static volatile ListAttribute<Item, ItemConnector> itemConnectorList;
    public static volatile SingularAttribute<Item, String> itemIdentifier2;

}