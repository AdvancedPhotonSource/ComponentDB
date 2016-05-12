package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(PropertyValueHistory.class)
public class PropertyValueHistory_ { 

    public static volatile SingularAttribute<PropertyValueHistory, String> displayValue;
    public static volatile SingularAttribute<PropertyValueHistory, String> targetValue;
    public static volatile SingularAttribute<PropertyValueHistory, UserInfo> enteredByUser;
    public static volatile SingularAttribute<PropertyValueHistory, String> description;
    public static volatile SingularAttribute<PropertyValueHistory, PropertyValue> propertyValue;
    public static volatile SingularAttribute<PropertyValueHistory, Integer> id;
    public static volatile SingularAttribute<PropertyValueHistory, String> tag;
    public static volatile SingularAttribute<PropertyValueHistory, String> units;
    public static volatile SingularAttribute<PropertyValueHistory, Date> enteredOnDateTime;
    public static volatile SingularAttribute<PropertyValueHistory, String> value;

}