package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(PropertyValue.class)
public class PropertyValue_ { 

    public static volatile ListAttribute<PropertyValue, PropertyValueHistory> propertyValueHistoryList;
    public static volatile SingularAttribute<PropertyValue, Boolean> isDynamic;
    public static volatile ListAttribute<PropertyValue, ItemElement> itemElementList;
    public static volatile SingularAttribute<PropertyValue, Boolean> isUserWriteable;
    public static volatile SingularAttribute<PropertyValue, UserInfo> enteredByUser;
    public static volatile SingularAttribute<PropertyValue, String> description;
    public static volatile ListAttribute<PropertyValue, Connector> connectorList;
    public static volatile SingularAttribute<PropertyValue, String> units;
    public static volatile ListAttribute<PropertyValue, ItemElementRelationship> itemElementRelationshipList;
    public static volatile ListAttribute<PropertyValue, PropertyMetadata> propertyMetadataList;
    public static volatile SingularAttribute<PropertyValue, String> displayValue;
    public static volatile SingularAttribute<PropertyValue, PropertyType> propertyType;
    public static volatile SingularAttribute<PropertyValue, String> targetValue;
    public static volatile SingularAttribute<PropertyValue, Integer> id;
    public static volatile SingularAttribute<PropertyValue, String> tag;
    public static volatile SingularAttribute<PropertyValue, Date> enteredOnDateTime;
    public static volatile ListAttribute<PropertyValue, ItemConnector> itemConnectorList;
    public static volatile SingularAttribute<PropertyValue, String> value;

}