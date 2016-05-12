package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationshipHistory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemResource;
import gov.anl.aps.cdb.portal.model.db.entities.ResourceTypeCategory;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(ResourceType.class)
public class ResourceType_ { 

    public static volatile SingularAttribute<ResourceType, String> defaultUnits;
    public static volatile SingularAttribute<ResourceType, String> defaultValue;
    public static volatile SingularAttribute<ResourceType, ResourceTypeCategory> resourceTypeCategory;
    public static volatile ListAttribute<ResourceType, ItemResource> itemResourceList;
    public static volatile SingularAttribute<ResourceType, String> name;
    public static volatile SingularAttribute<ResourceType, String> description;
    public static volatile SingularAttribute<ResourceType, String> handlerName;
    public static volatile ListAttribute<ResourceType, Connector> connectorList;
    public static volatile SingularAttribute<ResourceType, Integer> id;
    public static volatile ListAttribute<ResourceType, ItemElementRelationshipHistory> itemElementRelationshipHistoryList;
    public static volatile ListAttribute<ResourceType, ItemElementRelationship> itemElementRelationshipList;

}