package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(ItemType.class)
public class ItemType_ { 

    public static volatile SingularAttribute<ItemType, String> name;
    public static volatile SingularAttribute<ItemType, String> description;
    public static volatile ListAttribute<ItemType, Item> itemList;
    public static volatile SingularAttribute<ItemType, Integer> id;

}