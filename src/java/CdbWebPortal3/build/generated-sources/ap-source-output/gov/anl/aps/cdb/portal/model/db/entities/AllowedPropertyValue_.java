package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(AllowedPropertyValue.class)
public class AllowedPropertyValue_ { 

    public static volatile SingularAttribute<AllowedPropertyValue, Float> sortOrder;
    public static volatile SingularAttribute<AllowedPropertyValue, PropertyType> propertyType;
    public static volatile SingularAttribute<AllowedPropertyValue, String> description;
    public static volatile SingularAttribute<AllowedPropertyValue, Integer> id;
    public static volatile SingularAttribute<AllowedPropertyValue, String> units;
    public static volatile SingularAttribute<AllowedPropertyValue, String> value;

}