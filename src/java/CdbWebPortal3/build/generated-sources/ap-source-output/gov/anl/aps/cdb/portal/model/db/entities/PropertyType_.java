package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(PropertyType.class)
public class PropertyType_ { 

    public static volatile SingularAttribute<PropertyType, String> defaultUnits;
    public static volatile SingularAttribute<PropertyType, PropertyTypeCategory> propertyTypeCategory;
    public static volatile SingularAttribute<PropertyType, Boolean> isDynamic;
    public static volatile SingularAttribute<PropertyType, String> defaultValue;
    public static volatile SingularAttribute<PropertyType, Boolean> isUserWriteable;
    public static volatile ListAttribute<PropertyType, AllowedPropertyValue> allowedPropertyValueList;
    public static volatile SingularAttribute<PropertyType, String> description;
    public static volatile SingularAttribute<PropertyType, PropertyTypeHandler> propertyTypeHandler;
    public static volatile SingularAttribute<PropertyType, Boolean> isActive;
    public static volatile SingularAttribute<PropertyType, Boolean> isInternal;
    public static volatile ListAttribute<PropertyType, Domain> domainList;
    public static volatile SingularAttribute<PropertyType, String> name;
    public static volatile ListAttribute<PropertyType, PropertyValue> propertyValueList;
    public static volatile SingularAttribute<PropertyType, Integer> id;
    public static volatile ListAttribute<PropertyType, EntityType> entityTypeList;

}