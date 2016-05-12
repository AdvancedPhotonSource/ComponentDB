package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(ItemCategory.class)
public class ItemCategory_ { 

    public static volatile SingularAttribute<ItemCategory, String> name;
    public static volatile SingularAttribute<ItemCategory, String> description;
    public static volatile ListAttribute<ItemCategory, Item> itemList;
    public static volatile SingularAttribute<ItemCategory, Integer> id;

}