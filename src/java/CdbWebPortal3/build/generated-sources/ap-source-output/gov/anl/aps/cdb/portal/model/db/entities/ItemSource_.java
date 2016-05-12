package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(ItemSource.class)
public class ItemSource_ { 

    public static volatile SingularAttribute<ItemSource, Item> item;
    public static volatile SingularAttribute<ItemSource, Float> cost;
    public static volatile SingularAttribute<ItemSource, String> contactInfo;
    public static volatile SingularAttribute<ItemSource, Boolean> isManufacturer;
    public static volatile SingularAttribute<ItemSource, String> description;
    public static volatile SingularAttribute<ItemSource, Boolean> isVendor;
    public static volatile SingularAttribute<ItemSource, String> partNumber;
    public static volatile SingularAttribute<ItemSource, Integer> id;
    public static volatile SingularAttribute<ItemSource, Source> source;
    public static volatile SingularAttribute<ItemSource, String> url;

}