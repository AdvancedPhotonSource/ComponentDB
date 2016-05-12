package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.ConnectorType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.ResourceType;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(Connector.class)
public class Connector_ { 

    public static volatile SingularAttribute<Connector, ConnectorType> connectorType;
    public static volatile SingularAttribute<Connector, String> name;
    public static volatile SingularAttribute<Connector, String> description;
    public static volatile ListAttribute<Connector, PropertyValue> propertyValueList;
    public static volatile SingularAttribute<Connector, Integer> id;
    public static volatile ListAttribute<Connector, ItemConnector> itemConnectorList;
    public static volatile SingularAttribute<Connector, ResourceType> resourceType;

}