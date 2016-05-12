package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(ConnectorType.class)
public class ConnectorType_ { 

    public static volatile SingularAttribute<ConnectorType, String> name;
    public static volatile SingularAttribute<ConnectorType, String> description;
    public static volatile ListAttribute<ConnectorType, Connector> connectorList;
    public static volatile SingularAttribute<ConnectorType, Integer> id;

}