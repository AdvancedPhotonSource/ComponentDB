package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ResourceType;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(ItemResource.class)
public class ItemResource_ { 

    public static volatile SingularAttribute<ItemResource, Boolean> isUsedOptional;
    public static volatile SingularAttribute<ItemResource, Item> item;
    public static volatile SingularAttribute<ItemResource, ItemConnector> itemConnector;
    public static volatile SingularAttribute<ItemResource, Boolean> isProvided;
    public static volatile SingularAttribute<ItemResource, String> description;
    public static volatile SingularAttribute<ItemResource, Boolean> isUsedRequired;
    public static volatile SingularAttribute<ItemResource, Integer> id;
    public static volatile SingularAttribute<ItemResource, String> units;
    public static volatile SingularAttribute<ItemResource, String> value;
    public static volatile SingularAttribute<ItemResource, ResourceType> resourceType;

}