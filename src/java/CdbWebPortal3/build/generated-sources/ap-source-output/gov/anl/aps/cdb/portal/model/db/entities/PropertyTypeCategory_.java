package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(PropertyTypeCategory.class)
public class PropertyTypeCategory_ { 

    public static volatile ListAttribute<PropertyTypeCategory, PropertyType> propertyTypeList;
    public static volatile SingularAttribute<PropertyTypeCategory, String> name;
    public static volatile SingularAttribute<PropertyTypeCategory, String> description;
    public static volatile SingularAttribute<PropertyTypeCategory, Integer> id;

}