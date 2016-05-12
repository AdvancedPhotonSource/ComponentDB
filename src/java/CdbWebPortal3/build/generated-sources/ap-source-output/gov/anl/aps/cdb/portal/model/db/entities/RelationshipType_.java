package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipTypeHandler;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-05-12T14:58:28")
@StaticMetamodel(RelationshipType.class)
public class RelationshipType_ { 

    public static volatile SingularAttribute<RelationshipType, RelationshipTypeHandler> relationshipTypeHandler;
    public static volatile SingularAttribute<RelationshipType, String> name;
    public static volatile SingularAttribute<RelationshipType, String> description;
    public static volatile SingularAttribute<RelationshipType, Integer> id;
    public static volatile ListAttribute<RelationshipType, ItemElementRelationship> itemElementRelationshipList;

}